package com.panmatsu.unigiri.data.local

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.Relation

@Entity(tableName = "decks")
data class DeckEntity(
    @PrimaryKey val id: String,
    val name: String,
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(
    tableName = "deck_cards",
    foreignKeys = [
        ForeignKey(
            entity = DeckEntity::class,
            parentColumns = ["id"],
            childColumns = ["deckId"],
            onDelete = ForeignKey.CASCADE,
        )
    ],
    indices = [Index("deckId")],
)
data class DeckCardEntity(
    @PrimaryKey(autoGenerate = true) val pk: Long = 0,
    val deckId: String,
    val cardId: String,
    val title: String,
    val img: String,
    val cardType: String,
    val pack: List<String>,
    val count: Int,
    val sortOrder: Int,
)

data class DeckWithCards(
    @Embedded val deck: DeckEntity,
    @Relation(parentColumn = "id", entityColumn = "deckId")
    val cards: List<DeckCardEntity>,
)
