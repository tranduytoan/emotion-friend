package com.emotionfriend.feature.relax

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.emotionfriend.core.audio.rememberTtsPlayer
import kotlinx.coroutines.delay
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emotionfriend.core.designsystem.components.EmotionCard
import com.emotionfriend.core.designsystem.components.EmotionPrimaryButton
import com.emotionfriend.core.designsystem.components.EmotionScreenScaffold
import com.emotionfriend.core.designsystem.theme.EmotionCalm
import com.emotionfriend.core.designsystem.theme.EmotionCalmBg
import com.emotionfriend.core.designsystem.theme.EmotionFriendTheme
import com.emotionfriend.core.designsystem.theme.EmotionHappy
import com.emotionfriend.core.designsystem.theme.EmotionHappyBg
import com.emotionfriend.core.designsystem.theme.MintGreen40
import com.emotionfriend.core.designsystem.theme.MintGreen80
import com.emotionfriend.core.designsystem.theme.OnSurfaceVar
import com.emotionfriend.core.designsystem.theme.SkyBlue40
import com.emotionfriend.core.designsystem.theme.SkyBlueLight

// ---------------------------------------------------------------------------
// Activity state
// ---------------------------------------------------------------------------

private enum class RelaxActivity { NONE, MUSIC, BREATHING, PUZZLE }

// ---------------------------------------------------------------------------
// Screen
// ---------------------------------------------------------------------------

@Composable
fun RelaxScreen(
    onBack  : () -> Unit,
    modifier: Modifier = Modifier
) {
    var active by remember { mutableStateOf(RelaxActivity.NONE) }
    // Duration settings — adjustable in ChoiceScreen before starting an activity
    var breathingCycles by remember { mutableIntStateOf(10) }   // 10 × 6 s = 1 min
    var musicDurationSecs by remember { mutableIntStateOf(30) } // 30 s default

    EmotionScreenScaffold(title = "Thư giãn", onBack = onBack) {
        // Fade between choice screen and active activity screen
        AnimatedContent(
            targetState = active,
            transitionSpec = { fadeIn(tween(300)) togetherWith fadeOut(tween(200)) },
            label = "relaxContent"
        ) { current ->
            when (current) {
                RelaxActivity.NONE -> ChoiceScreen(
                    onChoose               = { active = it },
                    breathingCycles        = breathingCycles,
                    onBreathingCyclesChange = { breathingCycles = it },
                    musicDurationSecs      = musicDurationSecs,
                    onMusicDurationChange  = { musicDurationSecs = it },
                    modifier               = modifier
                )
                RelaxActivity.BREATHING -> BreathingScreen(
                    onStop   = { active = RelaxActivity.NONE },
                    cycles   = breathingCycles,
                    modifier = modifier
                )
                RelaxActivity.MUSIC -> MusicScreen(
                    onStop        = { active = RelaxActivity.NONE },
                    durationSecs  = musicDurationSecs,
                    modifier      = modifier
                )
                RelaxActivity.PUZZLE -> PuzzleScreen(
                    onStop   = { active = RelaxActivity.NONE },
                    modifier = modifier
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Choice screen — 3 large tiles, one task each
// ---------------------------------------------------------------------------

@Composable
private fun ChoiceScreen(
    onChoose: (RelaxActivity) -> Unit,
    breathingCycles: Int,
    onBreathingCyclesChange: (Int) -> Unit,
    musicDurationSecs: Int,
    onMusicDurationChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier            = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text      = "Con muốn làm gì?",
            style     = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            modifier  = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(4.dp))

        // --- Breathing with duration picker ---------------------------------
        DurationChips(
            label   = "🫧 Thời gian thở:",
            options = listOf(10 to "1 phút", 20 to "2 phút", 30 to "3 phút"),
            selected = breathingCycles,
            onSelect = onBreathingCyclesChange
        )
        ActivityChoiceTile(
            emoji       = "🫧",
            title       = "Thở cùng bóng",
            background  = EmotionCalmBg,
            onClick     = { onChoose(RelaxActivity.BREATHING) }
        )

        // --- Music with duration picker -------------------------------------
        DurationChips(
            label   = "🎵 Thời gian nghe:",
            options = listOf(30 to "30 giây", 60 to "1 phút", 120 to "2 phút"),
            selected = musicDurationSecs,
            onSelect = onMusicDurationChange
        )
        ActivityChoiceTile(
            emoji       = "🎵",
            title       = "Nghe nhạc nhẹ",
            background  = EmotionHappyBg,
            onClick     = { onChoose(RelaxActivity.MUSIC) }
        )

        ActivityChoiceTile(
            emoji       = "🧩",
            title       = "Xếp hình vui",
            background  = SkyBlueLight,
            onClick     = { onChoose(RelaxActivity.PUZZLE) }
        )
    }
}

@Composable
private fun DurationChips(
    label: String,
    options: List<Pair<Int, String>>,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier              = modifier.fillMaxWidth()
    ) {
        Text(
            text  = label,
            style = MaterialTheme.typography.labelMedium,
            color = OnSurfaceVar
        )
        options.forEach { (value, chipLabel) ->
            val isSelected = selected == value
            OutlinedButton(
                onClick        = { onSelect(value) },
                shape          = MaterialTheme.shapes.extraLarge,
                border         = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.outline
                ),
                colors         = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer
                                     else androidx.compose.ui.graphics.Color.Transparent
                ),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                modifier       = Modifier.height(32.dp)
            ) {
                Text(text = chipLabel, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
private fun ActivityChoiceTile(
    emoji: String,
    title: String,
    background: androidx.compose.ui.graphics.Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    EmotionPrimaryButton(
        text    = "$emoji  $title",
        onClick = onClick,
        modifier = modifier.heightIn(min = 80.dp)
    )
}

// ---------------------------------------------------------------------------
// Breathing activity — TTS-guided, auto-exits after 60 s
// ---------------------------------------------------------------------------

@Composable
private fun BreathingScreen(
    onStop: () -> Unit,
    cycles: Int = 10,
    modifier: Modifier = Modifier
) {
    val tts = rememberTtsPlayer()
    var phaseIsInhale by remember { mutableStateOf(true) }

    // cycles × (3 s inhale + 3 s exhale) total, then goodbye and exit
    LaunchedEffect(Unit) {
        delay(400)
        tts.speak("Hít vào")
        repeat(cycles) {
            delay(3_000)
            phaseIsInhale = false
            tts.speak("Thở ra")
            delay(3_000)
            phaseIsInhale = true
            tts.speak("Hít vào")
        }
        delay(3_000)
        phaseIsInhale = false
        tts.speak("Thở ra")
        delay(3_000)
        tts.speak("Tuyệt vời! Bé đã thở rất giỏi. Chúc bé luôn vui vẻ và bình tĩnh nhé!")
        delay(4_500)
        onStop()
    }

    val scale by animateFloatAsState(
        targetValue   = if (phaseIsInhale) 1.25f else 0.75f,
        animationSpec = tween(2800),
        label         = "scale"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier            = modifier
            .fillMaxSize()
            .padding(vertical = 32.dp)
    ) {
        Text(
            text      = "Thở cùng bóng 🫧",
            style     = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier         = Modifier
                .size(160.dp)
                .scale(scale)
                .clip(CircleShape)
                .background(EmotionCalm.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .background(EmotionCalm.copy(alpha = 0.65f))
            )
        }

        Text(
            text      = if (phaseIsInhale) "Hít vào..." else "Thở ra...",
            style     = MaterialTheme.typography.headlineMedium.copy(fontStyle = FontStyle.Italic),
            color     = EmotionCalm,
            textAlign = TextAlign.Center
        )

        Text(
            text      = "Hít vào 3 giây · Thở ra 3 giây",
            style     = MaterialTheme.typography.titleMedium,
            color     = OnSurfaceVar,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.weight(1f))

        EmotionPrimaryButton(text = "Dừng lại", onClick = onStop)
    }
}

// ---------------------------------------------------------------------------
// Music activity — simulated tracks with end-of-song buttons
// ---------------------------------------------------------------------------

private val relaxSongs = listOf(
    "Ánh Nắng Bình Yên",
    "Giọt Mưa Thu",
    "Tiếng Sóng Biển",
    "Gió Nhẹ Đồng Quê",
    "Suối Reo Sớm Mai",
    "Bầu Trời Xanh",
)

@Composable
private fun MusicScreen(
    onStop: () -> Unit,
    durationSecs: Int = 30,
    modifier: Modifier = Modifier
) {
    val tts = rememberTtsPlayer()
    var playIndex by remember { mutableIntStateOf(0) }
    var songEnded by remember { mutableStateOf(false) }
    val currentSong = remember(playIndex) { relaxSongs.random() }

    // Track duration determined by durationSecs parameter
    LaunchedEffect(playIndex) {
        songEnded = false
        delay(400)
        tts.speak("Đang phát bài $currentSong. Nhắm mắt và thở đều nhé bé ơi.")
        delay(durationSecs * 1_000L)
        songEnded = true
        tts.speak("Bài nhạc kết thúc rồi. Con muốn nghe thêm hay quay lại?")
    }

    val infiniteTransition = rememberInfiniteTransition(label = "musicPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue  = 0.45f,
        targetValue   = 1f,
        animationSpec = infiniteRepeatable(tween(1200), RepeatMode.Reverse),
        label         = "alpha"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier            = modifier
            .fillMaxSize()
            .padding(vertical = 32.dp)
    ) {
        Text(
            text      = "Nghe nhạc nhẹ 🎵",
            style     = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text     = "♪",
            fontSize = 96.sp,
            color    = MintGreen40.copy(alpha = if (songEnded) 1f else pulseAlpha)
        )

        if (songEnded) {
            Text(
                text      = "🎶 Bài nhạc kết thúc rồi!",
                style     = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center
            )
        } else {
            Text(
                text      = "🎵 $currentSong",
                style     = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
            Text(
                text      = "Nhắm mắt và thở đều nhé 😌",
                style     = MaterialTheme.typography.titleMedium,
                color     = OnSurfaceVar,
                textAlign = TextAlign.Center
            )
        }

        Spacer(Modifier.weight(1f))

        if (songEnded) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier              = Modifier.fillMaxWidth()
            ) {
                EmotionPrimaryButton(
                    text     = "⬅ Quay lại",
                    onClick  = onStop,
                    modifier = Modifier.weight(1f)
                )
                EmotionPrimaryButton(
                    text     = "🎵 Nghe thêm",
                    onClick  = { playIndex++ },
                    modifier = Modifier.weight(1f)
                )
            }
        } else {
            EmotionPrimaryButton(text = "Dừng lại", onClick = onStop)
        }
    }
}

// ---------------------------------------------------------------------------
// Puzzle activity — full screen, single task
// ---------------------------------------------------------------------------

@Composable
private fun PuzzleScreen(
    onStop: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tileColors = listOf(
        listOf(MintGreen80, SkyBlueLight),
        listOf(SkyBlueLight, MintGreen80)
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier            = modifier
            .fillMaxSize()
            .padding(vertical = 32.dp)
    ) {
        Text(
            text      = "Xếp hình vui 🧩",
            style     = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            tileColors.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { color ->
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(MaterialTheme.shapes.large)
                                .background(color)
                        )
                    }
                }
            }
        }

        Text(
            text      = "Trò chơi xếp hình sẽ sớm có nhé! 🌟",
            style     = MaterialTheme.typography.titleMedium,
            color     = OnSurfaceVar,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.weight(1f))

        EmotionPrimaryButton(text = "Quay lại", onClick = onStop)
    }
}

// ---------------------------------------------------------------------------
// Previews
// ---------------------------------------------------------------------------

@Preview(showBackground = true, showSystemUi = true)
@Composable
private fun RelaxScreenPreview() {
    EmotionFriendTheme {
        RelaxScreen(onBack = {})
    }
}

@Preview(showBackground = true)
@Composable
private fun BreathingScreenPreview() {
    EmotionFriendTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            BreathingScreen(onStop = {})
        }
    }
}


