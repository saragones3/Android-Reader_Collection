/*
 * Copyright (c) 2025 Sergio Aragonés. All rights reserved.
 * Created by Sergio Aragonés on 11/1/2025
 */

package aragones.sergio.readercollection.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import aragones.sergio.readercollection.presentation.account.AccountView
import aragones.sergio.readercollection.presentation.bookdetail.BookDetailView
import aragones.sergio.readercollection.presentation.booklist.BookListView
import aragones.sergio.readercollection.presentation.books.BooksView
import aragones.sergio.readercollection.presentation.friends.FriendsView
import aragones.sergio.readercollection.presentation.login.LoginView
import aragones.sergio.readercollection.presentation.register.RegisterView
import aragones.sergio.readercollection.presentation.search.SearchView
import aragones.sergio.readercollection.presentation.settings.SettingsOption
import aragones.sergio.readercollection.presentation.settings.SettingsView
import aragones.sergio.readercollection.presentation.statistics.StatisticsView
import com.aragones.sergio.util.BookState

fun NavGraphBuilder.authGraph(navController: NavHostController, navigator: Navigator) {
    navigation<Route.Auth>(startDestination = Route.Login) {
        composable<Route.Login>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            LoginView(
                onGoToMain = {
                    navigator.goToMain()
                },
                onGoToRegister = {
                    navController.navigate(Route.Register)
                },
            )
        }
        composable<Route.Register>(
            enterTransition = { slideIntoContainer() },
            exitTransition = { slideOutOfContainer() },
        ) {
            RegisterView(
                onGoToMain = {
                    navigator.goToMain()
                },
            )
        }
    }
}

fun NavGraphBuilder.booksGraph(navController: NavHostController) {
    navigation<Route.Books>(startDestination = Route.BooksHome) {
        composable<Route.BooksHome>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            BooksView(
                onBookClick = { bookId ->
                    navController.navigate(
                        Route.BookDetail(bookId),
                    )
                },
                onShowAll = { bookState, sortParam, isSortDescending, query ->
                    navController.navigate(
                        Route.BookList(
                            state = bookState,
                            sortParam = sortParam,
                            isSortDescending = isSortDescending,
                            query = query,
                            year = -1,
                            month = -1,
                            author = null,
                            format = null,
                            genre = null,
                        ),
                    )
                },
                onAddBook = {
                    navController.navigate(Route.Search)
                },
            )
        }
        composable<Route.Search>(
            enterTransition = { slideIntoContainer() },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { slideOutOfContainer() },
        ) {
            SearchView(
                onBookClick = { bookId ->
                    navController.navigate(
                        Route.BookDetail(bookId),
                    )
                },
                onBack = {
                    navController.navigateUp()
                },
            )
        }
        composable<Route.BookList>(
            enterTransition = { slideIntoContainer() },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { slideOutOfContainer() },
        ) {
            BookListView(
                onBookClick = { bookId, friendId ->
                    navController.navigate(
                        Route.BookDetail(bookId, friendId),
                    )
                },
                onBack = {
                    navController.navigateUp()
                },
            )
        }
        composable<Route.BookDetail>(
            enterTransition = { slideIntoContainer() },
            exitTransition = { slideOutOfContainer() },
        ) {
            BookDetailView(
                onBack = {
                    navController.navigateUp()
                },
            )
        }
    }
}

fun NavGraphBuilder.statisticsGraph(navController: NavHostController) {
    navigation<Route.Statistics>(startDestination = Route.StatisticsHome) {
        composable<Route.StatisticsHome>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            StatisticsView(
                onBookClick = { bookId ->
                    navController.navigate(
                        Route.BookDetail(bookId),
                    )
                },
                onShowAll = { sortParam, isSortDescending, year, month, author, format, genre ->
                    navController.navigate(
                        Route.BookList(
                            state = BookState.READ,
                            sortParam = sortParam,
                            isSortDescending = isSortDescending,
                            query = "",
                            year = year,
                            month = month,
                            author = author,
                            format = format,
                            genre = genre,
                        ),
                    )
                },
            )
        }
        composable<Route.BookList>(
            enterTransition = { slideIntoContainer() },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { slideOutOfContainer() },
        ) {
            BookListView(
                onBookClick = { bookId, friendId ->
                    navController.navigate(
                        Route.BookDetail(bookId, friendId),
                    )
                },
                onBack = {
                    navController.navigateUp()
                },
            )
        }
        composable<Route.BookDetail>(
            enterTransition = { slideIntoContainer() },
            exitTransition = { slideOutOfContainer() },
        ) {
            BookDetailView(
                onBack = {
                    navController.navigateUp()
                },
            )
        }
    }
}

fun NavGraphBuilder.friendsGraph(navController: NavHostController) {
    navigation<Route.Friends>(startDestination = Route.FriendsHome) {
        composable<Route.FriendsHome>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            FriendsView(
                onBookClick = { bookId, friendId ->
                    navController.navigate(
                        Route.BookDetail(bookId, friendId),
                    )
                },
                onShowAll = { state, sortParam, isSortDescending, friendId ->
                    navController.navigate(
                        Route.BookList(
                            state = state,
                            sortParam = sortParam,
                            isSortDescending = isSortDescending,
                            query = "",
                            friendId = friendId,
                        ),
                    )
                },
            )
        }
        composable<Route.BookList>(
            enterTransition = { slideIntoContainer() },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { slideOutOfContainer() },
        ) {
            BookListView(
                onBookClick = { bookId, friendId ->
                    navController.navigate(
                        Route.BookDetail(bookId, friendId),
                    )
                },
                onBack = {
                    navController.navigateUp()
                },
            )
        }
        composable<Route.BookDetail>(
            enterTransition = { slideIntoContainer() },
            exitTransition = { slideOutOfContainer() },
        ) {
            BookDetailView(
                onBack = {
                    navController.navigateUp()
                },
            )
        }
    }
}

fun NavGraphBuilder.settingsGraph(navController: NavHostController, navigator: Navigator) {
    navigation<Route.Settings>(startDestination = Route.SettingsHome) {
        composable<Route.SettingsHome>(
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
        ) {
            SettingsView(
                onClickOption = {
                    when (it) {
                        is SettingsOption.Account -> navController.navigate(Route.Account)
                        is SettingsOption.Logout -> navigator.goToLanding()
                    }
                },
                onRelaunch = {
                    navigator.goToMain(withOptions = false)
                },
            )
        }
        composable<Route.Account>(
            enterTransition = { slideIntoContainer() },
            exitTransition = { slideOutOfContainer() },
        ) {
            AccountView(
                onBack = {
                    navController.navigateUp()
                },
                onLogout = {
                    navigator.goToLanding()
                },
            )
        }
    }
}