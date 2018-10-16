package com.jumio.sample.kotlin

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.jumio.core.enums.JumioDataCenter
import com.jumio.core.exceptions.MissingPermissionException
import com.jumio.core.exceptions.PlatformNotSupportedException
import com.jumio.dv.DocumentVerificationSDK
import com.jumio.sample.R

/**
 * Copyright 2018 Jumio Corporation All rights reserved.
 */
class DocumentVerificationFragment : Fragment(), View.OnClickListener {

    private var apiToken: String? = null
    private var apiSecret: String? = null

    internal lateinit var documentVerificationSDK: DocumentVerificationSDK

    companion object {
        private val TAG = "JumioSDK_DV"
        private val PERMISSION_REQUEST_CODE_DOCUMENT_VERIFICATION = 301
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val rootView = inflater.inflate(R.layout.fragment_main, container, false)
        rootView.findViewById<View>(R.id.switchOptionOne).visibility = View.GONE
        rootView.findViewById<View>(R.id.switchOptionTwo).visibility = View.GONE
        rootView.findViewById<View>(R.id.tvOptions).visibility = View.GONE

        apiToken = arguments!!.getString(MainActivity.KEY_API_TOKEN)
        apiSecret = arguments!!.getString(MainActivity.KEY_API_SECRET)

        val startSDK = rootView.findViewById<View>(R.id.btnStart) as Button
        startSDK.text = java.lang.String.format(resources.getString(R.string.button_start), resources.getString(R.string.section_documentverification))
        startSDK.setOnClickListener(this)

        return rootView
    }

    override fun onClick(view: View) {
        //Since the DocumentVerificationSDK is a singleton internally, a new instance is not
        //created here.
        initializeDocumentVerificationSDK()

        if ((activity as MainActivity).checkPermissions(PERMISSION_REQUEST_CODE_DOCUMENT_VERIFICATION)) {
            try {
                startActivityForResult(documentVerificationSDK.intent, DocumentVerificationSDK.REQUEST_CODE)
            } catch (e: MissingPermissionException) {
                Toast.makeText(activity, e.message, Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun initializeDocumentVerificationSDK() {
        try {
            // You can get the current SDK version using the method below.
            // DocumentVerificationSDK.getSDKVersion();

            // Call the method isSupportedPlatform to check if the device is supported.
            if (!DocumentVerificationSDK.isSupportedPlatform(activity))
                Log.w(TAG, "Device not supported")

            // Applications implementing the SDK shall not run on rooted devices. Use either the below
            // method or a self-devised check to prevent usage of SDK scanning functionality on rooted
            // devices.
            if (DocumentVerificationSDK.isRooted(activity))
                Log.w(TAG, "Device is rooted")

            // To create an instance of the SDK, perform the following call as soon as your activity is initialized.
            // Make sure that your merchant API token and API secret are correct and specify an instance
            // of your activity. If your merchant account is created in the EU data center, use
            // JumioDataCenter.EU instead.
            documentVerificationSDK = DocumentVerificationSDK.create(activity, apiToken, apiSecret, JumioDataCenter.US)

            // One of the configured DocumentTypeCodes: BC, BS, CAAP, CB, CCS, CRC, HCC, IC, LAG, LOAP,
            // MEDC, MOAP, PB, SEL, SENC, SS, STUC, TAC, TR, UB, SSC, VC, VT, WWCC, CUSTOM
            documentVerificationSDK.setType("BC")

            // ISO 3166-1 alpha-3 country code
            documentVerificationSDK.setCountry("USA")

            // The merchant scan reference allows you to identify the scan (max. 100 characters).
            // Note: Must not contain sensitive data like PII (Personally Identifiable Information) or account login.
            documentVerificationSDK.setMerchantScanReference("YOURSCANREFERENCE")

            // You can also set a customer identifier (max. 100 characters).
            // Note: The customer ID should not contain sensitive data like PII (Personally Identifiable Information) or account login.
            documentVerificationSDK.setCustomerId("CUSTOMERID")

            // Set the following property to enable/disable data extraction for documents.
//            documentVerificationSDK.setEnableExtraction(true);

            // One of the Custom Document Type Codes as configurable by Merchant in Merchant UI.
//            documentVerificationSDK.setCustomDocumentCode("YOURCUSTOMDOCUMENTCODE");

            // Overrides the label for the document name (on Help Screen below document icon)
//            documentVerificationSDK.setDocumentName("DOCUMENTNAME");

            // Use the following property to identify the scan in your reports (max. 255 characters).
//            documentVerificationSDK.setMerchantReportingCriteria("YOURREPORTINGCRITERIA");

            // Callback URL for the confirmation after the verification is completed. This setting overrides your Jumio merchant settings.
//            documentVerificationSDK.setCallbackUrl("YOURCALLBACKURL");

            // Use the following method to set the default camera position.
//            documentVerificationSDK.setCameraPosition(JumioCameraPosition.FRONT);

            // Use the following method to override the SDK theme that is defined in the Manifest with a custom Theme at runtime
//            documentVerificationSDK.setCustomTheme(R.style.YOURCUSTOMTHEMEID);

        } catch (e: PlatformNotSupportedException) {
            android.util.Log.e(DocumentVerificationFragment.TAG, "Error in initializeNetverifySDK: ", e)
            Toast.makeText(activity!!.applicationContext, "This platform is not supported", Toast.LENGTH_LONG).show()
        } catch (e1: NullPointerException) {
            android.util.Log.e(DocumentVerificationFragment.TAG, "Error in initializeNetverifySDK: ", e1)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DocumentVerificationSDK.REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                //Handle the result for the DocumentVerification SDK
                if (data == null) {
                    return
                }
            }
            //At this point, the SDK is not needed anymore. It is highly advisable to call destroy(), so that
            //internal resources can be freed.
            documentVerificationSDK.destroy()
        }
    }
}