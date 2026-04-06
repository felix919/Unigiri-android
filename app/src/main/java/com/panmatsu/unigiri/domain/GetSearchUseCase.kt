package com.panmatsu.unigiri.domain

import com.panmatsu.unigiri.model.SearchResultModel

class GetSearchResultUseCase(
    private val repository: SearchRepository
) {
    suspend operator fun invoke(): SearchResultModel {
        return repository.fetchSearchResult()
    }
}
