package com.sttptech.toshiba_lighting.Mqtt

import com.google.gson.Gson
import com.google.gson.JsonObject
import java.util.*

class MqttPublish {
    
    companion object {
        
        /** 獲取device 狀態 */
        fun getStatus(mqttClient: MqttClient, model: String, bssid: String) {
            val json = JsonObject()
            json.addProperty("config", "STATEUPDATE")
            mqttClient.sendMsg(
                Gson().toJson(json),
                model,
                bssid,
                MqttTopicTag.STATUS
            )
        }
    
        /** All ON/OFF */
        fun triggerAll(mqttClient: MqttClient, onOff: Boolean, model: String, bssid: String) {
            val switchResult = if (onOff) "ON" else "OFF"
            val jsonBody = JsonObject().also {
                it.addProperty("f", "2")
                it.addProperty("t", "4")
                it.add("p", JsonObject().also { p ->
                    p.addProperty("m", "all")
                    p.addProperty("s", switchResult)
                })
            }
            mqttClient.sendMsg(
                Gson().toJson(jsonBody),
                model,
                bssid,
                MqttTopicTag.CONTROL
            )
        }
    
        /** 全光, 節能 */
        fun triggerMainMode(
            mqttClient: MqttClient,
            mode: Int,
            br: Int,
            wy: Int,
            model: String,
            bssid: String
        ) {
            val jsonBody = JsonObject().also { body ->
                body.addProperty("f", "2")
                body.addProperty("t", "4")
                body.add("p", JsonObject().also { parms ->
                    parms.addProperty("m", "mode")
                    parms.add("p", JsonObject().also { modeP ->
                        modeP.addProperty("md", mode.toString())
                        modeP.addProperty("br", br.toString())
                        modeP.addProperty("wy", wy.toString())
                    })
                })
            }
            
            mqttClient.sendMsg(
                Gson().toJson(jsonBody),
                model,
                bssid,
                MqttTopicTag.CONTROL
            )
        }
    
        /** 夜燈 */
        fun triggerNightMode(mqttClient: MqttClient, br: Int, model: String, bssid: String) {
            val jsonBody = JsonObject().also { body ->
                body.addProperty("f", "2")
                body.addProperty("t", "4")
                body.add("p", JsonObject().also { parms ->
                    parms.addProperty("m", "mode")
                    parms.add("p", JsonObject().also { modeP ->
                        modeP.addProperty("md", "53")
                        modeP.addProperty("br", br.toString())
                    })
                })
            }
            
            mqttClient.sendMsg(
                Gson().toJson(jsonBody),
                model,
                bssid,
                MqttTopicTag.CONTROL
            )
        }
    
    
        /** 內建模式 */
        fun triggerMode(
            mqttClient: MqttClient,
            mode: Int,
            br: Int,
            model: String,
            bssid: String
        ) {
            val jsonBody = JsonObject().also { body ->
                body.addProperty("f", "2")
                body.addProperty("t", "4")
                body.add("p", JsonObject().also { parms ->
                    parms.addProperty("m", "mode")
                    parms.add("p", JsonObject().also { modeP ->
                        modeP.addProperty("md", mode.toString())
                        modeP.addProperty("br", br.toString())
                    })
                })
            }
            
            mqttClient.sendMsg(
                Gson().toJson(jsonBody),
                model,
                bssid,
                MqttTopicTag.CONTROL
            )
        }
    
        /** 晨昏模式 */
        fun triggerMorningMode(mqttClient: MqttClient, br: Int, model: String, bssid: String) {
            val calendar = Calendar.getInstance()
            val hour = calendar[Calendar.HOUR_OF_DAY].toString()
            val minute = calendar[Calendar.MINUTE].toString()
            val second = calendar[Calendar.SECOND].toString()
            
            val jsonBody = JsonObject().also { body ->
                body.addProperty("f", "2")
                body.addProperty("t", "4")
                body.add("p", JsonObject().also { parms ->
                    parms.addProperty("m", "mode")
                    parms.add("p", JsonObject().also { modeP ->
                        modeP.addProperty("md", "6")
                        modeP.addProperty("br", br.toString())
                        modeP.add("t", JsonObject().also { time ->
                            time.addProperty("hr", hour)
                            time.addProperty("min", minute)
                            time.addProperty("sec", second)
                        })
                    })
                })
            }
            
            mqttClient.sendMsg(
                Gson().toJson(jsonBody),
                model,
                bssid,
                MqttTopicTag.CONTROL
            )
        }
    
        /** 彩色模式 */
        fun triggerColorMode(
            mqttClient: MqttClient,
            br: Int,
            intent: Int,
            model: String,
            bssid: String
        ) {
            val jsonBody = JsonObject().also { body ->
                body.addProperty("f", "2")
                body.addProperty("t", "4")
                body.add("p", JsonObject().also { parms ->
                    parms.addProperty("m", "mode")
                    parms.add("p", JsonObject().also { modeP ->
                        modeP.addProperty("md", "10")
                        modeP.addProperty("br", br.toString())
                        modeP.addProperty("sw", intent.toString()) // 0: 一般操作, 1: 暫停, 2: 繼續
                    })
                })
            }
            
            mqttClient.sendMsg(
                Gson().toJson(jsonBody),
                model,
                bssid,
                MqttTopicTag.CONTROL
            )
        }
    
        /** 全彩模式 */
        fun triggerRGBMode(
            mqttClient: MqttClient,
            br: Int,
            rgb: IntArray,
            model: String,
            bssid: String
        ) {
            val r = rgb[0]
            val g = rgb[1]
            val b = rgb[2]
    
            val jsonBody = JsonObject().also { body ->
                body.addProperty("f", "2")
                body.addProperty("t", "4")
                body.add("p", JsonObject().also { parms ->
                    parms.addProperty("m", "mode")
                    parms.add("p", JsonObject().also { modeP ->
                        modeP.addProperty("md", "12")
                        modeP.addProperty("br", br.toString())
                        modeP.addProperty("r", r.toString())
                        modeP.addProperty("g", g.toString())
                        modeP.addProperty("b", b.toString())
                    })
                })
            }
            
            mqttClient.sendMsg(
                Gson().toJson(jsonBody),
                model,
                bssid,
                MqttTopicTag.CONTROL
            )
        }
    
    
        /** 自訂模式 */
        fun triggerCustomMode(
            mqttClient: MqttClient,
            mode: Int,
            intent: Int,
            model: String,
            bssid: String
        ) {
            val jsonBody = JsonObject().also { body ->
                body.addProperty("f", "2")
                body.addProperty("t", "4")
                body.add("p", JsonObject().also { parms ->
                    parms.addProperty("m", "mode")
                    parms.add("p", JsonObject().also { modeP ->
                        modeP.addProperty("md", mode.toString())
                        modeP.addProperty("br", intent.toString()) // 1:Save, 2:recall
                    })
                })
            }
            
            mqttClient.sendMsg(
                Gson().toJson(jsonBody),
                model,
                bssid,
                MqttTopicTag.CONTROL
            )
        }
    
    
        /** 倒數計時 */
        fun setCountDownTime(mqttClient: MqttClient, time: Int, model: String, bssid: String) {
            
            val jsonBody = JsonObject().also { body ->
                body.addProperty("f", "2")
                body.addProperty("t", "4")
                body.add("p", JsonObject().also { parms ->
                    parms.addProperty("m", "set")
                    parms.add("p", JsonObject().also { modeP ->
                        modeP.addProperty("md", "t")
                        modeP.addProperty("d", (time * 60).toString()) // 計時時間 單位：秒
                    })
                })
            }
            
            mqttClient.sendMsg(
                Gson().toJson(jsonBody),
                model,
                bssid,
                MqttTopicTag.CONTROL
            )
        }
    
        /** 倒數計時開關 */
        fun triggerCountDownTimeS(
            mqttClient: MqttClient,
            status: Int,
            model: String,
            bssid: String
        ) {
            val jsonBody = JsonObject().also { body ->
                body.addProperty("f", "2")
                body.addProperty("t", "4")
                body.add("p", JsonObject().also { parms ->
                    parms.addProperty("m", "set")
                    parms.add("p", JsonObject().also { modeP ->
                        modeP.addProperty("md", "e")
                        modeP.addProperty("d", status.toString()) // 0: 熄燈, 255: 小夜燈
                    })
                })
            }
            
            mqttClient.sendMsg(
                Gson().toJson(jsonBody),
                model,
                bssid,
                MqttTopicTag.CONTROL
            )
        }
    
        /** 操作聲開關 */
        fun setBuzzer(mqttClient: MqttClient, model: String, bssid: String) {
            
            val jsonBody = JsonObject().also { body ->
                body.addProperty("f", "2")
                body.addProperty("t", "4")
                body.add("p", JsonObject().also { parms ->
                    parms.addProperty("m", "set")
                    parms.add("p", JsonObject().also { modeP ->
                        modeP.addProperty("md", "b")
                    })
                })
            }
            
            mqttClient.sendMsg(
                Gson().toJson(jsonBody),
                model,
                bssid,
                MqttTopicTag.CONTROL
            )
        }
    
        /** 助眠模式設定時間 */
        fun setSleepTime(mqttClient: MqttClient, time: Int, model: String, bssid: String) {
            
            val jsonBody = JsonObject().also { body ->
                body.addProperty("f", "2")
                body.addProperty("t", "4")
                body.add("p", JsonObject().also { parms ->
                    parms.addProperty("m", "set")
                    parms.add("p", JsonObject().also { modeP ->
                        modeP.addProperty("md", "s")
                        modeP.addProperty("d", time.toString()) // 0: 30min  255: 60min
                    })
                })
            }
            
            mqttClient.sendMsg(
                Gson().toJson(jsonBody),
                model,
                bssid,
                MqttTopicTag.CONTROL
            )
        }
    
        /** 重置 */
        fun reset(mqttClient: MqttClient, model: String, bssid: String) {
            
            val jsonBody = JsonObject().also { body ->
                body.addProperty("f", "2")
                body.addProperty("t", "4")
                body.add("p", JsonObject().also { parms ->
                    parms.addProperty("m", "set")
                    parms.add("p", JsonObject().also { modeP ->
                        modeP.addProperty("md", "r")
                    })
                })
            }
            
            mqttClient.sendMsg(
                Gson().toJson(jsonBody),
                model,
                bssid,
                MqttTopicTag.CONTROL
            )
        }
    
        fun mainRGBAdjust(
            mqttClient: MqttClient,
            mode: Int,
            adjust: Int,
            model: String,
            bssid: String
        ) {
            val jsonBody = JsonObject().also { body ->
                body.addProperty("f", "2")
                body.addProperty("t", "4")
                body.add("p", JsonObject().also { parms ->
                    parms.addProperty("m", "mode")
                    parms.add("p", JsonObject().also { modeP ->
                        modeP.addProperty("md", mode.toString())
                        modeP.addProperty("br", adjust.toString()) // 1: add  2: less
                    })
                })
            }
            
            mqttClient.sendMsg(
                Gson().toJson(jsonBody),
                model,
                bssid,
                MqttTopicTag.CONTROL
            )
        }
    
        fun mainBrCAdjust(
            mqttClient: MqttClient,
            mode: Int,
            smd: Int,
            br: Int,
            wy: Int,
            model: String,
            bssid: String
        ) {
            val jsonBody = JsonObject().also { body ->
                body.addProperty("f", "2")
                body.addProperty("t", "4")
                body.add("p", JsonObject().also { parms ->
                    parms.addProperty("m", "mode")
                    parms.add("p", JsonObject().also { modeP ->
                        modeP.addProperty("md", mode.toString())
                        modeP.addProperty("smd", smd.toString())
                        modeP.addProperty("br", br.toString())
                        modeP.addProperty("wy", wy.toString())
                    })
                })
            }
            
            mqttClient.sendMsg(
                Gson().toJson(jsonBody),
                model,
                bssid,
                MqttTopicTag.CONTROL
            )
        }
    }
}