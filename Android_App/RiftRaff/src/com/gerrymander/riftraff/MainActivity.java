package com.gerrymander.riftraff;

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
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.os.Build;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class MainActivity extends Activity implements SensorEventListener {

	private Socket socket;

	private static final int SERVERPORT = 12102;
	private static String serverIP = "10.0.0.9";

	final float alpha = 0.03f; // User configurable

	SensorManager sensorManager = null;

	public long lastTime = 0;
	
	public double[] velocity = new double[3];
	public double[] position = new double[3];
	
	private Switch serverSwitch;
	private EditText ipAddress;

	Thread globalThread;
	boolean running =false;
	
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

		ipAddress = (EditText) findViewById(R.id.addressSpace);
		
		serverSwitch = (Switch) findViewById(R.id.switchState);
		
		//check the current state before we display the screen
    	
    	
		serverSwitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
		
			   @Override
			   public void onCheckedChanged(CompoundButton buttonView,
			     boolean isChecked) {
			 
			    if(isChecked){
			    	outputX.setText("Checked!");
			    	serverIP = ipAddress.getText().toString();
			    	running = true;
			    	if(globalThread==null) {
			        	globalThread = new Thread(new ClientThread());
			    	}
			    	globalThread.start();
			    // switchStatus.setText("Switch is currently ON");
			    }else{
			    	outputX.setText("unChecked!");
			    	running =false;
			    	globalThread = null;
			    // switchStatus.setText("Switch is currently OFF");
			    }
			 
			   }
			  });
			
			
	}

	@Override
	protected void onResume() {
		super.onResume();

		sensorManager
				.registerListener(this, sensorManager
						.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION),
						sensorManager.SENSOR_DELAY_FASTEST);

		sensorManager.registerListener(this,
				sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
				sensorManager.SENSOR_DELAY_FASTEST);
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
				position = getPos(data);
				if (damp(position)) {
					//outputX.setText(Double.toString(position[0]));
					outputY.setText(Double.toString(position[1]));
					outputZ.setText(Double.toString(position[2]));
					 composeMessage(data, "ACCEL");
				}
				break;

			case Sensor.TYPE_ORIENTATION:
				outputX2.setText(Float.toString(data[1]));
				outputY2.setText(Float.toString(data[0]));
				outputZ2.setText(Float.toString(data[2]));
				 composeMessage(data, "ROT");
				break;
			}
			// }

		}

	}

	public boolean damp(double[] newPos) {
		for (int i = 0; i < newPos.length; i++) {
			if (alpha < newPos[i]) {
				return true;
			}
		}
		return false;
	}

	public double[] getPos(float[] data) {
		double delta = (System.currentTimeMillis() / 1000) - (lastTime / 1000);
		lastTime = System.currentTimeMillis();

		double[] res = new double[data.length];

		for (int i = 0; i < data.length; i++) {

			res[i] = 0.5 * data[i] * Math.pow(delta, 2);
		}
		return res;
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
				InetAddress serverAddr = InetAddress.getByName(serverIP);
				socket = new Socket(serverAddr, SERVERPORT);
			} 
			catch (Exception e) {}
		}
	}
}
