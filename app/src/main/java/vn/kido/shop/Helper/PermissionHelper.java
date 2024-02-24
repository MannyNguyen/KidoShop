package vn.kido.shop.Helper;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;

import vn.kido.shop.Fragment.Dialog.MessageDialogFragment;

public class PermissionHelper {
    Fragment fragment;
    private boolean isSuccess;

    public PermissionHelper(Fragment fragment) {
        this.fragment = fragment;
    }

    public boolean requestPermission(int requestCode, String permission, String message) {
        try {
            if (ContextCompat.checkSelfPermission(fragment.getActivity(), permission) == PackageManager.PERMISSION_GRANTED) {
                setSuccess(true);
                return isSuccess();
            }

            //false nếu từ chối mãi mãi
            if (!fragment.shouldShowRequestPermissionRationale(permission)) {
                fragment.requestPermissions(new String[]{permission}, requestCode);
            } else {
                MessageDialogFragment messageDialogFragment = MessageDialogFragment.newInstance();
                messageDialogFragment.setMessage(message);
                messageDialogFragment.setRunnable(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //Open the specific App Info page:
                            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            intent.setData(Uri.parse("package:" + fragment.getActivity().getPackageName()));
                            fragment.startActivity(intent);
                        } catch (Exception e) {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                            fragment.startActivity(intent);
                        }
                    }
                });
                messageDialogFragment.show(fragment.getActivity().getSupportFragmentManager(), messageDialogFragment.getClass().getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        setSuccess(false);
        return isSuccess();
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    private void setSuccess(boolean success) {
        isSuccess = success;
    }
}
