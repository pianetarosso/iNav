package it.inav;

import it.inav.base_objects.Building;
import it.inav.base_objects.Room;

import java.util.AbstractMap;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.SortedMap;

import android.content.Context;

public class Find {
	
	private Building b;
	private Context context;
	
	private SortedMap<String, Room> people;
	private SortedMap<String, Room> rooms;
	
	private String[] people_s;
	private String[] rooms_s;

	public Find(Building b, Context context) {
		
		this.b = b;
		this.context = context;
		
		people = new AbstractMap<String, Room>();
		rooms = new HashMap<String, Room>();
		
		buildLists();
	}
	
	private void buildLists() {
		
		List<Room> r = b.getStanze();
		Collection<Room> c = new HashSet<Room>();
		
		
		
	}
}
