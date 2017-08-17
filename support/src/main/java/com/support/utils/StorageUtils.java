package com.support.utils;

import android.os.Environment;
import android.support.annotation.NonNull;

import com.support.base.CoreApp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class StorageUtils {

    public static String createExternalDirectory() {
        String state = Environment.getExternalStorageState();
        String baseDir = null;
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            File baseDirFile = CoreApp.getInstance().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            if (baseDirFile != null) {
                baseDir = baseDirFile.getAbsolutePath();
            }
        }
        if (baseDir == null) {
            baseDir = CoreApp.getInstance().getFilesDir().getAbsolutePath();
        }
        return baseDir;
    }

    public static String createInternalDirectory() {
        return CoreApp.getInstance().getFilesDir().getAbsolutePath();
    }

    public static File createFile(String fileName) {
        return createFile(null, fileName);
    }

    public static File createFile(String path, @NonNull String fileName) {
        String root = createExternalDirectory();
        File myDir;
        if (path != null && !path.isEmpty()) {
            myDir = new File(root, path);
        } else {
            myDir = new File(root);
        }
        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        File file = null;

        if (!fileName.isEmpty()) {
            file = new File(myDir, fileName);
            if (file.exists()) {
                file.delete();
            }
            try {
                file.createNewFile();
            } catch (IOException pE) {
                pE.printStackTrace();
            }
        }
        return file;
    }


    public static void writeStringToFile(String string, File file) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(string.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readStringFromFile(File file) {
        //Read text from file
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;

            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException e) {
            //You'll need to add proper error handling here
        }

        return text.toString();
    }


    public static void writeObjectToFile(Object object, File file) {
        ObjectOutputStream oos = null;
        try {
            FileOutputStream fos = new FileOutputStream(file);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (oos != null) {
                    oos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static <T> T readObjectFromFile(File file) {
        ObjectInputStream ois = null;
        T object = null;
        try {
            FileInputStream fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            object = (T) ois.readObject();
        } catch (ClassNotFoundException | IOException e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return object;
    }
}
