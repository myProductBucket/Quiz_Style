package student;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.TransactionDetails;

import java.util.ArrayList;

import models.Quiz;
import radyo.com.quix.R;
import student.fragments.StatisticsFragment;
import student.fragments.StudentDashboardFragment;
import utils.Utils;

/**
 * Created by emin on 25.10.2015.
 */


public class StudentDashboardActivity extends AppCompatActivity implements View.OnClickListener,  BillingProcessor.IBillingHandler {

    LinearLayout dashboard_btn, statistics_btn, upgrade_btn, share_btn, exit_btn ;
    ArrayList<LinearLayout> buttons = new ArrayList<>();
    FragmentManager fragmentManager ;
    public static Context ctx ;
    View sidebar ;
    public static Quiz quiz ;
    BillingProcessor bp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studentdashboard);

        ctx = this;

        setUI();

        fragmentManager = getSupportFragmentManager();
        Fragment fragment = new StudentDashboardFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        bp = new BillingProcessor(this, getString(R.string.inapp_licence_key), this);
    }

    public void setUI(){

        dashboard_btn = (LinearLayout)findViewById(R.id.dashboard_btn);
        statistics_btn = (LinearLayout)findViewById(R.id.statistics_btn);
        upgrade_btn = (LinearLayout)findViewById(R.id.upgrade_btn);
        share_btn = (LinearLayout)findViewById(R.id.share_btn);
        exit_btn = (LinearLayout)findViewById(R.id.exit_btn);

        buttons.add(dashboard_btn);
        buttons.add(statistics_btn);
        buttons.add(upgrade_btn);
        buttons.add(share_btn);
        buttons.add(exit_btn);

        dashboard_btn.setOnClickListener(this);
        statistics_btn.setOnClickListener(this);
        upgrade_btn.setOnClickListener(this);
        share_btn.setOnClickListener(this);
        exit_btn.setOnClickListener(this);

        dashboard_btn.setSelected(true);

        if(Utils.getPrefValue(this,"isfull","false").equals("true"))
            upgrade_btn.setVisibility(View.GONE);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.dashboard_btn :
                select(v.getId());
                fragmentManager.beginTransaction().replace(R.id.content_frame, new StudentDashboardFragment()).commit();
                break;

            case R.id.statistics_btn :
                select(v.getId());
                fragmentManager.beginTransaction().replace(R.id.content_frame, new StatisticsFragment()).commit();
                break;

            case R.id.upgrade_btn :
               // select(v.getId());
                showBuyDialog();
                break;

            case R.id.share_btn :
                //select(v.getId());
                //create the send intent
                Intent shareIntent =
                        new Intent(android.content.Intent.ACTION_SEND);

                shareIntent.setType("text/plain");

                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,
                        getString(R.string.share_title));

                String shareMessage = getString(R.string.share_message);

                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT,
                        shareMessage);

                startActivity(Intent.createChooser(shareIntent,
                        "Choose an app to share"));


                break;

            case R.id.exit_btn :
               // select(v.getId());
                showExitDialog();
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

                bp.purchase(StudentDashboardActivity.this, getString(R.string.product_id));
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

    @Override
    public void onProductPurchased(String s, TransactionDetails transactionDetails) {

        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.purchase_title))
                .setMessage(getString(R.string.purchase_message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        Utils.savePrefs(StudentDashboardActivity.this,"isfull","true");
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onPurchaseHistoryRestored() {

    }

    @Override
    public void onBillingError(int i, Throwable throwable) {

    }

    @Override
    public void onBillingInitialized() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!bp.handleActivityResult(requestCode, resultCode, data))
            super.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    public void onDestroy() {
        if (bp != null)
            bp.release();

        super.onDestroy();
    }

}