package com.example.music_videoplayer

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Environment
import com.example.music_videoplayer.db.AppDatabase
import com.example.music_videoplayer.db.DownloadedAudio
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DownloadCompletedReceiver: BroadcastReceiver() {
    override fun onReceive(context:  Context?, intent: Intent?) {
        if(intent?.action == "android.intent.action.DOWNLOAD_COMPLETE") {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            if(id != -1L) {
                println("Download with ID $id finished!")
                val downloadPath = "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)}/video.mp4"
                CoroutineScope(Dispatchers.IO).launch {
                    val db = AppDatabase.getDatabase(context!!)
                    val downloadedVideo = DownloadedAudio(
                        id = id.toInt(),
                        title = "video.mp4",
                        filePath = downloadPath,
                        progress = 0,
                        thumbnail = "https://i0.wp.com/picjumbo.com/wp-content/uploads/autumn-wallpaper-free-image.jpg?w=600&quality=80"
                    )
                    db.downloadedAudioDao().insert(downloadedVideo)
                }
            }
        }
    }
}