package de.fhkiel.temi.robogguide

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import de.fhkiel.temi.robogguide.database.DataLoader
import de.fhkiel.temi.robogguide.real.Location
import de.fhkiel.temi.robogguide.real.Text
import de.fhkiel.temi.robogguide.real.Transfer
import kotlin.concurrent.thread

class TourViewActivity(
    val mTourManager: TourManager
): AppCompatActivity() {



    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tour_view)

        mTourManager.registerAsTourStopListener{doTourStop()}



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

                transferText.forEach { t -> mTourManager.speakTextsTransfer(t.texts) }

                mTourManager.currentLocation = nextLocation
            }

        }

    }

}