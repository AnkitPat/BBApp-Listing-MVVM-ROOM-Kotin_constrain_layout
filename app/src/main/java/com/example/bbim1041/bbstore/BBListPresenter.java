package com.example.bbim1041.bbstore;



import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Ankit Patidar on 22/02/18.
 * Class is Presenter Type for working network calls
 */

class BBListPresenter {

    private DataPasser dataPasser;
    
    BBListPresenter(DataPasser dataPasser) {
        this.dataPasser = dataPasser;
        
    }
    
     void fetchListFromServer(final String url) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String fileData = downloadFileData(url);
                ArrayList<AppModel> appModels = parseData(fileData);
                dataPasser.afterAppListDownloaded(appModels);
            }
        }).start();
    }
    
    private ArrayList<AppModel> parseData(String fileData) {
        try{
            JSONObject jsonObject = new JSONObject(fileData);
            JSONArray appList  = jsonObject.getJSONArray("app");
            ArrayList<AppModel> appModels = new ArrayList<>();
            for(int i=0;i<appList.length();i++)
            {
                String appText = appList.getString(i);
                
                String appDatas[] = appText.split("%");
                if (appDatas.length==3) {
                    appModels.add(new AppModel(appDatas[0],appDatas[1],appDatas[2]));
                }
                else if(appDatas.length==2) {
                    appModels.add(new AppModel(appDatas[0],"",appDatas[1]));
                }
                else {
                    appModels.add(new AppModel(appDatas[0],"",""));
                }
            }
            
            return appModels;
            
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
        
        
    }
    
    
     private String downloadFileData(String fileUrl){
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(fileUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();


            InputStream stream = connection.getInputStream();

            reader = new BufferedReader(new InputStreamReader(stream));

            StringBuilder buffer = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                buffer.append(line).append("\n");
            }

            return buffer.toString();


        } catch (Exception e) {
            e.printStackTrace();
        }  finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
    
    public interface DataPasser {
        void afterAppListDownloaded(ArrayList<AppModel> appModelList);
    }
}
