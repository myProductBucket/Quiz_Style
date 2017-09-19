package models.Results;

import com.google.gson.annotations.SerializedName;

/**
 * Created by emin on 21.11.2015.
 */
public class Question {

    @SerializedName("id")
    public int id ;
    @SerializedName("question")
    public String question ;
    @SerializedName("correct_answer")
    public String correctAnswer ;
    @SerializedName("users_first_answer")
    public String userFirstAnswer ;
    @SerializedName("isCorrect")
    public boolean isCorrect ;
    @SerializedName("first_try_correct")
    public boolean firstTry ;
    @SerializedName("second_try_correct")
    public boolean secondTry ;
    @SerializedName("third_try_correct")
    public boolean thirdTry ;

    @Override
    public String toString() {
        return super.toString();
    }
}
