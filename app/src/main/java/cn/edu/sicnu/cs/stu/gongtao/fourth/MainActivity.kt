package cn.edu.sicnu.cs.stu.gongtao.fourth

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import cn.edu.sicnu.cs.stu.gongtao.fourth.databinding.ActivityMainBinding
import cn.edu.sicnu.cs.stu.gongtao.fourth.model.PlayerRepository

/**
 * 主 Activity（View）：显示当前玩家的基本信息
 * （头像、姓名、性别、学号、电话、邮件地址、爱好、游戏最高分），
 * 并提供两个入口，分别进入"信息修改"和"翻牌游戏"两个二级 Activity。
 */
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    /** 三种头像底色，按 Player.avatarIndex 选择 */
    private val avatarBackgrounds = intArrayOf(
        R.drawable.bg_avatar_1,
        R.drawable.bg_avatar_2,
        R.drawable.bg_avatar_3
    )

    /** 从"信息修改"页返回后刷新界面 */
    private val editProfileLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            renderProfile()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnEditProfile.setOnClickListener {
            editProfileLauncher.launch(Intent(this, EditProfileActivity::class.java))
        }
        binding.btnPlayGame.setOnClickListener {
            startActivity(Intent(this, GameActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // 从游戏页返回时最高分可能已经变化，重新渲染一次
        renderProfile()
    }

    /** 把 PlayerRepository 里的数据同步到界面 */
    private fun renderProfile() {
        val player = PlayerRepository.current()

        binding.tvAvatar.text = player.name.take(1)
        binding.tvAvatar.setBackgroundResource(
            avatarBackgrounds[player.avatarIndex % avatarBackgrounds.size]
        )
        binding.tvNameValue.text = player.name
        binding.tvGenderValue.text = player.gender
        binding.tvStudentIdValue.text = player.studentId
        binding.tvPhoneValue.text = player.phone
        binding.tvEmailValue.text = player.email
        binding.tvHobbiesValue.text = formatHobbies(player.hobbies)
        binding.tvHighScoreValue.text = getString(R.string.score_value_format, player.highScore)
    }

    /** 把爱好下标翻译成文字，用顿号连接；一个都没选时显示"未选择" */
    private fun formatHobbies(hobbies: Set<Int>): String {
        if (hobbies.isEmpty()) {
            return getString(R.string.hobby_none)
        }
        val names = resources.getStringArray(R.array.hobbies)
        return hobbies.sorted()
            .mapNotNull { index -> names.getOrNull(index) }
            .joinToString(getString(R.string.hobby_separator))
    }
}
