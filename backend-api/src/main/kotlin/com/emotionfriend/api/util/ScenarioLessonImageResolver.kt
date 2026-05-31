package com.emotionfriend.api.util

import java.io.File

object ScenarioLessonImageResolver {
    private val supportedExtensions = listOf("png", "jpg", "jpeg", "webp")

    fun resolveImageName(lessonId: Int, currentImageName: String?): String? {
        val normalized = currentImageName?.trim()?.takeIf { it.isNotEmpty() }
        if (normalized != null) return normalized

        val imageDir = resolveScenarioLessonImageDir()
        if (!imageDir.exists()) return null

        return supportedExtensions.firstNotNullOfOrNull { ext ->
            val candidateName = "$lessonId.$ext"
            val candidateFile = File(imageDir, candidateName)
            if (candidateFile.isFile) candidateName else null
        }
    }

    fun resolveScenarioLessonImageDir(): File {
        val explicitPath = System.getenv("SCENARIO_LESSONS_IMAGE_PATH")
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
        if (explicitPath != null) return File(explicitPath)

        val staticFilesPath = System.getenv("STATIC_FILES_PATH")
            ?.trim()
            ?.takeIf { it.isNotEmpty() }
        if (staticFilesPath != null) return File(staticFilesPath, "scenario_lessons")

        val cwd = File(System.getProperty("user.dir"))
        val repoResPath = File(cwd, "../res/img/scenario_lessons")
        return if (repoResPath.exists() || repoResPath.parentFile?.exists() == true) {
            repoResPath
        } else {
            File(cwd, "res/img/scenario_lessons")
        }
    }
}