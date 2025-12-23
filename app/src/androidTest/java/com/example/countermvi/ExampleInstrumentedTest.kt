package com.example.countermvi

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Инструментальный тест, который будет выполняться на реальном Android-устройстве или эмуляторе.
 * В отличие от модульных тестов, эти тесты имеют доступ к контексту приложения, ресурсам и компонентам Android.
 *
 * Смотри [документацию по тестированию](http://d.android.com/tools/testing).
 */
// Аннотация указывает JUnit запускать тест с помощью специального раннера для Android
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {

    // Аннотация помечает метод как тестовый случай
    @Test
    fun useAppContext() {
        // Получение контекста тестируемого приложения через InstrumentationRegistry
        // InstrumentationRegistry предоставляет доступ к тестовой среде
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext

        // Проверка утверждения: имя пакета приложения должно соответствовать ожидаемому
        // Это базовая проверка корректности настройки тестовой среды
        assertEquals("com.example.countermvi", appContext.packageName)
    }
}