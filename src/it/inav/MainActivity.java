package it.inav;

import it.inav.Memory.Save;
import it.inav.base_objects.Building;
import it.inav.communications.Connect;
import it.inav.graphics.MapView;
import it.inav.sensors.Compass;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

	// variabile per inizializzare il debug
	private boolean debug = true;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
        
        MapView cv = new MapView(this); 
      
       
        setContentView(cv);
        
        new Compass(this, cv, debug);
        
        try {
			Building b = Connect.getBuildingFromId(6);
			Connect.getImages(b);
			Save.SaveBuilding(b, this);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
      //  cv.setBearing(37);
 catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
