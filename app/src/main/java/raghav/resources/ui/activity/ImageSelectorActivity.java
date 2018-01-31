package raghav.resources.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;

import com.support.base.CoreActivity;
import com.support.utils.GlideApp;
import com.support.utils.MultiImageChooserUtil;

import java.io.File;

import raghav.resources.R;

public class ImageSelectorActivity extends CoreActivity<ImageSelectorActivity> {
    private MultiImageChooserUtil firstImageChooser, secondImageChooser, thirdImageChooser;
    private AppCompatImageView imageView, imageView2, imageView3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setDefaults(this, R.layout.activity_image_selector);
    }

    @Override
    public void createReference() {
        imageView = findViewById(R.id.imageView);
        imageView2 = findViewById(R.id.imageView2);
        imageView3 = findViewById(R.id.imageView3);
    }

    @Override
    protected void setListeners(boolean state) {

    }

    public void firstImageClick(View view) {
        if (firstImageChooser == null) firstImageChooser = MultiImageChooserUtil.getInstance(1001);
        firstImageChooser.openChooserDialog(this, "firstImage");
    }

    public void secondImageClick(View view) {
        if (secondImageChooser == null)
            secondImageChooser = MultiImageChooserUtil.getInstance(1003);
        secondImageChooser.openChooserDialog(this, "secondImage");
    }

    public void thirdImageClick(View view) {
        if (thirdImageChooser == null) thirdImageChooser = MultiImageChooserUtil.getInstance(1005);
        thirdImageChooser.openChooserDialog(this, "thirdImage");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (firstImageChooser != null && firstImageChooser.resolveOnRequestPermissionResult(requestCode, permissions, grantResults, this)) {
        } else if (secondImageChooser != null && secondImageChooser.resolveOnRequestPermissionResult(requestCode, permissions, grantResults, this)) {
        } else if (thirdImageChooser != null && thirdImageChooser.resolveOnRequestPermissionResult(requestCode, permissions, grantResults, this)) {
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (firstImageChooser != null && firstImageChooser.resolveOnActivityResult(requestCode, resultCode, data, new MultiImageChooserUtil.FileSaveListener() {
            @Override
            public void fileSaved(File file) {
                GlideApp.with(ImageSelectorActivity.this).load(file).centerCrop().into(imageView);
            }
        })) {
        } else if (secondImageChooser != null && secondImageChooser.resolveOnActivityResult(requestCode, resultCode, data, new MultiImageChooserUtil.FileSaveListener() {
            @Override
            public void fileSaved(File file) {
                GlideApp.with(ImageSelectorActivity.this).load(file).centerCrop().into(imageView2);
            }
        })) {
        } else if (thirdImageChooser != null && thirdImageChooser.resolveOnActivityResult(requestCode, resultCode, data, new MultiImageChooserUtil.FileSaveListener() {
            @Override
            public void fileSaved(File file) {
                GlideApp.with(ImageSelectorActivity.this).load(file).centerCrop().into(imageView3);
            }
        })) {
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
