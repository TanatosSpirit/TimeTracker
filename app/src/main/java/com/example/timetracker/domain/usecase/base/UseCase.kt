// domain/usecase/base/UseCase.kt
package com.example.timetracker.domain.usecase.base

/**
 * Базовый интерфейс для Use Case без параметров
 */
interface UseCase<out T> {
    suspend operator fun invoke(): T
}

/**
 * Базовый интерфейс для Use Case с одним параметром
 */
interface UseCaseWithParam<in P, out R> {
    suspend operator fun invoke(param: P): R
}

/**
 * Базовый интерфейс для Use Case с двумя параметрами
 */
interface UseCaseWithParams<in P1, in P2, out R> {
    suspend operator fun invoke(param1: P1, param2: P2): R
}