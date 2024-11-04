import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.example.music_videoplayer.VideoDownloader
import com.example.music_videoplayer.db.AppDatabase
import com.example.music_videoplayer.db.DownloadedAudio
import com.example.music_videoplayer.model.VideoData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


@OptIn(UnstableApi::class)
class VideoPlayerViewModel : ViewModel() {
    private var exoPlayer: ExoPlayer? = null
    var index: Int = 0
    var videoList: List<VideoData> = listOf()
    var downloadedAudios by mutableStateOf<List<DownloadedAudio>>(emptyList())
        private set

    fun initializePlayer(context: Context) {
        exoPlayer = ExoPlayer.Builder(context).build()
    }

    fun releasePlayer() {
        exoPlayer?.playWhenReady = false
        exoPlayer?.release()
        exoPlayer = null
    }

    fun playVideo() {
        exoPlayer?.let { player ->
            player.apply {
                stop()
                clearMediaItems()
                setMediaItem(MediaItem.fromUri(Uri.parse(videoList[index].videoUrl)))
                playWhenReady = true
                prepare()
                play()
            }
        }
    }

    fun playerViewBuilder(context: Context): PlayerView {
        val activity = context as Activity
        val playerView = PlayerView(context).apply {
            player = exoPlayer
            controllerAutoShow = true
            keepScreenOn = true
            setFullscreenButtonClickListener { isFullScreen ->
                if (isFullScreen){
                    activity.requestedOrientation = SCREEN_ORIENTATION_USER_LANDSCAPE
                }else{
                    activity.requestedOrientation = SCREEN_ORIENTATION_USER
                }
            }
        }
        return playerView
    }



    fun downloadVideo(context: Context, url: String, title: String) {
        val downloader = VideoDownloader(context, title)
        downloader.downloadFile(url)
    }

    fun playDownloadedVideo(filePath: String,context: Context) {
        if (exoPlayer == null) {
            Log.e("VideoPlayer", "ExoPlayer is not initialized. Initializing now.")
            initializePlayer(context) // You might need to pass the context here or manage it differently
        }

        println("File Path $filePath")
        exoPlayer?.let { player ->
            player.apply {
                stop()
                clearMediaItems()
                val mediaItem = MediaItem.fromUri(Uri.fromFile(File(filePath)))
                setMediaItem(mediaItem)
                playWhenReady = true
                prepare()


                addListener(object : Player.Listener {
                    override fun onPlayerError(error: PlaybackException) {
                        Log.e("VideoPlayer", "Playback error: ${error.message}")
                    }
                })
                play()
            }
        }
    }



    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    fun syncDownloads(context: Context) {
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            downloadedAudios = db.downloadedAudioDao().getAll()
            downloadedAudios.forEach { audio ->
                Log.d("DownloadedAudio", "ID: ${audio.id}, Title: ${audio.title}, Path: ${audio.filePath}, Progress: ${audio.progress}")
            }
        }
    }

}