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

    var transportControls: android.media.session.MediaController.TransportControls? = null

    fun updateAlbumArt(bitmap: Bitmap?) {
        _albumArt.postValue(bitmap)
    }

    fun updatePlaybackState(state: PlaybackState?) {
        _playbackState.postValue(state)
    }
}
