package com.example.countermvi.di

import com.example.countermvi.presentation.counter.reducer.CounterReducer
import com.example.countermvi.presentation.counter.reducer.DefaultTimeProvider
import com.example.countermvi.presentation.counter.reducer.TimeProvider
import com.example.countermvi.presentation.counter.viewmodel.CounterMviViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

// Объявление Koin-модуля приложения
// Модуль содержит объявления всех зависимостей, которые будут внедряться в приложении
val appModule = module {

    // Объявление синглтона (одиночного экземпляра) TimeProvider
    // single - создает единственный экземпляр на все время жизни приложения
    // <TimeProvider> - указывает тип интерфейса/абстракции
    // { DefaultTimeProvider() } - лямбда-выражение, создающее конкретную реализацию
    single<TimeProvider> { DefaultTimeProvider() }

    // Объявление синглтона CounterReducer
    // get() - функция Koin для автоматического получения зависимости TimeProvider
    // Позволяет автоматически внедрить TimeProvider в конструктор CounterReducer
    single { CounterReducer(get()) }

    // Объявление ViewModel с использованием специальной функции Koin для Android
    // viewModel - создает экземпляр ViewModel с учетом жизненного цикла Android
    // ViewModel будет пересоздаваться для разных экранов/фрагментов
    // get() - автоматически внедряет CounterReducer в ViewModel
    viewModel { CounterMviViewModel(get()) }
}