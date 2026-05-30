package com.emotionfriend.feature.story

import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.emotionfriend.core.audio.rememberTtsPlayer
import com.emotionfriend.core.designsystem.components.EmotionPrimaryButton
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.components.TeacherMyGuide
import com.emotionfriend.core.designsystem.components.VyEmotion
import com.emotionfriend.core.designsystem.theme.EmotionAngryBg
import com.emotionfriend.core.designsystem.theme.EmotionCalmBg
import com.emotionfriend.core.designsystem.theme.EmotionHappyBg
import com.emotionfriend.core.designsystem.theme.EmotionSadBg
import com.emotionfriend.core.designsystem.theme.EmotionSurprisedBg
import com.emotionfriend.core.designsystem.theme.EmotionTiredBg
import com.emotionfriend.core.designsystem.theme.MintGreen40
import com.emotionfriend.core.designsystem.theme.MintGreen80
import com.emotionfriend.core.designsystem.theme.OnSurfaceVar
import com.emotionfriend.core.designsystem.theme.SkyBlue40
import com.emotionfriend.core.designsystem.theme.SkyBlue80
import com.emotionfriend.core.designsystem.theme.SkyBlueLight
import com.emotionfriend.core.designsystem.theme.SunYellow80
import com.emotionfriend.domain.model.Story
import kotlinx.coroutines.delay

// ─── Entry point ─────────────────────────────────────────────────────────────

@Composable
fun StoryScreen(
    onNavigateBack: () -> Unit,
    viewModel: StoryViewModel = hiltViewModel(),
) {
    val stories by viewModel.stories.collectAsState()
    var selectedStory by remember { mutableStateOf<Story?>(null) }

    val selected = selectedStory
    if (selected != null) {
        StoryReaderScreen(
            story           = selected,
            onBack          = { selectedStory = null },
            onNavigateBack  = onNavigateBack,
        )
    } else {
        StoriesListScreen(
            stories         = stories,
            onSelectStory   = { selectedStory = it },
            onNavigateBack  = onNavigateBack,
        )
    }
}

// ─── Stories List ─────────────────────────────────────────────────────────────

private val categoryEmojis = mapOf(
    "SCHOOL"             to "🏫",
    "FRIENDSHIP"         to "🤝",
    "EMPATHY"            to "💛",
    "EMOTION_REGULATION" to "🌟",
    "DEFAULT"            to "📖",
)

private val categoryColors = mapOf(
    "SCHOOL"             to EmotionHappyBg,
    "FRIENDSHIP"         to EmotionCalmBg,
    "EMPATHY"            to SunYellow80,
    "EMOTION_REGULATION" to EmotionSadBg,
    "DEFAULT"            to SkyBlueLight,
)

@Composable
private fun StoriesListScreen(
    stories: List<Story>,
    onSelectStory: (Story) -> Unit,
    onNavigateBack: () -> Unit,
) {
    val tts = rememberTtsPlayer()

    LaunchedEffect(Unit) {
        delay(400)
        tts.speak("Chào con! Chọn một câu chuyện để cô Vy kể cho con nghe nhé!")
    }

    EmotionScreenScaffold(
        title      = "Kể chuyện",
        onBack     = onNavigateBack,
    ) {
        if (stories.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Đang tải câu chuyện...", color = OnSurfaceVar)
            }
        } else {
            LazyVerticalGrid(
                columns         = GridCells.Fixed(2),
                contentPadding  = PaddingValues(16.dp),
                verticalArrangement   = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier        = Modifier.fillMaxSize(),
            ) {
                items(stories) { story ->
                    StoryCard(story = story, onClick = { onSelectStory(story) })
                }
            }
        }
    }
}

@Composable
private fun StoryCard(
    story: Story,
    onClick: () -> Unit,
) {
    val bg    = categoryColors[story.category] ?: SkyBlueLight
    val emoji = categoryEmojis[story.category] ?: "📖"

    Card(
        shape     = RoundedCornerShape(16.dp),
        colors    = CardDefaults.cardColors(containerColor = bg),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier  = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(16.dp),
        ) {
            Text(text = emoji, fontSize = 48.sp)
            Text(
                text       = story.title,
                style      = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                textAlign  = TextAlign.Center,
                maxLines   = 2,
            )
        }
    }
}

// ─── Story Reader ─────────────────────────────────────────────────────────────

private val emotionChoices = listOf(
    Pair("😊 Vui vẻ",      EmotionHappyBg),
    Pair("😢 Buồn bã",     EmotionSadBg),
    Pair("😮 Ngạc nhiên",  EmotionSurprisedBg),
    Pair("😌 Bình yên",    EmotionCalmBg),
    Pair("😟 Lo lắng",     EmotionTiredBg),
    Pair("😠 Tức giận",    EmotionAngryBg),
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun StoryReaderScreen(
    story: Story,
    onBack: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    val tts          = rememberTtsPlayer()
    val totalSlides  = (story.images.size.coerceAtLeast(1)) + 1  // images + emotion-question
    val pagerState   = rememberPagerState(pageCount = { totalSlides })
    var storyRead    by remember { mutableStateOf(false) }
    var emotionPicked by remember { mutableStateOf<String?>(null) }

    // Auto-read story content via TTS when screen opens
    LaunchedEffect(story.id) {
        delay(500)
        tts.speak(story.title + ". " + story.content)
        storyRead = true
    }

    EmotionScreenScaffold(
        title  = story.title,
        onBack = onBack,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Pager — each page = illustration emoji + excerpt; last page = emotion question
            HorizontalPager(
                state    = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { page ->
                if (page < story.images.size) {
                    StoryImageSlide(
                        imageIndex = page,
                        content    = story.content,
                    )
                } else {
                    EmotionQuestionSlide(
                        storyRead      = storyRead,
                        emotionPicked  = emotionPicked,
                        onPickEmotion  = { label ->
                            emotionPicked = label
                            tts.speak("Con cảm thấy $label sau câu chuyện này. Cảm ơn con đã chia sẻ!")
                        },
                        onDone         = onNavigateBack,
                        tts            = tts,
                    )
                }
            }

            // Page indicator
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth(),
            ) {
                repeat(totalSlides) { idx ->
                    val color = if (pagerState.currentPage == idx) SkyBlue40 else MintGreen80
                    Box(
                        Modifier
                            .padding(horizontal = 4.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            // Controls
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                EmotionPrimaryButton(
                    text     = "🔊 Nghe lại",
                    onClick  = { tts.speak(story.title + ". " + story.content) },
                    modifier = Modifier.weight(1f),
                )
                EmotionPrimaryButton(
                    text     = "⬅ Truyện khác",
                    onClick  = onBack,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

// Slide with illustration (emoji) and a snippet of the story
@Composable
private fun StoryImageSlide(
    imageIndex: Int,
    content: String,
) {
    val illustrationEmojis = listOf("📚", "🌈", "⭐", "🎨")
    val emoji = illustrationEmojis.getOrElse(imageIndex) { "📖" }

    // Show a portion of the story text for this illustration slide
    val wordsPerSlide = 80
    val words = content.split(" ")
    val start = imageIndex * wordsPerSlide
    val end   = minOf(start + wordsPerSlide, words.size)
    val excerpt = if (start < words.size) words.subList(start, end).joinToString(" ") + "..." else ""

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(SkyBlue80),
        ) {
            Text(text = emoji, fontSize = 60.sp)
        }
        Text(
            text  = excerpt,
            style = MaterialTheme.typography.bodyLarge,
            color = OnSurfaceVar,
            textAlign = TextAlign.Start,
        )
    }
}

// Last slide — emotion question
@Composable
private fun EmotionQuestionSlide(
    storyRead: Boolean,
    emotionPicked: String?,
    onPickEmotion: (String) -> Unit,
    onDone: () -> Unit,
    tts: com.emotionfriend.core.audio.TtsPlayer,
) {
    val question = "Sau khi nghe xong câu chuyện, con cảm thấy như thế nào?"

    LaunchedEffect(storyRead) {
        if (storyRead) {
            delay(300)
            tts.speak(question)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(8.dp),
    ) {
        TeacherMyGuide(
            message   = question,
            onSpeak   = { tts.speak(question) },
            vyEmotion = VyEmotion.HAPPY,
        )

        Spacer(Modifier.height(8.dp))

        if (emotionPicked == null) {
            emotionChoices.forEach { (label, bg) ->
                Card(
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(containerColor = bg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier  = Modifier
                        .fillMaxWidth()
                        .clickable { onPickEmotion(label) },
                ) {
                    Text(
                        text     = label,
                        style    = MaterialTheme.typography.titleMedium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        textAlign = TextAlign.Center,
                    )
                }
            }
        } else {
            Text(
                text      = "Con đã chọn: $emotionPicked",
                style     = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color      = MintGreen40,
                textAlign  = TextAlign.Center,
            )
            Spacer(Modifier.height(8.dp))
            TeacherMyGuide(
                message   = "Cảm ơn con đã chia sẻ! Cô Vy rất vui khi được nghe cảm xúc của con.",
                onSpeak   = { tts.speak("Cảm ơn con đã chia sẻ!") },
                vyEmotion = VyEmotion.HAPPY,
            )
            Spacer(Modifier.height(12.dp))
            EmotionPrimaryButton(
                text     = "🏠 Về trang chủ",
                onClick  = onDone,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
