package com.panmatsu.unigiri.domain

import com.panmatsu.unigiri.model.DeckModel
import kotlinx.coroutines.flow.Flow

class DeckInteractor(
    private val getDecksUseCase: GetDecksUseCase,
    private val saveDeckUseCase: SaveDeckUseCase,
    private val deleteDeckUseCase: DeleteDeckUseCase,
) {
    fun getDecks(): Flow<List<DeckModel>> {
        return getDecksUseCase()
    }

    suspend fun getDeck(id: String): DeckModel? {
        return getDecksUseCase.byId(id)
    }

    suspend fun save(deck: DeckModel) {
        saveDeckUseCase(deck)
    }

    suspend fun delete(id: String) {
        deleteDeckUseCase(id)
    }
}
