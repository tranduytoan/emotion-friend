package com.emotionfriend.data.local

import androidx.room.TypeConverter
import com.emotionfriend.domain.model.EmotionType

class Converters {

    @TypeConverter
    fun fromEmotionType(value: EmotionType): String = value.name

    @TypeConverter
    fun toEmotionType(value: String): EmotionType = EmotionType.valueOf(value)

    @TypeConverter
    fun fromEmotionTypeNullable(value: EmotionType?): String? = value?.name

    @TypeConverter
    fun toEmotionTypeNullable(value: String?): EmotionType? =
        value?.let { EmotionType.valueOf(it) }

    @TypeConverter
    fun fromEmotionTypeList(value: List<EmotionType>): String =
        value.joinToString(separator = ",") { it.name }

    @TypeConverter
    fun toEmotionTypeList(value: String): List<EmotionType> =
        if (value.isBlank()) emptyList()
        else value.split(",").map { EmotionType.valueOf(it.trim()) }
}
