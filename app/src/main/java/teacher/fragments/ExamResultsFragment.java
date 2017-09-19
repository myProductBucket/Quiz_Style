package teacher.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;

import java.util.List;

import adapters.ResultAdapter;
import models.QuizApi;
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
public class ExamResultsFragment extends Fragment  {

    TextView name_tv, quiz_count ;
    RecyclerView mRecyclerView;
    LinearLayoutManager layoutManager ;
    RelativeLayout add_quiz_btn;
    String teacherID ;
    public static JSONArray array ;
    public static List<QuizApi> quizes ;
    private RestService api ;
    RelativeLayout progressBar,noresult ;
    public int quizid ;
    public String quizname ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_examresults, container, false);

        name_tv = (TextView)rootView.findViewById(R.id.name_tv);

        noresult = (RelativeLayout)rootView.findViewById(R.id.noresult);
        api = MyApp.getApi();

        quizid = getArguments().getInt("quizid", 0);
        quizname = getArguments().getString("quizname", "");
        Utils.savePrefs(getActivity(),"temp_quiz_id",String.valueOf(quizid));
        Utils.savePrefs(getActivity(),"temp_quiz_name",String.valueOf(quizname));

        name_tv.setText(quizname);

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

        api.getResults(quizid, new Callback<List<Results>>() {
            @Override
            public void success(List<Results> resultses, Response response) {
                ResultAdapter adapter = new ResultAdapter(getActivity(), resultses);
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

    public static ExamResultsFragment newInstance(int quizid, String quizname) {
        ExamResultsFragment fragmentDemo = new ExamResultsFragment();
        Bundle args = new Bundle();
        args.putInt("quizid", quizid);
        args.putString("quizname", quizname);
        fragmentDemo.setArguments(args);
        return fragmentDemo;
    }

}
