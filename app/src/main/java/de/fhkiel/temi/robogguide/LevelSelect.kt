package de.fhkiel.temi.robogguide

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import de.fhkiel.temi.robogguide.database.DataLoader

class LevelSelect : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.level_select)

        // Initialisiere die Daten aus dem DataLoader
        DataLoader.initData(this)

        // Spinner für die Orte
        val placesSpinner: Spinner = findViewById(R.id.places_spinner)

        // Erstelle eine Liste der Platznamen
        val placeNames = DataLoader.places.map { it.name }

        // Adapter für den Spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, placeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Setze den Adapter auf den Spinner
        placesSpinner.adapter = adapter

        // Button zum Bestätigen der Auswahl
        findViewById<Button>(R.id.btnConfirmPlace).setOnClickListener {
            // Hole den ausgewählten Ort aus dem Spinner
            val selectedPlace = placesSpinner.selectedItem.toString()

            // Intent, um zum TourSelectionActivity zu navigieren
            val intent = Intent(this, TourSelectionActivity::class.java)

            // Füge den ausgewählten Ort als Extra zum Intent hinzu
            intent.putExtra("selectedPlace", selectedPlace)

            // Starte die TourSelectionActivity und übergebe den Ort
            startActivity(intent)
        }
    }
}
