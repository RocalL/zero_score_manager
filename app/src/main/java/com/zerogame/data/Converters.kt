package com.zerogame.data

import androidx.room.TypeConverter
import com.zerogame.data.model.GameType
import org.json.JSONObject

class Converters {
    @TypeConverter
    fun fromGameType(value: GameType): String = value.name

    @TypeConverter
    fun toGameType(value: String): GameType = GameType.valueOf(value)

    @TypeConverter
    fun fromExtrasMap(value: Map<String, String>): String {
        val json = JSONObject()
        value.forEach { (k, v) -> json.put(k, v) }
        return json.toString()
    }

    @TypeConverter
    fun toExtrasMap(value: String): Map<String, String> {
        if (value.isBlank()) return emptyMap()
        val map = mutableMapOf<String, String>()
        val json = JSONObject(value)
        json.keys().forEach { key -> map[key] = json.getString(key) }
        return map
    }
}
