package com.support.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.support.base.CoreApp;

import java.io.File;

/* Usage:

     1. write permission and feature in manifest

    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

     2. calling code

      FileChooserUtil.openChooserDialog(coreFragment);

     3. Add onActivityResult

        @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FileChooserUtil.FILE_CHOOSER:
                 if (resultCode == Activity.RESULT_OK) {
                    File fileFromStorage = FileChooserUtil.getFileFromStorage(getContext(), data);
                    if (FileChooserUtil.getFileSize(fileFromStorage) <= FileChooserUtil.MB) {
                        SnackBarUtil.showSnackBar(getView(), fileFromStorage.getName());
                    }
                }
                  break;
        }
    }

      4. Add onRequestPermissionsResult

      @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case FileChooserUtil.PERMISSION_WRITE_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager
                        .PERMISSION_GRANTED) {
                    FileChooserUtil.openChooserDialog(getCoreFragment());
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

      */

public class FileChooserUtil {

    public static final int FILE_CHOOSER = 1238;
    public static final int PERMISSION_WRITE_STORAGE = 1239;
    public static final int MB = 1024;

    public static void openChooserDialog(final Activity activity) {
        if (PermissionUtil.checkPermission(activity, PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE)) {
            activity.startActivityForResult(Intent.createChooser(setupIntent(), "Select File"), FILE_CHOOSER);
        } else {
            PermissionUtil.getPermission(activity,
                    PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE,
                    PERMISSION_WRITE_STORAGE,
                    PermissionUtil.PermissionMessage.WRITE_EXTERNAL_STORAGE,
                    null);
        }
    }

    public static String getFileExtension(String fileLink) {

        String extension;
        Uri uri = Uri.parse(fileLink);
        String scheme = uri.getScheme();
        if (scheme != null && scheme.equals(ContentResolver.SCHEME_CONTENT)) {
            MimeTypeMap mime = MimeTypeMap.getSingleton();
            extension = mime.getExtensionFromMimeType(CoreApp.getInstance().getContentResolver().getType(uri));
        } else {
            extension = MimeTypeMap.getFileExtensionFromUrl(fileLink);
        }

        return extension;
    }

    public static String getMimeType(String fileLink) {
        String type = CoreApp.getInstance().getContentResolver().getType(Uri.parse(fileLink));
        if (!TextUtils.isEmpty(type)) return type;
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getMimeTypeFromExtension(FileChooserUtil.getFileExtension(fileLink));
    }

    public static String getFileName(String fileLink) {
        String lastPathSegment = Uri.parse(fileLink).getLastPathSegment();
        return lastPathSegment.substring(0, lastPathSegment.lastIndexOf(".") - 1);
    }

    public static void openChooserDialog(final Fragment fragment) {
        if (PermissionUtil.checkPermission(fragment.getContext(), PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE)) {
            fragment.startActivityForResult(Intent.createChooser(setupIntent(), "Select File"), FILE_CHOOSER);
        } else {
            PermissionUtil.getPermission(fragment,
                    PermissionUtil.Permissions.WRITE_EXTERNAL_STORAGE,
                    PERMISSION_WRITE_STORAGE,
                    PermissionUtil.PermissionMessage.WRITE_EXTERNAL_STORAGE,
                    null);
        }
    }

    private static Intent setupIntent() {
        Intent intent = new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
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
    private static double getFolderSize(File f) {
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


    public static File getFileFromStorage(Intent data) {
        if (data != null) {
            Uri uri = data.getData();
            String path = getPath(uri);
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
     * @param uri The Uri to query.
     */
    @SuppressLint("NewApi")
    private static String getPath(final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(CoreApp.getInstance(), uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(contentUri, null, null);
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

                return getDataColumn(contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(uri, null, null);
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
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Uri uri, String selection,
                                        String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = CoreApp.getInstance().getContentResolver().query(uri, projection, selection, selectionArgs,
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
    private static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
