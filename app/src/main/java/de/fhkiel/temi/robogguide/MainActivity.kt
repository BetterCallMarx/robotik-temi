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


    private var mRobot: Robot? = null
    private lateinit var mTourHelper : TourHelper






    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DataLoader.initData(this)
        mTourHelper = TourHelper(mRobot)
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

    private fun createTour() {
        /*
        val places: Map<String, JSONObject> = database.getTableDataAsJson("places") // Fetch data as JSON
        val databaseLocations: Map<String, JSONObject> = database.getTableDataAsJson("locations") // Fetch data as JSON
        Log.i("MainActivity", "Places: $places")
        Log.i("MainActivity", "Locations: $databaseLocations")
*/
        //mTourHelper = TourHelper(databaseLocations.values.map { jsonObject -> jsonObject.getString("name") }, mRobot!!)
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
            createTour()

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
        var list: List<Location> = DataLoader.places[0].locations
        mTourHelper.shortTour(list)
    }



    private fun sortData(){

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