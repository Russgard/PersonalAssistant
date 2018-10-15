package android.russgar.com.todolist;

import java.util.Date;

/**
 * Created by admin on 2017-12-03.
 */

public class ToDoItem {
    String name;
    Boolean completed;
    String priority;
    String date;
    String time;
    String rDate;
    String rTime;
    String repeat;
    Integer itemID;
    Boolean informed;


    //Constructor
    public  ToDoItem (String name, Boolean completed, String priority, String date, String time, String rDate, String rTime, String repeat, Integer itemID, Boolean informed){
        this.name = name;
        this.priority = priority;
        this.completed = completed;
        this.date = date;
        this.time = time;
        this.rDate = rDate;
        this.rTime = rTime;
        this.repeat = repeat;
        this.itemID = itemID;
        this.informed = informed;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Boolean getCompleted() {
        return completed;
    }
    public void setCompleted(Boolean copleted) {
        this.completed = copleted;
    }

    public String getPriority() {
        return priority;
    }
    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getDate() {return date;}
    public void setDate (String date){this.date = date;}

    public String getTime() {return time;}
    public void setTime(String time) {this.time = time;}

    public String getrDate() {return rDate;}
    public void setrDate (String date){this.rDate = date;}

    public String getrTime() {return rTime;}
    public void setrTime(String time) {this.rTime = time;}

    public String getRepeat() {return repeat;}
    public void setRepeat(String repeat) {this.repeat = repeat;}

    public Integer getItemID() {
        return itemID;
    }
    public void setItemID (Integer itemID){
        this.itemID = itemID;
    }

    public Boolean getInformed() {
        return informed;
    }
    public void setInformed(Boolean informed) {
        this.informed = informed;
    }
}
