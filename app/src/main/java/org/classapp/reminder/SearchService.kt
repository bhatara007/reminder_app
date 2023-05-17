package org.classapp.reminder

import org.classapp.reminder.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchService {

    @GET("mapsearch/json/search")
    suspend fun search(
        @Query("keyword") searchQuery: String = "bangkok",
        @Query("limit") limit: Int = 10,
        @Query("key") keyword: String = BuildConfig.SEARCH_API_KEY
    ): SearchResponse
}