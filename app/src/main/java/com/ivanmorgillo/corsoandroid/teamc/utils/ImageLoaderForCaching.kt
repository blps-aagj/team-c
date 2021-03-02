package com.ivanmorgillo.corsoandroid.teamc.utils

import android.content.Context
import coil.ImageLoader
import coil.util.CoilUtils
import okhttp3.OkHttpClient

class ImageLoaderForCaching {
    companion object {
        fun imageLoader(context: Context): ImageLoader {
            return ImageLoader.Builder(context)
                .crossfade(true)
                .okHttpClient {
                    OkHttpClient.Builder()
                        .cache(CoilUtils.createDefaultCache(context))
                        .build()
                }
                .build()
        }
    }
}
