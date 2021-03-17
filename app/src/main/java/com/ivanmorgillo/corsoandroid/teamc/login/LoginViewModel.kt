package com.ivanmorgillo.corsoandroid.teamc.login

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ivanmorgillo.corsoandroid.teamc.exhaustive
import com.ivanmorgillo.corsoandroid.teamc.firebase.Tracking
import com.ivanmorgillo.corsoandroid.teamc.utils.SingleLiveEvent
import timber.log.Timber

@Suppress("IMPLICIT_CAST_TO_ANY")
class LoginViewModel(
    private val tracking: Tracking

) : ViewModel() {
    val states = MutableLiveData<LoginScreenStates>()
    val actions = SingleLiveEvent<LoginScreenActions>()

    @SuppressLint("LogNotTimber")
    fun send(event: LoginScreenEvent) {
        when (event) {

            LoginScreenEvent.OnGoogleSignInClick -> {
                tracking.logEvent("login_google_sign_in")
                Log.d("msg", "Login Google")
                actions.postValue(LoginScreenActions.RequestGoogleSignIn)
            }
            LoginScreenEvent.OnGuestSignInClick -> {
                Log.d("msg ", "Login Guest")
                tracking.logEvent("login_guest_sign_in")
                actions.postValue(LoginScreenActions.NavigateToHome)
            }
            LoginScreenEvent.OnReady -> {

                Timber.d("login Onready")
            }
        }.exhaustive
    }
}

sealed class LoginScreenActions {

    object NavigateToHome : LoginScreenActions()
    object RequestGoogleSignIn : LoginScreenActions()
}

sealed class LoginScreenEvent {
    object OnReady : LoginScreenEvent()
    object OnGoogleSignInClick : LoginScreenEvent()
    object OnGuestSignInClick : LoginScreenEvent()
}

sealed class LoginScreenStates {
    object Loading : LoginScreenStates()
    object NoNetworkError : LoginScreenStates()
    object Content : LoginScreenStates()
}
