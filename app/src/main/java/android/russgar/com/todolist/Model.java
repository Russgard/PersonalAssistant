package android.russgar.com.todolist;

import android.app.Activity;
import android.content.Context;
import android.database.SQLException;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.AsyncTask;
import android.os.BadParcelableException;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import java.sql.Array;
import java.sql.Time;
import java.time.Clock;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;

/**
 * Created by admin on 2017-12-03.
 */

public class Model {
    ArrayList<ToDoItem> overdueItemArrayList = new ArrayList<>();
    ArrayList<ToDoItem> todayItemArrayList = new ArrayList<>();
    ArrayList<ToDoItem> tomorrowItemArrayList = new ArrayList<>();
    ArrayList<ToDoItem> weekItemArrayList = new ArrayList<>();
    ArrayList<ToDoItem> monthItemArrayList = new ArrayList<>();
    ArrayList<ToDoItem> otherItemArrayList = new ArrayList<>();
    ArrayList<String> textToVoice;
    Integer numberOfItems = 0;
    String HelloString = ((LocalTime.now().isAfter(LocalTime.now().withHour(4).withMinute(0).withSecond(0)) && LocalTime.now().isBefore(LocalTime.now().withHour(11).withMinute(59).withSecond(59)))?"Good Morning":(LocalTime.now().isAfter(LocalTime.now().withHour(11).withMinute(59).withSecond(59))&&LocalTime.now().isBefore(LocalTime.now().withHour(17).withMinute(30).withSecond(0)))?"Good Afternoon":(LocalTime.now().isAfter(LocalTime.now().withHour(17).withMinute(30).withSecond(0))&&LocalTime.now().isBefore(LocalTime.now().withHour(22).withMinute(0).withSecond(0)))?"Good Evening":(LocalTime.now().isAfter(LocalTime.now().withHour(22).withMinute(0).withSecond(0))&&LocalTime.now().isBefore(LocalTime.now().withHour(4).withMinute(0).withSecond(0)))?"Good Night":"Hello")+", Rustam!";
    String[] HowRUDoing = {"How are you doing?", "How are you doing today?", "How are you?","How are you today","What's up?",""};
    String Order = "";
    MainActivity mainActivity = new MainActivity();
    public ArrayList<String> SayHello() {
        int aNumber = (int) (6 * Math.random());
        ArrayList<String> Alist = new ArrayList<>();
        Alist.add(HelloString);
        Alist.add(HowRUDoing[aNumber]);
        return Alist;
    }
    public String Sorry() {
        String[] SorryReason ={"I couldn't get what you said, bey!", "I can't understand your accent, bey!", "I can't understand your strange accent, bey!", "I can't understand what you are talking about, bey!"};
        int aNumber = (int) (4 * Math.random());
        String Text = "Sorry, " +SorryReason[aNumber];
        return Text;
    }

    public String ResponceOne(String s) {
        Log.d("AAAA","Model.ResponceOne:"+s);
       List<String>StringToReturn = new ArrayList<>();

       //List<String> keyPrases = Arrays.asList(words);
       //List<String> numlist = Arrays.asList(words);

        if (s.contains("I")){
            int inddexOfI = s.indexOf("I");
            if ((s.charAt(inddexOfI+2) == 'm' || s.substring(inddexOfI+2, inddexOfI+4).matches("am")) && (s.contains("good") || s.contains("fine") || s.contains("okay"))) {
                StringToReturn.add("Nice to hear");
            }
        }

        if (s.toLowerCase().contains("how are you")|| s.toLowerCase().contains("are you okay")||s.toLowerCase().contains("What's up")){
                StringToReturn.add("I'm okay");
        }

         if ((s.contains("gexa") || s.contains("Gexa"))  && (s.contains("delete")|| s.contains("remove")||s.contains("clear") ) && (s.contains("events")||s.contains("records")||s.contains("plans")||s.contains("items"))&&(s.contains("expired")||s.contains("old")||s.contains("overdue")||s.contains("passed"))){
            if (overdueItemArrayList.size()==0){
                StringToReturn.add("Hmm, you have no any averdue records. What else can I do for you today?");
            }
            else{
                StringToReturn.add("Okay, I am removing all overdue records, please confirm");
                Order = "Remove?";
            }
         }
        if (s.contains("what") && (s.substring(5,6).matches("s") || s.substring(5,7).matches("is")) && s.contains("your name")){
            StringToReturn.add("I am Gexa!");
        }
        if (s.equalsIgnoreCase("who are you")){
            StringToReturn.add("I am Gexa, your personal assistant!");
        }
        if (s.contains("what can you do") || s.contains("how can you help me")||s.contains("what are you able to do")){
            StringToReturn.add("I am a very young, and I am still learning to do everything you wish to do with your mobile device!");
        }

         if ((s.contains("yes") && (s.contains("I do") || s.contains("confirmed") || s.contains("remove them")||s.contains("do it") ))){
             StringToReturn.add("Okay, I have deleted all your overdue records. What else can I do for you?");
             Order = "Remove!";
         }
         if (s.contains("bye") || s.contains("see you")|| s.contains("shut up")|| s.contains("get away")|| s.contains("go away") || s.contains("do nothing")) {
            Order = "bye";
            StringToReturn.add("Okay, bye-bye");

         }

         if (s.contains("my")&&(s.contains("plans")||s.contains("events")||s.contains("records")) &&(s.contains("tell")|| s.contains("remind")||s.contains("read"))&& (s.contains("for"))){
            if (s.contains("today")){
                StringToReturn.add("Okay!");
                StringToReturn.add(TextToVoice("today").toString());
            }
            else if (s.contains("tomorrow")){
                StringToReturn.add("Okay!");
                TextToVoice("tomorrow");
             }
            else if (s.contains("old")||s.contains("expired")||s.contains("passed")||s.contains("overdue")){
                StringToReturn.add("Okay!");
                TextToVoice("overdue");
            }
            else if (s.contains("this week")){
                StringToReturn.add("Okay!");
                TextToVoice("week");

            }
            else if (s.contains("this month")){
                StringToReturn.add("Okay!");
                StringToReturn.add(TextToVoice("month").toString());
            }
            else {
                StringToReturn.add(Sorry());
            }
         }

         switch (Order){
             case "":
                 StringToReturn.add("How can I help you?");
                 break;
             case "Remove!":
                 break;
             case "Bye":
                 break;
         }


        String words = StringToReturn.toString();
        return words;
    }

    public String ResponceDeleted(){

      return "I have deleted all overdue records.";

    }


    public void DeleteArrays(){
        overdueItemArrayList.clear();
        todayItemArrayList.clear();
        tomorrowItemArrayList.clear();
        weekItemArrayList.clear();
        monthItemArrayList.clear();
        otherItemArrayList.clear();
        numberOfItems = 0;

    }
    public  ArrayList<String> TextToVoice(String t){
        textToVoice = new ArrayList<>();
        switch (t){
            case "overdue":
                if (!overdueItemArrayList.isEmpty()){
                    ArrayList <ToDoItem> overvdueText = new ArrayList();
                    for (int i = 0; i<overdueItemArrayList.size(); ++i){
                        ToDoItem toDoItem = overdueItemArrayList.get(i);
                        if (!toDoItem.getCompleted())
                            overvdueText.add(toDoItem);
                    }
                    if (!overvdueText.isEmpty()){
                        String etext = (overvdueText.size()>1)?"There are overdue events":"There is an overdue event";
                        textToVoice.add(etext);
                        for (int i = 0; i<overvdueText.size(); ++i){
                            ToDoItem item = overvdueText.get(i);
                            String eventTime = item.getTime();
                            String eventMin = (eventTime.substring(3).equalsIgnoreCase("00"))?"":eventTime.substring(2);
                            int EventHourInt = Integer.parseInt(eventTime.substring(0,2));
                            if(EventHourInt == 0){
                                eventTime = "12"+ eventMin+"AM";
                            }
                            else{
                                if (EventHourInt == 12){
                                    eventTime = "12"+ eventMin+"PM";
                                }
                                else{
                                    if (EventHourInt < 12){
                                        eventTime = String.valueOf(EventHourInt) + eventMin + "AM";
                                    }
                                    else{
                                        eventTime = String.valueOf(EventHourInt - 12) + eventMin + "PM";
                                    }
                                }
                            }

                            Log.d("AAAA", "EventTime is ... "+ eventTime);
                            String text = "On "+item.getDate()+" at "+ eventTime+ " : " + item.getName();
                            textToVoice.add(text);
                        }
                    }
                }
                else{
                    textToVoice.add("Hmmm... You have no overdue plans.") ;
                }
                break;

            case "today":
                if (!todayItemArrayList.isEmpty()){
                    ArrayList <ToDoItem> todayText = new ArrayList();
                    for (int i = 0; i<todayItemArrayList.size(); ++i){
                        ToDoItem toDoItem = todayItemArrayList.get(i);
                        if (!toDoItem.getCompleted())
                            todayText.add(toDoItem);
                    }
                    if (!todayText.isEmpty()) {
                        String etext = (todayText.size()==1)?"You have a plan for today!":(todayText.size()==2)?"You have two plans for today!":(todayText.size()==3)?"You have three plans for today!":"You have some plans for today!";
                        textToVoice.add(etext);
                        for (int i = 0; i < todayText.size(); ++i) {
                            ToDoItem item = todayText.get(i);
                            String importantText = (item.getPriority().equals("Hi"))? ". It is a very important event!":".";
                            String eventTime = item.getTime();
                            String eventMin = (eventTime.substring(3).equalsIgnoreCase("00"))?"":eventTime.substring(2);
                            int EventHourInt = Integer.parseInt(eventTime.substring(0,2));
                            if(EventHourInt == 0){
                                eventTime = "12"+ eventMin+"AM";
                            }
                            else{
                                if (EventHourInt == 12){
                                    eventTime = "12"+ eventMin+"PM";
                                }
                                else{
                                    if (EventHourInt < 12){
                                        eventTime = String.valueOf(EventHourInt) + eventMin + "AM";
                                    }
                                    else{
                                        eventTime = String.valueOf(EventHourInt - 12) + eventMin + "PM";
                                    }
                                }
                            }
                            String text = "Today, at " + eventTime + " : " + item.getName()+importantText;
                            textToVoice.add(text);
                        }
                    }
                    else
                        textToVoice.add("You have no outstanding plans for today!");
                }
                else {
                    textToVoice.add("You have no plans for today!");
                }
                break;

            case "tomorrow":
                if (!tomorrowItemArrayList.isEmpty()) {
                    ArrayList <ToDoItem> tomorrowText = new ArrayList();
                    for (int i = 0; i < tomorrowItemArrayList.size(); ++i) {
                        ToDoItem todoItem = tomorrowItemArrayList.get(i);
                        if (!todoItem.getCompleted())
                            tomorrowText.add(todoItem);
                    }
                    if (!tomorrowText.isEmpty()){
                        String etext = (tomorrowText.size()==1)?"You have a plan for tomorrow!":(tomorrowText.size()==2)?"You have two plans for tomorrow!":(tomorrowText.size()==3)?"You have three plans for tomorrow!":"You have some plans for tomorrow!";
                        textToVoice.add(etext);
                        for (int i=0; i<tomorrowText.size(); ++i) {
                            ToDoItem item = tomorrowText.get(i);
                            String importantText = (item.getPriority().equals("Hi"))? ". It is a very important plan!":".";
                            String eventTime = item.getTime();
                            String eventMin = (eventTime.substring(3).equalsIgnoreCase("00"))?"":eventTime.substring(2);
                            int EventHourInt = Integer.parseInt(eventTime.substring(0,2));
                            if(EventHourInt == 0){
                                eventTime = "12"+ eventMin+"AM";
                            }
                            else{
                                if (EventHourInt == 12){
                                    eventTime = "12"+ eventMin+"PM";
                                }
                                else{
                                    if (EventHourInt < 12){
                                        eventTime = String.valueOf(EventHourInt) + eventMin + "AM";
                                    }
                                    else{
                                        eventTime = String.valueOf(EventHourInt - 12) + eventMin + "PM";
                                    }
                                }
                            }
                            String text = "Tomorrow, at " + eventTime + " : " + item.getName()+importantText;
                            textToVoice.add(text);
                        }
                    }
                    else
                        textToVoice.add("Congratulations! You have already done all your plans for tomorrow!");
                } else{
                    textToVoice.add("You have no plans for tomorrow!");
                }
                break;
            case "week":
                if (!weekItemArrayList.isEmpty()) {
                    ArrayList <ToDoItem> weekText = new ArrayList();
                    for (int i = 0; i < weekItemArrayList.size(); ++i) {
                        ToDoItem todoItem = weekItemArrayList.get(i);
                        if (!todoItem.getCompleted())
                            weekText.add(todoItem);
                    }
                    if (!weekText.isEmpty()){
                        String etext = (weekText.size()==1)?"You have a plan for this week!":(weekText.size()==2)?"You have two plans for this week!":(weekText.size()==3)?"You have three plans for this week!":"You have some plans for this week!";
                        textToVoice.add(etext);
                        for (int i=0; i<weekText.size(); ++i) {
                            ToDoItem item = weekText.get(i);
                            String importantText = (item.getPriority().equals("Hi"))? ". It is a very important plan!":".";
                            String eventTime = item.getTime();
                            String eventMin = (eventTime.substring(3).equalsIgnoreCase("00"))?"":eventTime.substring(2);
                            int EventHourInt = Integer.parseInt(eventTime.substring(0,2));
                            if(EventHourInt == 0){
                                eventTime = "12"+ eventMin+"AM";
                            }
                            else{
                                if (EventHourInt == 12){
                                    eventTime = "12"+ eventMin+"PM";
                                }
                                else{
                                    if (EventHourInt < 12){
                                        eventTime = String.valueOf(EventHourInt) + eventMin + "AM";
                                    }
                                    else{
                                        eventTime = String.valueOf(EventHourInt - 12) + eventMin + "PM";
                                    }
                                }
                            }
                            String text = "On "+item.getDate()+" at " + eventTime + " : " + item.getName()+importantText;
                            textToVoice.add(text);
                        }
                    }
                    else
                        textToVoice.add("Congratulations! You have already done all your plans for this week!");
                } else{
                    textToVoice.add("You have no plans for this week!");
                }
                break;
            case "month":
                if (!monthItemArrayList.isEmpty()) {
                    ArrayList <ToDoItem> monthText = new ArrayList();
                    for (int i = 0; i < monthItemArrayList.size(); ++i) {
                        ToDoItem todoItem = monthItemArrayList.get(i);
                        if (!todoItem.getCompleted())
                            monthText.add(todoItem);
                    }
                    if (!monthText.isEmpty()){
                        String etext = (monthText.size()==1)?"You have a plan for this month!":(monthText.size()==2)?"You have two plans for this month!":(monthText.size()==3)?"You have three plans for this month!":"You have some plans for this month!";
                        textToVoice.add(etext);
                        for (int i=0; i<monthText.size(); ++i) {
                            ToDoItem item = monthText.get(i);
                            String importantText = (item.getPriority().equals("Hi"))? ". It is a very important plan!":".";
                            String eventTime = item.getTime();
                            String eventMin = (eventTime.substring(3).equalsIgnoreCase("00"))?"":eventTime.substring(2);
                            int EventHourInt = Integer.parseInt(eventTime.substring(0,2));
                            if(EventHourInt == 0){
                                eventTime = "12"+ eventMin+"AM";
                            }
                            else{
                                if (EventHourInt == 12){
                                    eventTime = "12"+ eventMin+"PM";
                                }
                                else{
                                    if (EventHourInt < 12){
                                        eventTime = String.valueOf(EventHourInt) + eventMin + "AM";
                                    }
                                    else{
                                        eventTime = String.valueOf(EventHourInt - 12) + eventMin + "PM";
                                    }
                                }
                            }
                            String text = "On "+item.getDate()+" at " + eventTime + " : " + item.getName()+importantText;
                            textToVoice.add(text);
                        }
                    }
                    else
                        textToVoice.add("Congratulations! You have already done all your plans for this month!");
                } else{
                    textToVoice.add("You have no plans for this month!");
                }
                break;

        }


//        if (!overdueItemArrayList.isEmpty()){
//            ArrayList <ToDoItem> overvdueText = new ArrayList();
//            for (int i = 0; i<overdueItemArrayList.size(); ++i){
//                ToDoItem toDoItem = overdueItemArrayList.get(i);
//                if (!toDoItem.getCompleted())
//                    overvdueText.add(toDoItem);
//            }
//            if (!overvdueText.isEmpty()){
//                String etext = (overvdueText.size()>1)?"There are overdue events":"There is an overdue event";
//                textToVoice.add(etext);
//                for (int i = 0; i<overvdueText.size(); ++i){
//                    ToDoItem item = overvdueText.get(i);
//                    String eventTime = item.getTime();
//                    String eventMin = (eventTime.substring(3).equalsIgnoreCase("00"))?"":eventTime.substring(2);
//                    int EventHourInt = Integer.parseInt(eventTime.substring(0,2));
//                    if(EventHourInt == 0){
//                        eventTime = "12"+ eventMin+"AM";
//                    }
//                    else{
//                        if (EventHourInt == 12){
//                            eventTime = "12"+ eventMin+"PM";
//                        }
//                        else{
//                            if (EventHourInt < 12){
//                                eventTime = String.valueOf(EventHourInt) + eventMin + "AM";
//                            }
//                            else{
//                                eventTime = String.valueOf(EventHourInt - 12) + eventMin + "PM";
//                            }
//                        }
//                    }
//
//                    Log.d("AAAA", "EventTime is ... "+ eventTime);
//                    String text = "On "+item.getDate()+" at "+ eventTime+ " : " + item.getName();
//                    textToVoice.add(text);
//                }
//            }
//        }
//
//        if (!todayItemArrayList.isEmpty()){
//            ArrayList <ToDoItem> todayText = new ArrayList();
//            for (int i = 0; i<todayItemArrayList.size(); ++i){
//                ToDoItem toDoItem = todayItemArrayList.get(i);
//                if (!toDoItem.getCompleted())
//                    todayText.add(toDoItem);
//            }
//            if (!todayText.isEmpty()) {
//                String etext = (todayText.size()==1)?"You have a plan for today!":(todayText.size()==2)?"You have two plans for today!":(todayText.size()==3)?"You have three plans for today!":"You have some plans for today!";
//                textToVoice.add(etext);
//                for (int i = 0; i < todayText.size(); ++i) {
//                    ToDoItem item = todayText.get(i);
//                    String importantText = (item.getPriority().equals("Hi"))? ". It is a very important event!":".";
//                    String eventTime = item.getTime();
//                    String eventMin = (eventTime.substring(3).equalsIgnoreCase("00"))?"":eventTime.substring(2);
//                    int EventHourInt = Integer.parseInt(eventTime.substring(0,2));
//                    if(EventHourInt == 0){
//                        eventTime = "12"+ eventMin+"AM";
//                    }
//                    else{
//                        if (EventHourInt == 12){
//                            eventTime = "12"+ eventMin+"PM";
//                        }
//                        else{
//                            if (EventHourInt < 12){
//                                eventTime = String.valueOf(EventHourInt) + eventMin + "AM";
//                            }
//                            else{
//                                eventTime = String.valueOf(EventHourInt - 12) + eventMin + "PM";
//                            }
//                        }
//                    }
//                    String text = "Today, at " + eventTime + " : " + item.getName()+importantText;
//                    textToVoice.add(text);
//                }
//            }
//            else
//                textToVoice.add("You have no outstanding plans for today!");
//        }
//        else
//            textToVoice.add("You have no plans for today!");

//        if (LocalTime.now().isAfter(LocalTime.now().withHour(17).withMinute(0).withSecond(0))) {
//            if (!tomorrowItemArrayList.isEmpty()) {
//                ArrayList <ToDoItem> tomorrowText = new ArrayList();
//                for (int i = 0; i < tomorrowItemArrayList.size(); ++i) {
//                    ToDoItem todoItem = tomorrowItemArrayList.get(i);
//                    if (!todoItem.getCompleted())
//                        tomorrowText.add(todoItem);
//                }
//                if (!tomorrowText.isEmpty()){
//                    String etext = (tomorrowText.size()==1)?"You have a plan for tomorrow!":(tomorrowText.size()==2)?"You have two plans for tomorrow!":(tomorrowText.size()==3)?"You have three plans for tomorrow!":"You have some plans for tomorrow!";
//                    textToVoice.add(etext);
//                    for (int i=0; i<tomorrowText.size(); ++i) {
//                        ToDoItem item = tomorrowText.get(i);
//                        String importantText = (item.getPriority().equals("Hi"))? ". It is a very important plan!":".";
//                        String eventTime = item.getTime();
//                        String eventMin = (eventTime.substring(3).equalsIgnoreCase("00"))?"":eventTime.substring(2);
//                        int EventHourInt = Integer.parseInt(eventTime.substring(0,2));
//                        if(EventHourInt == 0){
//                            eventTime = "12"+ eventMin+"AM";
//                        }
//                        else{
//                            if (EventHourInt == 12){
//                                eventTime = "12"+ eventMin+"PM";
//                            }
//                            else{
//                                if (EventHourInt < 12){
//                                    eventTime = String.valueOf(EventHourInt) + eventMin + "AM";
//                                }
//                                else{
//                                    eventTime = String.valueOf(EventHourInt - 12) + eventMin + "PM";
//                                }
//                            }
//                        }
//                        String text = "Tomorrow, at " + eventTime + " : " + item.getName()+importantText;
//                        textToVoice.add(text);
//                    }
//                }
//                else
//                    textToVoice.add("Congratulations! You have already done all your plans for tomorrow!");
//            } else
//                textToVoice.add("You have no plans for tomorrow!");
//        }
//
//        if (!weekItemArrayList.isEmpty()){
//            for (int i = 0; i<weekItemArrayList.size(); ++i){
//                ToDoItem item = weekItemArrayList.get(i);
//                String text = "This week, on "+item.getDate()+" at "+ item.getTime()+ " : " + item.getName();
//                textToVoice.add(text);
//            }
//        }
//        else
//            textToVoice.add("You have no other plans for this week!");
//
//        if (!monthItemArrayList.isEmpty()){
//            for (int i = 0; i<monthItemArrayList.size(); ++i){
//                ToDoItem item = monthItemArrayList.get(i);
//                String text = "This month, on "+item.getDate()+" at "+ item.getTime()+ " : " + item.getName();
//                textToVoice.add(text);
//            }
//        }
//        else
//            textToVoice.add("You have no other plans for this month!");
//
//        if (!otherItemArrayList.isEmpty()){
//            textToVoice.add("You have other plans, for the period starting from next month!");
//            for (int i = 0; i<otherItemArrayList.size(); ++i){
//                ToDoItem item = otherItemArrayList.get(i);
//                String text = "On "+item.getDate()+" at "+ item.getTime()+ " : " + item.getName();
//                textToVoice.add(text);
//            }
//        }
//        else
//            textToVoice.add("You have no other plans!");
        return textToVoice;
    }
    public ArrayList<ToDoItem> getTodoData(String timePeriod) {

            if (timePeriod.contentEquals("overdue")){
                return overdueItemArrayList;}
            else if (timePeriod.contentEquals("today"))
                return todayItemArrayList;
            else if (timePeriod.contentEquals("tomorrow"))
                return tomorrowItemArrayList;
            else if (timePeriod.contentEquals("week"))
                return weekItemArrayList;
            else if (timePeriod.contentEquals("month"))
                return monthItemArrayList;
            else
                return otherItemArrayList;
    }

    public Date DateToStart (){
        Date date = new Date();
        Calendar c = new GregorianCalendar();
        c.setTime(date);
        c.add(Calendar.MINUTE, 1);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }
    public ArrayList<ToDoItem> StartAlarm(){
        LocalDateTime now = LocalDateTime.now();
        ArrayList<ToDoItem> result = new ArrayList<>();
        ArrayList <ArrayList> periods = new ArrayList<>();;
        periods.add(todayItemArrayList);
        periods.add(tomorrowItemArrayList);
        periods.add(weekItemArrayList);
        periods.add(monthItemArrayList);
        periods.add(otherItemArrayList);
        for (int i=0; i<periods.size(); ++i){
            ArrayList <ToDoItem> toDoItemArrayList = periods.get(i);
            for (int j=0; j < toDoItemArrayList.size(); j++) {
                ToDoItem toDoItem = toDoItemArrayList.get(j);
                String dt = toDoItem.getrDate() + " "+toDoItem.getrTime();
                String[] hm =  dt.split("\\:|\\-|\\s+");
                LocalTime lTime = LocalTime.of(Integer.parseInt(hm[3]),Integer.parseInt(hm[4]));
                LocalDate lDate = LocalDate.of(Integer.parseInt(hm[0]),Integer.parseInt(hm[1]),Integer.parseInt(hm[2]));
                LocalDateTime d = LocalDateTime.of(lDate,lTime);
                if (!now.isBefore(d)){
                    result.add(toDoItem);
                }
            }
        }
        return result;
    }

    public ToDoItem setTodoData(String name, Boolean completed, String priority, String date, String time, String rDate, String rTime, String repeat, Boolean informed){
        ToDoItem toDoItem = new ToDoItem(name, completed, priority, date, time, rDate, rTime, repeat, numberOfItems+1, informed);

        String dt = date+" "+time;
        String[] hm =  dt.split("\\:|\\-|\\s+");
        LocalTime lTime = LocalTime.of(Integer.parseInt(hm[3]),Integer.parseInt(hm[4]));
        LocalDate lDate = LocalDate.of(Integer.parseInt(hm[0]),Integer.parseInt(hm[1]),Integer.parseInt(hm[2]));
        LocalDateTime d = LocalDateTime.of(lDate,lTime);

        LocalDateTime timePoint = LocalDateTime.now();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate today = LocalDate.now();
        LocalDate lastDayOfWeek;
        LocalTime lastSec = LocalTime.of(23,59,59);
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        LocalDateTime weekend;
        switch (dayOfWeek){
            case MONDAY:
                lastDayOfWeek = LocalDate.now().plusDays(6);
                break;
            case TUESDAY:
                lastDayOfWeek = LocalDate.now().plusDays(5);
                break;
            case WEDNESDAY:
                lastDayOfWeek = LocalDate.now().plusDays(4);
                break;
            case THURSDAY:
                lastDayOfWeek = LocalDate.now().plusDays(3);
                break;
            case FRIDAY:
                lastDayOfWeek = LocalDate.now().plusDays(2);
                break;
            case SATURDAY:
                lastDayOfWeek = LocalDate.now().plusDays(1);
                break;
            case SUNDAY:
                lastDayOfWeek = LocalDate.now();
                break;
            default:
                lastDayOfWeek = LocalDate.now();
                break;
        }
        weekend = LocalDateTime.of(lastDayOfWeek,lastSec);

        if (timePoint.isAfter(d)){
            overdueItemArrayList.add(toDoItem);
        }
        else if (timePoint.isBefore(d) && today.isEqual(lDate))
            todayItemArrayList.add(toDoItem);
        else if (tomorrow.isEqual(lDate))
            tomorrowItemArrayList.add(toDoItem);
        else if (tomorrow.isBefore(lDate) && weekend.isAfter(d))
            weekItemArrayList.add(toDoItem);
        else if (weekend.isBefore(d) && d.getMonth()==timePoint.getMonth() && d.getYear() == timePoint.getYear())
            monthItemArrayList.add(toDoItem);
        else
            otherItemArrayList.add(toDoItem);
        numberOfItems += 1;
        return toDoItem;
    }

    public void updateTodoData(ToDoItem item){
        String dt = item.getDate()+" "+item.getTime();
        String[] hm =  dt.split("\\:|\\-|\\s+");
        LocalTime lTime = LocalTime.of(Integer.parseInt(hm[3]),Integer.parseInt(hm[4]));
        LocalDate lDate = LocalDate.of(Integer.parseInt(hm[0]),Integer.parseInt(hm[1]),Integer.parseInt(hm[2]));
        LocalDateTime d = LocalDateTime.of(lDate,lTime);

        LocalDateTime timePoint = LocalDateTime.now();
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDate today = LocalDate.now();
        LocalDate lastDayOfWeek;
        LocalTime lastSec = LocalTime.of(23,59,59);
        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        LocalDateTime weekend;
        switch (dayOfWeek){
            case MONDAY:
                lastDayOfWeek = LocalDate.now().plusDays(6);
                break;
            case TUESDAY:
                lastDayOfWeek = LocalDate.now().plusDays(5);
                break;
            case WEDNESDAY:
                lastDayOfWeek = LocalDate.now().plusDays(4);
                break;
            case THURSDAY:
                lastDayOfWeek = LocalDate.now().plusDays(3);
                break;
            case FRIDAY:
                lastDayOfWeek = LocalDate.now().plusDays(2);
                break;
            case SATURDAY:
                lastDayOfWeek = LocalDate.now().plusDays(1);
                break;
            case SUNDAY:
                lastDayOfWeek = LocalDate.now();
                break;
            default:
                lastDayOfWeek = LocalDate.now();
                break;
        }
        weekend = LocalDateTime.of(lastDayOfWeek,lastSec);

        if (timePoint.isAfter(d))
            overdueItemArrayList.add(item);
        else if (timePoint.isBefore(d) && today.isEqual(lDate))
            todayItemArrayList.add(item);
        else if (tomorrow.isEqual(lDate))
            tomorrowItemArrayList.add(item);
        else if (tomorrow.isBefore(lDate) && weekend.isAfter(d))
            weekItemArrayList.add(item);
        else if (weekend.isBefore(d) && d.getMonth()==timePoint.getMonth() && d.getYear() == timePoint.getYear())
            monthItemArrayList.add(item);
        else
            otherItemArrayList.add(item);

    }
    public void DeleteIOverdueItems(Context context) {
        ToDoItemDAO toDoItemDAO = new ToDoItemDAO(context);
        Log.d("AAAA", "Starting DeleteIOverdueItems at Model...");
        toDoItemDAO.removeToDoItems(overdueItemArrayList);
        Log.d("AAAA", "Starting DeleteArrays at Model...");
        DeleteArrays();
    }

}
