package cn.edu.sicnu.cs.stu.gongtao.fourth.model

/**
 * 翻牌游戏的核心逻辑（MVC 中的 Model 层）。
 *
 * 参照实验附件 model/CardMatchingGame.kt 实现：
 *  - 构造时从牌堆里随机抽 cardCount 张牌；
 *  - chooseCardAtIndex() 处理"翻开 / 收起 / 配对"与计分，
 *    配对加分（matchScore × MATCH_BONUS），不匹配扣分（MISMATCH_PENALTY），
 *    每选择一次牌都要付出代价（COST_TO_CHOOSE）；
 *  - 在附件代码基础上补充了界面需要的接口：allCards()、isCompleted、
 *    matchedCount、reset()（对应"可以重新开始一局游戏"的要求）。
 */
class CardMatchingGame(val cardCount: Int) {

    companion object {
        const val MISMATCH_PENALTY = 2
        const val MATCH_BONUS = 4
        const val COST_TO_CHOOSE = 1
    }

    var score = 0
        private set

    private var cards: MutableList<Card> = mutableListOf()

    init {
        reset()
    }

    /** 重新开始一局：重新洗牌抽牌，并清空分数 */
    fun reset() {
        val deck = Deck()
        cards = mutableListOf()
        for (i in 1..cardCount) {
            val card: Card? = deck.drawRandomCard()
            if (card != null) {
                cards.add(card)
            }
        }
        score = 0
    }

    fun cardAtIndex(index: Int): Card {
        return cards.get(index)
    }

    /** 界面用：只读地拿到全部牌 */
    fun allCards(): List<Card> = cards.toList()

    /** 实际抽到的牌数（牌堆够用时应等于 cardCount） */
    val cardTotal: Int
        get() = cards.size

    /** 已经配对成功的牌数 */
    val matchedCount: Int
        get() = cards.count { it.isMatched }

    /** 是否全部配对完成 */
    val isCompleted: Boolean
        get() = cards.isNotEmpty() && cards.all { it.isMatched }

    /**
     * 选择某一张牌：已被配对的牌不响应；已选中的牌再次点击则收起；
     * 否则去找当前已经选中且未配对的另一张牌做比较。
     */
    fun chooseCardAtIndex(index: Int) {
        val card = cardAtIndex(index)
        if (!card.isMatched) {
            if (card.isChosen) {
                card.isChosen = false
            } else {
                for (otherCard in cards) {
                    if (otherCard.isChosen && !otherCard.isMatched) {
                        val matchScore = card.match(arrayOf(otherCard))
                        if (matchScore > 0) {
                            score += matchScore * MATCH_BONUS
                            otherCard.isMatched = true
                            card.isMatched = true
                        } else {
                            score -= MISMATCH_PENALTY
                            otherCard.isChosen = false
                        }
                        break
                    }
                }
                score -= COST_TO_CHOOSE
                card.isChosen = true
            }
        }
    }
}
