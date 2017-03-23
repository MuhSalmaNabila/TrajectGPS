package com.nabil.radeon.trajectgps.api_interface;

import com.nabil.radeon.trajectgps.JSONResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RequestInterface {
    @GET("person/show.php")
    Call<JSONResponse> getJSON();

}
