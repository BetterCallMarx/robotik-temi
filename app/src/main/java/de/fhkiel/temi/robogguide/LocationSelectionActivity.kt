package de.fhkiel.temi.robogguide

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import de.fhkiel.temi.robogguide.database.DataLoader

/**
 * Activity zur auswahl von locations basierend auf einem ausgewählten ort von LevelSelect.
 * Die nutzer können mehrere Ziele auswählen welche dann
 * an die TourViewActivity weitergegben wird
 */
class LocationSelectionActivity : AppCompatActivity() {

    /**
     * set zum speichern der ausgewählten locations.
     * verwendet ein MutableSet um doppelte einträge zu vermeiden.
     */
    private val selectedLocations = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_selection)

        // views aus dem layout
        val tvLocationHeader = findViewById<TextView>(R.id.tvLocationHeader)
        val locationsContainer = findViewById<LinearLayout>(R.id.locationsContainer)
        val btnConfirmLocation = findViewById<Button>(R.id.btnConfirmLocation)

        // holt den ausgewählten ort die dauer aus dem intent von TourSelectionActivity
        val selectedPlace = intent.getStringExtra("selectedPlace")
        val selectedDauer = intent.getStringExtra("selectedDauer")

        // setzt den header text entsprechend des ausgewählten ortes
        tvLocationHeader.text = "Ziele für: $selectedPlace"

        // überprüft ob der ort gültig ist und fügt dynamisch buttons der ziele ein
        if (selectedPlace != null) {
            val index: Int = DataLoader.places.indexOfFirst { it.name == selectedPlace }
            if (index != -1) {
                val locations = DataLoader.places[index].locations

                if (locations.isNotEmpty()) {
                    locations.forEach { location ->
                        // erstell für jedes ziel einen button hinzu
                        val locationButton = Button(this).apply {
                            text = location.name
                            setBackgroundColor(Color.LTGRAY) // Standardhintergrund
                            setOnClickListener {
                                handleLocationSelection(location.name, btnConfirmLocation, this)
                            }
                        }
                        // fügt die buttons dem container hinzu
                        locationsContainer.addView(locationButton)
                    }
                } else {
                    Toast.makeText(this, "Keine Ziele für diesen Ort verfügbar.", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Ungültiger Ort ausgewählt.", Toast.LENGTH_SHORT).show()
            }
        }

        // umwandlung des umfangs in Boolean-Werte
        val isKurz = selectedDauer == "Kurz"
        val isLang = selectedDauer == "Lang"
        var isIndividuell = selectedDauer == "Individuell"

        // Listener für den bestätigungs button
        btnConfirmLocation.setOnClickListener {
            navigateToTourView(selectedPlace, isKurz, isLang, isIndividuell)
        }
    }

   // Behandelt die auswahl oder abwahl eines zieles
    private fun handleLocationSelection(
        locationName: String,
        btnConfirmLocation: Button,
        button: Button
    ) {
        if (selectedLocations.contains(locationName)) {
            // Abwählen-> hintergrund zurücksetzen und aus Set entfernen
            button.setBackgroundColor(Color.LTGRAY)
            selectedLocations.remove(locationName)
        } else {
            // Auswählen-> hintergrund auf Grün setzen und zum Set hinzufügen
            button.setBackgroundColor(Color.GREEN)
            selectedLocations.add(locationName)
        }

        // bestätigungsbutton sichtbar machen, wenn mindestens eine auswahl getroffen wurde
        btnConfirmLocation.visibility = if (selectedLocations.isNotEmpty()) {
            Button.VISIBLE
        } else {
            Button.GONE
        }
    }

    /**
     * Navigiert zur TourViewActivity und übergibt die ausgewählten Daten.
     *
     * @param selectedPlace Der ausgewählte Ort.
     * @param isKurz Boolean-Wert ob die Tour kurz ist.
     * @param isLang Boolean-Wert ob die Tour lang ist.
     * @param isIndividuell Boolean-Wert ob die Tour individuell ist.
     */
    private fun navigateToTourView(
        selectedPlace: String?,
        isKurz: Boolean,
        isLang: Boolean,
        isIndividuell: Boolean
    ) {
        val intent = Intent(this, TourViewActivity::class.java).apply {
            putExtra("selectedLocations", ArrayList(selectedLocations)) // Auswahl als ArrayList
            putExtra("selectedPlace", selectedPlace) // Ort
            putExtra("isKurz", isKurz) // "Kurz"-Boolean
            putExtra("isLang", isLang) // "Lang"-Boolean
            putExtra("isIndividuell", isIndividuell) // "Individuell"-Boolean
        }
        startActivity(intent)
    }
}
