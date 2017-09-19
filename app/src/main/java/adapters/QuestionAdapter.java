package adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import helpers.BASE64Encoder;
import models.QuestionApi;
import models.Stats.Stats;
import radyo.com.quix.MyApp;
import radyo.com.quix.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import teacher.TeacherDashboardActivity;


/**
 * Created by emin on 2.7.2015.
 */

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.CardViewHolder>{


    List<QuestionApi> items ;
    public Context ctx ;

    public QuestionAdapter(Context ct, List<QuestionApi> items){

        this.items = items;
        this.ctx = ct;
    }


    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        CardViewHolder cvh = new CardViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {

        QuestionApi question = items.get(position);
        holder.isim.setText(question.getText());
        holder.numara.setText(String.valueOf(position + 1));

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public  class CardViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        TextView numara,isim,sure,soru_sayi;
        ImageView delete, stats,edit;

        CardViewHolder(View itemView) {

            super(itemView);
            numara = (TextView)itemView.findViewById(R.id.sira_tv);
            isim = (TextView)itemView.findViewById(R.id.isim_tv);

            delete = (ImageView)itemView.findViewById(R.id.delete_iv);
            stats = (ImageView)itemView.findViewById(R.id.statistics_iv);
            edit = (ImageView)itemView.findViewById(R.id.edit_iv);
            itemView.setOnClickListener(this);
            edit.setOnClickListener(this);
            delete.setOnClickListener(this);
            stats.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            if(v == delete){

                showDeleteDialog(getPosition());

            } else if( v == edit ) {

                showUpdateAnsDialog(getPosition());

            } else if( v == stats) {

                showStatsDialog(getPosition());

            } else if( v == itemView ){

                    ((TeacherDashboardActivity) v.getContext()).openQuestion(items.get(getPosition()));
                }
            }

        }

    public void showStatsDialog(final int pos){

        final Dialog dialog = new Dialog(ctx);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_statistics);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        MyApp.getApi().getStats(items.get(pos).getId(), new Callback<Stats>() {
            @Override
            public void success(Stats stats, Response response) {

                TextView score, time, student, first_try, second_try, third_try , result;

                score = (TextView)dialog.findViewById(R.id.average_score);
                time = (TextView)dialog.findViewById(R.id.average_time);
                student = (TextView)dialog.findViewById(R.id.quiz_taken);
                first_try = (TextView)dialog.findViewById(R.id.first_try);
                second_try = (TextView)dialog.findViewById(R.id.second_try);
                third_try = (TextView)dialog.findViewById(R.id.third_try);
                result = (TextView)dialog.findViewById(R.id.teacher_feedback);

                ImageView cancel = (ImageView)dialog.findViewById(R.id.cancel);

                score.setText(String.valueOf(stats.getQuiz().avg_score));
                time.setText(String.valueOf(stats.getQuiz().avg_time) + " " + ctx.getString(R.string.second));
                student.setText(String.valueOf(stats.getQuiz().student_count) + " " + ctx.getString(R.string.student_take));
                first_try.setText(String.valueOf(stats.getQuestion().get(pos).first_try) + " %");
                second_try.setText(String.valueOf(stats.getQuestion().get(pos).second_try) + " %");
                third_try.setText(String.valueOf(stats.getQuestion().get(pos).third_try) + " %");

                if(stats.getQuestion().get(pos).third_try < 30) {
                    result.setText(ctx.getString(R.string.teacher_feedback_easy));
                    result.setTextColor(Color.parseColor("#de342b")); }
                else if(stats.getQuestion().get(pos).third_try >= 30 && stats.getQuestion().get(pos).third_try <= 80) {
                    result.setText(ctx.getString(R.string.teacher_feedback_normal));
                }
                else {
                    result.setText(ctx.getString(R.string.teacher_feedback_hard));
                    result.setTextColor(Color.parseColor("#de342b"));
                }
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });



            }

            @Override
            public void failure(RetrofitError error) {
                Log.d("hata",error.getMessage());
            }
        });

        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.90);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();

    }

    public void showDeleteDialog(final int pos){

        final QuestionApi question = items.get(pos);
        final Dialog dialog = new Dialog(ctx);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.delete_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        RelativeLayout yes_btn = (RelativeLayout)dialog.findViewById(R.id.yes_btn);
        RelativeLayout no_btn = (RelativeLayout)dialog.findViewById(R.id.no_btn);

        yes_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                deleteQuestion(question.getId());
                items.remove(pos);
                notifyDataSetChanged();
                dialog.dismiss();

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



    public void showUpdateAnsDialog(final int pos){

        final Dialog dialog = new Dialog(ctx);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_update_question);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView dismiss = (ImageView)dialog.findViewById(R.id.cancel);
        RelativeLayout save = (RelativeLayout)dialog.findViewById(R.id.add_quiz_btn);
        final EditText answer, feedback, match ;


        answer = (EditText)dialog.findViewById(R.id.name_et);
        feedback = (EditText)dialog.findViewById(R.id.time_et);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (answer.getText().toString().equals("")) {
                    Toast.makeText(ctx, ctx.getString(R.string.question_empty_error), Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject obj = new JSONObject();
                try {
                    obj.putOpt("text",answer.getText().toString());
                    obj.putOpt("question_id",items.get(pos).getId());
                    obj.putOpt("feedback",feedback.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                MyApp.getApi().updateQuestion(BASE64Encoder.encode(obj), new Callback<String>() {
                    @Override
                    public void success(String s, retrofit.client.Response response) {
                        Toast.makeText(ctx, "Question Updated Succesfully", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }

                    @Override
                    public void failure(RetrofitError error) {

                    }
                });

            }
        });

        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.90);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

    public void deleteQuestion(int quizid){

        MyApp.getApi().deleteQuestion(quizid, new Callback<String>() {
            @Override
            public void success(String s, retrofit.client.Response response) {
                Toast.makeText(ctx, "Delete Succesful", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

}