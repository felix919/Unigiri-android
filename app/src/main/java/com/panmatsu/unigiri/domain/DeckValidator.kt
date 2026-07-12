package com.panmatsu.unigiri.domain

import com.panmatsu.unigiri.model.DeckCardModel
import com.panmatsu.unigiri.model.DeckModel

data class DeckValidationState(
    val totalCount: Int = 0,
    val characterCount: Int = 0,
    val copyCounts: Map<String, Int> = emptyMap(),
) {
    val isComplete: Boolean
        get() = totalCount == DeckValidator.DECK_SIZE

    fun canAdd(cardId: String): Boolean =
        totalCount < DeckValidator.DECK_SIZE &&
                (copyCounts[cardId] ?: 0) < DeckValidator.MAX_COPIES

    // 公式ルール上キャラクターカード50%以上は推奨 (保存はブロックしない)
    val showsCharacterWarning: Boolean
        get() = totalCount > 0 && characterCount * 2 < totalCount
}

sealed class DeckValidationException(message: String) : Exception(message) {
    class TooManyCards(count: Int) :
        DeckValidationException("デッキは${DeckValidator.DECK_SIZE}枚までです (現在${count}枚)")

    class TooManyCopies(title: String) :
        DeckValidationException("「${title}」は${DeckValidator.MAX_COPIES}枚までです")
}

object DeckValidator {
    const val DECK_SIZE = 20
    const val MAX_COPIES = 2
    const val CHARACTER_CARD_TYPE = "Character"

    fun makeState(cards: List<DeckCardModel>): DeckValidationState {
        var totalCount = 0
        var characterCount = 0
        val copyCounts = mutableMapOf<String, Int>()

        for (card in cards) {
            totalCount += card.count
            if (card.cardType == CHARACTER_CARD_TYPE) {
                characterCount += card.count
            }
            copyCounts[card.cardId] = (copyCounts[card.cardId] ?: 0) + card.count
        }

        return DeckValidationState(
            totalCount = totalCount,
            characterCount = characterCount,
            copyCounts = copyCounts,
        )
    }

    fun validateHardRules(deck: DeckModel) {
        if (deck.totalCount > DECK_SIZE) {
            throw DeckValidationException.TooManyCards(deck.totalCount)
        }
        deck.cards.firstOrNull { it.count > MAX_COPIES }?.let {
            throw DeckValidationException.TooManyCopies(it.title)
        }
    }
}
