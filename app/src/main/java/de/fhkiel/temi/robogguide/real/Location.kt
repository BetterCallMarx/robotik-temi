package de.fhkiel.temi.robogguide.real

data class Location(
    val id: Int,
    var name: String,
    val important: Boolean,
    val texts: List<Text>,
    val items: List<Item>



) {
}
