package com.example.mangashelf.presentation.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mangashelf.databinding.HomeScreenBinding
import com.example.mangashelf.presentation.viewModels.HomeScreenViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeScreen : AppCompatActivity() {
    private lateinit var homeScreenViewModel: HomeScreenViewModel
    private lateinit var homeScreenBinding: HomeScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeScreenBinding = HomeScreenBinding.inflate(layoutInflater)
        setContentView(homeScreenBinding.root)
        homeScreenViewModel = ViewModelProvider(this)[HomeScreenViewModel::class.java]
        homeScreenBinding.mangaItemsRv.layoutManager = LinearLayoutManager(this)
        homeScreenViewModel.getMangaList()
        homeScreenViewModel.mangaListLiveData.observe(this) {
            homeScreenBinding.mangaItemsRv.adapter = MangaListAdapter(this, it)
        }

    }
}