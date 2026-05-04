package com.emotionfriend.api.plugins

import com.emotionfriend.api.repository.fake.*
import com.emotionfriend.api.routes.*
import com.emotionfriend.api.service.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    // In-memory repositories (swap with DB-backed implementations when ready)
    val emotionRepo = FakeEmotionRepository()
    val scenarioRepo = FakeScenarioRepository()
    val journalRepo = FakeJournalRepository()
    val practiceRepo = FakePracticeRepository()
    val progressRepo = FakeProgressRepository(journalRepo, practiceRepo)

    // Services
    val emotionService = EmotionService(emotionRepo)
    val scenarioService = ScenarioService(scenarioRepo)
    val journalService = JournalService(journalRepo)
    val practiceService = PracticeService(practiceRepo)
    val progressService = ProgressService(progressRepo)

    routing {
        healthRoute()
        emotionRoutes(emotionService)
        scenarioRoutes(scenarioService)
        journalRoutes(journalService)
        practiceRoutes(practiceService)
        progressRoutes(progressService)
    }
}
