package com.panmatsu.unigiri.domain

import com.panmatsu.unigiri.model.DeckModel
import kotlinx.coroutines.flow.Flow

class GetDecksUseCase(
    private val repository: DeckRepository
) {
    operator fun invoke(): Flow<List<DeckModel>> {
        return repository.getDecks()
    }

    suspend fun byId(id: String): DeckModel? {
        return repository.getDeck(id)
    }
}
