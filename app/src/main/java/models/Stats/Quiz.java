package models.Stats;

/**
 * Created by emin on 21.11.2015.
 */
public class Quiz {

    public int avg_score ;
    public int avg_time ;
    public int student_count ;

    public Quiz () {}

    public int getAvg_score() {
        return avg_score;
    }

    public void setAvg_score(int avg_score) {
        this.avg_score = avg_score;
    }

    public int getAvg_time() {
        return avg_time;
    }

    public void setAvg_time(int avg_time) {
        this.avg_time = avg_time;
    }

    public int getStudent_count() {
        return student_count;
    }

    public void setStudent_count(int student_count) {
        this.student_count = student_count;
    }
}
