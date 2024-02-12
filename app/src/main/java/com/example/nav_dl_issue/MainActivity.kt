package com.example.nav_dl_issue

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.nav_dl_issue.ui.theme.ComposeNavigationDeeplinksIssueTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeNavigationDeeplinksIssueTheme {
                val navController = rememberNavController()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = MaterialTheme.colorScheme.background,
                    content = {
                        Box(modifier = Modifier.padding(it)) {
                            NavHost(
                                navController = navController,
                                startDestination = "home",
                            ) {

                                // Home screen and start destination
                                composable(
                                    route = "home",
                                ) {
                                    HomeScreen(navController)
                                }

                                // Feature 1
                                val publicScheme = BuildConfig.DEEP_LINK_SCHEME
                                val publicHost = BuildConfig.FEATURE_1_DEEP_LINK_HOST
                                composable(
                                    route = "feature1",
                                    deepLinks = listOf(
                                        // This is a feature that can be opened through a public deeplink
                                        // matching the schema. We expose it through the app's manifest.
                                        navDeepLink {
                                            uriPattern = "$publicScheme://$publicHost/parameter={parameter}"
                                            action = Intent.ACTION_VIEW
                                        }
                                    ),
                                ) { navBackStackEntry: NavBackStackEntry ->
                                    val parameter = navBackStackEntry.arguments?.getString("parameter") ?: "null"
                                    Feature1Screen(navController, parameter)
                                }

                                // Feature 2
                                composable(
                                    route = "feature2",
                                    deepLinks = listOf(
                                        // This is a feature we only open from our own notifications.
                                        // We don't have it in the manifest since we don't want to expose it publicly.
                                        // This deeplink is opened from a notification's pending intent
                                        // through an explicit Intent targeted at the MainActivity and matching
                                        // the schema.

                                        // So here is the issue:
                                        // When the uriPattern here looks like that, it steals any other deeplink I open
                                        // from outside. For example, executing the following command in the terminal:
                                        // `adb shell am start com.example.nav_dl_issue://feature1/parameter=hello`
                                        // Will NOT open the feature1 composable as expected, but the feature2
                                        // composable instead. This is completely unexpected, considering the schema
                                        // defined here is completely different: schema, host, and path.
                                        // However, if you change this uriPattern to something like
                                        // `some.private.schema://feature2/{whateverNavPlaceholder}`, then everything
                                        // works as expected
                                        navDeepLink {
                                            uriPattern = "some.private.schema://feature2"
                                            // uriPattern = "some.private.schema://feature2/{whateverNavPlaceholder}"
                                            action = Intent.ACTION_VIEW
                                        }
                                    ),
                                ) {
                                    Feature2Screen(navController)
                                }
                            }
                        }
                    }
                )
            }
        }
    }

    @Composable
    private fun HomeScreen(navController: NavHostController) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "Home")
            Button(
                onClick = {
                    navController.navigate("feature1")
                }
            ) {
                Text(text = "Open feature1")
            }
            Button(
                onClick = {
                    navController.navigate("feature2")
                }
            ) {
                Text(text = "Open feature2")
            }
        }
    }

    @Composable
    private fun Feature1Screen(navController: NavHostController, parameter: String) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    navController.navigateUp()
                }
            ) {
                Text(text = "Up")
            }
            Text(text = "Feature 1\nParameter: $parameter")
        }
    }

    @Composable
    private fun Feature2Screen(navController: NavHostController) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    navController.navigateUp()
                }
            ) {
                Text(text = "Up")
            }
            Text(text = "Feature 2")
        }
    }
}
