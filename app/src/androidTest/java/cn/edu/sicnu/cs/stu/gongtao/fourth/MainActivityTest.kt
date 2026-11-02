package cn.edu.sicnu.cs.stu.gongtao.fourth

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 主界面的 UI 测试（Espresso）。
 * 需要连接真机或模拟器：./gradlew connectedDebugAndroidTest
 *
 * 覆盖：显示默认玩家信息、跳转到修改信息页、修改后主界面同步更新、进入游戏页。
 */
@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityTest {

    @Test
    fun 主界面显示玩家信息与两个入口按钮() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.tvNameValue)).check(matches(isDisplayed()))
            onView(withId(R.id.tvGenderValue)).check(matches(isDisplayed()))
            onView(withId(R.id.tvPhoneValue)).check(matches(isDisplayed()))
            onView(withId(R.id.tvHighScoreValue)).check(matches(isDisplayed()))
            onView(withId(R.id.btnEditProfile)).check(matches(isDisplayed()))
            onView(withId(R.id.btnPlayGame)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun 点击修改个人信息会打开编辑页() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnEditProfile)).perform(click())
            onView(withId(R.id.etName)).check(matches(isDisplayed()))
            onView(withId(R.id.etPhone)).check(matches(isDisplayed()))
            onView(withId(R.id.btnSave)).check(matches(isDisplayed()))
        }
    }

    @Test
    fun 修改姓名保存后主界面同步更新() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnEditProfile)).perform(click())

            onView(withId(R.id.etName)).perform(replaceText(NEW_NAME), closeSoftKeyboard())
            onView(withId(R.id.etPhone)).perform(replaceText(NEW_PHONE), closeSoftKeyboard())
            onView(withId(R.id.btnSave)).perform(click())

            onView(withId(R.id.tvNameValue)).check(matches(withText(NEW_NAME)))
            onView(withId(R.id.tvPhoneValue)).check(matches(withText(NEW_PHONE)))
        }
    }

    @Test
    fun 点击开始翻牌游戏会进入游戏页() {
        ActivityScenario.launch(MainActivity::class.java).use {
            onView(withId(R.id.btnPlayGame)).perform(click())
            onView(withId(R.id.gridBoard)).check(matches(isDisplayed()))
            onView(withId(R.id.btnRestart)).check(matches(isDisplayed()))
        }
    }

    private companion object {
        const val NEW_NAME = "测试玩家"
        const val NEW_PHONE = "13800001111"
    }
}
