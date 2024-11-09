package de.fhkiel.temi.robogguide

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import de.fhkiel.temi.robogguide.database.DataLoader

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

        // Variablen für jede Auswahl
        var isKurzSelected = false
        var isLangSelected = false
        var isIndividuellSelected = false

        var isEinfachSelected = false
        var isAusführlichSelected = false

        // Logik für die Sichtbarkeit des Bestätigungsbuttons
        val onSelectionChanged = {
            // Button nur anzeigen, wenn mindestens eine Dauer und Umfang ausgewählt wurden
            btnConfirmSelection.visibility = if ((isKurzSelected || isLangSelected || isIndividuellSelected) &&
                (isEinfachSelected || isAusführlichSelected)) {
                Button.VISIBLE
            } else {
                Button.GONE
            }
        }

        // Listener für Änderungen bei den RadioGroups für Dauer
        radioGroupDauer.setOnCheckedChangeListener { _, checkedId ->
            // Zurücksetzen der Boolean-Werte für Dauer
            isKurzSelected = false
            isLangSelected = false
            isIndividuellSelected = false

            // Überprüfen, welche Auswahl getroffen wurde
            when (checkedId) {
                R.id.rbKurz -> isKurzSelected = true
                R.id.rbLang -> isLangSelected = true
                R.id.rbIndividuell -> isIndividuellSelected = true
            }

            // Update der TextView für Dauer
            val selectedDauer = when {
                isKurzSelected -> "Kurz"
                isLangSelected -> "Lang"
                isIndividuellSelected -> "Individuell"
                else -> "Nicht ausgewählt"
            }
            tvDauerAuswahl.text = "Dauer der Führung: $selectedDauer"
            onSelectionChanged() // Prüfe nach jeder Auswahl, ob der Button angezeigt werden soll
        }


       // var index: Int = DataLoader.places.indexOf(selectedPlace)

       // DataLoader.places.get(index).locations

        // Listener für Änderungen bei den RadioGroups für Umfang
        radioGroupUmfang.setOnCheckedChangeListener { _, checkedId ->
            // Zurücksetzen der Boolean-Werte für Umfang
            isEinfachSelected = false
            isAusführlichSelected = false

            // Überprüfen, welche Auswahl getroffen wurde
            when (checkedId) {
                R.id.rbEinfach -> isEinfachSelected = true
                R.id.rbAusführlich -> isAusführlichSelected = true
            }

            // Update der TextView für Umfang
            val selectedUmfang = when {
                isEinfachSelected -> "Einfach"
                isAusführlichSelected -> "Ausführlich"
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

        btnConfirmSelection.setOnClickListener {
                if (isIndividuellSelected==true) {

                    // Weiter zu LocationSelectionActivity, wenn Individuell ausgewählt wurde
                    val intent = Intent(this, LocationSelectionActivity::class.java)
                    intent.putExtra("selectedPlace", selectedPlace) // Übergebe den Ort
                    startActivity(intent)
                }

        }


    }
}
