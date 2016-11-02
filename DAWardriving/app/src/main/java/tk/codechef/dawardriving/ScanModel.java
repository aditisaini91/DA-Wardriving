package tk.codechef.dawardriving;

import android.net.wifi.ScanResult;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * @author Arpitha
 * @since 02-01-2016.
 */
public class ScanModel {
    private List<ScanResult> scanResultList;
    private LatLng currentPos;
    private String timestamp;
    private String username;

    public List<ScanResult> getScanResultList() {
        return scanResultList;
    }

    public void setScanResultList(List<ScanResult> scanResultList) {
        this.scanResultList = scanResultList;
    }

    public LatLng getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(LatLng currentPos) {
        this.currentPos = currentPos;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
