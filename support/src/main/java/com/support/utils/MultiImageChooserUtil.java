package com.support.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;

import com.support.base.CoreApp;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class MultiImageChooserUtil {

    public final int REQUEST_GALLERY;
    public final int REQUEST_CAMERA;
    public final int PERMISSION_CAMERA;
    public final int PERMISSION_WRITE_STORAGE;
    private final String IMAGE_DIRECTORY = "Images";
    private final String CAPTURE_IMAGE_FILE_PROVIDER = ".fileprovider";
    private String FILE_EXTENSION = ".jpg";
    private String fileName;

    private MultiImageChooserUtil(int requestCode) {
        if (requestCode != 0) {
            REQUEST_CAMERA = requestCode + 1;
            REQUEST_GALLERY = requestCode;
            PERMISSION_CAMERA = requestCode + 1;
            PERMISSION_WRITE_STORAGE = requestCode;
        } else {
            REQUEST_GALLERY = 1233;
            REQUEST_CAMERA = 1234;
            PERMISSION_CAMERA = 1236;
            PERMISSION_WRITE_STORAGE = 1237;
        }
    }

    public static MultiImageChooserUtil getInstance(int requestCode) {
        return new MultiImageChooserUtil(requestCode);
    }

    public void openChooserDialog(final Activity activity) {
        if (!TextUtils.isEmpty(fileName)) {
            openChooserDialog(activity, fileName);
        } else
            throw new RuntimeException("you have to call openChooserDialog(final Activity activity, final String fileName) first");
    }

    /**
     * @param fileName keep file name in field. this will be required when getting permission.
     */
    public void openChooserDialog(final Activity activity, final String fileName) {
        this.fileName = fileName;

        if (PermissionUtil.checkPermission(activity, PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("Choose Image")
                    .setPositiveButton("Camera", (dialog, id) -> {
                        if (PermissionUtil.checkPermission(activity, PermissionUtil.Permissions.CAMERA)) {
                            startCameraIntent(activity);
                        } else {
                            PermissionUtil.getPermission(activity,
                                    PermissionUtil.Permissions.CAMERA,
                                    PERMISSION_CAMERA,
                                    PermissionUtil.PermissionMessage.CAMERA,
                                    null);
                        }
                    })
                    .setNegativeButton("Gallery", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            startGalleryIntent(activity);
                        }
                    })
                    .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

            AlertDialog dialog = builder.create();

            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            builder.show();
        } else {
            PermissionUtil.getPermission(activity,
                    PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE,
                    PERMISSION_WRITE_STORAGE,
                    PermissionUtil.PermissionMessage.WRITE_EXTERNAL_STORAGE,
                    null);
        }
    }

    public void openChooserDialog(final Fragment fragment) {
        if (!TextUtils.isEmpty(fileName)) {
            openChooserDialog(fragment, fileName);
        } else
            throw new RuntimeException("you have to call openChooserDialog(final Fragment fragment, final String fileName) first");
    }

    /**
     * @param fileName keep file name in field. this will be required when getting permission.
     */
    public void openChooserDialog(final Fragment fragment, final String fileName) {
        this.fileName = fileName;

        if (PermissionUtil.checkPermission(fragment.getContext(), PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(fragment.getContext());

            builder.setTitle("Choose Image")
                    .setPositiveButton("Camera", (dialog, id) -> {
                        if (PermissionUtil.checkPermission(fragment.getContext(), PermissionUtil.Permissions.CAMERA)) {
                            startCameraIntent(fragment);
                        } else {
                            PermissionUtil.getPermission(fragment,
                                    PermissionUtil.Permissions.CAMERA,
                                    PERMISSION_CAMERA,
                                    PermissionUtil.PermissionMessage.CAMERA,
                                    null);
                        }
                    })
                    .setNegativeButton("Gallery", (dialog, id) -> startGalleryIntent(fragment))
                    .setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());

            AlertDialog dialog = builder.create();

            dialog.setCanceledOnTouchOutside(true);
            dialog.setCancelable(true);
            builder.show();
        } else {
            PermissionUtil.getPermission(fragment,
                    PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE,
                    PERMISSION_WRITE_STORAGE,
                    PermissionUtil.PermissionMessage.WRITE_EXTERNAL_STORAGE,
                    null);
        }
    }

    private void startGalleryIntent(Activity activity) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY);
    }

    private void startGalleryIntent(Fragment fragment) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        fragment.startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_GALLERY);
    }

    @SuppressLint("ObsoleteSdkInt")
    public void startCameraIntent(Activity activity) {
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

    @SuppressLint("ObsoleteSdkInt")
    public void startCameraIntent(Fragment fragment) {
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

    private File getGalleryImageFile(Intent data, ContentResolver resolver) {
        if (data != null) {
            try {
                return saveImageToStorage(
                        MediaStore.Images.Media.getBitmap(resolver, data.getData()),
                        IMAGE_DIRECTORY,
                        fileName);
            } catch (IOException e) {
                AppLog.log(false, "MultiImageChooserUtil " + "getGalleryImageFile: ", e);
                return null;
            }
        } else {
            return null;
        }
    }

    private File getCameraImageFile() {
        File path = new File(StorageUtils.createInternalDirectory(), IMAGE_DIRECTORY);
        if (!path.exists()) path.mkdirs();
        return new File(path, fileName + FILE_EXTENSION);
    }

    private File saveImageToStorage(Bitmap finalBitmap, String path, String imageName) {
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
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            return file;
        } catch (Exception e) {
            AppLog.log(false, "MultiImageChooserUtil " + "saveImageToStorage: ", e);
            return file;
        }
    }

    public boolean resolveOnActivityResult(int requestCode, int resultCode, Intent data, FileSaveListener fileSaveListener) {
        if (requestCode == REQUEST_CAMERA || requestCode == REQUEST_GALLERY) {
            if (resultCode == RESULT_OK) {
                saveImage(data, requestCode, fileSaveListener);
            }
            return true;
        } else {
            return false;
        }
    }

    public boolean resolveOnRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, Activity activity) {

        if (PERMISSION_WRITE_STORAGE == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager
                    .PERMISSION_GRANTED) {
                openChooserDialog(activity);
            }
            return true;
        } else if (REQUEST_CAMERA == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager
                    .PERMISSION_GRANTED) {
                startCameraIntent(activity);
            }
            return true;
        } else return false;
    }

    public boolean resolveOnRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults, Fragment fragment) {

        if (PERMISSION_WRITE_STORAGE == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager
                    .PERMISSION_GRANTED) {
                openChooserDialog(fragment);
            }
            return true;
        } else if (REQUEST_CAMERA == requestCode) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager
                    .PERMISSION_GRANTED) {
                startCameraIntent(fragment);
            }
            return true;
        } else return false;
    }

    public void saveImage(Intent data, int requestCode,
                          FileSaveListener listener) {
        new SaveImageTask(data, requestCode, listener).execute();
    }

    public interface FileSaveListener {
        void fileSaved(File file);
    }

    public class SaveImageTask extends AsyncTask<Void, Void, File> {

        private Intent data;
        private int requestCode;
        private FileSaveListener listener;

        public SaveImageTask(Intent data,
                             int requestCode, FileSaveListener listener) {
            this.data = data;
            this.requestCode = requestCode;
            this.listener = listener;
        }

        @Override
        protected File doInBackground(Void... pObjects) {
            File file = null;
            if (requestCode == REQUEST_GALLERY) {
                file = getGalleryImageFile(data,
                        CoreApp.getInstance().getContentResolver()
                );
            } else if (requestCode == REQUEST_CAMERA) {
                file = getCameraImageFile();
            }

            return file;
        }

        @Override
        protected void onPostExecute(File file) {
            if (listener != null)
                listener.fileSaved(file);
            super.onPostExecute(file);
        }

    }
}
