package tk.codechef.dawardriving;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "DAW: ";
    private GoogleApiClient mGoogleApiClient;
    private TextView textView;
    private static String activityDetails = "Unknown";
    private static Map<Integer, ScanModel> mapScanDetails = new HashMap<>();
    private static int scanCount = 0;
    public static boolean dataFromAsyncTask = false;
    private LocationManager locationManager;
    private Boolean isGpsEnabled;
    private Boolean isNetworkEnabled;
    private BroadcastReceiver receiver;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGpsEnabled && !isNetworkEnabled) {
            Intent gpsSettings = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(gpsSettings);
            Log.d(TAG, "no internet provider is enabled");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_exit) {
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        Intent intent = new Intent(this, EventRecognitionActivity.class);
        PendingIntent mPendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Log.d(TAG, "Successfully connected to Event Recognition Activity");
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 0, mPendingIntent);

        textView.setText("Successfully connected to Google Play service. \nWaiting for Event Recognition... \n");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Event Recognition Suspended.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Event Recognition Suspended.");
    }

    public void StartScan(View view) {
        EditText usernameText = (EditText) findViewById(R.id.username);
        username = usernameText.getText().toString();

        if ((username.equals("arpitha.nagaraja")) || (username.equals("disha.bhat")) || (username.equals("arun.anjanappa")) || (username.equals("aditi.saini")) || (username.equals("preethini.kumar")) || (username.equals("rajkarnikar"))) {
            Log.d(TAG, "Username: " + username);
            textView = (TextView) findViewById(R.id.scanResults);
            textView.setMovementMethod(new ScrollingMovementMethod());

            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(ActivityRecognition.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
            mGoogleApiClient.connect();

            receiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Calendar rightNow = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss a");
                    String strDate = sdf.format(rightNow.getTime());
                    boolean flag = false;
                    String activityName = intent.getStringExtra("activity");
                    if (activityDetails.equals("Unknown")) {
                        flag = true;
                        scanAccessPoints();
                        activityDetails = activityName;
                    } else if (activityName.equals("Still")) {
                        if (!(activityName.equals(activityDetails))) {
                            Log.d(TAG, "Action Changed: Still");
                            flag = true;
                            scanAccessPoints();
                            activityDetails = activityName;
                        }
                    } else if (activityName.equals("Tilting")) {
                        activityDetails = activityName;
                        Log.d(TAG, "Action: Tilting");
                    } else if (activityName.equals("Moving")) {
                        flag = true;
                        scanAccessPoints();
                        activityDetails = activityName;
                        Log.d(TAG, "Action: Moving");
                    } else {
                        activityDetails = activityName;
                        Log.d(TAG, "Can't determine Action");
                    }

                    if (flag) {
                        String dataString = strDate + " Activity: " + intent.getStringExtra("activity") + ": " + "\n"
                                + "Successfully added Scan result " + scanCount + "\n";
                        dataString = textView.getText() + dataString;
                        textView.setText(dataString);
                    }
                }
            };

            IntentFilter filter = new IntentFilter();
            filter.addAction("ScanActivity");
            registerReceiver(receiver, filter);
        } else{
            alertString();
        }
    }

    private void alertString(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please enter the username and click on start.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void scanAccessPoints() {
        ScanModel scanDetails = new ScanModel();
        LatLng currentPos = new LatLng(0, 0);
        scanCount = scanCount + 1;

        WifiManager mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        mWifiManager.startScan();
        List<ScanResult> mScanResults = mWifiManager.getScanResults();
        scanDetails.setUsername(username);
        scanDetails.setScanResultList(mScanResults);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

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
            });
            if (locationManager != null) {
                Location location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
                if (location != null) {
                    Log.d(TAG, "Current Location, Latitude: " + location.getLongitude() + " , Longitude: " + location.getLongitude());
                    currentPos = new LatLng(location.getLatitude(), location.getLongitude());
                } else {
                    currentPos = new LatLng(0, 0);
                }
            }
        } catch (SecurityException e) {
            currentPos = new LatLng(0, 0);
            e.printStackTrace();
        }
        scanDetails.setCurrentPos(currentPos);
        Calendar rightNow = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String strDate = sdf.format(rightNow.getTime());
        scanDetails.setTimestamp(strDate);
        mapScanDetails.put(scanCount, scanDetails);
        Log.d(TAG, "Successfully added to Map");
    }

    /**
     * Store the scan results into database and clear map values
     *
     * @param view
     */
    public void StopScan(View view) {
        StoreAccessPointsActivity store = new StoreAccessPointsActivity(this);
        store.execute(mapScanDetails);
        if (dataFromAsyncTask) {
            mapScanDetails.clear();
            scanCount = 0;
        }
        Log.d(TAG, "Map cleared?" + mapScanDetails.isEmpty());
    }

    public void ClearScan(View view) {
        mapScanDetails.clear();
        scanCount = 0;
        activityDetails = "Unknown";
        textView = (TextView) findViewById(R.id.scanResults);
        textView.setMovementMethod(new ScrollingMovementMethod());
        textView.setText("Successfully connected to Google Play service. \nWaiting for Event Recognition... \n");
    }
}
