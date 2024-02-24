package vn.kido.shop.Helper;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import vn.kido.shop.Class.GlobalClass;

public class ImageHelper {
    public Bitmap dataToBitmap(Intent data) {
        Bitmap bmp = null;
        try {
            Uri uri = Uri.parse(data.getDataString());
            if (uri != null) {
                String realPath = PathHelper.getPathFromUri(GlobalClass.getActivity(), uri);
                if (realPath != null) {
                    ExifInterface exif = new ExifInterface(realPath);
                    int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    switch (rotation) {
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotation = 90;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotation = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotation = 270;
                            break;
                        default:
                            rotation = 0;
                            break;
                    }
                    Matrix matrix = new Matrix();
                    if (rotation != 0f) {
                        matrix.preRotate(rotation);
                    }

                    bmp = MediaStore.Images.Media.getBitmap(GlobalClass.getActivity().getContentResolver(), uri);
                    bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
                } else {
                    if (data.getData() == null) {
                        bmp = (Bitmap) data.getExtras().get("data");
                    } else {
                        InputStream inputStream = GlobalClass.getContext().getContentResolver().openInputStream(data.getData());
                        BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                        bmp = BitmapFactory.decodeStream(bufferedInputStream);
                    }
                }
            } else {
                if (data.getData() == null) {
                    bmp = (Bitmap) data.getExtras().get("data");
                } else {
                    InputStream inputStream = GlobalClass.getContext().getContentResolver().openInputStream(data.getData());
                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                    bmp = BitmapFactory.decodeStream(bufferedInputStream);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    public Bitmap uriToBitmap(Uri uri) {
        Bitmap bmp = null;
        try {
            String realPath = PathHelper.getPathFromUri(GlobalClass.getActivity(), uri);
            if (realPath != null) {
                ExifInterface exif = new ExifInterface(realPath);
                int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                switch (rotation) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotation = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotation = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotation = 270;
                        break;
                    default:
                        rotation = 0;
                        break;
                }
                Matrix matrix = new Matrix();
                if (rotation != 0f) {
                    matrix.preRotate(rotation);
                }

                bmp = MediaStore.Images.Media.getBitmap(GlobalClass.getActivity().getContentResolver(), uri);
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, false);
            } else {
                bmp = MediaStore.Images.Media.getBitmap(GlobalClass.getActivity().getContentResolver(), uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    private String path;

    private String getPath() {
        if (path == null) {
            ContextWrapper cw = new ContextWrapper(GlobalClass.getContext());
            File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
            path = directory.getAbsolutePath();
        }
        return path;
    }

    public boolean saveToInternalStorage(Bitmap bitmapImage, String name) {
        try {
            File mypath = new File(getPath(), name + ".jpg");
            FileOutputStream fos = new FileOutputStream(mypath);
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public Bitmap loadImageFromStorage(String name) {
        try {
            File f = new File(getPath(), name + ".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            return b;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String loadImageFromStorageToBase64(String name) {
        try {
            File f = new File(getPath(), name + ".jpg");
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            String encoded = Base64.encodeToString(byteArray, Base64.DEFAULT);
            return encoded;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public  Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
}
