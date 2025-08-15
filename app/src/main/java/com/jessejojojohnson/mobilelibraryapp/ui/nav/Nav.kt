package com.jessejojojohnson.mobilelibraryapp.ui.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.jessejojojohnson.mobilelibraryapp.ui.screens.LandingScreen
import com.jessejojojohnson.mobilelibraryapp.ui.screens.LendingScreen
import com.jessejojojohnson.mobilelibraryapp.ui.screens.BookEntryScreen
import com.jessejojojohnson.mobilelibraryapp.ui.screens.HistoryScreen

@Composable
fun Navigation(
    navController: NavHostController,
    startDestination: String = Destination.Landing.route
) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable(Destination.Landing.route) {
            LandingScreen(
                navigateToBookEntry = { isbn ->
                    navController.navigate(Destination.BookEntry.route + "/$isbn")
                },
                navigateToLending = { isbn ->
                    navController.navigate(Destination.Lending.route + "/$isbn")
                },
                navigateToReturn = { isbn ->
                    navController.navigate(Destination.Return.route + "/$isbn")
                },
                navigateToHistory = {
                    navController.navigate(Destination.History.route)
                }
            )
        }
        composable(
            Destination.BookEntry.route + "/{isbn}",
            arguments = listOf(navArgument("isbn") { type = NavType.StringType })
        ) {
            it.arguments?.getString("isbn")?.let { isbn ->
                BookEntryScreen(
                    isbn = isbn,
                    bookEntrySuccessCallback = { navController.popBackStack() }
                )
            }
        }

        composable(
            Destination.Lending.route + "/{isbn}",
            arguments = listOf(navArgument("isbn") { type = NavType.StringType })
        ) {
            it.arguments?.getString("isbn")?.let { isbn ->
                LendingScreen(
                    isbn = isbn,
                    isReturning = false,
                    lendingSuccessCallback = { navController.popBackStack() }
                )
            }
        }

        composable(
            Destination.Return.route + "/{isbn}",
            arguments = listOf(navArgument("isbn") { type = NavType.StringType })
        ) {
            it.arguments?.getString("isbn")?.let { isbn ->
                LendingScreen(
                    isbn = isbn,
                    isReturning = true,
                    lendingSuccessCallback = { navController.popBackStack() }
                )
            }
        }

        composable(
            Destination.History.route,
        ) {
            HistoryScreen(
                navigateBack = { navController.popBackStack() }
            )
        }
    }
}


sealed class Destination(val route: String) {
    object Landing: Destination("landing")
    object BookEntry: Destination("bookEntry")
    object Lending: Destination("lending")
    object Return: Destination("return")
    object History: Destination("history")

}