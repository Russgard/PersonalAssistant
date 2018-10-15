package android.russgar.com.todolist;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import android.widget.Toast;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class ToDoItemDAO {
    Model myModel;
    DB_Helper db;
    // app context has to be stored in this class somewhere
    Context context;
    public ToDoItemDAO(Context context) {
        this.context = context;
        db = new DB_Helper(this.context);
    }

    // this method gets all todoitems from the database
    public ArrayList<ToDoItem> getToDoItems()
    {
        SQLiteDatabase sqlDb = db.getReadableDatabase();
        //sqlDb.rawQuery("SELECT * FROM " + db.DB_TABLE_NAME);
        String[] columns = new String[]{db.DB_COLUMN_NAME, db.DB_COLUMN_COMPLETED, db.DB_COLUMN_PRIORITY, db.DB_COLUMN_DATE, db.DB_COLUMN_TIME, db.DB_COLUMN_RDATE, db.DB_COLUMN_RTIME, db.DB_COLUMN_REPEAT, db.DB_COLUMN_ITEMID, db.DB_COLUMN_INFORMED};

        Cursor cursor = sqlDb.query(db.DB_TABLE_NAME,columns, null, null, null , null, db.DB_COLUMN_DATE +" ASC, " + db.DB_COLUMN_TIME + " ASC, "+ db.DB_COLUMN_NAME + " ASC");

        ArrayList<ToDoItem> toDoItemArrayList = new ArrayList<>();
        if (cursor.getCount() !=0) {
            cursor.moveToFirst();
            Log.d("AAAA", cursor.getCount() + "");

            while (cursor.moveToNext()) {
                int index_1 = cursor.getColumnIndex(db.DB_COLUMN_NAME);
                int index_2 = cursor.getColumnIndex(db.DB_COLUMN_COMPLETED);
                int index_3 = cursor.getColumnIndex(db.DB_COLUMN_PRIORITY);
                int index_4 = cursor.getColumnIndex(db.DB_COLUMN_DATE);
                int index_5 = cursor.getColumnIndex(db.DB_COLUMN_TIME);
                int index_6 = cursor.getColumnIndex(db.DB_COLUMN_RDATE);
                int index_7 = cursor.getColumnIndex(db.DB_COLUMN_RTIME);
                int index_8 = cursor.getColumnIndex(db.DB_COLUMN_REPEAT);
                int index_9 = cursor.getColumnIndex(db.DB_COLUMN_ITEMID);
                int index_10 = cursor.getColumnIndex(db.DB_COLUMN_INFORMED);

                String name = cursor.getString(index_1);
                Integer completed = cursor.getInt(index_2);
                String priority = cursor.getString(index_3);
                String date = cursor.getString(index_4);
                String time = cursor.getString(index_5);
                String rdate = cursor.getString(index_6);
                String rtime = cursor.getString(index_7);
                String repeat = cursor.getString(index_8);
                Integer itemID = cursor.getInt(index_9);
                Integer informed = cursor.getInt(index_10);
                Boolean Completed = (completed==0) ? false : true;
                Boolean Informed = (informed==0) ? false : true;

                ToDoItem toDoItem = new ToDoItem(name, Completed, priority, date, time, rdate, rtime, repeat, itemID, Informed);
                toDoItemArrayList.add(toDoItem);
                // add todoItem to a list
            }
        }
        cursor.close();
        sqlDb.close();
        // return list
        return toDoItemArrayList;
    }

    public void addToDoItem(ToDoItem item)
    {
        String rawRepeat = item.getRepeat();
        int RID = item.getItemID();
        if (!rawRepeat.contains(":")){
            SQLiteDatabase sqlDb = db.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(db.DB_COLUMN_NAME, item.getName());
            contentValues.put(db.DB_COLUMN_COMPLETED, item.getCompleted());
            contentValues.put(db.DB_COLUMN_PRIORITY, item.getPriority());
            contentValues.put(db.DB_COLUMN_DATE, item.getDate());
            contentValues.put(db.DB_COLUMN_TIME, item.getTime());
            contentValues.put(db.DB_COLUMN_RDATE, item.getrDate());
            contentValues.put(db.DB_COLUMN_RTIME, item.getrTime());
            contentValues.put(db.DB_COLUMN_REPEAT, rawRepeat);
            contentValues.put(db.DB_COLUMN_ITEMID, item.getItemID());
            contentValues.put(db.DB_COLUMN_INFORMED, item.getInformed());
            contentValues.put(db.DB_COLUMN_RID, RID);
            try {
                long response = sqlDb.insertWithOnConflict(db.DB_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                Log.i("AAAA", "Saving.. " + response + "");
            }
            catch (SQLException e){
                Log.d("AAAA", "Error Adding the Item...");
                e.printStackTrace();
            }

            sqlDb.close();
            Log.i("AAAA","107 ToDoiTemDAO finished adding items");
        }
        else {

            String[] repeatArray = rawRepeat.split("\\:");
            String[] hm = repeatArray[1].split("\\-");
            LocalDate untilDate = LocalDate.of(Integer.parseInt(hm[0]),Integer.parseInt(hm[1]),Integer.parseInt(hm[2]));

            String[] mDate = item.getDate().split("\\-");
            String[] rDate = item.getrDate().split("\\-");
            LocalDate mlDate = LocalDate.of(Integer.parseInt(mDate[0]),Integer.parseInt(mDate[1]),Integer.parseInt(mDate[2]));
            LocalDate rlDate = LocalDate.of(Integer.parseInt(rDate[0]),Integer.parseInt(rDate[1]),Integer.parseInt(rDate[2]));
            LocalDate date = mlDate;
            LocalDate remdate = rlDate;
            SQLiteDatabase sqlDb = db.getWritableDatabase();
            int ID = item.getItemID();

            switch (repeatArray[0].toString()){
                case "Once a day":
                    while (!untilDate.isBefore(date)){
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(db.DB_COLUMN_NAME, item.getName());
                        contentValues.put(db.DB_COLUMN_COMPLETED, item.getCompleted());
                        contentValues.put(db.DB_COLUMN_PRIORITY, item.getPriority());
                        contentValues.put(db.DB_COLUMN_DATE, String.format("%s-%s-%s", date.getYear(), (date.getMonthValue()<10)?"0"+date.getMonthValue():date.getMonthValue(), (date.getDayOfMonth()<10)?"0"+date.getDayOfMonth():date.getDayOfMonth()));
                        contentValues.put(db.DB_COLUMN_TIME, item.getTime());
                        contentValues.put(db.DB_COLUMN_RDATE, String.format("%s-%s-%s", remdate.getYear(), (remdate.getMonthValue()<10)?"0"+remdate.getMonthValue():remdate.getMonthValue(), (remdate.getDayOfMonth()<10)?"0"+remdate.getDayOfMonth():remdate.getDayOfMonth()));
                        contentValues.put(db.DB_COLUMN_RTIME, item.getrTime());
                        contentValues.put(db.DB_COLUMN_REPEAT, rawRepeat);
                        contentValues.put(db.DB_COLUMN_ITEMID, ID);
                        contentValues.put(db.DB_COLUMN_INFORMED, item.getInformed());
                        contentValues.put(db.DB_COLUMN_RID, RID);
                        try {
                            long response = sqlDb.insertWithOnConflict(db.DB_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                            Log.i("AAAA", "Saving.. " + response + "");
                        }
                        catch (SQLException e){
                            Log.d("AAAA", "Error Adding the Item...");
                            e.printStackTrace();
                        }
                    date = date.plusDays(1);
                    remdate = remdate.plusDays(1);
                    ID +=1;
                    }
                    sqlDb.close();
                    break;
                case "Once a week":
                    while (!untilDate.isBefore(date)){
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(db.DB_COLUMN_NAME, item.getName());
                        contentValues.put(db.DB_COLUMN_COMPLETED, item.getCompleted());
                        contentValues.put(db.DB_COLUMN_PRIORITY, item.getPriority());
                        contentValues.put(db.DB_COLUMN_DATE, String.format("%s-%s-%s", date.getYear(), (date.getMonthValue()<10)?"0"+date.getMonthValue():date.getMonthValue(), (date.getDayOfMonth()<10)?"0"+date.getDayOfMonth():date.getDayOfMonth()));
                        contentValues.put(db.DB_COLUMN_TIME, item.getTime());
                        contentValues.put(db.DB_COLUMN_RDATE, String.format("%s-%s-%s", remdate.getYear(), (remdate.getMonthValue()<10)?"0"+remdate.getMonthValue():remdate.getMonthValue(), (remdate.getDayOfMonth()<10)?"0"+remdate.getDayOfMonth():remdate.getDayOfMonth()));
                        contentValues.put(db.DB_COLUMN_RTIME, item.getrTime());
                        contentValues.put(db.DB_COLUMN_REPEAT, rawRepeat);
                        contentValues.put(db.DB_COLUMN_ITEMID, ID);
                        contentValues.put(db.DB_COLUMN_INFORMED, item.getInformed());
                        contentValues.put(db.DB_COLUMN_RID, RID);
                        try {
                            long response = sqlDb.insertWithOnConflict(db.DB_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                            Log.i("AAAA", "Saving.. " + response + "");
                        }
                        catch (SQLException e){
                            Log.d("AAAA", "Error Adding the Item...");
                            e.printStackTrace();
                        }
                        date = date.plusWeeks(1);
                        remdate = remdate.plusWeeks(1);
                        ID +=1;
                    }
                    sqlDb.close();
                    break;
                case "Every weekday":
                    while (!untilDate.isBefore(date)){
                        if (date.getDayOfWeek().compareTo(DayOfWeek.SATURDAY) != 0 && date.getDayOfWeek().compareTo(DayOfWeek.SUNDAY) != 0){
                            Log.i("AAAA", "weekday is: "+ date.getDayOfWeek().toString());
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(db.DB_COLUMN_NAME, item.getName());
                            contentValues.put(db.DB_COLUMN_COMPLETED, item.getCompleted());
                            contentValues.put(db.DB_COLUMN_PRIORITY, item.getPriority());
                            contentValues.put(db.DB_COLUMN_DATE, String.format("%s-%s-%s", date.getYear(), (date.getMonthValue()<10)?"0"+date.getMonthValue():date.getMonthValue(), (date.getDayOfMonth()<10)?"0"+date.getDayOfMonth():date.getDayOfMonth()));
                            contentValues.put(db.DB_COLUMN_TIME, item.getTime());
                            contentValues.put(db.DB_COLUMN_RDATE, String.format("%s-%s-%s", remdate.getYear(), (remdate.getMonthValue()<10)?"0"+remdate.getMonthValue():remdate.getMonthValue(), (remdate.getDayOfMonth()<10)?"0"+remdate.getDayOfMonth():remdate.getDayOfMonth()));
                            contentValues.put(db.DB_COLUMN_RTIME, item.getrTime());
                            contentValues.put(db.DB_COLUMN_REPEAT, rawRepeat);
                            contentValues.put(db.DB_COLUMN_ITEMID, ID);
                            contentValues.put(db.DB_COLUMN_INFORMED, item.getInformed());
                            contentValues.put(db.DB_COLUMN_RID, RID);
                            try {
                                long response = sqlDb.insertWithOnConflict(db.DB_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                                Log.i("AAAA", "Saving.. " + response + "");
                            }
                            catch (SQLException e){
                                Log.d("AAAA", "Error Adding the Item...");
                                e.printStackTrace();
                            }
                        }
                        date = date.plusDays(1);
                        remdate = remdate.plusDays(1);
                        ID +=1;
                    }
                    sqlDb.close();
                    break;
                case "Every weekend":
                    while (!untilDate.isBefore(date)){
                        if (date.getDayOfWeek().compareTo(DayOfWeek.SATURDAY) == 0 || date.getDayOfWeek().compareTo(DayOfWeek.SUNDAY) == 0){
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(db.DB_COLUMN_NAME, item.getName());
                            contentValues.put(db.DB_COLUMN_COMPLETED, item.getCompleted());
                            contentValues.put(db.DB_COLUMN_PRIORITY, item.getPriority());
                            contentValues.put(db.DB_COLUMN_DATE, String.format("%s-%s-%s", date.getYear(), (date.getMonthValue()<10)?"0"+date.getMonthValue():date.getMonthValue(), (date.getDayOfMonth()<10)?"0"+date.getDayOfMonth():date.getDayOfMonth()));
                            contentValues.put(db.DB_COLUMN_TIME, item.getTime());
                            contentValues.put(db.DB_COLUMN_RDATE, String.format("%s-%s-%s", remdate.getYear(), (remdate.getMonthValue()<10)?"0"+remdate.getMonthValue():remdate.getMonthValue(), (remdate.getDayOfMonth()<10)?"0"+remdate.getDayOfMonth():remdate.getDayOfMonth()));
                            contentValues.put(db.DB_COLUMN_RTIME, item.getrTime());
                            contentValues.put(db.DB_COLUMN_REPEAT, rawRepeat);
                            contentValues.put(db.DB_COLUMN_ITEMID, ID);
                            contentValues.put(db.DB_COLUMN_INFORMED, item.getInformed());
                            contentValues.put(db.DB_COLUMN_RID, RID);
                            try {
                                long response = sqlDb.insertWithOnConflict(db.DB_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                                Log.i("AAAA", "Saving.. " + response + "");
                            }
                            catch (SQLException e){
                                Log.d("AAAA", "Error Adding the Item...");
                                e.printStackTrace();
                            }
                        }
                        date = date.plusDays(1);
                        remdate = remdate.plusDays(1);
                        ID +=1;
                    }
                    sqlDb.close();
                    break;
                case "Once a month":
                    while (!untilDate.isBefore(date)){
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(db.DB_COLUMN_NAME, item.getName());
                        contentValues.put(db.DB_COLUMN_COMPLETED, item.getCompleted());
                        contentValues.put(db.DB_COLUMN_PRIORITY, item.getPriority());
                        contentValues.put(db.DB_COLUMN_DATE, String.format("%s-%s-%s", date.getYear(), (date.getMonthValue()<10)?"0"+date.getMonthValue():date.getMonthValue(), (date.getDayOfMonth()<10)?"0"+date.getDayOfMonth():date.getDayOfMonth()));
                        contentValues.put(db.DB_COLUMN_TIME, item.getTime());
                        contentValues.put(db.DB_COLUMN_RDATE, String.format("%s-%s-%s", remdate.getYear(), (remdate.getMonthValue()<10)?"0"+remdate.getMonthValue():remdate.getMonthValue(), (remdate.getDayOfMonth()<10)?"0"+remdate.getDayOfMonth():remdate.getDayOfMonth()));
                        contentValues.put(db.DB_COLUMN_RTIME, item.getrTime());
                        contentValues.put(db.DB_COLUMN_REPEAT, rawRepeat);
                        contentValues.put(db.DB_COLUMN_ITEMID, ID);
                        contentValues.put(db.DB_COLUMN_INFORMED, item.getInformed());
                        contentValues.put(db.DB_COLUMN_RID, RID);
                        try {
                            long response = sqlDb.insertWithOnConflict(db.DB_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                            Log.i("AAAA", "Saving.. " + response + "");
                        }
                        catch (SQLException e){
                            Log.d("AAAA", "Error Adding the Item...");
                            e.printStackTrace();
                        }

                        date = date.plusMonths(1);
                        remdate = remdate.plusMonths(1);
                        ID +=1;
                    }
                    sqlDb.close();
                    break;
                default:
                    Integer number = Integer.parseInt(repeatArray[0].substring(6,repeatArray[0].length()-5));
                    while (!untilDate.isBefore(date)){
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(db.DB_COLUMN_NAME, item.getName());
                        contentValues.put(db.DB_COLUMN_COMPLETED, item.getCompleted());
                        contentValues.put(db.DB_COLUMN_PRIORITY, item.getPriority());
                        contentValues.put(db.DB_COLUMN_DATE, String.format("%s-%s-%s", date.getYear(), (date.getMonthValue()<10)?"0"+date.getMonthValue():date.getMonthValue(), (date.getDayOfMonth()<10)?"0"+date.getDayOfMonth():date.getDayOfMonth()));
                        contentValues.put(db.DB_COLUMN_TIME, item.getTime());
                        contentValues.put(db.DB_COLUMN_RDATE, String.format("%s-%s-%s", remdate.getYear(), (remdate.getMonthValue()<10)?"0"+remdate.getMonthValue():remdate.getMonthValue(), (remdate.getDayOfMonth()<10)?"0"+remdate.getDayOfMonth():remdate.getDayOfMonth()));
                        contentValues.put(db.DB_COLUMN_RTIME, item.getrTime());
                        contentValues.put(db.DB_COLUMN_REPEAT, rawRepeat);
                        contentValues.put(db.DB_COLUMN_ITEMID, ID);
                        contentValues.put(db.DB_COLUMN_INFORMED, item.getInformed());
                        contentValues.put(db.DB_COLUMN_RID, RID);
                        try {
                            long response = sqlDb.insertWithOnConflict(db.DB_TABLE_NAME, null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
                            Log.i("AAAA", "Saving.. " + response + "");
                        }
                        catch (SQLException e){
                            Log.d("AAAA", "Error Adding the Item...");
                            e.printStackTrace();
                        }
                        date = date.plusDays(number);
                        remdate = remdate.plusDays(number);
                        ID +=1;
                    }
                    sqlDb.close();

                    break;
            }
        }
    }
    public  void SetInformed (ToDoItem item){
        SQLiteDatabase sqlDb = db.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(db.DB_COLUMN_INFORMED, 1);
        try {
            int count = sqlDb.update(db.DB_TABLE_NAME,values, db.DB_COLUMN_ITEMID + "=" +item.getItemID(), null);
            sqlDb.close();
        }
        catch (SQLException e) {
            Log.d("AAA", "Error Creating Table...");
            e.printStackTrace();
            sqlDb.close();
        }

    }
    // this method deletes the given todoitem from the database
    public void removeToDoItem(int itemID)
    {
        SQLiteDatabase sqlDb = db.getWritableDatabase();
        // remove the todo table
        try {
            sqlDb.delete(db.DB_TABLE_NAME, db.DB_COLUMN_ITEMID + "="+ itemID, null);
        }
        catch (SQLException e) {
            Log.d("AAAA", "Error Creating Table...");
            e.printStackTrace();
        }
        sqlDb.close();

    }
    // this method deletes all todoitems of given todoitem array from the database
    public void removeToDoItems(ArrayList<ToDoItem> items)
    {
        Log.d("AAAA", "Starting removeToDoItems at DAO...");
        SQLiteDatabase sqlDb = db.getWritableDatabase();
        // remove the todo table
        for (ToDoItem item : items
             ) {
            try {
                sqlDb.delete(db.DB_TABLE_NAME, db.DB_COLUMN_ITEMID + "="+ item.itemID, null);
            }
            catch (SQLException e) {
                Log.d("AAAA", "Error Creating Table...");
                e.printStackTrace();
            }

        }
        sqlDb.close();

    }

    // this method updates the given todoitem on the database
    public void updateToDoItem(ToDoItem item)
    {    String rawRepeat = item.getRepeat();
        SQLiteDatabase sqlDb = db.getReadableDatabase();
        String[] columns = new String[]{db.DB_COLUMN_REPEAT, db.DB_COLUMN_ITEMID, db.DB_COLUMN_RID};
        Cursor cursor = sqlDb.query(db.DB_TABLE_NAME,columns, db.DB_COLUMN_ITEMID + "=" + item.getItemID().toString(), null, null, null, null);
        if (cursor.getCount() !=0){
            cursor.moveToFirst();
            int index_1 = cursor.getColumnIndex(db.DB_COLUMN_REPEAT);
            int index_2 = cursor.getColumnIndex(db.DB_COLUMN_ITEMID);
            int index_3 = cursor.getColumnIndex(db.DB_COLUMN_RID);
            String repeat = cursor.getString(index_1);
            Integer itemID = cursor.getInt(index_2);
            Integer itemRID = cursor.getInt(index_3);
            sqlDb.close();
            if (!rawRepeat.contains(":")){
                sqlDb = db.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(db.DB_COLUMN_NAME, item.getName());
                values.put(db.DB_COLUMN_PRIORITY, item.getPriority());
                values.put(db.DB_COLUMN_DATE, item.getDate());
                values.put(db.DB_COLUMN_TIME, item.getTime());
                values.put(db.DB_COLUMN_RDATE, item.getrDate());
                values.put(db.DB_COLUMN_RTIME, item.getrTime());
                values.put(db.DB_COLUMN_REPEAT, item.getRepeat());
                values.put(db.DB_COLUMN_INFORMED,item.getInformed());
                values.put(db.DB_COLUMN_RID, item.getItemID());

                try {
                    int count = sqlDb.update(db.DB_TABLE_NAME,values, db.DB_COLUMN_ITEMID + "=" +item.getItemID(), null);
                    Log.i("AAAA", "Updated");
                }
                catch (SQLException e) {
                    Log.d("AAAA", "Error Updating Item to No Repeat ...");
                    e.printStackTrace();
                }
                sqlDb.close();
            }
            else {
                sqlDb = db.getWritableDatabase();
                ToDoItem nToDoItem = myModel.setTodoData(item.getName(), item.getCompleted(), item.getPriority(), item.getDate(), item.getTime(), item.getrDate(), item.getrTime(), item.getRepeat(), item.getInformed());
                // remove the todo item
                try {
                    sqlDb.delete(db.DB_TABLE_NAME, db.DB_COLUMN_RID + "= ? AND " + db.DB_COLUMN_REPEAT +"= ?", new String[]{itemRID.toString(), repeat.toString()});
                }
                catch (SQLException e) {
                    Log.i("AAAA", "Error Deleting Old Items to Create New Items.."+ repeat);
                    e.printStackTrace();
                }
                sqlDb.close();
                this.addToDoItem(nToDoItem);
            }
        }
        else
            sqlDb.close();
    }
    public Boolean updateCheckbox(ToDoItem item){
        SQLiteDatabase sqDb = db.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(db.DB_COLUMN_COMPLETED, item.getCompleted());
        try {
            int count = sqDb.update(db.DB_TABLE_NAME,values, db.DB_COLUMN_ITEMID + "=" +item.getItemID(), null);
            sqDb.close();
            return true;
        }
        catch (SQLException e) {
            Log.d("AAAA", "Error Creating Table...");
            e.printStackTrace();
            sqDb.close();
            return false;
        }
    }
    public void CloseDB(){
        db.close();
    }
}

