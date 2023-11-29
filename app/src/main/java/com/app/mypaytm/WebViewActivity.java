package com.app.mypaytm;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import androidx.annotation.Nullable;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {
    WebView web_view;
    private String mid, orderId, custid, stramount, varifyurl, CHECKSUMHASH;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_xml);
        Intent intent = getIntent();
        orderId = intent.getExtras().getString("orderid");
        custid = intent.getExtras().getString("custid");
        stramount = intent.getExtras().getString("amount");

        mid = "Qsl6aSZgi0!wT@n0"; /// your marchant id bumppy
        varifyurl = //"https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
                "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID"+orderId;

        web_view = findViewById(R.id.web_view); // Replace with your WebView ID
        String urlToLoad = "https://securegw.paytm.in/theia/processTransaction"+"MID="+mid+
                "&ORDER_ID=" + orderId+
                "&CUST_ID="+custid+
                "&CHANNEL_ID=WAP&TXN_AMOUNT="+stramount+"&WEBSITE=WEBSTAGING"+
                "&CALLBACK_URL="+ varifyurl+"&INDUSTRY_TYPE_ID=Retail";


        web_view.setWebViewClient(new WebViewClient());

        web_view.getSettings().setJavaScriptEnabled(true);


        web_view.setVerticalScrollBarEnabled(false);

        web_view.setHorizontalScrollBarEnabled(false);

        web_view.loadUrl(urlToLoad);


    }


}