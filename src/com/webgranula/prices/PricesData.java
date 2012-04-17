package com.webgranula.prices;

import static android.provider.BaseColumns._ID;
import static com.webgranula.prices.Constants.TABLE_NAME;
import static com.webgranula.prices.Constants.TITLE;
import static com.webgranula.prices.Constants.ADDRESS;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class PricesData extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "prices.db";
	private static final int DATABASE_VERSION = 2;
	
	public PricesData(Context ctx) {
		super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL("CREATE TABLE " + TABLE_NAME + " (" + _ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE
				+ " TEXT NOT NULL, " + ADDRESS + " TEXT, "
				+ "STATUS TEXT);");
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
		onCreate(db);
	}
}
