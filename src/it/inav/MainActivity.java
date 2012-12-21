package it.inav;

import it.inav.Memory.SP;
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

public class MainActivity extends Activity {

	private Button start_navigate;
	private Button search_building;
	private Button danneggiati;
	
	private SparseArray<String[]> buildings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);

		loadButtons();
		testConnection();


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

	// verifico se il dispositivo è online
	private boolean isOnline() {

		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) 
			return false;
		return true;
	}

	// carico i pulsanti 
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
	
	
	
	
	private void startNavigation() {

		if (buildings.size() == 0) {

			Toast t = Toast.makeText(this, 
					this.getString(R.string.no_buildings), 
					Toast.LENGTH_SHORT);
			t.show();
		}

		else {
			// costruisco la lista
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
					i.putExtra("building", buildings.get(item));
					context.startActivity(i);
				}
			}).show();
		}
}


// testo se il dispositivo è connesso, e agisco di conseguenza
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
