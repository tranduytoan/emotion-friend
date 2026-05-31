package com.emotionfriend.api.plugins

import com.emotionfriend.api.repository.db.*
import com.emotionfriend.api.routes.*
import com.emotionfriend.api.service.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    val emotionRepo   = DbEmotionRepository()
    val scenarioRepo  = DbScenarioRepository()
    val journalRepo   = DbJournalRepository()
    val practiceRepo  = DbPracticeRepository()
    val progressRepo  = DbProgressRepository()
    val storyRepo     = DbStoryRepository()
    val musicRepo     = DbMusicRepository()
    val topicRepo     = DbLessonTopicRepository()

    val emotionService  = EmotionService(emotionRepo)
    val scenarioService = ScenarioService(scenarioRepo)
    val journalService  = JournalService(journalRepo)
    val practiceService = PracticeService(practiceRepo)
    val progressService = ProgressService(progressRepo)
    val storyService    = StoryService(storyRepo)
    val musicService    = MusicService(musicRepo)
    val topicService    = LessonTopicService(topicRepo)

    routing {
        healthRoute()
        emotionRoutes(emotionService)
        scenarioRoutes(scenarioService)
        topicRoutes(topicService)
        journalRoutes(journalService)
        practiceRoutes(practiceService)
        progressRoutes(progressService, practiceService)
        storyRoutes(storyService)
        adminRoutes(scenarioService, storyService, musicService, topicService)
    }
}
