package it.inav.database;

import it.inav.base_objects.Building;
import it.inav.base_objects.Floor;
import it.inav.base_objects.Path;
import it.inav.base_objects.Point;
import it.inav.base_objects.Room;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.List;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class InitializeDB {

	
	private static final String DATABASE_NAME = "Edifici";
	static final int DATABASE_VERSION = 1;
	
	
	private final Context mCtx;
	private DatabaseHelper mDbHelper;
	private SQLiteDatabase mDb;
	
	protected Buildings buildings;
	protected Paths paths;
	protected Points points;
	protected Rooms rooms;
	protected Floors floors;
	
	// Stringhe creazione Trigger

	// TR_Point_Buildings---------------------------------------------------------------------------------
	// verifica che l'id dell'edificio tra la tabella Buildings e Points sia coerente
	private static final String TR_Point_Buildings = "CREATE TRIGGER TR_Point_Buildings " +
			" BEFORE INSERT ON " + Building.POINTS_TAG +
			" FOR EACH ROW BEGIN" +
			" SELECT CASE WHEN ((SELECT " + Building.ID + " AS ID " + 
			" FROM " + Building.BUILDING_TAG +
			" WHERE " +" ID "+"=new."+ Building.BUILDING_TAG +") IS NULL)"+
			" THEN RAISE (ABORT,'Foreign Key Violation') END;"+
			" END;";
	
	// TR_Floor_Buildings---------------------------------------------------------------------------------
	// verifica che l'id dell'edificio tra la tabella Buildings e Floor sia coerente
	private static final String TR_Floor_Buildings = "CREATE TRIGGER TR_Floor_Buildings " +
			" BEFORE INSERT ON " + Building.FLOORS_TAG +
			" FOR EACH ROW BEGIN" +
			" SELECT CASE WHEN ((SELECT " + Building.ID + " AS ID " + 
			" FROM " + Building.BUILDING_TAG +
			" WHERE " +" ID "+"=new."+ Building.BUILDING_TAG +") IS NULL)"+
			" THEN RAISE (ABORT,'Foreign Key Violation') END;"+
			" END;";
	
	// TR_Path_Buildings---------------------------------------------------------------------------------
	// verifica che l'id dell'edificio tra la tabella Buildings e Path sia coerente
	private static final String TR_Path_Buildings = "CREATE TRIGGER TR_Path_Buildings " +
			" BEFORE INSERT ON " + Building.PATHS_TAG +
			" FOR EACH ROW BEGIN" +
			" SELECT CASE WHEN ((SELECT " + Building.ID + " AS ID " + 
			" FROM " + Building.BUILDING_TAG +
			" WHERE " +" ID "+"=new."+ Building.BUILDING_TAG +") IS NULL)"+
			" THEN RAISE (ABORT,'Foreign Key Violation') END;"+
			" END;";
	
	// TR_Path_Point_A---------------------------------------------------------------------------------
	// verifica che l'id dell'edificio tra la tabella Point e Path sia coerente
	private static final String TR_Path_Point_A = "CREATE TRIGGER TR_Path_Point_A " +
			" BEFORE INSERT ON " + Building.PATHS_TAG +
			" FOR EACH ROW BEGIN" +
			" SELECT CASE WHEN ((SELECT " + Point.ID + " AS ID " + 
			" FROM " + Building.POINTS_TAG +
			" WHERE " +" ID "+"=new."+ Path.A +") IS NULL)"+
			" THEN RAISE (ABORT,'Foreign Key Violation') END;"+
			" END;";
		
	// TR_Path_Point_B---------------------------------------------------------------------------------
	// verifica che l'id dell'edificio tra la tabella Point e Path sia coerente
	private static final String TR_Path_Point_B = "CREATE TRIGGER TR_Path_Point_B " +
			" BEFORE INSERT ON " + Building.PATHS_TAG +
			" FOR EACH ROW BEGIN" +
			" SELECT CASE WHEN ((SELECT " + Point.ID + " AS ID " + 
			" FROM " +Building.POINTS_TAG+
			" WHERE " +" ID "+"=new."+ Path.B +") IS NULL)"+
			" THEN RAISE (ABORT,'Foreign Key Violation') END;"+
			" END;";
	
	// TR_Room_Buildings---------------------------------------------------------------------------------
	// verifica che l'id dell'edificio tra la tabella Buildings e Roomss sia coerente
	private static final String TR_Rooms_Buildings = "CREATE TRIGGER TR_Room_Buildings " +
			" BEFORE INSERT ON " + Building.ROOMS_TAG +
			" FOR EACH ROW BEGIN" +
			" SELECT CASE WHEN ((SELECT " + Building.ID + " AS ID " + 
			" FROM " + Building.BUILDING_TAG +
			" WHERE " +" ID "+"=new."+ Building.BUILDING_TAG +") IS NULL)"+
			" THEN RAISE (ABORT,'Foreign Key Violation') END;"+
			" END;";
		
	// TR_Room_Points---------------------------------------------------------------------------------
	// verifica che l'id dei Punti tra la tabella Points e Roomss sia coerente
	private static final String TR_Rooms_Points = "CREATE TRIGGER TR_Room_Points " +
			"BEFORE INSERT ON " + Building.ROOMS_TAG +
			" FOR EACH ROW BEGIN" +
			" SELECT CASE WHEN ((SELECT " + Point.ID + " AS ID " + 
			" FROM " + Building.POINTS_TAG +
			" WHERE " +" ID "+"=new."+ Room.PUNTO +") IS NULL)"+
			" THEN RAISE (ABORT,'Foreign Key Violation') END;"+
			" END;";
	
	
	
	private static class DatabaseHelper extends SQLiteOpenHelper {

		
		// DATABASE HELPER --------------------------------------------------------
		DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		
		// CREAZIONE DATABASE E TRIGGER --------------------------------------------
		@Override
		public void onCreate(SQLiteDatabase db) {  
		
			// creazione tabella degli edifici
			Log.i(DATABASE_NAME, "Creating Table: " +  Building.BUILDING_TAG );
			db.execSQL(Buildings.Buildings);
			  	   
			// creazione tabella dei punti
			Log.i(DATABASE_NAME, "Creating Table: " + Building.POINTS_TAG);
			db.execSQL(Points.Points);
			
			// creazione tabella dei piani 
			Log.i(DATABASE_NAME, "Creating Table: " +  Building.FLOORS_TAG );
			db.execSQL(Floors.Floors);
			  
			// creazione tabella dei percorsi 
			Log.i(DATABASE_NAME, "Creating Table: " +  Building.PATHS_TAG );
			db.execSQL(Paths.Paths);
			
			// creazione tabella delle staze 
			Log.i(DATABASE_NAME, "Creating Table: " +  Building.ROOMS_TAG );
			db.execSQL(Rooms.Rooms);
						
			// trigger controllo consistenza tra i Points e i Buildings
			Log.i(DATABASE_NAME, "Creating Trigger: TR_Point_Buildings");
			db.execSQL(TR_Point_Buildings);
			
			//trigger controllo consistenza tra i Floors e i Buildings
			Log.i(DATABASE_NAME, "Creating Trigger: TR_Floor_Buildings");
			db.execSQL(TR_Floor_Buildings);
			
			//trigger controllo consistenza tra i Paths e i Buildings
			Log.i(DATABASE_NAME, "Creating Trigger: TR_Path_Buildings");
			db.execSQL(TR_Path_Buildings);
			
			//trigger controllo consistenza tra i Paths e i Point
			Log.i(DATABASE_NAME, "Creating Trigger: TR_Path_Point_A");
			db.execSQL(TR_Path_Point_A);
			
			//trigger controllo consistenza tra i Paths e i Point
			Log.i(DATABASE_NAME, "Creating Trigger: TR_Path_Point_B");
			db.execSQL(TR_Path_Point_B);
			
			//trigger controllo consistenza tra i Buildings e le Room
			Log.i(DATABASE_NAME, "Creating Trigger: TR_Rooms_Buildings");
			db.execSQL(TR_Rooms_Buildings);
			
			//trigger controllo consistenza tra i Points e le Room
			Log.i(DATABASE_NAME, "Creating Trigger: TR_Rooms_Points");
			db.execSQL(TR_Rooms_Points);
		}


		// UPGRADE DATABASE ----------------------------------------------------------
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			Log.w(DATABASE_NAME, "Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			db.execSQL("DROP TABLE IF EXISTS " + Building.BUILDING_TAG );
			db.execSQL("DROP TABLE IF EXISTS " + Building.POINTS_TAG);
			db.execSQL("DROP TABLE IF EXISTS " + Building.FLOORS_TAG );
			db.execSQL("DROP TABLE IF EXISTS " + Building.PATHS_TAG );
			db.execSQL("DROP TABLE IF EXISTS " + Building.ROOMS_TAG );
			db.execSQL("DROP TRIGGER IF EXISTS " + "TR_Point_Buildings");
			db.execSQL("DROP TRIGGER IF EXISTS " + "TR_Floor_Buildings");
			db.execSQL("DROP TRIGGER IF EXISTS " + "TR_Path_Buildings");
			db.execSQL("DROP TRIGGER IF EXISTS " + "TR_Path_Point_A");
			db.execSQL("DROP TRIGGER IF EXISTS " + "TR_Path_Point_B");
			db.execSQL("DROP TRIGGER IF EXISTS " + "TR_Rooms_Buildings");
			db.execSQL("DROP TRIGGER IF EXISTS " + "TR_Rooms_Points");
			onCreate(db);
		}
		
	}
	
	// COSTRUTTORE ----------------------------------------------------------------------
		public InitializeDB(Context ctx) {
			this.mCtx = ctx;
		}
		
		// OPEN DATABASE --------------------------------------------------------------------
		public InitializeDB open() throws SQLException {
			Log.i(DATABASE_NAME, "OPening DataBase Connection....");
			
			mDbHelper = new DatabaseHelper(mCtx);
			mDb = mDbHelper.getWritableDatabase();
			
			buildings = new Buildings(mDb);
			points = new Points(mDb);
			floors = new Floors(mDb);
			paths = new Paths(mDb);
			rooms = new Rooms(mDb);
			
			return this;
		}
		
		// CLOSE DATABASE
		public void close() {
			Log.i(DATABASE_NAME, "Closing DataBase Connection....");
			mDb.close();
		}	
	
	//CREATE/////////////////////////////////////////////////////////////////////////////////////
	
		
	// Crea un edificio
	public boolean createBuilding(Building b) {
		
		deleteBuilding(b.id);
	
		return buildings.createBuilding(b.id, b.nome, b.posizione, b.descrizione, b.data_creazione,
				b.data_update, b.link, b.numero_di_piani, 
				b.versione, b.foto_link, b.geometria) > -1;
	}
	
	// Crea un punto
	public long createPoint(Point p, long building) {
		return points.createPoint(p.id, building, p.RFID, p.posizione, p.piano, p.ingresso);
	}
	
	// crea un piano
	public long createFloor(Floor f, long building) {
		return floors.createFloor(building, f.link_immagine.toString(), f.bearing, 
				f.numero_di_piano, f.descrizione);
	}
	
	// crea un percorso
	public boolean createPath(Path p, long building) {
		return paths.createPath(building, p.costo, p.ascensore, p.scala, p.a.id, p.b.id) > -1;
	}
	
	// crea una stanza
	public boolean createRoom(Room r, long building) {
		return rooms.createRooms(building, r.punto.id, r.link.toString(), r.nome_stanza,
				r.getPersone(), r.altro)> -1;
	}
	
	// DELETE /////////////////////////////////////////////////////////////////////////////////
	
	// cancella un edificio, i piani, i punti e le immagini
	public boolean deleteBuilding(long id) {
		boolean test;
		test = paths.deleteAllPaths(id);
		test = test && rooms.deleteAllRooms(id);
		test = test && points.deleteAllPoints(id);
		test = test && floors.deleteAllFloors(id);
		test = test && buildings.deleteBuilding(id);
		return test;
	}

	
	// FETCH//////////////////////////////////////////////////////////////////////////////////////
	
	// recupera tutti gli edifici
	public List<Building> fetchBuildings() 
			throws SQLException, MalformedURLException, URISyntaxException {
		return buildings.fetchBuilding();
	}
	
	// verifico se un edificio è presente nel datatbase (tramite il suo id)
	public boolean existBuilding(long id) 
			throws SQLException, MalformedURLException, URISyntaxException {
		return buildings.fetchBuilding(id) != null;
	}
	
	// recupera un edificio specifico (tramite il suo id)
	public Building fetchBuilding(long id) 
			throws SQLException, MalformedURLException, URISyntaxException {
		Building b = buildings.fetchBuilding(id);
		b.setPunti(points.fetchPoints(id));
		b.setPiani(floors.fetchFloors(id, b.getPunti()));
		b.setPaths(paths.fetchAllPaths(id, b.getPunti()));
		b.setStanze(rooms.fetchRooms(id, b.getPunti()));
		return b;
	}

	// recupero i piani di un edificio
	public List<Floor> fetchFloors(long id, List<Point> points) throws SQLException {
		return floors.fetchFloors(id, points);
	}
	
	// recupero i punti di un edificio
	public List<Point> fetchPoints(long id) throws SQLException {
			return points.fetchPoints(id);
	}
	
	// recupero i percorsi di un edificio
	public List<Path> fetchAllPaths(long id, List<Point> points) throws SQLException {
		return paths.fetchAllPaths(id, points);
	}
	
	// recupero le stanze di un edificio
	public List<Room> fetchRooms(long id, List<Point> points) throws SQLException, MalformedURLException {
		return rooms.fetchRooms(id, points);
	}
	
	// UPDATE/CREAZIONE DI UN EDIFICIO////////////////////////////////////////////////
	public Building generateBuilding(Building b) 
			throws SQLException, MalformedURLException, URISyntaxException {		
		
		List<Floor> floor = b.getPiani();
		List<Point> point = b.getPunti();
		List<Room> room = b.getStanze();
		List<Path> path = b.getPaths();
		
		// verifico se si tratta di un update o di una creazione, nel primo
		// caso cerco di eliminare il viaggio dal DB
		if(existBuilding(b.id)) { 
			if (!deleteBuilding(b.id)) {
				Log.e(DATABASE_NAME, "Cannot delete building: "+b.id);
				return null;
			}		
		}
		
		// creo l'edificio
		if (!createBuilding(b)) {
					Log.e(DATABASE_NAME, "Cannot create building: "+b.toString());
					return null;
				}
		
		// creo i piani
		for(Floor f : floor) {
			if (createFloor(f, b.id) < 0) {
				Log.e(DATABASE_NAME, "Error save floors: "+f.toString());
				return null;
			}			
		}
		
		// creo i punti
		for(Point p : point) {
				if (createPoint(p, b.id) < 0) {
					Log.e(DATABASE_NAME, "Error save points: "+p.toString());
					return null;
				}
			}
			
	
		for(Path p : path) {
			if (!createPath(p, b.id)) {
				Log.e(DATABASE_NAME, "Error save paths: "+p.toString());
				return null;
			}
		}
		
		// creo le stanze
		for (Room r: room) {
			if (!createRoom(r, b.id)) {
				Log.e(DATABASE_NAME, "Error save rooms: "+r.toString());
				return null;
			}
		}
	
		return b;
    }	
}


