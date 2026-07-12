package com.panmatsu.unigiri.domain

import com.panmatsu.unigiri.model.DeckModel

class SaveDeckUseCase(
    private val repository: DeckRepository
) {
    suspend operator fun invoke(deck: DeckModel) {
        DeckValidator.validateHardRules(deck)
        repository.saveDeck(deck)
    }
}
