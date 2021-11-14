package com.example.testapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp.databinding.ActivityMainBinding
import com.example.testplayer.TestPlayer
import org.webrtc.RendererCommon

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private lateinit var testPlayer: TestPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.lifecycleOwner = this
    }

    private var activityStarted = false

    private val playerCallback = object : TestPlayer.Callback {
        override fun onInit() {
            testPlayer.start()
        }
    }

    override fun onStart() {
        super.onStart()

        activityStarted = true

        testPlayer = TestPlayer(applicationContext, playerCallback).apply {
            init()
            remoteProxyVideoSink.setTarget(binding.remote)
        }

        binding.remote.run {
            init(testPlayer.eglContext, null)
            setScalingType(RendererCommon.ScalingType.SCALE_ASPECT_FILL)
            holder.setKeepScreenOn(true)
        }
    }

    override fun onStop() {
        super.onStop()

        if (activityStarted) {
            testPlayer.stop()
            testPlayer.deinit()

            binding.remote.run {
                holder.setKeepScreenOn(false)
                release()
            }
        }

        activityStarted = false
    }
}