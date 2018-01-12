package com.support.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;

import com.support.R;
import com.support.base.CoreApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;

/* Usage:

     1. declare provider in manifest

     <provider
        android:name="android.support.v4.content.FileProvider"
        android:authorities="${applicationId}.fileprovider"
        android:exported="false"
        android:grantUriPermissions="true">
        <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/image_path" />
        </provider>

     2. write permission and feature in manifest

     <uses-permission android:name="android.permission.CAMERA" />

     <uses-feature
        android:name="android.hardware.camera"
        android:required="false" />

     3. xml file for provider resource

     <paths>
        <files-path name="captured_image" path="Images/" />
     </paths>

     4. calling code

        ImageChooserUtil.openChooserDialog(getCoreFragment(),fileName,targetView);

     4. add in requesting activity

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {
                case ImageChooserUtil.PERMISSION_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    ImageChooserUtil.openChooserDialog(getCoreFragment(), fileName,targetView);
                }
                break;
            case ImageChooserUtil.PERMISSION_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    ImageChooserUtil.startCameraIntent(getCoreFragment(), fileName);
                }
                break;
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

      5. Add onActivityResult

       @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case ImageChooserUtil.REQUEST_GALLERY:
                case ImageChooserUtil.REQUEST_CAMERA:
                    if (resultCode == RESULT_OK) {
                        new ImageChooserUtil.SaveImageTask(
                                data,
                                requestCode,
                                fileName,
                                new ImageChooserUtil.SaveImageTask.FileSaveListener() {
                                    @Override
                                    public void fileSaved(File file) {

                                    }
                                }).execute();
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

      */

public class ImageChooserUtil {

    public static final int REQUEST_GALLERY = 1235;
    public static final int REQUEST_CAMERA = 1234;
    public static final int PERMISSION_CAMERA = 1236;
    public static final int PERMISSION_WRITE_STORAGE = 1237;
    private static final String IMAGE_DIRECTORY = "Images";
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = ".fileprovider";
    private static String FILE_EXTENSION = ".png";

    public static void openChooserDialog(final Activity activity, final String fileName) {
        openChooserDialog(activity, fileName, null);
    }

    /**
     * @param fileName   keep file name in field. this will be required when getting permission.
     * @param targetView it will trigger in an animation on device above lollipop.
     */
    public static void openChooserDialog(final Activity activity, final String fileName, View targetView) {
        if (PermissionUtil.checkPermission(activity, PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE)) {
            showDialog(activity, targetView, fileName);
        } else {
            PermissionUtil.getPermission(activity,
                    PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE,
                    PERMISSION_WRITE_STORAGE,
                    PermissionUtil.PermissionMessage.WRITE_EXTERNAL_STORAGE,
                    null);
        }
    }

    private static void showDialog(Activity activity, View targetView, String fileName) {
        final View dialogView = View.inflate(activity, R.layout.image_chooser_dialog, null);
        AppCompatDialog appCompatDialog = new AppCompatDialog(activity);
        appCompatDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        appCompatDialog.setContentView(dialogView);

        Window window = appCompatDialog.getWindow();
        if (window != null) {
            window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        if (targetView != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            appCompatDialog.setOnShowListener(dialogInterface -> revealShow(dialogView, true, appCompatDialog, targetView));
        }
        appCompatDialog.setCancelable(true);
        appCompatDialog.show();

        appCompatDialog.findViewById(R.id.btn_gallery).setOnClickListener(view -> {
            if (targetView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                revealShow(dialogView, false, appCompatDialog, targetView);
            } else appCompatDialog.dismiss();
            startGalleryIntent(activity);
        });
        appCompatDialog.findViewById(R.id.btn_camera).setOnClickListener(view -> {
            if (targetView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                revealShow(dialogView, false, appCompatDialog, targetView);
            } else appCompatDialog.dismiss();
            if (PermissionUtil.checkPermission(activity, PermissionUtil.Permissions.CAMERA)) {
                startCameraIntent(activity, fileName);
            } else {
                PermissionUtil.getPermission(activity,
                        PermissionUtil.Permissions.CAMERA,
                        PERMISSION_CAMERA,
                        PermissionUtil.PermissionMessage.CAMERA,
                        null);
            }
        });

        appCompatDialog.findViewById(R.id.btn_cancel).setOnClickListener(view -> {
            if (targetView != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                revealShow(dialogView, false, appCompatDialog, targetView);
            } else appCompatDialog.dismiss();
        });
        appCompatDialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {
                if (targetView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    revealShow(dialogView, false, appCompatDialog, targetView);
                } else appCompatDialog.dismiss();
                return true;
            }

            return false;
        });
    }

    public static void openChooserDialog(@NonNull Fragment fragment, final String fileName) {
        openChooserDialog(fragment, fileName, null);
    }

    /**
     * @param fileName   keep file name in field. this will be required when getting permission.
     * @param targetView it will trigger in an animation on device above lollipop.
     */
    public static void openChooserDialog(@NonNull Fragment fragment, final String fileName, View targetView) {
        if (PermissionUtil.checkPermission(fragment.getContext(), PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE)) {
            showDialog(fragment, fileName, targetView);
        } else {
            PermissionUtil.getPermission(fragment,
                    PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE,
                    PERMISSION_WRITE_STORAGE,
                    PermissionUtil.PermissionMessage.WRITE_EXTERNAL_STORAGE,
                    null);
        }
    }

    private static void showDialog(Fragment fragment, String fileName, View targetView) {
        final View dialogView = View.inflate(fragment.getContext(), R.layout.image_chooser_dialog, null);
        AppCompatDialog appCompatDialog = new AppCompatDialog(fragment.getContext());
        appCompatDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        appCompatDialog.setContentView(dialogView);

        Window window = appCompatDialog.getWindow();
        if (window != null) {
            window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        if (targetView != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            appCompatDialog.setOnShowListener(dialogInterface -> revealShow(dialogView, true, appCompatDialog, targetView));
        }
        appCompatDialog.setCancelable(true);
        appCompatDialog.show();

        appCompatDialog.findViewById(R.id.btn_gallery).setOnClickListener(view -> {
            if (targetView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                revealShow(dialogView, false, appCompatDialog, targetView);
            }
            startGalleryIntent(fragment);
        });
        appCompatDialog.findViewById(R.id.btn_camera).setOnClickListener(view -> {
            if (targetView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                revealShow(dialogView, false, appCompatDialog, targetView);
            }
            if (PermissionUtil.checkPermission(fragment.getContext(), PermissionUtil.Permissions.CAMERA)) {
                startCameraIntent(fragment, fileName);
            } else {
                PermissionUtil.getPermission(fragment,
                        PermissionUtil.Permissions.CAMERA,
                        PERMISSION_CAMERA,
                        PermissionUtil.PermissionMessage.CAMERA,
                        null);
            }
        });

        appCompatDialog.findViewById(R.id.btn_cancel).setOnClickListener(view -> {
            if (targetView != null && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                revealShow(dialogView, false, appCompatDialog, targetView);
            }
        });
        appCompatDialog.setOnKeyListener((dialogInterface, i, keyEvent) -> {
            if (i == KeyEvent.KEYCODE_BACK) {
                if (targetView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    revealShow(dialogView, false, appCompatDialog, targetView);
                }
                return true;
            }

            return false;
        });
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private static void revealShow(View dialogView, boolean show, AppCompatDialog dialog, View targetView) {

        int w = dialogView.getWidth();
        int h = dialogView.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (targetView.getX() + (targetView.getWidth() / 2));
        int cy = (int) (targetView.getY()) + targetView.getHeight() + 56;


        if (show) {
            dialogView.setVisibility(View.VISIBLE);
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(dialogView, cx, cy, 0, endRadius);
            revealAnimator.setDuration(700);
            revealAnimator.start();
        } else {

            Animator anim = ViewAnimationUtils.createCircularReveal(dialogView, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    dialogView.setVisibility(View.INVISIBLE);
                }
            });
            anim.setDuration(700);
            anim.start();
        }

    }

    private static void startGalleryIntent(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        activity.startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY);
    }

    private static void startGalleryIntent(Fragment fragment) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        fragment.startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY);
    }

    public static void startCameraIntent(Activity activity, String fileName) {
        File path = new File(StorageUtils.createInternalDirectory(), IMAGE_DIRECTORY);

        if (!path.exists()) path.mkdirs();

        File image = new File(path, fileName + FILE_EXTENSION);

        Uri imageUri = FileProvider.getUriForFile(activity, activity.getPackageName() + CAPTURE_IMAGE_FILE_PROVIDER, image);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData clip =
                    ClipData.newUri(activity.getContentResolver(), "A photo", imageUri);

            intent.setClipData(clip);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            List<ResolveInfo> resInfoList =
                    activity.getPackageManager()
                            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                activity.grantUriPermission(packageName, imageUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }

        activity.startActivityForResult(intent, REQUEST_CAMERA);
    }

    public static void startCameraIntent(Fragment fragment, String fileName) {
        File path = new File(StorageUtils.createInternalDirectory(), IMAGE_DIRECTORY);

        if (!path.exists()) path.mkdirs();

        File image = new File(path, fileName + FILE_EXTENSION);

        Uri imageUri = FileProvider.getUriForFile(fragment.getContext(), fragment.getContext().getPackageName() + CAPTURE_IMAGE_FILE_PROVIDER, image);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ClipData clip =
                    ClipData.newUri(fragment.getContext().getContentResolver(), "A photo", imageUri);

            intent.setClipData(clip);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        } else {
            List<ResolveInfo> resInfoList =
                    fragment.getContext().getPackageManager()
                            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

            for (ResolveInfo resolveInfo : resInfoList) {
                String packageName = resolveInfo.activityInfo.packageName;
                fragment.getContext().grantUriPermission(packageName, imageUri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            }
        }

        fragment.startActivityForResult(intent, REQUEST_CAMERA);
    }

    public static File getGalleryImageFile(Intent data, String fileName) {
        if (data != null) {
            return saveImageToStorage(
                    data.getData(),
                    IMAGE_DIRECTORY,
                    fileName);
        } else {
            return null;
        }
    }

    private static File saveImageToStorage(Uri uri, String imageDirectory, String fileName) {
        String root = StorageUtils.createInternalDirectory();
        File myDir = new File(root + "/" + imageDirectory);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File file = new File(myDir, fileName + "." + FileChooserUtil.getFileExtension(uri.toString()));
        if (file.exists()) {
            file.delete();
        }

        try {
            FileOutputStream out = new FileOutputStream(file);
            InputStream in = CoreApp.getInstance().getContentResolver().openInputStream(uri);
            if (in != null) {
                byte[] buffer = new byte[1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                out.flush();
                out.close();
                in.close();
            }
            return file;
        } catch (Exception e) {
            AppLog.log(false, "ImageChooserUtil " + "saveImageToStorage: ", e);
            return file;
        }
    }

    public static File getCameraImageFile(String fileName) {
        File path = new File(StorageUtils.createInternalDirectory(), IMAGE_DIRECTORY);
        if (!path.exists()) path.mkdirs();
        return new File(path, fileName + FILE_EXTENSION);
    }

    public static File saveImageToStorage(Bitmap finalBitmap, String path, String imageName) {
        String root = StorageUtils.createInternalDirectory();
        File myDir = new File(root + "/" + path);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File file = new File(myDir, imageName + FILE_EXTENSION);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return file;
        } catch (Exception e) {
            AppLog.log(false, "ImageChooserUtil " + "saveImageToStorage: ", e);
            return file;
        }
    }

    public static File saveImageToExternalStorage(Bitmap finalBitmap, String path, String imageName, String fileExtension) {
        String root = StorageUtils.createExternalDirectory();
        File myDir = new File(root + "/" + path);
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File file = new File(myDir, imageName + "." + fileExtension);
        if (file.exists()) {
            file.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            return file;
        } catch (Exception e) {
            AppLog.log(false, "ImageChooserUtil " + "saveImageToStorage: ", e);
            return file;
        }
    }

    public static class SaveImageTask extends AsyncTask<Void, Void, File> {

        private Intent data;
        private int requestCode;
        private String fileName;
        private FileSaveListener listener;

        public SaveImageTask(Intent data,
                             int requestCode,
                             String fileName,
                             FileSaveListener listener) {
            this.data = data;
            this.requestCode = requestCode;
            this.fileName = fileName;
            this.listener = listener;
        }

        @Override
        protected File doInBackground(Void... pObjects) {
            File file = null;

            if (requestCode == ImageChooserUtil.REQUEST_GALLERY) {
                file = ImageChooserUtil
                        .getGalleryImageFile(data,
                                String.valueOf(fileName));
            } else if (requestCode == ImageChooserUtil.REQUEST_CAMERA) {
                file = ImageChooserUtil.getCameraImageFile(String.valueOf(fileName));
            }

            return file;
        }

        @Override
        protected void onPostExecute(File file) {
            if (listener != null && file != null)
                listener.fileSaved(file);
            super.onPostExecute(file);
        }

        public interface FileSaveListener {
            void fileSaved(File file);
        }
    }
}
