/*
 * Copyright (c) 2026 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 2/1/2026
 */

package aragones.sergio.readercollection.presentation.di

import aragones.sergio.readercollection.domain.di.domainModule
import aragones.sergio.readercollection.presentation.MainViewModel
import aragones.sergio.readercollection.presentation.account.AccountViewModel
import aragones.sergio.readercollection.presentation.bookdetail.BookDetailViewModel
import aragones.sergio.readercollection.presentation.booklist.BookListViewModel
import aragones.sergio.readercollection.presentation.books.BooksViewModel
import aragones.sergio.readercollection.presentation.friends.FriendsViewModel
import aragones.sergio.readercollection.presentation.landing.LandingViewModel
import aragones.sergio.readercollection.presentation.login.LoginViewModel
import aragones.sergio.readercollection.presentation.register.RegisterViewModel
import aragones.sergio.readercollection.presentation.search.SearchViewModel
import aragones.sergio.readercollection.presentation.settings.SettingsViewModel
import aragones.sergio.readercollection.presentation.statistics.StatisticsViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val presentationModule = module {
    includes(domainModule)
    includes(platformModule)
    viewModelOf(::MainViewModel)
    viewModelOf(::AccountViewModel)
    viewModelOf(::BookDetailViewModel)
    viewModelOf(::BookListViewModel)
    viewModelOf(::BooksViewModel)
    viewModelOf(::FriendsViewModel)
    viewModelOf(::LandingViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::SearchViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::StatisticsViewModel)
}
