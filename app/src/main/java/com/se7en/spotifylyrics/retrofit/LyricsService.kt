package com.se7en.spotifylyrics.retrofit

import com.se7en.spotifylyrics.model.LyricsResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface LyricsService {

    @GET("{artist}/{track}")
    suspend fun getLyrics(
        @Path("artist") artist: String,
        @Path("track") track: String
    ): LyricsResponse

    companion object {
        fun create(): LyricsService =
            Retrofit.Builder()
                .baseUrl("https://api.lyrics.ovh/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(LyricsService::class.java)
    }
}
