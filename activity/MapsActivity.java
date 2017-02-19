package org.vorobjev.tripsdiary.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.vorobjev.tripsdiary.R;
import org.vorobjev.tripsdiary.TripsDiaryApplication;
import org.vorobjev.tripsdiary.db.TripEntity;
import org.vorobjev.tripsdiary.db.TripsDao;
import org.vorobjev.tripsdiary.db.TripsDiaryDatabaseHelper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    int PLACE_PICKER_REQUEST = 1;
    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

    @Bind(R.id.startTripButton)
    Button buttonStart;
    @Bind(R.id.endTripButton)
    Button buttonEnd;
    private GoogleMap mMap;
    TripsDiaryDatabaseHelper dbHelper;
    LatLng currentStart;

    @OnClick(R.id.startTripButton)
    public void setStartMarker() {
        mMap.addMarker(new MarkerOptions().position(mMap.getCameraPosition().target).title("Trip start"));
        currentStart = mMap.getCameraPosition().target;
    }

    @OnClick(R.id.addLocationButton)
    public void addLocation() {
        try {
            startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesNotAvailableException e) {

        } catch (GooglePlayServicesRepairableException e) {

        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                Place place = PlacePicker.getPlace(data, this);
                String toastMsg = String.format("Place: %s", place.getName());
                Toast.makeText(this, toastMsg, Toast.LENGTH_LONG).show();

                TripEntity trip = new TripEntity();
                trip.setLatStart(place.getLatLng().latitude);
                trip.setLonStart(place.getLatLng().longitude);
                trip.setLatEnd(place.getLatLng().latitude);
                trip.setLonEnd(place.getLatLng().longitude);

                try {
                    dbHelper.<TripsDao, TripEntity>getDao(TripEntity.class).add(trip);
                } catch (SQLException e){
                    e.printStackTrace();
                }

                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title("My location"));
            }
        }
    }

    @OnClick(R.id.endTripButton)
    public void setEndMarker() {
        if (currentStart != null) {
            mMap.addMarker(new MarkerOptions().position(mMap.getCameraPosition().target).title("Trip end"));
            PolylineOptions line =
                    new PolylineOptions().add(currentStart, mMap.getCameraPosition().target)
                            .width(5).color(Color.RED);
            mMap.addPolyline(line);
            TripEntity trip = new TripEntity();
            trip.setLatStart(currentStart.latitude);
            trip.setLonStart(currentStart.longitude);
            trip.setLatEnd(mMap.getCameraPosition().target.latitude);
            trip.setLonEnd(mMap.getCameraPosition().target.longitude);
            try {
                dbHelper.<TripsDao, TripEntity>getDao(TripEntity.class).add(trip);
            } catch (SQLException e){
                e.printStackTrace();
            }
            currentStart = null;
        } else {
            Toast.makeText(this, "Select start", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        dbHelper = TripsDiaryApplication.getInstance().getDbHelper();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!((LocationManager)getApplicationContext().getSystemService(Context.LOCATION_SERVICE)).isProviderEnabled(LocationManager.GPS_PROVIDER)){
            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }

        if (mMap != null){
            mMap.clear();
            List<TripEntity> records = null;
            try {
                records = dbHelper.<TripsDao, TripEntity>getDao(TripEntity.class).getRecords();
            } catch (SQLException e) {
            }
            for (TripEntity trip : records){
                LatLng start = new LatLng(trip.getLatStart(), trip.getLonStart());
                LatLng end = new LatLng(trip.getLatEnd(), trip.getLonEnd());
                mMap.addMarker(new MarkerOptions().position(start).title("Start"));
                mMap.addMarker(new MarkerOptions().position(end).title("End"));
                PolylineOptions line =
                        new PolylineOptions().add(start, end)
                                .width(5).color(Color.RED);
                mMap.addPolyline(line);
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.clear();
        List<TripEntity> records = null;
        try {
            records = dbHelper.<TripsDao, TripEntity>getDao(TripEntity.class).getRecords();
        } catch (SQLException e) {
        }
        for (TripEntity trip : records){
            LatLng start = new LatLng(trip.getLatStart(), trip.getLonStart());
            LatLng end = new LatLng(trip.getLatEnd(), trip.getLonEnd());
            mMap.addMarker(new MarkerOptions().position(start).title("Start"));
            mMap.addMarker(new MarkerOptions().position(end).title("End"));
            PolylineOptions line =
                    new PolylineOptions().add(start, end)
                            .width(5).color(Color.RED);
            mMap.addPolyline(line);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_trips) {
            startActivity(new Intent(this, TripsActivity.class));
        }
//        else if (id == R.id.action_stops) {
//            startActivity(new Intent(this, StopsActivity.class));
//        }
        return super.onOptionsItemSelected(item);
    }
}
