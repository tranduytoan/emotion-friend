package com.emotionfriend.feature.relax

import com.emotionfriend.data.repository.MusicRepository
import com.emotionfriend.domain.model.Music
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RelaxViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()

    private val testMusicTracks = listOf(
        Music(
            id = "1",
            title = "Calm Ocean Waves",
            artist = "Nature Sounds",
            duration = 600,
            category = "nature",
            filePath = "ocean_waves.mp3"
        ),
        Music(
            id = "2",
            title = "Forest Birds",
            artist = "Nature Sounds",
            duration = 480,
            category = "nature",
            filePath = "forest_birds.mp3"
        ),
        Music(
            id = "3",
            title = "Meditation Ambient",
            artist = "Calm Music",
            duration = 900,
            category = "meditation",
            filePath = "meditation.mp3"
        ),
    )

    private lateinit var musicRepo: FakeMusicRepository
    private lateinit var viewModel: RelaxViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        musicRepo = FakeMusicRepository(testMusicTracks)
        viewModel = RelaxViewModel(musicRepo)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `loads music tracks on init`() = runTest {
        val tracks = viewModel.musicTracks.value
        assertEquals(3, tracks.size)
    }

    @Test
    fun `can select and play music track`() = runTest {
        viewModel.selectTrack(testMusicTracks[0])
        viewModel.play()

        val state = viewModel.playerState.value
        assertNotNull("Current track should be set", state.currentTrack)
        assertTrue("Player should be playing", state.isPlaying)
    }

    @Test
    fun `pause stops playback`() = runTest {
        viewModel.selectTrack(testMusicTracks[0])
        viewModel.play()
        var state = viewModel.playerState.value
        assertTrue("Should be playing", state.isPlaying)

        viewModel.pause()
        state = viewModel.playerState.value
        assertFalse("Should be paused", state.isPlaying)
    }

    @Test
    fun `can skip to next track`() = runTest {
        viewModel.selectTrack(testMusicTracks[0])
        viewModel.play()

        viewModel.next()

        val state = viewModel.playerState.value
        assertNotNull("Should have next track", state.currentTrack)
    }

    @Test
    fun `can skip to previous track`() = runTest {
        viewModel.selectTrack(testMusicTracks[2])
        viewModel.play()
        viewModel.previous()

        val state = viewModel.playerState.value
        assertNotNull("Should have previous track", state.currentTrack)
    }

    @Test
    fun `playback progress is tracked`() = runTest {
        viewModel.selectTrack(testMusicTracks[0])
        viewModel.play()
        viewModel.setProgress(0.5f)

        val progress = viewModel.playerState.value.progress
        assertEquals(0.5f, progress, 0.01f)
    }

    @Test
    fun `can filter tracks by category`() = runTest {
        val natureTracks = viewModel.filterByCategory("nature")
        assertEquals(2, natureTracks.size)
    }

    @Test
    fun `duration is displayed correctly`() = runTest {
        viewModel.selectTrack(testMusicTracks[0])

        val duration = viewModel.playerState.value.currentTrack?.duration
        assertEquals(600, duration)
    }

    @Test
    fun `favorites list can be managed`() = runTest {
        viewModel.addToFavorites(testMusicTracks[0])

        assertTrue("Should be in favorites", viewModel.favorites.value.contains(testMusicTracks[0]))

        viewModel.removeFromFavorites(testMusicTracks[0])
        assertFalse("Should not be in favorites", viewModel.favorites.value.contains(testMusicTracks[0]))
    }

    @Test
    fun `can set volume level`() = runTest {
        viewModel.setVolume(0.7f)

        assertEquals(0.7f, viewModel.playerState.value.volume, 0.01f)
    }
}

data class PlayerState(
    val currentTrack: Music? = null,
    val isPlaying: Boolean = false,
    val progress: Float = 0f,
    val volume: Float = 1f,
)

private class FakeMusicRepository(
    tracks: List<Music>
) : MusicRepository {
    private val tracksFlow = MutableStateFlow(tracks)

    override fun getAll(): Flow<List<Music>> = tracksFlow
    override suspend fun getById(id: String): Music? = tracksFlow.value.find { it.id == id }
    override suspend fun upsertAll(music: List<Music>) { tracksFlow.value = music }
}
