package com.example.mangashelf.domain.models

enum class CurrentSortOption(val index: Int) {
    PUBLICATION_YEAR(0),
    SCORE_LOW_TO_HIGH(1),
    SCORE_HIGH_TO_LOW(2),
    POPULARITY_LOW_TO_HIGH(3),
    POPULARITY_HIGH_TO_LOW(4)
}