package com.panmatsu.unigiri.data.repository

import com.panmatsu.unigiri.data.api.ApiService
import com.panmatsu.unigiri.data.api.SearchRequest
import com.panmatsu.unigiri.domain.SearchRepository
import com.panmatsu.unigiri.model.SearchResultModel
import com.panmatsu.unigiri.model.ZutocaModel

class SearchRepositoryImpl(
    private val api: ApiService
): SearchRepository {

    override suspend fun fetchSearchResult(): SearchResultModel {
        val response = api.searchCards(
            SearchRequest(
                q = "",
                limit = 999,
                //filter = """public != "非公開" AND rare IN ["SR"] AND songs = "お勉強しといてよ""""
                filter = """public != "非公開""""
            )
        )

        return SearchResultModel(
            hits = response.hits.map {
                ZutocaModel(
                    id = it.id,
                    pack = it.pack,
                    title = it.title,
                    songs = it.songs,
                    illustrator = it.illustrator,
                    rare = it.rare,
                    type = it.type,
                    cardType = it.cardType,
                    clock = it.clock,
                    nightAttack = it.nightAttack.toIntOrNull(),
                    noonAttack = it.noonAttack.toIntOrNull(),
                    effect = it.effect,
                    cost = it.cost,
                    power = it.power,
                    img = it.img,
                    topDisplay = it.topDisplay,
                    public = it.public
                )
            },
            query = response.query,
            processingTimeMs = response.processingTimeMs,
            limit = response.limit,
            offset = response.offset,
            estimatedTotalHits = response.estimatedTotalHits,
            requestUid = response.requestUid
        )
    }
}