
package com.mopub.mobileads.dfp.adapters;

import android.graphics.drawable.Drawable;
import android.view.View;

import com.google.android.gms.ads.formats.NativeAd;
import com.google.android.gms.ads.mediation.NativeAppInstallAdMapper;
import com.mopub.nativeads.StaticNativeAd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class MoPubNativeAppInstallAdMapper extends NativeAppInstallAdMapper {

    private StaticNativeAd mMopubNativeAdData;

    public MoPubNativeAppInstallAdMapper(StaticNativeAd ad, HashMap<String, Drawable> drawableMap) {
        mMopubNativeAdData = ad;
        setHeadline(mMopubNativeAdData.getTitle());
        setBody(mMopubNativeAdData.getText());
        setCallToAction(mMopubNativeAdData.getCallToAction());

        if (drawableMap!=null) {
            setIcon(new MoPubNativeMappedImage(drawableMap.get(DownloadDrawablesAsync.KEY_ICON),
                    mMopubNativeAdData.getIconImageUrl(), MoPubAdapter.DEFAULT_MOPUB_IMAGE_SCALE));

            List<NativeAd.Image> imagesList = new ArrayList<NativeAd.Image>();
            imagesList.add(new MoPubNativeMappedImage(drawableMap.get(DownloadDrawablesAsync.KEY_IMAGE),
                    mMopubNativeAdData.getMainImageUrl(), MoPubAdapter.DEFAULT_MOPUB_IMAGE_SCALE));
            setImages(imagesList);
        }else {
            // If the publisher request for URLs only, the Images list and Icon image assets should
            // still be mapped but with image URLs only. - sbagadi@
            //That would be drawable with only URI right as below? Can you confirm? @rupa
            setIcon(new MoPubNativeMappedImage(null,
                    mMopubNativeAdData.getIconImageUrl(), MoPubAdapter.DEFAULT_MOPUB_IMAGE_SCALE));

            List<NativeAd.Image> imagesList = new ArrayList<NativeAd.Image>();
            imagesList.add(new MoPubNativeMappedImage(null,
                    mMopubNativeAdData.getMainImageUrl(), MoPubAdapter.DEFAULT_MOPUB_IMAGE_SCALE));
            setImages(imagesList);
        }

        setOverrideClickHandling(false);
        setOverrideImpressionRecording(false);
    }

    //untrackview is added - rupa
    @Override
    public void untrackView(View view) {
        super.untrackView(view);
        mMopubNativeAdData.clear(view);
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
