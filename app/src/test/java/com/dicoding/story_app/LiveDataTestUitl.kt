package com.dicoding.story_app

import androidx.annotation.VisibleForTesting
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import java.util.concurrent.TimeUnit

@VisibleForTesting(otherwise = VisibleForTesting.NONE)
fun <T> Flow<T>.getOrAwaitValue(
    time: Long = 10, // Increased timeout duration
    timeUnit: TimeUnit = TimeUnit.SECONDS,
    afterObserve: () -> Unit = {}
): T = runBlocking {
    afterObserve.invoke()
    withTimeout(timeUnit.toMillis(time)) {
        this@getOrAwaitValue.first()
    }
}