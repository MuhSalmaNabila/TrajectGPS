package com.nabil.radeon.trajectgps.menu_map;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView;

import java.lang.reflect.Method;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_HYBRID;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_NORMAL;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_SATELLITE;
import static com.google.android.gms.maps.GoogleMap.MAP_TYPE_TERRAIN;
import static com.nabil.radeon.trajectgps.R.id.map;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.nabil.radeon.trajectgps.model.AndroidVersion;
import com.nabil.radeon.trajectgps.JSONResponse;
import com.nabil.radeon.trajectgps.api_interface.MapInterface;
import com.nabil.radeon.trajectgps.R;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapTaxi extends AppCompatActivity implements GoogleMap.OnMyLocationButtonClickListener,AdapterView.OnItemSelectedListener, OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback{
    /*
     * Request code for location permission request.
     *
     * @see #onRequestPermissionsResult(int, String[], int[])
     */
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private static final int LOCATION_LAYER_PERMISSION_REQUEST_CODE = 2;
    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private static final String URL = "http://192.168.43.176";
    private boolean mPermissionDenied = false;
    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private ArrayList<AndroidVersion> data;
    String strID;
    Intent intent;
    int intSelected = 0;
    boolean bolSpinnerTouched = false;
//    private ProgressDialog pDialog;
    List<Polyline> polylines = new ArrayList<Polyline>();
    @BindView(R.id.spinner) Spinner mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //changeStatusBarColor();
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setStatusBarTranslucent(true);
        setContentView(R.layout.map_monthly);
        ButterKnife.bind(this);
        intent = getIntent();
        strID = intent.getStringExtra("p_id");
        Log.d("IDClickafter", strID);
        mSpinner.setOnItemSelectedListener(this);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);
       /* pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading");
        pDialog.setCancelable(false);*/
        loadJSON("-");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d("ONCREATE", "MENU");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menumap, menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu)
    {
        if((featureId & Window.FEATURE_ACTION_BAR) == Window.FEATURE_ACTION_BAR && menu != null){
            if(menu.getClass().getSimpleName().equals("MenuBuilder")){
                try{
                    Method m = menu.getClass().getDeclaredMethod(
                            "setOptionalIconsVisible", Boolean.TYPE);
                    m.setAccessible(true);
                    m.invoke(menu, true);
                }
                catch(NoSuchMethodException e){
                    Log.e("TAG", "onMenuOpened", e);
                }
                catch(Exception e){
                    throw new RuntimeException(e);
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        mMap = map;
        mUiSettings = mMap.getUiSettings();
        //updateMapType();
        mMap.setOnMyLocationButtonClickListener(this);
        enableMyLocation();
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setZoomControlsEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
    }

    /*private void changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }*/
    private void loadJSON(String strTanggal){
        //showpDialog();
        OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.readTimeout(60, TimeUnit.SECONDS);
        client.connectTimeout(60, TimeUnit.SECONDS);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL)
                .client(client.build())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        MapInterface request = retrofit.create(MapInterface.class);

        Call<JSONResponse> call = request.getJSON(strID, strTanggal);
        call.enqueue(new Callback<JSONResponse>() {
            @Override
            public void onResponse(Call<JSONResponse> call, Response<JSONResponse> response) {
                JSONResponse jsonResponse = (JSONResponse) response.body();
                data = new ArrayList<>(Arrays.asList(jsonResponse.getRoute()));
                ArrayList<AndroidVersion> datamarker = new ArrayList<>(Arrays.asList(jsonResponse.getMarker()));
                ArrayList<AndroidVersion> datetime = new ArrayList<>(Arrays.asList(jsonResponse.getDates()));
                Log.d("datetime", String.valueOf(datetime.size()));
                Log.d("jumlahArrayperID", String.valueOf(data.size()));

                final List<String> list1 = new ArrayList<String>();

                for (int i = 0; i < datetime.size(); i++) {
                    list1.add(datetime.get(i).getTanggal());
                }
                //hidepDialog();
                ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(MapTaxi.this,android.R.layout.simple_spinner_item, list1);
                spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
                mSpinner.setPrompt("Select Date");
                mSpinner.setAdapter(spinnerArrayAdapter);
                mSpinner.setSelection(intSelected, false);
                mSpinner.setOnTouchListener(new AdapterView.OnTouchListener(){
                    @Override
                    public boolean onTouch(View v, MotionEvent event){
                        bolSpinnerTouched = true;
                        return false;
                    }
                });
                mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        intSelected = position;

                        if (bolSpinnerTouched) {
                            String strPengirim = list1.get(position);
                            Log.d("strPengirim", strPengirim);
                            loadJSON(strPengirim);
                        }
                        bolSpinnerTouched = false;
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                mMap.clear();
                for (Polyline line : polylines)
                {
                    line.remove();
                }
                polylines.clear();

                Double lt_east = 0.0;
                Double lt_west = 0.0;
                Double lg_north = 0.0;
                Double lg_south = 0.0;
                Double lt_temp = 0.0;
                Double lg_temp = 0.0;
                LatLng start = new LatLng(0, 0);
                LatLng finish = new LatLng(0, 0);

                if (data.size() == 1)
                {
                    Double lt_awal = data.get(0).getLt_awal();
                    Double lg_awal = data.get(0).getLg_awal();

                    Log.d("LT AWAL", String.valueOf(lt_awal));
                    Log.d("LG AWAL", String.valueOf(lg_awal));

                    if (lt_awal != null || lg_awal != null)
                        start = new LatLng(lt_awal, lg_awal);
                    else
                        start = null;
                    Toast.makeText(getBaseContext(), "Taxi is not active",
                            Toast.LENGTH_SHORT).show();
                }
                else
                {
                    for (int i = 0; i < data.size(); i++) {
                        Double lt_awal = data.get(i).getLt_awal();
                        Double lg_awal = data.get(i).getLg_awal();
                        Double lt_akhir = data.get(i).getLt_akhir();
                        Double lg_akhir = data.get(i).getLg_akhir();
                        DecimalFormat df = new DecimalFormat("#.##");
                        df.setRoundingMode(RoundingMode.CEILING);

                        if (i == 0) {
                            lt_east = lt_awal;
                            lt_west = lt_awal;
                            lg_north = lg_awal;
                            lg_north = lg_awal;
                            lg_south = lg_awal;
                            start = new LatLng(lt_awal, lg_awal);
                        }
                        else if (i == data.size() - 1)
                        {
                            finish = new LatLng(lt_akhir, lg_akhir);
                        }
                        if (i < data.size() - 1) {
                            lt_temp = lt_awal;
                            lg_temp = lg_awal;
                        }
                        else
                        {
                            lt_temp = lt_akhir;
                            lg_temp = lg_akhir;
                        }
                        if (lt_east < lt_temp)
                        {
                            lt_east = lt_temp;
                        } else if (lt_west > lt_temp)
                        {
                            lt_west = lt_temp;
                        } else if (lg_north < lg_temp)
                        {
                            lg_north = lg_temp;
                        } else if (lg_south > lg_temp)
                        {
                            lg_south = lg_temp;
                        }

                        int ColorLine = Color.GREEN;
                        double kec = data.get(i).getKecepatan();
                        if (kec <=40) {
                            ColorLine = Color.GREEN;
                        }
                        if (kec >= 41 && kec <= 60) {
                            ColorLine = Color.YELLOW;
                        }
                        if (kec > 60) {
                            ColorLine = Color.RED;
                        }

                        polylines.add(mMap.addPolyline(new PolylineOptions()
                                .add(new LatLng(lt_awal, lg_awal), new LatLng(lt_akhir, lg_akhir))
                                .color(ColorLine)
                                .geodesic(true)
                                .width(5)));
                    }
                }

                if (data.size() == 1) {
                    if (start != null) {
                        mMap.addMarker(new MarkerOptions()
                                .position(start)
                                .title("TAXI")
                                .snippet("is not active")
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).draggable(false));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(start, 10), new GoogleMap.CancelableCallback() {
                            @Override
                            public void onFinish() {
                                CameraUpdate cu_scroll = CameraUpdateFactory.scrollBy(0, 0);
                                mMap.animateCamera(cu_scroll);
                            }
                            @Override
                            public void onCancel() {
                            }
                        });
                    }
                }
                else if (data.size() > 1) {
                    mMap.addMarker(new MarkerOptions()
                            .position(start)
                            .title("START")
                            .snippet("taxi move")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).draggable(false));
                    mMap.addMarker(new MarkerOptions()
                            .position(finish)
                            .title("FINISH")
                            .snippet("taxi stop")
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)).draggable(false));

                    LatLng southWest = new LatLng(lt_west, lg_south);
                    LatLng northEast = new LatLng(lt_east, lg_north);
                    LatLngBounds bounds = new LatLngBounds(southWest, northEast);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(bounds.getCenter(),10), new GoogleMap.CancelableCallback() {
                        @Override
                        public void onFinish() {
                            CameraUpdate cu_scroll = CameraUpdateFactory.scrollBy(0, 0);
                            mMap.animateCamera(cu_scroll);
                        }
                        @Override
                        public void onCancel() {
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<JSONResponse> call, Throwable t) {
                Log.d("Error",t.getMessage());
            }
        });
    }
//    private void showpDialog() {
//        if (!pDialog.isShowing())
//            pDialog.show();
//    }
//
//    private void hidepDialog() {
//        if (pDialog.isShowing())
//            pDialog.dismiss();
//    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        strID = intent.getStringExtra("p_id");
        switch (item.getItemId()) {
            case R.id.map_normal:
                mMap.setMapType(MAP_TYPE_NORMAL);
                return true;
            case R.id.map_hybrid:
                mMap.setMapType(MAP_TYPE_HYBRID);
                return true;
            case R.id.map_satellite:
                mMap.setMapType(MAP_TYPE_SATELLITE);
                return true;
            case R.id.map_terrain:
                mMap.setMapType(MAP_TYPE_TERRAIN);
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else {
                // Show rationale and request permission.
            }
        }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "My Location", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

   @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            mPermissionDenied = true;
        }
    }

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }
    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void profileDriver(MenuItem item) {
        intent = new Intent(MapTaxi.this, Profile.class);
        intent.putExtra("p_id", strID);
        startActivity(intent);
    }
}
