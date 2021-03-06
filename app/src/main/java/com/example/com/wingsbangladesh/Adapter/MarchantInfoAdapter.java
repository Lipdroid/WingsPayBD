package com.example.com.wingsbangladesh.Adapter;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.com.wingsbangladesh.Activity.ConcernedMarchantPickupActivity;
import com.example.com.wingsbangladesh.Interface.ItemClickListener;
import com.example.com.wingsbangladesh.Model.MarchantModel;
import com.example.com.wingsbangladesh.R;

import java.util.List;

/**
 * Created by sabbir on 2/22/17.
 */


public class MarchantInfoAdapter extends RecyclerView.Adapter<MarchantInfoAdapter.MyViewHolder> {

    private List<MarchantModel> list;
    Context context;
    LinearLayout one,two,three,four;
    private ItemClickListener clickListener;
    MarchantModel m;
    MarchantModel marchant = new MarchantModel();;
    String username,password,usertype,marchantcode,employeeName;



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView textView1,textView2,textView3,textView4;
        public LinearLayout lnrLayout,send;

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
            lnrLayout = (LinearLayout) view.findViewById(R.id.name);
            send = (LinearLayout) view.findViewById(R.id.next);

            view.setTag(view);
            view.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            if (clickListener != null) clickListener.onClick(view, getAdapterPosition());
        }
    }


    public MarchantInfoAdapter(List<MarchantModel> List,String username,String password,String usertype,String marchantcode,String employeeName) {
        this.list = List;
        this.username = username;
        this.password = password;
        this.usertype = usertype;
        this.marchantcode=marchantcode;
        this.employeeName=employeeName;


    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.marchant_info_item, parent, false);

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
            four.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightblue));

        }
        else{

            one.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightbluelist));
            two.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightbluelist));
            three.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightbluelist));
            four.setBackgroundColor(ContextCompat.getColor(context, R.color.colorlightbluelist));



        }



        marchant = list.get(position);

        holder.textView1.setText(marchant.getMerchantName());
        holder.textView2.setText(marchant.getTotorder());
        holder.textView3.setText(marchant.getLarge());
        holder.textView4.setText(marchant.getExpress());

//dial call
        holder.lnrLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent intent = new Intent(this, DetailLessonActivity.class);
                //intent.putExtra("id", lesson.getId());
                //      startActivity(intent);


                m=new MarchantModel();

                m=list.get(position);


                final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.custom_dialog);
                dialog.setTitle("Marchant Info");
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);


                TextView    name = (TextView) dialog.findViewById(R.id.text1);
                TextView  address = (TextView) dialog.findViewById(R.id.text2);
                TextView   phone = (TextView) dialog.findViewById(R.id.text3);

                String tempString=m.getCompanyPhone();
                //TextView text=(TextView)findViewById(R.id.text);
                SpannableString spanString = new SpannableString(tempString);
                spanString.setSpan(new UnderlineSpan(), 0, spanString.length(), 0);
                spanString.setSpan(new StyleSpan(Typeface.BOLD), 0, spanString.length(), 0);
                spanString.setSpan(new StyleSpan(Typeface.ITALIC), 0, spanString.length(), 0);
                phone.setText(spanString);

                name.setText(m.getMerchantName());
                address.setText(m.getAddress());
                phone.setText(m.getCompanyPhone());


                phone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {



                        try {


                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                            //    callIntent.setPackage("com.android.server.telecom");


                            callIntent.setData(Uri.parse("tel:" + m.getCompanyPhone()));
                            // callIntent.setData(Uri.parse(print.getMarchent_phone1()));

                            if (ActivityCompat.checkSelfPermission(context,
                                    Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {

                                context.startActivity(callIntent);
                                // return;
                            }
                            else{

                                //  Toast.makeText(getApplicationContext(), "Permission Required For calling", Toast.LENGTH_SHORT).show();

                            }


                        } catch (Exception e) {
                            // no activity to handle intent. show error dialog/toast whatever
                        }




                    }
                });

                ImageView add = (ImageView) dialog.findViewById(R.id.close);



                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        dialog.dismiss();

                        //    dailyTourPlanlist.addAll(sharedPreferencesData.getSharedPrefDailyTourPlan(Daily_Visit_Plan));
                    }
                });


                dialog.show();
            }
        });



        //next activity

        holder.send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                m=list.get(position);
                Intent intent = new Intent(context, ConcernedMarchantPickupActivity.class);

                intent.putExtra("username",username);
                intent.putExtra("password",password);
                intent.putExtra("usertype",usertype);
                intent.putExtra("marchantcode",m.getMerchantCode());
                intent.putExtra("employeeName",employeeName);

                context.startActivity(intent);

            }
        });

    }



    public void setClickListener(ItemClickListener itemClickListener) {
        this.clickListener = itemClickListener;
    }




    @Override
    public int getItemCount() {
        return list.size();
    }
}
