package com.awra.catalogue.data.database

import androidx.room.TypeConverter
import com.awra.catalogue.data.model.Difficulty
import com.awra.catalogue.data.model.ExpressiveShape
import com.awra.catalogue.data.model.MaterialType
import com.awra.catalogue.data.model.Stream

class Converters {
    @TypeConverter
    fun fromStream(stream: Stream): String = stream.name

    @TypeConverter
    fun toStream(value: String): Stream = Stream.valueOf(value)

    @TypeConverter
    fun fromMaterialType(type: MaterialType): String = type.name

    @TypeConverter
    fun toMaterialType(value: String): MaterialType = MaterialType.valueOf(value)

    @TypeConverter
    fun fromDifficulty(difficulty: Difficulty): String = difficulty.name

    @TypeConverter
    fun toDifficulty(value: String): Difficulty = Difficulty.valueOf(value)

    @TypeConverter
    fun fromExpressiveShape(shape: ExpressiveShape): String = shape.name

    @TypeConverter
    fun toExpressiveShape(value: String): ExpressiveShape = ExpressiveShape.valueOf(value)

    @TypeConverter
    fun fromStringList(list: List<String>): String = list.joinToString("|||")

    @TypeConverter
    fun toStringList(value: String): List<String> = value.split("|||")
}
