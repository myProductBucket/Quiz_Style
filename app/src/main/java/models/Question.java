package models;

import android.content.Context;

import java.util.ArrayList;

import radyo.com.quix.R;

/**
 * Created by emin on 26.10.2015.
 */
public class Question {

    int id ;
    String text ;
    ArrayList<Answer> answers = new ArrayList<>();
    ArrayList<Answer> answersOrdered = new ArrayList<>();
    ArrayList<String> multipleCorrectAnswers = new ArrayList<>();
    String feedback ;
    String attachment ;

    public Question(){}

    public String getText() {
        return text;
    }
    public String getText(Context ctx) {
        if(haveAttachment())
            return ctx.getString(R.string.image_question) + " - " + text;
        else
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<Answer> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<Answer> answers) {
        this.answers = answers;
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

    public ArrayList<Answer> getAnswersOrdered() {
        return answersOrdered;
    }

    public void setAnswersOrdered(ArrayList<Answer> answersOrdered) {
        this.answersOrdered = answersOrdered;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMultipleCorrectAnswers() {

        String ans = "";

        for(int i = 0;i<getAnswers().size();i++)
            if(getAnswers().get(i).isCorrect())
               ans += getAnswers().get(i).getText()+"-";

        return ans;
    }

    public void setMultipleCorrectAnswers(ArrayList<String> multipleCorrectAnswers) {
        this.multipleCorrectAnswers = multipleCorrectAnswers;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
    public boolean haveAttachment(){

        if(!attachment.equals(""))
            return true ;
        else
            return false ;
    }
}
