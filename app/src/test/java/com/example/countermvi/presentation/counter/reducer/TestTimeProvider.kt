package com.example.countermvi.presentation.counter.reducer

class TestTimeProvider(private val fixedTime: String = "12:00:00") : TimeProvider {
    override fun getCurrentTime(): String = fixedTime
}