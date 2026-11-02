package cn.edu.sicnu.cs.stu.gongtao.fourth

import android.app.Application
import cn.edu.sicnu.cs.stu.gongtao.fourth.model.PlayerRepository

/**
 * 应用入口：用字符串资源初始化默认的玩家信息，
 * 保证代码里不出现任何硬编码的界面文字。
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        PlayerRepository.init(
            name = getString(R.string.player_default_name),
            gender = getString(R.string.player_default_gender),
            studentId = getString(R.string.player_default_student_id),
            phone = getString(R.string.player_default_phone),
            email = getString(R.string.player_default_email)
        )
    }
}
