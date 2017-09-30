package com.neoriddle.wifiscanner;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by zhyuan on 2017/8/9.
 */
/*// start a new Activity via Intent
        Intent intent = new Intent();
        intent.setClass(this, ListItemDetail.class);
        intent.putExtra("position", position);
        // Or / And
        intent.putExtra("id", id);
        startActivity(intent);*/
public class ListItemDetail extends Activity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listitem);

        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);

        // Here we turn your string.xml in an array
        String[] myKeys = getResources().getStringArray(R.array.sections);

        TextView myTextView = (TextView) findViewById(R.id.my_textview);
        myTextView.setText(myKeys[position]);

    }
}
