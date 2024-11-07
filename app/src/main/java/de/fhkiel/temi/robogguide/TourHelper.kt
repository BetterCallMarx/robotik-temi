package de.fhkiel.temi.robogguide

import android.util.Log
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import org.json.JSONObject

class TourHelper(val locationsForTour: List<String>, val mRobot: Robot) : OnGoToLocationStatusChangedListener {

    private var lastLocation : String = ""
    private var currentLocation : String = ""
    private  var nextLocation: String = ""
    private var currentStatus: String = ""

    init {
        mRobot.addOnGoToLocationStatusChangedListener(this)
    }


    fun shortTour(){
        Log.i("TourHelper", "$locationsForTour")
        if(locationsForTour.isNotEmpty()){
            currentLocation = locationsForTour[0]
            mRobot.goTo(currentLocation)
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
            val nextLocation = locationsForTour.indexOf(currentLocation)+1
            if(nextLocation<locationsForTour.size){
                currentLocation = locationsForTour[nextLocation]
                mRobot.goTo(currentLocation)

            }
        }
    }

}