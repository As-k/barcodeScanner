package com.cioc.barcodescanner;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.client.CookieStore;
import cz.msebera.android.httpclient.cookie.Cookie;


public class MainActivity extends AppCompatActivity {

    private CookieStore httpCookieStore;
    private AsyncHttpClient client = new AsyncHttpClient();

    private static String serverURL;
    long ids = 65465765;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setPrompt("Scan");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();
    }

    void showModal(final int pk, String scanContent, final int inStock){
        if (pk != 0) {

            TextView productName, currentStock;
            final EditText quantity;
            Button inScan, updateScan, outScan;
            View v = getLayoutInflater().inflate(R.layout.dialog_scan_style, null, false);
            productName = v.findViewById(R.id.productName);
            currentStock = v.findViewById(R.id.current_stock);
            quantity = v.findViewById(R.id.quantity);
            inScan = v.findViewById(R.id.inScan);
            updateScan = v.findViewById(R.id.updateScan);
            outScan = v.findViewById(R.id.outScan) ;
            productName.setText(scanContent);
            currentStock.setText("Current Stock :" + inStock);
            quantity.setText("1");

            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setView(v);
            adb.setCancelable(false);
            final AlertDialog ad = adb.create();

            inScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ad.dismiss();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                }
            });

            updateScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = quantity.getText().toString().trim();
                    Integer i = Integer.parseInt(s);
//                    final int stock = inStock - i;
//                    Toast.makeText(MainActivity.this, "" + stock, Toast.LENGTH_SHORT).show();

                    RequestParams params = new RequestParams();
                    params.put("inStock", i);
                    serverURL = "http://192.168.43.9:8000/api/POS/product/" + pk + '/';

                    client.patch(serverURL , params , new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            try {
                                JSONObject usrObj = response.getJSONObject(0);
//                                String name = usrObj.getString("name");
//                                Integer pk = usrObj.getInt("pk");
                                Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();

//                                final String scanContent = result.getContents();
//                                showModal(pk, name,inStock);

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onFinish() {
                            System.out.println("finished 0101");
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            System.out.println("finished failed 001001");
                        }
                    });
                    ad.dismiss();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                }
            });

            outScan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String s = quantity.getText().toString().trim();
                    Integer i = Integer.parseInt(s);
                    final int stock = inStock - i;
                    Toast.makeText(MainActivity.this, "" + stock, Toast.LENGTH_SHORT).show();

                    RequestParams params = new RequestParams();
                    params.put("inStock", stock);
                    serverURL = "http://192.168.43.9:8000/api/POS/product/" + pk + '/';

                    client.patch(serverURL , params , new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            try {
                                JSONObject usrObj = response.getJSONObject(0);
//                                String name = usrObj.getString("name");
//                                Integer pk = usrObj.getInt("pk");
                                Toast.makeText(MainActivity.this, "Updated", Toast.LENGTH_SHORT).show();

//                                final String scanContent = result.getContents();
//                                showModal(pk, name,inStock);

                            } catch (JSONException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        @Override
                        public void onFinish() {
                            System.out.println("finished 0101");
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                            // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                            System.out.println("finished failed 001001");
                        }
                    });
                    ad.dismiss();
                    startActivity(new Intent(MainActivity.this, MainActivity.class));
                    finish();
                }
            });

            ad.show();
//                Toast.makeText(MainActivity.this, ""+scanContent, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        final IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(MainActivity.this, "You cancelled the sanning", Toast.LENGTH_LONG).show();

            } else {

                serverURL = "http://192.168.43.9:8000/api/POS/product/?&search=" + ids;

                client.get(serverURL, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        try {
                            JSONObject usrObj = response.getJSONObject(0);
                            String name = usrObj.getString("name");
                            Integer pk = usrObj.getInt("pk");
                            Integer inStock = usrObj.getInt("inStock");

                            final String scanContent = result.getContents();
                            Toast.makeText(MainActivity.this, scanContent, Toast.LENGTH_SHORT).show();
                            showModal(pk, name, inStock);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public void onFinish() {
                        System.out.println("finished 001");
                        Log.e("============>>>pk","======finished 001");
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable e, JSONObject errorResponse) {
                        // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                        System.out.println("finished failed 001");
                        Log.e("============>>>pk","=====>>="+statusCode);
                    }
                });

            }
        }else  Toast.makeText(getApplicationContext(),"No scan data received!", Toast.LENGTH_SHORT).show();
    }
}