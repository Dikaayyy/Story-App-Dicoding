package com.dicoding.story_app.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListUpdateCallback
import com.dicoding.story_app.DataDummy
import com.dicoding.story_app.MainDispatcherRule
import com.dicoding.story_app.data.repository.UserRepository
import com.dicoding.story_app.data.response.Story
import com.dicoding.story_app.getOrAwaitValue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@OptIn(ExperimentalCoroutinesApi::class)
class StoryViewModelTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var userRepository: UserRepository
    private lateinit var storyViewModel: StoryViewModel

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        userRepository = Mockito.mock(UserRepository::class.java)
        storyViewModel = StoryViewModel(userRepository, TestScope())
    }

    @Test
    fun `when Get Stories Should Not Null and Return Data`() = runTest {
        val dummyStories = DataDummy.generatePagingData()
        Mockito.`when`(userRepository.getAllStory()).thenReturn(dummyStories)

        val actualStories = storyViewModel.stories.first()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryDiffCallback(),
            updateCallback = NoopListUpdateCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)


        // Check if the snapshot is not null
        assertNotNull(differ.snapshot())
        assertNotNull(actualStories)

        assertEquals(DataDummy.generateDummyStories().size, differ.snapshot().size)
        assertEquals(DataDummy.generateDummyStories()[0], differ.snapshot()[0])
    }

    @Test
    fun `when No Stories Should Return Empty Data`() = runTest {
        val emptyPagingData: Flow<PagingData<Story>> = flowOf(PagingData.empty())
        Mockito.`when`(userRepository.getAllStory()).thenReturn(emptyPagingData)

        val actualStories: PagingData<Story> = storyViewModel.stories.first()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryDiffCallback(),
            updateCallback = NoopListUpdateCallback(),
            workerDispatcher = Dispatchers.Main
        )
        differ.submitData(actualStories)

        // Check if the snapshot is not null
        assertNotNull(differ.snapshot())
        assertNotNull(actualStories)

        assertEquals(0, differ.snapshot().size)
    }
}

class StoryDiffCallback : DiffUtil.ItemCallback<Story>() {
    override fun areItemsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Story, newItem: Story): Boolean {
        return oldItem == newItem
    }
}

class NoopListUpdateCallback : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {}
    override fun onRemoved(position: Int, count: Int) {}
    override fun onMoved(fromPosition: Int, toPosition: Int) {}
    override fun onChanged(position: Int, count: Int, payload: Any?) {}
}