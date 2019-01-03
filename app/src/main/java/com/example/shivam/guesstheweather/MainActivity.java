package com.example.shivam.guesstheweather;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    TextView city, weather;
    Button find;

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data!=-1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }
                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return "failed";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                JSONObject object = new JSONObject(s);
                String weatherInfo = object.getString("weather");

                JSONArray array = new JSONArray(weatherInfo);
                System.out.println(weatherInfo);

                for (int i=0; i<array.length(); i++) {
                    JSONObject jsonPart = array.getJSONObject(i);

                    weather.setText(jsonPart.getString("main")+" : "+jsonPart.getString("description")+"\r\n");

                    Log.i("main: ", jsonPart.getString("main"));
                    Log.i("desc: ", jsonPart.getString("description"));
                }

            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Unable to find weather!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void findWeather(View view) {

        DownloadTask task = new DownloadTask();
        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(weather.getWindowToken(), 0);

        Log.i("city: ", city.getText().toString());

        try {
                String cty = URLEncoder.encode(city.getText().toString(), "UTF-8");
                task.execute("https://openweathermap.org/data/2.5/weather?q=" + cty + "&appid=b6907d289e10d714a6e88b30761fae22");
        }catch(Exception e){
            e.printStackTrace();
            }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = findViewById(R.id.city);
        weather = findViewById(R.id.weather);
    }
}