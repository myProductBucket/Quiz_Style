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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import helpers.BASE64Encoder;
import models.AnswersApi;
import models.QuestionApi;
import radyo.com.quix.MyApp;
import radyo.com.quix.R;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by emin on 2.7.2015.
 */

public class AnswerAdapter extends RecyclerView.Adapter<AnswerAdapter.CardViewHolder>{


    List<AnswersApi> items ;
    public Context ctx ;
    int type ;
    QuestionApi question ;

    public AnswerAdapter(Context ct, QuestionApi item, int type){

        this.items = item.getAnswers();
        this.question = item;
        this.ctx = ct;
        this.type = type ;
    }


    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.question_item, parent, false);
        CardViewHolder cvh = new CardViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {

        AnswersApi answer = items.get(position);
        holder.isim.setText(answer.getText());
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
            stats.setImageResource(R.drawable.ic_edit);
            stats.setOnClickListener(this);
            delete.setVisibility(View.GONE);
            edit.setVisibility(View.GONE);
        }

        @Override
        public void onClick(View v) {

            showUpdateAnsDialog(getPosition());
        }

    }

    public void showUpdateDialog(){

    }

    public void showUpdateAnsDialog(final int pos){

        final Dialog dialog = new Dialog(ctx);
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

                if (answer.getText().toString().equals("")) {
                    Toast.makeText(ctx, ctx.getString(R.string.question_empty_error), Toast.LENGTH_LONG).show();
                    return;
                }
                if (type == 4 && match.getText().toString().equals("")) {
                    Toast.makeText(ctx, ctx.getString(R.string.question_match_empty_error), Toast.LENGTH_LONG).show();
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


                MyApp.getApi().updateAnswer(BASE64Encoder.encode(obj), new Callback<String>() {
                    @Override
                    public void success(String s, Response response) {
                        Toast.makeText(ctx, "Answer Updated Succesfully", Toast.LENGTH_LONG).show();
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

}