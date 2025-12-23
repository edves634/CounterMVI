package com.example.countermvi.presentation.counter.reducer

import com.example.countermvi.presentation.counter.contract.*

/**
 * Редуктор (Reducer) для обработки интентов и обновления состояния счетчика.
 * Следует принципу: (state, intent) -> newState
 */
class CounterReducer(
    private val timeProvider: TimeProvider // Поставщик времени для отметок в истории
) {

    /**
     * Основная функция редуктора для обработки интентов.
     * Принимает текущее состояние и интент, возвращает новое состояние.
     *
     * @param state текущее состояние счетчика
     * @param intent интент (намерение пользователя) для обработки
     * @return новое состояние счетчика после обработки интента
     */
    fun reduce(state: CounterState, intent: CounterIntent): CounterState {
        return when (intent) {
            is CounterIntent.Increment -> handleIncrement(state)
            is CounterIntent.Decrement -> handleDecrement(state)
            is CounterIntent.Reset -> handleReset(state)
            is CounterIntent.SetValue -> handleSetValue(state, intent.value)
            is CounterIntent.IncrementByTen -> handleIncrementByTen(state)
            is CounterIntent.DecrementByTen -> handleDecrementByTen(state)
            is CounterIntent.ClearHistory -> handleClearHistory(state)
            is CounterIntent.ErrorHandled -> handleErrorHandled(state)
            is CounterIntent.ShowToastEffect -> handleShowToastEffect(state)
            is CounterIntent.ShowSnackbarEffect -> handleShowSnackbarEffect(state)
        }
    }

    /**
     * Обрабатывает интент увеличения счетчика на 1.
     * Проверяет возможность увеличения, добавляет запись в историю,
     * обновляет статистику и метку времени.
     *
     * @param state текущее состояние
     * @return новое состояние после увеличения или состояние с ошибкой
     */
    private fun handleIncrement(state: CounterState): CounterState {
        val counter = state.counter
        if (!counter.canIncrement()) {
            return state.copy(
                error = "Достигнут максимальный лимит: ${counter.maxValue}",
                effects = state.effects + CounterEffect.ShowSnackbar("Нельзя увеличить!")
            )
        }

        val newValue = counter.value + 1
        val timestamp = timeProvider.getCurrentTime()  // Получение текущего времени

        return state.copy(
            counter = counter.copy(value = newValue),
            history = state.history + "Увеличение: $newValue ($timestamp)",
            error = null,
            statistics = state.statistics.copy(
                incrementCount = state.statistics.incrementCount + 1,
                totalOperations = state.statistics.totalOperations + 1
            ),
            lastUpdated = timestamp
        )
    }

    /**
     * Обрабатывает интент уменьшения счетчика на 1.
     * Проверяет возможность уменьшения, добавляет запись в историю,
     * обновляет статистику и метку времени.
     *
     * @param state текущее состояние
     * @return новое состояние после уменьшения или состояние с ошибкой
     */
    private fun handleDecrement(state: CounterState): CounterState {
        val counter = state.counter
        if (!counter.canDecrement()) {
            return state.copy(
                error = "Достигнут минимальный лимит: ${counter.minValue}",
                effects = state.effects + CounterEffect.ShowSnackbar("Нельзя уменьшить!")
            )
        }

        val newValue = counter.value - 1
        val timestamp = timeProvider.getCurrentTime()  // Получение текущего времени

        return state.copy(
            counter = counter.copy(value = newValue),
            history = state.history + "Уменьшение: $newValue ($timestamp)",
            error = null,
            statistics = state.statistics.copy(
                decrementCount = state.statistics.decrementCount + 1,
                totalOperations = state.statistics.totalOperations + 1
            ),
            lastUpdated = timestamp
        )
    }

    /**
     * Обрабатывает интент сброса счетчика к нулю.
     * Добавляет запись в историю, обновляет статистику,
     * генерирует эффект показа тоста и метку времени.
     *
     * @param state текущее состояние
     * @return новое состояние со сброшенным значением
     */
    private fun handleReset(state: CounterState): CounterState {
        val timestamp = timeProvider.getCurrentTime()  // Получение текущего времени

        return state.copy(
            counter = state.counter.copy(value = 0),
            history = state.history + "Сброс: 0 ($timestamp)",
            error = null,
            statistics = state.statistics.copy(
                resetCount = state.statistics.resetCount + 1,
                totalOperations = state.statistics.totalOperations + 1
            ),
            lastUpdated = timestamp,
            effects = state.effects + CounterEffect.ShowToast("Счетчик сброшен!")
        )
    }

    /**
     * Обрабатывает интент установки произвольного значения счетчика.
     * Проверяет, что значение находится в допустимых пределах,
     * добавляет запись в историю и метку времени.
     *
     * @param state текущее состояние
     * @param value новое значение для установки
     * @return новое состояние с установленным значением или состояние с ошибкой
     */
    private fun handleSetValue(state: CounterState, value: Int): CounterState {
        if (value < state.counter.minValue || value > state.counter.maxValue) {
            return state.copy(
                error = "Значение должно быть между ${state.counter.minValue} и ${state.counter.maxValue}",
                effects = state.effects + CounterEffect.ShowSnackbar("Недопустимое значение")
            )
        }

        val timestamp = timeProvider.getCurrentTime()  // Получение текущего времени
        return state.copy(
            counter = state.counter.copy(value = value),
            history = state.history + "Установка: $value ($timestamp)",
            lastUpdated = timestamp
        )
    }

    /**
     * Обрабатывает интент увеличения счетчика на 10 единиц.
     * Значение ограничивается максимальным допустимым значением.
     * Добавляет запись в историю, метку времени и эффект тоста.
     *
     * @param state текущее состояние
     * @return новое состояние после увеличения на 10 (или до максимума)
     */
    private fun handleIncrementByTen(state: CounterState): CounterState {
        val newValue = minOf(state.counter.value + 10, state.counter.maxValue)
        val timestamp = timeProvider.getCurrentTime()  // Получение текущего времени

        return state.copy(
            counter = state.counter.copy(value = newValue),
            history = state.history + "Увеличение на 10: $newValue ($timestamp)",
            lastUpdated = timestamp,
            effects = state.effects + CounterEffect.ShowToast("Увеличено на 10!")
        )
    }

    /**
     * Обрабатывает интент уменьшения счетчика на 10 единиц.
     * Значение ограничивается минимальным допустимым значением.
     * Добавляет запись в историю, метку времени и эффект тоста.
     *
     * @param state текущее состояние
     * @return новое состояние после уменьшения на 10 (или до минимума)
     */
    private fun handleDecrementByTen(state: CounterState): CounterState {
        val newValue = maxOf(state.counter.value - 10, state.counter.minValue)
        val timestamp = timeProvider.getCurrentTime()  // Получение текущего времени

        return state.copy(
            counter = state.counter.copy(value = newValue),
            history = state.history + "Уменьшение на 10: $newValue ($timestamp)",
            lastUpdated = timestamp,
            effects = state.effects + CounterEffect.ShowToast("Уменьшено на 10!")
        )
    }

    /**
     * Обрабатывает интент очистки истории операций.
     * Очищает список истории и генерирует эффект тоста.
     *
     * @param state текущее состояние
     * @return новое состояние с пустой историей
     */
    private fun handleClearHistory(state: CounterState): CounterState {
        return state.copy(
            history = emptyList(),
            effects = state.effects + CounterEffect.ShowToast("История очищена!")
        )
    }

    /**
     * Обрабатывает интент сброса ошибки.
     * Удаляет сообщение об ошибке из состояния.
     *
     * @param state текущее состояние
     * @return новое состояние без ошибки
     */
    private fun handleErrorHandled(state: CounterState): CounterState {
        return state.copy(error = null)
    }

    /**
     * Обрабатывает тестовый интент показа тоста.
     * Генерирует эффект показа тоста с тестовым сообщением.
     *
     * @param state текущее состояние
     * @return новое состояние с добавленным эффектом тоста
     */
    private fun handleShowToastEffect(state: CounterState): CounterState {
        return state.copy(
            effects = state.effects + CounterEffect.ShowToast("Это тестовое сообщение!")
        )
    }

    /**
     * Обрабатывает тестовый интент показа снекбара.
     * Генерирует эффект показа снекбара с тестовым сообщением.
     *
     * @param state текущее состояние
     * @return новое состояние с добавленным эффектом снекбара
     */
    private fun handleShowSnackbarEffect(state: CounterState): CounterState {
        return state.copy(
            effects = state.effects + CounterEffect.ShowSnackbar("Это тестовый снекбар!")
        )
    }
}