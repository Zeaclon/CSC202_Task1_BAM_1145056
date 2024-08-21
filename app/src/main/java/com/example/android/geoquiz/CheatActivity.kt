package com.example.android.geoquiz

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.android.geoquiz.databinding.ActivityCheatBinding

const val EXTRA_ANSWER_SHOWN = "com.example.android.geoquiz.answer_shown"
private const val EXTRA_ANSWER_IS_TRUE = "com.example.android.geoquiz.answer_is_true"

class CheatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCheatBinding
    private val quizViewModel: QuizViewModel by viewModels()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizViewModel.answerIsTrue = intent.getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false)

        // Restore the state if cheat was already shown
        if (savedInstanceState != null) {
            quizViewModel.isCheater = savedInstanceState.getBoolean(IS_CHEATER_KEY, false)
            if (quizViewModel.isCheater) {
                showCheatResult()
                setAnswerShownResult(true)
            }
        }

        binding.showAnswerButton.setOnClickListener {
            showCheatResult()
            setAnswerShownResult(true)
            quizViewModel.isCheater = true // Set isCheater to true when the answer is shown
        }
    }


    private fun showCheatResult() {
        val answerText = when {
            quizViewModel.answerIsTrue -> R.string.true_button
            else -> R.string.false_button
        }
        binding.answerTextView.setText(answerText)
    }

    private fun setAnswerShownResult(isAnswerShown: Boolean) {
        val data = Intent().apply {
            putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown)
        }
        setResult(Activity.RESULT_OK, data)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Save whether the answer was shown (i.e., user cheated)
        outState.putBoolean(IS_CHEATER_KEY, quizViewModel.isCheater)
    }


    companion object {
        fun newIntent(packageContext: Context, answerIsTrue: Boolean): Intent {
            return Intent(packageContext, CheatActivity::class.java).apply {
                putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue)
            }
        }
    }
}