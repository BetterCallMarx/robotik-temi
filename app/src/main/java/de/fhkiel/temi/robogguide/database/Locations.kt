package de.fhkiel.temi.robogguide.database

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

/**
 * Orm Data class to get the locations table
 *
 * @property id
 * @property name
 * @property important
 * @property places_id
 */
@DatabaseTable(tableName = "locations")
data class Locations(

    @DatabaseField(generatedId = true, columnName = "id")
    val id: Int,

    @DatabaseField(columnName = "name")
    var name: String,

    @DatabaseField(columnName = "important")
    val important: Int,

    @DatabaseField(columnName = "places_id")
    val places_id: Int

) {
    constructor() : this(0,"",0,0)
}