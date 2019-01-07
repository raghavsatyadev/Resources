package com.support.utils;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.Collections;
import java.util.List;


public class FileUtil {
    private static final int DEFAULT_BUFFER_SIZE = 1024 * 4;

    // Make this class non-instantiable
    private FileUtil() {
        throw new UnsupportedOperationException();
    }

    /**
     * Utility function for getting the app's external storage directory.
     *
     * @param context The current context.
     * @return The app's external storage directory.
     */
    @Nullable
    public static File getExternalAppDir(@NonNull Context context) {
        File externalFilesDir = context.getExternalFilesDir(null);
        return (externalFilesDir != null ? externalFilesDir.getParentFile() : null);
    }

    @Nullable
    public static File getInternalDirectory(@NonNull Context context) {
        // TODO: 19/5/17 change to external directory
        return context.getFilesDir();
    }

    /**
     * Returns the text of a file as a String object
     *
     * @param context  The current context
     * @param fileName The name of the file to load from assets folder
     * @return The text content of the file
     */
    public static String loadTextFileFromAssets
    (Context context, String fileName) throws IOException {
        InputStream inputStream = context.getAssets().open(fileName);
        try {
            OutputStream outputStream = new ByteArrayOutputStream();
            try {
                byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                for (int n; (n = inputStream.read(buffer)) >= 0; ) {
                    outputStream.write(buffer, 0, n);
                }
                return outputStream.toString();
            } finally {
                outputStream.close();
            }
        } finally {
            inputStream.close();
        }
    }

    public static File moveFile(File inputFile, File outputFile) {

        FileInputStream in = null;
        FileOutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File dir = new File(outputFile.getAbsolutePath());
            if (!dir.exists()) {
                dir.mkdirs();
            }


            in = new FileInputStream(inputFile);
            out = new FileOutputStream(outputFile, true);

            byte[] buffer = new byte[1024 * 1024 * 5];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
                out.flush();
            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;


            // delete the original file
            inputFile.delete();

            return outputFile;

        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }
        return outputFile;
    }

    /**
     * @param newFilePath only give path till parent directory of file
     * @throws IOException
     */
    public static String moveFile(File oldFile, String newFilePath) throws IOException {
        File newFile = new File(newFilePath);
        FileChannel outputChannel = null;
        FileChannel inputChannel = null;
        try {
            outputChannel = new FileOutputStream(newFile).getChannel();
            inputChannel = new FileInputStream(oldFile).getChannel();
            inputChannel.transferTo(0, inputChannel.size(), outputChannel);
            inputChannel.close();
            oldFile.delete();
            Log.d("FileUtil", "moveFile: file moved");
        } finally {
            Log.d("FileUtil", "moveFile: file moved");
            if (inputChannel != null) inputChannel.close();
            if (outputChannel != null) outputChannel.close();
        }
        return newFile.getAbsolutePath();
    }

    /**
     * Deletes a file or directory and all its content recursively.
     *
     * @param fileOrDirectory The file or directory that needs to be deleted.
     */
    public static void deleteRecursive(@NonNull File fileOrDirectory) {
        deleteRecursive(fileOrDirectory, Collections.EMPTY_LIST);
    }

    /**
     * Deletes a file or directory and all its content recursively.
     *
     * @param fileOrDirectory The file or directory that needs to be deleted.
     * @param exceptions      Names of the files or directories that need to be skipped while deletion.
     */
    public static void deleteRecursive(@NonNull File fileOrDirectory,
                                       @NonNull List<String> exceptions) {
        if (exceptions.contains(fileOrDirectory.getName())) return;

        if (fileOrDirectory.isDirectory()) {
            File[] filesList = fileOrDirectory.listFiles();
            if (filesList != null) {
                for (File child : filesList) {
                    deleteRecursive(child, exceptions);
                }
            }
        }

        // Don't break the recursion upon encountering an error
        // noinspection ResultOfMethodCallIgnored
        fileOrDirectory.delete();
    }

    public static boolean deleteFile(File file) {
        boolean deletedAll = true;
        if (file != null) {
            if (file.isDirectory()) {
                String[] children = file.list();
                for (int i = 0; i < children.length; i++) {
                    deletedAll = deleteFile(new File(file, children[i])) && deletedAll;
                }
            } else {
                deletedAll = file.delete();
            }
        }

        return deletedAll;
    }

    public static String getInterStorageDirectory(Context context) {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static File getDecryptVideoFilePath(Context context, String fileName) {
        File dir = new File(getInterStorageDirectory(context) + "/decv");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File decryptFile = new File(
                dir.getAbsolutePath(), fileName);

        if (decryptFile.exists())
            decryptFile.delete();

        try {
            decryptFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return decryptFile;
    }

    public static boolean deleteDecryptDirectory(Context context) {
        File decryptFile = new File(getInterStorageDirectory(context) + "/decv");
        if (decryptFile.exists())
            return deleteFile(decryptFile);
        return false;
    }

    public static File getDownloadFilePath(Context context, String fileName) {
        File dir = new File(getInterStorageDirectory(context) + "/encv");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File decryptFile = new File(dir.getAbsolutePath(), fileName);

        if (decryptFile.exists())
            decryptFile.delete();

        try {
            decryptFile.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return decryptFile;
    }

    public static File getFileFromSDCard(Context context, Intent data) {
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
     * @param context
     * @param uri     The Uri to query.
     */
    @SuppressLint("NewApi")
    public static String getPath(Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                final String path = split[1];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                } else if (context.getExternalCacheDirs().length > 0) {
                    String[] temp = null;
                    for (File f : context.getExternalCacheDirs()) {
                        if (!f.getAbsolutePath().contains(Environment.getExternalStorageDirectory().getAbsolutePath())) {
                            temp = f.getParent().split("/");
                            break;
                        }
                    }
                    String tempresult = "";
                    if (temp != null) {
                        for (String s : temp) {
                            if (s.equals("Android"))
                                break;
                            if (!android.text.TextUtils.isEmpty(s))
                                tempresult += "/" + s;
                        }
                    }
                    String result = tempresult + "/" + path;
                    File file = new File(result);
                    if (file.exists() && file.canRead()) {
                        return result;
                    }
                } else {
                    String result = System.getenv("SECONDARY_STORAGE") + "/" + path;
                    File file = new File(result);
                    if (file.exists() && file.canRead()) {
                        return result;
                    }
                }

            }// DownloadsProvider
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
        else if ("content".

                equalsIgnoreCase(uri.getScheme()))

        {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".

                equalsIgnoreCase(uri.getScheme()))

        {
            return uri.getPath();
        }

        return null;
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    public static String getPathToNonPrimaryVolume(Context context, String tag) {
        File[] volumes = context.getExternalCacheDirs();
        if (volumes != null) {
            for (File volume : volumes) {
                if (volume != null) {
                    String path = volume.getAbsolutePath();
                    if (path != null) {
                        int index = path.indexOf(tag);
                        if (index != -1) {
                            return path.substring(0, index) + tag;
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    private static String getDataColumn(Context context, Uri uri, String selection,
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
     * @return will return file size in KB
     */
    public static double getFileSize(File file) {
        return getFolderSize(file);
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

