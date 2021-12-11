package com.uqac_8inf865.sysi;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.camera2.Camera2Config;
import androidx.camera.core.CameraXConfig;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;

public class PopupAddSpot extends Dialog implements CameraXConfig.Provider,
        Serializable, ActivityCompat.OnRequestPermissionsResultCallback {

    private final static String TAG = "PopupAddSpot";

    private Activity activity;
    private PopupAddSpot popupAddSpot;

    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 10;

    //fields
    private Button addBtn;
    private ImageButton cancelBtn, imageSpot;
    private Spinner spinner;
    private TextView titleTextView, descriptionTextView;

    private LatLng latLng;

    private List<Category> categoryList;

    // constructor
    public PopupAddSpot(Activity activity, LatLng latLng)
    {
        super(activity, R.style.Theme_AppCompat_DayNight_Dialog);
        setContentView(R.layout.activity_popup_add_spot);

        this.activity = activity;
        this.popupAddSpot = this;

        this.latLng = latLng;

        this.addBtn = findViewById(R.id.addButton);
        this.cancelBtn = findViewById(R.id.cancelButton);

        this.imageSpot = findViewById(R.id.imageButtonSpot);
        this.titleTextView = findViewById(R.id.entertitle);
        this.descriptionTextView = findViewById(R.id.enterdescrip);

        this.spinner = findViewById(R.id.action_bar_spinner);
        this.categoryList = CategoryDataUtils.getCategories();
        CategoryAdapter categoryAdapter = new CategoryAdapter(activity,
                CategoryDataUtils.getCategories());
        this.spinner.setAdapter(categoryAdapter);

        this.imageSpot.setOnClickListener(view -> {
            checkPermission(activity);
            popupAddSpot.dismiss();
        });
    }

    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    public ImageButton getNoButton() {
        return cancelBtn;
    }

    public Button getYesButton() {
        return addBtn;
    }

    public TextView getTitleTextView() {return titleTextView;}

    public String getTitle(){return titleTextView.getText().toString();}

    public String getDescription(){return descriptionTextView.getText().toString();}

    public TextView getDescriptionTextView() {return descriptionTextView;}

    public Spinner getSpinner() {return spinner;}

    public String getCategory(){
        Category category = (Category) spinner.getSelectedItem();
        return category.getName();
    }

    public ImageButton getImageSpot() {return imageSpot;}

    public void build()
    {
        show();
    }

    public void clear(){
        titleTextView.setText(null);
        descriptionTextView.setText(null);
        spinner.setSelection(0);
    }

    private void checkPermission(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (getFromPref(getContext(), ALLOW_KEY)) {
                showSettingsAlert();
            } else if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity,
                        Manifest.permission.CAMERA)) {
                    showAlert();
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
            }
        } else {
            Bundle bundle = new Bundle();
            bundle.putString("title", titleTextView.getText().toString());
            bundle.putString("description", descriptionTextView.getText().toString());
            bundle.putInt("category", spinner.getSelectedItemPosition());
            bundle.putParcelable("coordinate", this.latLng);
            Fragment fragment = new CameraFragment();
            fragment.setArguments(bundle);
            ((AppCompatActivity) activity).getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .addToBackStack(null)
                    .replace(R.id.fragmentContainerView, fragment, fragment.getClass().getSimpleName())
                    .commit();
        }
    }

    @NonNull
    @Override
    public CameraXConfig getCameraXConfig() {
        return Camera2Config.defaultConfig();
    }

    public static void saveToPreferences(Context context, String key,
                                         Boolean allowed) {
        SharedPreferences myPrefs = context.getSharedPreferences
                (CAMERA_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, allowed);
        prefsEditor.commit();
    }

    public static Boolean getFromPref(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences
                (CAMERA_PREF, Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }

    private void showAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);

                    }
                });
        alertDialog.show();
    }

    private void showSettingsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getContext()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startInstalledAppDetailsActivity(activity);
                    }
                });
        alertDialog.show();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale =
                                ActivityCompat.shouldShowRequestPermissionRationale
                                        (activity, permission);
                        if (showRationale) {
                            showAlert();
                        } else if (!showRationale) {
                            // user denied flagging NEVER ASK AGAIN
                            // you can either enable some fall back,
                            // disable features of your app
                            // or open another dialog explaining
                            // again the permission and directing to
                            // the app setting
                            saveToPreferences(getContext(), ALLOW_KEY, true);
                        }
                    }
                }
            }

            // other 'case' lines to check for other
            // permissions this app might request

        }
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }
}