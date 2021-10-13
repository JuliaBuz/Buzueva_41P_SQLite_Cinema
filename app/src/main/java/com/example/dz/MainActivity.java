package com.example.dz;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnAdd, btnRead, btnClear;
    EditText etName, etReg, etGanr;
    DBHelper dbHelper;
    SQLiteDatabase database;
    ContentValues contentValues;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        etName = (EditText) findViewById(R.id.etName);
        etReg = (EditText) findViewById(R.id.etreg);
        etGanr = (EditText) findViewById(R.id.etganr);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        UpdateTable();

    }
    public  void UpdateTable(){
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int regIndex = cursor.getColumnIndex(DBHelper.KEY_REG);
            int ganrIndex = cursor.getColumnIndex(DBHelper.KEY_GANR);
            TableLayout dbOutPut = findViewById(R.id.dbOutPut);
            dbOutPut.removeAllViews();
            do {
                TableRow dbOuyPutRow = new TableRow( this);
                dbOuyPutRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                LinearLayout.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT,TableRow.LayoutParams.WRAP_CONTENT);

                TextView OutPutID = new TextView(this);
                params.weight = 1.0f;
                OutPutID.setLayoutParams(params);
                OutPutID.setText(cursor.getString(idIndex));
                dbOuyPutRow.addView(OutPutID);

                TextView OutPutName = new TextView(this);
                params.weight = 3.0f;
                OutPutName.setLayoutParams(params);
                OutPutName.setText(cursor.getString(nameIndex));
                dbOuyPutRow.addView(OutPutName);

                TextView OutPutReg = new TextView(this);
                params.weight = 3.0f;
                OutPutReg.setLayoutParams(params);
                OutPutReg.setText(cursor.getString(regIndex));
                dbOuyPutRow.addView(OutPutReg);

                TextView OutPutGanr = new TextView(this);
                params.weight = 2.0f;
                OutPutGanr.setLayoutParams(params);
                OutPutGanr.setText(cursor.getString(ganrIndex));
                dbOuyPutRow.addView(OutPutGanr);

                Button deleteBtn = new Button(this);
                deleteBtn.setOnClickListener(this);
                params.weight = 1.0f;
                deleteBtn.setLayoutParams(params);
                deleteBtn.setText("Удалить запись");
                deleteBtn.setId(cursor.getInt(idIndex));
                dbOuyPutRow.addView(deleteBtn);

                dbOutPut.addView(dbOuyPutRow);
            } while (cursor.moveToNext());
        }
        cursor.close();
    }
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnAdd:
                String name = etName.getText().toString();
                String reg = etReg.getText().toString();
                String ganr = etGanr.getText().toString();
                contentValues = new ContentValues();
                contentValues.put(DBHelper.KEY_NAME, name);
                contentValues.put(DBHelper.KEY_REG, reg);
                contentValues.put(DBHelper.KEY_GANR, ganr);
                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);
                etName.setText(null);
                etReg.setText(null);
                etGanr.setText(null);
                UpdateTable();
                break;
            case R.id.btnClear:
                database.delete(DBHelper.TABLE_CONTACTS, null, null);
                TableLayout dbOutPut = findViewById(R.id.dbOutPut);
                dbOutPut.removeAllViews();
                etName.setText(null);
                etReg.setText(null);
                etGanr.setText(null);
                UpdateTable();
                break;
            default:
                View outputDBRow = (View) v.getParent();
                ViewGroup outputDB = (ViewGroup) outputDBRow.getParent();
                outputDB.removeView(outputDBRow);
                outputDB.invalidate();
                database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID+ " = ?", new String[]{String.valueOf(v.getId())});
                contentValues = new ContentValues();
                Cursor cursorUpdater = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                if (cursorUpdater.moveToFirst()) {
                    int idIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_ID);
                    int nameIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_NAME);
                    int regIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_REG);
                    int ganrIndex = cursorUpdater.getColumnIndex(DBHelper.KEY_GANR);
                    int realID = 1;
                    do {
                        if (cursorUpdater.getInt(idIndex)>realID){
                            contentValues.put(DBHelper.KEY_ID, realID);
                            contentValues.put(DBHelper.KEY_NAME, cursorUpdater.getString(nameIndex));
                            contentValues.put(DBHelper.KEY_REG, cursorUpdater.getString(regIndex));
                            contentValues.put(DBHelper.KEY_GANR, cursorUpdater.getString(ganrIndex));
                            database.replace(DBHelper.TABLE_CONTACTS, null, contentValues);
                        }
                        realID++;
                    }while (cursorUpdater.moveToNext());
                    if (cursorUpdater.moveToLast()&& v.getId()!=realID){
                        database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID + " = ?", new String[]{cursorUpdater.getString(idIndex)});
                    }
                    UpdateTable();
                }
                break;
        }
    }
}