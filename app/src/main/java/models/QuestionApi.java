package models;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by emin on 26.10.2015.
 */
public class QuestionApi {
    @SerializedName("id")
    int id ;
    @SerializedName("text")
    String text ;
    @SerializedName("answers")
    ArrayList<AnswersApi> answers = new ArrayList<>();
    @SerializedName("feedback")
    String feedback ;
    @SerializedName("attachment")
    String attachment ;

    public QuestionApi(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<AnswersApi> getAnswers() {
        return answers;
    }

    public void setAnswers(ArrayList<AnswersApi> answers) {
        this.answers = answers;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }
}
