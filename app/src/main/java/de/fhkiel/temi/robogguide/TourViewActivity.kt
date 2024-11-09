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
): AppCompatActivity() {

    private lateinit var currentLocationName: TextView
    private lateinit var currentItemName: TextView



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tour_view)

        val selectedLocation = intent.getStringArrayListExtra("selectedLocations")
        val selectedUmfang = intent.getStringExtra("selectedUmfang")
        val selectedPlace = intent.getStringExtra("selectedPlace")
        Log.i("test", selectedLocation.toString())

        val isKurz = intent.getBooleanExtra("isKurz", false)
        val isLang = intent.getBooleanExtra("isLang", false)
        val isIndividuell = intent.getBooleanExtra("isIndividuell", false)


        //TODO ABfrage über art und dann ausführung von createtour und  register

        if(isKurz){

        }


        mTourManager.registerAsTourStopListener{doTourStop()}


        val currentLocationName: TextView = findViewById(R.id.currentLocation)
        val currentItemName: TextView = findViewById(R.id.currentItem)


        val mediaItems : List<Text> = DataLoader.places[2].locations[1].texts
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        val adapter = MediaAdapter(this, mediaItems)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

    }

    fun doTourStop(){
        thread {

            //speak out every text for the location
            mTourManager.speakTexts(mTourManager.currentLocation.texts)

            runOnUiThread {
               currentLocationName.text  = mTourManager.currentLocation.name
            }

            sleep(500)

            //speak every location for each item
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