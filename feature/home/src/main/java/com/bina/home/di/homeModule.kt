package com.bina.home.di

import androidx.room.Room
import com.bina.home.data.local.database.AppDatabase
import com.bina.home.data.local.datasource.UsersLocalDataSource
import com.bina.home.data.local.datasource.UsersLocalDataSourceImpl
import com.bina.home.data.remote.datasource.UsersRemoteDataSource
import com.bina.home.data.remote.datasource.UsersRemoteDataSourceImpl
import com.bina.home.data.repository.ErrorMapper
import com.bina.home.data.repository.UsersRepositoryImpl
import com.bina.home.domain.repository.UsersRepository
import com.bina.home.domain.usecase.ObserveUsersUseCase
import com.bina.home.domain.usecase.RefreshUsersUseCase
import com.bina.home.presentation.viewmodel.HomeViewModel
import com.bina.home.utils.RetrofitService
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val homeModule = module {

    single { RetrofitService.service }

    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "app_db"
        )
            .build()
    }
    single { get<AppDatabase>().userDao() }

    single { ErrorMapper() }

    single<UsersRemoteDataSource> { UsersRemoteDataSourceImpl(get()) }
    single<UsersLocalDataSource> { UsersLocalDataSourceImpl(get()) }

    single<UsersRepository> {
        UsersRepositoryImpl(
            get(),
            get(),
            get()
        )
    }

    factory { ObserveUsersUseCase(get()) }
    factory { RefreshUsersUseCase(get()) }

    viewModel { HomeViewModel(get(), get()) }
}
