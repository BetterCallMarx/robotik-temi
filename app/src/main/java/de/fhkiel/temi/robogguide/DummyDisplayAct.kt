package de.fhkiel.temi.robogguide

import android.util.Log
import de.fhkiel.temi.robogguide.real.Location
import de.fhkiel.temi.robogguide.real.Transfer
import kotlin.concurrent.thread

class DummyDisplayAct(
    val mTourManager: TourManager
) {

    init {
        mTourManager.registerAsTourStopListener{doTourStop()}
    }

    fun doTourStop(){
        thread {
            Log.i("Arrived", "Bin angekommen")

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