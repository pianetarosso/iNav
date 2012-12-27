package it.inav.alertDialogs;

import it.inav.R;
import it.inav.base_objects.Building;
import it.inav.base_objects.Room;
import it.inav.graphics.MapView;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;



/** Java Class
 * <br />
 * Classe che contiene metodi per caricare e mostrare AlertDialog di ricerca.
 * Viene utilizzata sia nella {@link it.inav.MainActivity} che nella {@link it.inav.Navigator}
 * @author Marco Fedele
 * 
 */
public class Find {

	private Context context;

	/** Lista di stanze, ordinata in base al nome delle persone ivi presenti */
	private SortedMap<String, Room> people;
	
	/** Lista di stanze, ordinata in base al nome delle stanze stesse */
	private SortedMap<String, Room> rooms;

	/** Array di stringhe, contengono di fatto le rispettive liste {@link #people} e {@link #rooms}  
	 * Necessari per visualizzare le scelte negli AlertDialog 
	 */
	public String[] people_s, rooms_s;
	
	/** Contiene i nomi dei due gruppi su cui operare scelta, ROOM or PEOPLE */
	CharSequence[] items = null;
	
	/** Vedi {@link it.inav.graphics.MapView} */
	private MapView cv;

	
	
	/**
	 * Costruttore, costruisce le liste in base all'edificio, vedi {@link Find#buildLists(Building)}
	 * 
	 * @param b Building l'edificio in considerazione
	 * @param context Context il contesto
	 */
	public Find(Building b, Context context) {

		this.context = context;

		// creo gli oggetti
		people = new TreeMap<String, Room>();
		rooms = new TreeMap<String, Room>();

		// costruisco le liste
		buildLists(b);
	}

	/** Metodo per costruire le liste ordinate di stanze e persone
	 * 
	 * @param b Building l'edificio considerato
	 */
	private void buildLists(Building b) {

		List<Room> rooms = b.getStanze();

		// popolo le liste
		for(Room r : rooms) {
			this.rooms.put(r.nome_stanza, r);

			for (String persone : r.persone)
				this.people.put(persone, r);
		}

		// costruisco le liste per gli AlertBuilding
		people_s = new String[this.people.size()];
		int counter = 0;
		for(String s : this.people.keySet()) {
			people_s[counter] = s;
			counter++;
		}

		// costruisco le liste per gli AlertBuilding
		rooms_s = new String[this.rooms.size()];
		counter = 0;
		for(String s : this.rooms.keySet()) {
			rooms_s[counter] = s;
			counter++;
		}	
		
		
		// A seconda del caso, popolo ITEMS
		if ((this.rooms.size() > 0) && (this.people.size() > 0)) {
			items = new CharSequence[2];
			items[0] = context.getString(R.string.rooms);
			items[1] = context.getString(R.string.people);
		}
		else if (this.rooms.size() > 0) {
			items = new CharSequence[1];
			items[0] = context.getString(R.string.rooms);
		}
		else if (this.people.size() > 0) {
			items = new CharSequence[1];
			items[0] = context.getString(R.string.people);
		}
		
	}

	
	/** Costruttore di AlertDialog, chiede all'utente se vuole effettuare una 
	 * ricerca tra le stanze o il personale
	 * 
	 * @param cv MapView la View che conterrà questo AlertDialog
	 */
	public void showQuestion(MapView cv) {

		this.cv = cv;
		
		AlertDialog.Builder b = new AlertDialog.Builder(context);

		b.setCancelable(true);
		b.setTitle(R.string.find_message);
		
		b.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();

				// A seconda della casistica, carico un tipo di AlertDialog o un'altro
				if (items[which].equals(context.getString(R.string.rooms)))
					showLists(rooms_s, rooms, context.getString(R.string.rooms));
				else
					showLists(people_s, people, context.getString(R.string.people));
			}
		});
		
		// se SONO presenti STANZE o PERSONE, mostro l'AlertDialog
		if (items != null)
			b.show();
	}

	
	/** Costruttore di AlertDialog, 
	 * mostra all'utente una lista di nomi di stanze o di persone, a seconda della scelta precedente.
	 * <br />
	 * 
	 * In seguito alla selezione, chiama {@link MapView#goToPoint(it.inav.base_objects.Point)} 
	 *  per portare in evidenza il piano ed il punto in cui si trova l'oggetto cercato.
	 * 
	 * @param list String[] lista di persone o stanze presenti in quell'edificio
	 * @param objects SortedMap contenitore ordinato, può essere di 
	 * (Nomi_persone, Stanze) o (Nomi_stanze, Stanze)
	 * @param title CharSequence indica COSA si sta cercando nel titolo dell'AlertDialog
	 */
	private void showLists(final String[] list, 
			final SortedMap<String, Room> objects, CharSequence title) {
		
		Builder b = new AlertDialog.Builder(context);
		b.setCancelable(true);
		b.setTitle(title+":");
		
		b.setItems(list, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				
				// recupero l'elemento dalla lista
				Room selected = objects.get(list[which]);
				
				// mi sposto sul piano e punto della stanza.
				cv.setFloor(selected.punto.piano);
				cv.goToPoint(selected.punto);
			}
		});
		b.show();
	}

}
