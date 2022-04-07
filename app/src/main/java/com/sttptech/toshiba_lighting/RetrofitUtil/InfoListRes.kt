package com.sttptech.toshiba_lighting.RetrofitUtil

import com.google.gson.Gson

data class InfoListRes(
    val code: Int,
    val datum: Datum,
    val i18nCode: String,
    val msg: String,
    val success: Boolean
) {
    data class Datum(
        val cloneGroups: List<Any>,
        val cloneGrsituations: List<Any>,
        val clones: List<Any>,
        val ownGroups: List<OwnGroup>,
        val ownGrsituations: List<OwnGrsituation>,
        val owns: List<Own>,
        val shareGroups: List<Any>,
        val shares: List<Any>,
        val share: Boolean,
        val shareMail: String
    ) {
        data class OwnGroup(
            val devUuids: List<String>,
            val groupDef: String,
            val groupName: String,
            val groupUuid: String
        )

        data class OwnGrsituation(
            val devUuids: List<String>,
            val groupUuids: List<Any>,
            val grsituationDef: String,
            val grsituationImage: String,
            val grsituationName: String,
            val grsituationOrder: Int,
            val grsituationSeq: Int,
            val grsituationUuid: String
        )

        data class Own(
            val info: Info,
            val infoD: InfoD
        ) {
            data class Info(
                val devName: String,
                val devSn: String,
                val devUuid: String,
                val prodScode: String
            )

            data class InfoD(
                val confFw: String,
                val confHw: String,
                val confPc: String,
                val confSw: String,
                val infoType: String
            ) {

                val parseConfPC: ConfPC
                    get() = Gson().fromJson(confPc, ConfPC::class.java)

                val parseConfSW: ConfSW
                    get() = Gson().fromJson(confSw, ConfSW::class.java)

                data class ConfPC(
                    val devIconB64: String,
                    val devLoc: String,
                    val devName: String
                )

                data class ConfSW(
                    val rcuModes: List<RcuMode>,
                    val rcuSetting: RcuSetting
                ) {
                    data class RcuMode(
                        val c: String,
                        val cn: String,
                        val qp: String
                    )

                    data class RcuSetting(
                        val dailyClose: String,
                        val dailyCloseS: String,
                        val dailyOpen: String,
                        val dailyOpenS: String,
                        val dailyWakeUp: String,
                        val dailyWakeUpS: String
                    )
                }
            }
        }
    }
}