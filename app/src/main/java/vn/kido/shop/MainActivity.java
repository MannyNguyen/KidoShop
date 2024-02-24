package vn.kido.shop;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.DisplayMetrics;

import com.google.android.gms.auth.api.signin.internal.Storage;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import vn.kido.shop.Api.APIService;
import vn.kido.shop.Api.ISubscriber;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Class.GlobalClass;
import vn.kido.shop.Constant.AppConfig;
import vn.kido.shop.Fragment.Dialog.NoticeDialogFragment;
import vn.kido.shop.Fragment.Dialog.UpdateDialogFragment;
import vn.kido.shop.Fragment.OAuth.ChooseServerFragment;
import vn.kido.shop.Helper.SocketIOHelper;
import vn.kido.shop.Helper.StorageHelper;


public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        GlobalClass.setActivity(MainActivity.this);
        StorageHelper.init(getApplicationContext());
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        APIService.setInstance(null);
        CmmFunc.hideKeyboard(this);
        new ActionGetVersion().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        //Prevent zoom of system
//        Configuration configuration = getResources().getConfiguration();
//        configuration.fontScale = (float) 1; //0.85 small size, 1 normal size, 1,15 big etc
//        DisplayMetrics metrics = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        metrics.scaledDensity = configuration.fontScale * metrics.density;
//        configuration.densityDpi = (int) getResources().getDisplayMetrics().xdpi;
//        getBaseContext().getResources().updateConfiguration(configuration, metrics);
    }


    public void route() {
        if (TextUtils.isEmpty(StorageHelper.get(StorageHelper.TOKEN))) {
   /*         Get account Guest when user has no token (first login)
            Yêu cầu tài khoản khác khi không có token (lần đăng nhập đầu tiên)*/
            getAccountGuest();

//            Intent intent = new Intent(MainActivity.this, OAuthActivity.class);
//            startActivity(intent);
//            finish();
            return;
        }
        //Đã Login
        try {
            if (StorageHelper.get(StorageHelper.DEVICE_ID).equals("")) {
                String deviceId = Settings.Secure.getString(GlobalClass.getContext().getContentResolver(),
                        Settings.Secure.ANDROID_ID);
                StorageHelper.set(StorageHelper.DEVICE_ID, deviceId);
            }

            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            Bundle bundle = MainActivity.this.getIntent().getExtras();
            if (bundle != null) {
                intent.putExtras(bundle);
            }
            Uri data = getIntent().getData();
            if (data != null) {
                intent.setData(data);
            }
            startActivity(intent);
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private class ActionGetVersion extends AsyncTask<Void, Void, JSONObject> {
        String response;

        @Override
        protected JSONObject doInBackground(Void... voids) {
            try {
                URL url = new URL(AppConfig.DOMAIN_CONFIG);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                int responseCode = urlConnection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    response = readStream(urlConnection.getInputStream());
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray config = jsonObject.getJSONArray("config");
                    for (int i = 0; i < config.length(); i++) {
                        JSONObject item = config.getJSONObject(i);
                        if (item.getString("version").equals(AppConfig.VERSION)) {
                            if (StorageHelper.get(StorageHelper.SERVER).equals("") || StorageHelper.get(StorageHelper.SERVER).equals(ChooseServerFragment.REAL)) {
                                AppConfig.DOMAIN = item.getString("restful_ip");
                                SocketIOHelper.HOST = item.getString("socket_chat_ip");
                            }
                            if (StorageHelper.get(StorageHelper.SERVER).equals(ChooseServerFragment.DEV)) {
                                AppConfig.DOMAIN = item.getString("restful_ip_dev");
                                SocketIOHelper.HOST = item.getString("socket_chat_ip_dev");
                            }
                            if (StorageHelper.get(StorageHelper.SERVER).equals(ChooseServerFragment.TEST)) {
                                AppConfig.DOMAIN = item.getString("restful_ip_test");
                                SocketIOHelper.HOST = item.getString("socket_chat_ip_test");
                            }
                            final String androidUrl = item.getString("android_url");
                            int updateStatus = item.getInt("update_status");
                            if (updateStatus == 0) {
                                //No update
                                route();
                            } else if (updateStatus == 1) {
                                //Option update
                                final UpdateDialogFragment messageDialogFragment = UpdateDialogFragment.newInstance();
                                messageDialogFragment.setTitle(getString(R.string.notify));
                                messageDialogFragment.setMessage(getString(R.string.alert_update));
                                messageDialogFragment.setBackRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        route();
                                    }
                                });
                                messageDialogFragment.setRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(androidUrl));
                                            startActivity(i);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                messageDialogFragment.show(MainActivity.this.getSupportFragmentManager(), messageDialogFragment.getClass().getName());
                            } else if (updateStatus == 2) {
                                //Force update
                                NoticeDialogFragment noticeDialogFragment = NoticeDialogFragment.newInstance();
                                noticeDialogFragment.setTouchOutSide(false);
                                noticeDialogFragment.setTitle(getString(R.string.notify));
                                noticeDialogFragment.setMessage(getString(R.string.alert_update));
                                noticeDialogFragment.setRunnable(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            Intent i = new Intent(Intent.ACTION_VIEW);
                                            i.setData(Uri.parse(androidUrl));
                                            startActivity(i);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                                noticeDialogFragment.show(MainActivity.this.getSupportFragmentManager(), noticeDialogFragment.getClass().getName());
                            }
                        }
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            super.onPostExecute(jsonObject);
            try {
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private String readStream(InputStream in) {
        BufferedReader reader = null;
        StringBuffer response = new StringBuffer();
        try {
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    private void getAccountGuest() {
        APIService.getInstance().getAccountGuest()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ISubscriber() {
                    @Override
                    public void excute(JSONObject jsonObject) {
                        try {
                            JSONObject data = jsonObject.getJSONObject("data");
                            StorageHelper.set(StorageHelper.DEVICE_ID, data.getString("deviceid"));
                            StorageHelper.set(StorageHelper.USERNAME, data.getString("username"));
                            StorageHelper.set(StorageHelper.TOKEN, data.getString("token"));

                            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                            Bundle bundle = MainActivity.this.getIntent().getExtras();
                            if (bundle != null) {
                                intent.putExtras(bundle);
                            }
                            Uri dataIntent = getIntent().getData();
                            if (dataIntent != null) {
                                intent.setData(dataIntent);
                            }
                            startActivity(intent);
                            finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
    }
}
