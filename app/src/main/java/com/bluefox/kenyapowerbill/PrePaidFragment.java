 package com.bluefox.kenyapowerbill;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.formats.MediaView;
import com.google.android.gms.ads.formats.UnifiedNativeAd;
import com.google.android.gms.ads.formats.UnifiedNativeAdView;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

 /**
 * A simple {@link Fragment} subclass.
 * Use the {@link PrePaidFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PrePaidFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Spinner tariff;
    private EditText mEtAmount;
    private Button mBttnCalculate;
    private UnifiedNativeAd nativeAd;
    private ProgressDialog progressDialog;
     private CardView cardView;
     private NetworkChecker networkChecker;
     private IntentFilter intentFilter;
     private InterstitialAd token_interstitialAd;


     public PrePaidFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PrePaidFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PrePaidFragment newInstance(String param1, String param2) {
        PrePaidFragment fragment = new PrePaidFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }




    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pre_paid, container, false);



        //populate the spinner with an array of strings
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        tariff = view.findViewById(R.id.prepaid_tariff);
        mEtAmount =view.findViewById(R.id.etAmount);
        mBttnCalculate = view.findViewById(R.id.btnCalculate);

        //initialise and load interstitial ads
        token_interstitialAd = new InterstitialAd(getActivity());
        token_interstitialAd.setAdUnitId(getResources().getString(R.string.token_interstitial_unit_id));
        token_interstitialAd.loadAd(new AdRequest.Builder().build());



        refreshAd(view);
        cardView = view.findViewById(R.id.ad_cardview);
        /// Register Broadcast receiver
        networkChecker = new NetworkChecker(){
            @Override
            public void onReceive(Context context, Intent intent) {
                if(NetworkChecker.checkInternetConenction(context)){
                    cardView.setVisibility(View.VISIBLE);
                    refreshAd(view);
                    token_interstitialAd.loadAd(new AdRequest.Builder().build());
                }else {
                    Toast.makeText(context, "No Internet connection!", Toast.LENGTH_LONG).show();

                }

            }
        };
        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
        getActivity().registerReceiver(networkChecker, intentFilter);



//https://www.youtube.com/watch?v=bb3vR-QvjKM progress dialog

        mBttnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = tariff.getSelectedItemPosition();
                if(!mEtAmount.getText().toString().isEmpty()){
                    int amount = Integer.parseInt(mEtAmount.getText().toString());
                    if(amount >249){
                        progressDialog = new ProgressDialog(getActivity());
                        progressDialog.show();
                        progressDialog.setContentView(R.layout.progress_dialog);
                        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                        Log.i("value for spinner","position "+position);
                        String url = "https://stima-app.herokuapp.com/prepaid";
                        String postBody = "{\"amount\":" + amount + ",\"tariff\":\"" + position + "\"}\n";
                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        RequestBody body = RequestBody.create(JSON, postBody);
                        Request request = new Request.Builder()
                                .url(url)
                                .post(body)
                                .build();
                        OkHttpClient client = new OkHttpClient();
                        client.setConnectTimeout(50, TimeUnit.SECONDS);
                        client.setReadTimeout(10,TimeUnit.MINUTES);
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                if(token_interstitialAd.isLoaded()){
                                    token_interstitialAd.show();

                                }
//                                Toast.makeText(getContext(), "An error occurred, try again", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                if(!response.isSuccessful()){
                                    throw new IOException("Unexpected Code "+ response);
                                }else {
                                    progressDialog.dismiss();
                                    String jsondata = response.body().string();

                                    token_interstitialAd.setAdListener(new AdListener(){
                                        @Override
                                        public void onAdClosed() {
                                            Log.i("response from server", " "+jsondata);
                                            Intent intent = new Intent(getActivity(),ResultsActivity.class);
                                            intent.putExtra("jsondata", jsondata);
                                            startActivity(intent);
                                            token_interstitialAd.loadAd(new AdRequest.Builder().build());
                                        }
                                    });
                                    if(token_interstitialAd.isLoaded()){
                                        token_interstitialAd.show();

                                    }else {
                                        Log.i("response from server", " "+jsondata);
                                        Intent intent = new Intent(getActivity(),ResultsActivity.class);
                                        intent.putExtra("jsondata", jsondata);
                                        startActivity(intent);

                                    }



                                }


                            }


                        });




                    }else {
                        mEtAmount.setError("Amount has to be 250 or above");
                    }



                }else{
                    mEtAmount.setError("Amount cannot be empty");
                }


            }

        });



        return view;
    }
     private boolean isNetworkAvailable() {
         ConnectivityManager connectivityManager
                 = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
         return activeNetworkInfo != null && activeNetworkInfo.isConnected();
     }

    private void refreshAd(View view) {

        AdLoader.Builder builder = new AdLoader.Builder(getActivity(), getString(R.string.token_native_ad_unit_id));
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {

                TemplateView templateView = view.findViewById(R.id.native_adView);
                templateView.setNativeAd(unifiedNativeAd);

            }


        });

        AdLoader adLoader = builder.withAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                Log.i("ad failed","native Ad failed");
                super.onAdClosed();
            }
        }).build();
        adLoader.loadAd(new AdRequest.Builder().build());
    }

    @Override
    public void onDestroy() {
        if (nativeAd!=null)
            nativeAd.destroy();
        super.onDestroy();
    }

     @Override
     public void onResume() {
         super.onResume();
         networkChecker = new NetworkChecker(){
             @Override
             public void onReceive(Context context, Intent intent) {
                 if(NetworkChecker.checkInternetConenction(context)){
                     cardView.setVisibility(View.VISIBLE);
                     refreshAd(getView());
                     token_interstitialAd.loadAd(new AdRequest.Builder().build());
                 }else {
                     Toast.makeText(context, "No Internet connection!", Toast.LENGTH_LONG).show();

                 }

             }
         };
         intentFilter = new IntentFilter();
         intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
         intentFilter.addAction("android.net.wifi.WIFI_STATE_CHANGED");
         getActivity().registerReceiver(networkChecker, intentFilter);
     }

     @Override
     public void onPause() {
         super.onPause();
         if(this.networkChecker != null){
             getActivity().unregisterReceiver(networkChecker);
         }
     }
 }