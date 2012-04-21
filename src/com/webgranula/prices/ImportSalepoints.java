package com.webgranula.prices;

import static android.provider.BaseColumns._ID;
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

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

public class ImportSalepoints extends AsyncTask<Void, Integer, Long> {
	public ProgressDialog dialog;
	Context ctx;
	private PricesData pricesData;
	
	public ImportSalepoints(Context ctx) {
		this.ctx = ctx;
		pricesData = new PricesData(ctx);
	}
	
	protected void onProgressUpdate(Integer... progUpdate) {
		if (progUpdate[0] == 1){  // change the 10000 to whatever
			dialog.setMessage("Импорт белых брендов");
	    }
	}
	
	protected void onPreExecute() {
		dialog = new ProgressDialog(ctx);
		dialog.setMessage("Импорт торговых точек");
		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.show();
	}
	
	protected void onPostExecute(Long unused) {
		dialog.dismiss();
		((Runnable)ctx).run();
		
		super.onPostExecute(unused);
	}
	
	protected Long doInBackground(Void... params) {
		// Импорт ТТ
		String getSalepointsJson = getJson("http://upload.v-zabote.ru/api/v1/salepoint/?format=json&username=test&password=test");
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
		
		publishProgress(1);
		
		// Импорт белых брендов
		deleteWB();
		String getWBJson = getJson("http://upload.v-zabote.ru/api/v1/whitebrand/?format=json&limit=0&username=test&password=test");
		try {
			JSONObject result = new JSONObject(getWBJson);
			JSONArray objects = result.getJSONArray("objects");
			for (int i = 0; i < objects.length(); i++) {
				JSONObject jsonObject = objects.getJSONObject(i);
				String title = jsonObject.getString("name");
				long id = jsonObject.getLong("ext_id");
				addWB(id, title);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void deleteWB() {
    	SQLiteDatabase db = pricesData.getWritableDatabase();
    	db.delete("whitebrands", null, null);
    	db.close();
    }
	
	private String getJson(String url) {
    	StringBuilder builder = new StringBuilder();
    	// настраиваем загрузку JSON-объекта
    	HttpClient client = new DefaultHttpClient();
    	// TODO: подставлять имя пользователя и пароль из базы
    	HttpGet httpGet = new HttpGet(url);
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
	
	private void addSalepoint(String title, String address, float lat, float lon) {
    	// TODO: вставлять остальные данные для торговой точки
		SQLiteDatabase db = pricesData.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TITLE, title);
		values.put(ADDRESS, address);
		db.insertOrThrow(TABLE_NAME, null, values);
		db.close();
	}
	
	private void addWB(long id, String title) {
		SQLiteDatabase db = pricesData.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(_ID, id);
		values.put("title", title);
		db.insertOrThrow("whitebrands", null, values);
		db.close();
	}
}
