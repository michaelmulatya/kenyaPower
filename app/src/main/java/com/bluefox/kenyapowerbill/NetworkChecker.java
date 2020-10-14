package com.bluefox.kenyapowerbill;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;

public class NetworkChecker extends BroadcastReceiver {

    public static boolean internet_status = false;
    private CardView view;

    public NetworkChecker(CardView view) {
        this.view = view;

    }

    public NetworkChecker() {
    }

    public static boolean checkInternetConenction(Context context) {
        internet_status = false;
        ConnectivityManager check = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (check != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities networkCapabilitieswork = check.getNetworkCapabilities(check.getActiveNetwork());
                if (networkCapabilitieswork != null) {
                    if (networkCapabilitieswork.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                            (networkCapabilitieswork.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR))
                            || (networkCapabilitieswork.hasTransport(NetworkCapabilities.TRANSPORT_VPN))) {
                        return true;
                    }

                }

            } else {
                final NetworkInfo ni = check.getActiveNetworkInfo();
                if (ni != null) {
                    return (ni.isConnected() && (ni.getType() == ConnectivityManager.TYPE_WIFI || ni.getType() == ConnectivityManager.TYPE_MOBILE));

                }

            }


        }
        return false;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
//        View prepaid_view = View.inflate(context,R.layout.fragment_pre_paid,null);
//        View postpaid_view = View.inflate(context,R.layout.fragment_post_paid,null);
//        CardView precardView = view.findViewById(R.id.ad_cardview);
//        CardView postcardView = view.findViewById(R.id.ad_cardview2);
        if(checkInternetConenction(context)){
//            postcardView.setVisibility(View.VISIBLE);
//            Toast.makeText(context, "Internet connection!", Toast.LENGTH_LONG).show();

        }else {
//            Toast.makeText(context, "No Internet connection!", Toast.LENGTH_LONG).show();

        }





    }

}

