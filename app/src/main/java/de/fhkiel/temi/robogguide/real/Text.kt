package de.fhkiel.temi.robogguide.real

data class Text(
    val title: String,
    val text: String,
    val detailed: Boolean,
    val mediaUrls: List<String>
) {

}
