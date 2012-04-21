package com.webgranula.prices;

import static android.provider.BaseColumns._ID;
import static com.webgranula.prices.Constants.ADDRESS;
import static com.webgranula.prices.Constants.TABLE_NAME;
import static com.webgranula.prices.Constants.TITLE;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class Salepoints extends ListActivity {
	private PricesData pricesData;
	
	@Override
	public void onResume() {
		// когда activity вылезает из стека, надо снова загрузить точки продаж
		super.onResume();
		Cursor cursor = getSalepoints();
		showSalepoints(cursor);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.salepoints);
		
		pricesData = new PricesData(this);
		try {
			Cursor cursor = getSalepoints();
			showSalepoints(cursor);
		} finally {
			pricesData.close();
		}
		
		// обработка клика по элементу списка
		ListView listView = getListView();
		listView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// формируем параметры, передаваемые в SalepointDetails
				Bundle bundle = new Bundle();
				bundle.putLong("salepoint_id", id);
				
				// вызываем SalepointDetails
				Intent i = new Intent(getApplicationContext(), SalepointDetails.class);
				i.putExtras(bundle);
				startActivity(i);
			}
		});
	}
	
	private static String[] FROM = { TITLE, ADDRESS, _ID, };
	private static String ORDER_BY = TITLE + " ASC";
	private Cursor getSalepoints() {
		SQLiteDatabase db = pricesData.getReadableDatabase();
		Cursor cursor = db.query(TABLE_NAME, FROM, null, null, null, null, ORDER_BY);
		startManagingCursor(cursor);
		return cursor;
	}
	
	private static int[] TO = { R.id.title, R.id.address, };
	private void showSalepoints(Cursor cursor) {
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.item,
				cursor, FROM, TO);
		setListAdapter(adapter);
	}
}
