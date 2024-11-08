package de.fhkiel.temi.robogguide


import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
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
import org.json.JSONObject
import java.io.IOException
import java.sql.SQLException

class MainActivity : AppCompatActivity(), OnRobotReadyListener, OnGoToLocationStatusChangedListener {


    private lateinit var mRobot: Robot
    private lateinit var mTourHelper : TourHelper
    private lateinit var tourManager: TourManager






    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DataLoader.initData(this)

        Log.i("Places" ,"${DataLoader.places}")
        Log.i("Places" ,"${DataLoader.places.filter { it.name == "C12"  }}")
        Log.i("Transfers" ,"${DataLoader.transfers}")



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
            mRobot = Robot.getInstance()
            mRobot?.hideTopBar()        // hide top action bar
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
        tourManager.createShortTour(DataLoader.places[1].locations.toMutableList(),detailed = false)
    }





    override fun onGoToLocationStatusChanged(
        location: String,
        status: String,
        descriptionId: Int,
        description: String
    ) {
        Log.i("test", "Location: $location $status")
    }


}