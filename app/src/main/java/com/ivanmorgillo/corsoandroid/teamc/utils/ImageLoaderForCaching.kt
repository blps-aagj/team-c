package com.ivanmorgillo.corsoandroid.teamc.utils

import android.content.Context
import coil.ImageLoader
import coil.util.CoilUtils
import com.ivanmorgillo.corsoandroid.teamc.R
import okhttp3.OkHttpClient

fun imageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .crossfade(true)
        .placeholder(R.drawable.loading)
        .okHttpClient {
            OkHttpClient.Builder()
                .cache(CoilUtils.createDefaultCache(context))
                .build()
        }
        .build()
}
