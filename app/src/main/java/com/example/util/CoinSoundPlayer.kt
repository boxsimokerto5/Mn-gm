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
    fun playCoinClink(frequencyMultiplier: Double = 1.0) {
        try {
            Thread {
                val sampleRate = 44100
                val durationMs = 340
                val numSamples = (sampleRate * durationMs) / 1000
                val buffer = ShortArray(numSamples)

                // Frekuensi nada tinggi khas koin perunggu/emas bertabrakan
                val f1 = 2489.0 * frequencyMultiplier // D#7 (denting utama)
                val f2 = 3136.0 * frequencyMultiplier // G7 (harmonik logam)
                val f3 = 4978.0 * frequencyMultiplier // D#8 (kilauan nada tinggi)

                for (i in 0 until numSamples) {
                    val t = i.toDouble() / sampleRate
                    val envelope = exp(-12.5 * t)
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

                Thread.sleep(durationMs.toLong() + 20)
                audioTrack.stop()
                audioTrack.release()
            }.start()
        } catch (_: Exception) {}
    }

    /**
     * Memainkan suara kerincing koin bertubi-tubi dengan variasi nada (cascade effect)
     * saat serbuan koin masuk ke dalam dompet.
     */
    fun playCoinCascade(count: Int = 4, intervalMs: Long = 75L) {
        try {
            Thread {
                val pitches = listOf(1.0, 1.08, 1.15, 1.22, 1.28)
                for (i in 0 until count) {
                    val pitch = pitches[i % pitches.size]
                    playCoinClink(pitch)
                    Thread.sleep(intervalMs)
                }
            }.start()
        } catch (_: Exception) {}
    }
}
