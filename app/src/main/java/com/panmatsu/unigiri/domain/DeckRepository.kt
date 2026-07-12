package com.panmatsu.unigiri.domain

import com.panmatsu.unigiri.model.DeckModel
import kotlinx.coroutines.flow.Flow

interface DeckRepository {
    fun getDecks(): Flow<List<DeckModel>>
    suspend fun getDeck(id: String): DeckModel?
    suspend fun saveDeck(deck: DeckModel)
    suspend fun deleteDeck(id: String)
}
