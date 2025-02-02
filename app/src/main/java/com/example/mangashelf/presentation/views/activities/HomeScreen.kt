package com.example.mangashelf.presentation.views.activities

import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mangashelf.databinding.BottomSheetSortingLayoutBinding
import com.example.mangashelf.databinding.HomeScreenBinding
import com.example.mangashelf.domain.models.CurrentSortOption
import com.example.mangashelf.domain.models.MangaItem
import com.example.mangashelf.presentation.viewModels.HomeScreenViewModel
import com.example.mangashelf.presentation.views.adapters.MangaListAdapter
import com.example.mangashelf.utils.TimeUtils
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayout
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeScreen : AppCompatActivity() {
    private lateinit var homeScreenViewModel: HomeScreenViewModel
    private lateinit var homeScreenBinding: HomeScreenBinding
    private var currentAdapterList = mutableListOf<Any>()
    private val yearToIndexMap = HashMap<Int, Int>()
    private val yearSortedAdapterList = ArrayList<Any>()
    private var isProgrammaticSync = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeScreenBinding = HomeScreenBinding.inflate(layoutInflater)
        setContentView(homeScreenBinding.root)
        homeScreenViewModel = ViewModelProvider(this)[HomeScreenViewModel::class.java]
        homeScreenBinding.mangaItemsRv.layoutManager = LinearLayoutManager(this)
        setupOnClickListeners()
        getMangaList()
    }

    private fun setupOnClickListeners() {
        homeScreenBinding.tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (isProgrammaticSync) {
                    isProgrammaticSync = false
                    return
                } else isProgrammaticSync = true
                (tab?.tag as? Int)?.let {
                    homeScreenBinding.mangaItemsRv.apply {
                        stopScroll()
                        (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                            yearToIndexMap.getOrDefault(it, 0), 0
                        )
                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
        homeScreenBinding.mangaItemsRv.addOnScrollListener(object :
            RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (homeScreenViewModel.currentSortOption != CurrentSortOption.PUBLICATION_YEAR) return
                if (isProgrammaticSync) {
                    isProgrammaticSync = false
                    return
                } else isProgrammaticSync = true
                val firstVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
                val year =
                    when (val firstVisibleItem = yearSortedAdapterList[firstVisibleItemPosition]) {
                        is Int -> firstVisibleItem
                        is MangaItem -> TimeUtils.convertUnixToYear(firstVisibleItem.publishedChapterDate)
                            .toInt()

                        else -> {}
                    }
                homeScreenBinding.tabLayout.getTabAt(yearToIndexMap.keys.indexOf(year))?.select()
            }
        })
        homeScreenBinding.sortBtn.setOnClickListener {
            val bottomSheetBinding = BottomSheetSortingLayoutBinding.inflate(layoutInflater)
            val dialog = BottomSheetDialog(this).apply {
                setContentView(bottomSheetBinding.root)
                show()
            }
            when (homeScreenViewModel.currentSortOption) {
                CurrentSortOption.PUBLICATION_YEAR -> bottomSheetBinding.publicationYear.setTypeface(
                    null, Typeface.BOLD
                )

                CurrentSortOption.SCORE_LOW_TO_HIGH -> bottomSheetBinding.scoreLowToHigh.setTypeface(
                    null, Typeface.BOLD
                )

                CurrentSortOption.SCORE_HIGH_TO_LOW -> bottomSheetBinding.scoreHighToLow.setTypeface(
                    null, Typeface.BOLD
                )

                CurrentSortOption.POPULARITY_LOW_TO_HIGH -> bottomSheetBinding.popularityLowToHigh.setTypeface(
                    null, Typeface.BOLD
                )

                CurrentSortOption.POPULARITY_HIGH_TO_LOW -> bottomSheetBinding.popularityHighToLow.setTypeface(
                    null, Typeface.BOLD
                )
            }
            setupClickListeners(bottomSheetBinding, dialog)
        }
    }

    private fun getMangaList() {
        homeScreenViewModel.apply {
            getMangaList()
            mangaListLiveData.observe(this@HomeScreen) {
                if (it.isNullOrEmpty()) return@observe
                provideAdapterList(it)
                homeScreenBinding.mangaItemsRv.adapter = MangaListAdapter(
                    this@HomeScreen,
                    currentAdapterList,
                    homeScreenViewModel,
                    mangaDetailedActivityResultLauncher
                )
            }
        }
    }

    private fun readAdapterList(mangaItemList: List<MangaItem>): MutableList<Any> {
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
        yearToIndexMap.keys.forEach { year ->
            homeScreenBinding.tabLayout.addTab(
                homeScreenBinding.tabLayout.newTab().setText(year.toString()).apply { tag = year })
        }
        return yearSortedAdapterList
    }

    private fun provideAdapterList(mangaItemList: List<MangaItem>?) {
        mangaItemList?.let {
            currentAdapterList = when (homeScreenViewModel.currentSortOption) {
                CurrentSortOption.PUBLICATION_YEAR -> readAdapterList(mangaItemList.sortedBy { mangaItem -> mangaItem.publishedChapterDate })
                CurrentSortOption.SCORE_LOW_TO_HIGH -> mangaItemList.sortedBy { it.score }
                    .toMutableList()

                CurrentSortOption.SCORE_HIGH_TO_LOW -> mangaItemList.sortedByDescending { it.score }
                    .toMutableList()

                CurrentSortOption.POPULARITY_LOW_TO_HIGH -> mangaItemList.sortedBy { it.popularity }
                    .toMutableList()

                CurrentSortOption.POPULARITY_HIGH_TO_LOW -> mangaItemList.sortedByDescending { it.popularity }
                    .toMutableList()
            }
        } ?: run {
            currentAdapterList = emptyList<MangaItem>().toMutableList()
        }
    }

    private fun setupClickListeners(
        bottomSheetSortingLayoutBinding: BottomSheetSortingLayoutBinding, dialog: BottomSheetDialog
    ) {
        val list = with(bottomSheetSortingLayoutBinding) {
            listOf(
                publicationYear,
                scoreLowToHigh,
                scoreHighToLow,
                popularityHighToLow,
                popularityHighToLow
            )
        }
        for (i in list.indices) {
            list[i].setOnClickListener {
                if (i != 0) homeScreenBinding.tabLayout.visibility = View.GONE
                else homeScreenBinding.tabLayout.visibility = View.VISIBLE
                homeScreenViewModel.currentSortOption = CurrentSortOption.entries[i]
                provideAdapterList(homeScreenViewModel.mangaListLiveData.value)
                homeScreenBinding.mangaItemsRv.adapter = MangaListAdapter(
                    this@HomeScreen,
                    currentAdapterList,
                    homeScreenViewModel,
                    mangaDetailedActivityResultLauncher
                )
                dialog.dismiss()
            }
        }
    }

    private val mangaDetailedActivityResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == RESULT_OK) {
                val position = it.data?.getIntExtra(MangaListAdapter.POSITION, -1)
                if (position != null && position != -1) {
                    currentAdapterList[position] =
                        it.data?.getParcelableExtra<MangaItem>(MangaDetailedActivity.MANGA_ITEM)!!
                    homeScreenBinding.mangaItemsRv.adapter?.notifyItemChanged(position)
                }
            }
        }
}