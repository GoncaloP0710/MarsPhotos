/*
 * Copyright (C) 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.marsphotos.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

import com.example.marsphotos.ui.theme.MarsPhotosTheme

import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.marsphotos.model.MarsPhoto
import com.example.marsphotos.model.PicsumPhoto
import com.example.marsphotos.network.MarsApi
import com.example.marsphotos.ui.MarsPhotosApp
import com.example.marsphotos.ui.PicsumPhotosApp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener

@Composable
fun HomeScreen(
    marsUiState: MarsUiState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    when (marsUiState) {
        is MarsUiState.Loading -> LoadingScreen(modifier = modifier.fillMaxSize())

        /*
        is MarsUiState.Success -> ResultScreen(
            marsUiState.photos,marsUiState.randomPhoto, modifier = modifier.fillMaxWidth()
        )
        */
        is MarsUiState.Success -> 0

        is MarsUiState.Error -> ErrorScreen( modifier = modifier.fillMaxSize())
    }
}

/**
 * The home screen displaying the loading message.
 */
@Composable
fun LoadingScreen(modifier: Modifier = Modifier) {

}

/**
 * The home screen displaying error message with re-attempt button.
 */
@Composable
fun ErrorScreen(modifier: Modifier = Modifier) {

}

@Preview(showBackground = true)
@Composable
fun LoadingScreenPreview() {
    MarsPhotosTheme {
        LoadingScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    MarsPhotosTheme {
        ErrorScreen()
    }
}

@Composable
fun PicsumHomeScreen(
    picsumUiState: PicsumUiState,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp),
) {
    when (picsumUiState) {
        is PicsumUiState.Loading -> PicsumLoadingScreen(modifier = modifier.fillMaxSize())

        /*
        is PicsumUiState.Success -> PicsumResultScreen(
            picsumUiState.photos,picsumUiState.randomPhoto, modifier = modifier.fillMaxWidth()
        )
        */
        is PicsumUiState.Success -> 0

        is PicsumUiState.Error -> PicsumErrorScreen( modifier = modifier.fillMaxSize())
    }
}

/**
 * The home screen displaying the loading message.
 */
@Composable
fun PicsumLoadingScreen(modifier: Modifier = Modifier) {

}

/**
 * The home screen displaying error message with re-attempt button.
 */
@Composable
fun PicsumErrorScreen(modifier: Modifier = Modifier) {

}

@Preview(showBackground = true)
@Composable
fun PicsumLoadingScreenPreview() {
    MarsPhotosTheme {
        PicsumLoadingScreen()
    }
}

@Preview(showBackground = true)
@Composable
fun PicsumErrorScreenPreview() {
    MarsPhotosTheme {
        PicsumErrorScreen()
    }
}

@Composable
fun ResultScreen(photos: String, randomPhoto: MarsPhoto, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize() // Ensure the column fills the available space
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Use available space for the image
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(randomPhoto.imgSrc)
                    .crossfade(true)
                    .build(),
                contentDescription = "A photo",
                modifier = Modifier.fillMaxSize(), // Ensure the image fills the Box
                contentScale = ContentScale.Crop // Scale the image to fill the Box
            )
        }
    }
}

@Composable
fun PicsumResultScreen(blurr: Boolean, gray: Boolean, photos: List<PicsumPhoto>, randomPhoto: PicsumPhoto, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize() // Ensure the column fills the available space
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Use available space for the image
        ) {
            val imageUrl = when {
                blurr && gray -> "${randomPhoto.downloadUrl}/?blur=10&grayscale"
                blurr -> "${randomPhoto.downloadUrl}/?blur=10"
                gray -> "${randomPhoto.downloadUrl}/?grayscale"
                else -> randomPhoto.downloadUrl
            }
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "A photo",
                modifier = Modifier.fillMaxSize(), // Ensure the image fills the Box
                contentScale = ContentScale.Crop // Scale the image to fill the Box
            )
        }
    }
}

// -------------------------------------------------------------------------------------

@Composable
fun CombinedImageScreen(
    marsViewModel: MarsViewModel,
    picsumViewModel: PicsumViewModel,
    db: FirebaseDatabase,
    imageRef: DatabaseReference,
    modifier: Modifier = Modifier
) {
    var picsumUiState = picsumViewModel.picsumUiState
    var marsUiState = marsViewModel.marsUiState

    // State to hold roll count
    var rollCount by remember { mutableStateOf(0) }

    // Function to retrieve the roll count from Firebase
    fun getRollCount(onRollCountRetrieved: (Int) -> Unit) {
        imageRef.child("rolls").get()
            .addOnSuccessListener { snapshot ->
                val currentRolls = snapshot.getValue(Int::class.java) ?: 0
                onRollCountRetrieved(currentRolls)
            }
            .addOnFailureListener { exception ->
                exception.printStackTrace() // Handle error if needed
                onRollCountRetrieved(0) // Return 0 if there was an error
            }
    }

    // Function to increment the roll count in Firebase
    fun incrementRollCount() {
        imageRef.child("rolls").runTransaction(object : Transaction.Handler {
            override fun doTransaction(mutableData: MutableData): Transaction.Result {
                val currentCount = mutableData.getValue(Int::class.java) ?: 0
                mutableData.value = currentCount + 1 // Increment the roll count
                return Transaction.success(mutableData)
            }

            override fun onComplete(
                databaseError: DatabaseError?,
                committed: Boolean,
                dataSnapshot: DataSnapshot?
            ) {
                if (databaseError != null) {
                    databaseError.toException().printStackTrace() // Handle error if needed
                }
            }
        })
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // Retrieve the roll count from Firebase
        getRollCount { count ->
            rollCount = count // Update the roll count when retrieved
        }

        var roll by remember { mutableStateOf(false) }
        var blurr by remember { mutableStateOf(false) }
        var gray by remember { mutableStateOf(false) }

        var save by remember { mutableStateOf(false) }
        var load by remember { mutableStateOf(false) }

        // Function to add image URL to Firebase
        fun addImageUrlToDatabase(url: String) {
            val imageEntry = mapOf(
                "url" to url,
                "timestamp" to System.currentTimeMillis()
            )
            imageRef.push().setValue(imageEntry)
                .addOnSuccessListener { /* Handle success if needed */ }
                .addOnFailureListener { /* Handle error if needed */ }
        }


        // Function to load the last image URL from Firebase
        fun loadLastImageUrl(onUrlLoaded: (String?) -> Unit) {
            imageRef.orderByChild("timestamp").limitToLast(1).get()
                .addOnSuccessListener { snapshot ->
                    val lastImageUrl = snapshot.children.firstOrNull()?.child("url")?.getValue(String::class.java)
                    onUrlLoaded(lastImageUrl)
                }
                .addOnFailureListener { exception ->
                    // Handle error if needed
                    onUrlLoaded(null) // Pass null in case of error
                    exception.printStackTrace() // Log the error for debugging
                }
        }



        // Display the Picsum photo
        if (picsumUiState is PicsumUiState.Success) {
            if (roll) {
                picsumUiState.randomPhoto = picsumUiState.photos.random()
                roll = false
                blurr = false
                gray = false
                // Increment the roll count in the database
                incrementRollCount() // Update local roll count is handled in incrementRollCount
            }
            if (save) {
                val url = when {
                    blurr && gray -> "${picsumUiState.randomPhoto.downloadUrl}/?blur=10&grayscale"
                    blurr -> "${picsumUiState.randomPhoto.downloadUrl}/?blur=10"
                    gray -> "${picsumUiState.randomPhoto.downloadUrl}/?grayscale"
                    else -> picsumUiState.randomPhoto.downloadUrl
                }
                // var url = picsumUiState.randomPhoto.downloadUrl
                // Save the new random photo URL to Firebase
                picsumUiState.randomPhoto?.let { addImageUrlToDatabase(url) }
                save = false
            }
            if (load) {
                loadLastImageUrl { lastImageUrl ->
                    lastImageUrl?.let { url ->
                        // Assuming `randomPhoto` is mutable and has a `downloadUrl` field
                        picsumUiState.randomPhoto?.downloadUrl = url
                        println("Loaded last image URL: $url") // Debugging print to confirm URL
                    }
                    load = false // Reset the load flag
                }
            }



            fun loadLastImageUrl(onUrlLoaded: (String?) -> Unit) {
                imageRef.orderByChild("timestamp").limitToLast(1).get()
                    .addOnSuccessListener { snapshot ->
                        val lastImageUrl = snapshot.children.firstOrNull()?.child("url")?.getValue(String::class.java)
                        onUrlLoaded(lastImageUrl)
                    }
                    .addOnFailureListener { /* Handle error if needed */ }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Let it take up half the screen height
            ) {
                PicsumResultScreen(
                    blurr = blurr,
                    gray = gray,
                    photos = picsumUiState.photos,
                    randomPhoto = picsumUiState.randomPhoto,
                    modifier = Modifier.fillMaxSize() // Ensure it fills the Box
                )
            }
        }

        // Display the Mars photo
        if (marsUiState is MarsUiState.Success) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f) // Let it take up half the screen height
            ) {
                ResultScreen(
                    photos = marsUiState.photos,
                    randomPhoto = marsUiState.randomPhoto,
                    modifier = Modifier.fillMaxSize() // Ensure it fills the Box
                )
            }
        }

        // Buttons section
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Add padding for spacing
            horizontalArrangement = Arrangement.SpaceEvenly // Space buttons evenly
        ) {
            Button(onClick = { roll = true }) {
                Text(text = "Roll")
            }
            Button(onClick = { blurr = true }) {
                Text(text = "Blur")
            }
            Button(onClick = { gray = true }) {
                Text(text = "Gray")
            }
            Button(onClick = { save = true }) {
                Text(text = "Save")
            }
            Button(onClick = { load = true }) {
                Text(text = "Load")
            }
            // Display the roll count
            Text(text = "Rolls: $rollCount", modifier = Modifier.padding(start = 8.dp))
        }
    }
}

// -------------------------------------------------------------------------------------


