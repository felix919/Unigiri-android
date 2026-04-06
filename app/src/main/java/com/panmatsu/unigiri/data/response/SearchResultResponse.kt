package com.panmatsu.unigiri.data.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class SearchResultResponse(
    val hits: List<ZutocaResponse>,
    val query: String,
    val processingTimeMs: Int,
    val limit: Int,
    val offset: Int,
    val estimatedTotalHits: Int,
    val requestUid: String,
)

@Serializable
data class ZutocaResponse(
    val id: String,
    val pack: List<String>,
    val title: String,
    val songs: String,
    val illustrator: String,
    val rare: String,
    val type: String,
    @SerializedName("class")
    val cardType: String,
    val clock: Int,
    @SerializedName("night_attack")
    val nightAttack: String,
    @SerializedName("noon_attack")
    val noonAttack: String,
    val effect: String,
    val cost: Int,
    val power: Int,
    val img: String,
    @SerializedName("top_display")
    val topDisplay: String,
    val public: String,
)
