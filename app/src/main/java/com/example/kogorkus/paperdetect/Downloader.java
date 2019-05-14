package com.example.kogorkus.paperdetect;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;

public class Downloader extends AsyncTask<Void,Void,String>{

    private String urlAddess;
    private String jsonData;

    public String getJsonData() {
        return jsonData;
    }

    public Downloader(String urlAddess) {
        this.urlAddess = urlAddess;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();


    }

    @Override
    protected String doInBackground(Void... params) {

        return this.downloadData();
    }

    @Override
    protected void onPostExecute(String jsonData) {
        //super.onPostExecute(jsonData);
        if(jsonData.startsWith("Error"))
        {
            Log.e("tag", "error in downloader");
        }

    }

    private String downloadData()
    {
        HttpURLConnection connection=Connector.connect(urlAddess);
        Log.d("tag", connection.toString());
        if(connection.toString().startsWith("Error"))
        {

            return connection.toString();
        }

        try {
            HttpURLConnection con=  connection;

            InputStream is=new BufferedInputStream(con.getInputStream());
            BufferedReader br=new BufferedReader(new InputStreamReader(is));

            String line;
            StringBuffer jsonDataSB=new StringBuffer();


            while ((line=br.readLine()) != null)
            {
                jsonDataSB.append(line);

            }

            Log.d("tag", String.valueOf(jsonDataSB));

            br.close();
            is.close();
            Log.d("jsonData", jsonDataSB.toString());
            jsonData = jsonDataSB.toString();
            return jsonDataSB.toString();

        } catch (IOException e) {
            e.printStackTrace();
            return "Error "+e.getMessage();
        }

    }
}
