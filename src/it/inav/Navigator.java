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
		
		// verifica che id sia positivo
		// verifica che sia possibile caricare le immagini
		
		// aggiungere gesture per lo zoom
		// aggiungere touch per navigare la mappa
		
		// aggiungere il resume dalla pausa
		
		// drawing sulla mappa del percorso
		// zoom automatico per mostrare il percorso fino al successivo "marker"
		// freccia per indicare la direzione
		
		// campo informazioni (salire al secondo piano...)
		// aggiungere pulsante "emergenza" ( ma per questo servirebbe anche introdurre una nuova modalità sul web)
		// aggiungere campo "solo ascensore"
		// campo per cambiare piano
		
		// rilevatore continuo di rfid
		
		// implementare ricerca del percorso più breve con dijkstra
		// implementare il calcolo del costo in base alla distanza tra due punti 
		// (con valori particolari per ascensori e scale)
		
		
		Log.i("id", ""+id);
		
		MapView cv = new MapView(this, id); 


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
