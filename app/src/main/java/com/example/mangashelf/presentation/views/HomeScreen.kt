package com.example.mangashelf.presentation.views

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangashelf.databinding.HomeScreenBinding
import com.example.mangashelf.domain.models.MangaItem
import com.example.mangashelf.presentation.viewModels.HomeScreenViewModel
import com.example.mangashelf.utils.TimeUtils
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeScreen : AppCompatActivity() {
    private lateinit var homeScreenViewModel: HomeScreenViewModel
    private lateinit var homeScreenBinding: HomeScreenBinding
    private val yearToIndexMap = HashMap<Int, Int>()
    private val adapterList = ArrayList<Any>()
    private var isProgramScroll = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeScreenBinding = HomeScreenBinding.inflate(layoutInflater)
        setContentView(homeScreenBinding.root)
        homeScreenViewModel = ViewModelProvider(this)[HomeScreenViewModel::class.java]
        homeScreenBinding.mangaItemsRv.layoutManager = LinearLayoutManager(this)
        homeScreenViewModel.getMangaList()
        homeScreenViewModel.mangaListLiveData.observe(this) {
            if (it.isNullOrEmpty()) return@observe
            homeScreenBinding.mangaItemsRv.adapter = MangaListAdapter(
                this,
                provideAdapterList(it.sortedBy { mangaItem -> mangaItem.publishedChapterDate })
            )
            yearToIndexMap.keys.forEach { year ->
                homeScreenBinding.tabLayout.addTab(homeScreenBinding.tabLayout.newTab()
                    .setText(year.toString()).apply { tag = year })
            }
        }
        homeScreenBinding.tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                (tab?.tag as? Int)?.let {
                    isProgramScroll = true
                    (homeScreenBinding.mangaItemsRv.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                        yearToIndexMap.getOrDefault(it, 0), 0
                    )
                    homeScreenBinding.mangaItemsRv.postDelayed({ isProgramScroll = false }, 500)
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        homeScreenBinding.mangaItemsRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (isProgramScroll) return
                val firstVisibleItemPosition = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                val year = when (val firstVisibleItem = adapterList[firstVisibleItemPosition]) {
                    is Int -> firstVisibleItem
                    is MangaItem -> TimeUtils.convertUnixToYear(firstVisibleItem.publishedChapterDate).toInt()
                    else -> {}
                }
                homeScreenBinding.tabLayout.getTabAt(yearToIndexMap.keys.indexOf(year))?.select()
            }
        })
    }

    private fun provideAdapterList(mangaItemList: List<MangaItem>): List<Any> {
        adapterList.clear()
        yearToIndexMap.clear()
        var prevYear = TimeUtils.convertUnixToYear(mangaItemList[0].publishedChapterDate)
        adapterList.add(prevYear.toInt())
        yearToIndexMap[prevYear.toInt()] = adapterList.size - 1
        adapterList.add(mangaItemList[0])
        for (i in 1..<mangaItemList.size) {
            val currentYear = TimeUtils.convertUnixToYear(mangaItemList[i].publishedChapterDate)
            if (prevYear != currentYear) {
                prevYear = currentYear
                adapterList.add(prevYear.toInt())
                yearToIndexMap[prevYear.toInt()] = adapterList.size - 1
            }
            adapterList.add(mangaItemList[i])
        }
        return adapterList
    }
}