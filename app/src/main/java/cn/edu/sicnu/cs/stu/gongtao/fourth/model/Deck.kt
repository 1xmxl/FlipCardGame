package cn.edu.sicnu.cs.stu.gongtao.fourth.model

import java.util.Random

/**
 * 一副扑克牌（MVC 中的 Model 层）。
 *
 * 参照实验附件 model/Deck.kt 实现：初始化时生成 4 种花色 × 13 个点数共 52 张牌，
 * drawRandomCard() 随机抽走一张，抽完返回 null。
 */
class Deck {
    private val cards = mutableListOf<Card>()
    private val r = Random()

    init {
        for (suit in Card.validSuits) {
            for (rank in Card.rankStrings) {
                cards.add(Card(suit = suit, rank = rank))
            }
        }
    }

    /** 牌堆里剩余的牌数 */
    val size: Int
        get() = cards.size

    fun drawRandomCard(): Card? {
        var randomCard: Card? = null
        if (cards.size > 0) {
            randomCard = cards.removeAt(r.nextInt(cards.size))
        }
        return randomCard
    }
}
