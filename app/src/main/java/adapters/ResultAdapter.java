package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import models.Results.Results;
import radyo.com.quix.MyApp;
import radyo.com.quix.R;
import teacher.TeacherDashboardActivity;
import utils.RestService;
import utils.Utils;


/**
 * Created by emin on 2.7.2015.
 */

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.CardViewHolder>{


    List<Results>  items ;
    public Context ctx ;
    private RestService api ;



    public ResultAdapter(Context ct, List<Results> result){

        this.items = result;
        this.ctx = ct;
        api = MyApp.getApi();
    }


    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_card, parent, false);
        CardViewHolder cvh = new CardViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {

        Results result = items.get(position);
        holder.name.setText(result.results.studentName);
        holder.time.setText(String.valueOf(result.results.time) + " " + ctx.getString(R.string.second));
        holder.score.setText(String.valueOf(result.results.score));
        holder.correct.setText(String.valueOf(result.results.correctCount) + " / " + String.valueOf(result.results.totalQuestion) + " " + ctx.getString(R.string.correct));

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

        TextView name, time, correct, score ;
        Button results ;

        CardViewHolder(View itemView) {

            super(itemView);
            name = (TextView)itemView.findViewById(R.id.name_tv);
            time = (TextView)itemView.findViewById(R.id.time_tv);
            correct = (TextView)itemView.findViewById(R.id.correct_tv);
            score = (TextView)itemView.findViewById(R.id.puan_count_tv);
            results = (Button)itemView.findViewById(R.id.results_btn);

            results.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {


            if(v == itemView){


            }else if(v==results){

                ((TeacherDashboardActivity) v.getContext()).openExam(items.get(getPosition()).results.studentName, Utils.getPrefValue(ctx,"temp_quiz_name",""),items.get(getPosition()).results.quizID,getPosition());

            }


        }

    }

}