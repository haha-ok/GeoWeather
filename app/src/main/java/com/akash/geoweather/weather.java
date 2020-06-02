package com.akash.geoweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class weather extends AppCompatActivity {

    EditText e1;
    TextView tv2,tv3;
    Button b1;
    ProgressBar pb;

    // for moving to maps activity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);
        e1 = findViewById(R.id.edit);
        b1 = findViewById(R.id.button1);
        pb = findViewById(R.id.pb);

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(),MapsActivity.class);
                i.setClass(getApplicationContext(), MapsActivity.class);
                startActivity(i);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    // click listenter for what's the weather button
    public void getWeather(View view){

        pb.animate().alpha(1).setDuration(100);
        // fetching the json
        try {
            String  encodedCityName = URLEncoder.encode(e1.getText().toString(), "UTF-8");
            DownloadTask task = new DownloadTask();
            task.execute("https://openweathermap.org/data/2.5/weather?q="+encodedCityName+"&appid=439d4b804bc8187953eb36d2a8c26a02");
            InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            mgr.hideSoftInputFromWindow(e1.getWindowToken(), 0);
        }

        catch (Exception e) {
            Toast.makeText(getApplicationContext(),"Weather couldn't be found!",Toast.LENGTH_LONG).show();
            tv2.setAlpha(0);
            tv3.setAlpha(0);
            e.printStackTrace();
            pb.animate().alpha(0).setDuration(300);
            return;
        }

    }

    // processing the json string and returnoing it as a string
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


        // json parsing
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            tv2 = findViewById(R.id.tv2);
            tv3 = findViewById(R.id.tv3);
            tv2.setMovementMethod(new ScrollingMovementMethod());
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
                city= jsonObject.getString("name");
                if(MaxTemp.equals(MinTemp)){
                    MaxTemp="";
                    MinTemp="";
                }else{
                    MaxTemp="Max Temp: "+main.getString("temp_max")+"°C \n";
                    MinTemp="Min Temp: "+main.getString("temp_min")+"°C";
                }

                for(int i=0;i<arr.length();i++){
                    JSONObject part = arr.getJSONObject(i);

                    String main2=part.getString("main");
                    String description=part.getString("description");

                    if(!main.equals("") && !description.equals("")){
                        message += "Conditions" +": " + description +"\r\n";
                    }
                }
                if(!city.equals("")){
                    tv3.setText(city);
                    e1.setText("");
                    tv3.setAlpha(1);
                    //tv3.setBackgroundColor(000000);
                }
                if(city.equals("")){
                    tv3.setText("Unknown Location");
                    e1.setText("");
                    tv3.setAlpha(1);
                }
                if(!message.equals("")){
                    //Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
                    tv2.setText(message + "Temp: "+temperature+"°C" + "\n"+MaxTemp+MinTemp);
                    pb.animate().alpha(0).setDuration(300);
                tv2.setAlpha(1);//tv2.setBackgroundColor(000000);
                }
                else{
                    Toast.makeText(getApplicationContext(),"Enter a valid city!",Toast.LENGTH_LONG).show();
                    tv2.setAlpha(0);
                    pb.animate().alpha(0).setDuration(300);
                    tv3.setAlpha(0);
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(),"Enter a valid city!",Toast.LENGTH_LONG).show();
                tv2.setAlpha(0);
                tv3.setAlpha(0);
                pb.animate().alpha(0).setDuration(300);
                e.printStackTrace();
            }
        }
    }
}
