package com.se7en.spotifylyrics.ui.home

import androidx.lifecycle.*
import com.se7en.spotifylyrics.Resource
import com.se7en.spotifylyrics.model.LyricsResponse
import com.se7en.spotifylyrics.model.Track
import com.se7en.spotifylyrics.retrofit.LyricsService
import java.lang.Exception

class HomeViewModel : ViewModel() {
    private val apiService = LyricsService.create()

    private val nowPlaying = MutableLiveData<Track>()

    val lyricsLiveData: LiveData<Resource<LyricsResponse>>
            = Transformations.switchMap(nowPlaying) { track ->
        liveData(context = viewModelScope.coroutineContext) {
            emit(Resource.Loading())
            emit(fetchLyrics(track))
        }
    }

    private suspend fun fetchLyrics(track: Track): Resource<LyricsResponse> {
        return try {
            Resource.Success(apiService.getLyrics(track.artist, track.name))
        } catch (e: Exception) {
            Resource.Error(e)
        }
    }

    fun setNowPlaying(track: Track) {
        nowPlaying.value = track
    }

    fun getNowPlaying(): LiveData<Track> = nowPlaying
}
