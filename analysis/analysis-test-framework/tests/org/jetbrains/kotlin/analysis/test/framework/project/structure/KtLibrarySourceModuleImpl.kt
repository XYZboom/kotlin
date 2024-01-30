/*
 * Copyright 2010-2023 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.test.framework.project.structure

import com.intellij.openapi.project.Project
import com.intellij.psi.search.GlobalSearchScope
import org.jetbrains.kotlin.analysis.project.structure.*
import org.jetbrains.kotlin.platform.TargetPlatform
import org.jetbrains.kotlin.resolve.PlatformDependentAnalyzerServices
import org.jetbrains.kotlin.test.getAnalyzerServices

class KtLibrarySourceModuleImpl(
    override val libraryName: String,
    override val platform: TargetPlatform,
    override val contentScope: GlobalSearchScope,
    override val project: Project,
    override val binaryLibrary: KtLibraryModule,
) : KtModuleWithModifiableDependencies(), KtLibrarySourceModule {
    override val analyzerServices: PlatformDependentAnalyzerServices get() = platform.getAnalyzerServices()

    override val directRegularDependencies: MutableList<KtModule> = mutableListOf()
    override val directDependsOnDependencies: MutableList<KtModule> = mutableListOf()
    override val directFriendDependencies: MutableList<KtModule> = mutableListOf()

    override fun toString(): String = libraryName
}
