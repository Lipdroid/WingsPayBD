package com.example.com.wingsbangladesh.Adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.com.wingsbangladesh.Model.CustomModel;
import com.example.com.wingsbangladesh.Interface.ItemClickListener;
import com.example.com.wingsbangladesh.Model.ModelMarchantInfo;
import com.example.com.wingsbangladesh.R;

import java.util.List;

/**
 * Created by sabbir on 2/22/17.
 */


public class MarchantInfoAdapter extends RecyclerView.Adapter<MarchantInfoAdapter.MyViewHolder> {

    private List<CustomModel> list;
    private List<ModelMarchantInfo> marchantlist;
    Context context;
    LinearLayout one,two,three,four;
    ModelMarchantInfo m2;
    private ItemClickListener clickListener;



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView1,textView2,textView3,textView4;

        public MyViewHolder(View view) {
            super(view);
            textView1 = (TextView) view.findViewById(R.id.text1);
            textView2 = (TextView) view.findViewById(R.id.text2);
            textView3 = (TextView) view.findViewById(R.id.text3);
            textView4 = (TextView) view.findViewById(R.id.text4);

            one = (LinearLayout) view.findViewById(R.id.name);
            two = (LinearLayout) view.findViewById(R.id.two);
            three = (LinearLayout) view.findViewById(R.id.three);
            four = (LinearLayout) view.findViewById(R.id.four);

            view.setTag(view);
            view.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }


    public MarchantInfoAdapter(List<CustomModel> List,List<ModelMarchantInfo> marchantlist) {
        this.list = List;
        this.marchantlist = marchantlist;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.marchant_info_item, parent, false);

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
            four.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightblue));

        }
        else{

            one.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightbluelist));
            two.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightbluelist));
            three.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightbluelist));
            four.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightbluelist));



        }

        CustomModel m = list.get(position);

         m2 = marchantlist.get(position);

        holder.textView1.setText(m.getMarchent_name());
        holder.textView2.setText(m.getTotal_order_count());
        holder.textView3.setText(m.getLs_order_count());
        holder.textView4.setText(m.getExpress_order_cont());

    }



    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }




    @Override
    public int getItemCount() {
        return list.size();
    }
}
