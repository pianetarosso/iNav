package it.inav.sensors;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class GPS {

	private LocationManager locationManager;
	private String bestProvider;
	private LocationListener myLocationListener = null;
	private String serviceString = Context.LOCATION_SERVICE;
	
	
	public GPS(Context context) {
		
		locationManager = (LocationManager)context.getSystemService(serviceString);
 	   	bestProvider = locationManager.getBestProvider(setCriteria(), true);
		
	}
	
	public void stopListener() {		
		locationManager.removeUpdates(myLocationListener);
	}
	
	
	public void startListener(final int time, int space) {	// recupera la posizione gps
	
		myLocationListener = new LocationListener() {

			@Override
			public void onLocationChanged(Location location) {
				
				if (location != null) {
					//inviare in giro il risultato
					stopListener();
				}	
			}

			@Override
			public void onProviderDisabled(String provider) {}

			@Override
			public void onProviderEnabled(String provider) {}

			@Override
			public void onStatusChanged(String provider, int status,
					Bundle extras) {}
  		};

	   locationManager.requestLocationUpdates(bestProvider, 3600, 0, myLocationListener);
}
	
	
	
	
	// criteri globali per la gestione del gps
			private Criteria setCriteria() {
				
				Criteria criteria = new Criteria();
				criteria.setAccuracy(Criteria.ACCURACY_FINE);
				criteria.setPowerRequirement(Criteria.POWER_LOW);
				criteria.setAltitudeRequired(false);
				criteria.setBearingRequired(false);
				criteria.setSpeedRequired(false);
				criteria.setCostAllowed(false);
				return criteria;
			}
}
