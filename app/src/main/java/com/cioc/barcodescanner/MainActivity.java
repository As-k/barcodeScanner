package com.cioc.barcodescanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class MainActivity extends Activity {


    TextView barcodeInfo;
    Button scanBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        barcodeInfo = (TextView) findViewById(R.id.txtContent);
        scanBar = findViewById(R.id.button);


        scanBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentIntegrator integrator = new IntentIntegrator(MainActivity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                integrator.initiateScan();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result!=null) {
            if (result.getContents() == null) {
                Toast.makeText(MainActivity.this, "You cancelled the sanning", Toast.LENGTH_LONG).show();

            } else {

                final String scanContent = result.getContents();
                Toast.makeText(MainActivity.this, ""+scanContent, Toast.LENGTH_SHORT).show();
                barcodeInfo.setText(scanContent);
            }
        }
    }
}