/*
 * Copyright (C) 2015 Google, Inc.
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

import android.graphics.drawable.Drawable;
import android.view.View;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.mediation.NativeContentAdMapper;
import com.mopub.nativeads.StaticNativeAd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MoPubNativeContentAdMapper extends NativeContentAdMapper {

    private StaticNativeAd mMopubNativeAdData;

    public MoPubNativeContentAdMapper(StaticNativeAd ad, HashMap<String, Drawable> drawableMap) {

        mMopubNativeAdData = ad;
        setHeadline(mMopubNativeAdData.getTitle());
        setBody(mMopubNativeAdData.getText());
        setCallToAction(mMopubNativeAdData.getCallToAction());

        if (drawableMap!=null) {
            setLogo(new MoPubNativeMappedImage(drawableMap.get(DownloadDrawablesAsync.KEY_ICON), mMopubNativeAdData.getIconImageUrl(), MoPubAdapter.DEFAULT_MOPUB_IMAGE_SCALE));

            List<NativeAd.Image> imagesList = new ArrayList<NativeAd.Image>();
            imagesList.add(new MoPubNativeMappedImage(drawableMap.get(DownloadDrawablesAsync.KEY_IMAGE), mMopubNativeAdData.getMainImageUrl(), MoPubAdapter.DEFAULT_MOPUB_IMAGE_SCALE));
            setImages(imagesList);
        }

        setOverrideClickHandling(false);
        setOverrideImpressionRecording(true);
    }


    public void trackView(View view) {

        mMopubNativeAdData.prepare(view);
    }

    @Override
    public void recordImpression() {

    }

    @Override
    public void handleClick(View view) {

    }
}
