package de.fhkiel.temi.robogguide

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import de.fhkiel.temi.robogguide.database.DataLoader

/**
 * Die LevelSelect Klasse ist für die auswahl des Ortes.
 * Der Benutzer kann einen Ort aus einer Liste auswählen und zur nächsten aktivität gehen,
 * oder zur hauptseite zurückkehren.
 */
class LevelSelect : AppCompatActivity() {

    /**
     * wird aufgerufen wenn die aktivität erstellt wird.
     * hier wird die benutzeroberfläche initialisiert, die Orte geladen und der Event Listener gesetzt.
     *
     * @param savedInstanceState Zustand der Aktivität, falls diese neu erstellt wurde
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.level_select)

        // inistiallierung der Orte aus DataLoader
        DataLoader.initData(this)

        // spinner fürs anzeigen der orte
        val placesSpinner: Spinner = findViewById(R.id.places_spinner)

        // erstellt eine liste fürs anzeigen der orte
        val placeNames = DataLoader.places.map { it.name }

        // adapter für den spinner um die orte dazustellen
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, placeNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // setzt adapter auf spinner
        placesSpinner.adapter = adapter

        // button um die auswahl zu bestätigen
        findViewById<Button>(R.id.btnConfirmPlace).setOnClickListener {
            // holt den aktull ausgewählten ort
            val selectedPlace = placesSpinner.selectedItem.toString()

            // intent um zur TourSelectionActivity zu gehen
            val intent = Intent(this, TourSelectionActivity::class.java)

            // übertragt den ausgewählten ort zur nächsten aktivität
            intent.putExtra("selectedPlace", selectedPlace)

            // startet TourSelectionActivity
            startActivity(intent)
        }

        // button um zurück zur main zu gehen
        findViewById<Button>(R.id.buttonBackToMain).setOnClickListener {
            // intent um zurück zu  main zu gelangen
            val intent = Intent(this, MainActivity::class.java)

            // start der main
            startActivity(intent)

            // beendet die aktuelle aktivität
            finish()
        }
    }
}
