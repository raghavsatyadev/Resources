package raghav.resources.support.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;

import java.io.File;



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

     4. add in requesting activity

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            switch (requestCode) {
                case PermissionUtil.PermissionCode.WRITE_EXTERNAL_STORAGE:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager
                            .PERMISSION_GRANTED) {
                        ImageChooserUtil.openChooserDialog(activity, String.valueOf(fileName));
                    }
                    break;
                case PermissionUtil.PermissionCode.CAMERA:
                    if (grantResults.length > 0 && grantResults[0] == PackageManager
                            .PERMISSION_GRANTED) {
                        ImageChooserUtil.startCameraIntent(activity, String.valueOf(fileName));
                    }
                    break;
            }
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

      5. Add onActivityResult

       @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            switch (requestCode) {
                case ImageChooserUtil.FILE_CHOOSER:
                case ImageChooserUtil.REQUEST_CAMERA:
                    if (resultCode == RESULT_OK) {
                        new ImageChooserUtil.SaveImageTask(activity,
                                data,
                                requestCode,
                                String.valueOf(goodsFileName),
                                new ImageChooserUtil.SaveImageTask.FileSaveListener() {
                                    @Override
                                    public void fileSaved(File file) {
                                        goodsPhotoAdapter.addItem(file.getAbsolutePath());
                                    }
                                }).execute();
                    }
                    break;
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

      */

public class FileChooserUtil {

    public static final int FILE_CHOOSER = 1235;

    public static void openChooserDialog(final Activity activity, final String fileName) {
        if (PermissionUtil.checkPermission(activity, PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE)) {
            activity.startActivityForResult(Intent.createChooser(setupIntent(), "Select File"), FILE_CHOOSER);
        } else {
            Toaster.shortToast("No permission to write");
        }
    }

    public static void openChooserDialog(final Fragment fragment, final String fileName) {
        if (PermissionUtil.checkPermission(fragment.getContext(), PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE)) {
            fragment.startActivityForResult(Intent.createChooser(setupIntent(), "Select File"), FILE_CHOOSER);
        } else {
            Toaster.shortToast("No permission to write");
        }
    }

    private static Intent setupIntent() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        return intent;
    }

    /**
     * @return will return file size in KB
     */
    public static double getFileSize(File file) {
        return getFolderSize(file) / 1024;
    }

    /**
     * @return will return file size in Bytes
     */
    public static double getFolderSize(File f) {
        double size = 0;
        if (f.isDirectory()) {
            for (File file : f.listFiles()) {
                size += getFolderSize(file);
            }
        } else {
            size = f.length();
        }
        return size;
    }


    public static File getFileFromStorage(Context context, Intent data) {
        if (data != null) {
            Uri uri = data.getData();
            String path = getPath(context, uri);
            if (path != null) {
                return new File(path);
            } else return null;
        } else {
            return null;
        }
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     */
    @SuppressLint("NewApi")
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
