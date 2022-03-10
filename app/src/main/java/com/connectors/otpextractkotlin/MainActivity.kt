package com.connectors.otpextractkotlin

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.util.regex.Matcher
import java.util.regex.Pattern

class MainActivity : AppCompatActivity() {

    private var myTextView: TextView? = null
    private var button: Button? = null
    private var found :Boolean? = false
    var timer : CountDownTimer? =null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        button = findViewById(R.id.button)
        myTextView = findViewById(R.id.textView)

        ActivityCompat.requestPermissions(
            this@MainActivity,
            arrayOf(Manifest.permission.READ_SMS),
            PackageManager.PERMISSION_GRANTED
        )
        button?.setOnClickListener {
            timer = object : CountDownTimer(60000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    if (millisUntilFinished > 0) {
                        val timeRemaining = (millisUntilFinished / 1000).toInt()
                        myTextView?.text = timeRemaining.toString()
                        if (timeRemaining % 5 == 0 && found == false) {
                            Read_SMS()
                        }
                    }
                }
                override fun onFinish() {

                }
            }
            (timer as CountDownTimer).start()
        }


    }
    fun Read_SMS() {
        val cursor = contentResolver.query(Uri.parse("content://sms"), null, null, null, null)
        cursor!!.moveToFirst()
        val message = cursor.getString(12)

        if (message != null) {
            val pattern: Pattern = Pattern
                .compile("^\\d+(?=\\sis)|(?<=is\\s)\\d+\\.?\$") //^\d+(?=\sis) - Matches any digit followed by space and is. (^ anchor to start of string)
            val matcher: Matcher = pattern.matcher(message)
            var otp = ""
            if (matcher.find()) {
                otp = matcher.group(0) as String
            }
            if(otp.length == 4 || otp.length == 6){
                timer?.cancel()
                found = true
                myTextView?.text  = otp
            }


        }
    }

}