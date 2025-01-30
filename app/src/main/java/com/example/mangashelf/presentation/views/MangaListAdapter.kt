package com.example.mangashelf.presentation.views

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import coil.load
import com.example.mangashelf.R
import com.example.mangashelf.databinding.MangaGroupsHeadingLayoutBinding
import com.example.mangashelf.databinding.MangaItemLayoutBinding
import com.example.mangashelf.domain.models.MangaItem
import com.example.mangashelf.utils.TimeUtils

class MangaListAdapter(
    private val context: Context, private val adapterList: List<Any>
) : RecyclerView.Adapter<ViewHolder>() {
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
                    title.text = mangaItem.title
                    score.text = context.getString(R.string.manga_score, mangaItem.score)
                    popularity.text = context.getString(R.string.popularity, mangaItem.popularity)
                    yearOfPublication.text = context.getString(
                        R.string.year_of_publication,
                        TimeUtils.convertUnixToYear(mangaItem.publishedChapterDate)
                    )
                    coverImage.load(mangaItem.image) {
                        crossfade(true)
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