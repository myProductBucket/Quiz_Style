package teacher.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;

import java.util.List;

import adapters.ResultDetailAdapter;
import models.Results.Question;
import models.Results.Results;
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
public class ResultDetailFragment extends Fragment  {

    TextView name_tv, quiz_tv ;
    RecyclerView mRecyclerView;
    LinearLayoutManager layoutManager ;
    RelativeLayout add_quiz_btn;
    String teacherID ;
    public static JSONArray array ;
    public static List<Question> questions ;
    private RestService api ;
    RelativeLayout progressBar,noresult ;
    public int quizid, pos ;

    public String quizname,studentname ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_result_detail, container, false);

        name_tv = (TextView)rootView.findViewById(R.id.name_tv);
        quiz_tv = (TextView)rootView.findViewById(R.id.quiz_tv);

        api = MyApp.getApi();

        studentname = getArguments().getString("studentname", "");
        quizname = getArguments().getString("quizname", "");
        quizid = getArguments().getInt("quizid", 0);
        pos = getArguments().getInt("pos",0);
        noresult = (RelativeLayout)rootView.findViewById(R.id.noresult);
        progressBar = (RelativeLayout)rootView.findViewById(R.id.progressbar);
        Gson gson = new Gson();
        name_tv.setText(studentname);
        quiz_tv.setText(quizname);

        progressBar = (RelativeLayout)rootView.findViewById(R.id.progressbar);
        teacherID = Utils.getPrefValue(getActivity(),"userID","0");

        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.itemsRecyclerView);
        layoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(layoutManager);

        fetchData();

        return rootView;
    }

    public void fetchData(){

        api.getResults(quizid, new Callback<List<Results>>() {
            @Override
            public void success(List<Results> resultses, Response response) {


                ResultDetailAdapter adapter = new ResultDetailAdapter(getActivity(), resultses.get(pos).questions);
                Log.d("pos",String.valueOf(resultses.get(pos).questions.size()));
                Log.d("uzunluk",String.valueOf(pos));
                mRecyclerView.setAdapter(adapter);
                progressBar.setVisibility(View.GONE);
                noresult.setVisibility(View.GONE);
            }

            @Override
            public void failure(RetrofitError error) {
                noresult.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        });

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    public static ResultDetailFragment newInstance(String studentname, String quizname,int quizid,int pos) {
        ResultDetailFragment fragmentDemo = new ResultDetailFragment();
        Bundle args = new Bundle();
        args.putString("studentname", studentname);
        args.putString("quizname", quizname);
        args.putInt("quizid",quizid);
        args.putInt("pos",pos);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

}
