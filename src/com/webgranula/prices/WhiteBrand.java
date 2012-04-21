package com.webgranula.prices;

import static android.provider.BaseColumns._ID;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.OnItemClickListener;


public class WhiteBrand extends ListActivity {
	private PricesData pricesData;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.salepoints);
		
		pricesData = new PricesData(this);
		try {
			Cursor cursor = getBrands();
			showBrands(cursor);
		} finally {
			pricesData.close();
		}
	}
	
	private static String[] FROM = { "title", _ID, };
	private static String ORDER_BY = "TITLE ASC";
	private Cursor getBrands() {
		SQLiteDatabase db = pricesData.getReadableDatabase();
		Cursor cursor = db.query("whitebrands", FROM, null, null, null, null, ORDER_BY);
		startManagingCursor(cursor);
		return cursor;
	}
	
	private static int[] TO = { R.id.title, };
	private void showBrands(Cursor cursor) {
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item,
				cursor, FROM, TO);
		setListAdapter(adapter);
	}
}
