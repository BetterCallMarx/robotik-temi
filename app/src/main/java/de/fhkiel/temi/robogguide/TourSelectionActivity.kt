package de.fhkiel.temi.robogguide

import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity

class TourSelectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tour_options)

        val radioGroupDauer = findViewById<RadioGroup>(R.id.radioGroupDauer)
        val radioGroupUmfang = findViewById<RadioGroup>(R.id.radioGroupUmfang)
        val switchIndividuell = findViewById<Switch>(R.id.switchIndividuell)
        val btnConfirmSelection = findViewById<Button>(R.id.btnConfirmSelection)

        val onSelectionChanged = {
            btnConfirmSelection.visibility = when {
                radioGroupDauer.checkedRadioButtonId != -1 ||
                        radioGroupUmfang.checkedRadioButtonId != -1 ||
                        switchIndividuell.isChecked -> Button.VISIBLE
                else -> Button.GONE
            }
        }

        radioGroupDauer.setOnCheckedChangeListener { _, _ -> onSelectionChanged() }
        radioGroupUmfang.setOnCheckedChangeListener { _, _ -> onSelectionChanged() }
        switchIndividuell.setOnCheckedChangeListener { _, _ -> onSelectionChanged() }
    }
}
