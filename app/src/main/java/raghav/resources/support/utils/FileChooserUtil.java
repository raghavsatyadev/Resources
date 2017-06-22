package raghav.resources.support.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
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

    public static File getFileFromStorage(Intent data, ContentResolver resolver, String fileName) {
        // TODO: 23-06-2017 Add File Picker Code from Naviiti , update comments
        if (data != null) {
            return null;
        } else {
            return null;
        }
    }
}
