package com.nabil.radeon.trajectgps.api_interface;

import com.nabil.radeon.trajectgps.JSONResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface RequestInterface {

    @GET("person/show.php")
    Call<JSONResponse> getShow();

    @GET("person/connect3.php")
    Call<JSONResponse> getMap(@Query("id") String p_id, @Query("tanggal") String tanggal);


}
