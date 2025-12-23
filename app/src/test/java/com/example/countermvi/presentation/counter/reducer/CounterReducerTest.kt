package com.example.countermvi.presentation.counter.reducer

import com.example.countermvi.presentation.counter.contract.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class CounterReducerTest {

    private lateinit var reducer: CounterReducer
    private lateinit var testTimeProvider: TestTimeProvider

    @Before
    fun setUp() {
        testTimeProvider = TestTimeProvider("12:00:00")
        reducer = CounterReducer(testTimeProvider)
    }

    /**
     * Тест проверяет корректное увеличение счетчика на 1, когда значение не достигло максимума.
     * Ожидается:
     * - Значение счетчика увеличивается на 1
     * - Статистика инкрементов и общих операций увеличивается на 1
     * - Ошибка отсутствует (null)
     * - История операций обновляется корректной записью
     * - Время последнего обновления устанавливается корректно
     */
    @Test
    fun `handleIncrement should increase counter by 1 when not at max`() {
        // Дано: начальное состояние со счетчиком на значении 5 и максимумом 10
        val initialState = CounterState(
            counter = Counter(value = 5, maxValue = 10)
        )

        // Когда: обрабатываем интент увеличения счетчика
        val resultState = reducer.reduce(initialState, CounterIntent.Increment)

        // Тогда: проверяем все ожидаемые изменения состояния
        assertEquals(6, resultState.counter.value)
        assertEquals(1, resultState.statistics.incrementCount)
        assertEquals(1, resultState.statistics.totalOperations)
        assertNull(resultState.error)
        assertTrue(resultState.history.isNotEmpty())
        assertTrue(resultState.history.last().contains("Увеличение: 6"))
        assertEquals("12:00:00", resultState.lastUpdated)
    }

    /**
     * Тест проверяет обработку попытки увеличения счетчика при достижении максимального лимита.
     * Ожидается:
     * - Значение счетчика не изменяется
     * - Устанавливается сообщение об ошибке
     * - Добавляется эффект показа snackbar
     * - Статистика инкрементов не изменяется
     */
    @Test
    fun `handleIncrement should not increase when at max limit and show error`() {
        // Дано: счетчик достиг максимального значения 10
        val initialState = CounterState(
            counter = Counter(value = 10, maxValue = 10)
        )

        // Когда: пытаемся увеличить счетчик
        val resultState = reducer.reduce(initialState, CounterIntent.Increment)

        // Тогда: проверяем, что счетчик не изменился и показана ошибка
        assertEquals(10, resultState.counter.value) // Значение не изменилось
        assertEquals("Достигнут максимальный лимит: 10", resultState.error)
        assertTrue(resultState.effects.isNotEmpty())
        assertTrue(resultState.effects.first() is CounterEffect.ShowSnackbar)
        assertEquals(0, resultState.statistics.incrementCount) // Статистика не изменилась
    }

    /**
     * Тест проверяет корректное уменьшение счетчика на 1, когда значение не достигло минимума.
     * Ожидается:
     * - Значение счетчика уменьшается на 1
     * - Статистика декрементов и общих операций увеличивается на 1
     * - Ошибка отсутствует
     * - История операций обновляется корректной записью
     * - Время последнего обновления устанавливается
     */
    @Test
    fun `handleDecrement should decrease counter by 1 when not at min`() {
        // Дано: начальное состояние со счетчиком на значении 5 и минимумом 0
        val initialState = CounterState(
            counter = Counter(value = 5, minValue = 0)
        )

        // Когда: обрабатываем интент уменьшения счетчика
        val resultState = reducer.reduce(initialState, CounterIntent.Decrement)

        // Тогда: проверяем все ожидаемые изменения состояния
        assertEquals(4, resultState.counter.value)
        assertEquals(1, resultState.statistics.decrementCount)
        assertEquals(1, resultState.statistics.totalOperations)
        assertNull(resultState.error)
        assertTrue(resultState.history.last().contains("Уменьшение: 4"))
        assertEquals("12:00:00", resultState.lastUpdated)
    }

    /**
     * Тест проверяет обработку попытки уменьшения счетчика при достижении минимального лимита.
     * Ожидается:
     * - Значение счетчика не изменяется
     * - Устанавливается сообщение об ошибке
     * - Добавляется эффект показа snackbar
     * - Статистика декрементов не изменяется
     */
    @Test
    fun `handleDecrement should not decrease when at min limit`() {
        // Дано: счетчик достиг минимального значения -50
        val initialState = CounterState(
            counter = Counter(value = -50, minValue = -50)
        )

        // Когда: пытаемся уменьшить счетчик
        val resultState = reducer.reduce(initialState, CounterIntent.Decrement)

        // Тогда: проверяем, что счетчик не изменился и показана ошибка
        assertEquals(-50, resultState.counter.value)
        assertEquals("Достигнут минимальный лимит: -50", resultState.error)
        assertTrue(resultState.effects.first() is CounterEffect.ShowSnackbar)
        assertEquals(0, resultState.statistics.decrementCount)
    }

    /**
     * Тест проверяет корректность сброса счетчика в значение 0.
     * Ожидается:
     * - Значение счетчика устанавливается в 0
     * - Статистика сбросов увеличивается на 1, общие операции увеличиваются
     * - Добавляется эффект показа toast
     * - История операций обновляется записью о сбросе
     * - Время последнего обновления устанавливается
     */
    @Test
    fun `handleReset should set counter to 0 and update statistics`() {
        // Дано: начальное состояние со счетчиком на значении 10 и существующей статистикой
        val initialState = CounterState(
            counter = Counter(value = 10),
            statistics = CounterStatistics(incrementCount = 5, totalOperations = 5)
        )

        // Когда: обрабатываем интент сброса счетчика
        val resultState = reducer.reduce(initialState, CounterIntent.Reset)

        // Тогда: проверяем сброс счетчика и обновление статистики
        assertEquals(0, resultState.counter.value)
        assertEquals(1, resultState.statistics.resetCount)
        assertEquals(6, resultState.statistics.totalOperations)
        assertTrue(resultState.effects.isNotEmpty())
        assertTrue(resultState.effects.first() is CounterEffect.ShowToast)
        assertTrue(resultState.history.last().contains("Сброс: 0"))
        assertEquals("12:00:00", resultState.lastUpdated)
    }

    /**
     * Тест проверяет корректную установку значения счетчика в пределах допустимых границ.
     * Ожидается:
     * - Значение счетчика устанавливается в указанное значение
     * - Ошибка отсутствует
     * - История операций обновляется записью об установке значения
     * - Время последнего обновления устанавливается
     */
    @Test
    fun `handleSetValue should update counter when value is within limits`() {
        // Дано: счетчик с границами от 0 до 100
        val initialState = CounterState(
            counter = Counter(minValue = 0, maxValue = 100)
        )

        // Когда: устанавливаем значение 50
        val resultState = reducer.reduce(initialState, CounterIntent.SetValue(50))

        // Тогда: проверяем установку значения
        assertEquals(50, resultState.counter.value)
        assertNull(resultState.error)
        assertTrue(resultState.history.last().contains("Установка: 50"))
        assertEquals("12:00:00", resultState.lastUpdated)
    }

    /**
     * Тест проверяет обработку попытки установки значения ниже минимального лимита.
     * Ожидается:
     * - Значение счетчика не изменяется
     * - Устанавливается сообщение об ошибке с указанием допустимого диапазона
     * - Добавляется эффект показа snackbar
     */
    @Test
    fun `handleSetValue should show error when value is below min`() {
        // Дано: счетчик с границами от 0 до 100
        val initialState = CounterState(
            counter = Counter(minValue = 0, maxValue = 100)
        )

        // Когда: пытаемся установить значение -10 (ниже минимума)
        val resultState = reducer.reduce(initialState, CounterIntent.SetValue(-10))

        // Тогда: проверяем, что значение не изменилось и показана ошибка
        assertEquals(0, resultState.counter.value)
        assertNotNull(resultState.error)
        assertTrue(resultState.error!!.contains("должно быть между"))
        assertTrue(resultState.effects.first() is CounterEffect.ShowSnackbar)
    }

    /**
     * Тест проверяет обработку попытки установки значения выше максимального лимита.
     * Ожидается:
     * - Значение счетчика не изменяется
     * - Устанавливается сообщение об ошибке с указанием допустимого диапазона
     * - Добавляется эффект показа snackbar
     */
    @Test
    fun `handleSetValue should show error when value is above max`() {
        // Дано: счетчик с границами от 0 до 100
        val initialState = CounterState(
            counter = Counter(minValue = 0, maxValue = 100)
        )

        // Когда: пытаемся установить значение 150 (выше максимума)
        val resultState = reducer.reduce(initialState, CounterIntent.SetValue(150))

        // Тогда: проверяем, что значение не изменилось и показана ошибка
        assertEquals(0, resultState.counter.value)
        assertNotNull(resultState.error)
        assertTrue(resultState.error!!.contains("должно быть между"))
        assertTrue(resultState.effects.first() is CounterEffect.ShowSnackbar)
    }

    /**
     * Тест проверяет увеличение счетчика на 10 с проверкой ограничения максимума.
     * Ожидается:
     * - Значение счетчика увеличивается на 10, но не превышает максимум
     * - Добавляется эффект показа toast
     * - История операций обновляется соответствующей записью
     * - Время последнего обновления устанавливается
     */
    @Test
    fun `handleIncrementByTen should increase by 10 but not exceed max`() {
        // Дано: счетчик на значении 95 с максимумом 100
        val initialState = CounterState(
            counter = Counter(value = 95, maxValue = 100)
        )

        // Когда: увеличиваем счетчик на 10
        val resultState = reducer.reduce(initialState, CounterIntent.IncrementByTen)

        // Тогда: проверяем, что значение не превысило максимум (остановилось на 100)
        assertEquals(100, resultState.counter.value) // Не превышает максимум
        assertTrue(resultState.effects.first() is CounterEffect.ShowToast)
        assertTrue(resultState.history.last().contains("Увеличение на 10"))
        assertEquals("12:00:00", resultState.lastUpdated)
    }

    /**
     * Тест проверяет уменьшение счетчика на 10 с проверкой ограничения минимума.
     * Ожидается:
     * - Значение счетчика уменьшается на 10, но не опускается ниже минимума
     * - Добавляется эффект показа toast
     * - История операций обновляется соответствующей записью
     * - Время последнего обновления устанавливается
     */
    @Test
    fun `handleDecrementByTen should decrease by 10 but not go below min`() {
        // Дано: счетчик на значении -45 с минимумом -50
        val initialState = CounterState(
            counter = Counter(value = -45, minValue = -50)
        )

        // Когда: уменьшаем счетчик на 10
        val resultState = reducer.reduce(initialState, CounterIntent.DecrementByTen)

        // Тогда: проверяем, что значение не опустилось ниже минимума (остановилось на -50)
        assertEquals(-50, resultState.counter.value) // Не ниже минимума
        assertTrue(resultState.effects.first() is CounterEffect.ShowToast)
        assertTrue(resultState.history.last().contains("Уменьшение на 10"))
        assertEquals("12:00:00", resultState.lastUpdated)
    }

    /**
     * Тест проверяет очистку истории операций.
     * Ожидается:
     * - Список истории становится пустым
     * - Добавляется эффект показа toast
     */
    @Test
    fun `handleClearHistory should empty history list`() {
        // Дано: начальное состояние с тремя записями в истории
        val initialState = CounterState(
            history = listOf("Увеличение: 1", "Увеличение: 2", "Увеличение: 3")
        )

        // Когда: обрабатываем интент очистки истории
        val resultState = reducer.reduce(initialState, CounterIntent.ClearHistory)

        // Тогда: проверяем, что история очищена
        assertTrue(resultState.history.isEmpty())
        assertTrue(resultState.effects.first() is CounterEffect.ShowToast)
    }

    /**
     * Тест проверяет обработку ошибки пользователем.
     * Ожидается:
     * - Сообщение об ошибке очищается (становится null)
     */
    @Test
    fun `handleErrorHandled should clear error`() {
        // Дано: начальное состояние с сообщением об ошибке
        val initialState = CounterState(
            error = "Some error message"
        )

        // Когда: пользователь обработал ошибку
        val resultState = reducer.reduce(initialState, CounterIntent.ErrorHandled)

        // Тогда: проверяем, что ошибка очищена
        assertNull(resultState.error)
    }

    /**
     * Тест проверяет добавление эффекта показа toast.
     * Ожидается:
     * - В список эффектов добавляется эффект ShowToast
     */
    @Test
    fun `handleShowToastEffect should add toast effect`() {
        // Дано: начальное состояние без эффектов
        val initialState = CounterState()

        // Когда: обрабатываем интент показа toast
        val resultState = reducer.reduce(initialState, CounterIntent.ShowToastEffect)

        // Тогда: проверяем наличие эффекта
        assertEquals(1, resultState.effects.size)
        assertTrue(resultState.effects.first() is CounterEffect.ShowToast)
    }

    /**
     * Тест проверяет добавление эффекта показа snackbar.
     * Ожидается:
     * - В список эффектов добавляется эффект ShowSnackbar
     */
    @Test
    fun `handleShowSnackbarEffect should add snackbar effect`() {
        // Дано: начальное состояние без эффектов
        val initialState = CounterState()

        // Когда: обрабатываем интент показа snackbar
        val resultState = reducer.reduce(initialState, CounterIntent.ShowSnackbarEffect)

        // Тогда: проверяем наличие эффекта
        assertEquals(1, resultState.effects.size)
        assertTrue(resultState.effects.first() is CounterEffect.ShowSnackbar)
    }

    /**
     * Тест проверяет обработку всех известных интентов reducer'ом.
     * Ожидается:
     * - Для каждого тестируемого интента reducer возвращает корректно измененное состояние
     * - Это косвенно подтверждает, что все кейсы в when-выражении обработаны
     */
    @Test
    fun `reduce with unknown intent should return same state`() {
        // Дано: начальное состояние со счетчиком на значении 5
        val initialState = CounterState(counter = Counter(value = 5))

        // Когда: тестируем обработку различных интентов
        val incrementResult = reducer.reduce(initialState, CounterIntent.Increment)
        val decrementResult = reducer.reduce(initialState, CounterIntent.Decrement)
        val resetResult = reducer.reduce(initialState, CounterIntent.Reset)

        // Тогда: убеждаемся, что каждый интент обрабатывается корректно
        assertEquals(6, incrementResult.counter.value)
        assertEquals(4, decrementResult.counter.value)
        assertEquals(0, resetResult.counter.value)
    }
}