package de.fhkiel.temi.robogguide

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * Die TourSelectionActivity ermöglicht dem Benutzer eine führung basierend auf dauer
 * ( kurz, lang, individuell und umfang (einfach, ausführlich) auszuwählen.
 * je nach auswahl wird eine andere aktivität gestartet.
 */
class TourSelectionActivity : AppCompatActivity() {

    // variablen zur speichern der auswahl
    var isKurzSelected = false
    var isLangSelected = false
    var isIndividuellSelected = false
    var isEinfachSelected = false
    var isAusführlichSelected = false

    /**
     * wird aufgerufen, wenn die aktivität erstellt wird.
     * erstellt die Benutzeroberfläche.
     *
     * @param savedInstanceState Zustand der Aktivität, falls diese neu erstellt wurde
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tour_options)

        // UI-Elemente
        val radioGroupDauer = findViewById<RadioGroup>(R.id.radioGroupDauer)
        val radioGroupUmfang = findViewById<RadioGroup>(R.id.radioGroupUmfang)
        val btnConfirmSelection = findViewById<Button>(R.id.btnConfirmSelection)
        val tvSelectedPlace = findViewById<TextView>(R.id.tvSelectedPlace)
        val tvDauerAuswahl = findViewById<TextView>(R.id.tvDauerAuswahl)
        val tvUmfangAuswahl = findViewById<TextView>(R.id.tvUmfangAuswahl)

        // holt den ausgewählten Ort aus dem intent von LevelSelect
        val selectedPlace = intent.getStringExtra("selectedPlace")
        selectedPlace?.let {
            tvSelectedPlace.text = "Ausgewählter Ort: $it"
        }

        // funktion zur sichtbarkeit des bestätigen buttons, im bezug ob die entsprechenden voraussetzungen erfüllt sind
        val onSelectionChanged = {
            btnConfirmSelection.visibility = if (
                (isKurzSelected || isLangSelected || isIndividuellSelected) &&
                (isEinfachSelected || isAusführlichSelected)
            ) {
                Button.VISIBLE
            } else {
                Button.GONE
            }
        }

        // Listener für änderungen bei der dauer auswahl
        radioGroupDauer.setOnCheckedChangeListener { _, checkedId ->
            isKurzSelected = false
            isLangSelected = false
            isIndividuellSelected = false

            when (checkedId) {
                R.id.rbKurz -> isKurzSelected = true
                R.id.rbLang -> isLangSelected = true
                R.id.rbIndividuell -> isIndividuellSelected = true
            }

            val selectedDauer = when {
                isKurzSelected -> "Kurz"
                isLangSelected -> "Lang"
                isIndividuellSelected -> "Individuell"
                else -> "Nicht ausgewählt"
            }
            tvDauerAuswahl.text = "Dauer der Führung: $selectedDauer"
            onSelectionChanged()
        }

        // Listener für änderungen bei der umfang auswahl
        radioGroupUmfang.setOnCheckedChangeListener { _, checkedId ->
            isEinfachSelected = false
            isAusführlichSelected = false

            when (checkedId) {
                R.id.rbEinfach -> isEinfachSelected = true
                R.id.rbAusführlich -> isAusführlichSelected = true
            }

            val selectedUmfang = when {
                isEinfachSelected -> "Einfach"
                isAusführlichSelected -> "Ausführlich"
                else -> "Nicht ausgewählt"
            }
            tvUmfangAuswahl.text = "Umfang der Erklärung: $selectedUmfang"
            onSelectionChanged()
        }

        /**
         * startet die nächste activity basierend auf der auswahl und übergibt die ausgewählten daten.
         *
         * wenn individuell ausgewählt wurde, wird zur LocationSelectionActivity navigiert.
         * bei kurz oder lang wird direkt zur TourViewActivity navigiert.
         *
         * @param selectedPlace der ausgewählte ort.
         * @param isEinfachSelected boolean-wert, ob einfach ausgewählt wurde.
         * @param isAusführlichSelected boolean-wert, ob ausführlich ausgewählt wurde.
         * @param isKurzSelected boolean-wert, ob die tour kurz ist.
         * @param isLangSelected boolean-wert, ob die tour lang ist.
         * @param isIndividuellSelected boolean-wert, ob die tour individuell ist.
         */
        btnConfirmSelection.setOnClickListener {
            if (isIndividuellSelected) {
                val intent = Intent(this, LocationSelectionActivity::class.java).apply {
                    putExtra("selectedPlace", selectedPlace)
                    putExtra("isEinfachSelected", isEinfachSelected)
                    putExtra("isAusführlichSelected", isAusführlichSelected)
                    putExtra("isIndividuell", isIndividuellSelected)
                }
                startActivity(intent)
            } else if (isKurzSelected || isLangSelected) {
                val intent = Intent(this, TourViewActivity::class.java).apply {
                    putExtra("selectedPlace", selectedPlace)
                    putExtra("isEinfachSelected", isEinfachSelected)
                    putExtra("isAusführlichSelected", isAusführlichSelected)
                    putExtra("isLang", isLangSelected)
                    putExtra("isKurzSelected", isKurzSelected)
                }
                startActivity(intent)
            }
        }

        // Listener der zurück zu LevelSelect führt und ein remove der den selectedPlace löscht, damit er erneut weiter gegeben werden kann
        val btnBackToLevelSelect = findViewById<Button>(R.id.btnBackToLevelSelect)
        btnBackToLevelSelect.setOnClickListener {
            getSharedPreferences("TourPrefs", MODE_PRIVATE).edit().apply {
                remove("selectedPlace")
                apply()
            }
            startActivity(Intent(this, LevelSelect::class.java))
            finish()
        }
    }
}
