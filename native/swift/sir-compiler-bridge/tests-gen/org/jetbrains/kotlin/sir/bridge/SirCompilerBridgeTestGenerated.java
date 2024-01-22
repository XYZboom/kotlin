/*
 * Copyright 2010-2024 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.sir.bridge;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.native.swift.sir.GenerateSirTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("native/swift/sir-compiler-bridge/testData")
@TestDataPath("$PROJECT_ROOT")
public class SirCompilerBridgeTestGenerated extends AbstractKotlinSirBridgeTest {
    @Test
    public void testAllFilesPresentInTestData() throws Exception {
        KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("native/swift/sir-compiler-bridge/testData"), Pattern.compile("^([^\\.]+)$"), null, false);
    }

    @Test
    @TestMetadata("multiple_requests")
    public void testMultiple_requests() throws Exception {
        runTest("native/swift/sir-compiler-bridge/testData/multiple_requests/");
    }

    @Test
    @TestMetadata("primitive_parameters")
    public void testPrimitive_parameters() throws Exception {
        runTest("native/swift/sir-compiler-bridge/testData/primitive_parameters/");
    }

    @Test
    @TestMetadata("primitive_types")
    public void testPrimitive_types() throws Exception {
        runTest("native/swift/sir-compiler-bridge/testData/primitive_types/");
    }

    @Test
    @TestMetadata("smoke0")
    public void testSmoke0() throws Exception {
        runTest("native/swift/sir-compiler-bridge/testData/smoke0/");
    }

    @Test
    @TestMetadata("unsigned_primitive_types")
    public void testUnsigned_primitive_types() throws Exception {
        runTest("native/swift/sir-compiler-bridge/testData/unsigned_primitive_types/");
    }

    @Test
    @TestMetadata("property_accessors")
    public void testProperty_accessors() throws Exception {
        runTest("native/swift/sir-compiler-bridge/testData/property_accessors/");
    }

}
