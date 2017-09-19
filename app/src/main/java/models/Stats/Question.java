package models.Stats;

/**
 * Created by emin on 21.11.2015.
 */
public class Question {

    public int id ;
    public int first_try ;
    public int second_try ;
    public int third_try ;

    public Question(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFirst_try() {
        return first_try;
    }

    public void setFirst_try(int first_try) {
        this.first_try = first_try;
    }

    public int getSecond_try() {
        return second_try;
    }

    public void setSecond_try(int second_try) {
        this.second_try = second_try;
    }

    public int getThird_try() {
        return third_try;
    }

    public void setThird_try(int third_try) {
        this.third_try = third_try;
    }
}
