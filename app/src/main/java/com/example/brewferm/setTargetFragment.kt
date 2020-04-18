package com.example.brewferm


import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import io.particle.android.sdk.cloud.ParticleCloudSDK
import kotlinx.android.synthetic.main.fragment_set_target.*
import kotlinx.coroutines.*
import java.lang.Exception

/**
 * A simple [Fragment] subclass.
 */
class setTargetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_set_target, container, false)
    }


    fun sendTemp(view: View) {
        progressBar2.visibility = ProgressBar.VISIBLE

        val cloud = ParticleCloudSDK.getCloud()

        val job = GlobalScope.launch(Dispatchers.IO) {
            try {
                val myDevice = cloud.getDevice("2b0023000e51353532343635")
                val temp = "${txtBeerTarget.text.toString()}"

                var args: List<String> = listOf(temp)

                val result = myDevice.callFunction("setBeerTarget", args)
                if (result != 0) {
                    txtBeerTarget.setBackgroundColor(Color.parseColor("#AA0000"))
                } else {
                    txtBeerTarget.setBackgroundColor(Color.parseColor("#00AA00"))
                    delay(12000)
                    withContext(Dispatchers.Main) {
                        view.findNavController().navigateUp()
                    }
                }
                //systemStatus = myDevice.getStringVariable("SystemStatus").toString()
                Log.i("BrewFerm2", "Call Status: ${result}")

            } catch (e: Exception) {
                Log.e("BrewFerm2", "${e.message}")
            } finally {
                progressBar2.visibility = View.INVISIBLE
            }
        }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        progressBar2.visibility = ProgressBar.INVISIBLE

        btnSetTarget.setOnClickListener {
            sendTemp(view)
        }
    }
}
