package com.example.music_videoplayer

import VideoPlayerViewModel
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel

import android.os.Bundle
import android.os.Environment
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
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
import com.example.music_videoplayer.model.mainVideoList
import com.example.music_videoplayer.ui.theme.MusicVideoPlayerTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MusicVideoPlayerTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    StreamingVideo()
                }
            }
        }
    }
}

@Composable
fun StreamingVideo() {
    var isPlaying by remember {
        mutableStateOf(false)
    }
    var videoItemIndex by remember {
        mutableIntStateOf(0)
    }
    val viewModel: VideoPlayerViewModel = viewModel()
    viewModel.videoList = mainVideoList
    val context = LocalContext.current

    val currentVideo = mainVideoList[videoItemIndex]
    Column {
        StreamerPlayer(
            viewModel = viewModel,
            isPlaying = isPlaying,
            onPlayerClosed = { isVideoPlaying ->
                isPlaying = isVideoPlaying
            })


        Button(onClick = {
            viewModel.downloadAudio(context, currentVideo.videoUrl, currentVideo.videoUrl)
        }) {
            Text("Download Audio")
        }

        Button(onClick = {
            viewModel.playDownloadedAudio(context, Environment.DIRECTORY_MUSIC)
        }) {
            Text("Play Downloaded Audio")
        }
        LaunchedEffect(key1 = videoItemIndex){
            isPlaying = true
            viewModel.apply {
                releasePlayer()
                initializePlayer(context)
                playVideo()
            }
        }
    }
}