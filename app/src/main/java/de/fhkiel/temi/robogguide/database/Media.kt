package de.fhkiel.temi.robogguide.database

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable


@DatabaseTable(tableName = "media")
data class Media (
    @DatabaseField(generatedId = true, columnName = "id")
    val id: Int,

    @DatabaseField(canBeNull = false, columnName = "url")
    val url: String,

    @DatabaseField(canBeNull = false, foreign = true, columnName = "texts_id")
    val texts_id: Int
)
{  constructor() : this(0,"",0) }

