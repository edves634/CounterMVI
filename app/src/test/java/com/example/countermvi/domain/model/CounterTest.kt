package com.example.countermvi.domain.model

import com.example.countermvi.presentation.counter.contract.Counter
import org.junit.Assert.*
import org.junit.Test

class CounterTest {

    /**
     * Тест проверяет, что счетчик находится в состоянии "максимальное значение",
     * когда текущее значение равно максимальному значению.
     * Ожидается:
     * - isAtMax = true
     * - isAtMin = false
     * - canIncrement() = false (нельзя увеличить)
     * - canDecrement() = true (можно уменьшить)
     */
    @Test
    fun `counter should be at max when value equals maxValue`() {
        // Дано: счетчик со значением 100 и максимальным значением 100
        val counter = Counter(value = 100, maxValue = 100)

        // Тогда: выполняются проверки состояния
        assertTrue(counter.isAtMax)
        assertFalse(counter.isAtMin)
        assertFalse(counter.canIncrement())
        assertTrue(counter.canDecrement())
    }

    /**
     * Тест проверяет, что счетчик находится в состоянии "минимальное значение",
     * когда текущее значение равно минимальному значению.
     * Ожидается:
     * - isAtMax = false
     * - isAtMin = true
     * - canIncrement() = true (можно увеличить)
     * - canDecrement() = false (нельзя уменьшить)
     */
    @Test
    fun `counter should be at min when value equals minValue`() {
        // Дано: счетчик со значением -50 и минимальным значением -50
        val counter = Counter(value = -50, minValue = -50)

        // Тогда: выполняются проверки состояния
        assertFalse(counter.isAtMax)
        assertTrue(counter.isAtMin)
        assertTrue(counter.canIncrement())
        assertFalse(counter.canDecrement())
    }

    /**
     * Тест проверяет возможность увеличения счетчика,
     * когда текущее значение меньше максимального.
     * Ожидается:
     * - isAtMax = false
     * - isAtMin = false
     * - canIncrement() = true (можно увеличить)
     * - canDecrement() = true (можно уменьшить)
     */
    @Test
    fun `counter can increment when value less than max`() {
        // Дано: счетчик со значением 50 и максимальным значением 100
        val counter = Counter(value = 50, maxValue = 100)

        // Тогда: выполняются проверки состояния
        assertFalse(counter.isAtMax)
        assertFalse(counter.isAtMin)
        assertTrue(counter.canIncrement())
        assertTrue(counter.canDecrement())
    }

    /**
     * Тест проверяет возможность уменьшения счетчика,
     * когда текущее значение больше минимального.
     * Ожидается:
     * - isAtMax = false
     * - isAtMin = false
     * - canIncrement() = true (можно увеличить)
     * - canDecrement() = true (можно уменьшить)
     */
    @Test
    fun `counter can decrement when value greater than min`() {
        // Дано: счетчик со значением 0 и минимальным значением -50
        val counter = Counter(value = 0, minValue = -50)

        // Тогда: выполняются проверки состояния
        assertFalse(counter.isAtMax)
        assertFalse(counter.isAtMin)
        assertTrue(counter.canIncrement())
        assertTrue(counter.canDecrement())
    }

    /**
     * Тест проверяет корректность значений по умолчанию для счетчика.
     * Ожидаемые значения по умолчанию:
     * - value = 0
     * - maxValue = 100
     * - minValue = -50
     * Также проверяется состояние:
     * - isAtMax = false
     * - isAtMin = false
     * - canIncrement() = true (можно увеличить)
     * - canDecrement() = true (можно уменьшить)
     */
    @Test
    fun `counter with default values should have correct limits`() {
        // Дано: счетчик с значениями по умолчанию
        val counter = Counter()

        // Тогда: проверяются значения и состояние
        assertEquals(0, counter.value)
        assertEquals(100, counter.maxValue)
        assertEquals(-50, counter.minValue)
        assertFalse(counter.isAtMax)
        assertFalse(counter.isAtMin)
        assertTrue(counter.canIncrement())
        assertTrue(counter.canDecrement())
    }
}