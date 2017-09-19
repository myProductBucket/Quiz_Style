package adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import models.Results.Question;
import radyo.com.quix.MyApp;
import radyo.com.quix.R;
import utils.RestService;


/**
 * Created by emin on 2.7.2015.
 */

public class ResultDetailAdapter extends RecyclerView.Adapter<ResultDetailAdapter.CardViewHolder>{


    List<Question>  items ;
    public Context ctx ;
    private RestService api ;



    public ResultDetailAdapter(Context ct, List<Question> result){

        this.items = result;
        this.ctx = ct;
        api = MyApp.getApi();
    }


    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_detail_card, parent, false);
        CardViewHolder cvh = new CardViewHolder(v);
        return cvh;
    }

    @Override
    public void onBindViewHolder(final CardViewHolder holder, int position) {

        Question question = items.get(position);
        holder.question.setText(ctx.getString(R.string.question) + " " + question.question);

        holder.correctans.setText(ctx.getString(R.string.correct_ans) +" " + String.valueOf(question.correctAnswer) );

        holder.firstans.setText(ctx.getString(R.string.first_ans) +" " +question.userFirstAnswer);

        int i  = 0;
        if(question.firstTry)
            i = 1;
        else if(question.secondTry)
            i=2;
        else if(question.thirdTry)
            i=3;
        else
            i=-1 ;
        if(i==-1)
            holder.whichtry.setVisibility(View.GONE);
        else
            holder.whichtry.setText(ctx.getString(R.string.which_try) +" " + String.valueOf(i));

        if(question.isCorrect)
            holder.check.setImageResource(R.drawable.ic_correct);
        else
            holder.check.setImageResource(R.drawable.ic_wrong);

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

        TextView question, correctans, firstans, whichtry ;
        ImageView check ;

        CardViewHolder(View itemView) {

            super(itemView);
            question = (TextView)itemView.findViewById(R.id.question_tv);
            correctans = (TextView)itemView.findViewById(R.id.correct_ans_tv);
            firstans = (TextView)itemView.findViewById(R.id.first_ans_tv);
            whichtry = (TextView)itemView.findViewById(R.id.which_try_tv);
            check = (ImageView)itemView.findViewById(R.id.is_correct_iv);
        }

        @Override
        public void onClick(View v) {



        }

    }

}