package cn.edu.sicnu.cs.stu.gongtao.fourth.model

/**
 * 一张扑克牌（MVC 中的 Model 层）。
 *
 * 参照实验附件 model/Card.kt 实现：花色 + 点数 + 是否被选中 / 是否已配对，
 * match() 方法与附件中的判分规则保持一致（同点数 4 分，同花色 1 分）。
 */
class Card(
    private val suit: String,
    private val rank: String,
    var isChosen: Boolean = false,
    var isMatched: Boolean = false
) {
    companion object {
        val rankStrings = arrayOf("A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K")
        val validSuits = arrayOf("♥", "♦", "♠", "♣")
    }

    override fun toString(): String {
        return suit + rank
    }

    /** 红色花色（红桃 / 方块）界面上用红字显示 */
    val isRed: Boolean
        get() = suit == validSuits[0] || suit == validSuits[1]

    /**
     * 与另一张牌比较，返回得分：
     * 同点数 4 分，同花色 1 分，都不同 0 分。
     */
    fun match(otherCards: Array<Card>): Int {
        var score = 0
        if (otherCards.size == 1) {
            val otherCard = otherCards[0]
            score = when {
                otherCard.rank == rank -> 4
                otherCard.suit == suit -> 1
                else -> 0
            }
        }
        return score
    }
}
