package com.example.countermvi.presentation.counter.contract

/**
 * Единое неизменяемое состояние экрана счетчика.
 * Вся информация для отрисовки UI находится здесь.
 */
data class CounterState(
    // Основное значение счетчика
    val counter: Counter = Counter(),

    // История операций (для отображения)
    val history: List<String> = emptyList(),

    // Состояние загрузки (можно использовать для будущих фич)
    val isLoading: Boolean = false,

    // Сообщение об ошибке
    val error: String? = null,

    // Эффекты для одноразовых событий (показать тост, навигация и т.д.)
    val effects: List<CounterEffect> = emptyList(),

    // Статистика (демонстрация расширения состояния)
    val statistics: CounterStatistics = CounterStatistics(),

    // Время последнего обновления
    val lastUpdated: String = ""
)

/**
 * Статистика по операциям со счетчиком
 */
data class CounterStatistics(
    val incrementCount: Int = 0,
    val decrementCount: Int = 0,
    val resetCount: Int = 0,
    val totalOperations: Int = 0
)

/**
 * Модель счетчика из доменного слоя
 */
data class Counter(
    val value: Int = 0,
    val maxValue: Int = 100,
    val minValue: Int = -50
) {
    val isAtMax: Boolean get() = value >= maxValue
    val isAtMin: Boolean get() = value <= minValue

    fun canIncrement(): Boolean = value < maxValue
    fun canDecrement(): Boolean = value > minValue
}