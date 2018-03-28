package com.example.bbim1041.bbstore.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.bbim1041.bbstore.R;
import com.example.bbim1041.bbstore.model.data.App;
import com.example.bbim1041.bbstore.model.data.SectionHeader;
import com.example.bbim1041.bbstore.view.AppListFragment;
import com.intrusoft.sectionedrecyclerview.SectionRecyclerViewAdapter;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by BBIM1041 on 20/03/18.
 */
public class AdapterSectionRecycler extends SectionRecyclerViewAdapter<SectionHeader, App, AdapterSectionRecycler.SectionViewHolder, AdapterSectionRecycler.ChildViewHolder> {

    Activity context;

    int textSize;
    AdapterSectionRecycler.RecyclerListItemClickListener recyclerListItemClickListener;

    public AdapterSectionRecycler(Activity context, List<SectionHeader> sectionItemList, AppListFragment appListFragment, int textSize) {
        super(context, sectionItemList);
        this.context = context;
        this.textSize = textSize;

        this.recyclerListItemClickListener = (AdapterSectionRecycler.RecyclerListItemClickListener) appListFragment;

    }

    @Override
    public SectionViewHolder onCreateSectionViewHolder(ViewGroup sectionViewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.section_item, sectionViewGroup, false);
        return new SectionViewHolder(view);
    }

    @Override
    public ChildViewHolder onCreateChildViewHolder(ViewGroup childViewGroup, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.app_single_item, childViewGroup, false);
        return new ChildViewHolder(view);
    }

    @Override
    public void onBindSectionViewHolder(SectionViewHolder sectionViewHolder, int sectionPosition, SectionHeader section) {

        if (!section.getSectionText().equals("")) {
            try {
                DateFormat format = new SimpleDateFormat("MM/dd/yy", Locale.ENGLISH);
                Date date = format.parse(section.getSectionText());
                sectionViewHolder.name.setText(theMonth(date.getMonth()) + "," + section.sectionText.substring(6, 8)

                );

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            sectionViewHolder.name.setText("Older");
        }


    }

    public static String theMonth(int month) {
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return monthNames[month];
    }

    @Override
    public void onBindChildViewHolder(final ChildViewHolder holder, int position, int i1, final App app) {
        holder.apkTitleTextView.setTextSize(textSize);
        holder.apkTitleTextView.setText(app.getApk_name().replace(".apk", ""));
        double sizeOfApk = Double.valueOf(app.getApk_length()) / (1024 * 1024);

        DecimalFormat df = new DecimalFormat("#.##");

        holder.apkSizeTextView.setText(df.format(sizeOfApk) + "");
        holder.apkSizeTextView.setTextSize(textSize);
        if (!app.getApk_date().isEmpty()) {
            holder.apkDateTextView.setText(app.getApk_date());
        } else {
            holder.apkDateTextView.setText("Older");
        }
        holder.apkDateTextView.setTextSize(textSize);

        holder.apkDownloadImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.download));

        holder.apkDownloadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerListItemClickListener.onCLickListener(app);
            }
        });
        
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if (app.getApk_type().equalsIgnoreCase("beta"))
                    holder.betaImageView.setVisibility(View.VISIBLE);
                else 
                    holder.betaImageView.setVisibility(View.GONE);

            }
        });
        
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerListItemClickListener.onListItemClickListener(app);
            }
        });
       
            
       
        
    }


    public interface RecyclerListItemClickListener {
        void onCLickListener(App appObject);
        void onListItemClickListener(App appObject);
    }


    public class SectionViewHolder extends RecyclerView.ViewHolder {

        TextView name;

        public SectionViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.section);
        }
    }

    public class ChildViewHolder extends RecyclerView.ViewHolder {

        TextView apkTitleTextView, apkSizeTextView, apkTypeTextView, apkDateTextView;

        ImageView apkImage, apkDownloadImage, betaImageView;

        public ChildViewHolder(View itemView) {
            super(itemView);
            apkTitleTextView = (TextView) itemView.findViewById(R.id.apk_name_tv);

            apkDateTextView = (TextView) itemView.findViewById(R.id.apk_date_tv);
            apkSizeTextView = (TextView) itemView.findViewById(R.id.apk_size_tv);


            apkImage = itemView.findViewById(R.id.apkImage);
            apkDownloadImage = itemView.findViewById(R.id.apkDownloadImage);

            betaImageView = itemView.findViewById(R.id.betaImageView);
        }
    }

}