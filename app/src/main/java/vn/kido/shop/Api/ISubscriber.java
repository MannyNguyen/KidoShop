package vn.kido.shop.Api;

import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import org.json.JSONObject;

import java.net.ConnectException;
import java.net.UnknownHostException;

import rx.Subscriber;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Class.GlobalClass;
import vn.kido.shop.Fragment.Dialog.ErrorDialogFragment;
import vn.kido.shop.Fragment.OAuth.LoginFragment;
import vn.kido.shop.Fragment.OAuth.RegisterCustomerCodeFragment;
import vn.kido.shop.Fragment.OAuth.RegisterFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.Helper.StorageHelper;
import vn.kido.shop.MainActivity;
import vn.kido.shop.OAuthActivity;

import static vn.kido.shop.Class.GlobalClass.getActivity;

public abstract class ISubscriber extends Subscriber {
    public static final String SIGNUP = "SIGNUP";
    public static final String LOGIN = "LOGIN";

    @Override
    public void onCompleted() {
    }

    @Override
    public void onError(final Throwable e) {
        try {
            if (e instanceof ConnectException || e instanceof UnknownHostException) {
                //not network
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //Toast.makeText(GlobalClass.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(GlobalClass.getContext(), "Không có kết nối mạng. Vui lòng kiểm tra Wifi/3G và thử lại", Toast.LENGTH_LONG).show();
                    }
                });

                return;
            }
//            GlobalClass.getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast toast = Toast.makeText(GlobalClass.getContext(), e.getMessage(), Toast.LENGTH_SHORT);
//                    toast.show();
//                }
//            });
            done();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @Override
    public void onNext(Object response) {
        try {
            done();
            JSONObject jsonObject;
            if (!(response instanceof JSONObject)) {
                jsonObject = new JSONObject(response.toString());
            } else {
                jsonObject = (JSONObject) response;
            }

            int code = jsonObject.getInt("code");
            if (code != 1) {
                //error
                if (code == -1) {
                    JSONObject errorMessage = jsonObject.getJSONObject("error_message");
                    ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.newInstance(errorMessage.getString("vn") + "");
                    errorDialogFragment.setCancelable(false);
                    errorDialogFragment.show(getActivity().getSupportFragmentManager(), ErrorDialogFragment.class.getName());
                    errorDialogFragment.setRunnable(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                StorageHelper.set(StorageHelper.TOKEN, "");
                                StorageHelper.set(StorageHelper.USERNAME, "");
                                StorageHelper.set(StorageHelper.DEVICE_ID, "");
                                Intent intent = new Intent(getActivity(), OAuthActivity.class);
                                getActivity().startActivity(intent);
                                getActivity().finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    CmmFunc.hideKeyboard(getActivity());
                    return;
                }
                if (code == -3) {
                    JSONObject errorMessage = jsonObject.getJSONObject("error_message");
                    ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.newInstance(errorMessage.getString("vn") + "", SIGNUP);
                    errorDialogFragment.show(getActivity().getSupportFragmentManager(), ErrorDialogFragment.class.getName());
                    errorDialogFragment.setRunnable(new Runnable() {
                        @Override
                        public void run() {
                            FragmentHelper.add(RegisterCustomerCodeFragment.newInstance());
                        }
                    });
                    return;
                }

                //With account guest, if user use function add to cart or order product, show dialog request login
                if (code == -2) {
                    //request login
                    JSONObject errorMessage = jsonObject.getJSONObject("error_message");
                    ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.newInstance(errorMessage.getString("vn") + "", LOGIN);
                    errorDialogFragment.show(getActivity().getSupportFragmentManager(), ErrorDialogFragment.class.getName());
                    errorDialogFragment.setRunnable(new Runnable() {
                        @Override
                        public void run() {
                            FragmentHelper.add(LoginFragment.newInstance());
                        }
                    });
                    return;
                }

                if (code == -43){
                    FragmentHelper.add(LoginFragment.newInstance(true));
                    return;
                }

                JSONObject errorMessage = jsonObject.getJSONObject("error_message");
                final ErrorDialogFragment errorDialogFragment = ErrorDialogFragment.newInstance(errorMessage.getString("vn") + "");
                errorDialogFragment.show(getActivity().getSupportFragmentManager(), ErrorDialogFragment.class.getName());
                errorDialogFragment.setRunnable(new Runnable() {
                    @Override
                    public void run() {
                        errorDialogFragment.dismiss();
                    }
                });
                return;
            }
            excute(jsonObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //Gọi khi xong tiến trình voi code = 1
    public abstract void excute(JSONObject jsonObject);

    //khi chay xong tien trinh (tat progress)
    public void done() {

    }

}
