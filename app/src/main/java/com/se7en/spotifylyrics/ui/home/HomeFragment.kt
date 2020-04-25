package com.se7en.spotifylyrics.ui.home

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.se7en.spotifylyrics.R
import com.se7en.spotifylyrics.Resource
import com.se7en.spotifylyrics.model.Track
import com.se7en.spotifylyrics.ui.SpotifyManager
import com.spotify.android.appremote.api.SpotifyAppRemote
import kotlinx.android.synthetic.main.fragment_home.*
import kotlinx.android.synthetic.main.fragment_home.view.*
import retrofit2.HttpException

class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_home, container, false)

        val observer = SpotifyManager(requireContext(), ::onConnected)
        viewLifecycleOwner.lifecycle.addObserver(observer)

        homeViewModel.getNowPlaying().observe(viewLifecycleOwner, Observer { track ->
            nowPlaying.visibility = View.VISIBLE
            trackName.visibility = View.VISIBLE
            trackName.text = getString(R.string.track_name_template, track.name, track.artist)

            retrievingTrackLayout.visibility = View.GONE
        })

        homeViewModel.lyricsLiveData.observe(viewLifecycleOwner, Observer { response ->
            when(response) {
                is Resource.Loading -> {
                    progressBar.visibility = View.VISIBLE
                }

                is Resource.Success -> {
                    progressBar.visibility = View.INVISIBLE
                    root.lyrics.text = response.data.lyrics
                }

                is Resource.Error -> {
                    progressBar.visibility = View.INVISIBLE

                    root.lyrics.text =
                        if(response.exception is HttpException) "Lyrics not found"
                        else "Failed to get lyrics"

                    Log.d("HomeFragment", "Fucking hell")
                }
            }
        })

        return root
    }

    private fun onConnected(spotifyAppRemote: SpotifyAppRemote) {
        spotifyAppRemote.playerApi
            .subscribeToPlayerState()
            .setEventCallback { playerState ->
                playerState.track?.apply {
                    Log.d("HomeFragment", "$name by $artist")

                    homeViewModel.setNowPlaying(Track(name, artist.name))
                }
            }
    }
}
