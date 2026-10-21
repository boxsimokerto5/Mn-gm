package com.example.util

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.exp
import kotlin.math.sin

/**
 * Pembangkit suara denting / kerincing koin logam murni (PCM Audio Synthesis).
 * Bekerja 100% offline tanpa membutuhkan file audio eksternal.
 * Menghasilkan resonansi logam khas koin jatuh (dual-tone metallic ping).
 */
object CoinSoundPlayer {
    fun playCoinClink() {
        try {
            Thread {
                val sampleRate = 44100
                val durationMs = 380
                val numSamples = (sampleRate * durationMs) / 1000
                val buffer = ShortArray(numSamples)

                // Frekuensi nada tinggi khas koin perunggu/emas bertabrakan
                val f1 = 2489.0 // D#7 (denting utama)
                val f2 = 3136.0 // G7 (harmonik logam)
                val f3 = 4978.0 // D#8 (kilauan nada tinggi)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    // Peluruhan eksponensial cepat untuk resonansi khas logam
                    val envelope = exp(-11.5 * t)
                    val sample = envelope * (
                        0.55 * sin(2.0 * Math.PI * f1 * t) +
                        0.30 * sin(2.0 * Math.PI * f2 * t) +
                        0.15 * sin(2.0 * Math.PI * f3 * t)
                    )
                    buffer[i] = (sample * Short.MAX_VALUE).toInt()
                        .coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt())
                        .toShort()
                }

                val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_GAME)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()

                val audioFormat = AudioFormat.Builder()
                    .setSampleRate(sampleRate)
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()

                val audioTrack = AudioTrack.Builder()
                    .setAudioAttributes(audioAttributes)
                    .setAudioFormat(audioFormat)
                    .setBufferSizeInBytes(buffer.size * 2)
                    .setTransferMode(AudioTrack.MODE_STATIC)
                    .build()

                audioTrack.write(buffer, 0, buffer.size)
                audioTrack.play()

                Thread.sleep(durationMs.toLong() + 30)
                audioTrack.stop()
                audioTrack.release()
            }.start()
        } catch (_: Exception) {}
    }
}
