package com.example.android.geoquiz

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.android.geoquiz.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar

private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val quizViewModel: QuizViewModel by viewModels<QuizViewModel>()

    private val cheatLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val isCheater = result.data?.getBooleanExtra(EXTRA_ANSWER_SHOWN, false) ?: false
            Log.d("MainActivity", "Cheat result received: $isCheater")
            quizViewModel.isCheater = isCheater
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        Log.d(TAG, "onCreate(Bundle?) called")
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.d(TAG, "Got a QuizViewModel: $quizViewModel")

        binding.trueButton.setOnClickListener { view: View ->
            checkAnswer(true)
        }
        binding.falseButton.setOnClickListener { view: View ->
            checkAnswer(false)
        }
        binding.nextButton.setOnClickListener {
            quizViewModel.moveToNext()
            updateQuestion()
        }
        binding.previousButton.setOnClickListener {
            quizViewModel.moveToPrevious()
            updateQuestion()
        }
        binding.cheatButton.setOnClickListener {
            val answerIsTrue = quizViewModel.currentQuestionAnswer
            val intent = CheatActivity.newIntent(this, answerIsTrue)
            cheatLauncher.launch(intent)
        }

        updateQuestion()
    }

    private fun updateQuestion() {
        val questionTextResId = quizViewModel.currentQuestionText
        binding.questionTextView.setText(questionTextResId)

        if (quizViewModel.isQuestionAnswered()) {
            disableAnswerButtons()
        } else {
            enableAnswerButtons()
        }
    }

    private fun checkAnswer(userAnswer: Boolean) {
        val correctAnswer = quizViewModel.currentQuestionAnswer

        val messageResId = when {
            quizViewModel.isCheater -> R.string.judgment_snackbar
            userAnswer == correctAnswer -> R.string.correct_snackbar
            else -> R.string.incorrect_snackbar
        }

        Snackbar.make(binding.root, messageResId, Snackbar.LENGTH_SHORT).show()
        disableAnswerButtons()
        quizViewModel.markQuestionAsAnswered()
    }

    private fun disableAnswerButtons() {
        binding.trueButton.isEnabled = false
        binding.falseButton.isEnabled = false
    }

    private fun enableAnswerButtons() {
        binding.trueButton.isEnabled = true
        binding.falseButton.isEnabled = true
    }
}