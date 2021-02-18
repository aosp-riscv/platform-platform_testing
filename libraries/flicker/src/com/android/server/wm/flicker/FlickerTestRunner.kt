/*
 * Copyright (C) 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.server.wm.flicker

import android.platform.test.annotations.Presubmit
import android.platform.test.annotations.Postsubmit
import androidx.test.filters.FlakyTest
import com.android.server.wm.flicker.assertions.AssertionBlock
import org.junit.Assume
import org.junit.Rule
import org.junit.Test

/**
 * Flicker test runner compatible with JUnit
 *
 * Executes the test and run setup as well as the transition using @Before and
 * the test and run teardown using @After.
 *
 * All the enabled assertions are created in a single test and all flaky assertions are created on
 * a second test annotated with @FlakyTest
 *
 * @param testSpec Flicker test specification
 */
abstract class FlickerTestRunner(protected val testSpec: FlickerTestRunnerFactory.TestSpec) {
    @get:Rule
    val flickerTestRule = FlickerTestRule(testSpec)

    /**
     * Run only the enabled assertions on the recorded traces.
     */
    @Presubmit
    @Test
    fun presubmit() {
        Assume.assumeTrue(testSpec.assertion.block.and(AssertionBlock.PRESUBMIT) > 0)
        flickerTestRule.flicker.checkAssertion(testSpec.assertion)
    }

    @Postsubmit
    @Test
    fun postsubmit() {
        Assume.assumeTrue(testSpec.assertion.block.and(AssertionBlock.POSTSUBMIT) > 0)
        flickerTestRule.flicker.checkAssertion(testSpec.assertion)
    }

    /**
     * Run all trace assertions (including disabled) on the recorded traces.
     */
    @FlakyTest
    @Test
    fun flaky() {
        Assume.assumeTrue(testSpec.assertion.block.and(AssertionBlock.FLAKY) > 0)
        flickerTestRule.flicker.checkAssertion(testSpec.assertion)
    }
}