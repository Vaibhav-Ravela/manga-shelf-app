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
    private var isProgrammaticSync = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeScreenBinding = HomeScreenBinding.inflate(layoutInflater)
        setContentView(homeScreenBinding.root)
        homeScreenViewModel = ViewModelProvider(this)[HomeScreenViewModel::class.java]
        homeScreenBinding.mangaItemsRv.layoutManager = LinearLayoutManager(this)
        if (homeScreenViewModel.currentSortOption == CurrentSortOption.PUBLICATION_YEAR) homeScreenBinding.tabLayout.visibility =
            View.VISIBLE
        else homeScreenBinding.tabLayout.visibility = View.GONE
        setupOnClickListeners()
        homeScreenViewModel.mangaListLiveData.observe(this) {
            homeScreenBinding.apply {
                root.isRefreshing = false
                progressBar.visibility = View.GONE
                mainContent.visibility = View.VISIBLE
                if (it.isNullOrEmpty()) {
                    noData.visibility = View.VISIBLE
                    mainContent.visibility = View.GONE
                    return@observe
                } else {
                    noData.visibility = View.GONE
                    mainContent.visibility = View.VISIBLE
                }
                homeScreenViewModel.readAdapterList(it.sortedBy { mangaItem -> mangaItem.publishedChapterDate })
                tabLayout.removeAllTabs()
                homeScreenViewModel.yearToIndexMap.keys.forEach { year ->
                    homeScreenBinding.tabLayout.addTab(homeScreenBinding.tabLayout.newTab()
                        .setText(year.toString()).apply { tag = year })
                }
                mangaItemsRv.adapter = MangaListAdapter(
                    this@HomeScreen,
                    provideAdapterList(it),
                    homeScreenViewModel,
                    mangaDetailedActivityResultLauncher
                )
            }
        }
        homeScreenViewModel.getMangaList()
    }

    private fun setupOnClickListeners() {
        homeScreenBinding.root.setOnRefreshListener {
            homeScreenViewModel.getMangaList()
        }
        homeScreenBinding.tabLayout.addOnTabSelectedListener(object :
            TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (isProgrammaticSync) return
                else isProgrammaticSync = true
                (tab?.tag as? Int)?.let {
                    homeScreenBinding.mangaItemsRv.apply {
                        stopScroll()
                        (layoutManager as LinearLayoutManager).scrollToPositionWithOffset(
                            homeScreenViewModel.yearToIndexMap.getOrDefault(it, 0), 0
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
                val year = when (val firstVisibleItem =
                    homeScreenViewModel.yearSortedAdapterList[firstVisibleItemPosition]) {
                    is Int -> firstVisibleItem
                    is MangaItem -> TimeUtils.convertUnixToYear(firstVisibleItem.publishedChapterDate)
                        .toInt()

                    else -> {}
                }
                homeScreenBinding.tabLayout.getTabAt(
                    homeScreenViewModel.yearToIndexMap.keys.indexOf(
                        year
                    )
                )?.select()
                isProgrammaticSync = false
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

    private fun provideAdapterList(mangaItemList: List<MangaItem>): MutableList<Any> {
        return when (homeScreenViewModel.currentSortOption) {
            CurrentSortOption.PUBLICATION_YEAR -> homeScreenViewModel.yearSortedAdapterList
            CurrentSortOption.SCORE_LOW_TO_HIGH -> mangaItemList.sortedBy { it.score }
                .toMutableList()

            CurrentSortOption.SCORE_HIGH_TO_LOW -> mangaItemList.sortedByDescending { it.score }
                .toMutableList()

            CurrentSortOption.POPULARITY_LOW_TO_HIGH -> mangaItemList.sortedBy { it.popularity }
                .toMutableList()

            CurrentSortOption.POPULARITY_HIGH_TO_LOW -> mangaItemList.sortedByDescending { it.popularity }
                .toMutableList()
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
                popularityLowToHigh,
                popularityHighToLow
            )
        }
        for (i in list.indices) {
            list[i].setOnClickListener {
                if (i != 0) homeScreenBinding.tabLayout.visibility = View.GONE
                else homeScreenBinding.tabLayout.visibility = View.VISIBLE
                homeScreenViewModel.currentSortOption = CurrentSortOption.entries[i]
                if (homeScreenViewModel.mangaListLiveData.value.isNullOrEmpty()) return@setOnClickListener
                homeScreenBinding.mangaItemsRv.adapter = MangaListAdapter(
                    this@HomeScreen,
                    provideAdapterList(homeScreenViewModel.mangaListLiveData.value!!),
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
                    (homeScreenBinding.mangaItemsRv.adapter as MangaListAdapter).adapterList[position] =
                        it.data?.getParcelableExtra<MangaItem>(MangaDetailedActivity.MANGA_ITEM)!!
                    homeScreenBinding.mangaItemsRv.adapter?.notifyItemChanged(position)
                }
            }
        }
}