package com.sttptech.toshiba_lighting.RetrofitUtil

import com.google.gson.Gson
import com.sttptech.toshiba_lighting.Data.Bean.Group

data class ServerResponse(
    private val code: Int,
    private var success: Boolean
) {

    private var msg: String? = null
    private var msgGrp: List<MsgGrpDTO?>? = null
    private var i18nCode: String? = null
    private var data: DataDTO? = null
    private var datum: DatumDTO? = null

    fun getMsg(): String? {
        return msg
    }

    fun setMsg(msg: String?) {
        this.msg = msg
    }

    fun getMsgGrp(): List<MsgGrpDTO?>? {
        return msgGrp
    }

    fun setMsgGrp(msgGrp: List<MsgGrpDTO?>?) {
        this.msgGrp = msgGrp
    }

    fun getI18nCode(): String? {
        return i18nCode
    }

    fun setI18nCode(i18nCode: String?) {
        this.i18nCode = i18nCode
    }

    fun getData(): DataDTO? {
        return data
    }

    fun setData(data: DataDTO?) {
        this.data = data
    }

    fun getDatum(): DatumDTO? {
        return datum
    }

    fun setDatum(datum: DatumDTO?) {
        this.datum = datum
    }

    fun isSuccess(): Boolean {
        return success
    }

    fun setSuccess(success: Boolean) {
        this.success = success
    }

    class DataDTO {
        var ary: List<AryDTO>? = null
        var cnt = 0

        class AryDTO
    }

    class DatumDTO {
        var owns: List<OwnsDTO>? = null
        var ownGroups: List<OwnGroupsDTO>? = null
        var ownGrsituations: List<OwnGrsituationsDTO>? = null
        var info: InfoDTO? = null
        var infoD: InfoDDTO? = null
        var infos: List<InfosDTO>? = null
        var group: Group? = null
        var userInfo: UserInfoDTO? = null
        var token: String? = null
        var groupUuid: String? = null
        var groupName: String? = null
        var groupDef: String? = null
        var grsituationUuid: String? = null
        var grsituationName: String? = null
        var grsituationSeq = 0
        var grsituationDef: String? = null
        var devUuids: List<String>? = null
        var groupUuids: List<String>? = null
        var shares: List<String>? = null
        var shareGroups: List<String>? = null
        var clones: List<String>? = null
        var cloneGroups: List<String>? = null
        var cloneGrsituations: List<String>? = null
        var grsituationImage: String? = null
        var grsituationOrder: Int? = null
        var cycleTaskMode: String? = null
        var cycleTaskType: String? = null
        var payload: List<Payload>? = null
        var statusDb: String? = null
        var taskCode: String? = null
        var taskId: String? = null
        var taskSeq: Int? = null
    
        class Payload(
            var act: String?,
            var cycleTaskId: String?,
            var dayOfWeek: Int?,
            var grsituationUuid: String?,
            var minuteOfDay: Int?
        )
    
    
        fun getGroupData(): String? {
        
            val strBuilder = StringBuilder()
        
            for (i in 0 until devUuids?.size!!) {
                if ((i + 1) == devUuids?.size)
                    strBuilder.append("\"${devUuids!![i]}\"")
                else
                    strBuilder.append("\"${devUuids!![i]}\", ")
            }
            
            return "{" +
                    " \"groupUuid\" : \"$groupUuid\", \n" +
                    " \"groupName\" : \"$groupName\", \n" +
                    " \"groupDef\" : \"$groupDef\", \n" +
                    " \"devUuids\" : [ $strBuilder ] \n" +
                    "}"
        }

        class InfoDTO {
            var devUuid: String? = null
            var devName: String? = null
            var prodScode: String? = null
            var devSn: String? = null
        }

        class InfoDDTO {
            var confHw: String? = null
            var confFw: String? = null
            private var confSw: String? = null
            private var confPc: String? = null
            var infoType: String? = null

            fun getConfSw(): ConfSwDTO {
                return Gson().fromJson(confSw, ConfSwDTO::class.java)
            }

            fun setConfSw(confSw: String?) {
                this.confSw = confSw
            }

            fun getConfPc(): ConfPcDTO {
                return Gson().fromJson(confPc, ConfPcDTO::class.java)
            }

            fun setConfPc(confPc: String?) {
                this.confPc = confPc
            }

            class ConfSwDTO {
                var rcuSetting: RcuSettingDTO? = null
                var rcuModes: List<RcuModesDTO>? = null

                class RcuSettingDTO {
                    var dailyWakeUp: String? = null
                    var dailyWakeUpS: String? = null
                    var dailyOpen: String? = null
                    var dailyOpenS: String? = null
                    var dailyClose: String? = null
                    var dailyCloseS: String? = null
                }

                class RcuModesDTO {
                    var c: String? = null
                    var cn: String? = null
                    var qp: String? = null
                }
            }

            class ConfPcDTO {
                var devLoc: String? = null
                var devIconB64: String? = null
                var devName: String? = null
            }
        }
    
        class InfosDTO {
            var taskId: String? = null
            var taskCode: String? = null
            var taskSeq = 0
            var cycleTaskType: String? = null
            var cycleTaskMode: String? = null
            var payload: List<PayloadDTO>? = null
            var statusDb: String? = null
        
            class PayloadDTO {
                var cycleTaskId: String? = null
                var dayOfWeek = 0
                var minuteOfDay = 0
                var act: String? = null
                var grsituationUuid: String? = null
            }
        }

        class UserInfoDTO {
            var email: String? = null
            var prodLineScode: String? = null
        }

        class OwnsDTO {
            var info: InfoDTO? = null
            var infoD: InfoDDTO? = null

            class InfoDTO {
                var devUuid: String? = null
                var devName: String? = null
                var prodScode: String? = null
                var devSn: String? = null
            }

            class InfoDDTO {
                var confHw: String? = null
                var confFw: String? = null
                private var confSw: String? = null
                private var confPc: String? = null
                var infoType: String? = null

                fun getConfSw(): ConfSwDTO {
                    return Gson().fromJson(confSw, ConfSwDTO::class.java)
                }

                fun setConfSw(confSw: String?) {
                    this.confSw = confSw
                }

                fun getConfPc(): ConfPcDTO {
                    return Gson().fromJson(confPc, ConfPcDTO::class.java)
                }

                fun setConfPc(confPc: String?) {
                    this.confPc = confPc
                }

                class ConfSwDTO {
                    var rcuSettings: RcuSettingsDTO? = null
                    var rcuModes: List<RcuModesDTO>? = null

                    class RcuSettingsDTO {
                        var dailyWakeUp: String? = null
                        var dailyWakeUpS: String? = null
                        var dailyOpen: String? = null
                        var dailyOpenS: String? = null
                        var dailyClose: String? = null
                        var dailyCloseS: String? = null
                    }

                    class RcuModesDTO {
                        var c: String? = null
                        var cn: String? = null
                        var qp: String? = null
                    }
                }

                class ConfPcDTO {
                    var devLoc: String? = null
                    var devIconB64: String? = null
                    var devName: String? = null
                }
            }
        }

        class OwnGroupsDTO {
            var groupUuid: String? = null
            var groupName: String? = null
            var groupDef: String? = null
            var devUuids: List<String>? = null
        }

        class OwnGrsituationsDTO {
            var grsituationUuid: String? = null
            var grsituationName: String? = null
            var grsituationSeq: Int? = null
            var grsituationDef: String? = null
            var devUuids: List<String>? = null
            var groupUuids: List<String>? = null
            var grsituationOrder: Int? = null
        }
    }

    class MsgGrpDTO {
        var mkey: String? = null
        var msgs: List<MsgsDTO>? = null

        class MsgsDTO {
            var code = 0
            var datum: String? = null
            var i18nCode: String? = null
            var msg: String? = null
        }
    }
    
    
}