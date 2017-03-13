package com.example.com.wingsbangladesh.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.com.wingsbangladesh.Interface.ItemClickListener;
import com.example.com.wingsbangladesh.Model.ModelMarchantInfo;
import com.example.com.wingsbangladesh.Model.ModelPrint;
import com.example.com.wingsbangladesh.R;

import java.util.List;

/**
 * Created by sabbir on 2/22/17.
 */


public class ConcernedPickUpAdapter extends RecyclerView.Adapter<ConcernedPickUpAdapter.MyViewHolder> {

    private List<ModelPrint> marchantlist;
    Context context;
    LinearLayout one,two,three;
    ModelMarchantInfo m2;
    private ItemClickListener clickListener;



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView1,textView2;
        public Button button;

        public MyViewHolder(View view) {
            super(view);
            textView1 = (TextView) view.findViewById(R.id.text1);
            textView2 = (TextView) view.findViewById(R.id.text2);
            button = (Button) view.findViewById(R.id.button);

            one = (LinearLayout) view.findViewById(R.id.name);
            two = (LinearLayout) view.findViewById(R.id.two);
            three = (LinearLayout) view.findViewById(R.id.three);

            view.setTag(view);
            view.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }


    public ConcernedPickUpAdapter(List<ModelPrint> List) {
        this.marchantlist = List;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pickup_item, parent, false);

        context = parent.getContext();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        if((position%2)==0)
        {
            one.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightblue));
            two.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightblue));
            three.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightblue));
            //four.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightblue));

        }
        else{

            one.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightbluelist));
            two.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightbluelist));
            three.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightbluelist));
          //  four.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightbluelist));



        }
        ModelPrint m = marchantlist.get(position);

        holder.textView1.setText(m.getPaperFlyOrder());
        holder.textView2.setText(m.getBarCode());
      //  holder.textView3.setText(m.getMarchentCode());


    }



    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }


    @Override
    public int getItemCount() {
        return marchantlist.size();
    }
}

