package com.sttptech.toshiba_lighting.Application

import com.sttptech.toshiba_lighting.Data.Bean.CeilingLight
import com.sttptech.toshiba_lighting.Data.Bean.Device
import com.sttptech.toshiba_lighting.RetrofitUtil.InfoListRes
import com.sttptech.toshiba_lighting.RetrofitUtil.ServerResponse

interface DataSource {

    interface LocalData {

        fun getAccount(): String?

        fun clearCeilingLightTable()

        fun clearGroupTable()

        fun insertCeilingLight(data: CeilingLight)

        fun allCeilingLights(): List<CeilingLight>?

    }

    interface RemoteData {

        fun memberLogin(account: String, password: String): ServerResponse?

        fun getInfoList(): InfoListRes?

        fun devSignup(mac: String, group: String, name: String, model: String): ServerResponse?
    }
}