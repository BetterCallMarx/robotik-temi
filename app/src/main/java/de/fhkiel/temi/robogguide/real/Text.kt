package de.fhkiel.temi.robogguide.real

/**
 * Converted data class from corresponding orm data class, for easier use
 *
 * @property title
 * @property text
 * @property detailed
 * @property mediaUrls
 */
data class Text(
    val title: String,
    val text: String,
    val detailed: Boolean,
    val mediaUrls: List<String>
) {

}
