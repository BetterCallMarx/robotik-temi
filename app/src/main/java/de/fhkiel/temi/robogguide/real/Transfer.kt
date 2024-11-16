package de.fhkiel.temi.robogguide.real

/**
 * Converted data class from corresponding orm data class, for easier use
 *
 * @property title
 * @property locationFrom
 * @property locationTo
 * @property texts
 */
data class Transfer(
    val title: String,
    val locationFrom: Location,
    val locationTo: Location,
    val texts: List<Text>
) {
}