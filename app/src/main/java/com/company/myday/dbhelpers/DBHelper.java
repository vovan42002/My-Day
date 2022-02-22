package com.company.myday.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.company.myday.models.Item;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "myDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        System.out.println("--- onCreate database ---");
        // создаем таблицу с полями
        db.execSQL("create table task ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "checkBox integer,"
                + "date text" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void add_Model_item(Item modelItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("name", modelItem.getName());
        values.put("checkBox", modelItem.isSelected());
        values.put("date", modelItem.getCalendar());

        // Inserting Row
        db.insert("task", null, values);
        db.close(); // Closing database connection
    }

    public int update_Model_item(Item modelItem) {
        System.out.println("UPDATE: " + modelItem);
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put("name", modelItem.getName());
        values.put("checkBox", modelItem.isSelected());
        values.put("date", modelItem.getCalendar());

        // updating row
        return db.update("task", values, "id" + " = ?",
                new String[]{String.valueOf(modelItem.getId())});
    }

    // Getting All Item
    public List<Item> getAllModel_item() {
        List<Item> list = new ArrayList<Item>();
        // Select All Query
        String selectQuery = "SELECT  * FROM task";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Item modelItem = new Item();
                modelItem.setId(Long.parseLong(cursor.getString(0)));
                modelItem.setName(cursor.getString(1));
                modelItem.setSelected(Boolean.getBoolean(cursor.getString(2)));
                modelItem.setCalendar(Long.parseLong(cursor.getString(3)));
                // Adding model_item to list
                list.add(modelItem);
            } while (cursor.moveToNext());
        }
        // return model_item list
        return list;
    }

    // Getting single model_item

    public Item get_Model_item(String name, boolean box) {
        SQLiteDatabase db = this.getReadableDatabase();
        Item modelItem = null;
        Cursor cursor = db.query("task",
                null,
                "name =? AND checkBox=?",
                new String[]{name, String.valueOf(Boolean.compare(box, false))},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            modelItem = new Item(
                    cursor.getString(1),
                    Long.parseLong(cursor.getString(3)),
                    Boolean.getBoolean(cursor.getString(2))
            );
        } else {
            System.out.println("ERROR!");
            return null;
        }
        return modelItem;
    }

    public Item get_Model_item_by_id(Long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Item modelItem = null;
        Cursor cursor = db.query("task",
                null,
                "id =?",
                new String[]{String.valueOf(id)},
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            modelItem = new Item(
                    cursor.getString(1),
                    Long.parseLong(cursor.getString(3)),
                    Boolean.getBoolean(cursor.getString(2)),
                    Long.parseLong(cursor.getString(0))
            );
        } else {
            System.out.println("ERROR!");
            return null;
        }
        return modelItem;
    }

    public void delete_Model_item_by_id(Long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("task", "id" + " = ?",
                new String[] { String.valueOf(id) });
        db.close();
    }
}
