package com.ivanmorgillo.corsoandroid.teamc.firebase

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.ktx.Firebase

sealed class Screens {
    abstract val className: String
    abstract val name: String

    object Home : Screens() {
        override val className: String = "Home Fragment"
        override val name: String = "Home"
    }

    object Favourites : Screens() {
        override val className: String = "Favourites Fragment"
        override val name: String = "Favourites"
    }

    object Details : Screens() {
        override val className: String = "Recipe Detail Fragment"
        override val name: String = "Details"
    }
}

interface Tracking {
    fun logEvent(eventName: String)
    fun logScreen(screen: Screens)
}

class TrackingImpl : Tracking {
    override fun logEvent(eventName: String) {
        Firebase.analytics.logEvent(eventName, null)
    }

    override fun logScreen(screen: Screens) {
        Firebase.analytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW) {
            param(FirebaseAnalytics.Param.SCREEN_NAME, screen.name)
            param(FirebaseAnalytics.Param.SCREEN_CLASS, screen.className)
        }
    }
}
