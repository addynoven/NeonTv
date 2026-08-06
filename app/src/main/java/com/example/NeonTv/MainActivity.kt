package com.example.NeonTv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.NeonTv.ui.IptvViewModel
import com.example.NeonTv.ui.screens.ChannelListScreen
import com.example.NeonTv.ui.screens.VideoPlayerScreen
import com.example.NeonTv.ui.theme.NeonTvTheme
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NeonTvTheme {
                val navController = rememberNavController()
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Box(modifier = Modifier.padding(innerPadding)) {
                        NavHost(
                            navController = navController,
                            startDestination = "channelList"
                        ) {
                            composable("channelList") {
                                val viewModel: IptvViewModel = hiltViewModel()
                                ChannelListScreen(
                                    viewModel = viewModel,
                                    onChannelClick = { channel ->
                                        val encodedUrl = URLEncoder.encode(
                                            channel.streamUrl,
                                            StandardCharsets.UTF_8.toString()
                                        )
                                        navController.navigate("player/$encodedUrl")
                                    }
                                )
                            }
                            composable(
                                "player/{streamUrl}",
                                arguments = listOf(navArgument("streamUrl") {
                                    type = NavType.StringType
                                })
                            ) { backStackEntry ->
                                val streamUrl =
                                    backStackEntry.arguments?.getString("streamUrl") ?: ""
                                VideoPlayerScreen(
                                    streamUrl = streamUrl,
                                    onBack = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
