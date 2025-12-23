package com.example.countermvi.presentation.counter.contract

/**
 * Все намерения (Intent) пользователя на экране счетчика.
 * Это sealed interface для типобезопасности и удобства обработки.
 */
sealed interface CounterIntent {
    // Основные операции со счетчиком
    object Increment : CounterIntent
    object Decrement : CounterIntent
    object Reset : CounterIntent

    // Специальные операции
    data class SetValue(val value: Int) : CounterIntent
    object IncrementByTen : CounterIntent
    object DecrementByTen : CounterIntent

    // Управление историей
    object ClearHistory : CounterIntent

    // Обработка ошибок
    object ErrorHandled : CounterIntent

    // Эффекты
    object ShowToastEffect : CounterIntent
    object ShowSnackbarEffect : CounterIntent
}