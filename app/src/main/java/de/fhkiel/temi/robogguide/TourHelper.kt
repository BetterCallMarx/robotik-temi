package de.fhkiel.temi.robogguide

import android.location.Location
import android.util.Log
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import de.fhkiel.temi.robogguide.database.Items
import de.fhkiel.temi.robogguide.database.Locations
import de.fhkiel.temi.robogguide.database.Places
import de.fhkiel.temi.robogguide.database.Texts
import de.fhkiel.temi.robogguide.database.Transfers
import org.json.JSONObject

class TourHelper(
    val locations: List<Locations>,
    val texts: List<Texts>,
    val items: List<Items>,
    val transfers: List<Transfers>,
    val locationsForTour: List<String>,
    val mRobot: Robot

    ) : OnGoToLocationStatusChangedListener {

    private var lastLocation : String = ""
    private var currentLocationID : String = ""
    private var currentLocationText: String = ""
    private  var nextLocation: String = ""
    private var currentStatus: String = ""

    //lists that holds the Texts, that are supposed to be spoken out
    private var textsForLocations: Map<Int, Texts> =  mapOf<Int,Texts>()
    private var textsForItems: Map<Items,Texts> = mapOf<Items,Texts>()
    private var textsForTransfers: Map<Transfers,Texts> = mapOf<Transfers,Texts>()







    init {
        mRobot.addOnGoToLocationStatusChangedListener(this)
        textsForLocations = mapListsLocations()
    }

    fun mapListsLocations() : Map<Int,Texts>{
        val textByLocationID = texts
            .filter{it.locations_id != null}
            .associateBy { it.locations_id!!}

        return locations.associate{ location -> location.id to textByLocationID[location.id]!! }
    }

/*
    fun shortTour(){

        Log.i("TourHelper", "$locationsForTour")

        if(locationsForTour.isNotEmpty()){
            currentLocation = locationsForTour[0]
            mRobot.goTo(currentLocation)
        }

    }
*/

    fun shortTour(locationsForTour: List<Locations>){
        /*
        val itemsLocations: MutableList<Items> = mutableListOf()

        itemsLocations.forEach{ it ->

        }
        */

        if(locationsForTour.isNotEmpty()){
            if(locationsForTour[0].important == 1){
                currentLocation = locationsForTour[0].name
                mRobot.goTo(currentLocation)

            }
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

            currentLocationText =
            speakText()
            //speak(locations) the text about the location first

            //speak(items) talk about each item

            val nextLocation = locationsForTour.indexOf(currentLocation)+1
            if(nextLocation<locationsForTour.size){
                currentLocation = locationsForTour[nextLocation]
                mRobot.goTo(currentLocation)
                //speak(transfer) speak about transfer

            }
        }
    }

}