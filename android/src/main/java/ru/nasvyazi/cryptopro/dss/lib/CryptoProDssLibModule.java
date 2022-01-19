package ru.nasvyazi.cryptopro.dss.lib;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;

import com.digt.sdk.*;
import com.digt.sdk.auth.Auth;
import com.digt.sdk.auth.models.DssUser;
import com.digt.sdk.auth.models.RegisterInfo;
import com.digt.sdk.cert.Cert;
import com.digt.sdk.cert.models.Certificate;
import com.digt.sdk.docs.Docs;
import com.digt.sdk.interfaces.SdkCallback;
import com.digt.sdk.interfaces.SdkCertificateListCallback;
import com.digt.sdk.interfaces.SdkDssUserCallback;
import com.digt.sdk.interfaces.SdkGetDocumentCallback;
import com.digt.sdk.interfaces.SdkInitCallback;
import com.digt.sdk.interfaces.SdkMtOperationCallback;
import com.digt.sdk.interfaces.SdkMtOperationWithSuspendCallback;
import com.digt.sdk.interfaces.SdkPolicyCaParamsCallback;
import com.digt.sdk.interfaces.SdkPolicyOperationHistoryCallback;
import com.digt.sdk.interfaces.SdkPolicyOperationsInfoCallback;
import com.digt.sdk.interfaces.SdkQrCallback;
import com.digt.sdk.policy.Policy;
import com.digt.sdk.policy.models.CaParams;
import com.digt.sdk.policy.models.OperationHistory;
import com.digt.sdk.sign.Sign;
import com.digt.sdk.sign.models.ApproveRequestMT;
import com.digt.sdk.sign.models.Operation;
import com.digt.sdk.sign.models.OperationsInfo;
import com.digt.sdk.utils.Constants;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMarker;
import com.facebook.react.bridge.ReactMarkerConstants;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.common.LifecycleState;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ru.nasvyazi.cryptopro.dss.lib.ReactBridgeTools.convertJsonToMap;

class InitCallbackHandler implements SdkInitCallback {
    public void onInit(Constants.CSPInitCode var1) {
        System.out.println(var1);
    }
}

public class CryptoProDssLibModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;
    private Auth lastAuth;
    private Operation lastOperation;
    private ApproveRequestMT lastRequest;


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
        CryptoProDss.getInstance().registerActivityContext(this.reactContext);
     }


    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void SdkInitialization(Promise promise) {
        CryptoProDss.initDSS(((FragmentActivity)this.reactContext.getCurrentActivity()));
        CryptoProDss.getInstance().init(((FragmentActivity)this.reactContext.getCurrentActivity()),new HashMap<String,String[]>(),new InitCallbackHandler(){
            @Override
            public void onInit(Constants.CSPInitCode var1) {
                promise.resolve((var1).getTitle());
            }
        });
    }

    public URI getUriFromAssets(String pathResource) throws IOException {
        InputStream inputStream = null;
        OutputStream outputStream = null;

        inputStream = getReactApplicationContext().getAssets().open(pathResource);
        File tempFile = File.createTempFile("tmp", ".bin");
        byte[] fileReader = new byte[4096];
        outputStream = new FileOutputStream(tempFile);

        while (true){
            int read = inputStream.read(fileReader);
            if (read == -1){
                break;
            }
            outputStream.write(fileReader, 0, read);
        }
        outputStream.flush();
        return tempFile.toURI();
    }

    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void updateStyles(Promise promise) throws URISyntaxException {
        Policy policy = new Policy();

        URI url = null;
        try {
            url = getUriFromAssets("SDKStyles.json");
            policy.setPersonalisation(url);
           promise.resolve("updateStyles success");
        } catch (IOException e) {
            promise.reject("cant load styles", "cant load styles");
        }
    }



    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void getDocuments(Promise promise) {
        Policy policy = new Policy();

                Log.i("nasvyzi", "getDocuments called");
        policy.getCaParams(getReactApplicationContext().getCurrentActivity(), getLastUserKid(), new SdkPolicyCaParamsCallback() {
            @Override
            public void onOperationSuccessful(@NonNull CaParams caParams) {
                Log.i("nasvyzi", "getDocuments onOperationSuccessful");
                Log.i("nasvyzi", caParams.toString());
                promise.resolve(caParams);
            }

            @Override
            public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {
                Log.i("nasvyzi", "getDocuments onOperationFailed");
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
            public String toString() {
                return "$classname{}";
            }

            @Override
            public void onOperationSuccessful(@NonNull List<Certificate> list) {
                Log.i("nasvyzi", list.toString());
                Log.i("nasvyzi", Boolean.toString(list instanceof List));
                List<WritableMap> listWithJson =  new ArrayList<>();
                for (Certificate cert : list)
                {
                    try {

                        listWithJson.add(convertJsonToMap(new JSONObject(cert.toJsonString())));
                    } catch (JSONException e) {
                        Log.i("nasvyzi", "wtf error");
                        Log.i("nasvyzi", e.toString());
                    }
                }
                Log.i("nasvyzi", Boolean.toString(listWithJson instanceof List));
                WritableNativeArray array = Arguments.makeNativeArray((List)listWithJson);
                promise.resolve(array);
            }

            @Override
            public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {

                promise.reject("cert getCerts - failed", s, throwable);
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void getHistoryOperations(Promise promise) {

      //  Integer count = Integer.parseInt((_count));

        Policy policy = new Policy();
        policy.getHistoryOperations(getReactApplicationContext().getCurrentActivity(), getLastUserKid(), null, null, new ArrayList<>(), new SdkPolicyOperationHistoryCallback() {
            @Override
            public void onOperationSuccessful(@NonNull OperationHistory operationHistory) {

                Log.i("nasvyzi", operationHistory.toJsonString());
                promise.resolve("since ok");
            }

            @Override
            public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {
                Log.i("nasvyzi", "getHistoryOperations onOperationFailed");
                // Log.i("nasvyzi", s);
                promise.reject("getHistoryOperations onOperationFailed - failed", s, throwable);
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void getOperations(String kid,Promise promise) {
        Policy policy = new Policy();
        policy.getOperations(getReactApplicationContext().getCurrentActivity(), kid, null, null, new SdkPolicyOperationsInfoCallback() {
            @Override
            public void onOperationSuccessful(@NonNull OperationsInfo operationsInfo) {
                List<WritableMap> listWithJson =  new ArrayList<>();
                for (Operation operation : operationsInfo.getOperations())
                {
                    try {
                        Log.i("TEST", String.valueOf(operation.getCreatedAt()));
                        listWithJson.add(convertJsonToMap(new JSONObject(operation.toJsonString())));
                    } catch (JSONException e) {
                        promise.reject("json error", "json error");
                    }
                }
                WritableNativeArray array = Arguments.makeNativeArray((List)listWithJson);
                promise.resolve(array);
            }

            @Override
            public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {
                promise.reject("getOperations - failed", s, throwable);
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void downloadDocument(String id, Promise promise) {
        Docs docs = new Docs();
        docs.downloadDocument(getReactApplicationContext().getCurrentActivity(), getLastUserKid(), id, new SdkGetDocumentCallback() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onOperationSuccessful(byte[] bytes) {

                // readable string that encoded in base64, easy transfer as a string

                // byte[] to base64 string
                String s = Base64.getEncoder().encodeToString(bytes);

                // base64 string to byte[]
                byte[] decode = Base64.getDecoder().decode(s);


                promise.resolve(Arguments.makeNativeArray(bytes));
            }

            @Override
            public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {

                Log.i("nasvyzi", "downloadDocument onOperationFailed");
               // Log.i("nasvyzi", s);
                promise.reject("downloadDocument - failed", s, throwable);
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void signMT(String transactionId, String kid, Promise promise) {
        Sign sign = new Sign();

        Policy policy = new Policy();
        policy.getOperations(getReactApplicationContext().getCurrentActivity(), kid, null, null, new SdkPolicyOperationsInfoCallback() {
            @Override
            public void onOperationSuccessful(@NonNull OperationsInfo operationsInfo) {

                Operation _operation = null;

                for (Operation operation : operationsInfo.getOperations())
                {
                    if (operation.getTransactionId().equals(transactionId)){
                        _operation = operation;
                    }
                }


                Operation final_operation = _operation;
                sign.signMT(getReactApplicationContext().getCurrentActivity(),kid,_operation, false,false,false, new SdkMtOperationWithSuspendCallback() {
                    @Override
                    public void onOperationSuccessful() {
                        promise.resolve("success");
                    }

                    @Override
                    public void onOperationSuspendedConfirm(@NonNull ApproveRequestMT approveRequestMT) {
                        lastOperation = final_operation;
                        lastRequest = approveRequestMT;
                        try {
                            promise.resolve(convertJsonToMap(new JSONObject(lastRequest.toJsonString())));
                        } catch (JSONException e) {
                            promise.resolve("JSON PARSE ERROR");
                        }
                    }

                    @Override
                    public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {
                        promise.reject("signMT - failed",s, throwable);
                    }
                });
            }

            @Override
            public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {
                promise.reject("signMT - onOperationFailed - failed", s, throwable);
            }
        });

    }

    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void deferredRequest(String kid, Promise promise) {
        Sign sign = new Sign();

        sign.deferredRequest(getReactApplicationContext().getCurrentActivity(), kid, lastRequest, new SdkMtOperationCallback() {
            @Override
            public void onOperationSuccessful() {
                promise.resolve("success");
            }

            @Override
            public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {
                promise.reject("deferredRequest - failed",s, throwable);
            }
        });
    }

    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void setCert(String base64, Promise promise) {
        Cert cert = new Cert();
        cert.setCert(getReactApplicationContext().getCurrentActivity(), getLastUserKid(), base64, new SdkCallback() {
            @Override
            public void onOperationSuccessful() {

                Log.i("nasvyzi", "setCert ok?");
                promise.resolve("ok, since ok");
            }

            @Override
            public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {

                Log.i("nasvyzi", "setCert failed");
                Log.i("nasvyzi", s);
                promise.reject("setCert failed", s, throwable);
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

    public Map<String, String> convertDssUser(DssUser user){
        Map<String, String> object = new HashMap<>();
        object.put("kid", user.getKid());
        object.put("uid", user.getUid());
        return object;
    }

    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void getUsers(Promise promise) {
        List<DssUser> authList = new ArrayList<DssUser>();
        try {

            authList = Auth.getAuthList(getReactApplicationContext().getCurrentActivity());
        } catch (Exception e) {
            Log.i("nasvyzi", e.toString());
            e.printStackTrace();
        }

        List<Map<String, String>> list = new ArrayList<>();

        authList.forEach(dssUser -> {
            list.add(convertDssUser(dssUser));
        });

        WritableNativeArray array = Arguments.makeNativeArray((List)list);
        promise.resolve(array);
    }

    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void continueInitViaQr(String kid,Promise promise) {
        lastAuth.confirm(getReactApplicationContext().getCurrentActivity(), kid, new SdkCallback() {
            @Override
            public void onOperationSuccessful() {
                lastAuth.verify(getReactApplicationContext().getCurrentActivity(), kid, false, new SdkCallback() {
                    @Override
                    public void onOperationSuccessful() {
                        promise.resolve("success");
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

    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void initViaQr(String base64, Boolean useBiometric, Promise promise) {

        DssUser dssUser = new DssUser();
        RegisterInfo registerInfo = new RegisterInfo(null, null);
        lastAuth = new Auth();
        lastAuth.scanQr(this.reactContext.getCurrentActivity(), base64, new SdkQrCallback(){

            @Override
            public void onOperationSuccessful(@NonNull String s) {
                lastAuth.kinit(getReactApplicationContext().getCurrentActivity(), dssUser, registerInfo,useBiometric ? Constants.KeyProtectionType.BIOMETRIC: Constants.KeyProtectionType.PASSWORD, null, null, new SdkDssUserCallback(){
                    @Override
                    public void onOperationSuccessful() {
                        promise.resolve("success");
                    }

                    @Override
                    public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {
                        promise.reject("kinit - failed", s,throwable);
                    }
                });
            }

            @Override
            public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {
                Log.i("TEST", "scan onOperationFailed");
                promise.reject("scanQr - failed", s,throwable);
            }

            @Override
            public void onOperationCancelled() {

                promise.resolve("cancel");
            }
        });
    }


    @SuppressLint("RestrictedApi")
    @ReactMethod
    public void tryRelogin(Promise promise) {

        DssUser dssUser = new DssUser();
        RegisterInfo registerInfo = new RegisterInfo(null, null);
        Auth auth = new Auth();


        auth.verify(getReactApplicationContext().getCurrentActivity(), getLastUserKid(), false, new SdkCallback() {
            @Override
            public void onOperationSuccessful() {
                Log.i("nasvyzi", "auth verify onOperationSuccessful");
                promise.resolve("ok, since ok");
            }

            @Override
            public void onOperationFailed(int i, @Nullable String s, @Nullable Throwable throwable) {
                Log.i("nasvyzi", "auth verify onOperationFailed");
                promise.reject("auth verify - failed", s, throwable);
            }
        });



    }

    private JSONObject convertJsonTo(ReadableMap readableMap) {
        JSONObject object = new JSONObject();
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();
        try {
            while (iterator.hasNextKey()) {
                String key = iterator.nextKey();
                switch (readableMap.getType(key)) {
                    case Null:
                        object.put(key, JSONObject.NULL);
                        break;
                    case Boolean:
                        object.put(key, readableMap.getBoolean(key));
                        break;
                    case Number:
                        object.put(key, readableMap.getDouble(key));
                        break;
                    case String:
                        object.put(key, readableMap.getString(key));
                        break;
                    case Map:
                        object.put(key, convertJsonTo(readableMap.getMap(key)));
                        break;
                    case Array:
                        object.put(key, convertJsonTo((ReadableMap) readableMap.getArray(key)));
                        break;
                }
            }
        }
        catch (Exception ex) {
            Log.d("nasvyzi", "convertMapToJson fail: " + ex);
        }
        return object;
    }
}