 package com.bluefox.kenyapowerbill;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdLoader;
import com.google.android.gms.ads.AdRequest;
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
        tariff = view.findViewById(R.id.prepaid_tariff);
        mEtAmount =view.findViewById(R.id.etAmount);
        mBttnCalculate = view.findViewById(R.id.btnCalculate);
        refreshAd(view);

        mBttnCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshAd(view);
                int position = tariff.getSelectedItemPosition();
                if(!mEtAmount.getText().toString().isEmpty()){
                    int amount = Integer.parseInt(mEtAmount.getText().toString());
                    if(amount >249){
                        Log.i("value for spinner","position "+position);
                        String url = "https://3e7dfb7a70e4.ngrok.io/prepaid";
                        String postBody = "{\"amount\":" + amount + ",\"tariff\":\"" + position + "\"}\n";
                        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
                        RequestBody body = RequestBody.create(JSON, postBody);
                        Request request = new Request.Builder()
                                .url(url)
                                .build();
                        OkHttpClient client = new OkHttpClient();
                        client.setConnectTimeout(50, TimeUnit.SECONDS);
                        client.setReadTimeout(10,TimeUnit.MINUTES);
                        client.newCall(request).enqueue(new Callback() {
                            @Override
                            public void onFailure(Request request, IOException e) {
                                e.printStackTrace();
//                                Toast.makeText(getContext(), "An error occurred, try again", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onResponse(Response response) throws IOException {
                                if(!response.isSuccessful()){
                                    throw new IOException("Unexpected Code "+ response);
                                }else {
                                    String jsondata = response.body().string();
                                    Log.i("response from server", " "+jsondata);
                                    Intent intent = new Intent(getActivity(),ResultsActivity.class);
                                    intent.putExtra("jsondata", jsondata);
                                    startActivity(intent);


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

    private void refreshAd(View view) {
        AdLoader.Builder builder = new AdLoader.Builder(getContext(), getString(R.string.native_ad_unit_id));
        builder.forUnifiedNativeAd(new UnifiedNativeAd.OnUnifiedNativeAdLoadedListener() {
            @Override
            public void onUnifiedNativeAdLoaded(UnifiedNativeAd unifiedNativeAd) {
                if (nativeAd != null)
                    nativeAd = unifiedNativeAd;
                CardView cardView = view.findViewById(R.id.ad_cardview);

                UnifiedNativeAdView adView = (UnifiedNativeAdView) getLayoutInflater().inflate(R.layout.native_ad_layout, null);

                populateNativeAd(unifiedNativeAd, adView);
                cardView.removeAllViews();
                cardView.addView(adView);

            }

            private void populateNativeAd(UnifiedNativeAd nativeAd, UnifiedNativeAdView adView) {
                adView.setHeadlineView(view.findViewById(R.id.ad_headline));
                adView.setAdvertiserView(view.findViewById(R.id.ad_advertiser));
                adView.setBodyView(view.findViewById(R.id.ad_body_text));
                adView.setStarRatingView(view.findViewById(R.id.star_rating));
                adView.setMediaView((MediaView) adView.findViewById(R.id.media_view));
                adView.setCallToActionView(view.findViewById(R.id.ad_call_to_action));
                adView.setIconView(view.findViewById(R.id.adv_icon));

                adView.getMediaView().setMediaContent(nativeAd.getMediaContent());
                ((TextView)adView.getHeadlineView()).setText(nativeAd.getHeadline());

                if (nativeAd.getBody() == null) {
                    adView.getBodyView().setVisibility(View.INVISIBLE);
                } else {
                    ((TextView) adView.getBodyView()).setText(nativeAd.getBody());
                    adView.getBodyView().setVisibility(View.VISIBLE);

                }

                if (nativeAd.getAdvertiser() == null) {
                    adView.getAdvertiserView().setVisibility(View.INVISIBLE);
                } else {
                    ((TextView) adView.getAdvertiserView()).setText(nativeAd.getAdvertiser());
                    adView.getAdvertiserView().setVisibility(View.VISIBLE);
                }
                if (nativeAd.getStarRating() == null) {
                    adView.getStarRatingView().setVisibility(View.INVISIBLE);
                } else {
                    ((RatingBar) adView.getStarRatingView()).setRating(nativeAd.getStarRating().floatValue());
                    adView.getStarRatingView().setVisibility(View.VISIBLE);
                }

                if (nativeAd.getIcon() == null) {
                    adView.getIconView().setVisibility(View.GONE);
                } else {
                    ((ImageView) adView.getIconView()).setImageDrawable(nativeAd.getIcon().getDrawable());
                    adView.getIconView().setVisibility(View.VISIBLE);
                }
                if (nativeAd.getCallToAction() == null) {
                    adView.getCallToActionView().setVisibility(View.INVISIBLE);
                } else {
                    adView.getCallToActionView().setVisibility(View.VISIBLE);
                    ((Button) adView.getCallToActionView()).setText(nativeAd.getCallToAction());
                }

                adView.setNativeAd(nativeAd);
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
}