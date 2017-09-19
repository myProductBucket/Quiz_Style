package teacher;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.ArrayList;

import models.QuestionApi;
import models.Quiz;
import radyo.com.quix.R;
import teacher.fragments.AddQuestionFragment;
import teacher.fragments.ExamResultsFragment;
import teacher.fragments.ResultDetailFragment;
import teacher.fragments.ResultsDashboard;
import teacher.fragments.TeacherDashboardFragment;
import teacher.fragments.UpdateAnswersFragment;
import utils.Utils;

/**
 * Created by emin on 25.10.2015.
 */


public class TeacherDashboardActivity extends AppCompatActivity implements View.OnClickListener {

    LinearLayout dashboard_btn, statistics_btn, upgrade_btn, share_btn, exit_btn, results_btn ;
    ArrayList<LinearLayout> buttons = new ArrayList<>();
    FragmentManager fragmentManager ;
    public static Context ctx ;
    View sidebar ;
    public static Quiz quiz ;
    public boolean atQuiz = false ;
    public boolean atQuestion = false ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacherdashboard);

        ctx = this;

        setUI();

        fragmentManager = getSupportFragmentManager();
        Fragment fragment = new TeacherDashboardFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
    }

    public void setUI(){

        dashboard_btn = (LinearLayout)findViewById(R.id.dashboard_btn);
        statistics_btn = (LinearLayout)findViewById(R.id.statistics_btn);
        upgrade_btn = (LinearLayout)findViewById(R.id.upgrade_btn);
        share_btn = (LinearLayout)findViewById(R.id.share_btn);
        exit_btn = (LinearLayout)findViewById(R.id.exit_btn);
        results_btn = (LinearLayout)findViewById(R.id.results_btn);

        results_btn.setVisibility(View.VISIBLE);

        buttons.add(dashboard_btn);
        buttons.add(statistics_btn);
        buttons.add(upgrade_btn);
        buttons.add(share_btn);
        buttons.add(exit_btn);
        buttons.add(results_btn);

        dashboard_btn.setOnClickListener(this);
        statistics_btn.setOnClickListener(this);
        upgrade_btn.setOnClickListener(this);
        share_btn.setOnClickListener(this);
        exit_btn.setOnClickListener(this);
        results_btn.setOnClickListener(this);

        upgrade_btn.setVisibility(View.GONE);
        statistics_btn.setVisibility(View.GONE);
        dashboard_btn.setSelected(true);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.dashboard_btn :
                select(v.getId());
                fragmentManager.beginTransaction().replace(R.id.content_frame, new TeacherDashboardFragment()).commit();
                break;

            case R.id.statistics_btn :
                select(v.getId());
                break;

            case R.id.upgrade_btn :
               // select(v.getId());
                showBuyDialog();
                break;

            case R.id.share_btn :
                //select(v.getId());
                //create the send intent
                Intent shareIntent =
                        new Intent(Intent.ACTION_SEND);

                shareIntent.setType("text/plain");

                shareIntent.putExtra(Intent.EXTRA_SUBJECT,
                        getString(R.string.share_title));

                String shareMessage = getString(R.string.share_message);

                shareIntent.putExtra(Intent.EXTRA_TEXT,
                        shareMessage);

                startActivity(Intent.createChooser(shareIntent,
                        "Choose an app to share"));

                break;

            case R.id.exit_btn :
               // select(v.getId());
                showExitDialog();
                break;

            case R.id.results_btn :
                 select(v.getId());
                fragmentManager.beginTransaction().replace(R.id.content_frame, new ResultsDashboard()).commit();
                break;

        }

    }

    public void select(int v){

        for(int i = 0; i<buttons.size();i++){

            if(buttons.get(i).getId()==v){

                buttons.get(i).setSelected(true);
            }else {

                buttons.get(i).setSelected(false);
            }

        }

    }

    public  void showBuyDialog(){

        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.buy_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        RelativeLayout buy_btn = (RelativeLayout)dialog.findViewById(R.id.buy_btn);
        RelativeLayout no_btn = (RelativeLayout)dialog.findViewById(R.id.not_buy_btn);

        buy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                Toast.makeText(ctx,"These will be set up at last step of development",Toast.LENGTH_LONG).show();

            }
        });

        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

    public void showExitDialog(){

        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.exit_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        RelativeLayout yes_btn = (RelativeLayout)dialog.findViewById(R.id.yes_btn);
        RelativeLayout no_btn = (RelativeLayout)dialog.findViewById(R.id.no_btn);

        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Utils.savePrefs(getApplicationContext(),"isLogged","false");
             dialog.dismiss();
             finish();

            }
        });

        no_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

    public  void openQuiz(int pos){

        atQuiz = true ;
        atQuestion = false ;
        Fragment newFragment = new AddQuestionFragment().newInstance(TeacherDashboardFragment.quizes.get(pos));
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, newFragment, "addquestion");
        ft.commit();


    }

    public  void openExam(String name, String quiz,int quizid, int pos){

        Fragment newFragment = new ResultDetailFragment().newInstance(name,quiz,quizid,pos);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, newFragment, "addquestion");
        ft.commit();


    }

    public  void openQuestion(QuestionApi question){

        atQuestion = true ;
        atQuiz = false ;
        Fragment newFragment = new UpdateAnswersFragment().newInstance(question);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, newFragment, "addquestion");
        ft.commit();


    }

    public  void openResults(int quizid, String quizname){

        atQuestion = true ;
        atQuiz = false ;
        Fragment newFragment = new ExamResultsFragment().newInstance(quizid,quizname);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        ft.replace(R.id.content_frame, newFragment, "results");
        ft.commit();


    }

    @Override
    public void onBackPressed(){

        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);

        if (currentFragment instanceof TeacherDashboardFragment) {
            showExitDialog();
        } else if (currentFragment instanceof UpdateAnswersFragment) {
            Fragment newFragment = new AddQuestionFragment().newInstance(Utils.getPrefValue(ctx,"last_quiz_json","0"));
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, newFragment, "dashboard");
            ft.commit();
        } else if (currentFragment instanceof ExamResultsFragment) {
            Fragment newFragment = new ResultsDashboard();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, newFragment, "dashboard");
            ft.commit();
        } else if (currentFragment instanceof AddQuestionFragment) {
            Fragment newFragment = new TeacherDashboardFragment();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, newFragment, "dashboard");
            ft.commit();
        } else if (currentFragment instanceof ResultDetailFragment) {
            Fragment newFragment = new ExamResultsFragment().newInstance(Integer.valueOf(Utils.getPrefValue(this, "temp_quiz_id", "")),Utils.getPrefValue(this,"temp_quiz_name",""));
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, newFragment, "dashboard");
            ft.commit();
        } else if (currentFragment instanceof ResultsDashboard) {
            Fragment newFragment = new TeacherDashboardFragment();
            FragmentTransaction ft = fragmentManager.beginTransaction();
            ft.replace(R.id.content_frame, newFragment, "dashboard");
            ft.commit();
            select(dashboard_btn.getId());
        }

    }
}