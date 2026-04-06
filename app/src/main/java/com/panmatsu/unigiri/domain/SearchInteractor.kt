package com.panmatsu.unigiri.domain

import com.panmatsu.unigiri.model.SearchResultModel

class SearchInteractor(
    private val useCase: GetSearchResultUseCase
) {
    suspend fun fetchSamples(): SearchResultModel {
        return useCase()
    }
}
