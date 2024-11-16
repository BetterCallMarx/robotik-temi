package de.fhkiel.temi.robogguide.database

import com.j256.ormlite.field.DatabaseField
import com.j256.ormlite.table.DatabaseTable

/**
 * ORM data class for places table
 *
 * @property id
 * @property name
 */
@DatabaseTable(tableName = "places")
data class Places(
    @DatabaseField(generatedId = true, columnName = "id")
    val id: Int,

    @DatabaseField(columnName = "name")
    val name: String

) {
    constructor(): this(0,"")
}