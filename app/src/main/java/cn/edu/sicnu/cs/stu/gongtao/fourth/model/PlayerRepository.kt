package cn.edu.sicnu.cs.stu.gongtao.fourth.model

/** 玩家信息：头像、姓名、性别、学号、电话、邮件、爱好、游戏最高分 */
data class Player(
    var name: String = "",
    var gender: String = "",
    var studentId: String = "",
    var phone: String = "",
    var email: String = "",
    /** 选中的爱好下标（对应 res/values/strings.xml 中的 hobbies 数组） */
    val hobbies: MutableSet<Int> = mutableSetOf(),
    /** 头像样式下标，界面据此切换不同的圆形底色 */
    var avatarIndex: Int = 0,
    var highScore: Int = 0
)

/**
 * 玩家数据仓库（单例）：三个 Activity 共享同一份内存数据。
 *
 * 按实验要求"修改后的信息无需保存到文件中"，所以这里只放在内存里，
 * 应用进程结束即失效，不使用 SharedPreferences / 文件 / 数据库。
 */
object PlayerRepository {

    private var player: Player? = null

    /** 由 Application 在启动时用字符串资源初始化默认玩家信息 */
    fun init(name: String, gender: String, studentId: String, phone: String, email: String) {
        if (player == null) {
            player = Player(
                name = name,
                gender = gender,
                studentId = studentId,
                phone = phone,
                email = email,
                hobbies = mutableSetOf(0),
                avatarIndex = 0,
                highScore = 0
            )
        }
    }

    fun current(): Player {
        val existing = player
        if (existing == null) {
            val created = Player()
            player = created
            return created
        }
        return existing
    }

    /** 修改个人信息（只改内存），返回是否真的发生了变化 */
    fun updateProfile(
        name: String,
        gender: String,
        studentId: String,
        phone: String,
        email: String,
        hobbies: Set<Int>,
        avatarIndex: Int
    ): Boolean {
        val p = current()
        val changed = p.name != name || p.gender != gender || p.studentId != studentId ||
            p.phone != phone || p.email != email || p.hobbies != hobbies ||
            p.avatarIndex != avatarIndex

        p.name = name
        p.gender = gender
        p.studentId = studentId
        p.phone = phone
        p.email = email
        p.hobbies.clear()
        p.hobbies.addAll(hobbies)
        p.avatarIndex = avatarIndex
        return changed
    }

    /** 切换头像样式（0 → 1 → 2 → 0 ...） */
    fun cycleAvatar(avatarCount: Int): Int {
        val p = current()
        p.avatarIndex = (p.avatarIndex + 1) % avatarCount
        return p.avatarIndex
    }

    /** 提交一局游戏的得分，返回是否刷新了最高分 */
    fun submitScore(score: Int): Boolean {
        val p = current()
        return if (score > p.highScore) {
            p.highScore = score
            true
        } else {
            false
        }
    }
}
