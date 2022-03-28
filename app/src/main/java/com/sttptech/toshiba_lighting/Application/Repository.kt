package com.sttptech.toshiba_lighting.Application

import android.content.Context

class Repository(context: Context) {

    val remoteS: RepositoryService.RemoteData
    val localS: RepositoryService.LocalData

    init {
        remoteS = RemoteService(context)
        localS = LocalService(context)
    }
}