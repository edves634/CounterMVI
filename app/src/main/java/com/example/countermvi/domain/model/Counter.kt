package com.example.countermvi.domain.model

/**
 * Доменная модель счетчика.
 * Содержит бизнес-логику и валидацию.
 */
data class Counter(
    val value: Int = 0,
    val minValue: Int = -50,
    val maxValue: Int = 100
) {

    /**
     * Проверяет возможность увеличения значения счетчика.
     * @return true - если текущее значение меньше максимального,
     *         false - в противном случае
     */
    fun canIncrement(): Boolean = value < maxValue

    /**
     * Проверяет возможность уменьшения значения счетчика.
     * @return true - если текущее значение больше минимального,
     *         false - в противном случае
     */
    fun canDecrement(): Boolean = value > minValue

    /**
     * Увеличивает значение счетчика на 1.
     * @return новый экземпляр Counter с увеличенным значением,
     *         если увеличение разрешено, иначе возвращает текущий объект без изменений
     */
    fun increment(): Counter = if (canIncrement()) copy(value = value + 1) else this

    /**
     * Уменьшает значение счетчика на 1.
     * @return новый экземпляр Counter с уменьшенным значением,
     *         если уменьшение разрешено, иначе возвращает текущий объект без изменений
     */
    fun decrement(): Counter = if (canDecrement()) copy(value = value - 1) else this

    /**
     * Сбрасывает значение счетчика к нулю.
     * @return новый экземпляр Counter со значением 0
     */
    fun reset(): Counter = copy(value = 0)

    /**
     * Проверяет валидность переданного значения.
     * @param value значение для проверки
     * @return true - если значение находится в допустимом диапазоне [minValue .. maxValue],
     *         false - в противном случае
     */
    fun isValidValue(value: Int): Boolean = value in minValue..maxValue
}