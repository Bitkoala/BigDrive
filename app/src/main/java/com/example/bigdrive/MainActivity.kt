package com.example.bigdrive

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.bigdrive.databinding.ActivityMainBinding
import jp.wasabeef.glide.transformations.BlurTransformation

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Keep screen on
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
        observeMediaRepository()
    }

    override fun onResume() {
        super.onResume()
        checkNotificationPermission()
    }

    private fun checkNotificationPermission() {
        val componentName = ComponentName(this, MusicControllerService::class.java)
        val enabledListeners = NotificationManagerCompat.getEnabledListenerPackages(this)
        val isGranted = enabledListeners.contains(packageName)

        if (!isGranted) {
            binding.layoutPermission.visibility = View.VISIBLE
            binding.btnGrantPermission.setOnClickListener {
                startActivity(Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS))
            }
        } else {
            binding.layoutPermission.visibility = View.GONE
        }
    }

    private fun setupListeners() {
        binding.btnPrevious.setOnClickListener {
            vibrate()
            MediaRepository.transportControls?.skipToPrevious()
        }

        binding.btnNext.setOnClickListener {
            vibrate()
            MediaRepository.transportControls?.skipToNext()
        }

        binding.btnPlayPause.setOnClickListener {
            vibrate()
            val state = MediaRepository.playbackState.value?.state
            if (state == android.media.session.PlaybackState.STATE_PLAYING) {
                MediaRepository.transportControls?.pause()
            } else {
                MediaRepository.transportControls?.play()
            }
        }
    }

    private fun observeMediaRepository() {
        MediaRepository.albumArt.observe(this) { bitmap ->
            if (bitmap != null) {
                // Background blur
                Glide.with(this)
                    .load(bitmap)
                    .apply(RequestOptions.bitmapTransform(BlurTransformation(25, 3)))
                    .into(binding.ivBackgroundBlur)

                // Center Album Art with rounded corners
                Glide.with(this)
                    .load(bitmap)
                    .transform(CenterCrop(), RoundedCorners(32))
                    .into(binding.ivAlbumArt)
            } else {
                binding.ivBackgroundBlur.setImageDrawable(null)
                binding.ivAlbumArt.setImageDrawable(null)
            }
        }
    }

    private fun vibrate() {
        val duration = 50L
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            val vibrator = vibratorManager.defaultVibrator
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            vibrator.vibrate(VibrationEffect.createOneShot(duration, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }
}
