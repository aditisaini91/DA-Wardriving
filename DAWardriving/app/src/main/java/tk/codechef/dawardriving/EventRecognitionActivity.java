package tk.codechef.dawardriving;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;

/**
 * This function is used to recognize the movement of User.
 *
 * @author Arpitha
 * @since 10-01-2016
 */
public class EventRecognitionActivity extends IntentService {

    private static final String TAG = "DAW: ";

    public EventRecognitionActivity() {
        super("EventRecognitionActivity");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if(ActivityRecognitionResult.hasResult(intent)){
            //Extract result from Response.
            ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
            DetectedActivity detectedActivity = result.getMostProbableActivity();

            //Get the name of activity. (Still, Tilting or Moving)
            String activityName = getActivityName(detectedActivity.getType());

            //Fire the intent with activity name.
            Intent scanIntent = new Intent("ScanActivity");
            scanIntent.putExtra("activity", activityName);

            Log.d(TAG, "Activity Name: " + activityName);

            //Send the control back to the listener in MainActivity.
            this.sendBroadcast(scanIntent);
        } else{
            Log.d(TAG, "Intent has no Data to return");
        }
    }

    private String getActivityName(int type){
        String activityName = "Unknown";
        switch(type){
            case DetectedActivity.STILL:
                activityName = "Still";
                break;
            case DetectedActivity.TILTING:
                activityName = "Tilting";
                break;
            case DetectedActivity.IN_VEHICLE:
            case DetectedActivity.ON_BICYCLE:
            case DetectedActivity.ON_FOOT:
            case DetectedActivity.RUNNING:
            case DetectedActivity.WALKING:
            case DetectedActivity.UNKNOWN:
                activityName = "Moving";
                break;
        }
        return activityName;
    }
}
