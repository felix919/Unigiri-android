package com.panmatsu.unigiri.data.repository

import com.panmatsu.unigiri.data.local.DeckCardEntity
import com.panmatsu.unigiri.data.local.DeckDao
import com.panmatsu.unigiri.data.local.DeckEntity
import com.panmatsu.unigiri.data.local.DeckWithCards
import com.panmatsu.unigiri.domain.DeckRepository
import com.panmatsu.unigiri.model.DeckCardModel
import com.panmatsu.unigiri.model.DeckModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DeckRepositoryImpl(
    private val dao: DeckDao
) : DeckRepository {

    override fun getDecks(): Flow<List<DeckModel>> {
        return dao.getDecksWithCards().map { list ->
            list.map { it.toModel() }
        }
    }

    override suspend fun getDeck(id: String): DeckModel? {
        return dao.getDeckWithCards(id)?.toModel()
    }

    override suspend fun saveDeck(deck: DeckModel) {
        dao.saveDeckWithCards(
            deck = deck.toEntity(),
            cards = deck.cards.map { it.toEntity(deck.id) },
        )
    }

    override suspend fun deleteDeck(id: String) {
        dao.deleteDeck(id)
    }

    private fun DeckWithCards.toModel() = DeckModel(
        id = deck.id,
        name = deck.name,
        createdAt = deck.createdAt,
        updatedAt = deck.updatedAt,
        cards = cards
            .sortedBy { it.sortOrder }
            .map {
                DeckCardModel(
                    cardId = it.cardId,
                    title = it.title,
                    img = it.img,
                    cardType = it.cardType,
                    pack = it.pack,
                    count = it.count,
                    sortOrder = it.sortOrder,
                )
            },
    )

    private fun DeckModel.toEntity() = DeckEntity(
        id = id,
        name = name,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    private fun DeckCardModel.toEntity(deckId: String) = DeckCardEntity(
        deckId = deckId,
        cardId = cardId,
        title = title,
        img = img,
        cardType = cardType,
        pack = pack,
        count = count,
        sortOrder = sortOrder,
    )
}
