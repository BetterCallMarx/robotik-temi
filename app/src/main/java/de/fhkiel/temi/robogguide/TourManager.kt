package de.fhkiel.temi.robogguide

import android.util.Log
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import de.fhkiel.temi.robogguide.database.Items
import de.fhkiel.temi.robogguide.database.Locations
import de.fhkiel.temi.robogguide.database.Places
import de.fhkiel.temi.robogguide.database.Texts
import de.fhkiel.temi.robogguide.database.Transfers
import de.fhkiel.temi.robogguide.real.Location
import de.fhkiel.temi.robogguide.real.Transfer
import org.json.JSONObject

class TourManager(
    val inputLocations: MutableList<Location>,
    val mRobot: Robot,
    val inputTransfers: MutableList<Transfer>
): OnGoToLocationStatusChangedListener
{
    lateinit var locationsToVisit : MutableList<Location>
    lateinit var currentLocation: Location
    var detailedFlag: Boolean = true

    private fun createShortTour(listLocations: MutableList<Location>, detailed: Boolean){
        //filter by important locations
        detailedFlag = detailed
        locationsToVisit = listLocations.filter { dl -> dl.important }.toMutableList()

        if(locationsToVisit.isNotEmpty()){
            currentLocation = locationsToVisit[0]
            mRobot.goTo(currentLocation.name)
        }

    }

    private fun createLongTour(listLocations: MutableList<Location>, detailed: Boolean){
        //filter by important locations
        detailedFlag = detailed
        locationsToVisit = listLocations

        if(locationsToVisit.isNotEmpty()){
            currentLocation = locationsToVisit[0]
            mRobot.goTo(currentLocation.name)
        }

    }

    fun speakText(text: String, isShowOnConversationLayer: Boolean = true){
        mRobot.let { robot ->
            val ttsRequest: TtsRequest = TtsRequest.create(speech = text, isShowOnConversationLayer = isShowOnConversationLayer)
            robot.speak(ttsRequest)
        }
    }














    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {

        if(status == OnGoToLocationStatusChangedListener.COMPLETE){
            //speak out every text for the location
            currentLocation.texts.forEach{t ->
                if(t.detailed == detailedFlag){
                    speakText(t.text)
                }

            }
            //speak every location for each item
            currentLocation.items.forEach{i ->
                i.texts.forEach{t ->

                    if(t.detailed == detailedFlag){
                        speakText(t.text)
                    }
                }
            }
            val nextLocationIndex: Int = locationsToVisit.indexOf(currentLocation)+1
            if(nextLocationIndex < locationsToVisit.size){
                val nextLocation : Location = locationsToVisit[nextLocationIndex]
                mRobot.goTo(nextLocation.name)

                //find the corresponding transfer and output it
                inputTransfers.filter { tr ->
                    tr.locationFrom == currentLocation && tr.locationTo == nextLocation
                }.forEach{ tra ->
                    tra.texts.forEach{ t ->
                        speakText(t.text)
                    }

                }

            }




        }
    }


}