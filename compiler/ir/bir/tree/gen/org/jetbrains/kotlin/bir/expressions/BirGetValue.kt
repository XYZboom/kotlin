/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

// This file was generated automatically. See compiler/ir/bir.tree/tree-generator/ReadMe.md.
// DO NOT MODIFY IT MANUALLY.

package org.jetbrains.kotlin.bir.expressions

import org.jetbrains.kotlin.bir.BirElement
import org.jetbrains.kotlin.bir.BirElementBackReferencesKey
import org.jetbrains.kotlin.bir.BirElementClass

/**
 * A leaf IR tree element.
 *
 * Generated from: [org.jetbrains.kotlin.bir.generator.BirTree.getValue]
 */
abstract class BirGetValue(elementClass: BirElementClass<*>) : BirValueAccessExpression(elementClass), BirElement {
    companion object : BirElementClass<BirGetValue>(BirGetValue::class.java, 38, true) {
        val symbol = BirElementBackReferencesKey<BirGetValue, _>{ (it as? BirGetValue)?.symbol }
    }
}
