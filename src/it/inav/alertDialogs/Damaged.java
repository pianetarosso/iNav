package it.inav.alertDialogs;

import it.inav.R;
import it.inav.Memory.SP;
import it.inav.communications.Connect;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class Damaged {

	private Button danneggiati;
	private Context context;
	
	private long[] damaged;
	
	public Damaged(Button danneggiati, Context context) {
		
		this.danneggiati = danneggiati;
		this.context = context;
		
		setButtonProperties();
	}
	
	// imposto la visibilità e l'OnClick del pulsante
	public void setButtonProperties() {
		
		damaged = SP.getDamagedBuilding(context);
		
		if (damaged.length > 0) {
			danneggiati.setClickable(true);
			danneggiati.setVisibility(View.VISIBLE);
			
			String text = context.getString(R.string.damaged).replace("#", ""+damaged.length);
			danneggiati.setText(text);
			
			danneggiati.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					showDamaged(damaged);
				}
			});
		}
		else {
			danneggiati.setClickable(false);
			danneggiati.setVisibility(View.GONE);
		}
	}
	
	
	
	// ALERTDIALOG per chiedere all'utente se intende procedere con lo scaricamento dei dati, oppure
	// se preferisce lasciar perdere
		private void showDamaged(final long[] damaged) {
			
			Builder bd = new AlertDialog.Builder(context);
			bd.setMessage(R.string.repair_message);
			bd.setCancelable(false);
			
			// pulsante negativo, cancello il dialog
			bd.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			
			// pulsante positivo, ELIMINO il dialog e MOSTRO un PROGRESSDIALOG
			bd.setPositiveButton(R.string.download, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					dialog.dismiss();
					showProgressDownload(damaged);
				}
			});
			bd.show();
		}

		
		// PROGRESSDIALOG CON ASYNCTASK!!!!
		private void showProgressDownload(long[] damaged) {
			
			ProgressDialog pd = new ProgressDialog(context);
			pd.setMessage(context.getString(R.string.downloading));
			
			pd.setCancelable(false);
			
			pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			pd.setMax(damaged.length + 1);
			pd.setProgress(0);
			
			
			
			Connect.downloadBuildings cdb = new Connect.downloadBuildings();
			cdb.setParameters(context, pd);
			
			// converto i "damaged" da long a Long per farli digerire dall'asynctask
			Long[] t_d = new Long[damaged.length];
			for(int i=0; i < damaged.length; i++)
				t_d[i] = damaged[i];
			
			// contatore di quanti NON riesco a riparare
			int badcounter = 0;
			
			try {
				
				// i booleani in output rappresentano la riuscita o meno dell'aggiornamento
				// di ogni building da riparare
				cdb.execute(t_d);
				Boolean[] out = cdb.get();
				
				// se tutto a funzionato, posso rimuovere i B. danneggiati dalle SP
				for(int i=0; i < out.length; i++) {
					boolean b = out[i];
					
					if (b)
						SP.removeDamagedBuilding(context, damaged[i]);
					else
						badcounter++;
				}
				
				// in caso di un'eccezione nell'asynctask
			} catch (Exception e) {
				e.printStackTrace();
				badcounter = -1;
				pd.dismiss();
			}	
			
			// Pannello riassuntivo sull'efficacia della "cura" per gli edifici danneggiati
			showSummaryDownloadPanel(badcounter);
		}
		
		
		// mostra il sommario sulla riuscita o meno della riparazione
		private void showSummaryDownloadPanel(int badcounter) {
			
			String text = "";
					
			// RIPARAZIONE RIUSCITA!
			if (badcounter == 0) 
				text = context.getString(R.string.all_buildings_repaired);
			
			// È STATA LANCIATA UN'ECCEZIONE, QUALCHE GROSSO PROBLEMA
			else if (badcounter < 0)
				text = context.getString(R.string.download_problem);
			
			// QUALCHE EDIFICIO NON È STATO RIPARATO
			else
				text = context.getString(R.string.cannot_repair).replace("#", badcounter+"");
			
			// aggiorno le proprietà del pulsante
			setButtonProperties();
			
			// Mostro un altro alertdialog, per informare l'utente e aggiorno i pulsanti 
			Builder ad = new AlertDialog.Builder(context);
			ad.setMessage(text);
			ad.setCancelable(false);
			
			ad.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			
			ad.show();
		}
}
