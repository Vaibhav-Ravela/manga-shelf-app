package com.example.mangashelf.presentation.viewModels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mangashelf.data.local.repositories.MangaDBRepository
import com.example.mangashelf.data.remote.repositories.JsonKeeperRepository
import com.example.mangashelf.domain.models.MangaItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val jsonKeeperRepository: JsonKeeperRepository,
    private val mangaDBRepository: MangaDBRepository
): ViewModel() {
    val mangaListLiveData = MutableLiveData<List<MangaItem>>()

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
}