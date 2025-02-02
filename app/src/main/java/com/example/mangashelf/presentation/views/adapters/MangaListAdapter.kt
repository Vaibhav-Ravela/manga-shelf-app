package com.example.mangashelf.presentation.views.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.example.mangashelf.R
import com.example.mangashelf.databinding.MangaGroupsHeadingLayoutBinding
import com.example.mangashelf.databinding.MangaItemLayoutBinding
import com.example.mangashelf.domain.models.MangaItem
import com.example.mangashelf.presentation.viewModels.HomeScreenViewModel
import com.example.mangashelf.presentation.views.activities.MangaDetailedActivity
import com.example.mangashelf.utils.TimeUtils

class MangaListAdapter(
    private val context: Context,
    private val adapterList: List<Any>,
    private val homeScreenViewModel: HomeScreenViewModel,
    private val activityResultLauncher: ActivityResultLauncher<Intent>
) : RecyclerView.Adapter<ViewHolder>() {
    companion object {
        const val POSITION = "position"
    }

    private val VIEW_TYPE_HEADING = 0
    private val VIEW_TYPE_ITEM = 1

    override fun getItemViewType(position: Int): Int =
        if (adapterList[position] is Int) VIEW_TYPE_HEADING else VIEW_TYPE_ITEM

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        if (viewType == VIEW_TYPE_HEADING) MangaHeadingViewHolder(
            MangaGroupsHeadingLayoutBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )
        else MangaItemViewHolder(
            MangaItemLayoutBinding.inflate(
                LayoutInflater.from(context), parent, false
            )
        )

    override fun getItemCount(): Int = adapterList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (holder is MangaHeadingViewHolder) holder.mangaGroupsHeadingLayoutBinding.yearOfPublication.text =
            (adapterList[position] as Int).toString()
        else {
            (holder as MangaItemViewHolder).apply {
                mangaItemLayoutBinding.apply {
                    val mangaItem = adapterList[position] as MangaItem
                    coverImage.load(mangaItem.image) {
                        crossfade(true)
                    }
                    title.text = mangaItem.title
                    score.text = context.getString(R.string.manga_score, mangaItem.score)
                    popularity.text = context.getString(R.string.popularity, mangaItem.popularity)
                    yearOfPublication.text = context.getString(
                        R.string.year_of_publication,
                        TimeUtils.convertUnixToYear(mangaItem.publishedChapterDate)
                    )
                    if (mangaItem.isFavorite) favoriteIcon.setImageResource(R.drawable.favorite)
                    else favoriteIcon.setImageResource(R.drawable.not_favorite)
                    if (mangaItem.isRead) readIcon.setImageResource(R.drawable.read)
                    else readIcon.setImageResource(R.drawable.not_read)
                    favorite.setOnClickListener {
                        mangaItem.isFavorite = !mangaItem.isFavorite
                        if (mangaItem.isFavorite) favoriteIcon.setImageResource(R.drawable.favorite)
                        else favoriteIcon.setImageResource(R.drawable.not_favorite)
                        homeScreenViewModel.updateFavoriteStatus(mangaItem.id, mangaItem.isFavorite)
                    }
                    read.setOnClickListener {
                        mangaItem.isRead = !mangaItem.isRead
                        if (mangaItem.isRead) readIcon.setImageResource(R.drawable.read)
                        else readIcon.setImageResource(R.drawable.not_read)
                        homeScreenViewModel.updateReadStatus(mangaItem.id, mangaItem.isRead)
                    }
                    root.setOnClickListener {
                        activityResultLauncher.launch(Intent(
                            holder.itemView.context, MangaDetailedActivity::class.java
                        ).apply {
                            putExtra(MangaDetailedActivity.MANGA_ITEM, mangaItem)
                            putExtra(POSITION, position)
                        })
                    }
                }
            }
        }
    }

}

class MangaItemViewHolder(val mangaItemLayoutBinding: MangaItemLayoutBinding) :
    ViewHolder(mangaItemLayoutBinding.root)

class MangaHeadingViewHolder(val mangaGroupsHeadingLayoutBinding: MangaGroupsHeadingLayoutBinding) :
    ViewHolder(mangaGroupsHeadingLayoutBinding.root)