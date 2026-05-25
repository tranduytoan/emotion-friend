package com.emotionfriend.services

import com.emotionfriend.models.Situation
import com.emotionfriend.repositories.SituationRepository

class SituationService(private val repo: SituationRepository) {
    fun getAll(): List<Situation> = repo.findAll()
}
