package com.example.music_videoplayer

interface Downloader {
    fun downloadFile(url: String): Long
}