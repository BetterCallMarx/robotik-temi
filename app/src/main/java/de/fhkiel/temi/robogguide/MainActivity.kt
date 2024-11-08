package de.fhkiel.temi.robogguide


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
    private lateinit var mTourHelper : TourHelper
    private lateinit var tourManager: TourManager

    private lateinit var dummyText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DataLoader.initData(this)

        dummyText = findViewById<TextView>(R.id.dummyText)

        Log.i("Places" ,"${DataLoader.places}")
        Log.i("Places" ,"${DataLoader.places.filter { it.name == "C12"  }}")
        Log.i("Transfers" ,"${DataLoader.transfers}")

        findViewById<Button>(R.id.Tour).setOnClickListener {
            val intent = Intent(this, LevelSelect::class.java)
            startActivity(intent)
        }



        // let robot speak on button click
        findViewById<Button>(R.id.btnSpeakHelloWorld).setOnClickListener {
            speakHelloWorld("Hallo")
        }


        findViewById<Button>(R.id.btnSpeakLocations).setOnClickListener {
            speakLocations()
        }

        findViewById<Button>(R.id.btnCancelSpeak).setOnClickListener {
            mRobot?.cancelAllTtsRequests()
        }

        findViewById<Button>(R.id.btnGotoHomeBase).setOnClickListener {
            gotoHomeBase()
           // shortTour()
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
        //database.closeDatabase()
        //ormhelper.close()
    }

    override fun onRobotReady(isReady: Boolean) {
        if (isReady){

            Log.i("test", "reADY")
            mRobot = Robot.getInstance()
            mRobot.hideTopBar()        // hide top action bar
            // hide pull-down bar
            val activityInfo: ActivityInfo = packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA)
            Robot.getInstance().onStart(activityInfo)
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
        var list: MutableList<Location> = DataLoader.places[0].locations.toMutableList()
        tourManager = TourManager(mRobot,DataLoader.transfers)
        tourManager.registerAsTourStopListener{doTourStop()}
        tourManager.createShortTour(DataLoader.places[1].locations.toMutableList(),detailed = false)
        //runOnUiThread
    }

    fun doTourStop(){
        thread {
            Log.i("Arrived", "Bin angekommen")

            runOnUiThread {
                dummyText.text = tourManager.currentLocation.name
            }

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