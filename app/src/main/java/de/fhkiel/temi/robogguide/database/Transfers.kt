package de.fhkiel.temi.robogguide.database

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

@DatabaseTable(tableName = "transfers")
data class Transfers (

    @DatabaseField(generatedId = true, columnName = "id")
    val id: Int,

    @DatabaseField(columnName = "title")
    val title: String,

    @DatabaseField(columnName = "location_from", foreign = true)
    val location_from: Int,

    @DatabaseField(columnName = "location_to", foreign = true)
    val location_to: Int,
)
{
    constructor(): this(0,"",0,0)
}
