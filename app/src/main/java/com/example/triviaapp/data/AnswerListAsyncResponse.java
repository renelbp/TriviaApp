package com.example.triviaapp.data;

import com.example.triviaapp.model.Question;

import java.util.ArrayList;

public interface AnswerListAsyncResponse {
    void processFinish(ArrayList<Question> questionArrayList);
}
