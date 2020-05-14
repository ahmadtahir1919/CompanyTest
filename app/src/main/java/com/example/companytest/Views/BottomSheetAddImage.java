package com.example.companytest.Views;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.example.companytest.MyApplication;
import com.example.companytest.R;
import com.example.companytest.RxBus;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


import static android.app.Activity.RESULT_OK;


public class BottomSheetAddImage extends BottomSheetDialogFragment {

    @BindView(R.id.gallery)
    TextView gallery;
    @BindView(R.id.camera)
    TextView camera;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    Unbinder unbinder;

    PermissionListener cameraPermission;
    PermissionListener galleryPermission;
    File pictureSaveFolderPath;
    File outputImageFile;
    String currentPhotoPath;
    private static final int TAKE_PICTURE_FROM_CAMERA = 111;
    private static final int PICK_PICTURE_FROM_GALLERY = 222;
    private RxBus rxBus;
    public BottomSheetAddImage() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, R.style.AppBottomSheetDialogTheme);
      /*  if (getArguments() != null) {
            Bundle bundle = getArguments();
            isBottomSheetOpen = bundle.getBoolean("is_open");
        }*/
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.bottom_sheet_update_profile_image, container, false);
        unbinder = ButterKnife.bind(this, view);
        RxJavaInit();
        checkPermissions();

        clickListners();
        return view;
    }

    private void RxJavaInit() {
        rxBus = ((MyApplication) getContext().getApplicationContext()).getRxBus();
    }

    private void checkPermissions() {
        galleryPermission = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, PICK_PICTURE_FROM_GALLERY);
                MainActivity.isBottomSheetOpen=true;

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                MainActivity.isBottomSheetOpen=true;

            }
        };

        cameraPermission = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                MainActivity.isBottomSheetOpen=true;

                dispatchTakePictureIntent();

            }

            @Override
            public void onPermissionDenied(List<String> deniedPermissions) {
                MainActivity.isBottomSheetOpen=true;

            }
        };
    }

    private void clickListners() {
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isBottomSheetOpen=true;

                if (getContext() != null)
                    TedPermission.with(getContext())
                            .setPermissionListener(cameraPermission)
                            .setDeniedMessage(R.string.permission_denied)
                            .setPermissions(Manifest.permission.CAMERA)
                            .check();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.isBottomSheetOpen=true;

                TedPermission.with(getContext())
                        .setPermissionListener(galleryPermission)
                        .setDeniedMessage(R.string.permission_denied)
                        .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .check();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_PICTURE_FROM_CAMERA) {
                File f = new File(currentPhotoPath);
                rxBus.sendUserImage(f);
                dismiss();
                MainActivity.isBottomSheetOpen=false;

            }

            if (requestCode == PICK_PICTURE_FROM_GALLERY) {
                saveImageFromGalary(data);
            }
        }
    }

    private void saveImageFromGalary(Intent data) {
        if (data.getData() != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            if (getContext() != null) {
                Cursor cursor = getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    final String picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    final File selectedFile = new File(picturePath);
                    rxBus.sendUserImage(selectedFile);
                    dismiss();
                    MainActivity.isBottomSheetOpen=false;
                }
            }
        }
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {

            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.example.companytest.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, TAKE_PICTURE_FROM_CAMERA);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

}

