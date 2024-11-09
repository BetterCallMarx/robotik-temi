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

class LocationSelectionActivity : AppCompatActivity() {

    // Set zum Speichern der ausgewählten Locations
    private val selectedLocations = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location_selection)

        // Hole den ausgewählten Ort und Umfang aus dem Intent
        val selectedPlace = intent.getStringExtra("selectedPlace")
        val selectedDauer = intent.getStringExtra("selectedDauer") // Dies kann "Kurz", "Lang", "Individuell" sein
        val tvLocationHeader = findViewById<TextView>(R.id.tvLocationHeader)
        val locationsContainer = findViewById<LinearLayout>(R.id.locationsContainer)
        val btnConfirmLocation = findViewById<Button>(R.id.btnConfirmLocation)

        // Überprüfe, ob der Ort gültig ist
        if (selectedPlace != null) {
            // Finde den Index des ausgewählten Ortes
            val index: Int = DataLoader.places.indexOfFirst { it.name == selectedPlace }
            if (index != -1) {
                // Hole die Locations des Ortes
                val locations = DataLoader.places[index].locations

                // Wenn Locations vorhanden sind, füge sie dynamisch als Buttons hinzu
                if (locations.isNotEmpty()) {
                    locations.forEach { location ->
                        val locationButton = Button(this).apply {
                            text = location.name
                            setBackgroundColor(Color.LTGRAY) // Setze Standardhintergrund
                            setOnClickListener {
                                if (selectedLocations.contains(location.name)) {
                                    // Abwählen: Hintergrund zurücksetzen und aus Set entfernen
                                    setBackgroundColor(Color.LTGRAY)
                                    selectedLocations.remove(location.name)
                                } else {
                                    // Auswählen: Hintergrund auf Grün setzen und zum Set hinzufügen
                                    setBackgroundColor(Color.GREEN)
                                    selectedLocations.add(location.name)
                                }

                                // Bestätigungsbutton sichtbar machen, wenn mindestens eine Auswahl getroffen wurde
                                btnConfirmLocation.visibility = if (selectedLocations.isNotEmpty()) {
                                    Button.VISIBLE
                                } else {
                                    Button.GONE
                                }
                            }
                        }
                        locationsContainer.addView(locationButton)
                    }
                } else {
                    Toast.makeText(this, "Keine Ziele für diesen Ort verfügbar.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Umwandlung des Umfangs (Dauer) in Boolean-Werte
        val isKurz = selectedDauer == "Kurz"
        val isLang = selectedDauer == "Lang"
        val isIndividuell = selectedDauer == "Individuell"

        // Listener für den Bestätigungsbutton
        btnConfirmLocation.setOnClickListener {
            // Weiterleiten der ausgewählten Locations und des Umfangs an TourViewActivity
            val intent = Intent(this, TourViewActivity::class.java).apply {
                // Umwandeln des Sets in eine ArrayList
                putExtra("selectedLocations", ArrayList(selectedLocations)) // Set in ArrayList umwandeln
                putExtra("selectedPlace", selectedPlace) // Ort
                putExtra("isKurz", isKurz) // Boolean für "Kurz"
                putExtra("isLang", isLang) // Boolean für "Lang"
                putExtra("isIndividuell", isIndividuell) // Boolean für "Individuell"
            }
            startActivity(intent)
        }
    }
}
