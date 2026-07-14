package com.panmatsu.unigiri

import com.panmatsu.unigiri.domain.HandSimulator
import com.panmatsu.unigiri.model.DeckCardModel
import com.panmatsu.unigiri.model.DeckModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.random.Random

class HandSimulatorTest {

    private fun card(cardId: String, count: Int, sortOrder: Int) = DeckCardModel(
        cardId = cardId,
        title = "カード$cardId",
        img = "https://example.com/$cardId.png",
        cardType = "Character",
        pack = listOf("第1弾"),
        count = count,
        sortOrder = sortOrder,
    )

    // 4種×2枚 + 12種×1枚 = 20枚
    private fun deck(): DeckModel {
        val doubles = (1..4).map { card("d$it", count = 2, sortOrder = it) }
        val singles = (1..12).map { card("s$it", count = 1, sortOrder = 4 + it) }
        return DeckModel(
            id = "deck-1",
            name = "テストデッキ",
            createdAt = 0L,
            updatedAt = 0L,
            cards = doubles + singles,
        )
    }

    @Test
    fun deal_returns5HandAnd15Pile() {
        val dealt = HandSimulator.deal(deck(), Random(1))

        assertEquals(5, dealt.hand.size)
        assertEquals(15, dealt.pile.size)
    }

    @Test
    fun deal_allInstanceIdsUnique_andNoOverlapBetweenHandAndPile() {
        val dealt = HandSimulator.deal(deck(), Random(2))
        val allIds = (dealt.hand + dealt.pile).map { it.instanceId }

        assertEquals(20, allIds.size)
        assertEquals(20, allIds.toSet().size)
    }

    @Test
    fun deal_respectsCardCounts() {
        val dealt = HandSimulator.deal(deck(), Random(3))
        val countsByCardId = (dealt.hand + dealt.pile)
            .groupingBy { it.card.cardId }
            .eachCount()

        for (deckCard in deck().cards) {
            assertEquals(deckCard.count, countsByCardId[deckCard.cardId])
        }
    }

    @Test
    fun deal_sameSeedIsDeterministic() {
        val first = HandSimulator.deal(deck(), Random(42))
        val second = HandSimulator.deal(deck(), Random(42))

        assertEquals(first, second)
    }

    @Test
    fun mulligan_keepsUnselectedCardsInSameSlots() {
        val dealt = HandSimulator.deal(deck(), Random(4))
        val selected = setOf(dealt.hand[1].instanceId, dealt.hand[3].instanceId)

        val result = HandSimulator.mulligan(dealt.hand, selected, dealt.pile)

        assertEquals(dealt.hand[0], result[0])
        assertEquals(dealt.hand[2], result[2])
        assertEquals(dealt.hand[4], result[4])
    }

    @Test
    fun mulligan_replacementsComeFromPileFront_markedRedrawn() {
        val dealt = HandSimulator.deal(deck(), Random(5))
        val selected = setOf(dealt.hand[1].instanceId, dealt.hand[3].instanceId)

        val result = HandSimulator.mulligan(dealt.hand, selected, dealt.pile)

        assertEquals(dealt.pile[0].instanceId, result[1].instanceId)
        assertEquals(dealt.pile[1].instanceId, result[3].instanceId)
        assertTrue(result[1].isRedrawn)
        assertTrue(result[3].isRedrawn)
        assertFalse(result[0].isRedrawn)
        assertFalse(result[2].isRedrawn)
        assertFalse(result[4].isRedrawn)
    }

    @Test
    fun mulligan_selectedCardsDoNotReturn() {
        val dealt = HandSimulator.deal(deck(), Random(6))
        val selected = dealt.hand.take(3).map { it.instanceId }.toSet()

        val result = HandSimulator.mulligan(dealt.hand, selected, dealt.pile)

        val resultIds = result.map { it.instanceId }.toSet()
        assertTrue(selected.none { it in resultIds })
    }

    @Test
    fun mulligan_emptySelection_returnsHandUnchanged() {
        val dealt = HandSimulator.deal(deck(), Random(7))

        val result = HandSimulator.mulligan(dealt.hand, emptySet(), dealt.pile)

        assertEquals(dealt.hand, result)
    }

    @Test
    fun mulligan_allFiveSelected_replacesEntireHand() {
        val dealt = HandSimulator.deal(deck(), Random(8))
        val selected = dealt.hand.map { it.instanceId }.toSet()

        val result = HandSimulator.mulligan(dealt.hand, selected, dealt.pile)

        assertEquals(
            dealt.pile.take(5).map { it.instanceId },
            result.map { it.instanceId }
        )
        assertTrue(result.all { it.isRedrawn })
    }
}
