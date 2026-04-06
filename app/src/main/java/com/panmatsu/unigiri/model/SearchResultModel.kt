package com.panmatsu.unigiri.model

data class SearchResultModel(
    val hits: List<ZutocaModel>,
    val query: String,
    val processingTimeMs: Int,
    val limit: Int,
    val offset: Int,
    val estimatedTotalHits: Int,
    val requestUid: String,
) {
    companion object {
        fun createInstance() = SearchResultModel(
            hits = emptyList(),
            query = "",
            processingTimeMs = 0,
            limit = 0,
            offset = 0,
            estimatedTotalHits = 0,
            requestUid = ""
        )
    }
}

data class ZutocaModel(
    val id: String,
    val pack: List<String>,
    val title: String,
    val songs: String,
    val illustrator: String,
    val rare: String,
    val type: String,
    val cardType: String,
    val clock: Int,
    val nightAttack:Int?,
    val noonAttack: Int?,
    val effect: String,
    val cost: Int,
    val power: Int,
    val img: String,
    val topDisplay: String,
    val public: String,
)
