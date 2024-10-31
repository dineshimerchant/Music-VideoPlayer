import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER
import android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Environment
import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
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

    fun downloadAudio(context: Context, url: String, title: String){
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle(title)
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_MUSIC, "$title.mp3")

        val manager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = manager.enqueue(request)

        val downloadedAudio = DownloadedAudio(id = downloadId.toInt(), title = title, filePath = "$title.mp3", progress = 0)
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            db.downloadedAudioDao().insert(downloadedAudio)
        }
    }

    fun playDownloadedAudio(context: Context, filePath: String) {
        val player = ExoPlayer.Builder(context).build()
        val mediaItem = MediaItem.fromUri(Uri.fromFile(File(filePath)))
        player.setMediaItem(mediaItem)
        player.prepare()
        player.play()
    }

    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    fun syncDownloads(context: Context) {
        if (isNetworkAvailable(context)) {
            CoroutineScope(Dispatchers.IO).launch {
                val db = AppDatabase.getDatabase(context)
                val downloadedAudios = db.downloadedAudioDao().getAll()

            }
        }
    }

}