package com.webgranula.prices;

import static com.webgranula.prices.Constants.TABLE_NAME;
import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class PricesActivity extends Activity implements OnClickListener, Runnable {
	private PricesData pricesData;
	private ImportSalepoints req;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        pricesData = new PricesData(this);
        
        View getTaskBtn = findViewById(R.id.get_task);
        getTaskBtn.setOnClickListener(this);
    }
    
    public void onClick(View v) {
    	switch (v.getId()) {
    	case R.id.get_task:
    		// здесь надо импортировать все данные для текущего задания
    		// удаляем сохраненные ТТ
    		deleteSalepoints();
    		// импортируем ТТ
    		
    		req = new ImportSalepoints(this);
    		req.execute((Void)null);
    		
    		break;
    	}
    }
    
    private void deleteSalepoints() {
    	SQLiteDatabase db = pricesData.getWritableDatabase();
    	db.delete(TABLE_NAME, null, null);
    	db.close();
    }
    
    public void run() {
    	Intent i = new Intent(this, Salepoints.class);
		startActivity(i);
    }
    
}
