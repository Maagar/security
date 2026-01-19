package com.example.security.di

import com.example.security.data.repository.AuthRepository
import com.example.security.data.repository.AuthRepositoryImpl
import com.example.security.presentation.screen.viewModel.AuthViewModel
import com.example.security.presentation.screen.viewModel.HomeViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.core.context.startKoin
import org.koin.core.logger.PrintLogger
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<FirebaseAuth> {
        FirebaseAuth.getInstance()
    }

    single<AuthRepository> {
        AuthRepositoryImpl(
            auth = get()
        )
    }

    viewModel { AuthViewModel(repository = get()) }
    viewModel { HomeViewModel(repository = get()) }


}

fun initializeKoin() {
    startKoin {
        modules(appModule)
        logger(PrintLogger())
    }
}