package com.example.triviaapp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.triviaapp.data.Repository;
import com.example.triviaapp.databinding.ActivityMainBinding;
import com.example.triviaapp.model.Question;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private static final String MESSAGE_ID = "message id";
    private ActivityMainBinding binding;
    private int currentQuestionIndex = 0;
    List<Question> questionList;

    private int topScore = 0;
    private int yourScore = 0;
    private boolean isAnswered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        SharedPreferences getSharedData =  getSharedPreferences(MESSAGE_ID, MODE_PRIVATE);
        int retrievedMessage = getSharedData.getInt("message",0);
        binding.tvHighestScore.setText(Integer.toString(retrievedMessage));

      /*  questionList = new Repository().getQuestions(new AnswerListAsyncResponse() {//this can be switched to lambda
            @Override
            public void processFinish(ArrayList<Question> questionArrayList) {//this can be switched to lambda
                binding.tvQuestion.setText((CharSequence) questionArrayList.get(currentQuestionIndex)
                        .getAnswer());
            }
        });*/

        questionList = new Repository().getQuestions(questionArrayList -> {
            updateQuestion();
        });

        binding.btnNext.setOnClickListener(view -> {
            if (isAnswered) {
                binding.btnNext.setTextColor(getResources().getColor(R.color.teal_700));
                currentQuestionIndex = (currentQuestionIndex + 1) % questionList.size();
                updateQuestion();
                isAnswered = false;
            }else binding.btnNext.setTextColor(Color.RED);



        });

        binding.btnTrue.setOnClickListener(view -> {
            checkAnswer(true);
            updateQuestion();
            isAnswered = true;
        });

        binding.btnFalse.setOnClickListener(view -> {
            checkAnswer(false);
            updateQuestion();
            isAnswered = true;
        });


    }



    private void checkAnswer(boolean selection) {
        int snackMessage = 0;
        boolean answer = questionList.get(currentQuestionIndex).isAnswerTrue();
        if (selection == answer) {
            snackMessage = R.string.correct_answer;
            fadeAnimation();
            if (!isAnswered) {
                yourScore++;
                Log.d("RESULT", "CORRECT, SCORE: " + yourScore);
                updateYourScore(yourScore);
            }

        } else {
            snackMessage = R.string.incorrect_answer;
            shakeAnimation();
            if (!isAnswered && yourScore > 0) {
                yourScore--;
                Log.d("RESULT", "INCORRECT, SCORE: " + yourScore);
                updateYourScore(yourScore);
            }
        }
        Snackbar.make(binding.cvQuestions, snackMessage, Snackbar.LENGTH_SHORT).show();
    }

    private void updateYourScore(int yourScore) {
        String yourStrScore = Integer.toString(yourScore);
        binding.tvYourScore.setText(yourStrScore);
    }

    private void updateCounter(ArrayList<Question> questionArrayList) {
       /* binding.tvOutof.setText(MessageFormat
                .format("Question: {0}/{1}", currentQuestionIndex, questionArrayList.size()));*/
        binding.tvOutof.setText(String.format(getString(R.string.text_formatted),
                currentQuestionIndex, questionArrayList.size()));
    }

    private void updateQuestion() {
        String question = questionList.get(currentQuestionIndex).getAnswer();
        binding.tvQuestion.setText(question);
        updateCounter((ArrayList<Question>) questionList);
    }

    private void shakeAnimation() {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake_animation);
        binding.cvQuestions.setAnimation(shake);
        shake.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.tvQuestion.setTextColor(Color.RED);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.tvQuestion.setTextColor(Color.WHITE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }


    private void fadeAnimation() {
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0f, 0.0f);
        alphaAnimation.setDuration(300);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);

        binding.cvQuestions.setAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                binding.tvQuestion.setTextColor(Color.GREEN);

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                binding.tvQuestion.setTextColor(Color.WHITE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

        SharedPreferences getSharedData =  getSharedPreferences(MESSAGE_ID, MODE_PRIVATE);
        int retrievedMessage = getSharedData.getInt("message",0);
        binding.tvHighestScore.setText(Integer.toString(retrievedMessage));

        if (yourScore > retrievedMessage){
            topScore = yourScore;
            SharedPreferences sharedPreferences = getSharedPreferences(MESSAGE_ID, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt("message", topScore);
            editor.apply(); //the method apply or commit is used to save the changes in the sharedPreferences file and if we don't use it always there will be an error in the edit object declaration
        }
    }
}
