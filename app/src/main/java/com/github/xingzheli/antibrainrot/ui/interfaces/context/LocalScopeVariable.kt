package com.github.xingzheli.antibrainrot.ui.interfaces.context

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavHostController

val LocalNavHostController = compositionLocalOf<NavHostController> {
    error("NavController not provided")
}
val LocalSnackBarHostState = compositionLocalOf<SnackbarHostState> {
    error("SnackbarHostState not provided")
}
