package de.fhkiel.temi.robogguide.database

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

/**
 * Orm Data Class to to get the items table
 *
 * @property id
 * @property name
 * @property locations_id
 */

@DatabaseTable(tableName = "items")
data class Items(
    @DatabaseField(generatedId = true, columnName = "id")
    val id: Int,

    @DatabaseField(columnName = "name")
    val name: String,

    @DatabaseField(columnName = "locations_id")
    val locations_id: Int
) {

    constructor(): this(0,"",0)
}