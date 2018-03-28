package com.example.bbim1041.bbstore;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.bbim1041.bbstore.view.AppListFragment;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity  {

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.appListFrame, new AppListFragment()).commit();
        }
        
        
      
        
    }


  /*  @Override
    public void afterAppListDownloaded(ArrayList<AppModel> appModelList) {
        for(AppModel appModel:appModelList) {
            Log.v("DataModels",appModel.toString());
        }
        
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hideDownloading();     
            }
        });
    }

    public void showDownloading(){
        mLoadingMessage.setVisibility(View.VISIBLE);
        mLoadingProgressBar.setVisibility(View.VISIBLE);
        mAppListRecyclerView.setVisibility(View.INVISIBLE);
    }
    
    public void hideDownloading() {
        mLoadingMessage.setVisibility(View.INVISIBLE);
        mLoadingProgressBar.setVisibility(View.INVISIBLE);
        mAppListRecyclerView.setVisibility(View.VISIBLE);
    }*/
}
