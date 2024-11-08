package de.fhkiel.temi.robogguide

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TourSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tour_options)

        // RadioGroups für die Auswahl
        val radioGroupDauer = findViewById<RadioGroup>(R.id.radioGroupDauer)
        val radioGroupUmfang = findViewById<RadioGroup>(R.id.radioGroupUmfang)
        val btnConfirmSelection = findViewById<Button>(R.id.btnConfirmSelection)

        // TextViews für die ausgewählten Optionen
        val tvSelectedPlace = findViewById<TextView>(R.id.tvSelectedPlace)
        val tvDauerAuswahl = findViewById<TextView>(R.id.tvDauerAuswahl)
        val tvUmfangAuswahl = findViewById<TextView>(R.id.tvUmfangAuswahl)

        // Hole den ausgewählten Ort aus dem Intent
        val selectedPlace = intent.getStringExtra("selectedPlace")

        // Wenn der Ort vorhanden ist, setze ihn in die TextView
        if (selectedPlace != null) {
            tvSelectedPlace.text = "Ausgewählter Ort: $selectedPlace"
        }

        // Variablen zur Überprüfung, ob beide Optionen ausgewählt sind
        var isDauerSelected = false
        var isUmfangSelected = false

        // Logik für die Sichtbarkeit des Bestätigungsbuttons
        val onSelectionChanged = {
            // Button nur anzeigen, wenn sowohl Dauer als auch Umfang ausgewählt wurden
            btnConfirmSelection.visibility = if (isDauerSelected && isUmfangSelected) {
                Button.VISIBLE
            } else {
                Button.GONE
            }
        }

        // Listener für Änderungen bei den RadioGroups
        radioGroupDauer.setOnCheckedChangeListener { _, checkedId ->
            isDauerSelected = checkedId != -1 // Überprüfe, ob eine Auswahl getroffen wurde
            val selectedDauer = when (checkedId) {
                R.id.rbKurz -> "Kurz"
                R.id.rbLang -> "Lang"
                R.id.rbIndividuell -> "Individuell" // Hier wird die Individuell-Option behandelt
                else -> "Nicht ausgewählt"
            }
            tvDauerAuswahl.text = "Dauer der Führung: $selectedDauer"
            onSelectionChanged() // Prüfe nach jeder Auswahl, ob der Button angezeigt werden soll
        }

        radioGroupUmfang.setOnCheckedChangeListener { _, checkedId ->
            isUmfangSelected = checkedId != -1 // Überprüfe, ob eine Auswahl getroffen wurde
            val selectedUmfang = when (checkedId) {
                R.id.rbEinfach -> "Einfach"
                R.id.rbAusführlich -> "Ausführlich"
                else -> "Nicht ausgewählt"
            }
            tvUmfangAuswahl.text = "Umfang der Erklärung: $selectedUmfang"
            onSelectionChanged() // Prüfe nach jeder Auswahl, ob der Button angezeigt werden soll
        }

        // Button, um die Auswahl zu bestätigen
        btnConfirmSelection.setOnClickListener {
            // Weiterleitung zu einer anderen Aktivität, die die Tour startet oder bestätigt
            // Du kannst die Daten über ein Intent übergeben, um mit den Details fortzufahren
        }
    }
}
