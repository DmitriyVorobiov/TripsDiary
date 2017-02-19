package org.vorobjev.tripsdiary.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.vorobjev.tripsdiary.R;
import org.vorobjev.tripsdiary.TripsDiaryApplication;
import org.vorobjev.tripsdiary.db.TripEntity;
import org.vorobjev.tripsdiary.db.TripsDao;
import org.vorobjev.tripsdiary.db.TripsDiaryDatabaseHelper;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.sql.SQLException;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by pc on 24.12.2016.
 */
public class TripActivity extends Activity {

    private final String TEMP_FILE_NAME = "temp";
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    @Bind(R.id.photoFrame)
    public ImageView photoFrame;
    @Bind(R.id.tripName)
    public TextView tripNameView;
    @Bind(R.id.description)
    public EditText descriptionView;
    TripsDiaryDatabaseHelper dbHelper;
    public File tempPhotoFile;
    public Bitmap photo;
    int recordId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);
        ButterKnife.bind(this);
        dbHelper = TripsDiaryApplication.getInstance().getDbHelper();
        tempPhotoFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), TEMP_FILE_NAME);
        if (savedInstanceState == null) {
            recordId = getIntent().getStringExtra(TripsActivity.RECORD_ID) != null ? Integer.valueOf(getIntent().getStringExtra(TripsActivity.RECORD_ID)) : -1;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            TripEntity entity = dbHelper.<TripsDao, TripEntity>getDao(TripEntity.class).findRecord(recordId).get(0);
            tripNameView.setText("Trip# " + entity.getId());
            descriptionView.setText(entity.getDescription());
            byte[] bytes = entity.getImageBytes();
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            photoFrame.setImageBitmap(bitmap);
            photoFrame.setVisibility(ImageView.VISIBLE);
        } catch (Exception e) {

        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_record, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {
            try {
                TripEntity entity = dbHelper.<TripsDao, TripEntity>getDao(TripEntity.class).findRecord(recordId).get(0);
                entity.setDescription(descriptionView.getText().toString());

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                photo.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                byte[] byteArray = stream.toByteArray();
                entity.setImageBytes(byteArray);

                dbHelper.<TripsDao, TripEntity>getDao(TripEntity.class).update(entity);
            } catch (Exception e) {

            }
            finish();
        } else if (id == R.id.action_photo) {
            takePhoto();
        } else if (id == R.id.action_delete) {
            try {
                TripEntity entity = dbHelper.<TripsDao, TripEntity>getDao(TripEntity.class).findRecord(recordId).get(0);
                dbHelper.<TripsDao, TripEntity>getDao(TripEntity.class).remove(entity);
            } catch (Exception e) {

            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempPhotoFile));
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    public void showPhoto() {
        photo = decodeScaledImage(tempPhotoFile);
        photoFrame.setImageBitmap(photo);
        photoFrame.setVisibility(ImageView.VISIBLE);
    }

    private Bitmap decodeScaledImage(File file) {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int targetW = metrics.widthPixels;
        int targetH = metrics.heightPixels;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        return BitmapFactory.decodeFile(file.getAbsolutePath(), bmOptions);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                showPhoto();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Taking photo failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
