package tk.codechef.dawardriving;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by Disha Bhat on 1/27/2016.
 */
public class StoreAccessPointsActivity extends AsyncTask<Map<Integer, ScanModel>, String, String> {
    private static final String TAG = "DAW: ";
    private boolean flag = false;
    Context ctx;

    StoreAccessPointsActivity(Context ctx) {
        this.ctx = ctx;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(Map<Integer, ScanModel>... params) {

        try {
            URL url = new URL("http://130.83.163.65/DawResults/getListNew.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.setUseCaches(false);

            JSONObject parentJsonObject = new JSONObject();
            for (Map.Entry<Integer, ScanModel> entry : params[0].entrySet()) {
                JSONArray array = new JSONArray();
                List<ScanResult> scanResultList = entry.getValue().getScanResultList();
                LatLng currentPos = entry.getValue().getCurrentPos();
                for (int i = 0; i < scanResultList.size(); i++) {
                    ScanResult mScanResult = scanResultList.get(i);
                    JSONObject childJsonObject = new JSONObject();
                    childJsonObject.put("currentLat", currentPos.latitude);
                    childJsonObject.put("currentLng", currentPos.longitude);
                    childJsonObject.put("bssid", mScanResult.BSSID);
                    childJsonObject.put("ssid", mScanResult.SSID);
                    childJsonObject.put("frequency", mScanResult.frequency);
                    childJsonObject.put("rssi", mScanResult.level);
                    childJsonObject.put("timestamp", String.valueOf(entry.getValue().getTimestamp()));
                    childJsonObject.put("capabilities", mScanResult.capabilities);
                    childJsonObject.put("distance", calculateDistance(mScanResult.level, mScanResult.frequency));
                    childJsonObject.put("username", entry.getValue().getUsername());
                    array.put(childJsonObject);
                }
                parentJsonObject.put(entry.getKey().toString(), array);
            }

            OutputStream os = connection.getOutputStream();
            OutputStreamWriter osWriter = new OutputStreamWriter(os, "UTF-8");
            BufferedWriter writer = new BufferedWriter(osWriter);
            writer.write(parentJsonObject.toString());
            writer.flush();

            // Get the server response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;

            // Read Server Response
            while ((line = reader.readLine()) != null) {
                // Append server response in string
                sb.append(line).append("\n");
            }


            Log.d(TAG, "Server Response: " + sb.toString());
            Log.d(TAG, "Connection URL: " + connection.getURL());
            Log.d(TAG, "Response Code: " + connection.getResponseCode() + ": " + connection.getResponseMessage());
            if (connection.getResponseCode() == 200) {
                flag = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Stored successfully!";
    }


    protected void onPostExecute(String result) {
        if (flag == true) {
            MainActivity.dataFromAsyncTask = true;
        }

        Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();

    }

    /**
     * Calculates the distance from the user location to the AP based on signal strength
     *
     * @param rssi
     * @param frequency
     * @return distance
     */
    private double calculateDistance(double rssi, double frequency) {
        double exp = (27.55 - (20 * Math.log10(frequency)) + Math.abs(rssi)) / 20.0;
        return Math.pow(10.0, exp); //Returns distance value in Meters.
    }

}


