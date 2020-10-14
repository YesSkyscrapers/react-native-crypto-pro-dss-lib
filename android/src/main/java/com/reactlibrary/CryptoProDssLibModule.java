package com.reactlibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.digt.sdk.*;
import com.digt.sdk.auth.Auth;
import com.digt.sdk.auth.models.DssUser;
import com.digt.sdk.auth.models.RegisterInfo;
import com.digt.sdk.interfaces.SdkDssUserCallback;
import com.digt.sdk.interfaces.SdkInitCallback;
import com.digt.sdk.interfaces.SdkQrCallback;
import com.digt.sdk.utils.Constants;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.common.LifecycleState;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.Arrays;

class InitCallbackHandler implements SdkInitCallback {
    public void onInit(Constants.CSPInitCode var1) {
        System.out.println(var1);
    }
}

public class CryptoProDssLibModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;

    @SuppressLint("RestrictedApi")
    public CryptoProDssLibModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }



    @Override
    public String getName() {
        return "CryptoProDssLib";
    }



    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void onResumeActivity() {
        //CryptoProDss.getInstance().registerActivityContext(this.reactContext);
     }


    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void firstInitialization(Promise promise) {
        CryptoProDss.getInstance().init(((FragmentActivity)this.reactContext.getCurrentActivity()),new InitCallbackHandler(){
            @Override
            public void onInit(Constants.CSPInitCode var1) {
                promise.resolve((var1).getTitle());
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void initViaQr(String base64, Promise promise) {
        Log.i("nasvyzi", "here");
        try {
            String json = null;
                InputStream is = getCurrentActivity().getAssets().open("certs.json");
                int size = is.available();
                byte[] buffer = new byte[size];
                is.read(buffer);
                is.close();
                json = new String(buffer, "UTF-8");
            Log.i("nasvyzi", json);
        } catch (Exception e) {
            Log.i("nasvyzi", "errr");
            Log.i("nasvyzi", e.toString());
            e.printStackTrace();
        }
        DssUser dssUser = new DssUser();
        RegisterInfo registerInfo = new RegisterInfo(null, null);
        Auth auth = new Auth();
        auth.scanQr(this.reactContext.getCurrentActivity(), base64, new SdkQrCallback(){

            @Override
            public void onOperationSuccessful(@NonNull String s) {
                auth.kinit(getReactApplicationContext(), dssUser, registerInfo, Constants.KeyProtectionType.BIOMETRIC, null, null, new SdkDssUserCallback(){
                    @Override
                    public void onOperationSuccessful() {
                        promise.resolve("ok, since ok");
                    }

                    @Override
                    public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {
                        promise.reject("kinit - failed", s,throwable);
                    }
                });
            }

            @Override
            public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {
                promise.reject("scanQr - failed", s,throwable);
            }

            @Override
            public void onOperationCancelled() {
                promise.reject("scanQr - cancelled", "scanQr - cancelled");
            }
        });
    }
}
