package com.webgranula.prices;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class PricesActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        View getTaskBtn = findViewById(R.id.get_task);
        getTaskBtn.setOnClickListener(this);
    }
    
    public void onClick(View v) {
    	switch (v.getId()) {
    	case R.id.get_task:
    		Intent i = new Intent(this, Salepoints.class);
    		startActivity(i);
    		break;
    	}
    }
}
