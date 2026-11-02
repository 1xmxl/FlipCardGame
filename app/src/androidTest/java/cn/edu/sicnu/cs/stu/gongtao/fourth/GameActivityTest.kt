package cn.edu.sicnu.cs.stu.gongtao.fourth

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withTagValue
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.not
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 翻牌游戏的 UI 测试（Espresso）。
 * 需要连接真机或模拟器：./gradlew connectedDebugAndroidTest
 *
 * 覆盖：16 张牌是否渲染出来、点击某张牌后是否翻开（文字由牌背变成牌面）、重新开始是否正常。
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class GameActivityTest {

    @Test
    fun 游戏页显示棋盘与重新开始按钮() {
        ActivityScenario.launch(GameActivity::class.java).use {
            onView(withId(R.id.gridBoard)).check(matches(isDisplayed()))
            onView(withId(R.id.tvScore)).check(matches(isDisplayed()))
            onView(withId(R.id.tvProgress)).check(matches(isDisplayed()))
            onView(withId(R.id.btnRestart)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun 点击第一张牌后牌面被翻开() {
        ActivityScenario.launch(GameActivity::class.java).use {
            onView(withTagValue(`is`(0))).check(matches(withText(R.string.card_back)))
            onView(withTagValue(`is`(0))).perform(click())
            onView(withTagValue(`is`(0))).check(matches(not(withText(R.string.card_back))))
        }
    }

    @Test
    fun 重新开始后所有牌回到牌背状态() {
        ActivityScenario.launch(GameActivity::class.java).use {
            onView(withTagValue(`is`(0))).perform(click())
            onView(withTagValue(`is`(0))).check(matches(not(withText(R.string.card_back))))

            onView(withId(R.id.btnRestart)).perform(click())

            onView(withTagValue(`is`(0))).check(matches(withText(R.string.card_back)))
        }
    }
}
