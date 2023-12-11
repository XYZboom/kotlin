/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.low.level.api.fir.transformers

import org.jetbrains.kotlin.analysis.low.level.api.fir.api.FirDesignationWithFile
import org.jetbrains.kotlin.analysis.low.level.api.fir.api.targets.LLFirResolveTarget
import org.jetbrains.kotlin.analysis.low.level.api.fir.api.throwUnexpectedFirElementError
import org.jetbrains.kotlin.analysis.low.level.api.fir.file.builder.LLFirLockProvider
import org.jetbrains.kotlin.analysis.low.level.api.fir.file.structure.LLFirDeclarationModificationService
import org.jetbrains.kotlin.analysis.low.level.api.fir.lazy.resolve.FirLazyBodiesCalculator
import org.jetbrains.kotlin.analysis.low.level.api.fir.util.checkReturnTypeRefIsResolved
import org.jetbrains.kotlin.analysis.low.level.api.fir.util.isScriptDependentDeclaration
import org.jetbrains.kotlin.fir.FirElementWithResolveState
import org.jetbrains.kotlin.fir.FirFileAnnotationsContainer
import org.jetbrains.kotlin.fir.FirSession
import org.jetbrains.kotlin.fir.declarations.*
import org.jetbrains.kotlin.fir.declarations.builder.buildSimpleFunctionCopy
import org.jetbrains.kotlin.fir.declarations.builder.buildValueParameterCopy
import org.jetbrains.kotlin.fir.expressions.FirAnnotationCall
import org.jetbrains.kotlin.fir.resolve.ScopeSession
import org.jetbrains.kotlin.fir.resolve.transformers.body.resolve.FirImplicitAwareBodyResolveTransformer
import org.jetbrains.kotlin.fir.resolve.transformers.body.resolve.FirResolveContextCollector
import org.jetbrains.kotlin.fir.resolve.transformers.body.resolve.ImplicitBodyResolveComputationSession
import org.jetbrains.kotlin.fir.scopes.callableCopySubstitutionForTypeUpdater
import org.jetbrains.kotlin.fir.symbols.FirBasedSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirCallableSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirNamedFunctionSymbol
import org.jetbrains.kotlin.fir.symbols.impl.FirValueParameterSymbol
import org.jetbrains.kotlin.fir.types.FirImplicitTypeRef
import org.jetbrains.kotlin.fir.util.setMultimapOf
import org.jetbrains.kotlin.fir.utils.exceptions.withFirEntry
import org.jetbrains.kotlin.fir.utils.exceptions.withFirSymbolEntry
import org.jetbrains.kotlin.utils.exceptions.errorWithAttachment
import org.jetbrains.kotlin.utils.exceptions.requireWithAttachment

internal object LLFirImplicitTypesLazyResolver : LLFirLazyResolver(FirResolvePhase.IMPLICIT_TYPES_BODY_RESOLVE) {
    override fun resolve(
        target: LLFirResolveTarget,
        lockProvider: LLFirLockProvider,
        session: FirSession,
        scopeSession: ScopeSession,
        towerDataContextCollector: FirResolveContextCollector?,
    ) {
        val resolver = LLFirImplicitBodyTargetResolver(target, lockProvider, session, scopeSession, towerDataContextCollector)
        resolver.resolveDesignation()
    }

    override fun phaseSpecificCheckIsResolved(target: FirElementWithResolveState) {
        if (target !is FirCallableDeclaration) return
        checkReturnTypeRefIsResolved(target)
    }
}

internal class LLImplicitBodyResolveComputationSession : ImplicitBodyResolveComputationSession() {
    /**
     * The symbol on which foreign annotations will be postponed
     *
     * @see withAnchorForForeignAnnotations
     * @see postponeForeignAnnotationResolution
     */
    private var anchorForForeignAnnotations: FirCallableSymbol<*>? = null

    private inline fun <T> withAnchorForForeignAnnotations(symbol: FirCallableSymbol<*>, action: () -> T): T {
        val previousSymbol = anchorForForeignAnnotations
        return try {
            anchorForForeignAnnotations = symbol
            action()
        } finally {
            anchorForForeignAnnotations = previousSymbol
        }
    }

    override fun <D : FirCallableDeclaration> executeTransformation(symbol: FirCallableSymbol<*>, transformation: () -> D): D {
        // Do not store local declarations as we can postpone only non-local callables
        return if (symbol.cannotResolveAnnotationsOnDemand()) {
            transformation()
        } else {
            withAnchorForForeignAnnotations(symbol, transformation)
        }
    }

    private val postponedSymbols = setMultimapOf<FirCallableSymbol<*>, FirBasedSymbol<*>>()

    /**
     * Postpone the resolution request to [symbol] until [annotation arguments][FirResolvePhase.ANNOTATION_ARGUMENTS] phase
     * of the declaration which is used this foreign annotation.
     *
     * @see postponedSymbols
     */
    fun postponeForeignAnnotationResolution(symbol: FirBasedSymbol<*>) {
        // We should unwrap local symbols to avoid recursion
        val symbolToPostpone = when (symbol) {
            is FirValueParameterSymbol -> symbol.containingFunctionSymbol
            else -> symbol
        }

        // We cannot resolve them on demand, so we shouldn't postpone them
        if (symbolToPostpone.cannotResolveAnnotationsOnDemand()) {
            return
        }

        val currentSymbol = anchorForForeignAnnotations ?: errorWithAttachment("Unexpected state: the current symbol have to be here") {
            withFirSymbolEntry("symbol to postpone", symbolToPostpone)
        }

        // There is no sense to postpone itself as it will lead to recursion
        if (currentSymbol == symbolToPostpone) return

        postponedSymbols.put(currentSymbol, symbolToPostpone)
    }

    /**
     * @return all symbols postponed with [postponeForeignAnnotationResolution] for the [target] element
     *
     * @see postponeForeignAnnotationResolution
     */
    fun postponedSymbols(target: FirCallableDeclaration): Collection<FirBasedSymbol<*>> {
        return postponedSymbols[target.symbol]
    }
}

internal class LLFirImplicitBodyTargetResolver(
    target: LLFirResolveTarget,
    lockProvider: LLFirLockProvider,
    session: FirSession,
    scopeSession: ScopeSession,
    firResolveContextCollector: FirResolveContextCollector?,
    llImplicitBodyResolveComputationSessionParameter: LLImplicitBodyResolveComputationSession? = null,
) : LLFirAbstractBodyTargetResolver(
    target,
    lockProvider,
    scopeSession,
    FirResolvePhase.IMPLICIT_TYPES_BODY_RESOLVE,
    llImplicitBodyResolveComputationSession = llImplicitBodyResolveComputationSessionParameter ?: LLImplicitBodyResolveComputationSession(),
    isJumpingPhase = true,
) {
    val declarationContextMapping: MutableMap<FirDeclaration, FirDeclaration> by lazy(LazyThreadSafetyMode.NONE) {
        HashMap<FirDeclaration, FirDeclaration>()
    }

    override val transformer = object : FirImplicitAwareBodyResolveTransformer(
        session,
        implicitBodyResolveComputationSession = llImplicitBodyResolveComputationSession,
        phase = resolverPhase,
        implicitTypeOnly = true,
        scopeSession = scopeSession,
        firResolveContextCollector = firResolveContextCollector,
        returnTypeCalculator = createReturnTypeCalculator(firResolveContextCollector = firResolveContextCollector),
    ) {

        override val preserveCFGForClasses: Boolean get() = false
        override val buildCfgForFiles: Boolean get() = false
        override fun transformForeignAnnotationCall(symbol: FirBasedSymbol<*>, annotationCall: FirAnnotationCall): FirAnnotationCall {
            llImplicitBodyResolveComputationSession.postponeForeignAnnotationResolution(symbol)
            return annotationCall
        }

        override fun <T : FirDeclaration> substituteDeclarationForContextPurposes(declaration: T): T {
            return declarationContextMapping[declaration]?.let {
                @Suppress("UNCHECKED_CAST")
                it as T
            } ?: declaration
        }
    }

    override fun doResolveWithoutLock(target: FirElementWithResolveState): Boolean {
        when {
            target is FirCallableDeclaration && target.attributes.callableCopySubstitutionForTypeUpdater != null -> {
                performCustomResolveUnderLock(target) {
                    transformer.returnTypeCalculator.callableCopyTypeCalculator.computeReturnType(target)
                }

                return true
            }

            target is FirFunction -> {
                resolveFunction(target)
                return true
            }

            target is FirProperty -> {
                resolveProperty(target)
                return true
            }
        }

        return super.doResolveWithoutLock(target)
    }

    private fun resolveFunction(function: FirFunction) {
        if (function.returnTypeRef !is FirImplicitTypeRef) {
            performCustomResolveUnderLock(function) {
                // just update phase for function without an implicit type
            }

            return
        }

        // resolveFunction can be called only for constructor, error function, and simple function,
        // but only simple function can have an implicit type
        requireWithAttachment(
            function is FirSimpleFunction,
            { "${function::class.simpleName} found but only ${FirSimpleFunction::class.simpleName} acceptable" },
        ) {
            withFirEntry("function", function)
        }

        // copy the original function and unwrap lazy bodies
        val newFunction: FirSimpleFunction? = copySimpleFunction(function)

        // "null" means that some other thread is already resolved [function] to [resolverPhase]
        if (newFunction == null) return

        // save declarations mapping to provide original declarations as DFA and context nodes
        declarationContextMapping[newFunction] = function
        function.valueParameters.zip(newFunction.valueParameters).forEach { (old, new) ->
            declarationContextMapping[new] = old
        }

        // we should mark the original declaration as in progress
        llImplicitBodyResolveComputationSession.compute(function.symbol) {
            // We need this lock to be sure that resolution contracts are not violated
            // It is safe because we can't get a recursion here due to a new element
            performCustomResolveUnderLock(newFunction) {
                rawResolve(newFunction)
            }

            // publish results under lock
            performCustomResolveUnderLock(function) {
                publishFunctionResult(source = newFunction, destination = function)
                publishPostponedSymbols(from = newFunction, to = function)
            }

            function
        }
    }

    private fun publishFunctionResult(source: FirFunction, destination: FirFunction) {
        destination.replaceReturnTypeRef(source.returnTypeRef)
        destination.replaceControlFlowGraphReference(source.controlFlowGraphReference)
        destination.replaceBody(source.body)

        destination.valueParameters.zip(source.valueParameters).forEach { (dst, src) ->
            dst.replaceDefaultValue(src.defaultValue)
            dst.replaceControlFlowGraphReference(src.controlFlowGraphReference)
        }
    }

    /**
     * @return **null** if original declaration is already resolved
     */
    private fun copySimpleFunction(originalFunction: FirSimpleFunction): FirSimpleFunction? {
        var newFunction: FirSimpleFunction? = null
        withReadLock(originalFunction) {
            // it is enough to make a non-deep copy because we will process only default values and the body,
            // and they will be replaced later during lazy body calculation
            newFunction = buildSimpleFunctionCopy(originalFunction) {
                symbol = FirNamedFunctionSymbol(originalFunction.symbol.callableId)

                valueParameters.clear()
                originalFunction.valueParameters.mapTo(valueParameters) {
                    buildValueParameterCopy(it) {
                        symbol = FirValueParameterSymbol(it.symbol.name)
                    }
                }
            }
        }

        // function already resolved
        if (newFunction == null) return null

        // insert body
        val firDesignation = FirDesignationWithFile(nestedClassesStack, originalFunction, resolveTarget.firFile)
        @Suppress("USELESS_CAST")
        FirLazyBodiesCalculator.calculateLazyBodiesForFunction(designation = firDesignation, target = newFunction as FirSimpleFunction)

        return newFunction
    }

    private fun resolveProperty(property: FirProperty) {
        performCustomResolveUnderLock(property) {
            if (property.returnTypeRef is FirImplicitTypeRef || property.backingField?.returnTypeRef is FirImplicitTypeRef) {
                resolve(property, BodyStateKeepers.PROPERTY)
                publishPostponedSymbols(property)
            }
        }
    }

    override fun doLazyResolveUnderLock(target: FirElementWithResolveState) {
        when (target) {
            is FirFunction, is FirProperty -> error("${target::class.simpleName} should have been resolved in ${::doResolveWithoutLock.name}")
            is FirField -> {
                if (target.returnTypeRef is FirImplicitTypeRef) {
                    resolve(target, BodyStateKeepers.FIELD)
                }
            }

            is FirScript -> {
                if (target.statements.any { it.isScriptDependentDeclaration }) {
                    resolve(target, BodyStateKeepers.SCRIPT)
                }
            }

            is FirRegularClass,
            is FirTypeAlias,
            is FirFile,
            is FirCodeFragment,
            is FirAnonymousInitializer,
            is FirDanglingModifierList,
            is FirFileAnnotationsContainer,
            is FirEnumEntry,
            is FirErrorProperty,
            -> {
                // No implicit bodies here
            }
            else -> throwUnexpectedFirElementError(target)
        }

        if (target is FirDeclaration) {
            target.forEachDeclarationWhichCanHavePostponedSymbols(::publishPostponedSymbols)
        }
    }

    private fun publishPostponedSymbols(from: FirCallableDeclaration, to: FirCallableDeclaration = from) {
        val postponedSymbols = llImplicitBodyResolveComputationSession.postponedSymbols(from) - to.symbol
        if (postponedSymbols.isNotEmpty()) {
            to.postponedSymbolsForAnnotationResolution = postponedSymbols
        }
    }

    override fun rawResolve(target: FirElementWithResolveState) {
        if (target is FirScript) {
            resolveScript(target)
        } else {
            super.rawResolve(target)
        }

        LLFirDeclarationModificationService.bodyResolved(target, resolverPhase)
    }
}
