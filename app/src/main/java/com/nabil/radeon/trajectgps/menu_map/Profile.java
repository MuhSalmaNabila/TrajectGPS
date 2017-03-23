package com.nabil.radeon.trajectgps.menu_map;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.nabil.radeon.trajectgps.JSONResponse;
import com.nabil.radeon.trajectgps.R;
import com.nabil.radeon.trajectgps.api_interface.MapInterface;
import com.nabil.radeon.trajectgps.model.AndroidVersion;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Profile extends AppCompatActivity {
    TextView driver_velocity;
    TextView driver_habit;
    Double averageKec = 0d;
    int averageKec_Count = 0;
    String strID;
    private ArrayList<AndroidVersion> data;
    Intent intent;
    int low = 0;
    int standard = 0;
    int high = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setStatusBarTranslucent(true);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        intent = getIntent();
        strID = intent.getStringExtra("p_id");
        Log.d("IDClickProfileAfter", strID);
        loadJSON();
    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    private void loadJSON() {
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.readTimeout(60, TimeUnit.SECONDS);
        client.connectTimeout(60, TimeUnit.SECONDS);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.236.1")
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MapInterface request = retrofit.create(MapInterface.class);

        Call<JSONResponse> call = request.getJSON(strID, "-");
        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                JSONResponse jsonResponse = (JSONResponse) response.body();
                data = new ArrayList<>(Arrays.asList(jsonResponse.getRoute()));
                Log.d("jumlahArrayperIDProfile", String.valueOf(data.size()));
                ArrayList<AndroidVersion> dataprofile = new ArrayList<>(Arrays.asList(jsonResponse.getProfile()));
                Log.d("profil", String.valueOf(dataprofile.size()));

                DecimalFormat df = new DecimalFormat("#.##");
                df.setRoundingMode(RoundingMode.CEILING);
                strID = intent.getStringExtra("p_id");

                driver_habit = (TextView) findViewById(R.id.driver_habit);
                driver_velocity = (TextView) findViewById(R.id.driver_velocity);

                String taxi_id = dataprofile.get(0).getProfile_id();
                String name = dataprofile.get(0).getName();
                String email = dataprofile.get(0).getEmail();
                String phone = dataprofile.get(0).getPhone();

                TextView ID_taxi = (TextView)findViewById(R.id.ID_taxi);
                ID_taxi.setText(taxi_id);
                TextView driver_name = (TextView)findViewById(R.id.driver_name);
                driver_name.setText(name);
                TextView driver_email = (TextView)findViewById(R.id.driver_email);
                driver_email.setText(email);
                TextView driver_phone = (TextView)findViewById(R.id.driver_phone);
                driver_phone.setText(phone);

                for (int i = 0; i < data.size(); i++) {
                    double kec = data.get(i).getKecepatan();
                    if (kec > 0) {
                        averageKec += kec;
                        averageKec_Count++;
                    }
                    if (kec <=40){
                        low++;
                    }
                    if (kec > 40 && kec <= 60){
                        standard++;
                    }
                    if (kec > 60){
                        high++;
                    }
                    else if (kec == 0 && kec <= 0){
                        Log.d("Driver", "Not move");
                        driver_velocity.setText("0 Km/h");
                        driver_habit.setText("not move");
                    }
                    else if (low > standard && low > high ){
                        driver_habit.setText(R.string.lowspeed);
                    }
                    else if (standard > low && standard > high){
                        driver_habit.setText(R.string.normalspeed);
                    }
                    else if (high > low && high> standard){
                        driver_habit.setText(R.string.highspeed);
                    }
                }
                averageKec = averageKec / averageKec_Count;

                if (averageKec <=40)
                {
                    Log.d("Driver", "Low Speed");
                    driver_velocity.setText(String.valueOf(df.format(averageKec)) + " Km/h");
                }
                if (averageKec >= 41 && averageKec <= 60)
                {
                    Log.d("Driver", "biasa");
                    driver_velocity.setText(String.valueOf(df.format(averageKec)) + " Km/h");
                }
                else if (averageKec > 60)
                {
                    Log.d("Driver", "ngebut");
                    driver_velocity.setText(String.valueOf(df.format(averageKec)) + " Km/h");
                }
            }
            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                Log.d("Error", t.getMessage());
            }
        });
    }
}

