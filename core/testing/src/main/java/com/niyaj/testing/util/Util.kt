/*
 * Copyright 2024 Sk Niyaj Ali
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.niyaj.testing.util

import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Used to debug the semantic tree.
 */
fun ComposeTestRule.dumpSemanticNodes() {
    this.onRoot().printToLog(tag = "PoposLog")
}

fun runTestWithLogging(
    context: CoroutineContext = EmptyCoroutineContext,
    timeout: Duration = 30.seconds,
    testBody: suspend TestScope.() -> Unit,
) = runTest(context, timeout) {
    runCatching {
        testBody()
    }.onFailure { exception ->
        exception.printStackTrace()
        throw exception
    }
}
