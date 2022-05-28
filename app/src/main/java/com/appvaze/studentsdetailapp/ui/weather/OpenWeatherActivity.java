package com.appvaze.studentsdetailapp.ui.weather;

import static com.appvaze.studentsdetailapp.util.Constant.API_KEY;
import static com.appvaze.studentsdetailapp.util.Constant.API_URL;
import static com.appvaze.studentsdetailapp.util.Constant.makeToast;
import static com.appvaze.studentsdetailapp.util.Constant.networkInfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.appvaze.studentsdetailapp.R;
import com.appvaze.studentsdetailapp.databinding.ActivityOpenWeatherBinding;
import com.appvaze.studentsdetailapp.sqlite.OpenHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class OpenWeatherActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityOpenWeatherBinding binding;

    OpenHelper helper;
    SQLiteDatabase database;

    ArrayList<String> list;

    String cityName = "Berlin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityOpenWeatherBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.openWeatherToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        helper = new OpenHelper(this);
        database=helper.getWritableDatabase();

        list = new ArrayList<>();

        if(networkInfo!=null){
            binding.progressBar.setVisibility(View.VISIBLE);
            searchCityWeather(cityName);
            binding.btnSearchWeather.setEnabled(true);
        } else
            binding.tvShowLiveTemp.setText("No Internet");

        binding.btnSearchWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                cityName = binding.etCityNname.getText().toString().toLowerCase().trim();

                if(cityName.isEmpty()) {
                    binding.etCityNname.setError("Enter City Name");
                    binding.etCityNname.requestFocus();
                    binding.etCityNname.performClick();
                } else {
                    binding.progressBar.setVisibility(View.VISIBLE);
                    searchCityWeather(cityName);
                }
            }
        });

        binding.tvShowLiveTemp.setOnClickListener(this);
        binding.tvCityName.setOnClickListener(this);
    }

    private void searchCityWeather(String cityName) {

        RequestQueue requestQueue = Volley.newRequestQueue(OpenWeatherActivity.this);
        String temp_url=API_URL+cityName+API_KEY;

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, temp_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                binding.progressBar.setVisibility(View.GONE);
                getWeatherInfo(response);
                getMainInfo(response);

                try {
                    binding.tvCityName.setText(response.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                makeToast("No Found");
                binding.progressBar.setVisibility(View.GONE);
            }
        });
        requestQueue.add(request);
    }

    private void getWeatherInfo(JSONObject response) {
        JSONArray arr = null;
        try {
            arr = response.getJSONArray("weather");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JSONObject element;
        for(int i = 0; i < arr.length(); i++){
            try {
                element = arr.getJSONObject(i);
                String weatherStatus = element.getString("main");
                binding.tvWeatherStatus.setText(weatherStatus);

                if(weatherStatus.equals("Clear"))
                    binding.imageView4.setImageResource(R.drawable.sun);
                else if(weatherStatus.equals("Rain"))
                    binding.imageView4.setImageResource(R.drawable.heavy_rain);
                else if(weatherStatus.equals("Atmosphere"))
                    binding.imageView4.setImageResource(R.drawable.wind);
                else if(weatherStatus.equals("Clouds"))
                    binding.imageView4.setImageResource(R.drawable.clouds);
                else if(weatherStatus.equals("Drizzle"))
                    binding.imageView4.setImageResource(R.drawable.drizzle);
                else if(weatherStatus.equals("Thunderstorm"))
                    binding.imageView4.setImageResource(R.drawable.thunderstorm);
                else
                    binding.imageView4.setImageResource(R.drawable.sun);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getMainInfo(JSONObject response) {
        try {

            JSONObject object=response.getJSONObject("main");
            Double temp=Double.parseDouble(object.getString("temp"))-273.15;
            Double temp_min=Double.parseDouble(object.getString("temp_min"))-273.15;
            Double temp_max=Double.parseDouble(object.getString("temp_max"))-273.15;
            int pressure=Integer.parseInt(object.getString("pressure"));
            int humidity=Integer.parseInt(object.getString("humidity"));

            binding.tvShowLiveTemp.setText(String.valueOf(temp).substring(0,2)+" c");
            binding.tvShowLivePressure.setText(pressure +" hPa");
            binding.tvShowLiveHumidity.setText(String.valueOf(humidity));

        } catch (Exception e) {
            e.printStackTrace();
            makeToast(e.toString());
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(),WeatherDetailActivity.class);
        intent.putExtra("cityName", binding.tvCityName.getText().toString().trim());
        switch (view.getId()){
            case R.id.tv_showLiveTemp:
            case R.id.tv_cityName:
                startActivity(intent);
                break;
        }
    }
}