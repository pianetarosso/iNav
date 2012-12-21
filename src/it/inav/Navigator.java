package it.inav;

import it.DiarioDiViaggio.R;
import it.inav.base_objects.Building;
import it.inav.graphics.MapView;
import it.inav.sensors.Compass;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class Navigator extends Activity {

	// variabile per inizializzare il debug
	private boolean debug = true;
	private String[] b;
	private MapView cv;
	private Compass compass;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_main_);
		
		b = this.getIntent().getStringArrayExtra("building");
		
		
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
		
		
		
		cv = new MapView(this); 
        setContentView(cv);
        compass = new Compass(this, cv, debug);
	}

	@Override
	protected void onStart() {
		
		loadBuilding();
		super.onStart();
	}

	
	private void loadBuilding() {
		
		// aggiungere un'asynctask???????
		long id = Long.parseLong(b[0]);
		Building b = Building.getBuilding(this, id);
		
		if (b == null) 
			showError();
		else
			cv.setFloor(b.getPiani().get(0));
		
	}
	
	// nel caso di problemi nel caicamento di un edificio comunico all'utente
	// che c'è stato un errore, aggiungo un valore ai buildings danneggiati
	// nelle SP e ritorno al main "pulendo" le activities precendeti
	private void showError() {
		
		final Context context = this;
		
		Builder ad = new AlertDialog.Builder(this);
		
		ad.setMessage(R.string.error_loading_building);
		ad.setCancelable(false);
		ad.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				Intent i = new Intent(context, MainActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
				context.startActivity(i);
			}
		});
		ad.show();
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
	
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event)  {
    	// intercetto il tasto "cerca" di android, di modo che si apra una finestra di selezione e ricerca
		
    	boolean test;
    	
    	test = (keyCode == android.view.KeyEvent.KEYCODE_SEARCH) ;
    	
    	

	    return super.onKeyDown(keyCode, event);
	}    

}
