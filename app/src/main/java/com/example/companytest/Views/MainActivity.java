package com.example.companytest.Views;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.companytest.MyApplication;
import com.example.companytest.R;
import com.example.companytest.RxBus;
import com.example.companytest.adapter.ImagesAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.logout_fab)
    FloatingActionButton logoutFab;
    @BindView(R.id.add_imgage_fab)
    FloatingActionButton addImgageFab;
    @BindView(R.id.recy_image)
    RecyclerView recyImage;
    @BindView(R.id.txt_no_image_found)
    AppCompatTextView txtNoImageFound;

    RxBus rxBus;
    List<File> allFiles;
    String userName = "dummy";
    ImagesAdapter adapter;
    public static boolean isBottomSheetOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        getIntentValues();
        checkDirectoryExistOrNot();
        getImagesIfExist();
        initilizedAdapter();
        AdapterListner();
        checkAnyImageExistOrNot();
        initilizedRxBus();
        ClickListners();

    }

    private void ClickListners() {
        addImgageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openBottomSheet();
            }
        });
        logoutFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                finish();
            }
        });
        rxBus.observeUserImage().subscribe(new Observer<File>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(File file) {

                try {
                    isBottomSheetOpen=false;
                    checkDirectoryExistOrNot();
                    File defaultFile = createFile();
                    copyFile(file.getAbsolutePath(), defaultFile.getAbsolutePath());
                    showList(View.VISIBLE, View.GONE);
                    getAllDataAndNotifyAdapter();
                    smoothScrollToLastItemPosition();
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void smoothScrollToLastItemPosition() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                recyImage.smoothScrollToPosition(allFiles.size() - 1);

            }
        }, 1300);
    }

    private void getAllDataAndNotifyAdapter() {
        allFiles.clear();
        allFiles = getListFiles(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tempdatafile"));
        adapter.notifyDataSetChanged();
    }

    private void showList(int visible, int gone) {
        recyImage.setVisibility(visible);
        txtNoImageFound.setVisibility(gone);
    }

    private File createFile() {
        String dateAsString = null;
        SimpleDateFormat simpleDateFormat =
                new SimpleDateFormat("MMddhhmmss");
        dateAsString = simpleDateFormat.format(new Date());

        File defaultFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tempdatafile/" + userName + "_" + dateAsString + ".jpg");

        if (!defaultFile.exists()) {
            try {
                defaultFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return defaultFile;
    }

    private void initilizedRxBus() {
        rxBus = ((MyApplication) getApplicationContext()).getRxBus();
    }

    private void AdapterListner() {
        adapter.setonItemDelete(new ImagesAdapter.OnItemDelete() {
            @Override
            public void onItemDelete(int position, File file) {
                showAlert(position, file);
            }
        });
    }

    private void checkAnyImageExistOrNot() {
        if (allFiles.size() > 0) {
            showList(View.VISIBLE, View.GONE);


        } else {
            showList(View.GONE, View.VISIBLE);
        }
    }

    private void showAlert(int position, File file) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setTitle(R.string.alert);
        builder.setMessage(R.string.alert_des);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.confirm,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean deleted = file.delete();

                        allFiles.remove(position);
                        adapter.notifyDataSetChanged();
                        if (allFiles.size() == 0) {
                            showList(View.GONE, View.VISIBLE);
                        }

                        if (deleted) {
                            Toast.makeText(MainActivity.this, R.string.delete_success, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, R.string.image_not_delete, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void initilizedAdapter() {
        adapter = new ImagesAdapter(MainActivity.this, allFiles);
        recyImage.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyImage.setAdapter(adapter);
    }

    private void getImagesIfExist() {
        allFiles = new ArrayList<>();

        allFiles = getListFiles(new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tempdatafile"));
    }

    private void getIntentValues() {
        if (getIntent() != null) {
            if (getIntent().hasExtra("User")) {
                userName = getIntent().getStringExtra("User");
            }
        }
    }

    private void checkDirectoryExistOrNot() {
        File defaultFile2 = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/tempdatafile");

        if (!defaultFile2.exists())
            defaultFile2.mkdirs();
    }


    public static void copyFile(String inputPath, String outputPath) {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = new FileInputStream(inputPath);
            out = new FileOutputStream(outputPath);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;
            out.flush();
            out.close();
            out = null;
        } catch (FileNotFoundException fnfe1) {
            Log.d("e", fnfe1.getMessage());
        } catch (Exception e) {
        }
    }

    private List<File> getListFiles(File parentDir) {
        File[] files = parentDir.listFiles();
        List<String> nameList = new ArrayList<>();
        try {
            for (File file : files) {
                if (getUserFileName(file.getName()).equals(userName)) {
                    nameList.add(file.getName());
                    Log.e("FileName", file.getName());
                    allFiles.add(file);
                }
            }
        } catch (Exception e) {
            Log.e("TAG", e.toString());
        }

        return allFiles;
    }

    public void openBottomSheet() {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();


        BottomSheetAddImage bottomSheetAddImage = new BottomSheetAddImage();
        bottomSheetAddImage.show(transaction, "");
       // isBottomSheetOpen=true;

    }

    public static String getUserFileName(String fileName) {

        String userName = null;

        try {
            userName = fileName.substring(0, fileName.indexOf("_"));
            // System.out.println(new Date() + ": " + "Corporate:
            // "+userName);
            return userName;
        } catch (Exception e) {
            userName = null;
            e.printStackTrace();
        }

        return userName;
    }

    @Override
    protected void onPause() {
        super.onPause();

        //For privacy application close when user resume the App
        if (!isBottomSheetOpen)
            finish();
    }

}
