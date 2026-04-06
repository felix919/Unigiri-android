package com.panmatsu.unigiri.data.api

import com.panmatsu.unigiri.data.response.SearchResultResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("indexes/zutomayocard_cards/search")
    suspend fun searchCards(
        @Body request: SearchRequest
    ): SearchResultResponse
}
