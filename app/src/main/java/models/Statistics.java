package models;

import android.content.Context;
import android.util.Log;

import utils.Utils;

/**
 * Created by emin on 1.11.2015.
 */
public class Statistics {

    Context ctx ;
    boolean haveStatistics ;
    int quizTaken ;
    int questionsSolved ;
    int averageScore ;
    int correctCount ;
    int correctPercentage ;
    int averageTime ;
    int highestScore ;

    public Statistics(Context ctx){

        this.ctx = ctx;

    }

    public boolean isHaveStatistics() {
        if(Utils.getPrefValue(ctx,"havestatistics","false").equals("true"))
        return true;
        else
            return false;
    }

    public void setHaveStatistics(boolean haveStatistics) {

        Utils.savePrefs(ctx,"havestatistics","true");
        this.haveStatistics = haveStatistics;
    }

    public String getQuizTaken() {
        return Utils.getPrefValue(ctx,"quiz_taken","0");
    }



    public void setQuizTaken(int quizTaken) {

        int temp = Integer.valueOf(Utils.getPrefValue(ctx,"quiz_taken","0"));

        temp ++ ;

        Utils.savePrefs(ctx, "quiz_taken",String.valueOf(temp));

        this.quizTaken = temp;
    }

    public String getQuestionsSolved() {
        return Utils.getPrefValue(ctx,"question_solved","0");
    }



    public void setQuestionsSolved(int questionsSolved) {

        int temp = Integer.valueOf(Utils.getPrefValue(ctx,"question_solved","0"));

        temp = temp + questionsSolved ;

        Utils.savePrefs(ctx, "question_solved",String.valueOf(temp));

        this.questionsSolved = temp;
    }

    public String getAverageScore() {

        return Utils.getPrefValue(ctx,"average_score","0");

    }

    public void setAverageScore(int averageScore) {

        int temp = Integer.valueOf(Utils.getPrefValue(ctx,"average_score","0"));

        temp = ((temp * (Integer.valueOf(Utils.getPrefValue(ctx,"quiz_taken","0"))-1))+averageScore)/Integer.valueOf(Utils.getPrefValue(ctx,"quiz_taken","0"));

        Utils.savePrefs(ctx, "average_score",String.valueOf(temp));

        this.averageScore = temp;
    }

    public String getCorrectPercentage() {
        return Utils.getPrefValue(ctx,"correct_percentage","0") + "%";
    }

    public void setCorrectPercentage(int correctCount) {

        int temp ;

        temp =  ((Integer.valueOf(getCorrectCount())*100)/Integer.valueOf(getQuestionsSolved()));

        Log.d("percent",String.valueOf(Integer.valueOf(getCorrectCount()) + " -> " + String.valueOf(Integer.valueOf(getQuestionsSolved()))));
        Utils.savePrefs(ctx, "correct_percentage",String.valueOf(temp));

        this.correctPercentage = correctPercentage;
    }

    public String getAverageTime() {

        int temp = Integer.valueOf(Utils.getPrefValue(ctx,"average_time","0"))/Integer.valueOf(getQuizTaken());
        int hours = temp / 60 / 60;
        int minutes = temp/60 ;

        int seconds = temp%60;

        String m = String.valueOf(minutes);
        String s = String.valueOf(seconds);

        if(m.length()<=1)
            m = m.replace(m,"0"+m);

        if(s.length()<=1)
            s = s.replace(s, "0" + s);

        String elapsedTime_ = m + ":" + s;

        return elapsedTime_;

    }

    public void setAverageTime(int averageTime) {

        int temp = Integer.valueOf(Utils.getPrefValue(ctx,"average_time","0"));

        temp = temp + averageTime ;

        Utils.savePrefs(ctx, "average_time",String.valueOf(temp));

        this.averageTime = averageTime;

    }

    public String getHighestScore() {
        return Utils.getPrefValue(ctx,"high_score","0");
    }

    public void setHighestScore(int highestScore) {

        if(highestScore>Integer.valueOf(Utils.getPrefValue(ctx,"high_score","0")))
            Utils.savePrefs(ctx,"high_score",String.valueOf(highestScore));

        this.highestScore = highestScore;
    }

    public String getCorrectCount() {
        Log.d("correct_count",Utils.getPrefValue(ctx,"correct_count","0"));
        return Utils.getPrefValue(ctx,"correct_count","0") ;
    }

    public void setCorrectCount(int correctCount) {

        Utils.savePrefs(ctx, "correct_count",String.valueOf(Integer.valueOf(Utils.getPrefValue(ctx,"correct_count","0"))+correctCount));
        this.correctCount = correctCount;
    }
}
