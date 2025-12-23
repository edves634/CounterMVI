package com.example.countermvi.presentation.counter.reducer

import java.text.SimpleDateFormat
import java.util.*

/**
 * Интерфейс поставщика времени.
 * Абстрагирует получение текущего времени для улучшения тестируемости
 * и следования принципу Dependency Injection.
 */
interface TimeProvider {

    /**
     * Получает текущее время в формате строки.
     * @return текущее время в строковом представлении
     */
    fun getCurrentTime(): String
}

/**
 * Стандартная реализация интерфейса TimeProvider.
 * Использует системное время для получения текущего момента.
 */
class DefaultTimeProvider : TimeProvider {

    /**
     * Возвращает текущее время в формате "часы:минуты:секунды" (24-часовой формат).
     * Пример: "14:30:45"
     *
     * @return текущее время в формате HH:mm:ss
     */
    override fun getCurrentTime(): String {
        // Создание форматтера для преобразования времени в строку
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        // Получение текущего времени и форматирование
        return sdf.format(Date())
    }
}
