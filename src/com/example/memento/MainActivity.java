package com.example.memento;

import java.util.Calendar;

import android.os.Bundle;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {

	Button btnChooseDate,btnAdd,btnQuery;
	EditText etDate,etSubject,etBody;
	LinearLayout title;
	ListView result;
	MyDatabaseHelper mydbHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		etDate=(EditText) this.findViewById(R.id.etDate);
		etSubject=(EditText) this.findViewById(R.id.etSubject);
		etBody=(EditText) this.findViewById(R.id.etBody);
		title=(LinearLayout) this.findViewById(R.id.title);
		result=(ListView) this.findViewById(R.id.result);
		btnAdd=(Button) this.findViewById(R.id.btnAdd);
		btnQuery=(Button) this.findViewById(R.id.btnQuery);
		btnChooseDate=(Button) this.findViewById(R.id.btnChooseDate);
		
		title.setVisibility(View.INVISIBLE);
		
		btnChooseDate.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				Calendar c=Calendar.getInstance();
				new DatePickerDialog(MainActivity.this, new DatePickerDialog.OnDateSetListener() {
					
					@Override
					public void onDateSet(DatePicker view, int year, int month,
							int day) {
						// TODO Auto-generated method stub
						
						etDate.setText(year+"-"+(month+1)+"-"+day);
					}
				}, c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH)).show();
				
			}
		});
		
		MyOnClickListerner myOnClickListerner =new MyOnClickListerner();
		btnAdd.setOnClickListener(myOnClickListerner);
		btnQuery.setOnClickListener(myOnClickListerner);
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	 private class MyOnClickListerner implements OnClickListener{

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				mydbHelper=new MyDatabaseHelper(MainActivity.this, "memento.db", null, 1);
				SQLiteDatabase db=mydbHelper.getReadableDatabase();
				String subStr=etSubject.getText().toString();
				String bodyStr=etBody.getText().toString();
				String dateStr=etDate.getText().toString();
				switch(v.getId()){
				case R.id.btnAdd:
					title.setVisibility(View.INVISIBLE);
					addMemento(db,subStr,bodyStr,dateStr);
					Toast.makeText(MainActivity.this, "添加备忘录成功！", 1000).show();
					result.setAdapter(null);
					break;
				case R.id.btnQuery:
					title.setVisibility(View.VISIBLE);
					Cursor cursor=queryMemento(db,subStr,bodyStr,dateStr);
					SimpleCursorAdapter resultAdapter = new SimpleCursorAdapter
							(MainActivity.this,
							R.layout.result,cursor,
							new String[] {"_id","subject","body","date"},
							new int[] {R.id.memento_num,R.id.memento_subject,R.id.memento_body,R.id.memento_date}); 
					result.setAdapter(resultAdapter);
					break;
				default:
					break;
				}
			}		
		}

	public void addMemento(SQLiteDatabase db, String subject, String body,
			String date) {
		// TODO Auto-generated method stub
		db.execSQL("insert into memento_tb values(null,?,?,?)",new String[] {subject,body,date});
		this.etSubject.setText("");
		this.etBody.setText("");
		this.etDate.setText("");
		
	}


	public Cursor queryMemento(SQLiteDatabase db, String subject,
			String body, String date) {
		// TODO Auto-generated method stub
		Cursor cursor=db.rawQuery("select * from memento_tb where subject like ? and body like ? and date like ? ",
				new String[] {"%"+subject+"%","%"+body+"%","%"+date+"%"});
		return cursor;
	}


	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		if(mydbHelper!=null){
			mydbHelper.close();
		}
	}
	
	
}
