package com.au10tix.sampleapp.nfc

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import com.au10tix.backend.Au10Backend
import com.au10tix.backend.BackendCallback
import com.au10tix.faceliveness.FaceLivenessFeatureManager
import com.au10tix.passport.PassportFeatureManager
import com.au10tix.sdk.commons.Au10Error
import com.au10tix.sdk.core.Au10xCore
import com.au10tix.sdk.core.OnPrepareCallback
import com.au10tix.sdk.protocol.Au10Update
import com.au10tix.sdk.protocol.FeatureSessionError
import com.au10tix.sdk.protocol.FeatureSessionResult
import com.au10tix.sdk.ui.Au10UIManager
import com.au10tix.sdk.ui.UICallback

class MainFragment : Fragment() {

    companion object {
        private const val TAG = "Au10"
        private const val AU10TIX_BEARER_TOKEN = "xxx.xxx.xxx"
    }

    private var uiManager: Au10UIManager? = null
    private lateinit var dialog: AlertDialog
    private lateinit var startButton: Button
    private lateinit var sendButton: Button

    private var passportResult: FeatureSessionResult? = null
    private var pflResult: FeatureSessionResult? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.main_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        startButton = view.findViewById(R.id.start)
        sendButton = view.findViewById(R.id.sendResults)
        if (!Au10xCore.isPrepared()) {
            dialog =
                AlertDialog.Builder(context).setMessage("Preparing SDK").setCancelable(false).show()
            Au10xCore.prepare(context, AU10TIX_BEARER_TOKEN, object : OnPrepareCallback {
                override fun onPrepareError(error: Au10Error) {
                    dialog.dismiss()
                    Toast.makeText(context, "Failed to prepare", Toast.LENGTH_SHORT).show()
                    startButton.isEnabled = false
                }

                override fun onPrepared(sessionId: String) {
                    dialog.dismiss()
                }
            })
        }

        startButton.setOnClickListener {
            startPassport()
        }

        if (passportResult != null && pflResult != null) {
            sendButton.visibility = View.VISIBLE
        } else {
            sendButton.visibility = View.GONE
        }
        sendButton.setOnClickListener {
            val backendCallback = object : BackendCallback {
                override fun onSuccess(requestId: String) {
                    Toast.makeText(context, "Successfully sent", Toast.LENGTH_SHORT).show()
                    Log.v(TAG, "Passport data sent. request id: $requestId")
                    passportResult = null
                    pflResult = null
                    sendButton.visibility = View.GONE
                }

                override fun onError(error: String) {
                    Toast.makeText(context, "Passport data send failed", Toast.LENGTH_SHORT).show()
                    Log.v(TAG, "Passport data send failed. error: $error")
                }
            }

            Au10Backend.sendPassport(backendCallback)
        }
    }

    private fun startPassport() {
        uiManager = Au10UIManager.Builder(
            requireActivity(),
            PassportFeatureManager(requireActivity(), this),
            object : UICallback() {
                override fun onSessionResult(sessionResult: FeatureSessionResult) {
                    Log.v(TAG, "passport result")
                    passportResult = sessionResult
                    NavHostFragment.findNavController(this@MainFragment).navigateUp()
                    startPFL()
                }

                override fun onSessionError(error: FeatureSessionError) {
                    Log.v(TAG, "passport error: ${error.errorMessage}")
                }

                override fun onSessionUpdate(frame: Au10Update) {
                }

                override fun onFail(sessionResult: FeatureSessionResult) {
                }
            }).build()
        startFragment(uiManager!!.generateFragment())
    }

    private fun startPFL() {
        uiManager?.destroy()
        uiManager = Au10UIManager.Builder(
            requireActivity(),
            FaceLivenessFeatureManager(requireActivity(), this),
            object : UICallback() {
                override fun onSessionResult(sessionResult: FeatureSessionResult) {
                    Log.v(TAG, "pfl result")
                    pflResult = sessionResult
                    NavHostFragment.findNavController(this@MainFragment).navigateUp()
                    uiManager?.destroy()
                }

                override fun onSessionError(error: FeatureSessionError) {
                    Log.v(TAG, "pfl error: ${error.errorMessage}")
                }

                override fun onSessionUpdate(frame: Au10Update) {
                }

                override fun onFail(sessionResult: FeatureSessionResult) {
                }
            }).build()
        startFragment(uiManager!!.generateFragment())
    }

    private fun startFragment(fragment: Fragment?) {
        if (fragment != null) {
            NavHostFragment.findNavController(this)
                .navigate(R.id.start_au10Fragment, fragment.arguments)
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        uiManager?.destroy()
    }
}