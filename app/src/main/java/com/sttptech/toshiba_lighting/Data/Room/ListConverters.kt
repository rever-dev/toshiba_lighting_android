package com.sttptech.toshiba_lighting.Data.Room

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.sttptech.toshiba_lighting.Data.Bean.Group

class Converters {

    class ListConverters {
        @TypeConverter
        fun objectToString(list: List<String?>?): String? {
            return Gson().toJson(list)
        }

        @TypeConverter
        fun stringToObject(json: String?): List<String>? {
            return if (json != null) {
                val listType = object : TypeToken<List<String?>?>() {}.type
                Gson().fromJson(json, listType)
            } else
                null
        }
    }

    class GroupConverters{
        @TypeConverter
        fun objectToString(group: Group?): String? {
            return Gson().toJson(group)
        }

        @TypeConverter
        fun stringToObject(json: String?): Group? {
            return if (json != null) {
                val group = object : TypeToken<Group>() {}.type
                Gson().fromJson(json, group)
            } else
                null
        }
    }
}