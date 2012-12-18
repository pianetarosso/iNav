package it.inav.database;

import it.inav.base_objects.BuildAndFind;
import it.inav.base_objects.Building;
import it.inav.base_objects.Floor;
import it.inav.base_objects.Path;
import it.inav.base_objects.Pixel;
import it.inav.base_objects.Point;
import it.inav.base_objects.Room;

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
	
	private Buildings buildings;
	private Points points;
	private Floors floors;
	private Paths paths;
	private Rooms rooms;
	
	// Stringhe creazione Trigger

	// TR_Point_Buildings---------------------------------------------------------------------------------
	// verifica che l'id dell'edificio tra la tabella Buildings e Points sia coerente
	private static final String TR_Point_Buildings = "CREATE TRIGGER TR_Point_Buildings " +
			"BEFORE INSERT ON " +Points.pointTable+
			" FOR EACH ROW BEGIN" +
			" SELECT CASE WHEN ((SELECT " +Buildings.colBuildingId+ " AS ID " + " FROM " +Buildings.buildingTable+
			" WHERE " +" ID "+"=new."+Points.colPointReferenceBuilding+") IS NULL)"+
			" THEN RAISE (ABORT,'Foreign Key Violation') END;"+
			" END;";
	
	// TR_Floor_Buildings---------------------------------------------------------------------------------
	// verifica che l'id dell'edificio tra la tabella Buildings e Floor sia coerente
	private static final String TR_Floor_Buildings = "CREATE TRIGGER TR_Floor_Buildings " +
			"BEFORE INSERT ON " +Floors.floorTable+
			" FOR EACH ROW BEGIN" +
			" SELECT CASE WHEN ((SELECT " +Buildings.colBuildingId+ " AS ID " + " FROM " +Buildings.buildingTable+
			" WHERE " +" ID "+"=new."+Floors.colFloorReferenceBuilding+") IS NULL)"+
			" THEN RAISE (ABORT,'Foreign Key Violation') END;"+
			" END;";
	
	// TR_Path_Buildings---------------------------------------------------------------------------------
	// verifica che l'id dell'edificio tra la tabella Buildings e Path sia coerente
	private static final String TR_Path_Buildings = "CREATE TRIGGER TR_Path_Buildings " +
			"BEFORE INSERT ON " +Paths.pathTable+
			" FOR EACH ROW BEGIN" +
			" SELECT CASE WHEN ((SELECT " +Buildings.colBuildingId+ " AS ID " + " FROM " +Buildings.buildingTable+
			" WHERE " +" ID "+"=new."+Paths.colPathReferenceBuilding+") IS NULL)"+
			" THEN RAISE (ABORT,'Foreign Key Violation') END;"+
			" END;";
	
	// TR_Path_Point_A---------------------------------------------------------------------------------
	// verifica che l'id dell'edificio tra la tabella Point e Path sia coerente
	private static final String TR_Path_Point_A = "CREATE TRIGGER TR_Path_Point_A " +
			"BEFORE INSERT ON " +Paths.pathTable+
			" FOR EACH ROW BEGIN" +
			" SELECT CASE WHEN ((SELECT " +Points.colPointId+ " AS ID " + " FROM " +Points.pointTable+
			" WHERE " +" ID "+"=new."+Paths.colPathReferencePointA+") IS NULL)"+
			" THEN RAISE (ABORT,'Foreign Key Violation') END;"+
			" END;";
		
	// TR_Path_Point_B---------------------------------------------------------------------------------
	// verifica che l'id dell'edificio tra la tabella Point e Path sia coerente
	private static final String TR_Path_Point_B = "CREATE TRIGGER TR_Path_Point_B " +
			"BEFORE INSERT ON " +Paths.pathTable+
			" FOR EACH ROW BEGIN" +
			" SELECT CASE WHEN ((SELECT " +Points.colPointId+ " AS ID " + " FROM " +Points.pointTable+
			" WHERE " +" ID "+"=new."+Paths.colPathReferencePointB+") IS NULL)"+
			" THEN RAISE (ABORT,'Foreign Key Violation') END;"+
			" END;";
	
	// TR_Room_Buildings---------------------------------------------------------------------------------
	// verifica che l'id dell'edificio tra la tabella Buildings e Roomss sia coerente
	private static final String TR_Rooms_Buildings = "CREATE TRIGGER TR_Room_Buildings " +
			"BEFORE INSERT ON " +Rooms.roomTable+
			" FOR EACH ROW BEGIN" +
			" SELECT CASE WHEN ((SELECT " +Buildings.colBuildingId+ " AS ID " + " FROM " +Buildings.buildingTable+
			" WHERE " +" ID "+"=new."+Rooms.colRoomReferenceBuilding+") IS NULL)"+
			" THEN RAISE (ABORT,'Foreign Key Violation') END;"+
			" END;";
		
	// TR_Room_Points---------------------------------------------------------------------------------
	// verifica che l'id dei Punti tra la tabella Points e Roomss sia coerente
	private static final String TR_Rooms_Points = "CREATE TRIGGER TR_Room_Points " +
			"BEFORE INSERT ON " +Rooms.roomTable+
			" FOR EACH ROW BEGIN" +
			" SELECT CASE WHEN ((SELECT " +Points.colPointId+ " AS ID " + " FROM " +Points.pointTable+
			" WHERE " +" ID "+"=new."+Rooms.colRoomReferencePoint+") IS NULL)"+
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
			Log.i(DATABASE_NAME, "Creating Table: " + Buildings.buildingTable);
			db.execSQL(Buildings.Buildings);
			  	   
			// creazione tabella dei punti
			Log.i(DATABASE_NAME, "Creating Table: " + Points.pointTable);
			db.execSQL(Points.Points);
			
			// creazione tabella dei piani 
			Log.i(DATABASE_NAME, "Creating Table: " + Floors.floorTable);
			db.execSQL(Floors.Floors);
			  
			// creazione tabella dei percorsi 
			Log.i(DATABASE_NAME, "Creating Table: " + Paths.pathTable);
			db.execSQL(Paths.Paths);
						
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
			db.execSQL("DROP TABLE IF EXISTS " + Buildings.buildingTable);
			db.execSQL("DROP TABLE IF EXISTS " + Points.pointTable);
			db.execSQL("DROP TABLE IF EXISTS " + Floors.floorTable);
			db.execSQL("DROP TABLE IF EXISTS " + Paths.pathTable);
			db.execSQL("DROP TABLE IF EXISTS " + Rooms.roomTable);
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
	public boolean createBuilding(long id, String nome, int latitudine, int longitudine,
			int piani, int versione) {
		return buildings.createBuilding(id, nome, latitudine, longitudine, piani, versione) > -1;
	}
	
	// Crea un punto
	public long createPoint(long edificio, String RFID, Pixel p, int piano, boolean ingresso_uscita) {
		return points.createPoint(edificio, RFID, p, piano, ingresso_uscita);
	}
	
	// crea un piano
	public long createFloor(long edificio, String link, double bearing, int floor_number) {
		return floors.createFloor(edificio, link, bearing, floor_number);
	}
	
	// crea un percorso
	public boolean createPath(long edificio, int costo, boolean ascensore, boolean scale, long A, long B) {
		return paths.createPath(edificio, costo, ascensore, scale, A, B) > -1;
	}
	
	// crea una stanza
	public boolean createRoom(long edificio, long punto, int piano, String link, String nome_stanza, 
			String[] personale, String altro) {
		return rooms.createRooms(edificio, punto, piano, link, nome_stanza, rooms.convertArray(personale), altro)> -1;
	}
	
	public boolean createRoom(long edificio, long punto, int piano, String link, String nome_stanza, 
			List<String> personale, String altro) {
		return rooms.createRooms(edificio, punto, piano, link, nome_stanza, rooms.convertArray(personale), altro)> -1;
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
	public List<Building> fetchBuildings() {
		return buildings.fetchBuilding();
	}
	
	// verifico se un edificio è presente nel datatbase (tramite il suo id)
	public boolean existBuilding(long id) {
		return buildings.fetchBuilding(id) != null;
	}
	
	// recupera un edificio specifico (tramite il suo id)
	public Building fetchBuilding(long id) {
		Building b = buildings.fetchBuilding(id);
		b.setFloors(floors.fetchfloors(id));
		b.setPoints(points.fetchPoints(id));
		b.setPaths(paths.fetchAllPaths(id));
		b.setRooms(rooms.fetchRooms(id));
		return b;
	}

	// recupero i piani di un edificio
	public List<Floor> fetchFloors(long id) throws SQLException {
		return floors.fetchfloors(id);
	}
	
	// recupero i punti di un edificio
	public List<Point> fetchPoints(long id) throws SQLException {
			return points.fetchPoints(id);
	}
	
	// recupero i percorsi di un edificio
	public List<Path> fetchAllPaths(long id) throws SQLException {
		return paths.fetchAllPaths(id);
	}
	
	// recupero le stanze di un edificio
	public List<Room> fetchRooms(long id) throws SQLException {
		return rooms.fetchRooms(id);
	}
	
	// UPDATE/CREAZIONE DI UN EDIFICIO////////////////////////////////////////////////
	public Building generateBuilding(Building b) {		
		
		List<Floor> f = b.piani;
		List<Point> p = b.punti;
		List<Room> r = b.stanze;
		
		// verifico se si tratta di un update o di una creazione, nel primo
		// caso cerco di eliminare il viaggio dal DB
		if(existBuilding(b.id)) { 
			if (!deleteBuilding(b.id)) {
				Log.e(DATABASE_NAME, "Cannot delete building: "+b.id);
				return null;
			}		
		}
		
		// creo l'edificio
		if (!createBuilding(b.id, b.nome, b.latitudine, b.longitudine, 
				b.numero_di_piani, b.versione)) {
					Log.e(DATABASE_NAME, "Cannot create building: "+b.id);
					return null;
				}
		
		// creo i piani
		for(int i=0; i < f.size(); i++) {
			Floor f1 = f.get(i);
			long new_id = createFloor(b.id, f1.link, f1.bearing, f1.numero_di_piano);
			if (new_id < 0) {
				Log.e(DATABASE_NAME, "Error save floors: "+b.id+", element: "+ i);
				return null;
			}
			f1.setId(new_id);			
		}
		
		// creo i punti
		for(int i=0; i < p.size(); i++) {
				Point p1 = p.get(i);
				long new_id = createPoint(b.id, p1.RFID, p1.posizione, p1.piano, p1.via_accesso);
				if (new_id < 0) {
					Log.e(DATABASE_NAME, "Error save points: "+b.id+", element: "+ i);
					return null;
				}
				
				p.get(i).setId(new_id);
			}
			
		
		// creo la lista dei percorsi
		List<Path> pt = BuildAndFind.buildPaths(p);
		
		for(int i=0; i < pt.size(); i++) {
			Path t = pt.get(i);
			if (!createPath(b.id, t.costo, t.ascensore, t.scala, t.id_point_A, t.id_point_B)) {
				Log.e(DATABASE_NAME, "Error save paths: "+b.id+", element: "+ i);
				return null;
			}
		}
		
		// creo le stanze
		for (int i=0; i < r.size(); i++) {
			Room r1 = r.get(i);
			if (!createRoom(b.id, r1.punto.id, r1.piano, r1.link, r1.nome_stanza, 
					r1.persone, r1.altro)) {
				Log.e(DATABASE_NAME, "Error save rooms: "+b.id+", element: "+ i);
				return null;
			}
		}
		
		// aggiorno l'edificio
		b.setFloors(f);
		b.setPoints(p);
		b.setPaths(pt);
		b.setRooms(r);
		
		return b;
    }	
}


