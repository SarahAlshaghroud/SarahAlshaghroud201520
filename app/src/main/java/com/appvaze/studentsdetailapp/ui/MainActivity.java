package com.appvaze.studentsdetailapp.ui;

import static com.appvaze.studentsdetailapp.util.Constant.API_KEY;
import static com.appvaze.studentsdetailapp.util.Constant.API_URL;
import static com.appvaze.studentsdetailapp.util.Constant.networkInfo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.appvaze.studentsdetailapp.databinding.ActivityMainBinding;
import com.appvaze.studentsdetailapp.ui.weather.OpenWeatherActivity;
import com.appvaze.studentsdetailapp.ui.weather.WeatherDetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        if(networkInfo!=null)
            searchCityWeather();
        else {
            binding.progressBar3.setVisibility(View.GONE);
            binding.textView3.setText("No Internet");
        }


        binding.cvAllStds.setOnClickListener(this);
        binding.cvOpenWeather.setOnClickListener(this);
        binding.cvLocalDb.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(),StudentDetailActivity.class); //move from this to this
        switch (view.getId()){
            case R.id.cv_allStds:
                intent.putExtra("activityType", "0"); //firebase class
                startActivity(intent);
                break;
            case R.id.cv_openWeather:
                startActivity(new Intent(view.getContext(), OpenWeatherActivity.class)); //openweather
                break;
            case R.id.cv_localDb:
                intent.putExtra("activityType", "1"); //localDB
                startActivity(intent);
                break;
        }
    }

    private void searchCityWeather() {

        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String temp_url=API_URL+"Berlin"+API_KEY;

        JsonObjectRequest request=new JsonObjectRequest(Request.Method.GET, temp_url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                binding.progressBar3.setVisibility(View.GONE);
                binding.textView3.setVisibility(View.VISIBLE);

                try {
                    String city;
                    city = response.getString("name");

                    JSONObject object=response.getJSONObject("main");
                    String temp=String.valueOf(Double.parseDouble(object.getString("temp"))-273.15).substring(0,2);
                    JSONArray jsonArray = response.getJSONArray("weather");
                    for (int i=0; i<jsonArray.length();i++) {
                        JSONObject oneObject = jsonArray.getJSONObject(i);
                        String weather = oneObject.getString("main");
                        if(weather.equals("Clear"))
                            binding.imageView3.setImageResource(R.drawable.sun);
                        else if(weather.equals("Rain"))
                            binding.imageView3.setImageResource(R.drawable.heavy_rain);
                        else if(weather.equals("Atmosphere"))
                            binding.imageView3.setImageResource(R.drawable.wind);
                        else if(weather.equals("Clouds"))
                            binding.imageView3.setImageResource(R.drawable.clouds);
                        else if(weather.equals("Drizzle"))
                            binding.imageView3.setImageResource(R.drawable.drizzle);
                        else if(weather.equals("Thunderstorm"))
                            binding.imageView3.setImageResource(R.drawable.thunderstorm);
                        else
                            binding.imageView3.setImageResource(R.drawable.sun);
                    }

                    binding.textView3.setText(String.format("%s - %s", city, temp));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "not Found", Toast.LENGTH_SHORT).show();
                binding.progressBar3.setVisibility(View.GONE);
            }
        });
        requestQueue.add(request);
    }
}