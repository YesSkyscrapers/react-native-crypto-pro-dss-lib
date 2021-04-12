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
import com.digt.sdk.cert.Cert;
import com.digt.sdk.cert.models.Certificate;
import com.digt.sdk.interfaces.SdkCallback;
import com.digt.sdk.interfaces.SdkCertificateListCallback;
import com.digt.sdk.interfaces.SdkDssUserCallback;
import com.digt.sdk.interfaces.SdkInitCallback;
import com.digt.sdk.interfaces.SdkPolicyCaParamsCallback;
import com.digt.sdk.interfaces.SdkQrCallback;
import com.digt.sdk.policy.Policy;
import com.digt.sdk.policy.models.CaParams;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
        CryptoProDss.initDSS(((FragmentActivity)this.reactContext.getCurrentActivity()));
        CryptoProDss.getInstance().init(((FragmentActivity)this.reactContext.getCurrentActivity()),new HashMap<String,String[]>(),new InitCallbackHandler(){
            @Override
            public void onInit(Constants.CSPInitCode var1) {
                promise.resolve((var1).getTitle());
            }
        });
    }


    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void getDocuments(Promise promise) {
        Policy policy = new Policy();
        policy.getCaParams(getReactApplicationContext().getCurrentActivity(), getLastUserKid(), new SdkPolicyCaParamsCallback() {
            @Override
            public void onOperationSuccessful(@NonNull CaParams caParams) {
                Log.i("nasvyzi", caParams.toString());
                promise.resolve(caParams);
            }

            @Override
            public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {
                promise.reject("cert getDocuments - failed", s, throwable);
            }
        });

    }


    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void getCerts(Promise promise) {
        Cert cert = new Cert();
        cert.getCertList(getReactApplicationContext().getCurrentActivity(), getLastUserKid(), new SdkCertificateListCallback() {
            @Override
            public void onOperationSuccessful(@NonNull List<Certificate> list) {
                Log.i("nasvyzi", list.toString());
                promise.resolve(list);
            }

            @Override
            public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {
                promise.reject("cert getCerts - failed", s, throwable);
            }
        });
    }

    private String getLastUserKid(){
        List<DssUser> authList = new ArrayList<DssUser>();
        try {
            authList = Auth.getAuthList(getReactApplicationContext().getCurrentActivity());
        } catch (Exception e) {
            Log.i("nasvyzi", e.toString());
            e.printStackTrace();
        }

        DssUser lastUser = authList.get(authList.size()-1);

        return lastUser.getKid();
    }

    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void initViaQr(String base64, Promise promise) {

        DssUser dssUser = new DssUser();
        RegisterInfo registerInfo = new RegisterInfo(null, null);
        Auth auth = new Auth();
        auth.scanQr(this.reactContext.getCurrentActivity(), base64, new SdkQrCallback(){

            @Override
            public void onOperationSuccessful(@NonNull String s) {
                auth.kinit(getReactApplicationContext().getCurrentActivity(), dssUser, registerInfo, Constants.KeyProtectionType.BIOMETRIC, null, null, new SdkDssUserCallback(){
                    @Override
                    public void onOperationSuccessful() {


                        auth.confirm(getReactApplicationContext().getCurrentActivity(), getLastUserKid(), new SdkCallback() {
                            @Override
                            public void onOperationSuccessful() {

                                auth.verify(getReactApplicationContext().getCurrentActivity(), getLastUserKid(), false, new SdkCallback() {
                                    @Override
                                    public void onOperationSuccessful() {
                                        promise.resolve("ok, since ok");
                                    }

                                    @Override
                                    public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {

                                        promise.reject("auth verify - failed", s, throwable);
                                    }
                                });
                            }

                            @Override
                            public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {
                                promise.reject("auth confirm - failed", s, throwable);
                            }
                        });
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
