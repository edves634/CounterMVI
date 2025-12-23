package com.example.countermvi.presentation.counter.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.countermvi.presentation.counter.contract.Counter

/**
 * Компонент для отображения текущего значения счетчика.
 * Включает визуальную индикацию достижения предельных значений.
 *
 * @param counter объект счетчика с текущим значением и лимитами
 * @param modifier модификатор Compose для настройки расположения и размера
 */
@Composable
fun CounterDisplay(
    counter: Counter,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        colors = CardDefaults.cardColors(
            // Изменение цвета фона при достижении лимитов
            containerColor = when {
                counter.isAtMax -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                counter.isAtMin -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Заголовок компонента
            Text(
                text = "Текущее значение",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Основное отображение значения счетчика
            Text(
                text = counter.value.toString(),
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 64.sp,
                    fontWeight = FontWeight.Bold
                ),
                // Изменение цвета текста при достижении лимитов
                color = when {
                    counter.isAtMax -> MaterialTheme.colorScheme.primary
                    counter.isAtMin -> MaterialTheme.colorScheme.error
                    else -> MaterialTheme.colorScheme.onSurface
                },
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Отображение диапазона допустимых значений
            Text(
                text = "Лимиты: ${counter.minValue} ... ${counter.maxValue}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // Уведомление о достижении лимита (максимума или минимума)
            if (counter.isAtMax || counter.isAtMin) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (counter.isAtMax) "Максимум достигнут!" else "Минимум достигнут!",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (counter.isAtMax) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/**
 * Компонент для отображения статистики операций со счетчиком.
 * Показывает количество различных операций и их общее число.
 *
 * @param statistics объект статистики с подсчетом операций
 * @param modifier модификатор Compose для настройки расположения и размера
 */
@Composable
fun StatisticsDisplay(
    statistics: com.example.countermvi.presentation.counter.contract.CounterStatistics,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Заголовок компонента статистики
            Text(
                text = "Статистика операций",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Сетка для отображения 4 видов статистики
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Колонка для статистики увеличений
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("➕", fontSize = 20.sp) // Иконка
                    Text(statistics.incrementCount.toString(), fontWeight = FontWeight.Bold) // Количество
                    Text("увеличений", style = MaterialTheme.typography.labelSmall) // Подпись
                }

                // Колонка для статистики уменьшений
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("➖", fontSize = 20.sp)
                    Text(statistics.decrementCount.toString(), fontWeight = FontWeight.Bold)
                    Text("уменьшений", style = MaterialTheme.typography.labelSmall)
                }

                // Колонка для статистики сбросов
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🔄", fontSize = 20.sp)
                    Text(statistics.resetCount.toString(), fontWeight = FontWeight.Bold)
                    Text("сбросов", style = MaterialTheme.typography.labelSmall)
                }

                // Колонка для общей статистики
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("∑", fontSize = 20.sp) // Символ суммы
                    Text(statistics.totalOperations.toString(), fontWeight = FontWeight.Bold)
                    Text("всего", style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}