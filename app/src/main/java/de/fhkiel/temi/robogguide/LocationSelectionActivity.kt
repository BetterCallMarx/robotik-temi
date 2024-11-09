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

        // Hole den ausgewählten Ort aus dem Intent
        val selectedPlace = intent.getStringExtra("selectedPlace")
        val tvLocationHeader = findViewById<TextView>(R.id.tvLocationHeader)
        val locationsContainer = findViewById<LinearLayout>(R.id.locationsContainer)
        val btnConfirmLocation = findViewById<Button>(R.id.btnConfirmLocation)

        // Umfang der Erklärung (beispielsweise "Kurz", "Individuell", etc.)
        val selectedUmfang = "Kurz"  // Hier kannst du die Auswahl dynamisch setzen, wenn du ein entsprechendes UI-Element hast

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

        // Listener für den Bestätigungsbutton
        btnConfirmLocation.setOnClickListener {
            // Weiterleiten der ausgewählten Locations und des Umfangs an TourViewActivity
            val intent = Intent(this, TourViewActivity::class.java).apply {
                // Umwandeln des Sets in eine ArrayList
                putExtra("selectedLocations", ArrayList(selectedLocations)) // Set in ArrayList umwandeln
                putExtra("selectedPlace", selectedPlace) // Ort
                putExtra("selectedUmfang", selectedUmfang) // Umfang der Erklärung
            }
            startActivity(intent)
        }
    }
}

