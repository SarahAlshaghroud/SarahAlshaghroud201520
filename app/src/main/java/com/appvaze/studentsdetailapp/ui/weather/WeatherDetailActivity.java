package com.appvaze.studentsdetailapp.ui.weather;

import static com.appvaze.studentsdetailapp.util.Constant.API_KEY;
import static com.appvaze.studentsdetailapp.util.Constant.API_URL;
import static com.appvaze.studentsdetailapp.util.Constant.networkInfo;

import androidx.appcompat.app.AppCompatActivity;

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
import com.appvaze.studentsdetailapp.databinding.ActivityWeatherDetailBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class WeatherDetailActivity extends AppCompatActivity {

    ActivityWeatherDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWeatherDetailBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        setSupportActionBar(binding.detailWeatherToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String cityName = getIntent().getStringExtra("cityName");
        if(cityName!=null && networkInfo!=null)
            getCityWeatherInfo(cityName);
        else {
            binding.progressBar4.setVisibility(View.GONE);
            binding.tvDetailWeatherTemp.setText("No Internet");
        }
    }

    private void getCityWeatherInfo(String cityName) {
        RequestQueue requestQueue = Volley.newRequestQueue(WeatherDetailActivity.this);
        String temp_url=API_URL+cityName+API_KEY;

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, temp_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                binding.progressBar4.setVisibility(View.GONE);
                getWeatherInfo(response);
                getMainInfo(response);
                getWindSpeed(response);

                try {
                    binding.tvDetailWeatherCityName.setText(response.getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(WeatherDetailActivity.this, "No Found", Toast.LENGTH_SHORT).show();
                binding.progressBar4.setVisibility(View.GONE);
            }
        });
        requestQueue.add(request);
    }

    private void getWindSpeed(JSONObject response) {
        try {

            JSONObject object = response.getJSONObject("wind");
            binding.tvDetailWeatherWindSpeed.setText(object.getString("speed") + " km/h");

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(WeatherDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
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
                binding.tvDetailWeatherStatus.setText(weatherStatus);

                if(weatherStatus.equals("Clear"))
                    binding.tvDetailWeatherImg.setImageResource(R.drawable.sun);
                else if(weatherStatus.equals("Rain"))
                    binding.tvDetailWeatherImg.setImageResource(R.drawable.heavy_rain);
                else if(weatherStatus.equals("Atmosphere"))
                    binding.tvDetailWeatherImg.setImageResource(R.drawable.wind);
                else if(weatherStatus.equals("Clouds"))
                    binding.tvDetailWeatherImg.setImageResource(R.drawable.clouds);
                else if(weatherStatus.equals("Drizzle"))
                    binding.tvDetailWeatherImg.setImageResource(R.drawable.drizzle);
                else if(weatherStatus.equals("Thunderstorm"))
                    binding.tvDetailWeatherImg.setImageResource(R.drawable.thunderstorm);
                else
                    binding.tvDetailWeatherImg.setImageResource(R.drawable.sun);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void getMainInfo(JSONObject response) {
        try {

            JSONObject object = response.getJSONObject("main");
            Double temp = Double.parseDouble(object.getString("temp")) - 273.15;
            Double temp_min = Double.parseDouble(object.getString("temp_min")) - 273.15;
            Double temp_max = Double.parseDouble(object.getString("temp_max")) - 273.15;
            int pressure = Integer.parseInt(object.getString("pressure"));
            int humidity = Integer.parseInt(object.getString("humidity"));

            String maxMinTemp = String.valueOf(temp_min).substring(0, 2) + " | " + String.valueOf(temp_max).substring(0, 2);

            binding.tvDetailWeatherTemp.setText(String.valueOf(temp).substring(0, 2) + " c");
            binding.tvDetailWeatherHighLowTemp.setText(maxMinTemp);
            binding.tvDetailWeatherPressure.setText(pressure + " hPa");
            binding.tvDetailWeatherHumidity.setText(String.valueOf(humidity));

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(WeatherDetailActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }
}
