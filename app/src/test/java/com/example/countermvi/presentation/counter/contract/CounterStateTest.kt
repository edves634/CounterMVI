package com.example.countermvi.presentation.counter.contract

import org.junit.Assert.*
import org.junit.Test

class CounterStatisticsTest {

    /**
     * Тест проверяет корректность начальных значений статистики по умолчанию.
     * Ожидается, что все счетчики статистики будут инициализированы нулевыми значениями:
     * - incrementCount = 0
     * - decrementCount = 0
     * - resetCount = 0
     * - totalOperations = 0
     */
    @Test
    fun `default statistics should have zero values`() {
        // Дано: экземпляр статистики со значениями по умолчанию
        val stats = CounterStatistics()

        // Тогда: проверяем, что все значения равны 0
        assertEquals(0, stats.incrementCount)
        assertEquals(0, stats.decrementCount)
        assertEquals(0, stats.resetCount)
        assertEquals(0, stats.totalOperations)
    }

    /**
     * Тест проверяет корректность работы метода copy() для создания нового экземпляра
     * статистики с обновленными значениями.
     * Ожидается:
     * - Новый экземпляр содержит обновленные значения
     * - Исходный экземпляр остается неизменным
     * - Новый и исходный экземпляры являются разными объектами
     */
    @Test
    fun `copy with updated values should create new instance`() {
        // Дано: исходный экземпляр статистики с нулевыми значениями
        val original = CounterStatistics()

        // Когда: создаем копию с обновленными значениями
        val updated = original.copy(
            incrementCount = 5,
            decrementCount = 3,
            resetCount = 2,
            totalOperations = 10
        )

        // Тогда: проверяем, что новый экземпляр содержит обновленные значения
        assertEquals(5, updated.incrementCount)
        assertEquals(3, updated.decrementCount)
        assertEquals(2, updated.resetCount)
        assertEquals(10, updated.totalOperations)

        // Исходный экземпляр остается неизменным
        assertEquals(0, original.incrementCount)
    }

    /**
     * Тест проверяет корректность реализации методов equals() и hashCode()
     * для класса CounterStatistics.
     * Ожидается:
     * - Два экземпляра с одинаковыми значениями полей должны быть равны
     * - Хэш-коды равных объектов должны совпадать
     */
    @Test
    fun `statistics equality should work correctly`() {
        // Дано: два экземпляра статистики с одинаковыми значениями
        val stats1 = CounterStatistics(
            incrementCount = 5,
            decrementCount = 3,
            resetCount = 2,
            totalOperations = 10
        )

        val stats2 = CounterStatistics(
            incrementCount = 5,
            decrementCount = 3,
            resetCount = 2,
            totalOperations = 10
        )

        // Тогда: проверяем равенство объектов и хэш-кодов
        assertEquals(stats1, stats2)
        assertEquals(stats1.hashCode(), stats2.hashCode())
    }
}