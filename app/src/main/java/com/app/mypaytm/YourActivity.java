package com.app.mypaytm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YourActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    private ProgressDialog dialog;
    private String mid, orderId, custid, stramount, varifyurl, CHECKSUMHASH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize your variables (mid, orderId, custid, stramount, varifyurl)

        Intent intent = getIntent();
        orderId = intent.getExtras().getString("orderid");
        custid = intent.getExtras().getString("custid");
        stramount = intent.getExtras().getString("amount");

        mid = "Qsl6aSZgi0!wT@n0"; /// your marchant id bumppy
        varifyurl = //"https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
                "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID"+orderId;


        dialog = new ProgressDialog(this);
        dialog.setMessage("Please wait");

        new GenerateChecksumTask().execute();
    }

    private class GenerateChecksumTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            dialog.show();
        }

        @Override
        protected String doInBackground(Void... voids) {
            PaytmApiClient paytmApiClient = new PaytmApiClient();

            paytmApiClient.generateChecksum(mid, orderId, custid, "WAP", stramount, "WEBSTAGING", varifyurl, "Retail",
                    new Callback<JsonObject>() {
                        @Override
                        public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                JsonObject jsonObject = response.body();
                                if (jsonObject.has("CHECKSUMHASH")) {
                                    CHECKSUMHASH = jsonObject.get("CHECKSUMHASH").getAsString();
                                    Log.e("CheckSum result >>", CHECKSUMHASH);
                                    // Continue with your payment transaction
                                } else {
                                    Log.e("CheckSum result >>", "CHECKSUMHASH not found in JsonObject");
                                }
                            } else {
                                Log.e("CheckSum result >> ", "Response not successful");
                            }
                        }

                        @Override
                        public void onFailure(Call<JsonObject> call, Throwable t) {
                            Log.e("CheckSum result >>", "Network request failed", t);
                        }
                    });

            return CHECKSUMHASH;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            PaytmPGService Service = PaytmPGService.getStagingService(varifyurl);
            // when the app is ready to publish, use the production service
            // PaytmPGService  Service = PaytmPGService.getProductionService();

            // now call paytm service here
            // below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
            HashMap<String, String> paramMap = new HashMap<String, String>();
            // these are mandatory parameters
            paramMap.put("MID", mid); // MID provided by paytm
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", custid);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", stramount);
            paramMap.put("WEBSITE", "WEBSTAGING");
            paramMap.put("CALLBACK_URL" ,varifyurl);
            // paramMap.put( "EMAIL" , "abc@gmail.com");   // no need
            // paramMap.put( "MOBILE_NO" , "9144040888");  // no need
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            // paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");

            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order, null);
            // start the payment service call here
            Service.startPaymentTransaction(YourActivity.this, true, true, YourActivity.this);
        }
    }




    @Override
    public void onTransactionResponse(Bundle bundle) {
        Log.e("checksum ", " respon true " + bundle.toString());
    }

    @Override
    public void networkNotAvailable() {

    }

    @Override
    public void onErrorProceed(String s) {

    }

    @Override
    public void clientAuthenticationFailed(String s) {

    }

    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("checksum ", " ui fail respon  "+ s );
    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true "+ s + "  s1 " + s1);
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Log.e("checksum ", " cancel call back respon  " );
    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("checksum ", "  transaction cancel " );
    }


}

