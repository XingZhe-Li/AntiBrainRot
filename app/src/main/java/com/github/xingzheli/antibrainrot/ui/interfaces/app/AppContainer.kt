package com.github.xingzheli.antibrainrot.ui.interfaces.app

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.github.xingzheli.antibrainrot.data.datastore.PreferenceProxy.getUIUseDrawer
import com.github.xingzheli.antibrainrot.ui.interfaces.RegistryEntry
import com.github.xingzheli.antibrainrot.ui.interfaces.context.LocalNavHostController
import com.github.xingzheli.antibrainrot.ui.interfaces.context.LocalSnackBarHostState
import com.github.xingzheli.antibrainrot.ui.interfaces.startRegistry
import com.github.xingzheli.antibrainrot.ui.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AppContainer() {
    val snackbarHostState = remember { SnackbarHostState() }

    CompositionLocalProvider (
        LocalSnackBarHostState provides snackbarHostState
    ) {
        AppTheme {
            Scaffold (
                Modifier.fillMaxSize(),
                snackbarHost = {
                    SnackbarHost(snackbarHostState)
                }
            ) { innerPadding ->
                Box (
                    Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AppNavigator()
                }
            }
        }
    }
}

@Composable
private fun AppNavigator() {
    val navController = rememberNavController()
    var uiUseDrawer by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            uiUseDrawer = getUIUseDrawer()
        }
    }

    CompositionLocalProvider(
        LocalNavHostController provides navController
    ) {
        if (uiUseDrawer) {
            AppDrawer {
                AppNavHost()
            }
        } else {
            AppNavHost()
        }
    }
}

@Composable
private fun AppDrawer(
    content : @Composable ()-> Unit = {}
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()
    val navHostController = LocalNavHostController.current

    val currentBackStack by navHostController.currentBackStackEntryAsState()
    val currentRoute = currentBackStack?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet (
                modifier =
                    Modifier
                        .fillMaxWidth(0.9f)
                        .fillMaxHeight(),
                drawerContainerColor = MaterialTheme.colorScheme.surface
            ) {
                Column (
                    Modifier.padding(20.dp),
                    Arrangement.spacedBy(4.dp)
                ) {
                    Text (
                        text  = "AntiBrainRot",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    HorizontalDivider(
                        Modifier.padding(0.dp,8.dp)
                    )
                    for (registryEntry in DrawerEntry.entries) {
                        NavigationDrawerItem(
                            icon = {
                                Icon(
                                    imageVector = registryEntry.tabIcon,
                                    contentDescription = registryEntry.tabName
                                )
                            },
                            label = { Text( registryEntry.tabName) },
                            selected = currentRoute == registryEntry.route,
                            onClick = {
                                if (currentRoute == registryEntry.route) {
                                    coroutineScope.launch {
                                        drawerState.close()
                                    }
                                } else {
                                    coroutineScope.launch {
                                        navHostController.navigate(
                                            registryEntry.route
                                        )
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    ) {
        content()
    }
}

@Composable
private fun AppNavHost() {
    val navController = LocalNavHostController.current

    NavHost(
        navController = navController,
        startDestination = startRegistry.route,
        modifier = Modifier.fillMaxSize(),
        enterTransition = { slideInHorizontally(initialOffsetX = { it }) },
        exitTransition = { slideOutHorizontally(targetOffsetX = { -it }) },
        popEnterTransition = { slideInHorizontally(initialOffsetX = { -it }) },
        popExitTransition = { slideOutHorizontally(targetOffsetX = { it }) }
    ) {
        for ( registryEntry in RegistryEntry.entries ) {
            composable (
                route = registryEntry.route
            ) {
                registryEntry.content()
            }
        }
    }
}