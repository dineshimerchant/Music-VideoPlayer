package com.example.music_videoplayer

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import androidx.core.net.toUri

class VideoDownloader(
    private val context: Context,
    val title: String
) : Downloader {

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    override fun downloadFile(url: String): Long {
        val request = DownloadManager.Request(url.toUri())
            .setMimeType("video/mp4")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("video.mp4")
            .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "video.mp4")
        return downloadManager.enqueue(request)
    }
}