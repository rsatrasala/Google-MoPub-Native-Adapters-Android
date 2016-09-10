/*
 * Copyright (C) 2014 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mopub.mobileads.dfp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.formats.NativeAdOptions;
import com.google.android.gms.ads.mediation.MediationNativeAdapter;
import com.google.android.gms.ads.mediation.MediationNativeListener;
import com.google.android.gms.ads.mediation.NativeMediationAdRequest;
import com.mopub.nativeads.BaseNativeAd;
import com.mopub.nativeads.MoPubNative;
import com.mopub.nativeads.MoPubStaticNativeAdRenderer;
import com.mopub.nativeads.NativeAd;
import com.mopub.nativeads.NativeErrorCode;
import com.mopub.nativeads.RequestParameters;
import com.mopub.nativeads.StaticNativeAd;
import com.mopub.nativeads.ViewBinder;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * A mediation adapter for the Sample ad network. This class can be used as a reference to help
 * other ad networks build their own mediation adapter.
 * <p/>
 * NOTE: The audience for this sample is mediation ad networks who are trying to build an ad network
 * adapter, not an app developer trying to integrate Google Mobile Ads into their application.
 */
public class MoPubAdapter implements MediationNativeAdapter {


    public static final double DEFAULT_MOPUB_IMAGE_SCALE = 1;
    private static final String MOPUB_AD_UNIT_KEY = "ad_unit";
    private MoPubAdapter selfAdapter;

    @Override
    public void onDestroy() {

    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void requestNativeAd(Context context,
                                final MediationNativeListener listener,
                                Bundle serverParameters,
                                NativeMediationAdRequest mediationAdRequest,
                                Bundle mediationExtras) {
        /*
         * In this method, you should:
         *
         * 1. Create a SampleNativeAdLoader
         * 2. Set the native ad listener
         * 3. Set native ad options (optional assets) 
         * 4. Make an ad request.
         */

        String adunit = serverParameters.getString(MOPUB_AD_UNIT_KEY); // "28839b7b0d0b43a6827729cc50705a31"; //
        final NativeAdOptions options = mediationAdRequest.getNativeAdOptions();

        if(mediationAdRequest.isAppInstallAdRequested() || mediationAdRequest.isContentAdRequested()){
            listener.onAdFailedToLoad(MoPubAdapter.this, AdRequest.ERROR_CODE_INVALID_REQUEST);
        }

        MoPubNative.MoPubNativeNetworkListener moPubNativeNetworkListener = new MoPubNative.MoPubNativeNetworkListener() {

            @Override
            public void onNativeLoad(NativeAd nativeAd) {
                BaseNativeAd adData = nativeAd.getBaseNativeAd();
                if (adData instanceof StaticNativeAd) {
                    final StaticNativeAd staticNativeAd = (StaticNativeAd) adData;

                    //commented for testing as the sample app required Images to be downloaded
                   /* if(options!=null && options.shouldReturnUrlsForImageAssets()){
                        final MoPubNativeAppInstallAdMapper moPubNativeAppInstallAdMapper = new MoPubNativeAppInstallAdMapper(staticNativeAd, null);
                        listener.onAdLoadMed(MoPubAdapter.this, moPubNativeAppInstallAdMapper);
                    }*/

                    HashMap <String, URL>  map  =   new HashMap <String, URL>();
                    try {
                        map.put(DownloadDrawablesAsync.KEY_ICON, new URL(staticNativeAd.getIconImageUrl()));
                        map.put(DownloadDrawablesAsync.KEY_IMAGE, new URL(staticNativeAd.getMainImageUrl()));

                    } catch (MalformedURLException e) {
                        Log.d("MoPub", "Invalid ad response received from MoPub. Image URLs are invalid");
                    }

                    new DownloadDrawablesAsync(new DrawableDownloadListener() {
                        @Override
                        public void onDownloadSuccess(HashMap<String, Drawable> drawableMap) {

                            final MoPubNativeAppInstallAdMapper moPubNativeAppInstallAdMapper = new MoPubNativeAppInstallAdMapper(staticNativeAd, drawableMap);
                            listener.onAdLoaded(MoPubAdapter.this, moPubNativeAppInstallAdMapper);
                        }

                        @Override
                        public void onDownloadFailure() {
                            // Failed to download images, send failure callback.
                            listener.onAdFailedToLoad(MoPubAdapter.this, AdRequest.ERROR_CODE_INTERNAL_ERROR);
                        }
                    }).execute(map);
                }
            }

            @Override
            public void onNativeFail(NativeErrorCode errorCode) {
                switch (errorCode){
                    case EMPTY_AD_RESPONSE:
                        listener.onAdFailedToLoad(MoPubAdapter.this, AdRequest.ERROR_CODE_NO_FILL);
                        break;
                    case INVALID_REQUEST_URL:
                        listener.onAdFailedToLoad(MoPubAdapter.this, AdRequest.ERROR_CODE_INVALID_REQUEST);
                        break;
                    case CONNECTION_ERROR:
                        listener.onAdFailedToLoad(MoPubAdapter.this, AdRequest.ERROR_CODE_INVALID_REQUEST);
                        break;
                    case UNSPECIFIED:
                        listener.onAdFailedToLoad(MoPubAdapter.this, AdRequest.ERROR_CODE_INTERNAL_ERROR);
                        break;
                    default:
                        listener.onAdFailedToLoad(MoPubAdapter.this, AdRequest.ERROR_CODE_INTERNAL_ERROR);
                        break;
                }

            }
        };

        if(adunit==null) {
            Log.d("MoPub", "Adunit id is invalid. So failing the request.");
            listener.onAdFailedToLoad(MoPubAdapter.this, AdRequest.ERROR_CODE_INVALID_REQUEST);
        }

        MoPubNative moPubNative = new MoPubNative(context, adunit, moPubNativeNetworkListener);

        ViewBinder viewbinder = new ViewBinder.Builder(0).build();
        MoPubStaticNativeAdRenderer moPubStaticNativeAdRenderer = new MoPubStaticNativeAdRenderer(viewbinder);
        moPubNative.registerAdRenderer(moPubStaticNativeAdRenderer);

        RequestParameters requestParameters = new RequestParameters.Builder()
                .keywords("gender:" + mediationAdRequest.getGender() + ",age:" + mediationAdRequest.getBirthday())
                .location(mediationAdRequest.getLocation())
                .build();

            moPubNative.makeRequest(requestParameters);

    }

}

