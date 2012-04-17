package com.webgranula.prices;

import static com.webgranula.prices.Constants.ADDRESS;
import static com.webgranula.prices.Constants.TABLE_NAME;
import static com.webgranula.prices.Constants.TITLE;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class PricesActivity extends Activity implements OnClickListener {
	private PricesData pricesData;
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
    		String getSalepointsJson = getSalepointsJson();
    		
    		try {
    			JSONObject result = new JSONObject(getSalepointsJson);
    			JSONArray objects = result.getJSONArray("objects");
    			for (int i = 0; i < objects.length(); i++) {
    				JSONObject jsonObject = objects.getJSONObject(i);
    				String title = jsonObject.getString("name");
    				String address = jsonObject.getString("address");
    				float lat = (float)jsonObject.getDouble("latitude");
    				float lon = (float)jsonObject.getDouble("longitude");
    				addSalepoint(title, address, lat, lon);
    			}
    		} catch (Exception e) {
    			e.printStackTrace();
    		}
    		
    		Log.i("importer", "call activity");
    		Intent i = new Intent(this, Salepoints.class);
    		startActivity(i);
    		break;
    	}
    }
    
    private void deleteSalepoints() {
    	SQLiteDatabase db = pricesData.getWritableDatabase();
    	db.delete(TABLE_NAME, null, null);
    	db.close();
    }
    
    private void addSalepoint(String title, String address, float lat, float lon) {
    	// TODO: вставлять остальные данные для торговой точки
		SQLiteDatabase db = pricesData.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TITLE, title);
		values.put(ADDRESS, address);
		db.insertOrThrow(TABLE_NAME, null, values);
		db.close();
	}
    
    private String getSalepointsJson() {
    	StringBuilder builder = new StringBuilder();
    	// настраиваем загрузку JSON-объекта
    	HttpClient client = new DefaultHttpClient();
    	// TODO: подставлять имя пользователя и пароль из базы
    	HttpGet httpGet = new HttpGet(
				"http://upload.v-zabote.ru/api/v1/salepoint/?format=json&username=test&password=test");
    	try {
    		HttpResponse response = client.execute(httpGet);
    		StatusLine statusLine = response.getStatusLine();
    		int statusCode = statusLine.getStatusCode();
    		if (statusCode == 200) {
    			HttpEntity entity = response.getEntity();
				InputStream content = entity.getContent();
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(content));
				String line;
				while ((line = reader.readLine()) != null) {
					builder.append(line);
				}
    		} else {
				Log.e(PricesActivity.class.toString(), "Failed to download file");
			}
    		
    	} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return builder.toString();
    }
}
