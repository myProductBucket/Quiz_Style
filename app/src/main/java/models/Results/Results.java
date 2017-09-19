package models.Results;

import java.util.List;

/**
 * Created by emin on 21.11.2015.
 */
public class Results {

    public Result results ;
    public List<Question> questions ;

    public Results() {}

    public Result getResults() {
        return results;
    }

    public void setResults(Result results) {
        this.results = results;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public void setQuestions(List<Question> questions) {
        this.questions = questions;
    }


}
