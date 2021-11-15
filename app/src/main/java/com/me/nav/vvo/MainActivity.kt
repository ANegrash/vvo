package com.me.nav.vvo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import com.me.nav.vvo.model.FlightModel
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.lang.reflect.Type

class MainActivity : AppCompatActivity() {

    private val YESTERDAY : Int = 0
    private val TODAY : Int = 1
    private val TOMORROW : Int = 2

    val today = LocalDate.now()
    val tomorrow = today.plusDays(1)
    val yesterday = today.minusDays(1)

    var datesArray = arrayOf("Yesterday", "Today", "Tomorrow")
    var currentDateVar = 0

    private val DEPARTURE : Int = 0
    private val ARRIVAL : Int = 1

    var typesArray = arrayOf("Departure", "Arrival")
    var currentTypeVar = 0

    override fun onCreate (savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val spinnerDates = findViewById<Spinner>(R.id.spinner_dates)
        val spinnerTypes = findViewById<Spinner>(R.id.spinner_flightTypes)

        datesArray[0] = "Yesterday (" + yesterday.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ")"
        datesArray[1] = "Today (" + today.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ")"
        datesArray[2] = "Tomorrow (" + tomorrow.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) + ")"

        setListViewContent(currentTypeVar, currentDateVar)

        val adapterDates = ArrayAdapter(this, android.R.layout.simple_spinner_item, datesArray)
        adapterDates.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDates.adapter = adapterDates
        spinnerDates.setSelection(currentDateVar)

        val adapterTypes = ArrayAdapter(this, android.R.layout.simple_spinner_item, typesArray)
        adapterTypes.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTypes.adapter = adapterTypes
        spinnerTypes.setSelection(currentTypeVar)

        val itemSelectedListenerDates: AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected (
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                setFrameLayoutContent(0, 1)
                currentDateVar = position
                setListViewContent(currentTypeVar, currentDateVar)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerDates.onItemSelectedListener = itemSelectedListenerDates

        val itemSelectedListenerTypes: AdapterView.OnItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected (
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                setFrameLayoutContent(0, 1)
                currentTypeVar = position
                setListViewContent(currentTypeVar, currentDateVar)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        spinnerTypes.onItemSelectedListener = itemSelectedListenerTypes
    }

    private fun setListViewContent (
        type: Int = DEPARTURE,
        date: Int = TODAY
    ) {
        var url = "https://vvo.aero/php/ajax_xml.php?action=filter"

        if (type == DEPARTURE) {
            url += "&type=departure"
        } else if (type == ARRIVAL) {
            url += "&type=arrival"
        }

        when (date) {
            YESTERDAY -> {
                url += "&date=" + yesterday.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            }
            TODAY -> {
                url += "&date=" + today.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            }
            TOMORROW -> {
                url += "&date=" + tomorrow.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
            }
        }

        val getResponse = Get()

        getResponse.run(
            url,
            object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        //here will be error check
                    }
                }

                @Throws(IOException::class)
                override fun onResponse(call: Call, response: Response) {
                    if (response.body != null) {
                        val stringResponse = response.body!!.string()
                        val listView = findViewById<ListView>(R.id.flightList)
                        val builder = GsonBuilder()
                        val gson: Gson = builder.create()

                        val typeToken: Type = object : TypeToken<List<FlightModel?>?>() {}.type
                        val listOfFlights: List<FlightModel> = gson.fromJson(stringResponse, typeToken)

                        runOnUiThread {
                            val stateAdapter = FlightListAdapter(applicationContext, R.layout.list_item, listOfFlights, type)
                            listView.adapter = stateAdapter
                            setFrameLayoutContent(1, 0)
                        }
                    } else {
                        runOnUiThread {
                            setFrameLayoutContent(0, 1)
                        }
                    }
                }
            }
        )
    }

    private fun setFrameLayoutContent (
        list: Int = 1,
        loading: Int = 0
    ) {
        val listView = findViewById<ListView>(R.id.flightList)
        val loadingLayout = findViewById<ConstraintLayout>(R.id.loading_layout)

        when (list) {
            0 -> listView.visibility = View.GONE
            1 -> listView.visibility = View.VISIBLE
        }

        when (loading) {
            0 -> loadingLayout.visibility = View.GONE
            1 -> loadingLayout.visibility = View.VISIBLE
        }
    }
}