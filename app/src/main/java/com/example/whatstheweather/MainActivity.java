package com.example.whatstheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    // widgets
    private EditText mEnterCity;
    private TextView mWeatherData;

    public void onGoClick(View view) {

        mEnterCity = findViewById(R.id.etEnterCity);

        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mEnterCity.getWindowToken(), 0);

        mWeatherData = findViewById(R.id.txtWeatherData);

        try {
            String encodedCityName = URLEncoder
                    .encode(mEnterCity.getText().toString(), "UTF-8");

            FetchWeatherAPITask fetchWeatherAPITask = new FetchWeatherAPITask();
            fetchWeatherAPITask
                    .execute("https://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName
                            + "&appid=7b35086e80a579855a86a30689073378");

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();

            Toast.makeText(MainActivity.this,
                    "Error occurred!", Toast.LENGTH_SHORT).show();
        }
    }

    public class FetchWeatherAPITask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            URL url;
            HttpURLConnection httpURLConnection;
            String result = "";

            try {
                url = new URL(urls[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.connect();

                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                int data = inputStreamReader.read();

                while (data != -1) {

                    char current = (char) data;
                    result += current;

                    data = inputStreamReader.read();
                }

                return result;

            } catch (IOException e) {

                e.printStackTrace();

                Toast.makeText(MainActivity.this,
                        "Error occurred!", Toast.LENGTH_SHORT).show();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject apiObj = new JSONObject(result);
                String weather = apiObj.getString("weather");

                JSONArray weatherArray = new JSONArray(weather);

                for (int i = 0; i < weatherArray.length(); i++) {
                    JSONObject weatherArrayObj = weatherArray.getJSONObject(i);
                    String weatherData = weatherArrayObj.getString("description");

                    if (!weatherData.equals("")) {

                        mWeatherData.setText(weatherData);

                    } else {
                        Toast.makeText(MainActivity.this,
                                "Error occurred!", Toast.LENGTH_SHORT).show();
                    }

                }

            } catch (JSONException e) {

                e.printStackTrace();

                Toast.makeText(MainActivity.this,
                        "Error occurred!", Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}