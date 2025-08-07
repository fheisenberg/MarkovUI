package com.ejemplo.markovui;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

public class MarkovDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "markov.db";
    private static final int DB_VERSION = 1;

    public MarkovDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE States (" +
                "state_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "label TEXT UNIQUE)");

        db.execSQL("CREATE TABLE Transitions (" +
                "transition_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "from_state INTEGER, " +
                "to_state INTEGER, " +
                "count INTEGER DEFAULT 1, " +
                "last_updated INTEGER DEFAULT (strftime('%s','now')), " +
                "FOREIGN KEY(from_state) REFERENCES States(state_id), " +
                "FOREIGN KEY(to_state) REFERENCES States(state_id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Aquí se puede manejar actualización de esquema
        db.execSQL("DROP TABLE IF EXISTS Transitions");
        db.execSQL("DROP TABLE IF EXISTS States");
        onCreate(db);
    }

    public void recordTransition(String fromLabel, String toLabel) {
        SQLiteDatabase db = this.getWritableDatabase();

        long fromId = getStateId(fromLabel);
        long toId = getStateId(toLabel);

        if (fromId == -1) fromId = insertState(fromLabel);
        if (toId == -1) toId = insertState(toLabel);

        Cursor cursor = db.rawQuery(
                "SELECT transition_id, count FROM Transitions WHERE from_state = ? AND to_state = ?",
                new String[]{String.valueOf(fromId), String.valueOf(toId)});

        if (cursor.moveToFirst()) {
            int transitionId = cursor.getInt(0);
            int count = cursor.getInt(1) + 1;
            ContentValues values = new ContentValues();
            values.put("count", count);
            values.put("last_updated", System.currentTimeMillis() / 1000);
            db.update("Transitions", values, "transition_id = ?", new String[]{String.valueOf(transitionId)});
        } else {
            ContentValues values = new ContentValues();
            values.put("from_state", fromId);
            values.put("to_state", toId);
            values.put("count", 1);
            values.put("last_updated", System.currentTimeMillis() / 1000);
            db.insert("Transitions", null, values);
        }
        cursor.close();
    }

    private long insertState(String label) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("label", label);
        return db.insert("States", null, values);
    }

    private long getStateId(String label) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT state_id FROM States WHERE label = ?", new String[]{label});
        long id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getLong(0);
        }
        cursor.close();
        return id;
    }

    public String getMostProbableNextButton(String currentLabel) {
        SQLiteDatabase db = this.getReadableDatabase();
        String nextLabel = null;

        Cursor cursor = db.rawQuery(
                "SELECT s.label, t.count FROM Transitions t " +
                        "JOIN States s ON t.to_state = s.state_id " +
                        "WHERE t.from_state = (SELECT state_id FROM States WHERE label = ?) " +
                        "ORDER BY t.count DESC LIMIT 1",
                new String[]{currentLabel});

        if (cursor.moveToFirst()) {
            nextLabel = cursor.getString(0);
        }
        cursor.close();
        return nextLabel;
    }

    public String obtenerUltimoEstado() {
        SQLiteDatabase db = this.getReadableDatabase();
        String ultimoEstado = null;

        try {
            Cursor cursor = db.rawQuery(
                    "SELECT s.state_name FROM transitions t " +
                            "JOIN states s ON t.to_state = s.id " +
                            "ORDER BY t.rowid DESC LIMIT 1", null);

            if (cursor != null && cursor.moveToFirst()) {
                ultimoEstado = cursor.getString(0);
            }

            if (cursor != null) cursor.close();
        } catch (Exception e) {
            Log.e("DB_ERROR", "Error al obtener último estado: " + e.getMessage());
        } finally {
            db.close();
        }

        return ultimoEstado;
    }
}
//public class MarkovDatabaseHelper extends SQLiteOpenHelper {
//    private static final String DB_NAME = "markov.db";
//    private static final int DB_VERSION = 1;
//
//    public MarkovDatabaseHelper(Context context) {
//        super(context, DB_NAME, null, DB_VERSION);
//    }
//
//    @Override
//    public void onCreate(SQLiteDatabase db) {
//        db.execSQL("CREATE TABLE States (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE)");
//        db.execSQL("CREATE TABLE Transitions (" +
//                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
//                "from_state INTEGER, to_state INTEGER, " +
//                "count INTEGER, " +
//                "last_updated DATETIME DEFAULT CURRENT_TIMESTAMP, " +
//                "FOREIGN KEY(from_state) REFERENCES States(id), " +
//                "FOREIGN KEY(to_state) REFERENCES States(id))");
//    }
//
//    public void insertOrUpdateTransition(String from, String to) {
//        SQLiteDatabase db = getWritableDatabase();
//        long fromId = getStateId(from);
//        long toId = getStateId(to);
//
//        Cursor cursor = db.rawQuery("SELECT id, count FROM Transitions WHERE from_state=? AND to_state=?",
//                new String[]{String.valueOf(fromId), String.valueOf(toId)});
//        if (cursor.moveToFirst()) {
//            int id = cursor.getInt(0);
//            int count = cursor.getInt(1) + 1;
//            ContentValues cv = new ContentValues();
//            cv.put("count", count);
//            cv.put("last_updated", System.currentTimeMillis());
//            db.update("Transitions", cv, "id=?", new String[]{String.valueOf(id)});
//        } else {
//            ContentValues cv = new ContentValues();
//            cv.put("from_state", fromId);
//            cv.put("to_state", toId);
//            cv.put("count", 1);
//            db.insert("Transitions", null, cv);
//        }
//        cursor.close();
//    }
//
//    private long getStateId(String name) {
//        SQLiteDatabase db = getWritableDatabase();
//        Cursor cursor = db.rawQuery("SELECT id FROM States WHERE name=?", new String[]{name});
//        if (cursor.moveToFirst()) {
//            long id = cursor.getLong(0);
//            cursor.close();
//            return id;
//        } else {
//            ContentValues cv = new ContentValues();
//            cv.put("name", name);
//            long id = db.insert("States", null, cv);
//            cursor.close();
//            return id;
//        }
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        // Upgrade logic here
//    }
//
//    public String getMostProbableNextButton(String currentButton) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String nextButton = null;
//        int maxCount = 0;
//
//        Cursor cursor = db.rawQuery(
//                "SELECT to_state, count FROM Transitions t " +
//                        "JOIN States s ON t.to_state = s.state_id " +
//                        "WHERE from_state = (SELECT state_id FROM States WHERE label = ?) " +
//                        "ORDER BY count DESC LIMIT 1",
//                new String[]{currentButton});
//
//        if (cursor.moveToFirst()) {
//            // Obtenemos el label del estado siguiente
//            long toStateId = cursor.getLong(cursor.getColumnIndexOrThrow("to_state"));
//
//            Cursor c2 = db.rawQuery("SELECT label FROM States WHERE state_id = ?", new String[]{String.valueOf(toStateId)});
//            if (c2.moveToFirst()) {
//                nextButton = c2.getString(0);
//            }
//            c2.close();
//        }
//        cursor.close();
//        return nextButton;
//    }
//
//
////    public String getMostProbableNextButton(String currentButton) {
////        SQLiteDatabase db = this.getReadableDatabase();
////        String nextButton = null;
////        int maxCount = 0;
////
////        Cursor cursor = db.rawQuery("SELECT next_button, COUNT(*) as freq FROM transitions WHERE current_button = ? GROUP BY next_button", new String[]{currentButton});
////
////        while (cursor.moveToNext()) {
////            String candidate = cursor.getString(cursor.getColumnIndexOrThrow("next_button"));
////            int freq = cursor.getInt(cursor.getColumnIndexOrThrow("freq"));
////            if (freq > maxCount) {
////                maxCount = freq;
////                nextButton = candidate;
////            }
////        }
////
////        cursor.close();
////        db.close();
////
////        return nextButton;
////    }
//
//}