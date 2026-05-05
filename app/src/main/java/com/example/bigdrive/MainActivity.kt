package com.example.bigdrive

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.provider.Settings
import android.view.View
import android.view.WindowManager
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.bigdrive.databinding.ActivityMainBinding
import jp.wasabeef.glide.transformations.BlurTransformation
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val handler = Handler(Looper.getMainLooper())
    private var isDraggingSeekBar = false

    // Timer to update progress bar smoothly
    private val progressUpdater = object : Runnable {
        override fun run() {
            updateProgress()
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Edge-to-Edge support for immersive background
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Apply system window insets to the UI container to prevent overlap with status/nav bars
        val uiContainer = findViewById<View>(R.id.uiContainer)
        if (uiContainer != null) {
            ViewCompat.setOnApplyWindowInsetsListener(uiContainer) { view, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }
        }

        setupListeners()
        observeMediaRepository()
    }

    override fun onResume() {
        super.onResume()
        checkNotificationPermission()
        handler.post(progressUpdater)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(progressUpdater)
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

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    binding.tvCurrentTime.text = formatTime(progress.toLong())
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isDraggingSeekBar = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isDraggingSeekBar = false
                seekBar?.let {
                    MediaRepository.transportControls?.seekTo(it.progress.toLong())
                }
            }
        })
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

        MediaRepository.title.observe(this) { title ->
            binding.tvTitle.text = title ?: "未知歌曲"
            binding.tvTitle.isSelected = true // Enable marquee
        }

        MediaRepository.artist.observe(this) { artist ->
            binding.tvArtist.text = artist ?: "未知歌手"
        }

        MediaRepository.duration.observe(this) { duration ->
            binding.seekBar.max = duration.toInt()
            binding.tvDuration.text = formatTime(duration)
        }

        MediaRepository.playbackState.observe(this) { state ->
            val isPlaying = state?.state == android.media.session.PlaybackState.STATE_PLAYING
            binding.btnPlayPause.setImageResource(
                if (isPlaying) R.drawable.ic_pause else R.drawable.ic_play
            )
            if (!isDraggingSeekBar) {
                updateProgress()
            }
        }
    }

    private fun updateProgress() {
        if (isDraggingSeekBar) return
        
        val state = MediaRepository.playbackState.value ?: return
        var currentPos = state.position

        if (state.state == android.media.session.PlaybackState.STATE_PLAYING) {
            val timeDelta = android.os.SystemClock.elapsedRealtime() - state.lastPositionUpdateTime
            currentPos += (timeDelta * state.playbackSpeed).toLong()
        }

        // Prevent exceeding duration
        val duration = MediaRepository.duration.value ?: 0L
        if (currentPos > duration) currentPos = duration
        if (currentPos < 0) currentPos = 0

        binding.seekBar.progress = currentPos.toInt()
        binding.tvCurrentTime.text = formatTime(currentPos)
    }

    private fun formatTime(ms: Long): String {
        val totalSeconds = ms / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
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
