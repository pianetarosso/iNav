package it.inav.sensors;

import it.inav.graphics.MapView;

import org.openintents.sensorsimulator.hardware.Sensor;
import org.openintents.sensorsimulator.hardware.SensorEvent;
import org.openintents.sensorsimulator.hardware.SensorManagerSimulator;

import android.content.Context;
import android.hardware.SensorManager;
import android.util.Log;

public class Compass {
	
	private float[] accelerometerValues;
	private float[] magneticFieldValues;
	
	private MapView cv;
	
	
	// ritardo di lettura dei sensori
	//private final int delay = SensorManager.SENSOR_DELAY_FASTEST;//.SENSOR_DELAY_UI;
	
	
	// costruttore, abilita i sensori
	public Compass(Context context, final MapView cv, boolean debug) {
		
		this.cv = cv;
		SensorManagerSimulator sms;
		SensorManager sm;
		
		if (debug) {
			
			// bussola funzionante con simulatore
			sms = SensorManagerSimulator.getSystemService(context, Context.SENSOR_SERVICE);
			sms.connectSimulator();
			
			sms.registerListener(listenerSms,
					sms.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
					SensorManager.SENSOR_DELAY_FASTEST);
			sms.registerListener(listenerSms,
					sms.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
					SensorManager.SENSOR_DELAY_FASTEST);
			
		}
		else {
			// bussola funzionante con hw reale
			sm = (SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
			sm.registerListener(listenerSm, 
					sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 
					SensorManager.SENSOR_DELAY_FASTEST);
			sm.registerListener(listenerSm, 
					sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), 
					SensorManager.SENSOR_DELAY_FASTEST);	
		}
	}
	
	
	
	//////////////////////////////////////////////////////////////////////////////////////
	// LISTENERS
	
	// HW REALE
	private android.hardware.SensorEventListener listenerSm = 
			new android.hardware.SensorEventListener() {
		
		@Override
		public void onAccuracyChanged(android.hardware.Sensor sensor,
				int accuracy) {}

		@Override
		public void onSensorChanged(android.hardware.SensorEvent event) {
			onChangeListener(event.sensor.getType(), event.values);
		}		
	};
		
	// HW EMULATO
	private org.openintents.sensorsimulator.hardware.SensorEventListener listenerSms = 
			new org.openintents.sensorsimulator.hardware.SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int acc) {}
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			onChangeListener(event.type, event.values);
		}
	};
	
	
	// Classe di metodi comuni ai listener
	public void onChangeListener(int sensor, float[] values) {
		
		if (sensor == Sensor.TYPE_ACCELEROMETER)
			accelerometerValues = values;
		if (sensor == Sensor.TYPE_MAGNETIC_FIELD)
			magneticFieldValues = values;
		
		if ((accelerometerValues != null) && (magneticFieldValues!= null))
			ComputeOrientation();
	}
	
	
	// Funzione che esegue l'aggiornamento dei valori... per "girare" mappa bisogna 
	// impostare la chiamata qui
	
	private void ComputeOrientation() {
		
		float[] values = new float[3];
		float[] R = new float[9];
		float[] outR = new float[9];

		
		SensorManager.getRotationMatrix(R, null, accelerometerValues, magneticFieldValues);
		
		// ruoto la matrice delle coordinate per tenere conto dell'elevazione
		SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_Z, outR);

		SensorManager.getOrientation(R, values);
		
		// Convert from radians to degrees.
		
		// ASSE Z (azimut)
		// bussola 0 => Nord, 90 => Est, 180 => Sud, 270 => West
		values[0] = (float) Math.toDegrees(values[0]); 	 
		
		// ASSE X pitch
		values[1] = (float) Math.toDegrees(values[1]);	
		
		// ASSE Y roll
		values[2] = (float) Math.toDegrees(values[2]);
		
		//Log.i("compass", ""+values[0]);
		 
		cv.setBearing(values[0]);		
		
		cv.invalidate();
	}

}