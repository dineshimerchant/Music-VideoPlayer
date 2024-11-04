package com.example.music_videoplayer

import VideoPlayerViewModel
import android.content.pm.PackageManager
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import android.os.Bundle
import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.music_videoplayer.model.mainVideoList
import com.example.music_videoplayer.ui.theme.MusicVideoPlayerTheme


class MainActivity : ComponentActivity() {

    private lateinit var notificationPermissionLauncher: ActivityResultLauncher<Array<String>>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         notificationPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val notificationGranted = permissions[Manifest.permission.POST_NOTIFICATIONS] == true
            val writeGranted = permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true
        }

        requestPermissions()

        enableEdgeToEdge()
        setContent {
            MusicVideoPlayerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    StreamingVideo()
                }
            }
        }
    }





    private fun requestPermissions() {
        when {
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED -> {
            }
            else -> {
                notificationPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }

}

@Composable
fun StreamingVideo() {
    var isPlaying by remember { mutableStateOf(false) }
    var videoItemIndex by remember { mutableIntStateOf(0) }
    val viewModel: VideoPlayerViewModel = viewModel()
    viewModel.videoList = mainVideoList
    val context = LocalContext.current

    val currentVideo = mainVideoList[videoItemIndex]

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        StreamerPlayer(
            viewModel = viewModel,
            isPlaying = isPlaying,
            onPlayerClosed = { isVideoPlaying -> isPlaying = isVideoPlaying }
        )


        Button(
            onClick = {
                viewModel.downloadVideo(context, currentVideo.videoUrl, currentVideo.videoUrl)
                viewModel.syncDownloads(context)
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Download")
        }


        LazyColumn(
            modifier = Modifier.weight(1f)
        ) {
            items(viewModel.downloadedAudios) { audio ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                ) {
                    Column {
                        AsyncImage(
                            model = "https://i0.wp.com/picjumbo.com/wp-content/uploads/autumn-wallpaper-free-image.jpg?w=600&quality=80",
                            contentDescription = "Translated description of what the image contains"
                        )
                        Text(
                            text = audio.title,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                }
            }
        }

        // Add a spacer at the bottom
        Spacer(modifier = Modifier.height(16.dp))
    }

    LaunchedEffect(key1 = videoItemIndex) {
        isPlaying = true
        viewModel.apply {
            releasePlayer()
            initializePlayer(context)
            playVideo()
            syncDownloads(context)
        }
    }
}
