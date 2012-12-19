package it.inav;

import it.inav.graphics.MapView;
import it.inav.sensors.Compass;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class Navigator extends Activity {

	// variabile per inizializzare il debug
	private boolean debug = true;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main_);
		
		long id = this.getIntent().getLongExtra("id", 0);
		
		Log.i("id", ""+id);
		
		MapView cv = new MapView(this); 


        setContentView(cv);

        new Compass(this, cv, debug);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}

}
