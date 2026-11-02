package cn.edu.sicnu.cs.stu.gongtao.fourth

import android.app.Activity
import android.os.Bundle
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doAfterTextChanged
import cn.edu.sicnu.cs.stu.gongtao.fourth.databinding.ActivityEditProfileBinding
import cn.edu.sicnu.cs.stu.gongtao.fourth.model.PlayerRepository

/**
 * 二级 Activity 1（View）：修改个人信息。
 *
 * 修改结果直接写回内存中的 PlayerRepository，按实验要求不保存到文件；
 * 爱好是一组复选框，名称来自 res/values/strings.xml 的 hobbies 数组，
 * 复选框本身由代码动态生成。
 */
class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding

    private val hobbyChecks = mutableListOf<CheckBox>()

    private val avatarBackgrounds = intArrayOf(
        R.drawable.bg_avatar_1,
        R.drawable.bg_avatar_2,
        R.drawable.bg_avatar_3
    )

    private var avatarIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        buildHobbyChecks()
        fillCurrentProfile()

        binding.tvAvatar.setOnClickListener { switchAvatar() }
        binding.etName.doAfterTextChanged { renderAvatar() }
        binding.btnSave.setOnClickListener { saveProfile() }
        binding.btnCancel.setOnClickListener { finish() }
    }

    /** 用代码按字符串数组生成爱好复选框 */
    private fun buildHobbyChecks() {
        val hobbies = resources.getStringArray(R.array.hobbies)
        binding.containerHobbies.removeAllViews()
        hobbyChecks.clear()

        hobbies.forEachIndexed { index, name ->
            val checkBox = CheckBox(this).apply {
                text = name
                textSize = 15f
                tag = index
            }
            hobbyChecks.add(checkBox)
            binding.containerHobbies.addView(checkBox)
        }
    }

    /** 进入页面时把当前信息填进各个控件 */
    private fun fillCurrentProfile() {
        val player = PlayerRepository.current()

        avatarIndex = player.avatarIndex
        binding.etName.setText(player.name)
        binding.etStudentId.setText(player.studentId)
        binding.etPhone.setText(player.phone)
        binding.etEmail.setText(player.email)

        val isFemale = player.gender == getString(R.string.gender_female)
        binding.rbFemale.isChecked = isFemale
        binding.rbMale.isChecked = !isFemale

        hobbyChecks.forEachIndexed { index, checkBox ->
            checkBox.isChecked = player.hobbies.contains(index)
        }
        renderAvatar()
    }

    private fun switchAvatar() {
        avatarIndex = (avatarIndex + 1) % avatarBackgrounds.size
        renderAvatar()
    }

    /** 头像显示姓名首字，底色按 avatarIndex 切换 */
    private fun renderAvatar() {
        binding.tvAvatar.text = binding.etName.text.toString().take(1)
        binding.tvAvatar.setBackgroundResource(avatarBackgrounds[avatarIndex])
    }

    /** 保存：做非空校验后写回仓库，并把结果回传给主界面 */
    private fun saveProfile() {
        val name = binding.etName.text.toString().trim()
        val studentId = binding.etStudentId.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()

        if (name.isEmpty()) {
            showToast(getString(R.string.toast_name_required))
            return
        }
        if (phone.isEmpty()) {
            showToast(getString(R.string.toast_phone_required))
            return
        }

        val gender = getString(
            if (binding.rbFemale.isChecked) R.string.gender_female else R.string.gender_male
        )
        val hobbies = hobbyChecks
            .mapIndexedNotNull { index, checkBox -> if (checkBox.isChecked) index else null }
            .toSet()

        PlayerRepository.updateProfile(
            name = name,
            gender = gender,
            studentId = studentId,
            phone = phone,
            email = email,
            hobbies = hobbies,
            avatarIndex = avatarIndex
        )

        showToast(getString(R.string.toast_profile_saved))
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
