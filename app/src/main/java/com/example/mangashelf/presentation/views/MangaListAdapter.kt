package com.example.mangashelf.presentation.views

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.mangashelf.databinding.MangaItemLayoutBinding
import com.example.mangashelf.domain.models.MangaItem
import com.example.mangashelf.utils.TimeUtils

class MangaListAdapter(
    private val context: Context, private val mangaItemList: List<MangaItem>
) : RecyclerView.Adapter<MangaViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaViewHolder =
        MangaViewHolder(MangaItemLayoutBinding.inflate(LayoutInflater.from(context), parent, false))

    override fun getItemCount(): Int = mangaItemList.size

    override fun onBindViewHolder(holder: MangaViewHolder, position: Int) {
        holder.mangaItemLayoutBinding.apply {
            title.text = mangaItemList[position].title
            score.text = "Score: ${mangaItemList[position].score}"
            popularity.text = "Popularity: ${mangaItemList[position].popularity}"
            yearOfPublication.text = "Year of Publication: ${TimeUtils.convertUnixToYear(mangaItemList[position].publishedChapterDate)}"
            coverImage.load(mangaItemList[position].image) {
                crossfade(true)
            }
        }
    }

}

class MangaViewHolder(val mangaItemLayoutBinding: MangaItemLayoutBinding) :
    RecyclerView.ViewHolder(mangaItemLayoutBinding.root)