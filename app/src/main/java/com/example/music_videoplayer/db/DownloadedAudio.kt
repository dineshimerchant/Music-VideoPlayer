package com.example.music_videoplayer.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_audio")
data class DownloadedAudio(
    @PrimaryKey val id: Int,
    val title: String,
    val filePath: String,
    val progress: Long
)
