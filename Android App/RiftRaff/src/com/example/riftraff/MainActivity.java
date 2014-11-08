package com.example.riftraff;

//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Build;

public class MainActivity extends Activity implements SensorEventListener {

	SensorManager sensorManager = null;
	 
	//for accelerometer values
	TextView outputX;
	TextView outputY;
	TextView outputZ;
	 
	//for orientation values
	TextView outputX2;
	TextView outputY2;
	TextView outputZ2;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        setContentView(R.layout.activity_main);
        //just some textviews, for data output
        outputX = (TextView) findViewById(R.id.xBox01);
        outputY = (TextView) findViewById(R.id.yBox01);
        outputZ = (TextView) findViewById(R.id.zBox01);
     
        outputX2 = (TextView) findViewById(R.id.xBox02);
        outputY2 = (TextView) findViewById(R.id.yBox02);
        outputZ2 = (TextView) findViewById(R.id.zBox02);
    }


    @Override
    protected void onResume() {
       super.onResume();
       sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), sensorManager.SENSOR_DELAY_UI);
       sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), sensorManager.SENSOR_DELAY_UI);
    }
    
    @Override
    protected void onStop() {
       super.onStop();
       sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
       sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onSensorChanged(SensorEvent event) {
		  synchronized (this) {
		        switch (event.sensor.getType()){
		            case Sensor.TYPE_ACCELEROMETER:
		                outputX.setText(Float.toString(event.values[0]));
		                outputY.setText(Float.toString(event.values[1]));
		                outputZ.setText(Float.toString(event.values[2]));
		            break;
		        case Sensor.TYPE_GYROSCOPE:
		                outputX2.setText(Float.toString(event.values[0]));
		                outputY2.setText(Float.toString(event.values[1]));
		                outputZ2.setText(Float.toString(event.values[2]));
		        break;
		 
		        }
		    }
		
	}

}