package com.panmatsu.unigiri.domain

class DeleteDeckUseCase(
    private val repository: DeckRepository
) {
    suspend operator fun invoke(id: String) {
        repository.deleteDeck(id)
    }
}
