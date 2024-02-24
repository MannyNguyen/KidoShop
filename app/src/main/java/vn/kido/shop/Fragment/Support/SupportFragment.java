package vn.kido.shop.Fragment.Support;


import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import vn.kido.shop.Adapter.ChatAdapter;
import vn.kido.shop.Bean.BeanMessage;
import vn.kido.shop.Class.CmmFunc;
import vn.kido.shop.Fragment.BaseFragment;
import vn.kido.shop.Helper.FragmentHelper;
import vn.kido.shop.Helper.ImageHelper;
import vn.kido.shop.Helper.PermissionHelper;
import vn.kido.shop.Helper.SocketIOHelper;
import vn.kido.shop.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SupportFragment extends BaseFragment implements View.OnClickListener {
    final int GALLERY_REQUEST_CODE = 11;
    final int CAPTURE_REQUEST_CODE = 12;
    Uri captureURI;
    PermissionHelper permissionHelper;
    ImageView addMore, takePhoto, uploadPhoto, send, callSupport;
    LinearLayout mediaContainer;
    EditText message;
    public List<BeanMessage> messages;
    public RecyclerView recycler;
    public ChatAdapter adapter;
    public LinearLayoutManager layoutManager;
    public RecyclerView.OnScrollListener onScrollListener;

    public SupportFragment() {
        // Required empty public constructor
    }

    public static SupportFragment newInstance() {
        Bundle args = new Bundle();
        SupportFragment fragment = new SupportFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView != null) {
            return rootView;
        }
        rootView = inflater.inflate(R.layout.fragment_support, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isLoaded) {
            return;
        }
        permissionHelper = new PermissionHelper(SupportFragment.this);
        threadInit = new Thread(new Runnable() {
            @Override
            public void run() {
                final TextView title = rootView.findViewById(R.id.title);
                addMore = rootView.findViewById(R.id.add_more);
                takePhoto = rootView.findViewById(R.id.take_photo);
                uploadPhoto = rootView.findViewById(R.id.upload_photo);
                send = rootView.findViewById(R.id.send);
                mediaContainer = rootView.findViewById(R.id.media_container);
                message = rootView.findViewById(R.id.edt_messsage);
                recycler = rootView.findViewById(R.id.recycler);
                callSupport = rootView.findViewById(R.id.call_support);
                messages = new ArrayList<>();
                adapter = new ChatAdapter(SupportFragment.this, messages, recycler);
                layoutManager = new LinearLayoutManager(getContext());
                addMore.setOnClickListener(SupportFragment.this);
                message.setOnClickListener(SupportFragment.this);
                send.setOnClickListener(SupportFragment.this);
                uploadPhoto.setOnClickListener(SupportFragment.this);
                takePhoto.setOnClickListener(SupportFragment.this);
                callSupport.setOnClickListener(SupportFragment.this);
                rootView.findViewById(R.id.menu_support).setOnClickListener(SupportFragment.this);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recycler.setItemViewCacheSize(100);
                        SocketIOHelper.connect();
                        layoutManager.setStackFromEnd(true);
                        title.setText(getString(R.string.takecare_24));
                        onScrollListener = new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0 && messages.size() >= 10) {
                                    BeanMessage beanMessage = messages.get(0);
                                    if (beanMessage.getType() == BeanMessage.TIME) {
                                        SocketIOHelper.getInstance().getHistory(messages.get(1).getId());
                                        return;
                                    }
                                    SocketIOHelper.getInstance().getHistory(messages.get(0).getId());
                                }
                            }
                        };
                        recycler.addOnScrollListener(onScrollListener);
                        recycler.setLayoutManager(layoutManager);
                        recycler.setAdapter(adapter);
                    }
                });
            }
        });
        threadInit.start();
        isLoaded = true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_more:
                if (addMore.getVisibility() == View.GONE) {
                    return;
                }
                addMore.setVisibility(View.GONE);
                mediaContainer.setVisibility(View.VISIBLE);
                break;
            case R.id.edt_messsage:
                if (addMore.getVisibility() == View.VISIBLE) {
                    return;
                }
                addMore.setVisibility(View.VISIBLE);
                mediaContainer.setVisibility(View.GONE);
                break;

            case R.id.send:
                String m = message.getText().toString().trim();
                if (TextUtils.isEmpty(m)) {
                    return;
                }
                BeanMessage beanMessage = new BeanMessage();
                beanMessage.setMe(BeanMessage.ISME);
                beanMessage.setMessage(m);
                beanMessage.setType(BeanMessage.TEXT);
                beanMessage.setTimestamp(System.currentTimeMillis());
                messages.add(beanMessage);
                adapter.notifyItemInserted(messages.size() - 1);
                recycler.smoothScrollToPosition(messages.size() - 1);
                SocketIOHelper.getInstance().sendMessage(beanMessage.getType(), beanMessage.getMessage());
                message.setText("");
                break;
            case R.id.upload_photo:
                requestPermission(GALLERY_REQUEST_CODE);
                break;
            case R.id.take_photo:
                requestPermission(CAPTURE_REQUEST_CODE);
                break;
            case R.id.menu_support:
                FragmentHelper.add(MenuSupportFragment.newInstance());
                break;
            case R.id.call_support:
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + getString(R.string.phone_support)));
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void manuResume() {
        super.manuResume();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SocketIOHelper.getInstance().disconnect();
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case GALLERY_REQUEST_CODE:
                if (data == null) {
                    return;
                }
                parseDataImage(data);
                break;
            case CAPTURE_REQUEST_CODE:
                parseDataImage(captureURI);
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean isPerpermissionForAllGranted = true;
        switch (requestCode) {
            case GALLERY_REQUEST_CODE:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isPerpermissionForAllGranted = false;
                    }
                }
                if (isPerpermissionForAllGranted) {
                    Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                    intent.setType("image/*");
                    startActivityForResult(intent, GALLERY_REQUEST_CODE);
                }
                break;
            case CAPTURE_REQUEST_CODE:
                for (int i = 0; i < permissions.length; i++) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        isPerpermissionForAllGranted = false;
                    }
                }
                if (isPerpermissionForAllGranted) {
                    requestPermission(CAPTURE_REQUEST_CODE);
                }
                break;
        }
    }

    private void requestPermission(int requestCode) {
        if (permissionHelper.requestPermission(requestCode, Manifest.permission.WRITE_EXTERNAL_STORAGE, getString(R.string.per_write_storage))) {
            if (requestCode == GALLERY_REQUEST_CODE) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, requestCode);
            }
            if (requestCode == CAPTURE_REQUEST_CODE) {
                if (permissionHelper.requestPermission(requestCode, Manifest.permission.CAMERA, getString(R.string.per_access_camera))) {
                    openCamera();
                }
            }

        }
    }

    //region Methods

    private void openCamera() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, "KIDO" + System.currentTimeMillis());
                values.put(MediaStore.Images.Media.DESCRIPTION, "KIDO" + System.currentTimeMillis());
                captureURI = getActivity().getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, captureURI);
                startActivityForResult(intent, CAPTURE_REQUEST_CODE);
            }
        }).start();
    }

    private void parseDataImage(final Object data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bitmap bmp = null;
                    ImageHelper imageHelper = new ImageHelper();
                    if (data instanceof Intent) {
                        bmp = imageHelper.dataToBitmap((Intent) data);
                    } else if (data instanceof Uri) {
                        bmp = imageHelper.uriToBitmap((Uri) data);
                    }
                    if (bmp == null) {
                        return;
                    }
                    bmp = CmmFunc.resizeBitmap(bmp, CmmFunc.convertDpToPx(getContext(), 512));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 50, stream);

                    byte[] arr = stream.toByteArray();
                    final String base64 = Base64.encodeToString(arr, Base64.DEFAULT);
                    final BeanMessage beanMessage = new BeanMessage();
                    beanMessage.setMessage(base64);
                    beanMessage.setMe(BeanMessage.ISME);
                    beanMessage.setType(BeanMessage.IMAGE);
                    beanMessage.setTimestamp(System.currentTimeMillis());
                    messages.add(beanMessage);
                    final Bitmap finalBmp = bmp;
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                adapter.notifyItemInserted(messages.size() - 1);
                                recycler.smoothScrollToPosition(messages.size() - 1);
                                SocketIOHelper.getInstance().sendMessage(beanMessage.getType(), beanMessage.getMessage(), finalBmp.getWidth(), finalBmp.getHeight());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }
    //endregion


}
