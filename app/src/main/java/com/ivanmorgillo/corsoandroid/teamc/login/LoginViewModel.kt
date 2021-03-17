package com.ivanmorgillo.corsoandroid.teamc.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ivanmorgillo.corsoandroid.teamc.firebase.Tracking
import com.ivanmorgillo.corsoandroid.teamc.utils.SingleLiveEvent

class LoginViewModel(
    private val tracking: Tracking

) : ViewModel() {
    val states = MutableLiveData<LoginScreenStates>()
    val actions = SingleLiveEvent<LoginScreenActions>()
}

sealed class LoginScreenActions {
    object NavigateToHome : LoginScreenActions()
}

sealed class LoginScreenEvent {
    object OnGoogleSignInClick : LoginScreenEvent()
    object OnGuestSignInClick : LoginScreenEvent()
}

sealed class LoginScreenStates {
    object Loading : LoginScreenStates()
    object NoNetworkError : LoginScreenStates()
    object Content : LoginScreenStates()
}
