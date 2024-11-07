package de.fhkiel.temi.robogguide.database

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "texts")
data class Texts(
    @DatabaseField(generatedId = true, columnName = "id")
    val id: Int,

    @DatabaseField(columnName = "title")
    val title: String,

    @DatabaseField(columnName = "text")
    val text: String,

    @DatabaseField(columnName = "detailed")
    val detailed: Int,

    @DatabaseField(columnName = "locations_id")
    val locations_id: Int,

    @DatabaseField(columnName = "items_id")
    val items_id: Int,

    @DatabaseField(columnName = "transfers_id")
    val transfers_id: Int,



) {
    constructor(): this(0,"", "", 0,0,0,0)
}