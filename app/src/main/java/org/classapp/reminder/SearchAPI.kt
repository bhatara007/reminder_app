package org.classapp.reminder

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object SearchAPI {
    var retrofit: Retrofit = Retrofit.Builder()
        .baseUrl("https://search.longdo.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val service: SearchService = retrofit.create(SearchService::class.java)
}