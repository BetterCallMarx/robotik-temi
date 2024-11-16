package de.fhkiel.temi.robogguide.real

/**
 * Converted data class from corresponding orm data class, for easier use
 *
 * @property id
 * @property name
 * @property important
 * @property texts
 * @property items
 */
data class Location(
    val id: Int,
    var name: String,
    val important: Boolean,
    val texts: List<Text>,
    val items: List<Item>



) {
}
