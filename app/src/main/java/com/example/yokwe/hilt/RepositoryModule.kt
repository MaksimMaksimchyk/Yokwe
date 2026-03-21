package com.example.yokwe.hilt

import com.example.yokwe.data.repositories.AuthRepositoryImpl
import com.example.yokwe.data.repositories.GoalRepositoryImpl
import com.example.yokwe.domain.repositories.AuthRepository
import com.example.yokwe.domain.repositories.GoalRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(goalRepositoryImpl: GoalRepositoryImpl): GoalRepository
}