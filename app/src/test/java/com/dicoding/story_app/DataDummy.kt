package com.dicoding.story_app

import androidx.paging.PagingData
import com.dicoding.story_app.data.response.Story
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

object DataDummy {
    fun generateDummyStories(): List<Story> {
        val stories = ArrayList<Story>()
        for (i in 0..10) {
            val story = Story(
                id = "story-$i",
                name = "name $i",
                description = "description $i",
                photoUrl = "photoUrl $i",
                createdAt = "createdAt $i",
                lat = -6.200000,
                lon = 106.816666
            )
            stories.add(story)
        }
        return stories
    }

    fun generatePagingData(): Flow<PagingData<Story>> {
        return flowOf(PagingData.from(generateDummyStories()))
    }
}