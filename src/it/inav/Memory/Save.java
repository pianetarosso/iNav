package it.inav.Memory;

import it.inav.base_objects.Building;
import it.inav.base_objects.Floor;
import it.inav.database.InitializeDB;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.URISyntaxException;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;

public class Save {

	private static final String HOME = "iNav/";
	private static final String FLOORS = "FLOORS/";
	
	private static final String BUILDING_FOTO = "foto.jpg";
	
	public static void SaveBuilding(Building b, Context context) 
			throws FileNotFoundException, URISyntaxException {
		
		// verifico se esiste già una cartella per quel building
		File externalStorage = Environment.getExternalStorageDirectory();
		String path = externalStorage.getAbsolutePath();	
		
		if (!path.endsWith("/")) path += "/";
		
		path += HOME + b.id;
		File t = new File(path);
		
		// nel caso la elimino
		deleteBuilding(t);
		
		if (b.foto_link != null) {
			
			// creo le directory
			t.mkdirs();
		
			File foto = new File(t.getAbsolutePath() + "/" + BUILDING_FOTO);
			
			FileOutputStream out = new FileOutputStream(foto);
			b.foto.compress(Bitmap.CompressFormat.JPEG, 90, out);
			b.foto_link = new URI(foto.getAbsolutePath());
		}
		
		
		for(Floor f : b.getPiani()) {
			
			File baseFloor = new File(t.getAbsolutePath() + "/" + FLOORS);
			
			baseFloor.mkdirs();
			
			File foto = new File(baseFloor.getAbsolutePath() + "/" + f.numero_di_piano + ".JPEG");
			
			FileOutputStream out = new FileOutputStream(foto);
			f.immagine.compress(Bitmap.CompressFormat.JPEG, 90, out);
			f.link_immagine = new URI(foto.getAbsolutePath());
			
		}
		
		
		InitializeDB idb = new InitializeDB(context);
        idb.open();
        idb.createBuilding(b);
        idb.close();
		
	}
	
	// funzione ricorsiva di cancellazione della cartella di un building
	private static void deleteBuilding(File t) {
		
		String[] list;
		
		if (t.exists()) {
			
			// se è una directory, la svuoto, altrimenti lo cancello
			if (t.isDirectory()) {
				
				list = t.list();

				// chiamo ricorsivamente la funzione su ogni elemento della cartella
				for(String l : list) {
					String path = t.getAbsolutePath() + "/" + l;
					File n = new File(path);
					deleteBuilding(n);
				}
				// terminata la cancellazione di tutti gli elementi della cartella, la elimino
				t.delete();
			}
			else
				t.delete();
		}
	}
	
	
}
