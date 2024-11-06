package de.fhkiel.temi.robogguide.database

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "items")
data class Items(
    @DatabaseField(generatedId = true, columnName = "id")
    val id: Int,

    @DatabaseField(columnName = "name")
    val name: String,

    @DatabaseField(columnName = "locations_id", foreign = true)
    val locations_id: Int
) {

    constructor(): this(0,"",0)
}