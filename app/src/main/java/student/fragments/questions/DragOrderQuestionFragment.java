package student.fragments.questions;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import helpers.BASE64Encoder;
import models.Question;
import models.Quiz;
import models.Statistics;
import radyo.com.quix.MyApp;
import radyo.com.quix.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import student.StudentDashboardActivity;
import student.fragments.StudentDashboardFragment;
import utils.Utils;


/**
 * Created by emin on 20.6.2015.
 */
public class DragOrderQuestionFragment extends Fragment implements View.OnClickListener {

    ArrayList<LinearLayout> choices = new ArrayList<>();
    ArrayList<TextView> answers = new ArrayList<>();
    LinearLayout a_ll, b_ll, c_ll, d_ll ;
    TextView subject_tv, time_tv, question_tv, question_count_tv,
             a_text, b_text, c_text, d_text ;

    private CountDownTimer countDownTimer;
    private  long startTime ;
    private final long interval = 1 * 1000;
    public Button check_btn;

    public Quiz quiz ;
    int index = 0;
    int wrongCount = 0;

    String firstanswer = "";
    int elapsedTime = 0;
    int resultTime = 0;
    JSONObject result = new JSONObject();
    JSONArray questions = new JSONArray();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.drag_order_question_fragment, container, false);

        quiz = StudentDashboardActivity.quiz;

        setUI(rootView);
        setQuestion(index);

        startTime = quiz.getTime() * 1000 * 60;
        countDownTimer = new MyCountDownTimer(startTime, interval);
        countDownTimer.start();

        return rootView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    public void setUI(View root){

        check_btn = (Button)root.findViewById(R.id.check_btn);
        check_btn.setOnClickListener(this);

        a_ll = (LinearLayout)root.findViewById(R.id.answer_a_ll);
        b_ll = (LinearLayout)root.findViewById(R.id.answer_b_ll);
        c_ll = (LinearLayout)root.findViewById(R.id.answer_c_ll);
        d_ll = (LinearLayout)root.findViewById(R.id.answer_d_ll);

        choices.add(a_ll);
        choices.add(b_ll);
        choices.add(c_ll);
        choices.add(d_ll);

        subject_tv = (TextView)root.findViewById(R.id.subject_tv);
        time_tv = (TextView)root.findViewById(R.id.time_tv);
        question_tv = (TextView)root.findViewById(R.id.question_tv);
        question_count_tv = (TextView)root.findViewById(R.id.question_count_tv);

        a_text = (TextView)root.findViewById(R.id.answer_a_tv);
        b_text = (TextView)root.findViewById(R.id.answer_b_tv);
        c_text = (TextView)root.findViewById(R.id.answer_c_tv);
        d_text = (TextView)root.findViewById(R.id.answer_d_tv);

        answers.add(a_text);
        answers.add(b_text);
        answers.add(c_text);
        answers.add(d_text);

        for(int i=0;i<choices.size();i++){

            choices.get(i).setOnClickListener(this);
            answers.get(i).setOnTouchListener(new MyTouchListener());
            choices.get(i).setOnDragListener(new MyDragListener());

        }


    }

    public void setQuestion(int index){

        wrongCount = 0;
        firstanswer = "";


        Question question = quiz.getQuestions().get(index);
        for(int i = 0; i<choices.size();i++){

            choices.get(i).setVisibility(View.GONE);

        }

        for(int i = 0; i<quiz.getQuestions().get(index).getAnswers().size();i++){

            choices.get(i).setVisibility(View.VISIBLE);

        }

        subject_tv.setText(quiz.getSubject());

        question_tv.setText(question.getText());
        question_count_tv.setText(String.valueOf(index + 1) + "/" + String.valueOf(quiz.getLength()));

        for(int i=0; i<question.getAnswers().size();i++){

            answers.get(i).setText(question.getAnswers().get(i).getText());

        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.check_btn :

                isCorrect();

                break;

        }

    }

    public void isCorrect(){

        boolean isCorrect = true ;

        emin : for(int i=0;i<quiz.getQuestions().get(index).getAnswersOrdered().size();i++){

            TextView tv = (TextView)choices.get(i).getChildAt(1);

            Log.d("sonuc" + String.valueOf(i),tv.getText().toString() + " --> " + quiz.getQuestions().get(index).getAnswersOrdered().get(i).getText().toString());
            if(!tv.getText().toString().equals(quiz.getQuestions().get(index).getAnswersOrdered().get(i).getText().toString())){
                isCorrect = false ;
                break emin;
            }
        }

        if(isCorrect)
            showFeedback(true);
        else{
            showFeedback(false);
        wrongCount++;}



    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            time_tv.setText("Time's up!");
        }

        @Override
        public void onTick(long millisUntilFinished) {

            elapsedTime++;
           int sec = (int) (millisUntilFinished/1000);

            int hours = sec / 60 / 60;
            int minutes = (sec - (hoursToSeconds(hours)))
                    / 60;
            int seconds = sec
                    - ((hoursToSeconds(hours)) + (minutesToSeconds(minutes)));


            String m = String.valueOf(minutes);
            String s = String.valueOf(seconds);

            if(m.length()<=1)
                m = m.replace(m,"0"+m);

            if(s.length()<=1)
                s = s.replace(s,"0"+s);

            time_tv.setText(m+":" +s + " min") ;
        }
    }
    private static int hoursToSeconds(int hours) {
        return hours * 60 * 60;
    }

    private static int minutesToSeconds(int minutes) {
        return minutes * 60;
    }

    public void showFeedback(final boolean isTrue){


        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        if(isTrue)
            dialog.setContentView(R.layout.feedback_dialog_positive);
        else
            dialog.setContentView(R.layout.feedback_dialog_negative);


            TextView feedback = (TextView) dialog.findViewById(R.id.feedback_text);
        ImageView  cancel = (ImageView)dialog.findViewById(R.id.cancel);

        if(isTrue)
            feedback.setText(getString(R.string.correct_answer));
        else{

            if(wrongCount>1)
                feedback.setText(quiz.getQuestions().get(index).getFeedback());
            else
                feedback.setText(getString(R.string.wrong_answer));
        }

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();


                if(isTrue){

                    JSONObject tempObj = new JSONObject();

                    try {

                        tempObj.put("id", quiz.getQuestions().get(index).getId());
                        tempObj.put("question", quiz.getQuestions().get(index).getText());
                        tempObj.put("correct_answer","");
                        tempObj.put("users_first_answer",firstanswer);
                        if(wrongCount==0){

                            tempObj.put("isCorrect",true);
                            tempObj.put("first_try_correct",true);
                            tempObj.put("second_try_correct",false);
                            tempObj.put("third_try_correct",false);

                        }

                        else if(wrongCount==1){

                            tempObj.put("isCorrect",false);
                            tempObj.put("first_try_correct",false);
                            tempObj.put("second_try_correct",true);
                            tempObj.put("third_try_correct",false);

                        }else if(wrongCount>1){

                            tempObj.put("isCorrect",false);
                            tempObj.put("first_try_correct",false);
                            tempObj.put("second_try_correct",false);
                            tempObj.put("third_try_correct",true);

                        }

                        questions.put(tempObj);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    index ++ ;
                    if(index>=quiz.getQuestions().size())
                        showReport();
                    else
                        setQuestion(index);
                }

            }
        });

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            int screenWidth = (int) (metrics.widthPixels * 0.80);
            dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();


    }

    private final class MyTouchListener implements View.OnTouchListener {
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                ClipData data = ClipData.newPlainText("", "");
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(view);
                view.startDrag(data, shadowBuilder, view, 0);
                //view.setVisibility(View.INVISIBLE);
                return true;
            } else {
                return false;
            }
        }
    }

    class MyDragListener implements View.OnDragListener {
        private boolean containsDragable = true;
        @Override
        public boolean onDrag(View v, DragEvent event) {
            int action = event.getAction();
            switch (event.getAction()) {
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    containsDragable = true;
                    v.setSelected(true);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    containsDragable = false;
                    v.setSelected(false);
                    break;
                case DragEvent.ACTION_DROP:
                    // Dropped, reassign View to ViewGroup
                    if(containsDragable){
                        Log.d("contain","true");
                        View view = (View) event.getLocalState();
                        ViewGroup owner = (ViewGroup) view.getParent();
                        LinearLayout container = (LinearLayout) v;
                        owner.removeView(view);

                        TextView text = (TextView)((LinearLayout) v).getChildAt(1);
                        if(container!=owner){
                            container.removeView(text);
                            owner.addView(text);}
                        container.addView(view);
                        view.setVisibility(View.VISIBLE);

                    }

                    break;

                case DragEvent.ACTION_DRAG_ENDED:
                    v.setSelected(false);

                    if (dropEventNotHandled(event)){
                        v.setVisibility(View.VISIBLE);}
                default:
                    break;
            }
            return true;
        }
    }

    private boolean dropEventNotHandled(DragEvent dragEvent) {
        return !dragEvent.getResult();
    }

    public void showReport(){

        int correctCount = 0;

        for(int i=0;i<questions.length();i++){

            if(questions.optJSONObject(i).optBoolean("isCorrect"))
                correctCount++;
        }

        final int score = (int) Math.ceil(100.0/ quiz.getQuestions().size()*correctCount);

        int hours = elapsedTime / 60 / 60;
        int minutes = elapsedTime/60 ;
        resultTime = elapsedTime;
        int seconds = elapsedTime%60;

        String m = String.valueOf(minutes);
        String s = String.valueOf(seconds);

        if(m.length()<=1)
            m = m.replace(m,"0"+m);

        if(s.length()<=1)
            s = s.replace(s, "0" + s);



        Log.d("time", String.valueOf(elapsedTime));
        String elapsedTime_ = m + ":" + s;

        try {
            result.put("results", new JSONObject().put("quiz_id", quiz.getId())
                    .put("student_name", Utils.getPrefValue(getActivity(), "student_name", ""))
                    .put("time", resultTime)
                    .put("score", score)
                    .put("total_question", quiz.getQuestions().size())
                    .put("correct_count",correctCount));

            result.put("questions",questions);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.report_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        TextView name = (TextView)dialog.findViewById(R.id.name_tv);
        TextView point = (TextView)dialog.findViewById(R.id.puan_count_tv);
        TextView time = (TextView)dialog.findViewById(R.id.time_count_tv);
        TextView correct = (TextView)dialog.findViewById(R.id.correct_count_tv);
        TextView title = (TextView)dialog.findViewById(R.id.title_tv);
        Button complete = (Button)dialog.findViewById(R.id.finish_btn);

        final int finalCorrectCount = correctCount;
        complete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();

                Statistics stat = new Statistics(getActivity());
                stat.setQuizTaken(1);
                stat.setQuestionsSolved(quiz.getQuestions().size());
                stat.setAverageScore(score);
                stat.setAverageTime(elapsedTime);
                stat.setCorrectCount(finalCorrectCount);
                stat.setCorrectPercentage(finalCorrectCount);
                stat.setHighestScore(score);
                stat.setHaveStatistics(true);


                Log.d("json", result.toString());

                MyApp.getApi().saveResults(BASE64Encoder.encode(result), new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        int fullCounter = Integer.valueOf(Utils.getPrefValue(getActivity(), "fullcounter", "0"));
                        fullCounter ++;
                        Utils.savePrefs(getActivity(),"fullcounter", String.valueOf(fullCounter) );
                        Toast.makeText(getActivity(), getString(R.string.message_results_saved), Toast.LENGTH_LONG).show();
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, new StudentDashboardFragment());
                        ft.commit();

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        final FragmentTransaction ft = getFragmentManager().beginTransaction();
                        ft.replace(R.id.content_frame, new StudentDashboardFragment());
                        ft.commit();
                    }
                });


            }
        });

        title.setText(quiz.getSubject() + " Quiz");
        name.setText(getString(R.string.dear_text) + " " + Utils.getPrefValue(getActivity(), "student_name", ""));
        point.setText(String.valueOf(score));
        time.setText(String.valueOf(elapsedTime_));
        correct.setText(String.valueOf(correctCount));


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }
}
