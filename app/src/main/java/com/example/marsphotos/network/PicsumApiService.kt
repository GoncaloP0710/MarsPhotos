package com.example.marsphotos.network

import com.example.marsphotos.model.MarsPhoto
import com.example.marsphotos.model.PicsumPhoto
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path

private const val BASE_URL = "https://picsum.photos"

// Configure Json to ignore unknown keys
private val json = Json {
    ignoreUnknownKeys = true
}

private val retrofit = Retrofit.Builder()
    .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

interface PicsumApiService {
    @GET("v2/list?limit=100")
    suspend fun getPhotos(): List<PicsumPhoto>
}

object PicsumApi {
    val retrofitService: PicsumApiService by lazy {
        retrofit.create(PicsumApiService::class.java)
    }
}