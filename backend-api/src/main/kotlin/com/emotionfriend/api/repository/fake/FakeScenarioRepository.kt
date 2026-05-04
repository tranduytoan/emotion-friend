package com.emotionfriend.api.repository.fake

import com.emotionfriend.api.model.ScenarioLesson
import com.emotionfriend.api.repository.ScenarioRepository

class FakeScenarioRepository : ScenarioRepository {

    private val scenarios = listOf(
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
    )

    override suspend fun getAll(): List<ScenarioLesson> = scenarios

    override suspend fun getById(id: String): ScenarioLesson? = scenarios.find { it.id == id }
}
