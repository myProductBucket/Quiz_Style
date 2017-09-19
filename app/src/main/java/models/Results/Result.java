package models.Results;

import com.google.gson.annotations.SerializedName;

/**
 * Created by emin on 21.11.2015.
 */
public class Result {

    @SerializedName("id")
    public int id ;
    @SerializedName("quiz_id")
    public int quizID;
    @SerializedName("student_name")
    public String studentName;
    @SerializedName("time")
    public int time ;
    @SerializedName("score")
    public int score ;
    @SerializedName("total_question")
    public int totalQuestion ;
    @SerializedName("correct_count")
    public int correctCount ;

    public Result() {}
}
