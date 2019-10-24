package com.bombadu.triviaking

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONException
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var textView = findViewById<TextView>(R.id.helloworldTV);

        fetchData()
    }

    private fun fetchData() {
        var myResponse = ""
        var url = "https://opentdb.com/api.php?amount=1&category=22&difficulty=medium&type=multiple"
        val request = Request.Builder().url(url).build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //Do nothing
            }

            override fun onResponse(call: Call, response: Response) {

                try {
                    myResponse = response.body!!.string()
                    println(myResponse)
                }catch (e: JSONException) {
                    e.printStackTrace()
                }

                if (response.isSuccessful) runOnUiThread {
                    helloworldTV.text = myResponse
                }


            }


        })

    }
}
