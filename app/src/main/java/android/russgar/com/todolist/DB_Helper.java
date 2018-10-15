package android.russgar.com.todolist;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DB_Helper extends SQLiteOpenHelper {
    private  static  final String DB_NAME = "ToDoList";
    private  static  final Integer DB_VERSION = 4;

    public  static final String DB_TABLE_NAME = "ToDoItems";
    public  static final String DB_COLUMN_NAME = "NAME";
    public  static final String DB_COLUMN_COMPLETED = "COMPLETED";
    public  static final String DB_COLUMN_PRIORITY = "PRIORITY";
    public  static final String DB_COLUMN_DATE = "DATE";
    public  static final String DB_COLUMN_TIME = "TIME";
    public  static final String DB_COLUMN_RDATE = "RDATE";
    public  static final String DB_COLUMN_RTIME = "RTIME";
    public  static final String DB_COLUMN_REPEAT = "REPEAT";
    public  static final String DB_COLUMN_ITEMID = "ITEMID";
    public  static final String DB_COLUMN_INFORMED = "INFORMED";
    public  static final String DB_COLUMN_RID = "RID";

    // database is created!
    public DB_Helper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // remove the todo table
        String query = String.format("DROP TABLE IF EXISTS %s", DB_TABLE_NAME);
        db.execSQL(query);
        // recreate the new todo table
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    // Create Table that stores name, compleated, priority, date, tome, rdate, rtime, repeat.
        Log.d("AAAA", "DbHelper.onCreate is being called");
        String query = String.format("CREATE TABLE IF NOT EXISTS %s (ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                " %s TEXT, %s INTEGER, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s TEXT, %s INTEGER, %s INTEGER, %s INTEGER)", DB_TABLE_NAME, DB_COLUMN_NAME, DB_COLUMN_COMPLETED, DB_COLUMN_PRIORITY, DB_COLUMN_DATE, DB_COLUMN_TIME, DB_COLUMN_RDATE, DB_COLUMN_RTIME, DB_COLUMN_REPEAT, DB_COLUMN_ITEMID, DB_COLUMN_INFORMED, DB_COLUMN_RID );
        try {
            db.execSQL(query);
        }
        catch (SQLException e)
        {
            Log.d("AAAA", "Error Creating Table..." );
            e.printStackTrace();
        }
    }
}
