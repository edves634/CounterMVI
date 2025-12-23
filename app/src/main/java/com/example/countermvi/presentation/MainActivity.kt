package com.example.countermvi.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.countermvi.presentation.counter.ui.CounterScreen
import com.example.countermvi.presentation.counter.ui.theme.CounterMVITheme
import com.example.countermvi.presentation.counter.viewmodel.CounterMviViewModel

/**
 * Основная Activity приложения, являющаяся точкой входа в UI-слой.
 * Использует Jetpack Compose для декларативного UI и следует паттерну MVI.
 */
class MainActivity : ComponentActivity() {

    /**
     * ViewModel счетчика, управляющая состоянием и бизнес-логикой.
     * Инициализируется с помощью делегата `viewModels()`, который обеспечивает
     * сохранение состояния при повороте экрана и других изменениях конфигурации.
     */
    private val viewModel: CounterMviViewModel by viewModels()

    /**
     * Точка создания Activity. Вызывается системой при первом создании Activity.
     * Здесь происходит настройка пользовательского интерфейса.
     *
     * @param savedInstanceState сохраненное состояние Activity (если было пересоздание)
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Установка Compose как способа отрисовки UI
        setContent {
            // Применение пользовательской темы приложения
            CounterMVITheme {
                // Базовый контейнер для всего UI, обеспечивающий фон и размер
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Отображение основного экрана счетчика с передачей ViewModel
                    CounterScreen(viewModel = viewModel)
                }
            }
        }
    }
}