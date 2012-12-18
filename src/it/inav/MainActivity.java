package it.inav;

import it.inav.graphics.MapView;
import it.inav.sensors.Compass;
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
	
		
		// InitializeDB idb = new InitializeDB(this);
        //idb.open();
        //idb.close();
        
        MapView cv = new MapView(this);
        
       //EditText et = (EditText)findViewById(R.id.editText3);
       
      
       
        setContentView(cv);
        
        new Compass(this, cv, debug);
        
        
      //  cv.setBearing(37);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
