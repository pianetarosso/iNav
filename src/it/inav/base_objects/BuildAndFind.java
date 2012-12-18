package it.inav.base_objects;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class BuildAndFind {

	
	// MODIFICA LA LISTA DI POINTS PER POTER EFFETTUARE LA RICERCA
	// 1) Scansiona la lista delle path
	// 2) ad ogni "point A" modifica la lista di "sons" inserendo i riferimenti
	// dei punti nella lista di Points
	public static List<Point> buildSons(List<Point> p, List<Path> pt) {
		
		long old_ID = -1;
		int pA = -1, pB;
		
		for(int i=0; i< pt.size(); i++) {
			Path pt1 = pt.get(i);
			if (old_ID != pt1.id_point_A) {
				pA = find(pt1.id_point_A, p);
				old_ID = pt1.id_point_A;
			}
			pB = find(pt1.id_point_B, p);
			
			p.get(pA).addSon(pt1, p.get(pB));
			p.get(pB).addSon(pt1, p.get(pA));	
		}
		return p;
	}
	
	// ESTRAPOLA DALLA LISTA DI POINTS UNA LISTA DI PATH DA SALVARE NEL DB
	// usata nel salvataggio dei dati di un nuovoe dificio nel db
	// genera una lista di percorsi a partire dalla lista di punti
	public static List<Path> buildPaths(List<Point> pl) {
		
		List<Path> lpt = new ArrayList<Path>();
		List<Long> justused = new ArrayList<Long>();
		
		for(int i=0; i< pl.size(); i++) {
			Point p = pl.get(i);
			Collections.copy(lpt, p.buildPathList(justused));
			justused.add(p.id);
		}
		return lpt;
	}
	
	// PICCOLA FUNZIONE DI FIND LINEARE 
	public static int find(long id, List<Point> p) {
		for (int i=0; i< p.size(); i++) {
			if (p.get(i).id == id)
				return i;
		}
		return -1;
	}
}
