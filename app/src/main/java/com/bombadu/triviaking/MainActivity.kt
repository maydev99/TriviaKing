package com.bombadu.triviaking

import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    var isCorrect = false
    var correctAnswer = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

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
                var question = ""

                var incorrectList = mutableListOf<String>()



                try {
                    val myRepsonse = response.body!!.string()
                    println(myRepsonse)

                    val jsonObject = JSONObject(myRepsonse)
                    val resultsJa = jsonObject.getJSONArray("results")
                    for (i in 0 until resultsJa.length()) {
                        val jsonIndex = resultsJa.getJSONObject(i)
                        question = jsonIndex.getString("question")
                        correctAnswer = jsonIndex.getString("correct_answer")

                        val incorrectAnswersJA = jsonIndex.getJSONArray("incorrect_answers")
                        for (j in 0 until incorrectAnswersJA.length()) {
                            var incorrectAnswer = incorrectAnswersJA.getString(j)
                           // var incorrectAnswer: String = incorrectAnswersJA[i] as String
                           incorrectList.add(incorrectAnswer.toString())

                        }




                    }

                }catch (e: JSONException) {
                    e.printStackTrace()
                }

                if (response.isSuccessful) runOnUiThread {
                    questionTextView.text = question
                    incorrectList.add(correctAnswer)
                    incorrectList.shuffle()
                    var answerListView = findViewById<ListView>(R.id.answerListView)
                    var myAdapter = ArrayAdapter<String> (this@MainActivity, android.R.layout.simple_list_item_1, incorrectList)
                    answerListView.adapter = myAdapter
                    answerListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                        var selectedAnswer = answerListView.getItemAtPosition(position)
                        isCorrect = selectedAnswer.equals(correctAnswer)
                        showResultDialog()

                    }
                }
            }
        })

    }

    private fun showResultDialog() {
        val dialog = AlertDialog.Builder(this)
        if(isCorrect){
            dialog.setTitle("Correct")
            dialog.setMessage("Good Job!")
        } else {
            dialog.setTitle("Incorrect")
            dialog.setMessage("The correct answer is $correctAnswer")
        }

        dialog.setPositiveButton("Continue") {dialog, which ->
            fetchData()

        }

        dialog.show()

    }
}
