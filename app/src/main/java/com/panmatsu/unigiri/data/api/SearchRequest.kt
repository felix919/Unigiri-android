package com.panmatsu.unigiri.data.api

data class SearchRequest(
    val q: String,
    val limit: Int,
    val filter: String
)
