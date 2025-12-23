package com.example.countermvi.presentation.counter.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.countermvi.presentation.counter.contract.CounterEffect
import com.example.countermvi.presentation.counter.contract.CounterIntent
import com.example.countermvi.presentation.counter.contract.CounterState
import com.example.countermvi.presentation.counter.reducer.CounterReducer
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel для экрана счетчика, реализующая архитектуру MVI (Model-View-Intent).
 * Управляет состоянием приложения, обрабатывает пользовательские интенты
 * и генерирует побочные эффекты.
 *
 * @param reducer редуктор, содержащий бизнес-логику преобразования интентов в новые состояния
 */
class CounterMviViewModel(
    private val reducer: CounterReducer // Редуктор с чистой логикой преобразования состояний
) : ViewModel() {

    /**
     * Поток состояния счетчика. Хранит текущее состояние UI и бизнес-логики.
     * Используется MutableStateFlow для возможности обновления значения.
     */
    private val _state = MutableStateFlow(CounterState())

    /**
     * Публичный неизменяемый поток состояния для наблюдения из UI слоя.
     * Преобразует MutableStateFlow в StateFlow для обеспечения иммутабельности.
     */
    val state: StateFlow<CounterState> = _state.asStateFlow()

    /**
     * Поток для отправки одноразовых побочных эффектов (эффектов).
     * Используется SharedFlow для multicast-распространения эффектов.
     */
    private val _effect = MutableSharedFlow<CounterEffect>()

    /**
     * Публичный неизменяемый поток эффектов для наблюдения из UI слоя.
     * Преобразует MutableSharedFlow в SharedFlow.
     */
    val effect: SharedFlow<CounterEffect> = _effect.asSharedFlow()

    /**
     * Основной метод обработки пользовательских интентов (намерений).
     * Принимает интент, обрабатывает его через редуктор и обновляет состояние.
     * Также управляет побочными эффектами, которые генерируются редуктором.
     *
     * @param intent пользовательское намерение (действие), требующее обработки
     */
    fun processIntent(intent: CounterIntent) {
        // Получение текущего состояния
        val currentState = _state.value
        // Применение редуктора для получения нового состояния
        val newState = reducer.reduce(currentState, intent)

        // Обновление состояния новым значением
        _state.value = newState

        // Обработка побочных эффектов (если они есть в новом состоянии)
        if (newState.effects.isNotEmpty()) {
            // Запуск корутины для асинхронной отправки эффектов
            viewModelScope.launch {
                newState.effects.forEach { effect ->
                    _effect.emit(effect) // Отправка каждого эффекта в поток
                }
            }

            // Очистка эффектов в состоянии после их отправки
            // Это предотвращает повторную обработку одних и тех же эффектов
            _state.value = newState.copy(effects = emptyList())
        }
    }
}