package com.emotionfriend.feature.story

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.emotionfriend.data.local.StoryDao
import com.emotionfriend.domain.model.Story
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(
    private val storyDao: StoryDao,
) : ViewModel() {

    val stories = storyDao.getAllStories()
        .map { list ->
            list.map { entity ->
                Story(
                    id          = entity.id,
                    title       = entity.title,
                    content     = entity.content,
                    images      = listOf(entity.image1, entity.image2, entity.image3, entity.image4)
                        .filter { it.isNotBlank() },
                    category    = entity.category,
                    imageFolder = entity.imageFolder,
                )
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
