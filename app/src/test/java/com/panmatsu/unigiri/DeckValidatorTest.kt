package com.panmatsu.unigiri

import com.panmatsu.unigiri.domain.DeckValidationException
import com.panmatsu.unigiri.domain.DeckValidator
import com.panmatsu.unigiri.model.DeckCardModel
import com.panmatsu.unigiri.model.DeckModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class DeckValidatorTest {

    private fun card(
        cardId: String,
        count: Int,
        cardType: String = DeckValidator.CHARACTER_CARD_TYPE,
        sortOrder: Int = 0,
    ) = DeckCardModel(
        cardId = cardId,
        title = "カード$cardId",
        img = "https://example.com/$cardId.png",
        cardType = cardType,
        pack = listOf("第1弾"),
        count = count,
        sortOrder = sortOrder,
    )

    private fun deck(cards: List<DeckCardModel>) = DeckModel(
        id = "deck-1",
        name = "テストデッキ",
        createdAt = 0L,
        updatedAt = 0L,
        cards = cards,
    )

    @Test
    fun makeState_countsTotalsAndCopies() {
        val state = DeckValidator.makeState(
            listOf(
                card("a", count = 2),
                card("b", count = 1, cardType = "Enchant"),
            )
        )

        assertEquals(3, state.totalCount)
        assertEquals(2, state.characterCount)
        assertEquals(2, state.copyCounts["a"])
        assertEquals(1, state.copyCounts["b"])
    }

    @Test
    fun canAdd_blocksThirdCopy() {
        val state = DeckValidator.makeState(listOf(card("a", count = 2)))

        assertFalse(state.canAdd("a"))
        assertTrue(state.canAdd("b"))
    }

    @Test
    fun canAdd_blocksAt20Cards() {
        val cards = (1..10).map { card("c$it", count = 2, sortOrder = it) }
        val state = DeckValidator.makeState(cards)

        assertEquals(20, state.totalCount)
        assertTrue(state.isComplete)
        assertFalse(state.canAdd("new"))
    }

    @Test
    fun showsCharacterWarning_whenCharactersBelowHalf() {
        val belowHalf = DeckValidator.makeState(
            listOf(
                card("a", count = 1),
                card("b", count = 2, cardType = "Enchant"),
            )
        )
        assertTrue(belowHalf.showsCharacterWarning)

        val exactlyHalf = DeckValidator.makeState(
            listOf(
                card("a", count = 1),
                card("b", count = 1, cardType = "Enchant"),
            )
        )
        assertFalse(exactlyHalf.showsCharacterWarning)

        val empty = DeckValidator.makeState(emptyList())
        assertFalse(empty.showsCharacterWarning)
    }

    @Test
    fun validateHardRules_throwsOnTooManyCopies() {
        assertThrows(DeckValidationException.TooManyCopies::class.java) {
            DeckValidator.validateHardRules(deck(listOf(card("a", count = 3))))
        }
    }

    @Test
    fun validateHardRules_throwsOnTooManyCards() {
        val cards = (1..11).map { card("c$it", count = 2, sortOrder = it) }
        assertThrows(DeckValidationException.TooManyCards::class.java) {
            DeckValidator.validateHardRules(deck(cards))
        }
    }

    @Test
    fun validateHardRules_allowsDraft() {
        // 作りかけ (20枚未満) はハードルール違反にならない
        DeckValidator.validateHardRules(deck(listOf(card("a", count = 1))))
    }
}
