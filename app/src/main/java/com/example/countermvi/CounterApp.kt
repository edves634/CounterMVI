package com.example.countermvi

import android.app.Application
import com.example.countermvi.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

/**
 * Основной класс приложения, наследующий от Application.
 * Используется для глобальной инициализации приложения,
 * в частности для настройки dependency injection через Koin.
 */
class CounterApp : Application() {

    /**
     * Метод, вызываемый при создании приложения.
     * Здесь происходит инициализация Koin для управления зависимостями.
     */
    override fun onCreate() {
        super.onCreate()

        // Запуск Koin - библиотеки для dependency injection
        startKoin {
            // Предоставляем контекст приложения для Koin
            androidContext(this@CounterApp)
            // Регистрируем модули зависимостей, определенные в appModule
            modules(appModule)
        }
    }
}