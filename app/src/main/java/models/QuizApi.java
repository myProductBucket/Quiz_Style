package models;

import com.google.gson.annotations.SerializedName;

/**
 * Created by emin on 26.10.2015.
 */
public class QuizApi {

    @SerializedName("quiz_id")
    int id ;
    @SerializedName("quiz_type")
    public String type ;
    @SerializedName("time")
    int time ;
    @SerializedName("question_count")
    int questionCount ;
    @SerializedName("subject")
    String subject ;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getQuestionCount() {
        return questionCount;
    }

    public void setQuestionCount(int questionCount) {
        this.questionCount = questionCount;
    }
}
