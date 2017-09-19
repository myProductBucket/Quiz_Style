package utils;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import models.AnswersApi;
import models.QuestionApi;
import models.QuizApi;
import models.Results.Results;
import models.Stats.Stats;
import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by emin on 15.11.2015.
 */
public interface RestService {

    @FormUrlEncoded
    @POST("/register.php")
    void login(@Field("json") String json,
               Callback<String> callback);

    @FormUrlEncoded
    @POST("/addQuiz.php")
    void addQuiz(@Field("json") String json,
               Callback<String> callback);

    @FormUrlEncoded
    @POST("/getQuizes.php")
    void getQuizes(@Field("account_id") String id,
                 Callback<List<QuizApi>> callback);

    @FormUrlEncoded
    @POST("/deleteQuiz.php")
    void deleteQuiz(@Field("quiz_id") int id,
                   Callback<String> callback);

    @FormUrlEncoded
    @POST("/updateQuiz.php")
    void updateQuiz(@Field("json") String json,
                    Callback<String> callback);

    @FormUrlEncoded
    @POST("/addQuestion.php")
    void addQuestionWithPhoto(@Field("json") String base64, @Field("photo") String photo,
                    Callback<String> callback);

    @FormUrlEncoded
    @POST("/addQuestion.php")
    void addQuestion(@Field("json") String json,
                    Callback<String> callback);

    @GET("/getQuestions.php")
    void getQuestions(@Query("quiz_id") String id,
                   Callback<ArrayList<QuestionApi>> callback);

    @GET("/getQuestions.php")
    void getQuestionsAsArray(@Query("quiz_id") int id,
                      Callback<JSONArray> callback);

    @FormUrlEncoded
    @POST("/deleteQuestion.php")
    void deleteQuestion(@Field("question_id") int id,
                    Callback<String> callback);

    @FormUrlEncoded
    @POST("/getAnswers.php")
    void getAnswers(@Field("question_id") String id,
                   Callback<List<AnswersApi>> callback);

    @FormUrlEncoded
    @POST("/updateQuestion.php")
    void updateQuestion(@Field("json") String json,
                   Callback<String> callback);

    @FormUrlEncoded
    @POST("/updateAnswer.php")
    void updateAnswer(@Field("json") String json,
                        Callback<String> callback);

    @FormUrlEncoded
    @POST("/saveResults.php")
    void saveResults(@Field("json") String json,
                      Callback<String> callback);

    @FormUrlEncoded
    @POST("/questionStats.php")
    void getStats(@Field("question_id") int id,
                     Callback<Stats> callback);

    @FormUrlEncoded
    @POST("/getResults.php")
    void getResults(@Field("quiz_id") int id,
                  Callback<List<Results>> callback);

}
