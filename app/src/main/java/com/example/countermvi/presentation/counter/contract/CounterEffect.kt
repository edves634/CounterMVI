package com.example.countermvi.presentation.counter.contract

/**
 * Эффекты - одноразовые события, которые не являются частью состояния,
 * но требуют реакции (показать сообщение, навигация и т.д.)
 */
sealed interface CounterEffect {
    data class ShowToast(val message: String) : CounterEffect
    data class ShowSnackbar(val message: String) : CounterEffect
    data class NavigateTo(val destination: String) : CounterEffect
}