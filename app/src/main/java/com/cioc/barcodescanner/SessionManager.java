package com.cioc.barcodescanner;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by cioc on 28/3/18.
 */

public class SessionManager {
    Context context;

    SharedPreferences sp;
    SharedPreferences.Editor spe;
    private String status = "STATUS";

    public SessionManager(Context context){
        this.context = context;
        sp = context.getSharedPreferences("current_status",context.MODE_PRIVATE);
        spe = sp.edit();
    }

    public boolean getStatus(){
        return sp.getBoolean(status, false);
    }

    public void setStatus(boolean s){
        spe.putBoolean(status, s);
        spe.commit();
    }
}
