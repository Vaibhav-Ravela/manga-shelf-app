package com.example.mangashelf.presentation.views.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.mangashelf.domain.models.MangaItem
import com.example.mangashelf.domain.models.ResultState
import com.example.mangashelf.presentation.viewModels.HomeScreenViewModel
import com.example.mangashelf.utils.TimeUtils
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.placeholder
import com.google.accompanist.placeholder.shimmer
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class HomeScreen : ComponentActivity() {
    private val homeScreenViewModel: HomeScreenViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeScreenUI(homeScreenViewModel)
        }
    }
}


@Composable
fun HomeScreenUI(homeScreenViewModel: HomeScreenViewModel) {
    val mangaListStatus by homeScreenViewModel.mangaListStateFlow.collectAsState()
    LaunchedEffect(Unit) {
        homeScreenViewModel.getMangaList()
    }
    MangaItemList(LocalContext.current, mangaListStatus = mangaListStatus)
}

@Composable
fun MangaItemList(
    context: Context, mangaListStatus: ResultState<List<MangaItem>> = ResultState.Success(
        listOf(
            MangaItem(
                "4e70e91ac092255ef70016d6",
                "https://cdn.myanimelist.net/images/anime/6/73245.jpg",
                16.5.toFloat(),
                165588,
                "Neon Genesis Evangelion: Shinji Ikari Raising Project",
                1275542373,
                "Comedy",
                isFavorite = false,
                isRead = false
            ), MangaItem(
                "4e70e91ac092255ef70016d6",
                "https://cdn.myanimelist.net/images/anime/6/73245.jpg",
                16.5.toFloat(),
                165588,
                "Neon Genesis Evangelion: Shinji Ikari Raising Project",
                1275542373,
                "Comedy",
                isFavorite = false,
                isRead = false
            ), MangaItem(
                "4e70e91ac092255ef70016d6",
                "https://cdn.myanimelist.net/images/anime/6/73245.jpg",
                16.5.toFloat(),
                165588,
                "Neon Genesis Evangelion: Shinji Ikari Raising Project",
                1275542373,
                "Comedy",
                isFavorite = false,
                isRead = false
            )
        )
    )
) {
    LazyColumn {
        if (mangaListStatus == ResultState.Loading) {
            items(3) {
                MangaItemLayout(mangaItem = null, context = context)
            }
        } else if (mangaListStatus is ResultState.Success) {
            items(mangaListStatus.data.size) { item ->
                MangaItemLayout(mangaItem = mangaListStatus.data[item], context = context)
            }
        }
    }
}

@Composable
fun MangaItemLayout(
    context: Context, mangaItem: MangaItem?
) {
    var isImageLoading by remember { mutableStateOf(true) }
    val painter = rememberAsyncImagePainter(model = mangaItem?.image, onState = { state ->
        if (state is coil.compose.AsyncImagePainter.State.Success) isImageLoading = false
    })
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(10.dp)
        .clickable {
            context.startActivity(Intent(context, DummyActivity::class.java))
        }) {
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(280.dp)
        ) {
            Image(
                painter = painter,
                contentDescription = "Manga Icon",
                modifier = Modifier
                    .fillMaxSize()
                    .align(Alignment.Center)
                    .placeholder(
                        visible = isImageLoading,
                        highlight = PlaceholderHighlight.shimmer(highlightColor = Color.Gray),
                        color = Color.White
                    )
            )
        }
        Column(
            verticalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.height(280.dp)
        ) {
            Text(
                text = mangaItem?.title ?: "",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = TextStyle(textDecoration = TextDecoration.Underline),
                fontSize = 15.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(10.dp)
            )
            Text(text = mangaItem?.score?.let { "Score: $it" } ?: "",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(10.dp))
            Text(text = mangaItem?.popularity?.let { "Popularity: $it" } ?: "",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 15.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(10.dp))
            Text(text = mangaItem?.publishedChapterDate?.let {
                "YOP: ${
                    TimeUtils.convertUnixToYear(
                        mangaItem.publishedChapterDate
                    )
                }"
            } ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(10.dp))
            Text(text = mangaItem?.category?.let { "Category: $it" } ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                fontFamily = FontFamily.Serif,
                modifier = Modifier.padding(10.dp))
        }
    }
}