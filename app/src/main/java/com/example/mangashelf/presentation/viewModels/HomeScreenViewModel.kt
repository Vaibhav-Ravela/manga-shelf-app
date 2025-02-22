package com.example.mangashelf.presentation.viewModels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangashelf.data.local.repositories.MangaDBRepository
import com.example.mangashelf.data.remote.repositories.JsonKeeperRepository
import com.example.mangashelf.domain.models.MangaItem
import com.example.mangashelf.domain.models.ResultState
import com.example.mangashelf.utils.TimeUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val jsonKeeperRepository: JsonKeeperRepository,
    private val mangaDBRepository: MangaDBRepository
) : ViewModel() {

    val mangaListStateFlow: MutableStateFlow<ResultState<List<MangaItem>>> =
        MutableStateFlow(ResultState.Loading)

    fun getMangaList() {
        viewModelScope.launch {
            mangaListStateFlow.value = withContext(Dispatchers.IO) {
                try {
                    ResultState.Success(jsonKeeperRepository.getMangaList())
                } catch (e: Exception) {
                    Log.e("HomeScreenViewModel", "Exception: ${e.printStackTrace()}")
                    val dbList = mangaDBRepository.getAllMangaItems()
                    if (dbList.isEmpty()) ResultState.Error("Something went wrong")
                    else ResultState.Success(dbList)
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
}