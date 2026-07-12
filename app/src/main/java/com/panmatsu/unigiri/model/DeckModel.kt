package com.panmatsu.unigiri.model

import com.panmatsu.unigiri.domain.DeckValidator

data class DeckModel(
    val id: String,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long,
    val cards: List<DeckCardModel>,
) {
    val totalCount: Int
        get() = cards.sumOf { it.count }

    val characterCount: Int
        get() = cards
            .filter { it.cardType == DeckValidator.CHARACTER_CARD_TYPE }
            .sumOf { it.count }
}

data class DeckCardModel(
    val cardId: String,
    val title: String,
    val img: String,
    val cardType: String,
    val pack: List<String>,
    val count: Int,
    val sortOrder: Int,
) {
    companion object {
        fun from(card: ZutocaModel, count: Int, sortOrder: Int) = DeckCardModel(
            cardId = card.id,
            title = card.title,
            img = card.img,
            cardType = card.cardType,
            pack = card.pack,
            count = count,
            sortOrder = sortOrder,
        )
    }
}
