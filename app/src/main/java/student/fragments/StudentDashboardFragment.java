package student.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

import models.Answer;
import models.Question;
import models.Quiz;
import radyo.com.quix.R;
import student.StudentDashboardActivity;
import student.fragments.questions.DragOrderQuestionFragment;
import student.fragments.questions.FillBlankQuestionFragment;
import student.fragments.questions.MatchQuestionFragment;
import student.fragments.questions.MultipleChoiceQuestionFragment;
import student.fragments.questions.MultipleCorrectQuestionFragment;
import student.fragments.questions.ShortAnswerQuestionFragment;
import utils.Utils;

/**
 * Created by emin on 20.6.2015.
 */
public class StudentDashboardFragment extends Fragment  {

    EditText gototest_et ;
    ImageView gototest_btn ;
    public static Quiz quiz ;
    ProgressBar pBar ;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_studentdashboard, container, false);

        gototest_btn = (ImageView)rootView.findViewById(R.id.go_to_test_btn);
        gototest_et = (EditText)rootView.findViewById(R.id.test_id_edittext);
        pBar = (ProgressBar)rootView.findViewById(R.id.progressbar);

        gototest_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (gototest_et.getText().toString().length() <= 0)
                    Toast.makeText(getActivity(), getString(R.string.no_test_found), Toast.LENGTH_LONG).show();
                else {
                    if (Utils.getPrefValue(getActivity(), "student_name", "").equals("")) {
                        showNameInputDialog(gototest_et.getText().toString().trim());
                    } else {
                        createTest(gototest_et.getText().toString().trim());
                    }
                }
            }
        });

        return rootView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    public void createTest(final String id){

            String  isFull = Utils.getPrefValue(getActivity(),"isfull","false") ;

            int fullCounter = Integer.valueOf(Utils.getPrefValue(getActivity(), "fullcounter", "0"));

        if(fullCounter>200.000 && isFull.equals("false")) {

            ((StudentDashboardActivity)getContext()).showBuyDialog();

        } else {



        pBar.setVisibility(View.VISIBLE);

        quiz = new Quiz();
        String url = "";
        url = getString(R.string.api_root)+ "/examQuestions.php?quiz_id="+id ;

        Log.d("current", url);
        RequestQueue queue =  Volley.newRequestQueue(getActivity()); // Current context
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, (String)null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONObject info = response.optJSONObject("quiz_informations");
                        int type = info.optInt("quiz_type");
                        quiz.setId(info.optInt("quiz_id"));
                        quiz.setType(String.valueOf(type));
                        quiz.setSubject(info.optString("subject"));
                        quiz.setTime(info.optInt("time"));

                        JSONArray questions = response.optJSONArray("questions");

                        ArrayList<Question> questions_ = new ArrayList<>();

                        for(int i = 0;i<questions.length();i++){

                            Question question = new Question();

                            JSONObject tempQuestion = questions.optJSONObject(i);

                            question.setId(tempQuestion.optInt("id"));
                            question.setText(tempQuestion.optString("text"));
                            question.setFeedback(tempQuestion.optString("feedback"));
                            question.setAttachment(tempQuestion.optString("attachment"));

                            JSONArray answers = tempQuestion.optJSONArray("answers");

                            final ArrayList<Answer> answers_ = new ArrayList<>();
                            ArrayList<Answer> tempAnswers = new ArrayList<>();
                            Log.d("uzunluk",String.valueOf(answers.length()));

                            for (int j = 0; j<answers.length();j++){

                                Answer answer = new Answer();

                                JSONObject tempAnswer = answers.optJSONObject(j);

                                answer.setText(tempAnswer.optString("text"));
                                answer.setCorrect(tempAnswer.optBoolean("correct"));
                                answer.setFeedback(tempAnswer.optString("feedback"));
                                answer.setMatch(tempAnswer.optString("match_"));


                                answers_.add(answer);
                                tempAnswers.add(answer);

                            }


                            question.setAnswersOrdered(tempAnswers);
                            Collections.shuffle(answers_);
                            question.setAnswers(answers_);

                            questions_.add(question);
                        }


                        Collections.shuffle(questions_);

                        quiz.setQuestions(questions_);

                        StudentDashboardActivity.quiz = quiz;


                        final FragmentTransaction ft = getFragmentManager().beginTransaction();

                        if(type%6==0)
                            ft.replace(R.id.content_frame, new MultipleChoiceQuestionFragment());
                        else if(type%6==1)
                            ft.replace(R.id.content_frame, new MultipleCorrectQuestionFragment());
                        else if(type%6==2)
                            ft.replace(R.id.content_frame, new FillBlankQuestionFragment());
                        else if(type%6==3)
                            ft.replace(R.id.content_frame, new DragOrderQuestionFragment());
                        else if(type%6==4)
                            ft.replace(R.id.content_frame, new MatchQuestionFragment());
                        else
                            ft.replace(R.id.content_frame, new ShortAnswerQuestionFragment());

                        ft.commit();
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                        Toast.makeText(getActivity(),getString(R.string.msg_quiz_not_found),Toast.LENGTH_LONG).show();
                        pBar.setVisibility(View.GONE);
                    }
                }
        );

        getRequest.setRetryPolicy(new DefaultRetryPolicy(
                5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        queue.add(getRequest);


        }

    }

    public void showNameInputDialog(final String quizID){

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.name_input);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        final EditText et = (EditText)dialog.findViewById(R.id.answer_et);
        ImageView btn = (ImageView)dialog.findViewById(R.id.check_btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (et.getText().toString().length() <= 0) {

                    Toast.makeText(getActivity(), getString(R.string.valid_name_error), Toast.LENGTH_LONG).show();
                } else {

                    Utils.savePrefs(getActivity(), "student_name", et.getText().toString());
                    dialog.cancel();
                    createTest(quizID);
                }

            }
        });

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

}
