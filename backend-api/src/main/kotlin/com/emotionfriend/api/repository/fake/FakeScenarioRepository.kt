package com.emotionfriend.api.repository.fake

import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.repository.ScenarioRepository
import java.util.UUID
import java.util.concurrent.CopyOnWriteArrayList

class FakeScenarioRepository : ScenarioRepository {

    private val scenarios = CopyOnWriteArrayList(
        mutableListOf(
        ScenarioLesson(
            id = "scenario_1",
            title = "Bạn bị vấp ngã",
            situation = "Bạn đang chạy trong sân trường thì bị vấp ngã. Bạn cảm thấy thế nào?",
            options = listOf("Vui vẻ", "Đau và buồn", "Ngạc nhiên vui", "Tức giận"),
            correctIndex = 1,
            explanation = "Khi bị ngã, chúng ta thường cảm thấy đau và buồn.",
        ),
        ScenarioLesson(
            id = "scenario_2",
            title = "Nhận quà bất ngờ",
            situation = "Ba mẹ tặng bạn một món quà bất ngờ vào sinh nhật. Bạn cảm thấy thế nào?",
            options = listOf("Buồn", "Tức giận", "Vui và ngạc nhiên", "Mệt mỏi"),
            correctIndex = 2,
            explanation = "Nhận quà bất ngờ thường khiến chúng ta cảm thấy vui và ngạc nhiên.",
        ),
        ScenarioLesson(
            id = "scenario_3",
            title = "Bị mất đồ chơi yêu thích",
            situation = "Bạn không tìm thấy món đồ chơi yêu thích của mình. Bạn cảm thấy thế nào?",
            options = listOf("Vui", "Bình thản", "Buồn và lo lắng", "Ngạc nhiên"),
            correctIndex = 2,
            explanation = "Mất đồ vật yêu thích khiến chúng ta cảm thấy buồn và lo lắng.",
        ),
        ScenarioLesson(
            id = "scenario_4",
            title = "Được điểm cao",
            situation = "Bạn vừa nhận kết quả bài kiểm tra và được điểm rất cao. Bạn cảm thấy thế nào?",
            options = listOf("Mệt mỏi", "Vui và tự hào", "Buồn", "Tức giận"),
            correctIndex = 1,
            explanation = "Đạt thành tích tốt khiến chúng ta cảm thấy vui và tự hào.",
        ),
        ScenarioLesson(
            id = "scenario_5",
            title = "Bạn bè không cho chơi cùng",
            situation = "Các bạn trong lớp đang chơi nhưng không cho bạn tham gia. Bạn cảm thấy thế nào?",
            options = listOf("Vui", "Ngạc nhiên vui", "Buồn và tủi thân", "Bình thản"),
            correctIndex = 2,
            explanation = "Bị loại ra khỏi nhóm khiến chúng ta cảm thấy buồn và tủi thân.",
        ),
        ScenarioLesson(
            id = "scenario_6",
            title = "Không mở được đồ chơi",
            situation = "Con rất muốn chơi đồ chơi mới nhưng mãi không mở được hộp. Con cảm thấy thế nào?",
            options = listOf("Vui vẻ", "Tức giận và bực bội", "Ngạc nhiên", "Bình thản"),
            correctIndex = 1,
            explanation = "Khi không làm được điều mình muốn, chúng ta thường cảm thấy tức giận và bực bội.",
        ),
        ScenarioLesson(
            id = "scenario_7",
            title = "Phải dậy sớm đi học",
            situation = "Hôm nay con phải dậy rất sớm để đi học trong khi vẫn còn buồn ngủ. Con cảm thấy thế nào?",
            options = listOf("Vui vẻ", "Ngạc nhiên", "Mệt mỏi và buồn ngủ", "Tức giận"),
            correctIndex = 2,
            explanation = "Khi phải dậy sớm hơn bình thường, chúng ta thường cảm thấy mệt mỏi và buồn ngủ.",
        ),
        ScenarioLesson(
            id = "scenario_8",
            title = "Gặp lại bạn cũ",
            situation = "Con gặp lại người bạn thân mà đã lâu không gặp. Con cảm thấy thế nào?",
            options = listOf("Buồn", "Mệt mỏi", "Tức giận", "Vui và ngạc nhiên"),
            correctIndex = 3,
            explanation = "Gặp lại bạn thân sau thời gian xa cách khiến chúng ta cảm thấy vui và ngạc nhiên.",
        ),
        ScenarioLesson(
            id = "scenario_9",
            title = "Bị ướt vì mưa bất ngờ",
            situation = "Con đang chơi ngoài sân thì trời mưa to bất ngờ và con bị ướt hết. Con cảm thấy thế nào?",
            options = listOf("Vui vẻ", "Bình thản", "Khó chịu và tức giận", "Ngạc nhiên"),
            correctIndex = 2,
            explanation = "Khi bị ướt bất ngờ vì mưa, chúng ta thường cảm thấy khó chịu và tức giận.",
        ),
        ScenarioLesson(
            id = "scenario_10",
            title = "Nghe nhạc nhẹ trước khi ngủ",
            situation = "Trước khi ngủ, con nghe nhạc nhẹ nhàng và nằm thư giãn. Con cảm thấy thế nào?",
            options = listOf("Tức giận", "Bình thản và thư giãn", "Buồn", "Mệt và khó chịu"),
            correctIndex = 1,
            explanation = "Nghe nhạc nhẹ và thư giãn giúp chúng ta cảm thấy bình thản và dễ ngủ hơn.",
        ),
    )

    override suspend fun getAll(): List<ScenarioLesson> = scenarios.toList()

    override suspend fun getById(id: String): ScenarioLesson? = scenarios.find { it.id == id }

    override suspend fun create(lesson: ScenarioLesson): ScenarioLesson {
        val newLesson = lesson.copy(id = lesson.id.ifBlank { UUID.randomUUID().toString() })
        scenarios.add(newLesson)
        return newLesson
    }

    override suspend fun update(id: String, lesson: ScenarioLesson): ScenarioLesson? {
        val idx = scenarios.indexOfFirst { it.id == id }
        if (idx < 0) return null
        val updated = lesson.copy(id = id)
        scenarios[idx] = updated
        return updated
    }

    override suspend fun delete(id: String): Boolean {
        return scenarios.removeIf { it.id == id }
    }
}
