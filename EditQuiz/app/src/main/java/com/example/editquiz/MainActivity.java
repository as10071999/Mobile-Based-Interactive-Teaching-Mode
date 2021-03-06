package com.example.editquiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.editquiz.modal.Question;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.Collator;

public class MainActivity extends AppCompatActivity implements LocationListener {
    DatabaseReference mDatabase;
    // ...
    Button start_button;
    EditText timer;
    EditText restriction;
    long count;
    LocationManager locationManager;
    Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //runtime permissions
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, 100);
        }
        getLocation();//storing location in DB of this app
        if (locationManager != null) {
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            Log.d("TaG", "3rd lat"+String.valueOf(location.getLatitude())+"Long ="+String.valueOf(location.getLongitude()));
            Toast.makeText(this, "" + location.getLatitude() + "," + location.getLongitude(), Toast.LENGTH_SHORT).show();
            //Log.d(TAG, location == null ? "NO LastLocation" : location.toString());
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("EditQuizLoc").removeValue();
            mDatabase.child("restriction").removeValue();
            //mDatabase.child("EditQuizLoc").child("Latitude").setValue(String.valueOf(location.getLatitude()));
            //mDatabase.child("EditQuizLoc").child("Longitude").setValue(String.valueOf(location.getLongitude()));
            mDatabase.child("EditQuizLoc").child("Latitude").setValue(location.getLatitude());
            mDatabase.child("EditQuizLoc").child("Longitude").setValue(location.getLongitude());
        }
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Questions").removeValue();
        mDatabase.child("students").removeValue();
        mDatabase.child("time").removeValue();
        start_button = (Button) findViewById(R.id.start_quiz);
        start_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timer = (EditText) findViewById(R.id.time_count);
                restriction = (EditText) findViewById(R.id.restriction);
                try {
                    // checking valid integer using parseInt() method
                    Integer.parseInt(restriction.getText().toString());
                    mDatabase.child("restriction").setValue(Integer.valueOf(restriction.getText().toString()));
                }catch (NumberFormatException e){
                    Toast.makeText(getApplicationContext(), "Enter Correct distance in integer Meters", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    // checking valid integer using parseInt() method
                    Integer.parseInt(timer.getText().toString());
                    open_QuestionACT();
                    mDatabase.child("time").setValue(Integer.valueOf(timer.getText().toString()));
                } catch (NumberFormatException e) {
                    Toast.makeText(getApplicationContext(), "Enter Correct time in mins", Toast.LENGTH_SHORT).show();
                    return;
                }
                open_QuestionACT();
            }
        });
    }

    private void getLocation() {
        try {
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, 100);
            }else{
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 5, MainActivity.this);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void open_QuestionACT() {
        Intent intent = new Intent(this, Qustion_Entry.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {

    }

    @Override
    public void onLocationChanged(Location location) {
        DatabaseReference mDB = FirebaseDatabase.getInstance().getReference();
        mDB.child("EditQuizLoc").removeValue();
        mDB.child("EditQuizLoc").child("Latitude").setValue(location.getLatitude());
        mDB.child("EditQuizLoc").child("Longitude").setValue(location.getLongitude());
        Log.d("TaG", "onLocationChanged: lat: "+String.valueOf(location.getLatitude())+" long: "+String.valueOf(location.getLongitude()));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

