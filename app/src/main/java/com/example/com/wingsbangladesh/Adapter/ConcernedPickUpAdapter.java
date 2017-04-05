package com.example.com.wingsbangladesh.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.com.wingsbangladesh.Activity.PrintActivity;
import com.example.com.wingsbangladesh.Interface.ItemClickListener;
import com.example.com.wingsbangladesh.Model.ModelBarcodeList;
import com.example.com.wingsbangladesh.R;

import java.util.List;

/**
 * Created by sabbir on 2/22/17.
 */


public class ConcernedPickUpAdapter extends RecyclerView.Adapter<ConcernedPickUpAdapter.MyViewHolder> {

    private List<ModelBarcodeList> barcodelist;
    Context context;
    LinearLayout one,two,three;
    private ItemClickListener clickListener;
    String barcodeApi,barcodeType;
    ModelBarcodeList m;




    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView1,textView2;
        public Button button;
        Button btn;
        public MyViewHolder(View view) {
            super(view);
            textView1 = (TextView) view.findViewById(R.id.text1);
            textView2 = (TextView) view.findViewById(R.id.text2);
            button = (Button) view.findViewById(R.id.button);

            one = (LinearLayout) view.findViewById(R.id.name);
            two = (LinearLayout) view.findViewById(R.id.two);
            three = (LinearLayout) view.findViewById(R.id.three);

             btn = (Button) view.findViewById(R.id.button);

            view.setTag(view);
            view.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }


    public ConcernedPickUpAdapter(List<ModelBarcodeList> List) {

        this.barcodelist = List;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pickup_item, parent, false);

        context = parent.getContext();

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {



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
         m = barcodelist.get(position);

        holder.textView1.setText(m.getOrderid());
        holder.textView2.setText(m.getBarcode());
      //  holder.textView3.setText(m.getMarchentCode());


       holder.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

ModelBarcodeList p;
                p = barcodelist.get(position);

                Intent intent = new Intent(context, PrintActivity.class);
                intent.putExtra("orderId", p.getOrderid());
                intent.putExtra("marchantref", p.getMerOrderRef());
                intent.putExtra("marchantcode", p.getMerchantCode());
                intent.putExtra("productprice", p.getPackagePrice());
                intent.putExtra("phone", p.getPhone());
                intent.putExtra("barcode", p.getBarcode());

               context.startActivity(intent);
               // ((Activity)context).finish();*/
            }
        });


    }



    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }


    @Override
    public int getItemCount() {
        return barcodelist.size();
    }
}

