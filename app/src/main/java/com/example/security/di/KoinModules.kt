package com.example.security.di

import com.example.security.data.repository.AuthRepository
import com.example.security.data.repository.AuthRepositoryImpl
import com.example.security.data.repository.CryptoManager
import com.example.security.data.repository.NoteRepository
import com.example.security.data.repository.NoteRepositoryImpl
import com.example.security.data.repository.PinRepository
import com.example.security.data.repository.PinRepositoryImpl
import com.example.security.data.repository.SettingsRepository
import com.example.security.data.repository.SettingsRepositoryImpl
import com.example.security.presentation.screen.viewModel.AuthViewModel
import com.example.security.presentation.screen.viewModel.HomeViewModel
import com.example.security.presentation.screen.viewModel.SecretNoteViewModel
import com.example.security.presentation.screen.viewModel.SecurityAlertsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import org.koin.core.context.startKoin
import org.koin.core.logger.PrintLogger
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single<FirebaseAuth> {
        FirebaseAuth.getInstance()
    }

    single { FirebaseFirestore.getInstance() }
    single { CryptoManager() }

    single<AuthRepository> {
        AuthRepositoryImpl(
            auth = get(),
            firestore = get()
        )
    }

    single<PinRepository> {
        PinRepositoryImpl(
            context = get()
        )
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(
            context = get()
        )
    }

    single<NoteRepository> {
        NoteRepositoryImpl(
            firestore = get(),
            auth = get()
        )
    }


    viewModel { SecurityAlertsViewModel() }

    viewModel {
        AuthViewModel(
            repository = get(),
            pinRepository = get(),
            settingsRepository = get()
        )
    }
    viewModel {
        HomeViewModel(
            repository = get(),
            pinRepository = get(),
            settingsRepository = get()
        )
    }
    viewModel {
        SecretNoteViewModel(
            repository = get(),
            cryptoManager = get()
        )
    }


}

fun initializeKoin() {
    startKoin {
        modules(appModule)
        logger(PrintLogger())
    }
}