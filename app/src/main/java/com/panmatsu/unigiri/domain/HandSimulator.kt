package com.panmatsu.unigiri.domain

import com.panmatsu.unigiri.model.DeckCardModel
import com.panmatsu.unigiri.model.DeckModel
import kotlin.random.Random

data class HandCard(
    // 同名カード(count=2)を別インスタンスとして扱うためのID (スロット展開順 0..19)
    val instanceId: Int,
    val card: DeckCardModel,
    val isRedrawn: Boolean = false,
)

/**
 * 初期手札シミュレーション (公式ルール: 開始時に5枚ドロー、1回だけ任意枚数を引き直し可)
 * 「引き直しは1回のみ」はUI側のフェーズ管理で担保する
 */
object HandSimulator {
    const val HAND_SIZE = 5

    data class DealtHand(
        val hand: List<HandCard>,
        val pile: List<HandCard>,
    )

    fun deal(deck: DeckModel, random: Random = Random.Default): DealtHand {
        val slots = deck.cards
            .sortedBy { it.sortOrder }
            .flatMap { card -> List(card.count) { card } }
            .mapIndexed { index, card -> HandCard(instanceId = index, card = card) }
        require(slots.size == DeckValidator.DECK_SIZE) {
            "デッキが${DeckValidator.DECK_SIZE}枚ではありません"
        }

        val shuffled = slots.shuffled(random)
        return DealtHand(
            hand = shuffled.take(HAND_SIZE),
            pile = shuffled.drop(HAND_SIZE),
        )
    }

    // 選択カードを同じスロット位置で山札の先頭から置換する (pileは既にランダム順なのでRNG不要)
    // 選択カードは山札に戻さない
    fun mulligan(
        hand: List<HandCard>,
        selectedIds: Set<Int>,
        pile: List<HandCard>,
    ): List<HandCard> {
        var drawIndex = 0
        return hand.map { handCard ->
            if (handCard.instanceId in selectedIds) {
                pile[drawIndex++].copy(isRedrawn = true)
            } else {
                handCard
            }
        }
    }
}
