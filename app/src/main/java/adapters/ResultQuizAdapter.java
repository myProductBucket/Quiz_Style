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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

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

public class ResultQuizAdapter extends RecyclerView.Adapter<ResultQuizAdapter.CardViewHolder>{


    JSONArray items ;
    List<QuizApi>  items_ ;
    public Context ctx ;
    private RestService api ;

    public ResultQuizAdapter(Context ct, JSONArray array){

        this.items = array;
        this.ctx = ct;
    }

    public ResultQuizAdapter(Context ct, List<QuizApi> array){

        this.items_ = array;
        this.ctx = ct;
        api = MyApp.getApi();
    }


    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_quiz_item, parent, false);
        CardViewHolder cvh = new CardViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {

        QuizApi quiz = items_.get(position);
        holder.isim.setText(quiz.getSubject());
        holder.numara.setText(String.valueOf(position+1));

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
        RelativeLayout share, result ;

        CardViewHolder(View itemView) {

            super(itemView);
            numara = (TextView)itemView.findViewById(R.id.sira_tv);
            isim = (TextView)itemView.findViewById(R.id.isim_tv);
            soru_sayi = (TextView)itemView.findViewById(R.id.soru_sayisi_tv);
            share = (RelativeLayout)itemView.findViewById(R.id.share_btn);
            result = (RelativeLayout)itemView.findViewById(R.id.results_btn);

            share.setOnClickListener(this);
            result.setOnClickListener(this);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {


            if(v == itemView){


            }else if(v==result){

                ((TeacherDashboardActivity) v.getContext()).openResults(items_.get(getPosition()).getId(),items_.get(getPosition()).getSubject());

            }else if(v == share){
                showCopyClipboardDialog(getPosition());
            }


        }

    }

    public void showCopyClipboardDialog(int pos){

        final Dialog dialog = new Dialog(ctx);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.share_id_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView dismiss = (ImageView)dialog.findViewById(R.id.cancel);
        final TextView id = (TextView)dialog.findViewById(R.id.id);
        Button copy = (Button)dialog.findViewById(R.id.copy);

        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int sdk = android.os.Build.VERSION.SDK_INT;
                if(sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager clipboard = (android.text.ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(id.getText().toString());
                } else {
                    android.content.ClipboardManager clipboard = (android.content.ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
                    android.content.ClipData clip = android.content.ClipData.newPlainText("text label",id.getText().toString());
                    clipboard.setPrimaryClip(clip);
                }
                Toast.makeText(ctx,ctx.getString(R.string.copied),Toast.LENGTH_LONG).show();
                dialog.cancel();
            }

        });

        final QuizApi quiz = items_.get(pos);

        id.setText(String.valueOf(quiz.getId()));

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