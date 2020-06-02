package com.akash.geoweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.input.InputManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.akash.geoweather.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class Result extends AppCompatActivity {

    TextView tv,tv2;
    Bundle bundle;
    Double lat,lon;
    ProgressBar pb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        bundle = getIntent().getExtras();
        lat = bundle.getDouble("lat");
        lon = bundle.getDouble("lon");

        pb = findViewById(R.id.pb);


        try {
            DownloadTask task = new DownloadTask();
            task.execute("https://openweathermap.org/data/2.5/weather?lat="+lat.toString()+"&lon="+lon.toString()+"&appid=439d4b804bc8187953eb36d2a8c26a02");
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Weather couldn't be found!",Toast.LENGTH_LONG).show();
            e.printStackTrace();
            pb.setAlpha(0);
            return;
        }
    }


    public class DownloadTask extends AsyncTask<String,Void,String> {


        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;
            }catch(Exception e) {
                e.printStackTrace();
                return null;
            }


        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tv = findViewById(R.id.tv);
            tv2 = findViewById(R.id.tv2);
            try{
                JSONObject jsonObject = new JSONObject(s);
                String weather = jsonObject.getString("weather");
                //tv.setText(weather);

                JSONArray arr = new JSONArray(weather);
                //JSONArray arr1 = new JSONArray(main1);

                String message ="";
                String temperature="",MaxTemp="",MinTemp="",city="";

                JSONObject reader = new JSONObject(s);
                JSONObject main  = reader.getJSONObject("main");
                temperature = main.getString("temp");
                MaxTemp = main.getString("temp_max");
                MinTemp = main.getString("temp_min");

                if(MaxTemp.equals(MinTemp)){
                    MaxTemp="";
                    MinTemp="";
                }else{
                    MaxTemp="Max Temp: "+main.getString("temp_max")+"°C \n";
                    MinTemp="Min Temp: "+main.getString("temp_min")+"°C";
                }
                city= jsonObject.getString("name");

                for(int i=0;i<arr.length();i++){
                    JSONObject part = arr.getJSONObject(i);

                    String main2=part.getString("main");
                    String description=part.getString("description");

                    if(!main.equals("") && !description.equals("")){
                        message += "Conditions" +": " + description +"\r\n";
                    }
                }
                if(!city.equals("")){
                    tv.setText(city);
                    tv.animate().alpha(1).setDuration(1000);
                }
                if(city.equals("")){
                    tv.setText("Unknown Location");
                    tv.animate().alpha(1).setDuration(1000);
                }
                if(!message.equals("")){
                    tv2.setText(message + "Temp: "+temperature+"°C" + "\n"+MaxTemp+MinTemp);
                    tv2.animate().alpha(1).setDuration(1000);
                    pb.setAlpha(0);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Try again!",Toast.LENGTH_LONG).show();
                    pb.setAlpha(0);
                }

            } catch (Exception e) {
                pb.setAlpha(0);
                Toast.makeText(getApplicationContext(),"Try again!",Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}
