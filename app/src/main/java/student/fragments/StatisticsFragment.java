package student.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import models.Statistics;
import radyo.com.quix.R;

/**
 * Created by emin on 20.6.2015.
 */
public class StatisticsFragment extends Fragment  {

    TextView quiz_taken, question_solved, average_score, correct_percent, average_quiz_time, highest_score ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_statistics, container, false);

        Statistics stat = new Statistics(getActivity());

        quiz_taken = (TextView)rootView.findViewById(R.id.quiz_taken_tv);
        question_solved = (TextView)rootView.findViewById(R.id.question_solved_tv);
        average_score = (TextView)rootView.findViewById(R.id.average_score_tv);
        correct_percent = (TextView)rootView.findViewById(R.id.correct_percent_tv);
        average_quiz_time = (TextView)rootView.findViewById(R.id.average_time_tv);
        highest_score = (TextView)rootView.findViewById(R.id.highest_score_tv);


        if(stat.isHaveStatistics()){

            quiz_taken.setText(stat.getQuizTaken());
            question_solved.setText(stat.getQuestionsSolved());
            average_score.setText(stat.getAverageScore());
            correct_percent.setText(stat.getCorrectPercentage());
            average_quiz_time.setText(stat.getAverageTime());
            highest_score.setText(stat.getHighestScore());

        } else {

            quiz_taken.setText("0");
            question_solved.setText("0");
            average_score.setText("0");
            correct_percent.setText("0");
            average_quiz_time.setText("0");
            highest_score.setText("0");

        }



        return rootView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }


}
