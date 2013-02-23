package it.inav;

import it.inav.alertDialogs.Find;
import it.inav.base_objects.Building;
import it.inav.graphics.MapMovement;
import it.inav.graphics.MapView;
import it.inav.mapManagment.MapManagment;
import it.inav.sensors.Compass;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;


/** Activity del Navigatore.
 * <br />
 * Qui vengono mostrate la mappa di un piano, effettuate le ricerche, effettuata la navigazione
 * 
 * @author Marco Fedele
 * @category Activity
 * 
 * @see MapView
 * @see MapMovement
 */
public class Navigator extends Activity {

	
	/** Variabile di debug
	 * <br />
	 * TRUE siamo in modalità simulazione, viene utilizzata la bussola simulata
	 * <br />
	 * FALSE viene utilizzata la bussola del terminale
	 * 
	 * @see Compass
	 */
	private boolean debug = true;

	
	/** View di questa Activity
	 * @see MapView
	 */
	private MapView cv;
	
	/** Classe che gestisce le ricerche sulla mappa
	 * @see Find
	 */
	private Find find;
	
	/** edificio mostrato */
	private Building building = null;
	
	/** imposto alcune variabili al primo avvio */
	private boolean firstStart = true;
	
	/** gestore della mappa */
	private MapManagment mm;


	/** 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// imposto il layout
		setContentView(R.layout.navigator_layout);

		// recupero l'id dell'edificio
		String[] b = this.getIntent().getStringArrayExtra("building");
		

		// aggiungere il resume dalla pausa

		
		// campo informazioni (salire al secondo piano...)
		// aggiungere pulsante "emergenza" ( ma per questo servirebbe anche introdurre una nuova modalità sul web)
		// aggiungere campo "solo ascensore"
		// campo per cambiare piano

		// rilevatore continuo di rfid

		// implementare ricerca del percorso più breve con dijkstra
		// implementare il calcolo del costo in base alla distanza tra due punti 
		// (con valori particolari per ascensori e scale)


		// trovo la MapView nel Layout
		cv = (MapView) this.findViewById(R.id.map); 
		
		// imposto il TouchListener della MapView
		cv.setOnTouchListener(new MapMovement(cv));

		// aggiungo il supporto alla bussola
		new Compass(this, cv, debug);
		
		// carico l'edificio
		loadBuilding(b);
		
		TextView tv = (TextView)this.findViewById(R.id.navigatorMessages);
		
		// imposto il gestore della mappa
		mm = new MapManagment(cv, tv, building);
	}

	
	

	/** Caricamento dell'edificio, si occupa anche di mostrare il progressDialog
	 * @see Building
	 */
	private void loadBuilding(String[] b) {

		
		ProgressDialog pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pd.setMax(6);
		
		pd.setTitle(R.string.loading);
		
		pd.show();
		
		LoadBuilding lb = new LoadBuilding();
		lb.initialize(this,  pd);
		
		lb.execute(b);
		
		try {
			lb.get();
		} catch (Exception e) {
			e.printStackTrace();
			pd.dismiss();
		} 
		
		
		// nel caso l'edificio sia nullo, lancio un errore
		if (building == null) 
			showError();
		else
			// abilito il find
			find = new Find(building, this, mm);
	}

	// 
	// che c'è stato un errore, aggiungo un valore ai buildings danneggiati
	// nelle SP e ritorno al main "pulendo" le activities precendeti
	
	/**
	 * Nel caso di problemi nel caricamento di un edificio comunico all'utente che c'è stato
	 * un errore con un AlertDialog, e ritorno al MAIN eliminando la history delle Activities
	 */
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

	/** Mostra un AlertDialog per chiedere all'utente se vuole terminare la navigazione */
	private void askStopTravel() {
		
		Builder ad = new AlertDialog.Builder(this);

		ad.setMessage(R.string.stop_travel_question);
		ad.setCancelable(false);
		
		ad.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				// elimino il dialog 
				dialog.dismiss();
				
				// interrompo la navigazione
				cv.stopNavigation();
					
			}
		});
		
		ad.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		// se è in corso una navigazione, mostro la navigazione
		if (cv.isNavigating)
			ad.show();
	}

	

	/**
	 * Override per impostare alcuni valori della view senza incappare in errori nella 
	 * lettura delle dimensioni della View stessa
	 */
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		
		if (hasFocus && firstStart && (building != null)) {
			
			// imposto i piani e altri parametri nella MapView
			cv.init(building.getPiani());
			
			if (debug) {
				
				// imposto la posizione fittizia
				cv.setUserPosition(building.getStanze().get(0).punto);
			}
			firstStart = false;
		}
		
		super.onWindowFocusChanged(hasFocus);
	}


	/** Intercetto il tasto "cerca" di android, di modo che si apra una finestra di selezione e ricerca
	 * di staze e persone
	 * @see android.app.Activity#onKeyDown(int, android.view.KeyEvent)
	 */
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event)  {

		if (keyCode == android.view.KeyEvent.KEYCODE_SEARCH) {
			
			// chiedo se l'utente vuole terminare il viaggio (se è il caso)
			askStopTravel();
		
			// carico e mostro le domande
			find.showQuestion();
		}
		
		else if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
			
			askStopTravel();
			
			// ritornare al main
			Intent i = new Intent(this, MainActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			
			this.startActivity(i);
		}
		
		return super.onKeyDown(keyCode, event);
	}  
	


	
	/** AsyncTask per il caricamento di un edificio. 
	 * Legata ad un ProgressDialog effettua il loading di tutte le caratteristiche di un edificio 
	 * (immagini comprese).
	 * Prende in input l'id dell'edificio da caricare
	 */
	private class LoadBuilding extends AsyncTask<String, Void, Void> {

		private Context context;
		private ProgressDialog progress;
		
		/** Metodo per impostare alcuni valori necessari per la funzione getBuilding
		 * 
		 * @param context Context
		 * @param pd ProgressDialog indicatore del progresso per l'utente
		 */
		public void initialize(Context context, ProgressDialog pd) {
			this.context = context;
			this.progress = pd;
		}

		@Override
		protected Void doInBackground(String... b) {
			
			// recupero l'id
			long id = Long.parseLong(b[0]);
			
			// carico l'edificio
			building = Building.getBuilding(context, id, progress);
			
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			
			// elimino il progressDialog
			progress.dismiss();
			
			super.onPostExecute(result);
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Override
	protected void onStart() {
	
		super.onStart();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
	
		super.onResume();
	}
	
	
	

}
