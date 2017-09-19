package teacher.fragments;

import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import adapters.AnswerAdapter;
import helpers.BASE64Encoder;
import models.AnswersApi;
import models.QuestionApi;
import radyo.com.quix.MyApp;
import radyo.com.quix.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import utils.RestService;
import utils.Utils;

/**
 * Created by emin on 20.6.2015.
 */
@EFragment(R.layout.fragment_answers)
public class UpdateAnswersFragment extends Fragment  {

    @ViewById(R.id.name_tv)
    TextView question_tv;
    @ViewById(R.id.itemsRecyclerView)
    RecyclerView mRecyclerView;
    RelativeLayout progressBar ;
    LinearLayoutManager layoutManager ;
    JSONObject jsonObj ;

    int type = -1 ;
    String attachment = "";
    public String teacherID ;

    public RestService api ;
    QuestionApi question ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_answers, container, false);

        teacherID = Utils.getPrefValue(getActivity(), "userID", "0");

        api = MyApp.getApi();

        try {
            jsonObj = new JSONObject(getArguments().getString("json", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.itemsRecyclerView);
        question_tv = (TextView)rootView.findViewById(R.id.name_tv);
        teacherID = Utils.getPrefValue(getActivity(), "userID", "0");
        progressBar = (RelativeLayout)rootView.findViewById(R.id.progressbar);
        Gson gson = new Gson();

        question = gson.fromJson(jsonObj.toString(), QuestionApi.class);

        question_tv.setText(question.getText());

        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        fetchData();

        return rootView;
    }


    public void fetchData(){

        api.getAnswers(String.valueOf(question.getId()), new Callback<List<AnswersApi>>() {
            @Override
            public void success(List<AnswersApi> answers, retrofit.client.Response response) {
                Log.d("sonuc", String.valueOf(answers));
                AnswerAdapter adapter = new AnswerAdapter(getActivity(), question,type);
                mRecyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("hata", error.getLocalizedMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    public void showUpdateAnsDialog(final int pos){

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_answer);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView dismiss = (ImageView)dialog.findViewById(R.id.cancel);
        RelativeLayout save = (RelativeLayout)dialog.findViewById(R.id.add_quiz_btn);
        final EditText answer, feedback, match ;
        CheckBox check ;

        match = (EditText)dialog.findViewById(R.id.match_a_et);
        feedback = (EditText)dialog.findViewById(R.id.feedback_a_et);
        answer = (EditText)dialog.findViewById(R.id.choice_a_et);
        check = (CheckBox)dialog.findViewById(R.id.a_check);
        check.setVisibility(View.GONE);

        if(question.getAnswers().get(pos).getMatch() == null ||question.getAnswers().get(pos).getMatch().equals(""))
            match.setVisibility(View.GONE);

        match.setText(question.getAnswers().get(pos).getMatch());
        feedback.setText(question.getAnswers().get(pos).getFeedback());
        answer.setText(question.getAnswers().get(pos).getText());

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        switch(type){

            case 0:
                match.setVisibility(View.GONE);
                break;

            case 1:

               match.setVisibility(View.GONE);
                feedback.setVisibility(View.GONE);
                break;

            case 2:
                match.setVisibility(View.GONE);
                feedback.setVisibility(View.GONE);
                check.setVisibility(View.GONE);
                break;

            case 3:
                    match.setVisibility(View.GONE);
                    feedback.setVisibility(View.GONE);
                    check.setVisibility(View.GONE);
                break;

            case 4:
                    feedback.setVisibility(View.GONE);
                    check.setVisibility(View.GONE);
                break;

        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (question.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.question_empty_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (type == 4 && match.getText().toString().equals("")) {
                    Toast.makeText(getActivity(), getString(R.string.question_match_empty_error), Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject obj = new JSONObject();
                try {
                    obj.putOpt("text",answer.getText().toString());
                    obj.putOpt("answer_id",question.getAnswers().get(pos).getId());
                    obj.putOpt("match",match.getText().toString());
                    obj.putOpt("feedback",feedback.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                api.updateAnswer(BASE64Encoder.encode(obj), new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        Toast.makeText(getActivity(), "Answer Updated Succesfully", Toast.LENGTH_LONG).show();
                        fetchData();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

            }
        });


        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.90);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

    public static UpdateAnswersFragment newInstance(JSONObject object) {
        UpdateAnswersFragment fragmentDemo = new UpdateAnswersFragment();
        Bundle args = new Bundle();
        args.putString("json", object.toString());
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

    public static UpdateAnswersFragment newInstance(QuestionApi object) {
        UpdateAnswersFragment fragmentDemo = new UpdateAnswersFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        args.putString("json", gson.toJson(object));
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }
}
