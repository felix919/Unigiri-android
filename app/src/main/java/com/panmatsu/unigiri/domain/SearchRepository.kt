package com.panmatsu.unigiri.domain

import com.panmatsu.unigiri.model.SearchResultModel

interface SearchRepository {
    suspend fun fetchSearchResult(): SearchResultModel
}
