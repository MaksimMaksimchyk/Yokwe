package com.example.yokwe.hilt

import com.example.yokwe.data.repositories.AiRepositoryImpl
import com.example.yokwe.data.repositories.AuthRepositoryImpl
import com.example.yokwe.data.repositories.FamilyRepositoryImpl
import com.example.yokwe.data.repositories.GoalsRepositoryImpl
import com.example.yokwe.data.repositories.PetRepositoryImpl
import com.example.yokwe.domain.repositories.AiRepository
import com.example.yokwe.domain.repositories.AuthRepository
import com.example.yokwe.domain.repositories.FamilyRepository
import com.example.yokwe.domain.repositories.GoalsRepository
import com.example.yokwe.domain.repositories.PetRepository
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
    abstract fun bindFamilyRepository(familyRepositoryImpl: FamilyRepositoryImpl): FamilyRepository

    @Binds
    @Singleton
    abstract fun bindGoalRepository(goalRepositoryImpl: GoalsRepositoryImpl): GoalsRepository

    @Binds
    @Singleton
    abstract fun bindPetRepository(petRepositoryImpl: PetRepositoryImpl): PetRepository

    @Binds
    @Singleton
    abstract fun bindAiRepository(aiRepositoryImpl: AiRepositoryImpl): AiRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

}