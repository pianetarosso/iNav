package it.inav;

import it.inav.base_objects.Building;
import it.inav.communications.Connect;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	private Button start_navigate;
	private Button search_building;

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
		start_navigate = (Button)this.findViewById(R.id.inizia_navigazione);
		search_building = (Button)this.findViewById(R.id.trova_edifici);

		start_navigate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startNavigation();
			}
		});
	}

	private void startNavigation() {

		ProgressDialog pd = new ProgressDialog(this);
		pd.setCancelable(false);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.show();

		
		try {
			
			// carico in un asynctask i buildings
			final List<Building> buildings = Building.getBuildings(this);

			pd.dismiss();

			if (buildings.size() == 0) {

				Toast t = Toast.makeText(this, 
						this.getResources().getString(R.string.no_buildings), 
						Toast.LENGTH_SHORT);
				t.show();
			}

			else {
				// costruisco la lista
				CharSequence[] items = new CharSequence[buildings.size()];

				for(int i=0; i < buildings.size(); i++)
					items[i] = buildings.get(i).nome;
				
				final Context context = this;
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				
				builder.setTitle(this.getResources().getString(R.string.select_building));
				
				builder.setItems(items, new DialogInterface.OnClickListener() {
					
					public void onClick(DialogInterface dialog, int item) {
						
						dialog.dismiss();
						
						Intent i = new Intent(context, Navigator.class);
			    		i.putExtra("id", buildings.get(item).id);
			    		context.startActivity(i);
					}
				}).show();
			}
		} catch (Exception e) {
			
			e.printStackTrace();
			pd.dismiss();
			// mostare avviso errore
		}
	}


	// testo se il dispositivo è connesso, e agisco di conseguenza
	private void testConnection() {
		if(!isOnline()) {
			search_building.setEnabled(false);

			Toast t = Toast.makeText(this, 
					this.getResources().getString(R.string.no_connection), 
					Toast.LENGTH_LONG);
			t.show();
		}
	}
}
