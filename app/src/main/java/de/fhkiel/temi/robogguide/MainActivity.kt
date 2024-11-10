package de.fhkiel.temi.robogguide


import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.robotemi.sdk.Robot
import com.robotemi.sdk.TtsRequest
import com.robotemi.sdk.listeners.OnGoToLocationStatusChangedListener
import com.robotemi.sdk.listeners.OnRobotReadyListener
import de.fhkiel.temi.robogguide.database.DataLoader
import de.fhkiel.temi.robogguide.database.DatabaseHelper
import de.fhkiel.temi.robogguide.database.Items
import de.fhkiel.temi.robogguide.database.Locations
import de.fhkiel.temi.robogguide.database.Media
import de.fhkiel.temi.robogguide.database.OrmHelper
import de.fhkiel.temi.robogguide.database.Places
import de.fhkiel.temi.robogguide.database.Texts
import de.fhkiel.temi.robogguide.database.Transfers
import de.fhkiel.temi.robogguide.real.Location
import de.fhkiel.temi.robogguide.real.Transfer
import org.json.JSONObject
import java.io.IOException
import java.sql.SQLException
import kotlin.concurrent.thread


class MainActivity : AppCompatActivity(), OnRobotReadyListener {


    private lateinit var mRobot: Robot
    private lateinit var tourManager: TourManager
    private lateinit var dummyText: TextView


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DataLoader.initData(this)



        //Log.i("Places" ,"${DataLoader.places}")
        //Log.i("Places" ,"${DataLoader.places.filter { it.name == "C12"  }}")
        //Log.i("Transfers" ,"${DataLoader.transfers}")

        findViewById<Button>(R.id.Tour).setOnClickListener {
            val intent = Intent(this, LevelSelect::class.java)
            startActivity(intent)
        }



        findViewById<Button>(R.id.btnGotoHomeBase).setOnClickListener {
            gotoHomeBase()
        }

        findViewById<Button>(R.id.btnExitApp).setOnClickListener {
            finishAffinity()
        }
    }



    override fun onStart() {
        super.onStart()
        Robot.getInstance().addOnRobotReadyListener(this)
    }

    override fun onStop() {
        super.onStop()
        Robot.getInstance().removeOnRobotReadyListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onRobotReady(isReady: Boolean) {
        if (isReady){
            mRobot = Robot.getInstance()
            mRobot.hideTopBar()        // hide top action bar
            // hide pull-down bar
            val activityInfo: ActivityInfo = packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA)
            Robot.getInstance().onStart(activityInfo)
            tourManager = TourManager(mRobot,DataLoader.transfers)
        }
    }

    private fun speakHelloWorld(text: String, isShowOnConversationLayer: Boolean = true){
        mRobot?.let { robot ->
            val ttsRequest: TtsRequest = TtsRequest.create(speech = text, isShowOnConversationLayer = isShowOnConversationLayer)
            robot.speak(ttsRequest)
        }
    }

    private fun speakLocations(){
        mRobot?.let { robot ->
            var text = "Das sind alle Orte an die ich gehen kann:"
            robot.locations.forEach {
                text += " $it,"
            }
            speakHelloWorld(text, isShowOnConversationLayer = false)
        }
    }

    private fun gotoHomeBase(){

        tourManager.mRobot.goTo("home base")
        /*
        var list: MutableList<Location> = DataLoader.places[0].locations.toMutableList()
       // tourManager = TourManager(mRobot,DataLoader.transfers)
        tourManager.createLongTour(DataLoader.places[1].locations.toMutableList(),detailed = true)
        tourManager.registerAsTourStopListener{doTourStop()}

         */
    }




    fun doTourStop(){

        thread {
            Log.i("Arrived", "Bin angekommen")


            //speak out every text for the location
            tourManager.speakTexts(tourManager.currentLocation.texts)

            //speak every location for each item
            tourManager.currentLocation.items.forEach {
                tourManager.speakTexts(it.texts)
            }

            Log.i("Arrived", "Bin angekommen")
            val nextLocationIndex: Int = (tourManager.locationsToVisit.indexOf(tourManager.currentLocation)) + 1

            if (nextLocationIndex < tourManager.locationsToVisit.size) {
                val nextLocation: Location = tourManager.locationsToVisit[nextLocationIndex]
                Log.i("Next", "Als nächstes $nextLocation")
                tourManager.mRobot.goTo(nextLocation.name)

                val transferText: List<Transfer> = tourManager.inputTransfers.filter { tr ->
                    tr.locationFrom == tourManager.currentLocation && tr.locationTo == nextLocation
                }

                transferText.forEach { t -> tourManager.speakTextsTransfer(t.texts) }

                tourManager.currentLocation = nextLocation
            }

        }

    }


}