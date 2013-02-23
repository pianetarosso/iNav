package it.inav;

import it.inav.sensors.GPS;
import android.os.Bundle;

import com.google.android.maps.MapActivity;


/** Activity della mappa per la ricerca di edifici
 * <br />
 * Qui, in base alla posizione dell'utente, vengono mostrati gli edifici nel raggio di 10 Km
 * 
 * @author Marco Fedele
 * @category Activity
 * 
 * 
 */
public class MapSearch extends MapActivity {

	private com.google.android.maps.MapView mapView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// imposto il layout
		setContentView(R.layout.map_layout);
		
    	mapView = (com.google.android.maps.MapView) findViewById(R.id.mapview);
        
       mapView.setSatellite(true);
       
       GPS gps = new GPS(this);
      
	}

	
	
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
}
