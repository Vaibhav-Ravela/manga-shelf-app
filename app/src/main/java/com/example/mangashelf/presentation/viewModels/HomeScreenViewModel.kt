package com.example.mangashelf.presentation.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangashelf.data.local.repositories.MangaDBRepository
import com.example.mangashelf.data.remote.repositories.JsonKeeperRepository
import com.example.mangashelf.domain.models.CurrentSortOption
import com.example.mangashelf.domain.models.MangaItem
import com.example.mangashelf.utils.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val jsonKeeperRepository: JsonKeeperRepository,
    private val mangaDBRepository: MangaDBRepository
) : ViewModel() {
    val mangaListLiveData = MutableLiveData<List<MangaItem>>()
    var currentSortOption = CurrentSortOption.PUBLICATION_YEAR
    val yearToIndexMap = HashMap<Int, Int>()
    val yearSortedAdapterList = ArrayList<Any>()

    fun getMangaList() {
        viewModelScope.launch {
            mangaListLiveData.value = withContext(Dispatchers.IO) {
                try {
                    jsonKeeperRepository.getMangaList()
                } catch (e: Exception) {
                    Log.e("HomeScreenViewModel", "Exception: ${e.printStackTrace()}")
                    mangaDBRepository.getAllMangaItems()
                }
            }
        }
    }

    fun updateFavoriteStatus(id: String, isFavorite: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    mangaDBRepository.updateMangaItemFavorite(id, isFavorite)
                } catch (e: Exception) {
                    Log.e("HomeScreenViewModel", "Exception: ${e.printStackTrace()}")
                }
            }
        }
    }

    fun updateReadStatus(id: String, isRead: Boolean) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    mangaDBRepository.updateMangaItemRead(id, isRead)
                } catch (e: Exception) {
                    Log.e("HomeScreenViewModel", "Exception: ${e.printStackTrace()}")
                }
            }
        }
    }

    fun readAdapterList(mangaItemList: List<MangaItem>) {
        yearSortedAdapterList.clear()
        yearToIndexMap.clear()
        var prevYear = TimeUtils.convertUnixToYear(mangaItemList[0].publishedChapterDate)
        yearSortedAdapterList.add(prevYear.toInt())
        yearToIndexMap[prevYear.toInt()] = yearSortedAdapterList.size - 1
        yearSortedAdapterList.add(mangaItemList[0])
        for (i in 1..<mangaItemList.size) {
            val currentYear = TimeUtils.convertUnixToYear(mangaItemList[i].publishedChapterDate)
            if (prevYear != currentYear) {
                prevYear = currentYear
                yearSortedAdapterList.add(prevYear.toInt())
                yearToIndexMap[prevYear.toInt()] = yearSortedAdapterList.size - 1
            }
            yearSortedAdapterList.add(mangaItemList[i])
        }
    }
}