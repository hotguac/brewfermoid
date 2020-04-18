package com.example.brewferm

import android.util.Log

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.coroutines.CoroutineScope

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

import io.particle.android.sdk.cloud.ParticleCloudSDK
import io.particle.android.sdk.cloud.exceptions.ParticleLoginException


/**
 * A simple [Fragment] subclass.
 */

class LoginFragment : Fragment(), CoroutineScope {

    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Main + job

    private lateinit var job: Job

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    fun doLogin(x: View) {
        val cloud = ParticleCloudSDK.getCloud()

//        if (cloud.isLoggedIn) {
//            runBlocking (Dispatchers.IO) {
//                cloud.logOut()
//            }
//            Log.i("LoginFragment.kt","logged out")
//        }

        Log.i(
            "LoginFragment.kt", "Using username '" + txtUsername.text +
                    "' password '" + txtPassword.text + "'"
        )

        GlobalScope.launch(Dispatchers.IO) {
            try {
                cloud.logIn("${txtUsername.text}", "${txtPassword.text}")

                if (cloud.isLoggedIn) {
                    Log.i("LoginFragment.kt", "login OK")

                    withContext(Dispatchers.Main) {
                        btnSignIn.isEnabled = true
                        x.findNavController()
                            .navigate(R.id.action_loginFragment_to_readingsFragment)
                    }
                } else {
                    //
                }

            } catch (e: ParticleLoginException) {
                Log.e("LoginFragment.kt", "login failed" + e.toString())
            } catch (e: Exception) {
                Log.e("LoginFragment.kt", "login failed" + e.toString())
            }

        } // run blocking

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        job = Job()

        btnSignIn.setOnClickListener {
            btnSignIn.isEnabled = false
            doLogin(view)

//            val cloud = ParticleCloudSDK.getCloud()
//
//            if (cloud.isLoggedIn) {
//                view.findNavController().navigate(R.id.action_loginFragment_to_readingsFragment)
//            }


        }
    }

// end of
}
