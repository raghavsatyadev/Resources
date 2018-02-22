package com.support.utils.image_chooser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;

import com.support.base.CoreApp;
import com.support.utils.AppLog;
import com.support.utils.FileChooserUtil;
import com.support.utils.StorageUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class SaveImageTask extends AsyncTask<Void, Void, File> {

    private Intent data;
    private int requestCode;
    private String fileName;
    private FileSaveListener listener;
    private int REQUEST_GALLERY;
    private int REQUEST_CAMERA;
    private String FILE_EXTENSION;
    private String IMAGE_DIRECTORY;

    public SaveImageTask(Intent data,
                         int requestCode,
                         String fileName,
                         FileSaveListener listener, int REQUEST_GALLERY, int REQUEST_CAMERA, String fileExtension, String imageDirectory) {
        this.data = data;
        this.requestCode = requestCode;
        this.fileName = fileName;
        this.listener = listener;
        this.REQUEST_GALLERY = REQUEST_GALLERY;
        this.REQUEST_CAMERA = REQUEST_CAMERA;
        this.FILE_EXTENSION = fileExtension;
        this.IMAGE_DIRECTORY = imageDirectory;
    }

    @Override
    protected File doInBackground(Void... pObjects) {
        File file = null;
        if (requestCode == this.REQUEST_GALLERY) {
            file = getGalleryImageFile(data,
                    String.valueOf(fileName));
        } else if (requestCode == REQUEST_CAMERA) {
            file = getCameraImageFile(String.valueOf(fileName));
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

    public File getCameraImageFile(String fileName) {
        File path = new File(StorageUtils.createInternalDirectory(), IMAGE_DIRECTORY);
        if (!path.exists()) path.mkdirs();
        return new File(path, fileName + FILE_EXTENSION);
    }

    public File getGalleryImageFile(Intent data, String fileName) {
        if (data != null) {
            return saveImageToStorage(
                    data.getData(),
                    IMAGE_DIRECTORY,
                    fileName);
        } else {
            return null;
        }
    }

    private File saveImageToStorage(Uri uri, String imageDirectory, String fileName) {
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
}