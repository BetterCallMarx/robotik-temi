package de.fhkiel.temi.robogguide

import android.util.Log
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import de.fhkiel.temi.robogguide.real.Location
import de.fhkiel.temi.robogguide.real.Text
import de.fhkiel.temi.robogguide.real.Transfer
import java.lang.Thread.sleep

class TourManager(
    val mRobot: Robot,
    val inputTransfers: List<Transfer>
) : OnGoToLocationStatusChangedListener, Robot.TtsListener {
    lateinit var locationsToVisit: MutableList<Location>
    lateinit var currentLocation: Location
    var detailedFlag: Boolean = true
    var speaking: Boolean = false

    init {
        mRobot.addOnGoToLocationStatusChangedListener(this)
        mRobot.addTtsListener(this)
    }

    fun createShortTour(listLocations: MutableList<Location>, detailed: Boolean) {
        //filter by important locations
        detailedFlag = detailed
        locationsToVisit = listLocations.filter { dl -> dl.important }.toMutableList()
        Log.i("Locations", "$locationsToVisit")

        if (locationsToVisit.isNotEmpty()) {
            currentLocation = locationsToVisit[0]
            mRobot.goTo(currentLocation.name)
            Log.i("Going to", currentLocation.name)
        }

    }

    private fun createLongTour(listLocations: MutableList<Location>, detailed: Boolean) {
        //filter by important locations
        detailedFlag = detailed
        locationsToVisit = listLocations

        if (locationsToVisit.isNotEmpty()) {
            currentLocation = locationsToVisit[0]
            mRobot.goTo(currentLocation.name)
        }

    }

    private fun speak(text: String) {

        speaking = true
        val ttsRequest: TtsRequest = TtsRequest.create(speech = text, false)
        mRobot.speak(ttsRequest)

    }


    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {

        if (status == OnGoToLocationStatusChangedListener.COMPLETE) {
            Log.i("Arrived", "Bin angekommen")

            //speak out every text for the location
            speakTexts(currentLocation.texts)

            //speak every location for each item
            currentLocation.items.forEach { speakTexts(it.texts)
            }

            val nextLocationIndex: Int = (locationsToVisit.indexOf(currentLocation)) + 1

            if (nextLocationIndex < locationsToVisit.size) {
                val nextLocation: Location = locationsToVisit[nextLocationIndex]
                Log.i("Next", "Als nächstes $nextLocation")
                mRobot.goTo(nextLocation.name)

                //find the corresponding transfer and output it
                inputTransfers.filter { tr ->
                    tr.locationFrom == currentLocation && tr.locationTo == nextLocation
                }.forEach { tra ->
                    tra.texts.forEach { t ->
                        speak(t.text)
                    }

                }
                currentLocation = nextLocation
            }


        }
    }

    override fun onTtsStatusChanged(ttsRequest: TtsRequest) {
        Log.i("TTSListener", "Tjahahahah moin leute")
        if (ttsRequest.status == TtsRequest.Status.COMPLETED) {
            speaking = false
        }
    }

    private fun speakTexts(texts: List<Text>){
        speaking = true
        texts.forEach { t ->
            Log.i("Textstospeak",t.text)
            if (t.detailed == detailedFlag) {
                speak(t.text)
                while(speaking){
                    Log.i("Textlocation", t.text)
                    sleep(100)
                }
            }

        }
    }

}