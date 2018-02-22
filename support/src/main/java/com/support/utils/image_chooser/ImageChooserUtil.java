package com.support.utils.image_chooser;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;

import com.support.R;
import com.support.base.CoreApp;
import com.support.utils.AppLog;
import com.support.utils.FileChooserUtil;
import com.support.utils.PermissionUtil;
import com.support.utils.StorageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static android.app.Activity.RESULT_OK;

 /*Usage:

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

//     - one time call
        multiImageChooserUtil = new ImageChooserUtil(2001, 2002,
                2003, 2004);

        - call everytime when selecting an image is necessary
        multiImageChooserUtil.setTargetView(galleryList.getChildAt(position));
        multiImageChooserUtil.openChooserDialog(getCoreFragment(), fileName, null);

     4. add in requesting activity

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            if (requestCode == multiImageChooserUtil.PERMISSION_WRITE_STORAGE) {
                multiImageChooserUtil.openChooserDialog(getCoreFragment(), fileName, null);
            } else if (requestCode == multiImageChooserUtil.PERMISSION_CAMERA) {
                multiImageChooserUtil.startCameraIntent(getCoreFragment(), fileName);
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

      5. Add onActivityResult

       @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (requestCode == multiImageChooserUtil.REQUEST_GALLERY || requestCode == multiImageChooserUtil.REQUEST_CAMERA) {
               multiImageChooserUtil.saveImageTask(data, requestCode, fileName, file -> {
                       if (addImage) {
                        workerDetailAdapter.addItem(file.getAbsolutePath(), workerDetailAdapter.getItemCount() - 1);
                    } else {
                         workerDetailAdapter.setItem(file.getAbsolutePath(), position);
                    }
                 });
            }
            super.onActivityResult(requestCode, resultCode, data);
        }*/

public class ImageChooserUtil {

    private static final String IMAGE_DIRECTORY = "Images";
    private static final String CAPTURE_IMAGE_FILE_PROVIDER = ".fileprovider";
    private static String FILE_EXTENSION = ".png";
    public int REQUEST_GALLERY = 1235;
    public int REQUEST_CAMERA = 1234;
    public int PERMISSION_CAMERA = 1236;
    public int PERMISSION_WRITE_STORAGE = 1237;
    private View targetView;

    public ImageChooserUtil(int REQUEST_GALLERY, int REQUEST_CAMERA, int PERMISSION_CAMERA, int PERMISSION_WRITE_STORAGE) {
        this.REQUEST_GALLERY = REQUEST_GALLERY;
        this.REQUEST_CAMERA = REQUEST_CAMERA;
        this.PERMISSION_CAMERA = PERMISSION_CAMERA;
        this.PERMISSION_WRITE_STORAGE = PERMISSION_WRITE_STORAGE;
    }

    public void setTargetView(View targetView) {
        this.targetView = targetView;
    }

    public void openChooserDialog(final Activity activity, final String fileName) {
        openChooserDialog(activity, fileName, null);
    }

    /**
     * @param fileName   keep file name in field. this will be required when getting permission.
     * @param targetView it will trigger in an animation on device above lollipop.
     */
    public void openChooserDialog(final Activity activity, final String fileName, View targetView) {
        if (PermissionUtil.checkPermission(activity, PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE)) {
            if (targetView == null) targetView = this.targetView;
            showDialog(activity, targetView, fileName);
        } else {
            PermissionUtil.getPermission(activity,
                    PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE,
                    PERMISSION_WRITE_STORAGE,
                    PermissionUtil.PermissionMessage.WRITE_EXTERNAL_STORAGE,
                    null);
        }
    }

    private void showDialog(Activity activity, View targetView, String fileName) {
        final View dialogView = View.inflate(activity, R.layout.image_chooser_dialog, null);
        AppCompatDialog appCompatDialog = new AppCompatDialog(activity);
        appCompatDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        appCompatDialog.setContentView(dialogView);

        Window window = appCompatDialog.getWindow();
        if (window != null) {
            window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        if (targetView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
            if (targetView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

    public void openChooserDialog(@NonNull Fragment fragment, final String fileName) {
        openChooserDialog(fragment, fileName, null);
    }

    /**
     * @param fileName   keep file name in field. this will be required when getting permission.
     * @param targetView it will trigger in an animation on device above lollipop.
     */
    public void openChooserDialog(@NonNull Fragment fragment, final String fileName, View targetView) {
        if (PermissionUtil.checkPermission(fragment.getContext(), PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE)) {
            if (targetView == null) targetView = this.targetView;
            showDialog(fragment, fileName, targetView);
        } else {
            PermissionUtil.getPermission(fragment,
                    PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE,
                    PERMISSION_WRITE_STORAGE,
                    PermissionUtil.PermissionMessage.WRITE_EXTERNAL_STORAGE,
                    null);
        }
    }

    private void showDialog(Fragment fragment, String fileName, View targetView) {
        final View dialogView = View.inflate(fragment.getContext(), R.layout.image_chooser_dialog, null);
        AppCompatDialog appCompatDialog = new AppCompatDialog(fragment.getContext());
        appCompatDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        appCompatDialog.setContentView(dialogView);

        Window window = appCompatDialog.getWindow();
        if (window != null) {
            window.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        if (targetView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
            if (targetView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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
    private void revealShow(View dialogView, boolean show, AppCompatDialog dialog, View targetView) {

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

    private void startGalleryIntent(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        activity.startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY);
    }

    private void startGalleryIntent(Fragment fragment) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        fragment.startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY);
    }

    public void startCameraIntent(Activity activity, String fileName) {
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

    public void startCameraIntent(Fragment fragment, String fileName) {
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

    public void saveImageTask(Intent data, int requestCode, String fileName, SaveImageTask.FileSaveListener fileSaveListener) {
        new SaveImageTask(
                data,
                requestCode,
                fileName,
                fileSaveListener
                , REQUEST_GALLERY, REQUEST_CAMERA, FILE_EXTENSION, IMAGE_DIRECTORY
        ).execute();
    }

    public File saveImageToStorage(Bitmap finalBitmap, String path, String imageName) {
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

    public File saveImageToExternalStorage(Bitmap finalBitmap, String path, String imageName, String fileExtension) {
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
}
