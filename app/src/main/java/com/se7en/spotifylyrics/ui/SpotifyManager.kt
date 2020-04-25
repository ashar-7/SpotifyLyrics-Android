package com.se7en.spotifylyrics.ui

import android.content.Context
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.spotify.android.appremote.api.ConnectionParams
import com.spotify.android.appremote.api.Connector
import com.spotify.android.appremote.api.SpotifyAppRemote

class SpotifyManager(
    private val context: Context,
    private val onConnectedCallback: (SpotifyAppRemote) -> Unit
): LifecycleObserver {

    private val tag = javaClass.simpleName

    private val clientID = "65e7c3aa11cf4ce6ad67b00e0444af8d"
    private val redirectUri = "https://spotifylyrics.com/callback"

    private var spotifyAppRemote: SpotifyAppRemote? = null

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun connect() {
        val connectionParams = ConnectionParams.Builder(clientID)
            .setRedirectUri(redirectUri)
            .showAuthView(true)
            .build()

        SpotifyAppRemote.connect(context, connectionParams, object: Connector.ConnectionListener {
            override fun onConnected(appRemote: SpotifyAppRemote) {
                Log.d(tag, "connected")

                spotifyAppRemote = appRemote
                onConnectedCallback(appRemote)
            }

            override fun onFailure(throwable: Throwable) {
                Log.e(tag, throwable.message, throwable)
            }
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun disconnect() {
        spotifyAppRemote?.apply {
            SpotifyAppRemote.disconnect(this)
        }
    }
}
