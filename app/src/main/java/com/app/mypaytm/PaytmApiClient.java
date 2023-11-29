package com.app.mypaytm;

import android.util.Log;

import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PaytmApiClient {
    private static final String BASE_URL = "https://securegw.paytm.in/theia/";

    private static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    private static PaytmService paytmService = retrofit.create(PaytmService.class);

    public static void generateChecksum(
            String mid,
            String orderId,
            String custId,
            String channelId,
            String txnAmount,
            String website,
            String callbackUrl,
            String industryTypeId,
            Callback<JsonObject> callback) {

        Call<JsonObject> call = paytmService.generateChecksum(
                mid,
                orderId,
                custId,
                channelId,
                txnAmount,
                website,
                callbackUrl,
                industryTypeId
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonObject jsonObject = response.body();
                    if (jsonObject.has("CHECKSUMHASH")) {
                        String CHECKSUMHASH = jsonObject.get("CHECKSUMHASH").getAsString();
                        Log.e("CheckSum result >>", CHECKSUMHASH);
                        // Continue with your payment transaction
                        callback.onResponse(call, response);
                    } else {
                        Log.e("CheckSum result >>", "CHECKSUMHASH not found in JsonObject");
                        callback.onFailure(call, new Throwable("CHECKSUMHASH not found in JsonObject"));
                    }
                } else {
                    Log.e("CheckSum result >>", "Response not successful");
                    callback.onFailure(call, new Throwable("Response not successful"));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e("CheckSum result >>", "Network request failed", t);
                callback.onFailure(call, t);
            }
        });
    }
}
