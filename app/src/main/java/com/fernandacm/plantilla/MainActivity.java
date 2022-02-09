package com.fernandacm.plantilla;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;
import com.tomergoldst.tooltips.ToolTip;
import com.tomergoldst.tooltips.ToolTipsManager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import jp.wasabeef.picasso.transformations.CropSquareTransformation;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import pl.aprilapps.easyphotopicker.EasyImage;

public class MainActivity extends AppCompatActivity implements  AdapterView.OnItemClickListener, EasyPermissions.PermissionCallbacks {
    //LocalBD dbHelper = new LocalBD(this);


    private static final int EXTERNAL_STORAGE_REQUEST_CODE = 4655;
    private static final int LOCATION_REQUEST_CODE = 1651;
    private final static String[] tipos_activos = {"Seleccione tipo de activo", "Electronico", "Mobiliario", "Decoración", "Luminaria", "Electrodomestico", "Libros"};

    @BindView(R.id.ev_image_selected)
    ImageView imageView3;

    @BindView(R.id.edit_text_nombre)
    MaterialEditText editTextNombre;
    @BindView(R.id.frame_activo_nombre)
    FrameLayout frameNombre;

    @BindView(R.id.tipo_activo)
    Spinner spinnerTiposactivos;
    String activo;

    @BindView(R.id.edit_text_activo_desc)
    MaterialEditText editTextActivoDesc;

    @BindView(R.id.edit_text_fecha)
    MaterialEditText editTextFecha;

    @BindView(R.id.rootView)
    ConstraintLayout rootView;
    Bitmap bmap;
    private File selectedImage;
   // LocalBD bdHelper;
    BaseDatos baseDatos;
    private Unbinder unbinder;
    private ToolTipsManager toolTipsManager = new ToolTipsManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
     //   this.bdHelper = new LocalBD(this);
        String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
        unbinder = ButterKnife.bind(this);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, tipos_activos);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spinnerTiposactivos.setAdapter(adapter);
       // spinnerTiposactivos.setOnItemSelectedListener(this);

        spinnerTiposactivos.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id)
            {
                activo = tipos_activos[pos] ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {    }
        });
    }
    @AfterPermissionGranted(EXTERNAL_STORAGE_REQUEST_CODE)
    @OnClick({R.id.ev_image_selected, R.id.floatingActionButton})
    public void onViewClicked(View view) {
        String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (EasyPermissions.hasPermissions(Objects.requireNonNull(this), perms)) {
            EasyImage.openChooserWithGallery(this, "Ingresar nueva imagen.", 0);
        } else {
            EasyPermissions.requestPermissions(this, "Es necesario que nos permita acceder a su cámara para guardar las imágenes.",
                    EXTERNAL_STORAGE_REQUEST_CODE, perms);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Context context= this;
          EasyImage.handleActivityResult(requestCode, resultCode, data, this, new EasyImage.Callbacks() {
                @Override
                public void onImagePickerError(Exception e, EasyImage.ImageSource source, int type) {
                }
                @Override
                public void onImagePicked(File imageFile, EasyImage.ImageSource source, int type) {
                    selectedImage = imageFile;

                    Picasso.with(context).load(selectedImage).transform(new CropSquareTransformation()).into(imageView3);

                    bmap =  imageView3.getDrawingCache();
                }
                @Override
                public void onCanceled(EasyImage.ImageSource source, int type) {
                    if (source == EasyImage.ImageSource.CAMERA) {
                        File photoFile = EasyImage.lastlyTakenButCanceledPhoto(context);
                        if (photoFile != null) photoFile.delete();
                    }
                }
            });

        buildInitialTooltips();
    }
    private void buildInitialTooltips() {
        ToolTip.Builder builder = new ToolTip.Builder(
                this,
                imageView3,
                rootView,
                "Imagen de activo a guardar",
                ToolTip.POSITION_BELOW);
        builder.setBackgroundColor(getResources().getColor(R.color.colorTooltip));
        toolTipsManager.show(builder.build());

    }
    @OnClick(R.id.btn_enviar)
    public void onViewClicked() {
        //String img = Environment.getExternalStorageState().getBytes();
        baseDatos = new BaseDatos(this);

        SQLiteDatabase db = baseDatos.getWritableDatabase();


        if(!editTextNombre.getText().toString().isEmpty() && !editTextActivoDesc.getText().toString().isEmpty() && !editTextFecha.getText().toString().isEmpty()){
            imageView3.buildDrawingCache();
            Bitmap bmap = imageView3.getDrawingCache();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            bmap.compress(Bitmap.CompressFormat.PNG, 0, byteArrayOutputStream);
            byte[] bytesImg = byteArrayOutputStream.toByteArray();
            articuloModal articulo = new articuloModal(editTextNombre.getText().toString(),activo,editTextActivoDesc.getText().toString(), editTextFecha.getText().toString(), bytesImg);
             long response =  baseDatos.insert( articulo);
            System.out.println("Respuesta? " + response);
            if(response != -1){

              //  Toast.makeText(this, "Activo insertado correctamente",Toast.LENGTH_SHORT).show();
                builder.setMessage("Activo insertado correctamente")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // Continue with delete operation
                                Clear();
                            }
                        });
                builder.show();


            }else{
                //Toast.makeText(this, "Activo no ha podido ser insertado correctamente",Toast.LENGTH_SHORT).show();
                builder.setMessage("Activo no ha podido ser insertado correctamente")   .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                        Clear();
                    }
                });
                builder.show();
            }



        }else{
            Toast.makeText(this, "DEBES LLENAR TODOS LOS CAMPOS",Toast.LENGTH_SHORT).show();
        }
     }

    public void Clear(){
       imageView3.setImageResource(R.drawable.empty_image);
        editTextNombre.setText("");
        editTextActivoDesc.setText("");
        editTextFecha.setText("");

    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {

    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }
    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
}
