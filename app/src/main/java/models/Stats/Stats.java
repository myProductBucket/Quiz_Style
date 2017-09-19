package models.Stats;

import java.util.List;

/**
 * Created by emin on 21.11.2015.
 */
public class Stats {

    Quiz quiz ;
    List<Question> question ;

    public Stats() {}

    public Stats(Quiz quiz, List<Question> question) {
        this.quiz = quiz;
        this.question = question;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public List<Question> getQuestion() {
        return question;
    }

    public void setQuestion(List<Question> question) {
        this.question = question;
    }
}
