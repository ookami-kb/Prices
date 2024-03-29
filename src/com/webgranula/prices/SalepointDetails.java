package com.webgranula.prices;

import static android.provider.BaseColumns._ID;
import static com.webgranula.prices.Constants.TABLE_NAME;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;


public class SalepointDetails extends Activity implements OnClickListener {
	private long salepoint_id;
	private PricesData pricesData;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.salepoint_details);
        
        // вытаскиваем переданные параметры
        // сейчас это только Salepoint ID
        Bundle bundle = this.getIntent().getExtras();
        salepoint_id = bundle.getLong("salepoint_id");
        
        View start = findViewById(R.id.startMonitoring);
        start.setOnClickListener(this);
        
        try {
        	pricesData = new PricesData(this);
			getSalepointDetails();
		} finally {
			pricesData.close();
		}
	}
	
	public void onClick(View v) {
		switch (v.getId()) {
    	case R.id.startMonitoring:
    		// помечаем текущую ТТ как активную
    		setActiveSalepoint();
    		// TODO: вызываем список белых брендов
    		Intent i = new Intent(getApplicationContext(), WhiteBrand.class);
//			i.putExtras(bundle);
			startActivity(i);
    		break;
    	}
	}
	
	private void setActiveSalepoint() {
		SQLiteDatabase db = pricesData.getReadableDatabase();
		db.rawQuery("UPDATE " + TABLE_NAME + " SET status='monitoring' WHERE " + _ID + "=?", new String[] {Long.toString(salepoint_id)});
	}
	
	private Cursor getSalepointDetails() {
		SQLiteDatabase db = pricesData.getReadableDatabase();
		Cursor cursor = db.rawQuery("SELECT _id, title, address FROM " + TABLE_NAME + " where " + _ID + "=?", new String[] {Long.toString(salepoint_id)});
		cursor.moveToFirst();
		
		// устанавливаем значения в соответствии с выбранной торговой точкой
		TextView spName = (TextView)findViewById(R.id.salepointName);
		spName.setText(cursor.getString(1));
		TextView spAddress = (TextView)findViewById(R.id.salepointAddress);
		spAddress.setText(cursor.getString(2));
		
		return cursor;
	}
}
