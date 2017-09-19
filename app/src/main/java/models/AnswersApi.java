package models;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by emin on 26.10.2015.
 */
public class AnswersApi {

    @SerializedName("id")
    int id ;
    @SerializedName("text")
    String text ;
    @SerializedName("correct")
    boolean correct ;
    @SerializedName("feedback")
    String feedback ="" ;
    @Nullable
    @SerializedName("match")
    String match;

    public AnswersApi(){


    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public boolean haveFeedback(){

        if(!this.feedback.equals(""))
            return true ;
        else
            return false ;

    }

    public String getMatch() {
        return match;
    }

    public void setMatch(String match) {
        this.match = match;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
