package com.example.riftraff;

//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.app.ActionBar;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

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

	private Socket socket;

	private static final int SERVERPORT = 12102;
	private static final String SERVER_IP = "10.0.0.9";

	final float alpha = 0.8f; // User configurable

	SensorManager sensorManager = null;

	public long lastTime = 0;
	public double[] velocity = new double[3];

	// for accelerometer values
	TextView outputX;
	TextView outputY;
	TextView outputZ;

	// for orientation values
	TextView outputX2;
	TextView outputY2;
	TextView outputZ2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		setContentView(R.layout.activity_main);
		// just some textviews, for data output
		outputX = (TextView) findViewById(R.id.xBox01);
		outputY = (TextView) findViewById(R.id.yBox01);
		outputZ = (TextView) findViewById(R.id.zBox01);

		outputX2 = (TextView) findViewById(R.id.xBox02);
		outputY2 = (TextView) findViewById(R.id.yBox02);
		outputZ2 = (TextView) findViewById(R.id.zBox02);

		new Thread(new ClientThread()).start();
	}

	@Override
	protected void onResume() {
		super.onResume();

		sensorManager
				.registerListener(this, sensorManager
						.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
						sensorManager.SENSOR_DELAY_NORMAL);

		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				sensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onStop() {
		super.onStop();
		sensorManager
				.unregisterListener(this, sensorManager
						.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION));
		sensorManager.unregisterListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION));
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
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

	@Override
	public void onAccuracyChanged(Sensor arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		float[] data = event.values;

		synchronized (this) {
			// if (isDamp(event)) {
			switch (event.sensor.getType()) {
			case Sensor.TYPE_LINEAR_ACCELERATION:
				outputX.setText(Double.toString(data[0]));
				outputY.setText(Double.toString(data[1]));
	//			outputZ.setText(Float.toString(data[2]));

	//			composeMessage(data, "ACCEL");
				break;

			case Sensor.TYPE_ORIENTATION:
				outputX2.setText(Float.toString(data[1]));
				outputY2.setText(Float.toString(data[0]));
				outputZ2.setText(Float.toString(data[2]));
			//	composeMessage(data, "ROT");
				break;
			}
			// }

		}

	}


	// SensorEvent event
	public void composeMessage(float[] values, String type) {
		String res = "";
		if (type.equals("ACCEL")) {
			res = type + ":" + Float.toString(values[0]) + ","
					+ Float.toString(values[1]) + ","
					+ Float.toString(values[2]);
		} else if (type.equals("ROT")) {
			res = type + ":" + Float.toString(values[1]) + ","
					+ Float.toString(values[0]) + ","
					+ Float.toString(values[2]);
		}
		try {
			PrintWriter out = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())), true);
			out.println(res);
			// out.flush();
			// out.print(res);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public class ClientThread implements Runnable {

		@Override
		public void run() {
			try {
				InetAddress serverAddr = InetAddress.getByName(SERVER_IP);
				socket = new Socket(serverAddr, SERVERPORT);

			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

	}

}
