package com.panmatsu.unigiri.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface DeckDao {

    @Transaction
    @Query("SELECT * FROM decks ORDER BY updatedAt DESC")
    fun getDecksWithCards(): Flow<List<DeckWithCards>>

    @Transaction
    @Query("SELECT * FROM decks WHERE id = :deckId")
    suspend fun getDeckWithCards(deckId: String): DeckWithCards?

    @Upsert
    suspend fun upsertDeck(deck: DeckEntity)

    @Insert
    suspend fun insertCards(cards: List<DeckCardEntity>)

    @Query("DELETE FROM deck_cards WHERE deckId = :deckId")
    suspend fun deleteCardsForDeck(deckId: String)

    @Query("DELETE FROM decks WHERE id = :deckId")
    suspend fun deleteDeck(deckId: String)

    // @Insert(onConflict = REPLACE) は親行を delete+insert するため
    // FK CASCADE が新カードを巻き添え削除する。@Upsert なら安全
    @Transaction
    suspend fun saveDeckWithCards(deck: DeckEntity, cards: List<DeckCardEntity>) {
        upsertDeck(deck)
        deleteCardsForDeck(deck.id)
        insertCards(cards)
    }
}
