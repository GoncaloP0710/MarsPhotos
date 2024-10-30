package com.example.marsphotos.ui.screens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.marsphotos.model.MarsPhoto
import com.example.marsphotos.model.PicsumPhoto
import com.example.marsphotos.network.PicsumApi
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/**
 * UI state for the Home screen
 */
sealed interface PicsumUiState {
    data class Success(val photos: List<PicsumPhoto>, var randomPhoto : PicsumPhoto) : PicsumUiState
    object Error : PicsumUiState
    object Loading : PicsumUiState
}

class PicsumViewModel : ViewModel() {
    /** The mutable State that stores the status of the most recent request */
    var picsumUiState: PicsumUiState by mutableStateOf(PicsumUiState.Loading)
        private set

    /**
     * Call getMarsPhotos() on init so we can display status immediately.
     */
    init {
        getPicsumPhotos()
    }

    /**
     * Gets Mars photos information from the Mars API Retrofit service and updates the
     * [MarsPhoto] [List] [MutableList].
     */
    fun getPicsumPhotos() {
        viewModelScope.launch {
            picsumUiState = PicsumUiState.Loading
            picsumUiState = try {
                val listResult = PicsumApi.retrofitService.getPhotos()
                PicsumUiState.Success(listResult, listResult.random())
            } catch (e: IOException) {
                PicsumUiState.Error
            } catch (e: HttpException) {
                PicsumUiState.Error
            }
        }
    }
}