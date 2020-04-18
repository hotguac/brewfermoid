package com.example.brewferm


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.particle.android.sdk.cloud.ParticleCloudSDK
import kotlinx.android.synthetic.main.fragment_readings.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

import java.lang.Exception
import android.util.Log
import android.widget.ProgressBar
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_readings.progressBar2

/**
 * A simple [Fragment] subclass.
 */
class ReadingsFragment : Fragment(), CoroutineScope {
    private var systemStatus: String = "unknown"
    private var beerTarget: String = "0.0"
    private var beerActual: String = "0.0"
    private var chamberTarget: String = "0.0"
    private var chamberActual: String = "0.0"
    private var heatCoolDesired: String = "??"
    private var heatCoolActual: String = "??"
    private var heatPercent: String = "??.?%"
    private var coolPercent: String = "??.?%"
    private var beerITerm: String = "??.?%"
    private var chamberITerm: String = "??.?%"

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_readings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        job = Job()
        get_readings()

        btnRefresh.setOnClickListener {
            get_readings()
        }

        txtBeerValues.setOnClickListener {
            set_target(view)
        }

        txtSystemValues.setOnClickListener {
            togglePause()
        }
    }

    private fun togglePause() {
        progressBar2.visibility = ProgressBar.VISIBLE

        val cloud = ParticleCloudSDK.getCloud()
        var result = 0

        val job = GlobalScope.launch(Dispatchers.IO) {
            try {
                val myDevice = cloud.getDevice("2b0023000e51353532343635")

                if (heatCoolDesired.startsWith("Paused")) {
                    val args: List<String> = listOf("NO")
                    result = myDevice.callFunction("setPauseState", args)
                    Log.i("BrewFerm2", "setPauseState call result: ${result}")
                } else {
                    val args: List<String> = listOf("YES")
                    result = myDevice.callFunction("setPauseState", args)
                    Log.i("BrewFerm2", "setPauseState call result: ${result}")
                }

            } catch (e: Exception) {
                Log.e("BrewFerm2", "${e.message}")
            } finally {
                delay(12000)
                progressBar2.visibility = View.INVISIBLE
                //get_readings()
            }
        }

    }

    fun set_target(view: View) {
        view.findNavController().navigate(R.id.action_readingsFragment_to_setTargetFragment)
    }

    fun get_readings() {
        progressBar2.visibility = View.VISIBLE

        val cloud = ParticleCloudSDK.getCloud()

        val job = GlobalScope.launch(Dispatchers.IO) {
            try {
                val myDevice = cloud.getDevice("2b0023000e51353532343635")

                systemStatus = myDevice.getStringVariable("SystemStatus").toString()
                Log.i("BrewFerm2", "System Status: ${systemStatus}")

                val myValues: List<String> = systemStatus.split("|")

                heatCoolDesired = myValues[0].substring(3)
                heatCoolActual = myValues[1].substring(3)
                Log.i("BrewFerm2", "Desired: ${heatCoolDesired}")
                Log.i("BrewFerm2", "Current: ${heatCoolActual}")

                beerTarget = myValues[2].substring(3)
                beerActual = myValues[3].substring(3)
                Log.i("BrewFerm2", "Beer Target: ${beerTarget}")
                Log.i("BrewFerm2", "Beer Actual: ${beerActual}")

                chamberTarget = myValues[4].substring(3)
                chamberActual = myValues[5].substring(3)
                Log.i("BrewFerm2", "Chamber Target: ${chamberTarget}")
                Log.i("BrewFerm2", "Chamber Actual: ${chamberActual}")


                heatPercent = myValues[7].substring(3)
                coolPercent = myValues[8].substring(3)

                beerITerm = myValues[9].substring(3)
                chamberITerm = myValues[10].substring(3)

                withContext(Dispatchers.Main) {
                    txtBeerValues.setText("Beer: ${beerActual} Target: ${beerTarget}")
                    txtChamberValues.setText("Chamber: ${chamberActual} Target: ${chamberTarget}")
                    txtSystemValues.setText("Want: ${heatCoolDesired} Got: ${heatCoolActual}")
                    txtPercentValues.setText("Heat: ${heatPercent}% Cool: ${coolPercent}%")
                    txtITermValues.setText("bITerm: ${beerITerm} cITerm: ${chamberITerm}")
                }

            } catch (e: Exception) {
                Log.e("BrewFerm2", "${e.message}")
            } finally {
                progressBar2.visibility = View.INVISIBLE
            }
        } // launch
    }

}
