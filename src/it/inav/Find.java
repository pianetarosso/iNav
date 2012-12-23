package it.inav;

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



public class Find {

	private Context context;

	private SortedMap<String, Room> people;
	private SortedMap<String, Room> rooms;

	public String[] people_s;
	public String[] rooms_s;
	
	CharSequence[] items = null;
	private MapView cv;

	
	// costruttore
	public Find(Building b, Context context) {

		this.context = context;

		people = new TreeMap<String, Room>(); // add custom comparator tra parentesi
		rooms = new TreeMap<String, Room>();

		buildLists(b);
	}

	// costruisco le liste ordinate di stanze e persone
	private void buildLists(Building b) {

		List<Room> rooms = b.getStanze();

		// popolo le liste
		for(Room r : rooms) {
			this.rooms.put(r.nome_stanza, r);

			for (String persone : r.persone)
				this.people.put(persone, r);
		}

		people_s = new String[this.people.size()];
		int counter = 0;
		for(String s : this.people.keySet()) {
			people_s[counter] = s;
			counter++;
		}

		rooms_s = new String[this.rooms.size()];
		counter = 0;
		for(String s : this.rooms.keySet()) {
			rooms_s[counter] = s;
			counter++;
		}	
		
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

	// chiedo all'utente se vuole effettuare una ricerca tra le stanze o il personale
	public void showQuestion(MapView cv) {

		this.cv = cv;
		
		AlertDialog.Builder b = new AlertDialog.Builder(context);

		b.setCancelable(true);
		b.setTitle(R.string.find_message);
		
		b.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();

				if (items[which].equals(context.getString(R.string.rooms)))
					showLists(rooms_s, rooms, context.getString(R.string.rooms));
				else
					showLists(people_s, people, context.getString(R.string.people));
			}
		});
		
		if (items != null)
			b.show();
	}

	// mostro all'utente una lista di nomi di stanze o di persone a cui chiedere
	// in seguito alla selezione, mostro il piano ed il punto in cui si trova l'oggetto
	private void showLists(final String[] list, 
			final SortedMap<String, Room> objects, CharSequence title) {
		
		Builder b = new AlertDialog.Builder(context);
		b.setCancelable(true);
		b.setTitle(title+":");
		
		b.setItems(list, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {

				dialog.dismiss();
				
				Room selected = objects.get(list[which]);
				cv.setFloor(selected.punto.piano);
				cv.goToPoint(selected.punto);
				// aggiungere anche lo zoom della mappa sul punto in questione
			}
		});
		b.show();
	}

}
