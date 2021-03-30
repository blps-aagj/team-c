package com.ivanmorgillo.corsoandroid.teamc

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ivanmorgillo.corsoandroid.teamc.firebase.Tracking

class MainViewModel(private val tracking: Tracking) : ViewModel() {
    val state = MutableLiveData<MainScreenStates>()
    val actions = MutableLiveData<MainScreenAction>()
    fun send(event: MainScreenEvent) {
        when (event) {
            MainScreenEvent.OnFeedbackClicked -> {
                tracking.logEvent("drawer_feedback_clicked")
            }
            MainScreenEvent.OnFavouriteListMenuClicked -> {
                tracking.logEvent("drawer_favourite_list_clicked")
            }
        }.exhaustive
    }
}

sealed class MainScreenAction {

}

sealed class MainScreenEvent {
    object OnFavouriteListMenuClicked : MainScreenEvent()
    object OnFeedbackClicked : MainScreenEvent()
}

sealed class MainScreenStates {

}
