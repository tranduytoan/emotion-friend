package com.emotionfriend.api.plugins

import com.emotionfriend.api.repository.db.*
import com.emotionfriend.api.repository.fake.*
import com.emotionfriend.api.routes.*
import com.emotionfriend.api.service.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting(useDatabase: Boolean = false) {
    val emotionRepo   = if (useDatabase) DbEmotionRepository()   else FakeEmotionRepository()
    val scenarioRepo  = if (useDatabase) DbScenarioRepository()  else FakeScenarioRepository()
    val journalRepo   = if (useDatabase) DbJournalRepository()   else FakeJournalRepository()
    val practiceRepo  = if (useDatabase) DbPracticeRepository()  else FakePracticeRepository()
    val progressRepo  = if (useDatabase) DbProgressRepository()  else FakeProgressRepository(journalRepo, practiceRepo)
    val storyRepo     = if (useDatabase) DbStoryRepository()     else FakeStoryRepository()
    val musicRepo     = if (useDatabase) DbMusicRepository()     else FakeMusicRepository()

    val emotionService  = EmotionService(emotionRepo)
    val scenarioService = ScenarioService(scenarioRepo)
    val journalService  = JournalService(journalRepo)
    val practiceService = PracticeService(practiceRepo)
    val progressService = ProgressService(progressRepo)
    val storyService    = StoryService(storyRepo)
    val musicService    = MusicService(musicRepo)

    routing {
        healthRoute()
        emotionRoutes(emotionService)
        scenarioRoutes(scenarioService)
        journalRoutes(journalService)
        practiceRoutes(practiceService)
        progressRoutes(progressService, practiceService)
        storyRoutes(storyService)
        adminRoutes(scenarioService, storyService, musicService)
    }
}
