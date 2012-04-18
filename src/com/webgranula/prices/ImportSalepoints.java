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
		return null;
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
	
	private void addSalepoint(String title, String address, float lat, float lon) {
    	// TODO: вставлять остальные данные для торговой точки
		SQLiteDatabase db = pricesData.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TITLE, title);
		values.put(ADDRESS, address);
		db.insertOrThrow(TABLE_NAME, null, values);
		db.close();
	}
}
