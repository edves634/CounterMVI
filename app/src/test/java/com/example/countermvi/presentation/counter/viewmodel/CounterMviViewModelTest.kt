package com.example.countermvi.presentation.counter.viewmodel

import com.example.countermvi.presentation.counter.contract.*
import com.example.countermvi.presentation.counter.reducer.CounterReducer
import com.example.countermvi.presentation.counter.reducer.TestTimeProvider
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
@OptIn(ExperimentalCoroutinesApi::class)
class CounterMviViewModelTest {

    private lateinit var viewModel: CounterMviViewModel
    private lateinit var reducer: CounterReducer
    private lateinit var testTimeProvider: TestTimeProvider
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        testTimeProvider = TestTimeProvider("12:00:00")
        reducer = CounterReducer(testTimeProvider)
        viewModel = CounterMviViewModel(reducer)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Тест проверяет корректность начального состояния ViewModel.
     * Ожидается, что ViewModel инициализируется с дефолтными значениями:
     * - Значение счетчика = 0
     * - Максимальное значение = 100
     * - Минимальное значение = -50
     * - История операций пуста
     * - Состояние загрузки неактивно
     * - Ошибка отсутствует
     * - Эффекты отсутствуют
     */
    @Test
    fun `initial state should have default values`() = runTest {
        // Когда: получаем начальное состояние ViewModel
        val initialState = viewModel.state.first()

        // Тогда: проверяем все дефолтные значения
        assertEquals(0, initialState.counter.value)
        assertEquals(100, initialState.counter.maxValue)
        assertEquals(-50, initialState.counter.minValue)
        assertTrue(initialState.history.isEmpty())
        assertFalse(initialState.isLoading)
        assertNull(initialState.error)
        assertTrue(initialState.effects.isEmpty())
    }

    /**
     * Тест проверяет обработку интента Increment и корректное обновление состояния.
     * Ожидается:
     * - Значение счетчика увеличивается на 1
     * - Статистика инкрементов и общих операций увеличивается на 1
     * - История операций не пуста
     * - Время последнего обновления устанавливается корректно
     */
    @Test
    fun `processIntent Increment should update state`() = runTest {
        // Дано: получаем начальное значение счетчика
        val initialCount = viewModel.state.first().counter.value

        // Когда: отправляем интент увеличения счетчика
        viewModel.processIntent(CounterIntent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()

        // Тогда: проверяем обновленное состояние
        val state = viewModel.state.first()
        assertEquals(initialCount + 1, state.counter.value)
        assertEquals(1, state.statistics.incrementCount)
        assertEquals(1, state.statistics.totalOperations)
        assertTrue(state.history.isNotEmpty())
        assertEquals("12:00:00", state.lastUpdated)
    }

    /**
     * Тест проверяет обработку нескольких интентов и корректное обновление статистики.
     * Ожидается:
     * - Счетчик возвращается к 0 после сброса
     * - Статистика корректно считает операции по типам
     * - Общее количество операций равно сумме всех операций
     * - История содержит все операции
     */
    @Test
    fun `processIntent multiple intents should update statistics correctly`() = runTest {
        // Когда: отправляем последовательность интентов
        viewModel.processIntent(CounterIntent.Increment)
        viewModel.processIntent(CounterIntent.Increment)
        viewModel.processIntent(CounterIntent.Decrement)
        viewModel.processIntent(CounterIntent.Reset)
        testDispatcher.scheduler.advanceUntilIdle()

        // Тогда: проверяем итоговое состояние
        val state = viewModel.state.first()
        assertEquals(0, state.counter.value)
        assertEquals(2, state.statistics.incrementCount)
        assertEquals(1, state.statistics.decrementCount)
        assertEquals(1, state.statistics.resetCount)
        assertEquals(4, state.statistics.totalOperations)
        assertEquals(4, state.history.size)
    }

    /**
     * Тест проверяет, что эффекты очищаются после обработки.
     * Ожидается:
     * - После отправки интента с эффектом, эффект добавляется в состояние
     * - Через короткое время эффект автоматически очищается из состояния
     * - Состояние возвращается к отсутствию эффектов
     */
    @Test
    fun `processIntent should clear effects after processing`() = runTest {
        // Когда: отправляем интент с эффектом toast
        viewModel.processIntent(CounterIntent.ShowToastEffect)
        testDispatcher.scheduler.advanceUntilIdle()

        // Даем время на автоматическую очистку эффектов
        delay(100)

        // Тогда: проверяем, что эффекты очищены
        val state = viewModel.state.first()
        assertTrue(state.effects.isEmpty())
    }

    /**
     * Тест проверяет обработку ошибок при установке некорректного значения.
     * Ожидается:
     * - При попытке установить значение ниже минимального возникает ошибка
     * - Ошибка содержит сообщение о допустимом диапазоне значений
     */
    @Test
    fun `processIntent with error should show error in state`() = runTest {
        // Когда: пытаемся установить значение -100 (ниже минимального -50)
        viewModel.processIntent(CounterIntent.SetValue(-100))
        testDispatcher.scheduler.advanceUntilIdle()

        // Тогда: проверяем наличие ошибки в состоянии
        val state = viewModel.state.first()
        assertNotNull(state.error)
        assertTrue(state.error!!.contains("должно быть между"))
    }

    /**
     * Тест проверяет очистку ошибки после её обработки пользователем.
     * Ожидается:
     * - После возникновения ошибки она присутствует в состоянии
     * - После отправки интента ErrorHandled ошибка очищается
     * - Состояние возвращается к отсутствию ошибок
     */
    @Test
    fun `processIntent ErrorHandled should clear error`() = runTest {
        // Дано: создаем состояние с ошибкой
        viewModel.processIntent(CounterIntent.SetValue(-100))
        testDispatcher.scheduler.advanceUntilIdle()

        // Проверяем наличие ошибки
        var state = viewModel.state.first()
        assertNotNull(state.error)

        // Когда: отправляем интент обработки ошибки
        viewModel.processIntent(CounterIntent.ErrorHandled)
        testDispatcher.scheduler.advanceUntilIdle()

        // Тогда: проверяем очистку ошибки
        state = viewModel.state.first()
        assertNull(state.error)
    }

    /**
     * Тест проверяет эмиссию эффектов через отдельный поток effect.
     * Ожидается:
     * - При обработке интента с эффектом, эффект эмитится в поток effect
     * - Эффект содержит корректное сообщение
     */
    @Test
    fun `effects flow should emit when effect is added`() = runTest {
        // Дано: начинаем сбор эффектов в список
        val effects = mutableListOf<CounterEffect>()
        val collectJob = launch {
            viewModel.effect.collect { effect ->
                effects.add(effect)
            }
        }

        // Когда: отправляем интент с эффектом toast
        viewModel.processIntent(CounterIntent.ShowToastEffect)
        testDispatcher.scheduler.advanceUntilIdle()

        // Даем время на обработку
        delay(100)

        // Тогда: проверяем наличие эффекта
        assertTrue(effects.isNotEmpty())
        assertTrue(effects.first() is CounterEffect.ShowToast)
        val toastEffect = effects.first() as CounterEffect.ShowToast
        assertEquals("Это тестовое сообщение!", toastEffect.message)

        collectJob.cancel()
    }

    /**
     * Тест проверяет увеличение счетчика на 10 через интент IncrementByTen.
     * Ожидается:
     * - Значение счетчика увеличивается на 10
     * - В истории появляется соответствующая запись
     */
    @Test
    fun `IncrementByTen should increase by 10`() = runTest {
        // Дано: получаем начальное значение счетчика
        val initialValue = viewModel.state.first().counter.value

        // Когда: отправляем интент увеличения на 10
        viewModel.processIntent(CounterIntent.IncrementByTen)
        testDispatcher.scheduler.advanceUntilIdle()

        // Тогда: проверяем обновленное состояние
        val state = viewModel.state.first()
        assertEquals(initialValue + 10, state.counter.value)
        assertTrue(state.history.last().contains("Увеличение на 10"))
    }

    /**
     * Тест проверяет очистку истории операций.
     * Ожидается:
     * - После добавления операций история не пуста
     * - После отправки интента ClearHistory история становится пустой
     */
    @Test
    fun `ClearHistory should empty history`() = runTest {
        // Дано: добавляем несколько операций в историю
        viewModel.processIntent(CounterIntent.Increment)
        viewModel.processIntent(CounterIntent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()

        var state = viewModel.state.first()
        assertEquals(2, state.history.size)

        // Когда: отправляем интент очистки истории
        viewModel.processIntent(CounterIntent.ClearHistory)
        testDispatcher.scheduler.advanceUntilIdle()

        // Тогда: проверяем, что история пуста
        state = viewModel.state.first()
        assertTrue(state.history.isEmpty())
    }

    /**
     * Тест проверяет обработку попытки увеличения при достижении максимального значения.
     * Ожидается:
     * - После 100 инкрементов счетчик достигает максимума
     * - Дополнительная попытка инкремента вызывает ошибку
     * - Значение счетчика не изменяется
     */
    @Test
    fun `processIntent Increment when at max should show error`() = runTest {
        // Дано: увеличиваем счетчик до максимального значения
        for (i in 1..100) {
            viewModel.processIntent(CounterIntent.Increment)
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Когда: пытаемся увеличить еще раз
        viewModel.processIntent(CounterIntent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()

        // Тогда: проверяем состояние
        val state = viewModel.state.first()
        assertEquals(100, state.counter.value)
        assertNotNull(state.error)
        assertEquals("Достигнут максимальный лимит: 100", state.error)
    }

    /**
     * Тест проверяет обработку попытки уменьшения при достижении минимального значения.
     * Ожидается:
     * - После 50 декрементов счетчик достигает минимума
     * - Дополнительная попытка декремента вызывает ошибку
     * - Значение счетчика не изменяется
     */
    @Test
    fun `processIntent Decrement when at min should show error`() = runTest {
        // Дано: уменьшаем счетчик до минимального значения
        for (i in 1..50) {
            viewModel.processIntent(CounterIntent.Decrement)
        }
        testDispatcher.scheduler.advanceUntilIdle()

        // Когда: пытаемся уменьшить еще раз
        viewModel.processIntent(CounterIntent.Decrement)
        testDispatcher.scheduler.advanceUntilIdle()

        // Тогда: проверяем состояние
        val state = viewModel.state.first()
        assertEquals(-50, state.counter.value)
        assertNotNull(state.error)
        assertEquals("Достигнут минимальный лимит: -50", state.error)
    }

    /**
     * Тест проверяет эмиссию состояний через поток state при каждом изменении.
     * Ожидается:
     * - При каждом интенте состояние обновляется и эмитится в поток
     * - Каждое новое состояние содержит корректные значения счетчика и статистики
     */
    @Test
    fun `state flow should emit new values on each intent`() = runTest {
        // Дано: начинаем сбор состояний в список
        val states = mutableListOf<CounterState>()
        val collectJob = launch {
            // Используем distinctUntilChanged, чтобы получать только уникальные состояния
            viewModel.state.collect { state ->
                states.add(state)
            }
        }

        // Даем время корутине начать сбор
        testDispatcher.scheduler.advanceUntilIdle()

        // Когда: отправляем последовательность интентов
        viewModel.processIntent(CounterIntent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(CounterIntent.Increment)
        testDispatcher.scheduler.advanceUntilIdle()

        viewModel.processIntent(CounterIntent.Decrement)
        testDispatcher.scheduler.advanceUntilIdle()

        // Отменяем сбор
        collectJob.cancel()

        // Тогда: проверяем все эмитированные состояния
        assertEquals(4, states.size)

        // Проверяем начальное состояние
        assertEquals(0, states[0].counter.value)

        // Проверяем первое увеличение
        assertEquals(1, states[1].counter.value)
        assertEquals(1, states[1].statistics.incrementCount)

        // Проверяем второе увеличение
        assertEquals(2, states[2].counter.value)
        assertEquals(2, states[2].statistics.incrementCount)

        // Проверяем уменьшение (финальное состояние)
        assertEquals(1, states[3].counter.value)
        assertEquals(2, states[3].statistics.incrementCount)
        assertEquals(1, states[3].statistics.decrementCount)
    }

    /**
     * Тест проверяет корректную эмиссию нескольких эффектов подряд.
     * Ожидается:
     * - Каждый эффект эмитится в поток effect
     * - Порядок эмиссии соответствует порядку обработки интентов
     * - Типы эффектов соответствуют отправленным интентам
     */
    @Test
    fun `multiple effects should be emitted correctly`() = runTest {
        // Дано: начинаем сбор эффектов в список
        val effects = mutableListOf<CounterEffect>()
        val collectJob = launch {
            viewModel.effect.collect { effect ->
                effects.add(effect)
            }
        }

        // Когда: отправляем несколько интентов с эффектами
        viewModel.processIntent(CounterIntent.ShowToastEffect)
        viewModel.processIntent(CounterIntent.ShowSnackbarEffect)
        viewModel.processIntent(CounterIntent.Reset)
        testDispatcher.scheduler.advanceUntilIdle()

        // Даем время на обработку
        delay(100)

        // Тогда: проверяем все эмитированные эффекты
        assertEquals(3, effects.size)
        assertTrue(effects[0] is CounterEffect.ShowToast)
        assertTrue(effects[1] is CounterEffect.ShowSnackbar)
        assertTrue(effects[2] is CounterEffect.ShowToast)

        collectJob.cancel()
    }
}