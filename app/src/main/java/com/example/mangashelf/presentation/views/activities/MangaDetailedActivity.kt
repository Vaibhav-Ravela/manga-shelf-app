package com.example.mangashelf.presentation.views.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.mangashelf.R
import com.example.mangashelf.data.local.repositories.MangaDBRepository
import com.example.mangashelf.databinding.MangaDetailedScreenBinding
import com.example.mangashelf.domain.models.MangaItem
import com.example.mangashelf.presentation.views.adapters.MangaListAdapter
import com.example.mangashelf.utils.TimeUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@AndroidEntryPoint
class MangaDetailedActivity : AppCompatActivity() {
    companion object {
        const val MANGA_ITEM = "manga_item"
    }

    @Inject
    lateinit var mangaDBRepository: MangaDBRepository
    private var mangaItem: MangaItem? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mangaDetailedScreenBinding = MangaDetailedScreenBinding.inflate(layoutInflater)
        setContentView(mangaDetailedScreenBinding.root)
        savedInstanceState?.let { mangaItem = it.getParcelable(MANGA_ITEM) } ?: run {
            mangaItem = intent.extras?.getParcelable(MANGA_ITEM)
        }
        mangaItem?.let { mangaItem ->
            mangaDetailedScreenBinding.apply {
                mangaHeading.text = mangaItem.title
                coverImage.load(mangaItem.image) {
                    crossfade(true)
                }
                score.text = mangaItem.score.toString()
                popularity.text = mangaItem.popularity.toString()
                publishedChapterDate.text =
                    TimeUtils.convertUnixToReadableDate(mangaItem.publishedChapterDate)
                category.text = mangaItem.category
                if (mangaItem.isFavorite) {
                    favoriteIcon.setImageResource(R.drawable.favorite)
                    favoriteText.text = getString(R.string.favorite_text)
                } else {
                    favoriteIcon.setImageResource(R.drawable.not_favorite)
                    favoriteText.text = getString(R.string.unfavorite_text)
                }
                if (mangaItem.isRead) {
                    readIcon.setImageResource(R.drawable.read)
                    readText.text = getString(R.string.read_text)
                } else {
                    readIcon.setImageResource(R.drawable.not_read)
                    readText.text = getString(R.string.unread_text)
                }
                favorite.setOnClickListener {
                    mangaItem.isFavorite = !mangaItem.isFavorite
                    if (mangaItem.isFavorite) {
                        favoriteIcon.setImageResource(R.drawable.favorite)
                        favoriteText.text = getString(R.string.favorite_text)
                    } else {
                        favoriteIcon.setImageResource(R.drawable.not_favorite)
                        favoriteText.text = getString(R.string.unfavorite_text)
                    }
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            mangaDBRepository.updateMangaItemFavorite(
                                mangaItem.id, mangaItem.isFavorite
                            )
                        }
                    }
                    setResult(RESULT_OK, Intent().apply {
                        putExtra(MANGA_ITEM, mangaItem)
                        putExtra(
                            MangaListAdapter.POSITION,
                            intent.extras?.getInt(MangaListAdapter.POSITION)
                        )
                    })
                }
                read.setOnClickListener {
                    mangaItem.isRead = !mangaItem.isRead
                    if (mangaItem.isRead) {
                        readIcon.setImageResource(R.drawable.read)
                        readText.text = getString(R.string.read_text)
                    } else {
                        readIcon.setImageResource(R.drawable.not_read)
                        readText.text = getString(R.string.unread_text)
                    }
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            mangaDBRepository.updateMangaItemRead(
                                mangaItem.id, mangaItem.isRead
                            )
                        }
                    }
                    setResult(RESULT_OK, Intent().apply {
                        putExtra(MANGA_ITEM, mangaItem)
                        putExtra(
                            MangaListAdapter.POSITION,
                            intent.extras?.getInt(MangaListAdapter.POSITION)
                        )
                    })
                }
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(MANGA_ITEM, mangaItem)
    }
}
