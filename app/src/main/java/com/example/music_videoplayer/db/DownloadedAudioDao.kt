package com.example.music_videoplayer.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DownloadedAudioDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insert(audio: DownloadedAudio)

    @Query("SELECT * FROM downloaded_audio")
     fun getAll() : List<DownloadedAudio>

    @Update
     fun update(audio: DownloadedAudio)

    @Delete
     fun delete(audio: DownloadedAudio)
}