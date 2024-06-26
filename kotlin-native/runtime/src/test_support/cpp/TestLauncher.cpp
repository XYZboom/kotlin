/*
 * Copyright 2010-2020 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license
 * that can be found in the LICENSE file.
 */

#include "gmock/gmock.h"
#include "gtest/gtest.h"

#include "Runtime.h"

extern "C" void Kotlin_TestSupport_AssertClearGlobalState();

namespace {

class GlobalStateChecker : public testing::Environment {
public:
    void TearDown() override { Kotlin_TestSupport_AssertClearGlobalState(); }
};

} // namespace

int main(int argc, char** argv) {
    testing::InitGoogleMock(&argc, argv);

    // Use the `threadsafe` style to mitigate possible issues with multithreaded death tests.
    // See more about death test styles: https://github.com/google/googletest/blob/master/googletest/docs/advanced.md#how-it-works
    testing::FLAGS_gtest_death_test_style="threadsafe";

    // Googletest takes ownership of the registered environment object.
    testing::AddGlobalTestEnvironment(new GlobalStateChecker());

    kotlin::initializeGlobalRuntimeIfNeeded();

    return RUN_ALL_TESTS();
}
