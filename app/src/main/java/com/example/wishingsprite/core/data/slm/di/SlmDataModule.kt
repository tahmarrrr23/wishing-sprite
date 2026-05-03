package com.example.wishingsprite.core.data.slm.di

import com.example.wishingsprite.core.data.slm.repository.DefaultSlmRepository
import com.example.wishingsprite.core.data.slm.repository.SlmRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface SlmDataModule {
  @Binds @Singleton fun bindSlmRepository(repository: DefaultSlmRepository): SlmRepository
}
