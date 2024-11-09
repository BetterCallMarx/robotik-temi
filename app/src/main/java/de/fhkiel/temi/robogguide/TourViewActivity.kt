package de.fhkiel.temi.robogguide

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.fhkiel.temi.robogguide.database.DataLoader
import de.fhkiel.temi.robogguide.real.Location
import de.fhkiel.temi.robogguide.real.Text
import de.fhkiel.temi.robogguide.real.Transfer
import java.lang.Thread.sleep
import kotlin.concurrent.thread

class TourViewActivity(
    val mTourManager: TourManager
) : AppCompatActivity() {

    private lateinit var currentLocationName: TextView
    private lateinit var currentItemName: TextView

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tour_view)

        // Hole die übergebenen Parameter aus dem Intent
        val selectedLocation = intent.getStringArrayListExtra("selectedLocations") // Keine Typargumente nötig
        val selectedUmfang = intent.getStringExtra("selectedUmfang")
        val selectedPlace = intent.getStringExtra("selectedPlace")
        Log.i("TourView", "Selected locations: $selectedLocation")
        Log.i("TourView", "Selected umfang: $selectedUmfang")

        val isKurz = intent.getBooleanExtra("isKurz", false)
        val isLang = intent.getBooleanExtra("isLang", false)
        val isIndividuell = intent.getBooleanExtra("isIndividuell", false)

        // Wenn "Individuell" ausgewählt wurde, die spezifische Tour vorbereiten
        if (isIndividuell) {
            // Hier kannst du die Logik für eine individuell anpassbare Tour hinzufügen
            setupIndividualTour(selectedLocation, selectedUmfang)
        }

        // Starte die Registrierung für Tourstopps
        mTourManager.registerAsTourStopListener { doTourStop() }

        // Initialisiere Views
        val currentLocationName: TextView = findViewById(R.id.currentLocation)
        val currentItemName: TextView = findViewById(R.id.currentItem)

        // Beispiel für die Items für die Touransicht (kann dynamisch angepasst werden)
        val mediaItems: List<Text> = DataLoader.places[2].locations[1].texts
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        val adapter = MediaAdapter(this, mediaItems)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    // Methode zur Einrichtung einer individuellen Tour
    private fun setupIndividualTour(selectedLocation: ArrayList<String>?, selectedUmfang: String?) {
        // Hier kannst du mit den übergebenen Parametern die Tour individuell gestalten
        // Beispielweise könntest du diese Daten nutzen, um Tourdetails zu laden
        if (selectedLocation != null && selectedLocation.isNotEmpty()) {
            Log.i("TourView", "Individuelle Tour für Orte: $selectedLocation")
            // Weiterverarbeitung je nach Umfang (z.B. Umfang der Erklärung anpassen)
            when (selectedUmfang) {
                "Einfach" -> {
                    // Beispiel: Einfache Tour mit weniger Erläuterungen
                }
                "Ausführlich" -> {
                    // Beispiel: Ausführliche Tour mit mehr Erläuterungen
                }
                else -> {
                    // Standard oder leere Auswahl
                }
            }
        }
    }

    // Die Methode für den Tour-Stopp-Listener, die bei jeder neuen Station aufgerufen wird
    fun doTourStop() {
        thread {
            // Alle Texte für den aktuellen Standort sprechen
            mTourManager.speakTexts(mTourManager.currentLocation.texts)

            runOnUiThread {
                currentLocationName.text = mTourManager.currentLocation.name
            }

            sleep(500)

            // Alle Items des aktuellen Standorts sprechen
            mTourManager.currentLocation.items.forEach {
                mTourManager.speakTexts(it.texts)
            }

            Log.i("Arrived", "Bin angekommen")
            val nextLocationIndex: Int = (mTourManager.locationsToVisit.indexOf(mTourManager.currentLocation)) + 1

            if (nextLocationIndex < mTourManager.locationsToVisit.size) {
                val nextLocation: Location = mTourManager.locationsToVisit[nextLocationIndex]
                Log.i("Next", "Als nächstes $nextLocation")
                mTourManager.mRobot.goTo(nextLocation.name)

                val transferText: List<Transfer> = mTourManager.inputTransfers.filter { tr ->
                    tr.locationFrom == mTourManager.currentLocation && tr.locationTo == nextLocation
                }

                sleep(1000)
                transferText.forEach { t -> mTourManager.speakTextsTransfer(t.texts) }

                mTourManager.currentLocation = nextLocation
            }
        }
    }
}
