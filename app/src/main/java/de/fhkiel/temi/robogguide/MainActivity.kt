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

    /**
     * Function called upon creation of this Activity
     *
     * @param savedInstanceState
     */
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        DataLoader.initData(this)


        findViewById<Button>(R.id.Tour).setOnClickListener {
            val intent = Intent(this, LevelSelect::class.java)
            startActivity(intent)
        }


        findViewById<Button>(R.id.btnExitApp).setOnClickListener {
            finishAffinity()
        }
    }

    /**
     * Function that is called upon start of the Robot
     * Gets the instance of the Robot and adds a onRobotReadyListener
     *
     */
    override fun onStart() {
        super.onStart()
        Robot.getInstance().addOnRobotReadyListener(this)
    }

    /**
     * Method called upon Stop of Robot
     * removes any added Listeners
     */
    override fun onStop() {
        super.onStop()
        Robot.getInstance().removeOnRobotReadyListener(this)
    }

    /**
     * Method called upon destruction of robot
     *
     */
    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     * called when robot is ready
     *
     * @param isReady whether robot instance is ready or not
     */
    override fun onRobotReady(isReady: Boolean) {
        if (isReady) {
            mRobot = Robot.getInstance()
            mRobot.hideTopBar()        // hide top action bar
            // hide pull-down bar
            val activityInfo: ActivityInfo =
                packageManager.getActivityInfo(componentName, PackageManager.GET_META_DATA)
            Robot.getInstance().onStart(activityInfo)
            tourManager = TourManager(mRobot, DataLoader.transfers)
        }
    }


}