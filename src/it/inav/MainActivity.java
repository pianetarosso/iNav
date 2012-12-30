package it.inav;

import it.inav.Memory.SP;
import it.inav.alertDialogs.Damaged;
import it.inav.communications.Connect;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


/** Classe della main Activity
 *  
 * @author Marco Fedele
 *
 */
public class MainActivity extends Activity {

	/** pulsante per iniziare a navigare */
	private Button start_navigate;

	/** pulsante per cercare un edificio */
	private Button search_building;

	/** pulsante per riparare gli edifici danneggiati */
	private Button danneggiati;

	/** lista delgi edifici scaricati dalle SP */
	private SparseArray<String[]> buildings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		// carico i pulsanti
		loadButtons();

		// verifico la connessione
		testConnection();
		
		Connect.downloadBuilding(6, this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	@Override
	protected void onResume() {
		loadButtons();
		testConnection();
		super.onResume();
	}

	/** Metodo per verificare se il dispositivo è connesso alla rete
	 * 
	 * @return TRUE se è connesso, FALSE altrimenti
	 */
	private boolean isOnline() {

		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) 
			return false;
		return true;
	}


	/** Carico i listeners per i pulsanti (ed eventualmente ne mostro altri) */
	private void loadButtons() {

		// carico i buildings presenti dalle SP
		buildings = SP.loadBuildings(this);

		// inizia la navigazione su un edificio già presente in memoria
		start_navigate = (Button)this.findViewById(R.id.inizia_navigazione);
		start_navigate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startNavigation();
			}
		});

		// trova un edificio online
		search_building = (Button)this.findViewById(R.id.trova_edifici);


		// visualizzo il pulsante degli edifici danneggiati solo se ce ne sono nelle SP
		danneggiati = (Button)this.findViewById(R.id.edifici_danneggiati);
		new Damaged(danneggiati, this);		
	}


	/** Metodo chiamato per iniziare la navigazione, mostra un AlertDialog con la lista di edifici
	 * disponibili in locale
	 */
	private void startNavigation() {

		// se non sono presenti edifici in memoria, mostro un toast e esco
		if (buildings.size() == 0) {

			Toast t = Toast.makeText(this, 
					this.getString(R.string.no_buildings), 
					Toast.LENGTH_SHORT);
			t.show();
		}

		else {
			// costruisco la lista degli edifici dalle SP
			CharSequence[] items = new CharSequence[buildings.size()];

			for (int i=0; i < buildings.size(); i++) 
				items[i] = buildings.get(i)[1];


			final Context context = this;

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(this.getString(R.string.select_building));
			builder.setItems(items, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int item) {

					dialog.dismiss();

					Intent i = new Intent(context, Navigator.class);

					// passo all'intent della navigazione l'id dell'edificio
					i.putExtra("building", buildings.get(item));
					context.startActivity(i);
				}
			}).show();
		}
	}


	/** Testo se il dispositivo è connesso, e abilito/disabilito alcuni pulsanti di conseguenza.
	 *  Se non è presente una connessione, ne informo l'utente con un Toast
	*/
	private void testConnection() {
		if(!isOnline()) {
			search_building.setEnabled(false);
			if (danneggiati.isClickable())
				danneggiati.setEnabled(false);

			Toast t = Toast.makeText(this, 
					this.getString(R.string.no_connection), 
					Toast.LENGTH_LONG);
			t.show();
		}
	}
}
