/*
 * Copyright 2010-2022 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.analysis.api.standalone.fir.test.cases.generated.cases.components.compileTimeConstantProvider;

import com.intellij.testFramework.TestDataPath;
import org.jetbrains.kotlin.test.util.KtTestUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.analysis.api.standalone.fir.test.AnalysisApiFirStandaloneModeTestConfiguratorFactory;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiTestConfiguratorFactoryData;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiTestConfigurator;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.TestModuleKind;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.FrontendKind;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisSessionMode;
import org.jetbrains.kotlin.analysis.test.framework.test.configurators.AnalysisApiMode;
import org.jetbrains.kotlin.analysis.api.impl.base.test.cases.components.compileTimeConstantProvider.AbstractCompileTimeConstantEvaluatorTest;
import org.jetbrains.kotlin.test.TestMetadata;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.regex.Pattern;

/** This class is generated by {@link org.jetbrains.kotlin.generators.tests.analysis.api.GenerateAnalysisApiTestsKt}. DO NOT MODIFY MANUALLY */
@SuppressWarnings("all")
@TestMetadata("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate")
@TestDataPath("$PROJECT_ROOT")
public class FirStandaloneNormalAnalysisSourceModuleCompileTimeConstantEvaluatorTestGenerated extends AbstractCompileTimeConstantEvaluatorTest {
    @NotNull
    @Override
    public AnalysisApiTestConfigurator getConfigurator() {
        return AnalysisApiFirStandaloneModeTestConfiguratorFactory.INSTANCE.createConfigurator(
            new AnalysisApiTestConfiguratorFactoryData(
                FrontendKind.Fir,
                TestModuleKind.Source,
                AnalysisSessionMode.Normal,
                AnalysisApiMode.Standalone
            )
        );
    }

    @Test
    public void testAllFilesPresentInEvaluate() throws Exception {
        KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate"), Pattern.compile("^(.+)\\.kt$"), null, true);
    }

    @Test
    @TestMetadata("binaryExpressionWithString.kt")
    public void testBinaryExpressionWithString() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/binaryExpressionWithString.kt");
    }

    @Test
    @TestMetadata("integerLiteral_minusOne_entire.kt")
    public void testIntegerLiteral_minusOne_entire() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/integerLiteral_minusOne_entire.kt");
    }

    @Test
    @TestMetadata("integerLiteral_minusOne_justOne.kt")
    public void testIntegerLiteral_minusOne_justOne() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/integerLiteral_minusOne_justOne.kt");
    }

    @Test
    @TestMetadata("integerLiteral_plusOne_entire.kt")
    public void testIntegerLiteral_plusOne_entire() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/integerLiteral_plusOne_entire.kt");
    }

    @Test
    @TestMetadata("integerLiteral_plusOne_justOne.kt")
    public void testIntegerLiteral_plusOne_justOne() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/integerLiteral_plusOne_justOne.kt");
    }

    @Test
    @TestMetadata("javaFinalField.kt")
    public void testJavaFinalField() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/javaFinalField.kt");
    }

    @Test
    @TestMetadata("javaStaticField.kt")
    public void testJavaStaticField() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/javaStaticField.kt");
    }

    @Test
    @TestMetadata("javaStaticFinalField.kt")
    public void testJavaStaticFinalField() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/javaStaticFinalField.kt");
    }

    @Test
    @TestMetadata("namedReference_const.kt")
    public void testNamedReference_const() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/namedReference_const.kt");
    }

    @Test
    @TestMetadata("namedReference_val.kt")
    public void testNamedReference_val() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/namedReference_val.kt");
    }

    @Test
    @TestMetadata("namedReference_var.kt")
    public void testNamedReference_var() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/namedReference_var.kt");
    }

    @Test
    @TestMetadata("propertyInCompanionObject.kt")
    public void testPropertyInCompanionObject() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/propertyInCompanionObject.kt");
    }

    @Test
    @TestMetadata("propertyInCompanionObject_indirect.kt")
    public void testPropertyInCompanionObject_indirect() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/propertyInCompanionObject_indirect.kt");
    }

    @Test
    @TestMetadata("propertyInCompanionObject_indirect_twice.kt")
    public void testPropertyInCompanionObject_indirect_twice() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/propertyInCompanionObject_indirect_twice.kt");
    }

    @Test
    @TestMetadata("propertyInit_Byte.kt")
    public void testPropertyInit_Byte() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/propertyInit_Byte.kt");
    }

    @Test
    @TestMetadata("propertyInit_DivByOtherProperty_const.kt")
    public void testPropertyInit_DivByOtherProperty_const() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/propertyInit_DivByOtherProperty_const.kt");
    }

    @Test
    @TestMetadata("propertyInit_DivByOtherProperty_val.kt")
    public void testPropertyInit_DivByOtherProperty_val() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/propertyInit_DivByOtherProperty_val.kt");
    }

    @Test
    @TestMetadata("propertyInit_DivByZero.kt")
    public void testPropertyInit_DivByZero() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/propertyInit_DivByZero.kt");
    }

    @Test
    @TestMetadata("propertyInit_Double.kt")
    public void testPropertyInit_Double() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/propertyInit_Double.kt");
    }

    @Test
    @TestMetadata("propertyInit_Float.kt")
    public void testPropertyInit_Float() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/propertyInit_Float.kt");
    }

    @Test
    @TestMetadata("propertyInit_Int.kt")
    public void testPropertyInit_Int() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/propertyInit_Int.kt");
    }

    @Test
    @TestMetadata("propertyInit_Long.kt")
    public void testPropertyInit_Long() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/propertyInit_Long.kt");
    }

    @Test
    @TestMetadata("propertyInit_UInt.kt")
    public void testPropertyInit_UInt() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/propertyInit_UInt.kt");
    }

    @Test
    @TestMetadata("stringLiteral.kt")
    public void testStringLiteral() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/stringLiteral.kt");
    }

    @Test
    @TestMetadata("string_compareTo.kt")
    public void testString_compareTo() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/string_compareTo.kt");
    }

    @Test
    @TestMetadata("string_length.kt")
    public void testString_length() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/string_length.kt");
    }

    @Test
    @TestMetadata("string_plusMany.kt")
    public void testString_plusMany() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/string_plusMany.kt");
    }

    @Test
    @TestMetadata("string_plusOnce.kt")
    public void testString_plusOnce() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/string_plusOnce.kt");
    }

    @Test
    @TestMetadata("string_plusTwice.kt")
    public void testString_plusTwice() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/string_plusTwice.kt");
    }

    @Test
    @TestMetadata("string_toString.kt")
    public void testString_toString() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/string_toString.kt");
    }

    @Test
    @TestMetadata("string_trimIndent.kt")
    public void testString_trimIndent() throws Exception {
        runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/string_trimIndent.kt");
    }

    @Nested
    @TestMetadata("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/incompleteCode")
    @TestDataPath("$PROJECT_ROOT")
    public class IncompleteCode {
        @Test
        public void testAllFilesPresentInIncompleteCode() throws Exception {
            KtTestUtil.assertAllTestsPresentByMetadataWithExcluded(this.getClass(), new File("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/incompleteCode"), Pattern.compile("^(.+)\\.kt$"), null, true);
        }

        @Test
        @TestMetadata("commaInWhenCondition.kt")
        public void testCommaInWhenCondition() throws Exception {
            runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/incompleteCode/commaInWhenCondition.kt");
        }

        @Test
        @TestMetadata("incompleteRange.kt")
        public void testIncompleteRange() throws Exception {
            runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/incompleteCode/incompleteRange.kt");
        }

        @Test
        @TestMetadata("invalidLPARinPropertyName.kt")
        public void testInvalidLPARinPropertyName() throws Exception {
            runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/incompleteCode/invalidLPARinPropertyName.kt");
        }

        @Test
        @TestMetadata("noRightOperand.kt")
        public void testNoRightOperand() throws Exception {
            runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/incompleteCode/noRightOperand.kt");
        }

        @Test
        @TestMetadata("noRightOperandLong.kt")
        public void testNoRightOperandLong() throws Exception {
            runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/incompleteCode/noRightOperandLong.kt");
        }

        @Test
        @TestMetadata("noRightOperandUnsignedLong.kt")
        public void testNoRightOperandUnsignedLong() throws Exception {
            runTest("analysis/analysis-api/testData/components/compileTimeConstantProvider/evaluate/incompleteCode/noRightOperandUnsignedLong.kt");
        }
    }
}
