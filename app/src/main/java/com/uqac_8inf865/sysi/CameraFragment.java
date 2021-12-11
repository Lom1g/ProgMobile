package com.uqac_8inf865.sysi;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class CameraFragment extends Fragment {

    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    private PreviewView previewView;
    private ImageButton buttonPicture, buttonValidate, buttonCancel;
    private ImageView imageViewCapture;

    private Context context;

    private Bitmap bitmapImageCapture;
    private ImageCapture imageCapture = null;

    private LatLng latLng;
    private String title, description;
    private int category;

    private static final String TAG = "MainActivity";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.latLng = getArguments().getParcelable("coordinate");
            this.title = getArguments().getString("title");
            this.description = getArguments().getString("description");
            this.category = getArguments().getInt("category");
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        previewView = view.findViewById(R.id.previewView);
        buttonPicture = view.findViewById(R.id.buttonPicture);
        buttonValidate = view.findViewById(R.id.buttonValidate);
        buttonCancel = view.findViewById(R.id.buttonCancel);
        imageViewCapture = view.findViewById(R.id.imageViewCapture);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buttonPicture.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Photo captured", Toast.LENGTH_LONG).show();
            takePhoto();
        });
        buttonCancel.setOnClickListener(v -> showPreview());
        buttonValidate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("title", title);
                bundle.putString("description", description);
                bundle.putInt("category", category);
                bundle.putParcelable("coordinate", latLng);
                bundle.putParcelable("spotPicture", bitmapImageCapture);
                MapsFragment fragment = new MapsFragment();
                fragment.setArguments(bundle);
                requireActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .setReorderingAllowed(true)
                        .addToBackStack(null)
                        .replace(R.id.fragmentContainerView, fragment, fragment.getClass().getSimpleName())
                        .commit();
            }
        });
        startCamera();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private void takePhoto() {
        if (imageCapture == null) {
            return;
        }
        try {
            File photoFile = File.createTempFile("spotPicture", ".png");
            ImageCapture.OutputFileOptions outputFileOptions =
                    new ImageCapture.OutputFileOptions.Builder(photoFile).build();

            imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(context), new ImageCapture.OnImageSavedCallback() {
                @Override
                public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                    try {
                        bitmapImageCapture = rotateImageIfRequired(BitmapFactory.decodeFile(photoFile.getPath()),Uri.fromFile(photoFile));
                        imageViewCapture.setImageBitmap(bitmapImageCapture);
                        showCapturePicture();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                @Override
                public void onError(@NonNull ImageCaptureException exception) {
                    Log.e(TAG, String.valueOf(exception));
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void startCamera() {

        cameraProviderFuture = ProcessCameraProvider.getInstance(context);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(context));
    }

    void bindPreview(ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder()
                .build();

        imageCapture = new ImageCapture.Builder()
                .build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        cameraProvider.unbindAll();
        Camera camera = cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageCapture);
    }

    private void showCapturePicture(){
        imageViewCapture.setVisibility(View.VISIBLE);
        buttonValidate.setVisibility(View.VISIBLE);
        buttonCancel.setVisibility(View.VISIBLE);
        buttonPicture.setVisibility(View.INVISIBLE);
        previewView.setVisibility(View.INVISIBLE);

    }

    private void showPreview(){
        buttonValidate.setVisibility(View.INVISIBLE);
        buttonCancel.setVisibility(View.INVISIBLE);
        buttonPicture.setVisibility(View.VISIBLE);
        previewView.setVisibility(View.VISIBLE);
        imageViewCapture.setVisibility(View.INVISIBLE);
    }

    private static Bitmap rotateImageIfRequired(Bitmap img, Uri selectedImage) throws IOException
    {
        ExifInterface ei = new ExifInterface(selectedImage.getPath());
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                return rotateImage(img, 90);
            case ExifInterface.ORIENTATION_ROTATE_180:
                return rotateImage(img, 180);
            case ExifInterface.ORIENTATION_ROTATE_270:
                return rotateImage(img, 270);
            default:
                return img;
        }
    }

    private static Bitmap rotateImage(Bitmap img, int degree)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(degree);
        Bitmap rotatedImg = Bitmap.createBitmap(img, 0, 0, img.getWidth(), img.getHeight(), matrix, true);
        img.recycle();
        return rotatedImg;
    }
}