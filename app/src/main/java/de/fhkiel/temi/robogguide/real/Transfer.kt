package de.fhkiel.temi.robogguide.real

data class Transfer(
    val title: String,
    val locationFrom: Location,
    val locationTo: Location,
    val texts: List<Text>
) {
}