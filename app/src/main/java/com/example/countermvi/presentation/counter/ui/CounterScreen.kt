package com.example.countermvi.presentation.counter.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.countermvi.presentation.counter.ui.components.CounterDisplay
import com.example.countermvi.presentation.counter.ui.components.StatisticsDisplay
import com.example.countermvi.presentation.counter.contract.CounterEffect
import com.example.countermvi.presentation.counter.contract.CounterIntent
import com.example.countermvi.presentation.counter.viewmodel.CounterMviViewModel


/**
 * Основной экран приложения "Счетчик" с использованием MVI-архитектуры.
 * Отображает интерфейс счетчика, обрабатывает пользовательские действия и показывает состояние.
 *
 * @param viewModel ViewModel, управляющая состоянием и логикой счетчика
 */
@Composable
fun CounterScreen(viewModel: CounterMviViewModel) {
    // Сбор состояния из ViewModel с учетом жизненного цикла
    val state by viewModel.state.collectAsStateWithLifecycle()
    // Получение потока эффектов из ViewModel
    val effectFlow = viewModel.effect
    // Создание области видимости корутин для обработки эффектов
    rememberCoroutineScope()

    // Обработка эффектов (тосты, снекбары, навигация)
    LaunchedEffect(key1 = effectFlow) {
        effectFlow.collect { effect ->
            when (effect) {
                is CounterEffect.ShowToast -> {
                    // Показать Toast (в Compose нужно использовать контекст)
                    // Реализация зависит от выбранного подхода к управлению UI-эффектами
                }
                is CounterEffect.ShowSnackbar -> {
                    // Показать Snackbar
                    // Для отображения Snackbar обычно используется SnackbarHost
                }
                is CounterEffect.NavigateTo -> {
                    // Навигация на другой экран
                    // Требуется передача контроллера навигации или использование Navigation Compose
                }
            }
        }
    }

    // Основной контейнер экрана
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Компонент отображения текущего значения счетчика
        CounterDisplay(
            counter = state.counter,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Компонент отображения статистики операций
        StatisticsDisplay(
            statistics = state.statistics,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Секция основных кнопок управления счетчиком
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Кнопка увеличения счетчика на 1
            Button(
                onClick = { viewModel.processIntent(CounterIntent.Increment) },
                enabled = state.counter.canIncrement() // Кнопка активна только если можно увеличить
            ) {
                Text("+")
            }

            // Кнопка уменьшения счетчика на 1
            Button(
                onClick = { viewModel.processIntent(CounterIntent.Decrement) },
                enabled = state.counter.canDecrement() // Кнопка активна только если можно уменьшить
            ) {
                Text("-")
            }

            // Кнопка сброса счетчика к нулю
            Button(
                onClick = { viewModel.processIntent(CounterIntent.Reset) }
            ) {
                Text("Сбросить")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Секция дополнительных кнопок для быстрого изменения значений
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Кнопка увеличения счетчика на 10
            Button(
                onClick = { viewModel.processIntent(CounterIntent.IncrementByTen) }
            ) {
                Text("+10")
            }

            // Кнопка уменьшения счетчика на 10
            Button(
                onClick = { viewModel.processIntent(CounterIntent.DecrementByTen) }
            ) {
                Text("-10")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Кнопка очистки истории операций
        Button(
            onClick = { viewModel.processIntent(CounterIntent.ClearHistory) }
        ) {
            Text("Очистить историю")
        }

        // Секция отображения истории операций (последние 5 записей)
        if (state.history.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "История операций:",
                style = MaterialTheme.typography.titleMedium
            )
            Column {
                state.history.takeLast(5).forEach { record ->
                    Text(text = record)
                }
            }
        }

        // Секция отображения и обработки ошибок
        state.error?.let { error ->
            Spacer(modifier = Modifier.height(16.dp))
            // Отображение текста ошибки
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
            // Кнопка для подтверждения ошибки (очистки состояния ошибки)
            Button(
                onClick = { viewModel.processIntent(CounterIntent.ErrorHandled) }
            ) {
                Text("ОК")
            }
        }
    }
}