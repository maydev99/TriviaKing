package com.bombadu.triviaking

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.lang.StringBuilder

class MainActivity : AppCompatActivity() {

    private val client = OkHttpClient()
    var isCorrect = false
    var correctAnswer = ""
    var questionNum = 0
    var score = 0.0
    var numOfQuestions = 20


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        var scoreTextView = findViewById<TextView>(R.id.scoreTextView)

        fetchData()
    }

    private fun fetchData() {
        var myResponse = ""
        val url = "https://opentdb.com/api.php?amount=1"
        //var url = "https://opentdb.com/api.php?amount=1&category=22&difficulty=medium&type=multiple"
        val request = Request.Builder().url(url).build()
        val call = client.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                //Do nothing
            }

            override fun onResponse(call: Call, response: Response) {
                var question = ""
                var answerList = mutableListOf<String>()



                try {
                    var myRepsonse = response.body!!.string()
                    //var myResponseClean = cleanResponse(myResponse)
                    println(myRepsonse)

                    val jsonObject = JSONObject(myRepsonse)
                    val resultsJa = jsonObject.getJSONArray("results")
                    for (i in 0 until resultsJa.length()) {
                        val jsonIndex = resultsJa.getJSONObject(i)
                        question = jsonIndex.getString("question")
                        //var cleanedQuestion = cleanResponse(question)
                        //question.
                        correctAnswer = jsonIndex.getString("correct_answer")
                        correctAnswer = cleanResponse(correctAnswer)

                        val incorrectAnswersJA = jsonIndex.getJSONArray("incorrect_answers")
                        for (j in 0 until incorrectAnswersJA.length()) {
                            var incorrectAnswer = incorrectAnswersJA.getString(j)
                            incorrectAnswer = cleanResponse(incorrectAnswer)
                            answerList.add(incorrectAnswer)

                        }




                    }

                }catch (e: JSONException) {
                    e.printStackTrace()
                }

                if (response.isSuccessful) runOnUiThread {
                    questionNum++
                    if(questionNum > numOfQuestions){
                        gameOver()
                    } else {
                        questionCounterTextView.findViewById<TextView>(R.id.questionCounterTextView)
                        questionCounterTextView.text = "Question: $questionNum of $numOfQuestions"
                        questionTextView.text = cleanResponse(question)
                        answerList.add(correctAnswer)
                        answerList.shuffle()
                        var answerListView = findViewById<ListView>(R.id.answerListView)
                        var myAdapter = ArrayAdapter<String> (this@MainActivity, android.R.layout.simple_list_item_1, answerList)
                        answerListView.adapter = myAdapter
                        answerListView.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
                            var selectedAnswer = answerListView.getItemAtPosition(position)
                            isCorrect = selectedAnswer.equals(correctAnswer)
                            showResultDialog()

                        }
                    }

                }
            }
        })

    }

    private fun cleanResponse(myResponse: String): String {
        //var cleanedResponse = myResponse
        var delimiter = "&quot;"
        var delimiter2 = "&#039;"
        var splitResponse = myResponse.split(Regex(delimiter))
        var splitResponse2 = splitResponse.toString().split(Regex(delimiter2))
        var splitResponseStr = splitResponse2.toString()
        splitResponseStr = splitResponseStr.drop(2)
        splitResponseStr = splitResponseStr.dropLast(2)
        splitResponseStr = splitResponseStr.replace(",", "'")
        println("TEST $splitResponseStr")


        return splitResponseStr.toString()
    }

    private fun gameOver() {
        var percentage = ((score / numOfQuestions)*100)
        val goDialog = AlertDialog.Builder(this)
        goDialog.setTitle("Congratulations!")
        goDialog.setMessage("Your score is: $percentage%")
        goDialog.setCancelable(true)
        goDialog.setPositiveButton("Restart") {dialog, which ->

            resetGame()

        }
        goDialog.show()

    }

    private fun resetGame() {
        questionNum = 0
        score = 0.0
        fetchData()
        scoreTextView.text = "Score: 0"

    }

    private fun showResultDialog() {
        val dialog = AlertDialog.Builder(this)
        if(isCorrect){
            dialog.setTitle("$correctAnswer is Correct")
            dialog.setMessage("Good Job!")
            score++

            scoreTextView.text = "Correct: $score"

        } else {
            dialog.setTitle("Incorrect")
            dialog.setMessage("The correct answer is $correctAnswer")
        }
        dialog.setCancelable(false)
        dialog.setPositiveButton("Continue") {dialog, which ->
            fetchData()

        }

        dialog.show()

    }


}
