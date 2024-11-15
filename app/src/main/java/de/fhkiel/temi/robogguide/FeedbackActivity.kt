package de.fhkiel.temi.robogguide

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class FeedbackActivity : AppCompatActivity() {

    private lateinit var buttonGood: Button
    private lateinit var buttonNeutral: Button
    private lateinit var buttonBad: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        buttonGood = findViewById(R.id.button_good)
        buttonNeutral = findViewById(R.id.button_neutral)
        buttonBad = findViewById(R.id.button_bad)

        buttonGood.setOnClickListener {
            selectFeedback(buttonGood, Color.GREEN, "Gut")
        }

        buttonNeutral.setOnClickListener {
            selectFeedback(buttonNeutral, Color.YELLOW, "Neutral")
        }

        buttonBad.setOnClickListener {
            selectFeedback(buttonBad, Color.RED, "Schlecht")
        }
    }

    private fun selectFeedback(selectedButton: Button, color: Int, feedback: String) {
        // Set color for selected button
        selectedButton.setBackgroundColor(color)

        // Reset color for other buttons
        if (selectedButton != buttonGood) {
            buttonGood.setBackgroundColor(getColor(android.R.color.holo_green_light))
        }
        if (selectedButton != buttonNeutral) {
            buttonNeutral.setBackgroundColor(getColor(android.R.color.holo_orange_light))
        }
        if (selectedButton != buttonBad) {
            buttonBad.setBackgroundColor(getColor(android.R.color.holo_red_light))
        }

        // Speichere Feedback in einer Textdatei
        saveFeedbackToFile(feedback)
        createAndSaveTxtFile("de.fhkiel.temi.robogguide","feedbacks","test.txt","test")
        intent = Intent(this,MainActivity::class.java)
        startActivity(intent)
    }

    private fun saveFeedbackToFile(feedback: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = dateFormat.format(Date())
        val fileName = "$date-feedback.txt"

        val file = File(filesDir, fileName)
        file.appendText("Feedback: $feedback\n")
    }

    fun createAndSaveTxtFile(baseDir: String, subDir: String, fileName: String, content: String): Boolean {
        try {
            // Create the subdirectory if it doesn't exist
            val directory = File(baseDir, subDir)
            if (!directory.exists()) {
                if (!directory.mkdirs()) {
                    println("Failed to create directory: $directory")
                    return false
                }
            }

            // Create the file within the subdirectory
            val file = File(directory, fileName)
            file.writeText(content) // Write content to the file
            println("File created successfully at: ${file.absolutePath}")
            return true
        } catch (e: IOException) {
            e.printStackTrace()
            return false
        }
    }
}