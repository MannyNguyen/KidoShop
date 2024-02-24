package vn.kido.shop.Fragment.Search;


import android.support.v4.app.Fragment;


//import com.google.zxing.Result;
//import me.dm7.barcodescanner.zxing.ZXingScannerView;
import vn.kido.shop.Fragment.BaseFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScanFragment extends BaseFragment {

//    ZXingScannerView scanner;
//    final int CAMERA = 1001;
//    PermissionHelper permissionHelper;
//
//    public ScanFragment() {
//        // Required empty public constructor
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Inflate the layout for this fragment
//        if (rootView != null) {
//            return rootView;
//        }
//        rootView = inflater.inflate(R.layout.fragment_scan, container, false);
//        return rootView;
//    }
//
//    public static ScanFragment newInstance() {
//
//        Bundle args = new Bundle();
//
//        ScanFragment fragment = new ScanFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        if (isLoaded) {
//            return;
//        }
//        permissionHelper = new PermissionHelper(ScanFragment.this);
//        TextView title = rootView.findViewById(R.id.title);
//        title.setText(getString(R.string.scan));
//        scanner = rootView.findViewById(R.id.scanner);
//        scanner.setResultHandler(new ZXingScannerView.ResultHandler() {
//            @Override
//            public void handleResult(Result result) {
//                Toast.makeText(getContext(), result.getText(), Toast.LENGTH_SHORT).show();
//            }
//        });
//        isLoaded = true;
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        requestPermission();
//    }
//
//    @Override
//    public void manuResume() {
//        super.manuResume();
//        requestPermission();
//    }
//
//    private void requestPermission() {
//        if (permissionHelper.requestPermission(CAMERA, Manifest.permission.CAMERA, "Xin cấp quyền Camera cho Kido!")) {
//            scanner.startCamera();
//        }
//    }
//
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        switch (requestCode) {
//            case CAMERA:
//                boolean isPerpermissionForAllGranted = true;
//                for (int i = 0; i < permissions.length; i++) {
//                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
//                        isPerpermissionForAllGranted = false;
//                    }
//                }
//                if (isPerpermissionForAllGranted) {
//                    if (scanner != null) {
//                        scanner.startCamera();
//                    }
//                }
//                break;
//        }
//    }
}
