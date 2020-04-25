package com.se7en.spotifylyrics.model

import com.google.gson.annotations.SerializedName

data class LyricsResponse(
    @SerializedName("lyrics")
    val lyrics: String
)
