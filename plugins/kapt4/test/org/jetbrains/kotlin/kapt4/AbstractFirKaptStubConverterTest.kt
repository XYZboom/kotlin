/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.kapt4

import org.jetbrains.kotlin.kapt3.base.util.doOpenInternalPackagesIfRequired
import org.jetbrains.kotlin.kapt3.test.JvmCompilerWithKaptFacade
import org.jetbrains.kotlin.kapt3.test.KaptContextBinaryArtifact
import org.jetbrains.kotlin.kapt3.test.KaptEnvironmentConfigurator
import org.jetbrains.kotlin.kapt3.test.KaptTestDirectives.MAP_DIAGNOSTIC_LOCATIONS
import org.jetbrains.kotlin.kapt3.test.runners.AbstractKaptStubConverterTest
import org.jetbrains.kotlin.platform.jvm.JvmPlatforms
import org.jetbrains.kotlin.test.Constructor
import org.jetbrains.kotlin.test.builders.TestConfigurationBuilder
import org.jetbrains.kotlin.test.directives.ConfigurationDirectives.WITH_STDLIB
import org.jetbrains.kotlin.test.model.*
import org.jetbrains.kotlin.test.runners.AbstractKotlinCompilerTest
import org.jetbrains.kotlin.test.services.configuration.CommonEnvironmentConfigurator
import org.jetbrains.kotlin.test.services.configuration.JvmEnvironmentConfigurator

open class AbstractFirKaptStubConverterTest : AbstractKaptStubConverterTest() {
    override val frontendKind: FrontendKind<*> get() = FrontendKinds.FIR

    override val kaptFacade: Constructor<AbstractTestFacade<ResultingArtifact.Source, KaptContextBinaryArtifact>>
        get() = { JvmCompilerWithKaptFacade(it) }
}

/**
 * This test checks the implementation of K2 kapt via Analysis API standalone.
 * This implementation is discontinued, and is left in the codebase only as a potential fallback in case we encounter critical problems
 * with the new implementation ([FirKaptAnalysisHandlerExtension]).
 */
open class ObsoleteFirKapt4StubConverterTest : AbstractKotlinCompilerTest() {
    init {
        doOpenInternalPackagesIfRequired()
    }

    override fun configure(builder: TestConfigurationBuilder) = with(builder) {
        globalDefaults {
            frontend = FrontendKinds.FIR
            targetPlatform = JvmPlatforms.defaultJvmPlatform
            dependencyKind = DependencyKind.Binary
        }

        defaultDirectives {
            +MAP_DIAGNOSTIC_LOCATIONS
            +WITH_STDLIB
        }

        useConfigurators(
            ::CommonEnvironmentConfigurator,
            ::JvmEnvironmentConfigurator,
            ::KaptEnvironmentConfigurator,
            ::Kapt4EnvironmentConfigurator,
        )

        facadeStep(::Kapt4Facade)

        handlersStep(Kapt4ContextBinaryArtifact.Kind) {
            useHandlers(::Kapt4Handler)
        }

        useAfterAnalysisCheckers(::TemporaryKapt4Suppressor)
    }
}
