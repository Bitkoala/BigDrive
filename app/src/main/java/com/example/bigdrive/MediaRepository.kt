package com.example.bigdrive

import android.graphics.Bitmap
import android.media.session.PlaybackState
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object MediaRepository {
    private val _albumArt = MutableLiveData<Bitmap?>()
    val albumArt: LiveData<Bitmap?> get() = _albumArt

    private val _playbackState = MutableLiveData<PlaybackState?>()
    val playbackState: LiveData<PlaybackState?> get() = _playbackState

    private val _title = MutableLiveData<String?>()
    val title: LiveData<String?> get() = _title

    private val _artist = MutableLiveData<String?>()
    val artist: LiveData<String?> get() = _artist

    private val _duration = MutableLiveData<Long>()
    val duration: LiveData<Long> get() = _duration

    var transportControls: android.media.session.MediaController.TransportControls? = null

    fun updateAlbumArt(bitmap: Bitmap?) {
        _albumArt.postValue(bitmap)
    }

    fun updatePlaybackState(state: PlaybackState?) {
        _playbackState.postValue(state)
    }

    fun updateMetadata(title: String?, artist: String?, duration: Long) {
        _title.postValue(title)
        _artist.postValue(artist)
        _duration.postValue(duration)
    }
}
