package com.example.mangashelf.presentation.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil.load
import com.example.mangashelf.R
import com.example.mangashelf.databinding.MangaDetailedScreenBinding
import com.example.mangashelf.domain.models.MangaItem
import com.example.mangashelf.presentation.viewModels.HomeScreenViewModel
import com.example.mangashelf.presentation.views.adapters.MangaListAdapter
import com.example.mangashelf.utils.TimeUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MangaDetailedActivity : AppCompatActivity() {
    companion object {
        const val MANGA_ITEM = "manga_item"
    }

    private lateinit var homeScreenViewModel: HomeScreenViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mangaDetailedScreenBinding = MangaDetailedScreenBinding.inflate(layoutInflater)
        setContentView(mangaDetailedScreenBinding.root)
        homeScreenViewModel = ViewModelProvider(this)[HomeScreenViewModel::class.java]
        intent.extras?.let { extras ->
            (extras.getParcelable(MANGA_ITEM) as? MangaItem)?.let { mangaItem ->
                mangaDetailedScreenBinding.apply {
                    mangaHeading.text = mangaItem.title
                    coverImage.load(mangaItem.image) {
                        crossfade(true)
                    }
                    score.text = getString(R.string.manga_score, mangaItem.score)
                    popularity.text = getString(R.string.popularity, mangaItem.popularity)
                    publishedChapterDate.text = getString(
                        R.string.publication_date,
                        TimeUtils.convertUnixToReadableDate(mangaItem.publishedChapterDate)
                    )
                    category.text = getString(R.string.category, mangaItem.category)
                    if (mangaItem.isFavorite) favoriteIcon.setImageResource(R.drawable.favorite)
                    else favoriteIcon.setImageResource(R.drawable.not_favorite)
                    if (mangaItem.isRead) readIcon.setImageResource(R.drawable.read)
                    else readIcon.setImageResource(R.drawable.not_read)
                    favorite.setOnClickListener {
                        mangaItem.isFavorite = !mangaItem.isFavorite
                        if (mangaItem.isFavorite) favoriteIcon.setImageResource(R.drawable.favorite)
                        else favoriteIcon.setImageResource(R.drawable.not_favorite)
                        homeScreenViewModel.updateFavoriteStatus(mangaItem.id, mangaItem.isFavorite)
                        setResult(RESULT_OK, Intent().apply {
                            putExtra(MANGA_ITEM, mangaItem)
                            putExtra(MangaListAdapter.POSITION, extras.getInt(MangaListAdapter.POSITION))
                        })
                    }
                    read.setOnClickListener {
                        mangaItem.isRead = !mangaItem.isRead
                        if (mangaItem.isRead) readIcon.setImageResource(R.drawable.read)
                        else readIcon.setImageResource(R.drawable.not_read)
                        homeScreenViewModel.updateReadStatus(mangaItem.id, mangaItem.isRead)
                        setResult(RESULT_OK, Intent().apply {
                            putExtra(MANGA_ITEM, mangaItem)
                            putExtra(MangaListAdapter.POSITION, extras.getInt(MangaListAdapter.POSITION))
                        })
                    }
                }
            }
        }
    }
}