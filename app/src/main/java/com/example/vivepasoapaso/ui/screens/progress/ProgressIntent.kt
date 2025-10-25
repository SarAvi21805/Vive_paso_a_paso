package com.example.vivepasoapaso.presentation.screens.progress

sealed class ProgressIntent {
    object RequestRecommendation : ProgressIntent()
    object RefreshData : ProgressIntent()
}
