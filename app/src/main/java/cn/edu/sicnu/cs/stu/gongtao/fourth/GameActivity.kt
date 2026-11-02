package cn.edu.sicnu.cs.stu.gongtao.fourth

import android.graphics.Typeface
import android.os.Bundle
import android.view.Gravity
import android.widget.GridLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import cn.edu.sicnu.cs.stu.gongtao.fourth.databinding.ActivityGameBinding
import cn.edu.sicnu.cs.stu.gongtao.fourth.model.CardMatchingGame
import cn.edu.sicnu.cs.stu.gongtao.fourth.model.PlayerRepository

/**
 * 二级 Activity 2：翻牌游戏。
 *
 * MVC 分工：
 *  - Model：CardMatchingGame（牌堆、选中/配对状态、计分规则，完全不依赖界面）；
 *  - View：activity_game.xml + 用代码生成的 16 个卡片控件（在 GridLayout 中排成 4×4）；
 *  - Controller：本类的 onCardClicked / syncAll，接收点击 → 交给 Model → 把状态同步回界面。
 */
class GameActivity : AppCompatActivity() {

    companion object {
        /** 4 × 4 = 16 张牌 */
        private const val CARD_COUNT = 16
        private const val COLUMN_COUNT = 4
    }

    private lateinit var binding: ActivityGameBinding
    private val game = CardMatchingGame(CARD_COUNT)
    private val cardViews = mutableListOf<TextView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buildCardViews()
        binding.btnRestart.setOnClickListener { startNewGame() }
        binding.btnExit.setOnClickListener { finish() }
        syncAll()
    }

    // ---------------------------------------------------------------
    // View：用代码动态生成牌面控件
    // ---------------------------------------------------------------

    private fun buildCardViews() {
        binding.gridBoard.removeAllViews()
        binding.gridBoard.columnCount = COLUMN_COUNT
        cardViews.clear()

        val cardWidth = calculateCardWidth()
        val cardHeight = (cardWidth * 1.35f).toInt()

        for (index in 0 until game.cardTotal) {
            val cardView = TextView(this).apply {
                tag = index            // 给 UI 测试定位用
                textSize = 26f
                gravity = Gravity.CENTER
                setTypeface(typeface, Typeface.BOLD)
                layoutParams = GridLayout.LayoutParams().apply {
                    width = cardWidth
                    height = cardHeight
                    setMargins(dp(5), dp(5), dp(5), dp(5))
                }
                setOnClickListener { onCardClicked(index) }
            }
            cardViews.add(cardView)
            binding.gridBoard.addView(cardView)
        }
    }

    /** 按屏幕宽度算出每张牌的宽度，保证 4 列刚好铺满且留出间距 */
    private fun calculateCardWidth(): Int {
        val screenWidth = resources.displayMetrics.widthPixels
        val pagePadding = dp(12) * 2
        val cardMargins = dp(5) * 2 * COLUMN_COUNT
        return (screenWidth - pagePadding - cardMargins) / COLUMN_COUNT
    }

    // ---------------------------------------------------------------
    // Controller：处理交互并同步界面
    // ---------------------------------------------------------------

    private fun onCardClicked(index: Int) {
        game.chooseCardAtIndex(index)
        syncAll()

        if (game.isCompleted) {
            onGameCompleted()
        }
    }

    private fun startNewGame() {
        game.reset()
        buildCardViews()
        syncAll()
    }

    /** 把模型的全部状态同步到界面（16 张牌 + 顶部状态栏） */
    private fun syncAll() {
        cardViews.forEachIndexed { index, view -> renderCard(index, view) }
        binding.tvScore.text = getString(R.string.game_score, game.score)
        binding.tvProgress.text = getString(R.string.game_progress, game.matchedCount, game.cardTotal)
        binding.tvBest.text = getString(R.string.game_best, PlayerRepository.current().highScore)
    }

    /** 单张牌的三态渲染：已配对 / 已翻开 / 牌背 */
    private fun renderCard(index: Int, view: TextView) {
        val card = game.cardAtIndex(index)
        when {
            card.isMatched -> {
                view.text = card.toString()
                view.setTextColor(color(if (card.isRed) R.color.heart_red else R.color.spade_black))
                view.setBackgroundResource(R.drawable.bg_card_matched)
                view.alpha = 0.6f
            }

            card.isChosen -> {
                view.text = card.toString()
                view.setTextColor(color(if (card.isRed) R.color.heart_red else R.color.spade_black))
                view.setBackgroundResource(R.drawable.bg_card_face)
                view.alpha = 1f
            }

            else -> {
                view.text = getString(R.string.card_back)
                view.setTextColor(color(R.color.card_back_text))
                view.setBackgroundResource(R.drawable.bg_card_back)
                view.alpha = 1f
            }
        }
    }

    /** 全部配对完成：记录最高分并询问是否再来一局 */
    private fun onGameCompleted() {
        val isNewRecord = PlayerRepository.submitScore(game.score)
        syncAll()

        val message = if (isNewRecord) {
            getString(R.string.dialog_win_record, game.score)
        } else {
            getString(R.string.dialog_win, game.score)
        }

        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_win_title)
            .setMessage(message)
            .setPositiveButton(R.string.dialog_play_again) { _, _ -> startNewGame() }
            .setNegativeButton(R.string.dialog_close) { _, _ -> }
            .show()
    }

    private fun color(resId: Int): Int = ContextCompat.getColor(this, resId)

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}
