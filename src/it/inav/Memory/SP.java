package it.inav.Memory;

import it.inav.base_objects.Building;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.SparseArray;


public class SP {
	
	
	// EDIFICI!!!!!!!!!!!!!!!! ////////////////////////////////////////////////////////////
	
	
	// carico tutti gli edifici in una MAP formate nel seguente modo:
	// key => contatore
	// String[0] => id
	// String[1] => nome
	// String[2] => link_foto
	// String[3] => descrizione
	public static SparseArray<String[]> loadBuildings(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(Building.BUILDING_TAG, Context.MODE_PRIVATE);
		
		String t_ids = sp.getString(Building.ID, "");
		
		SparseArray<String[]> buildings = new SparseArray<String[]>();
		int counter = 0;
		
		for(String i : t_ids.split(",")) {
			try {
				long id = Long.parseLong(i);
				String nome = sp.getString(Building.NOME + id, "");
				String link_foto = sp.getString(Building.FOTO + id, "");
				String descrizione = sp.getString(Building.DESCRIZIONE + id, "");
				
				if ((nome.length() > 0) && (link_foto.length() > 0)) {
					
					String[] c = new String[4];
					c[0] = "" + id;
					c[1] = nome;
					c[2] = link_foto;
					c[3] = descrizione;
					buildings.append(counter, c);
					counter++;
				}
			}
			catch (Exception e) {}
		}
		
		return buildings;
	}
	
	
	// aggiungo un nuovo edificio
	public static boolean addBuilding(Context context, Building building) {
		
		SharedPreferences sp = context.getSharedPreferences(Building.BUILDING_TAG, Context.MODE_PRIVATE);
		Editor ed = sp.edit();
		
		String t_ids = sp.getString(Building.ID, "");
		
		ed.putString(Building.ID, t_ids + building.id + ",");
		
		ed.putString(Building.NOME + building.id, building.nome);
		ed.putString(Building.DESCRIZIONE + building.id, building.descrizione);
		
		if (building.foto_link != null)
			ed.putString(Building.FOTO + building.id, building.foto_link.toString());
		
		return ed.commit();
	}
	
	// elimino un edificio
	public static boolean deleteBuilding(Context context, long id) {
		
		SharedPreferences sp = context.getSharedPreferences(Building.BUILDING_TAG, Context.MODE_PRIVATE);
		Editor ed = sp.edit();
		
		String t_ids = sp.getString(Building.ID, "");
		
		String new_ids = "";
		
		for(String i : t_ids.split(",")) {
			try {
				long id_s = Long.parseLong(i);
				
				if (id_s != id)
					new_ids += ","+id_s;
			}
			catch (Exception e) {}
		}
		
		if (new_ids.length() > 0)
			ed.putString(Building.ID, new_ids);
		else
			ed.remove(Building.ID);
		
		ed.remove(Building.NOME + id);
		ed.remove(Building.DESCRIZIONE + id);
		ed.remove(Building.FOTO + id);

		
		return ed.commit();
	}
	
	// eseguo l'update, rimuovendo prima il vecchio (se presente) e aggiungendo il nuovo
	public static boolean updateBuilding(Context context, Building building) {
		
		return 
				deleteBuilding(context, building.id) &&
				addBuilding(context, building);
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	
	// EDIFICI DANNEGGIATI ///////////////////////////////////////////////////////////////////
	
	private static final String DAMAGED = "danneggiati";
	
	
	// recupero gli edifici danneggiati
	public static long[] getDamagedBuilding(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(DAMAGED, Context.MODE_PRIVATE);
		
		long[] danneggiati = null;
		
		String[] dann = sp.getString(Building.BUILDING_TAG, "").split(",");
		
		int counter = 0;
		
		for(String d : dann) {
			try {
				Long.parseLong(d);
				counter++;
			} catch (Exception e) {}
		}
		
		danneggiati = new long[counter];
		
		for(String d : dann) {
			try {
				danneggiati[counter - 1] = Long.parseLong(d);
				counter--;
			} catch (Exception e) {}
		}
		
		return danneggiati;
	}
	
	public static boolean addDamagedBuilding(Context context, long id) {
		
		SharedPreferences sp = context.getSharedPreferences(DAMAGED, Context.MODE_PRIVATE);
		Editor ed = sp.edit();
		
		String danneggiati = sp.getString(Building.BUILDING_TAG, "");
		
		danneggiati += id + ",";
		
		ed.putString(Building.BUILDING_TAG, danneggiati);
		
		return ed.commit() && deleteBuilding(context, id);
	}
	
	
	// elimino un building danneggiato
	public static boolean removeDamagedBuilding(Context context, long id) {
		
		SharedPreferences sp = context.getSharedPreferences(DAMAGED, Context.MODE_PRIVATE);
		Editor ed = sp.edit();
		
		String[] danneggiati = sp.getString(Building.BUILDING_TAG, "").split(",");
		
		String new_dann = "";
		
		for(String d : danneggiati) {
			try {
				long t_id = Long.parseLong(d);
				
				if (t_id != id)
					new_dann += t_id + ",";
			}
			catch (Exception e) {}
		}
		
		ed.putString(Building.BUILDING_TAG, new_dann);
		
		return ed.commit();
	}

	///////////////////////////////////////////////////////////////////////////////////////////
}
