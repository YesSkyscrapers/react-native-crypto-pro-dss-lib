package com.reactlibrary;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.digt.sdk.*;
import com.digt.sdk.auth.Auth;
import com.digt.sdk.auth.models.DssUser;
import com.digt.sdk.auth.models.RegisterInfo;
import com.digt.sdk.interfaces.SdkInitCallback;
import com.digt.sdk.utils.Constants;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.common.LifecycleState;

import java.lang.ref.WeakReference;

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


    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        CryptoProDss.getInstance().init(((FragmentActivity)this.reactContext.getCurrentActivity()),new InitCallbackHandler());


        DssUser dssUser = new DssUser();
        dssUser.setServiceUrl("service Url");
        dssUser.setAlias("alias");
        RegisterInfo registerInfo = new RegisterInfo("push address", "app version");
        Auth auth = new Auth();

        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }
}
