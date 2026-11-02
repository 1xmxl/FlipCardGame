package cn.edu.sicnu.cs.stu.gongtao.fourth.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Model 层的本地单元测试（不需要设备，执行 ./gradlew testDebugUnitTest）。
 * 覆盖：Card 的配对规则、Deck 的发牌数量、CardMatchingGame 的选牌/计分/重开。
 */
class CardMatchingGameTest {

    // ---------------- Card ----------------

    @Test
    fun 同点数的两张牌得4分() {
        val a = Card(suit = Card.validSuits[0], rank = Card.rankStrings[0])
        val b = Card(suit = Card.validSuits[1], rank = Card.rankStrings[0])
        assertEquals(4, a.match(arrayOf(b)))
    }

    @Test
    fun 同花色不同点数得1分() {
        val a = Card(suit = Card.validSuits[0], rank = Card.rankStrings[0])
        val b = Card(suit = Card.validSuits[0], rank = Card.rankStrings[1])
        assertEquals(1, a.match(arrayOf(b)))
    }

    @Test
    fun 花色点数都不同得0分() {
        val a = Card(suit = Card.validSuits[0], rank = Card.rankStrings[0])
        val b = Card(suit = Card.validSuits[1], rank = Card.rankStrings[1])
        assertEquals(0, a.match(arrayOf(b)))
    }

    // ---------------- Deck ----------------

    @Test
    fun 一副牌共52张且抽完返回空() {
        val deck = Deck()
        assertEquals(52, deck.size)
        repeat(52) { deck.drawRandomCard() }
        assertNull(deck.drawRandomCard())
    }

    // ---------------- CardMatchingGame ----------------

    @Test
    fun 新的一局应发出指定数量的牌且分数为0() {
        val game = CardMatchingGame(16)
        assertEquals(16, game.cardTotal)
        assertEquals(0, game.score)
        assertEquals(0, game.matchedCount)
        assertFalse(game.isCompleted)
        assertTrue(game.allCards().none { it.isChosen || it.isMatched })
    }

    @Test
    fun 第一次选牌会被翻开并扣1分() {
        val game = CardMatchingGame(16)
        game.chooseCardAtIndex(0)
        assertTrue(game.cardAtIndex(0).isChosen)
        assertEquals(-CardMatchingGame.COST_TO_CHOOSE, game.score)
    }

    @Test
    fun 再次点击同一张牌会收起且分数不变() {
        val game = CardMatchingGame(16)
        game.chooseCardAtIndex(0)
        val scoreAfterFirst = game.score
        game.chooseCardAtIndex(0)
        assertFalse(game.cardAtIndex(0).isChosen)
        assertEquals(scoreAfterFirst, game.score)
    }

    @Test
    fun 选中两张同花色的牌应当配对成功() {
        val game = CardMatchingGame(16)
        val cards = game.allCards()
        // 16 张牌分布在 4 种花色里，必定存在两张同花色的牌
        val first = 0
        val second = cards.indices.first { it != first && isSameSuit(cards, first, it) }

        game.chooseCardAtIndex(first)
        game.chooseCardAtIndex(second)

        assertTrue(game.cardAtIndex(first).isMatched)
        assertTrue(game.cardAtIndex(second).isMatched)
        assertEquals(2, game.matchedCount)
        assertTrue(game.score > 0)
    }

    @Test
    fun 配对成功的牌不会再被翻开() {
        val game = CardMatchingGame(16)
        val cards = game.allCards()
        val first = 0
        val second = cards.indices.first { it != first && isSameSuit(cards, first, it) }
        game.chooseCardAtIndex(first)
        game.chooseCardAtIndex(second)

        val scoreAfterMatch = game.score
        game.chooseCardAtIndex(first) // 已配对的牌不响应
        assertEquals(scoreAfterMatch, game.score)
        assertTrue(game.cardAtIndex(first).isMatched)
    }

    @Test
    fun 重新开始一局后分数归零且牌全部复位() {
        val game = CardMatchingGame(16)
        game.chooseCardAtIndex(0)
        game.chooseCardAtIndex(1)
        game.reset()

        assertEquals(0, game.score)
        assertEquals(0, game.matchedCount)
        assertEquals(16, game.cardTotal)
        assertTrue(game.allCards().none { it.isChosen || it.isMatched })
    }

    @Test
    fun 玩家仓库_提交更高分才刷新纪录() {
        PlayerRepository.init(
            name = "tester", gender = "M", studentId = "1", phone = "000", email = "t@e.com"
        )
        val base = PlayerRepository.current().highScore

        assertTrue(PlayerRepository.submitScore(base + 10))
        assertFalse(PlayerRepository.submitScore(base + 5))
        assertEquals(base + 10, PlayerRepository.current().highScore)
    }

    @Test
    fun 玩家仓库_修改信息后能读回新值() {
        PlayerRepository.init(
            name = "tester", gender = "M", studentId = "1", phone = "000", email = "t@e.com"
        )

        assertTrue(
            PlayerRepository.updateProfile(
                name = "新名字",
                gender = "F",
                studentId = "2024110509",
                phone = "13800001111",
                email = "new@example.com",
                hobbies = setOf(0, 2),
                avatarIndex = 2
            )
        )

        val player = PlayerRepository.current()
        assertEquals("新名字", player.name)
        assertEquals("F", player.gender)
        assertEquals("2024110509", player.studentId)
        assertEquals("13800001111", player.phone)
        assertEquals("new@example.com", player.email)
        assertEquals(setOf(0, 2), player.hobbies)
        assertEquals(2, player.avatarIndex)
    }

    /** 判断两张牌是否同花色（Card 的 suit 是私有的，用 toString 的颜色/花色前缀判断） */
    private fun isSameSuit(cards: List<Card>, first: Int, second: Int): Boolean {
        val a = cards[first].toString()
        val b = cards[second].toString()
        return a.first() == b.first()
    }
}
