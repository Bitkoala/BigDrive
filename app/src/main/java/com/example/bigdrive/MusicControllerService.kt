package com.example.bigdrive

import android.content.ComponentName
import android.content.Context
import android.media.MediaMetadata
import android.media.session.MediaController
import android.media.session.MediaSessionManager
import android.media.session.PlaybackState
import android.service.notification.NotificationListenerService

class MusicControllerService : NotificationListenerService() {

    private lateinit var mediaSessionManager: MediaSessionManager
    private var activeController: MediaController? = null

    private val activeSessionsChangedListener =
        MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
            updateActiveController(controllers)
        }

    private val mediaControllerCallback = object : MediaController.Callback() {
        override fun onMetadataChanged(metadata: MediaMetadata?) {
            super.onMetadataChanged(metadata)
            val bitmap = metadata?.getBitmap(MediaMetadata.METADATA_KEY_ALBUM_ART)
                ?: metadata?.getBitmap(MediaMetadata.METADATA_KEY_ART)
            MediaRepository.updateAlbumArt(bitmap)
            
            val title = metadata?.getString(MediaMetadata.METADATA_KEY_TITLE)
                ?: metadata?.getString(MediaMetadata.METADATA_KEY_DISPLAY_TITLE)
                ?: "未知歌曲"
            val artist = metadata?.getString(MediaMetadata.METADATA_KEY_ARTIST)
                ?: metadata?.getString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST)
                ?: "未知歌手"
            val duration = metadata?.getLong(MediaMetadata.METADATA_KEY_DURATION) ?: 0L
            
            MediaRepository.updateMetadata(title, artist, duration)
        }

        override fun onPlaybackStateChanged(state: PlaybackState?) {
            super.onPlaybackStateChanged(state)
            MediaRepository.updatePlaybackState(state)
        }
    }

    override fun onCreate() {
        super.onCreate()
        mediaSessionManager = getSystemService(Context.MEDIA_SESSION_SERVICE) as MediaSessionManager
    }

    override fun onListenerConnected() {
        super.onListenerConnected()
        try {
            val componentName = ComponentName(this, MusicControllerService::class.java)
            mediaSessionManager.addOnActiveSessionsChangedListener(
                activeSessionsChangedListener,
                componentName
            )
            val controllers = mediaSessionManager.getActiveSessions(componentName)
            updateActiveController(controllers)
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    override fun onListenerDisconnected() {
        super.onListenerDisconnected()
        activeController?.unregisterCallback(mediaControllerCallback)
        try {
            mediaSessionManager.removeOnActiveSessionsChangedListener(activeSessionsChangedListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateActiveController(controllers: List<MediaController>?) {
        activeController?.unregisterCallback(mediaControllerCallback)
        
        // Find first playing controller or just first one
        activeController = controllers?.firstOrNull { 
            it.playbackState?.state == PlaybackState.STATE_PLAYING 
        } ?: controllers?.firstOrNull()

        activeController?.let {
            it.registerCallback(mediaControllerCallback)
            mediaControllerCallback.onMetadataChanged(it.metadata)
            mediaControllerCallback.onPlaybackStateChanged(it.playbackState)
            MediaRepository.transportControls = it.transportControls
        } ?: run {
            MediaRepository.updateAlbumArt(null)
            MediaRepository.updatePlaybackState(null)
            MediaRepository.transportControls = null
        }
    }
}
