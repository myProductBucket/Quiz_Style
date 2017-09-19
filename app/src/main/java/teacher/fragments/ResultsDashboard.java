package teacher.fragments;

import android.app.Activity;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import adapters.ResultQuizAdapter;
import helpers.BASE64Encoder;
import models.QuizApi;
import radyo.com.quix.MyApp;
import radyo.com.quix.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import utils.RestService;
import utils.Utils;

/**
 * Created by emin on 20.6.2015.
 */
public class ResultsDashboard extends Fragment  {

    TextView name_tv, quiz_count ;
    RecyclerView mRecyclerView;
    LinearLayoutManager layoutManager ;
    RelativeLayout add_quiz_btn;
    String teacherID ;
    public static JSONArray array ;
    public static List<QuizApi> quizes ;
    private RestService api ;
    RelativeLayout progressBar,noresult ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_teacherdashboard, container, false);

        name_tv = (TextView)rootView.findViewById(R.id.name_tv);
        quiz_count = (TextView)rootView.findViewById(R.id.quiz_count_tv);
        add_quiz_btn = (RelativeLayout)rootView.findViewById(R.id.add_quiz_btn);

        add_quiz_btn.setVisibility(View.GONE);

        noresult = (RelativeLayout)rootView.findViewById(R.id.noresult);
        api = MyApp.getApi();

        name_tv.setText(getString(R.string.exams));

        quiz_count.setText(Utils.getPrefValue(getActivity(),"quiz_created","0") + " Quiz");
        progressBar = (RelativeLayout)rootView.findViewById(R.id.progressbar);
        teacherID = Utils.getPrefValue(getActivity(),"userID","0");

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.itemsRecyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        fetchData();

        return rootView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    public void fetchData(){

        api.getQuizes(Utils.getPrefValue(getActivity(), "userID", ""), new Callback<List<QuizApi>>() {
            @Override
            public void success(List<QuizApi> s, retrofit.client.Response response) {

                Log.d("success", String.valueOf(s.size()));

                quizes = s ;

                ResultQuizAdapter adapter = new ResultQuizAdapter(getActivity(), s);
                mRecyclerView.setAdapter(adapter);

                quiz_count.setText(String.valueOf(s.size()) + " Quiz");
                progressBar.setVisibility(View.GONE);
                noresult.setVisibility(View.GONE);

            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("hata",error.getLocalizedMessage());
                noresult.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });


    }

    public void showAddQuizDialog(){

        final int[] selected_type = {-1};

        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_quiz);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView dismiss = (ImageView)dialog.findViewById(R.id.cancel);
        RelativeLayout save = (RelativeLayout)dialog.findViewById(R.id.add_quiz_btn);
        final EditText name_et = (EditText)dialog.findViewById(R.id.name_et);
        final EditText time_et = (EditText)dialog.findViewById(R.id.time_et);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        final ArrayList<LinearLayout> types = new ArrayList<>();

        LinearLayout multiple_choice= (LinearLayout)dialog.findViewById(R.id.multiple_choice);
        LinearLayout multiple_correct= (LinearLayout)dialog.findViewById(R.id.multiple_correct);
        LinearLayout drag= (LinearLayout)dialog.findViewById(R.id.drag_order);
        LinearLayout match= (LinearLayout)dialog.findViewById(R.id.matching);
        LinearLayout shortans= (LinearLayout)dialog.findViewById(R.id.short_answer);
        LinearLayout fill= (LinearLayout)dialog.findViewById(R.id.fill_blank);

        types.add(multiple_choice);
        types.add(multiple_correct);
        types.add(fill);
        types.add(drag);
        types.add(match);
        types.add(shortans);

        View.OnClickListener typesclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i = 0; i<types.size();i++){
                    types.get(i).setSelected(false);
                    if(types.get(i)==v)
                        selected_type[0] = i;
                }

                v.setSelected(true);

            }
        };

        for(int i = 0; i<types.size();i++)
            types.get(i).setOnClickListener(typesclick);

        final JSONObject objTemp = new JSONObject();

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(name_et.getText().toString().trim().equals("")){

                    Toast.makeText(getActivity(),getString(R.string.name_empty_error),Toast.LENGTH_LONG).show();

                }else if(selected_type[0] == -1){

                    Toast.makeText(getActivity(),getString(R.string.type_empty_error),Toast.LENGTH_LONG).show();

                } else {

                    try {
                        if(time_et.getText().toString().trim().equals(""))
                            objTemp.putOpt("time", 0);
                        else
                        objTemp.putOpt("time", Integer.valueOf(time_et.getText().toString()));

                        objTemp.putOpt("subject", name_et.getText().toString());
                        objTemp.putOpt("quiz_type", selected_type[0]);
                        objTemp.putOpt("teacher_id", Utils.getPrefValue(getActivity(),"userID",""));

                        Log.d("json",objTemp.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        addQuiz(new JSONObject(objTemp.toString().trim()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    dialog.cancel();

                }


            }
        });


        DisplayMetrics metrics = getActivity().getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.90);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

    public void addQuiz(JSONObject obj) {

        api.addQuiz(BASE64Encoder.encode(obj), new Callback<String>() {
            @Override
            public void success(String teacher, retrofit.client.Response response) {
                Toast.makeText(getActivity(), "Quiz Added Succesfully", Toast.LENGTH_LONG).show();
                fetchData();
            }

            @Override
            public void failure(RetrofitError error) {

                Log.d("hata", error.getLocalizedMessage().toString());
                Toast.makeText(getActivity(), error.getLocalizedMessage(), Toast.LENGTH_LONG).show();
            }
        });

    }




}
