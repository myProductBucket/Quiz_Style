package adapters;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
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

import helpers.BASE64Encoder;
import models.QuizApi;
import radyo.com.quix.MyApp;
import radyo.com.quix.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import teacher.TeacherDashboardActivity;
import utils.RestService;


/**
 * Created by emin on 2.7.2015.
 */

public class QuizAdapter extends RecyclerView.Adapter<QuizAdapter.CardViewHolder>{


    JSONArray items ;
    List<QuizApi>  items_ ;
    public Context ctx ;
    private RestService api ;

    public QuizAdapter(Context ct, JSONArray array){

        this.items = array;
        this.ctx = ct;
    }

    public QuizAdapter(Context ct, List<QuizApi> array){

        this.items_ = array;
        this.ctx = ct;
        api = MyApp.getApi();
    }


    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.quiz_item, parent, false);
        CardViewHolder cvh = new CardViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {

        QuizApi quiz = items_.get(position);
        holder.isim.setText(quiz.getSubject());
        holder.numara.setText(String.valueOf(position+1));
        holder.sure.setText(String.valueOf(quiz.getTime()) + ":00 min");
        holder.soru_sayi.setText(String.valueOf(quiz.getQuestionCount()) + " Question");



    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return items_.size();
    }

    public  class CardViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener {

        TextView numara,isim,sure,soru_sayi;
        ImageView delete, edit;

        CardViewHolder(View itemView) {

            super(itemView);
            numara = (TextView)itemView.findViewById(R.id.sira_tv);
            isim = (TextView)itemView.findViewById(R.id.isim_tv);
            sure = (TextView)itemView.findViewById(R.id.sure_tv);
            soru_sayi = (TextView)itemView.findViewById(R.id.soru_sayisi_tv);
            delete = (ImageView)itemView.findViewById(R.id.delete_iv);
            edit = (ImageView)itemView.findViewById(R.id.edit_iv);

            edit.setOnClickListener(this);
            delete.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {


            if(v==itemView){

                ((TeacherDashboardActivity) v.getContext()).openQuiz(getPosition());

            }else if(v==edit){

                showQuizEditDialog(getPosition());

            }else if(v==delete){

                showDeleteDialog(getPosition());

            }


        }

    }

    public void showQuizEditDialog(int pos){

        final Dialog dialog = new Dialog(ctx);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_quiz);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView dismiss = (ImageView)dialog.findViewById(R.id.cancel);
        final EditText name_et = (EditText)dialog.findViewById(R.id.name_et);
        final EditText time_et = (EditText)dialog.findViewById(R.id.time_et);
        RelativeLayout save = (RelativeLayout)dialog.findViewById(R.id.add_quiz_btn);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        final QuizApi quiz = items_.get(pos);

        name_et.setText(quiz.getSubject());
        time_et.setText(String.valueOf(quiz.getTime()));

        final ArrayList<LinearLayout> types = new ArrayList<>();

        LinearLayout multiple_choice= (LinearLayout)dialog.findViewById(R.id.multiple_choice);
        LinearLayout multiple_correct= (LinearLayout)dialog.findViewById(R.id.multiple_correct);
        LinearLayout drag= (LinearLayout)dialog.findViewById(R.id.drag_order);
        LinearLayout match= (LinearLayout)dialog.findViewById(R.id.matching);
        LinearLayout shortans= (LinearLayout)dialog.findViewById(R.id.short_answer);
        LinearLayout fill= (LinearLayout)dialog.findViewById(R.id.fill_blank);
        TextView title = (TextView)dialog.findViewById(R.id.feedback_title);

        title.setText(ctx.getString(R.string.edit_quiz));

        types.add(multiple_choice);
        types.add(multiple_correct);
        types.add(drag);
        types.add(match);
        types.add(shortans);
        types.add(fill);

        View.OnClickListener typesclick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                for(int i = 0; i<types.size();i++)
                    types.get(i).setSelected(false);

                v.setSelected(true);

            }
        };

        for(int i = 0; i<types.size();i++)
            types.get(i).setVisibility(View.GONE);




        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final JSONObject objTemp = new JSONObject();

                try {
                    objTemp.putOpt("quiz_id",quiz.getId());
                    objTemp.putOpt("time", time_et.getText().toString());
                    objTemp.putOpt("subject", name_et.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if(name_et.getText().toString().equals("")){

                    Toast.makeText(ctx,ctx.getString(R.string.name_empty_error),Toast.LENGTH_LONG).show();

                }else{

                    updateQuiz(objTemp);
                    dialog.dismiss();

                }


            }
        });


        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        int screenWidth = (int) (metrics.widthPixels * 0.80);
        dialog.getWindow().setLayout(screenWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.show();
    }

    public void showDeleteDialog(final int pos){

        final QuizApi obj = items_.get(pos);

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

               deleteQuiz(obj.getId());
                items_.remove(pos);
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

    public void updateQuiz(JSONObject obj){


        api.updateQuiz(BASE64Encoder.encode(obj), new Callback<String>() {
            @Override
            public void success(String s, retrofit.client.Response response) {
                Toast.makeText(ctx, "Update Succesful", Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });

    }

    public void deleteQuiz(int quizid){

        api.deleteQuiz(quizid, new Callback<String>() {
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