package it.inav.communications;

import it.inav.Memory.SD;
import it.inav.base_objects.Building;
import it.inav.base_objects.Floor;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.text.ParseException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

/** Classe per la connessione e lo scaricamento dei dati dal server
 * 
 * @author Marco fedele
 *
 */
public class Connect {

	/** Link di base del server */
	private static final String HOME = "http://10.0.2.2:8000/";
	
	/** Link per scaricare gli edifici dal server */
	private static final String GET_BUILDING = "buildings/get/building=ID&LATITUDE&LONGITUDE&RADIUS";	 


	/** Metodo per scaricare l'edificio dal server 
	 * 
	 * @param id long id dell'edificio da scaricare
	 * @return new Building 
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 * @throws ParseException
	 * @throws URISyntaxException
	 */
	private static Building getBuildingFromId(long id) 
			throws ClientProtocolException, IOException, JSONException, ParseException, URISyntaxException {

		String url = getBuildingURL(id, "0", "0", -1);
		JSONObject json = JSONParser.getJSONFromUrl(url);
		return new Building(json, HOME);
	}

	/** Metodo per scaricare una lista di edifici dal server, in base alla posizione e al raggio
	 * 
	 * @param latitude String latitudine del centro della ricerca
	 * @param longitude String longitudine del centro della ricerca
	 * @param radius int raggio (in Km) entro cui effettuare la ricerca degli edifici 
	 * @return List(Building)
	 * @throws ClientProtocolException
	 * @throws IOException
	 * @throws JSONException
	 * @throws ParseException
	 * @throws URISyntaxException
	 */
	public static List<Building> getBuildingsInRadius(String latitude, String longitude, int radius) 
			throws ClientProtocolException, IOException, JSONException, ParseException, URISyntaxException {

		String url = getBuildingURL(-1, latitude, longitude, radius);
		JSONObject json = JSONParser.getJSONFromUrl(url);
		return Building.Buildings(json, HOME);
	}

	/** Metodo per costruire il link per scaricare un edificio o una lista di edifici
	 * 
	 * @param id long id dell'edificio
	 * @param latitude String latitudine del centro della ricerca
	 * @param longitude String longitudine del centro della ricerca
	 * @param radius int raggio (in Km) entro cui effettuare la ricerca degli edifici 
	 * @return String formattata correttamente per la ricerca
	 */
	private static String getBuildingURL(long id, String latitude, String longitude, int radius) {
		String out = HOME;
		out += GET_BUILDING.replace("ID", ""+id);
		out = out.replace("LATITUDE", latitude);
		out = out.replace("LONGITUDE", longitude);
		out = out.replace("RADIUS", ""+radius);

		return out;
	}

	/** Metodo per scaricare le immagini dell'edificio dal server a partire dai link
	 *  
	 * @param building Building edificio scaricato
	 * @throws IOException
	 */
	private static void getImages(Building building) throws IOException {

		if (building.foto_link != null)
			building.foto = downloadImage(building.foto_link);

		for (Floor f : building.getPiani()) 
			f.immagine = downloadImage(f.link_immagine);		
	}


	/** Metodo per scaricare un'immagine da un link
	 * 
	 * @param foto_link URI link dell'immagine sul server
	 * @return Bitmap l'immagine scaricata
	 * @throws IOException
	 */
	private static Bitmap downloadImage(URI foto_link) throws IOException {

		// Open a connection to that URL. 
		URLConnection connection = foto_link.toURL().openConnection();
		connection.connect();

		BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
		Bitmap b = BitmapFactory.decodeStream(bis);

		bis.close();

		return b;
	}


	/** Metodo statico per lo scaricamento e il salvataggio di un edificio dal server.
	 * Utilizza un'AsyncTask per lo scaricamento: {@link downloadBuildings}
	 * 
	 * @param id long id dell'edificio da scaricare
	 * @param context Context necessario per aprire la connessione con il database
	 * @return
	 */
	public static Building downloadBuilding(long id, Context context) {

		try {
			Building b = Connect.getBuildingFromId(id);
			Connect.getImages(b);
			b.setWeigths();
			SD.SaveBuilding(b, context);

			return b;

		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
	}

	/** AsyncTask per scaricare un'edificio dal server. Gestisce anche un ProgressDialog
	 * 
	 * @author Marco Fedele
	 *
	 */
	public static class downloadBuildings extends AsyncTask<Long, Void, Boolean[]> {

		private Context context;
		private ProgressDialog pd;
		private Boolean[] output;

		// imposto alcuni parametri
		public void setParameters(Context context, ProgressDialog pd) {
			this.context = context;
			this.pd = pd;
		}


		@Override
		public Boolean[] doInBackground(Long... id) {

			// array di booleani in output, si occupa di comunicare se c'è stato
			// qualche errore nello scaricamento di uno degli edifici
			output = new Boolean[id.length];

			// inizializzo l'output
			for (int i=0; i < output.length; i++)
				output[i] = false;

			for(int i=0; i < id.length; i++) {
				
				// scarico l'edificio e le immagini
				Building b = downloadBuilding(id[i], context);
				
				// imposto il valore di output
				output[i] = (b != null);
				
				// libero la memoria dalle immagini
				b.freeMemory();
				
				// aumento il progress del ProgressDialog
				pd.setProgress(i);
			}
			
			return output;
		}


		@Override
		protected void onPostExecute(Boolean[] result) {
			pd.dismiss();
		}
	}
}


