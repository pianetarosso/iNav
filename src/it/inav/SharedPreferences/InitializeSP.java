package it.inav.SharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class InitializeSP {

	
	private static final String settings = "Settings";
	
	
	public InitializeSP(Context context) {	
		loadFromSP(context);
	}
	
	
	public void loadFromSP(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(settings, Context.MODE_PRIVATE);
		
		//inCorso = sp.getLong(inCorsoS, TRAVEL_DEF_VALUE);
		
	}
		
	public boolean saveToSP(Context context) {
		
		Editor editor = context.getSharedPreferences(settings, Context.MODE_PRIVATE).edit();
		
		//editor.putLong(inCorsoS, inCorso);
		
		return editor.commit();
	}
	
	
}