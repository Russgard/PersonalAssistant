package android.russgar.com.todolist;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.provider.Telephony;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.lang.reflect.GenericArrayType;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    myArrayAdapter overdueAdapter;
    myArrayAdapter todayAdapter;
    myArrayAdapter tomorrowAdapter;
    myArrayAdapter weekAdapter;
    myArrayAdapter monthAdapter;
    myArrayAdapter otherAdapter;
    Model myModel;
    ToDoItemDAO toDoItemDAO;
    ListView overdueListView;
    private TextToSpeech myTTS;
    private TextToSpeech remTTS;
    TextToSpeech TTS2;
    private Timer myTimer;
    Ringtone r;
    private static final int RECOGNIZER_RESULT_10 = 10;
    public boolean StartAction;
    String word;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context ctx = getApplicationContext();
        myModel = new Model();
        overdueListView = (ListView) findViewById(R.id.overdueListView);
        //ListView to showall events by section
        //new StartDB().execute(1);
        toDoItemDAO = new ToDoItemDAO(getApplicationContext());
        // Updating ArrayLists of sections
        //new UpdateToDoArrayList().execute(1);

        //Custom adapters for each section
        overdueAdapter = new myArrayAdapter(ctx, R.layout.row, myModel.getTodoData("overdue"));
        todayAdapter = new myArrayAdapter(ctx, R.layout.row, myModel.getTodoData("today"));
        tomorrowAdapter = new myArrayAdapter(ctx, R.layout.row, myModel.getTodoData("tomorrow"));
        weekAdapter = new myArrayAdapter(ctx, R.layout.row, myModel.getTodoData("week"));
        monthAdapter = new myArrayAdapter(ctx, R.layout.row, myModel.getTodoData("month"));
        otherAdapter = new myArrayAdapter(ctx, R.layout.row, myModel.getTodoData("other"));

        //Add section custom adapters to the main section managing adaptor
        adapter.addSection("Overdue", overdueAdapter);
        adapter.addSection("Today", todayAdapter);
        adapter.addSection("Tomorrow", tomorrowAdapter);
        adapter.addSection("This Week", weekAdapter);
        adapter.addSection("This month", monthAdapter);
        adapter.addSection("Others", otherAdapter);

        //Set main adaptor
        overdueListView.setAdapter(adapter);

        //Set OnClick for rows
        overdueListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ToDoItem item = (ToDoItem) adapter.getItem(i);
                Intent intent = new Intent(getApplicationContext(), DeleteOrChangeActivity.class);
                intent.putExtra("Name", item.getName());
                intent.putExtra("Priority", item.getPriority());
                intent.putExtra("Date", item.getDate());
                intent.putExtra("Time", item.getTime());
                intent.putExtra("rDate", item.getrDate());
                intent.putExtra("rTime", item.getrTime());
                intent.putExtra("Repeat", item.getRepeat());
                intent.putExtra("ID", item.getItemID());
                intent.putExtra("Informed", item.getInformed());
                startActivityForResult(intent, 2);
            }
        });

        //Starting time listener
        new StartTimeListener().execute(1);
        Intent reminderIntent = new Intent();
        reminderIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(reminderIntent, 4);

        Intent textToSpeechIntent = new Intent();
        textToSpeechIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(textToSpeechIntent, 5);

        toDoItemDAO.myModel = myModel;

        //Start updating the event list for the first time...
        try {
            ArrayList <ToDoItem> tdArray = toDoItemDAO.getToDoItems();
            ArrayList <Integer> itemIDs = new ArrayList();
            if (tdArray.size() != 0){
                for (int i = 0; i < tdArray.size(); i++){
                    myModel.updateTodoData(tdArray.get(i));
                    itemIDs.add(tdArray.get(i).getItemID());
                }
                int max = itemIDs.get(0);
                for (int j = 1 ; j < itemIDs.size() ; j++ ){
                    max = (max < itemIDs.get(j)) ? itemIDs.get(j) : max;
                }
                myModel.numberOfItems = max;
                overdueAdapter.notifyDataSetChanged();
                todayAdapter.notifyDataSetChanged();
                tomorrowAdapter.notifyDataSetChanged();
                weekAdapter.notifyDataSetChanged();
                monthAdapter.notifyDataSetChanged();
                otherAdapter.notifyDataSetChanged();
                adapter.notifyDataSetChanged();

            }
            else{
                Log.i("AAAA","Array size is "+ tdArray.size());
            }
        }
        catch (android.database.SQLException e) {
            Log.d("AAAA", "Error getting todo items ...");
            e.printStackTrace();
        }


    }



    @Override
    protected void onDestroy() {
        toDoItemDAO.CloseDB();
        super.onDestroy();
    }

    // Menu staff
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.addbutton, menu);
        return true;
    }
    //Menu: onClick (select)
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.mnNew) {
//            Intent intent = new Intent(getApplicationContext(), EditActivity.class);
//            startActivityForResult(intent,  0);
//            return true;
//        }
//        else if (item.getItemId() == R.id.mnPlay){
//            ArrayList<String> textsToSpeak = myModel.TextToVoice();
//            TTS2.setSpeechRate((float) 0.8);
//            TTS2.setPitch((float) 1);
//            for (int i=0; i< textsToSpeak.size(); i++) {
//                final Handler handler = new Handler();
//                final String word = textsToSpeak.get(i);
//
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                    TTS2.playSilentUtterance(1000, TextToSpeech.QUEUE_ADD, null);
//                    TTS2.speak(word, TextToSpeech.QUEUE_ADD, null, null);
//                } else {
//                    TTS2.playSilence(1000, TextToSpeech.QUEUE_ADD, null);
//                    TTS2.speak(word, TextToSpeech.QUEUE_ADD, null);
//                }
//            }
//
//        }
//        else{}
//        return super.onOptionsItemSelected(item);
//    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.mnNew) {
            Intent intent = new Intent(getApplicationContext(), EditActivity.class);
            startActivityForResult(intent,  0);
            return true;
        }
        else if (item.getItemId() == R.id.mnPlay){
            StartAction = true;
            Conversation(null);
        }


        else{}
        return super.onOptionsItemSelected(item);
    }

    private void Conversation( ArrayList<String> text) {
        ArrayList<String> textsToSpeak;
        String actionStep = null;
       if (StartAction)
       {
           textsToSpeak = myModel.SayHello();
           StartAction = false;
           actionStep ="SayHello";
       }
       else {
           textsToSpeak = text;
           actionStep ="ResponceOne";
       }
        TTS2.setSpeechRate((float) 0.9);
        TTS2.setPitch((float) 1);
        word = "";
        for (int i=0; i< textsToSpeak.size(); i++) {
            word += textsToSpeak.get(i);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TTS2.playSilentUtterance(1000, TextToSpeech.QUEUE_ADD, null);
            TTS2.speak(word, TextToSpeech.QUEUE_ADD, null, actionStep);
        }
        else {
            TTS2.playSilence(1000, TextToSpeech.QUEUE_ADD, null);
            TTS2.speak(word, TextToSpeech.QUEUE_ADD, null);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            // API > 15
            TTS2.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onStart(String utteranceId) {
                    // do something
                }

                @Override
                public void onDone(String utteranceId) {
                    Log.d("AAAA","262---------"+word);
                   if (utteranceId.equalsIgnoreCase("SayHello") || utteranceId.equalsIgnoreCase("ResponceOne" )){
                       if(word.contains("bye")){}
                       else
                        StartSpeechToText();
                   }
                }

                @Override
                public void onError(String utteranceId) {
                    // do something
                }
            });
        }
        else {
            // API < 15
            TTS2.setOnUtteranceCompletedListener(new TextToSpeech.OnUtteranceCompletedListener() {
                @Override
                public void onUtteranceCompleted(String utteranceId) {
                    StartSpeechToText();
                }
            });
        }


    }


    public void StartSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to text");
        startActivityForResult(intent, RECOGNIZER_RESULT_10);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0){
            if (resultCode == RESULT_OK){
                if (data.getStringExtra("STATUS").equals("ADD")){
                    String rpt = (data.getStringExtra("UNTIL").isEmpty())?data.getStringExtra("RPT"):data.getStringExtra("RPT")+":"+data.getStringExtra("UNTIL");
                    ToDoItem toDoItem = myModel.setTodoData(data.getStringExtra("NAME"), false, data.getStringExtra("PRT"), data.getStringExtra("EDATE"), data.getStringExtra("ETIME"), data.getStringExtra("RDATE"), data.getStringExtra("RTIME"), rpt, false);
                    new saveToDatabase().execute(toDoItem);
                   // new UpdateToDoArrayList().execute(2);
                }
            }
        }
        if (requestCode == 2){
            if (resultCode == RESULT_OK){
                if (data.getStringExtra("STATUS").equals("ADD")){
                    String rpt = (data.getStringExtra("UNTIL").isEmpty())?data.getStringExtra("RPT"):data.getStringExtra("RPT")+":"+data.getStringExtra("UNTIL");
                    ToDoItem toDoItem = new ToDoItem(data.getStringExtra("NAME"),false, data.getStringExtra("PRT"), data.getStringExtra("EDATE"), data.getStringExtra("ETIME"), data.getStringExtra("RDATE"), data.getStringExtra("RTIME"), rpt, data.getIntExtra("ID", 0), false);
                    new UpdateDatabase().execute(toDoItem);
                }
                if (data.getStringExtra("STATUS").equals("DELETE")){
                    new DeleteItem().execute(data.getIntExtra("ID", 0));
                }
            }
        }
        if (requestCode ==4){
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                remTTS = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        remTTS.setLanguage(Locale.US);
                    }
                });


            } else {
                // TTS data not yet loaded, try to install it
                Intent rttsLoadIntent = new Intent();
                rttsLoadIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(rttsLoadIntent);
            }


        }
        if (requestCode ==5){
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                TTS2 = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int i) {
                        TTS2.setLanguage(Locale.US);
                    }
                });


            } else {
                // TTS data not yet loaded, try to install it
                Intent tts2LoadIntent = new Intent();
                tts2LoadIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(tts2LoadIntent);
            }


        }
        if (requestCode == RECOGNIZER_RESULT_10 && resultCode == RESULT_OK){
            List<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            final String string = matches.get(0).toString();
            if (!string.isEmpty() && string != null){
                if (myModel.ResponceOne(string).contains("I have deleted all your overdue records")){
                    new DeleteOverdueItems().execute(1);
                }
                else{
                    Log.d("AAAA", myModel.ResponceOne(string));
                }
                ArrayList<String> newstring = new ArrayList<String>();
                newstring.add(myModel.ResponceOne(string));
                Conversation(newstring);
            }
        }

    }
    public void ResponceOne(String e){
        if (!e.equalsIgnoreCase("") && e != null){
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    TTS2.playSilentUtterance(1000, TextToSpeech.QUEUE_ADD, "Pause");
                    TTS2.speak(e, TextToSpeech.QUEUE_ADD, null, "ResponceOne");
                } else {
                    TTS2.playSilence(1000, TextToSpeech.QUEUE_ADD, null);
                    TTS2.speak(e, TextToSpeech.QUEUE_ADD, null);
                }

        }

    }


    //Time listener runs it on the same thread
    // Refresh all arrays
    private void TimerMethod()
    {

        myModel.DeleteArrays();
        try {
            ArrayList <ToDoItem> tdArray = toDoItemDAO.getToDoItems();
            ArrayList <Integer> itemIDs = new ArrayList();
            if (tdArray.size() != 0){
                for (int i = 0; i < tdArray.size(); i++){
                    myModel.updateTodoData(tdArray.get(i));
                    itemIDs.add(tdArray.get(i).getItemID());
                }
                int max = itemIDs.get(0);
                for (int j = 1 ; j < itemIDs.size() ; j++ ){
                    max = (max < itemIDs.get(j)) ? itemIDs.get(j) : max;
                }
                myModel.numberOfItems = max;
            }
        }
        catch (android.database.SQLException e) {
            Log.d("AAAA", "Error Creating Table...");
            e.printStackTrace();
        }

        this.runOnUiThread(Timer_Tick);
    }

    //This one runs on UI's thread
    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            //Get Array of ToDoItems that need a notificatiom
            final ArrayList toDoItems = myModel.StartAlarm();

            //updated arrays
            overdueAdapter.notifyDataSetChanged();
            todayAdapter.notifyDataSetChanged();
            tomorrowAdapter.notifyDataSetChanged();
            weekAdapter.notifyDataSetChanged();
            monthAdapter.notifyDataSetChanged();
            otherAdapter.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
            if (!toDoItems.isEmpty()){
                ArrayList <ToDoItem> items = new ArrayList();
                for (int i=0; i < toDoItems.size(); i++) {
                    ToDoItem item = (ToDoItem) toDoItems.get(i);
                    if (!item.getInformed())
                        items.add(item);
                }
                if (items.size()!=0){
                    String activity = (items.size()>1)?"activities":"activity";
                    String text = "I would like to remind that you planned the following "+ activity + " :";
                    String utteranceId = UUID.randomUUID().toString();
                    remTTS.speak(text,TextToSpeech.QUEUE_FLUSH,null, utteranceId);


                    for (int j=0; j<items.size();j++) {
                        ToDoItem item = items.get(j);
                        remTTS.speak(" " + item.getName() + " on " + item.getDate() + " at " + item.getTime(), TextToSpeech.QUEUE_ADD, null, utteranceId);
                        new SetInformed().execute(item);
                    }
                    try {
                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                        final Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                        r.play();
                        final Handler h = new Handler();
                        h.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                r.stop();
                            }}, 20000);
                    } catch (Exception e) {
                        e.printStackTrace();

                    }

                }
            }
        }
    };


    public class SetInformed extends AsyncTask<ToDoItem, Integer, Boolean>{


        @Override
        protected Boolean doInBackground(ToDoItem... toDoItems) {
            try {
                toDoItemDAO.SetInformed(toDoItems[0]);
                Log.i("AAAA","set informed");
                return true;
            }
            catch (android.database.SQLException e) {
                Log.d("AAAA", "Error setting informed ...");
                e.printStackTrace();
                return false;
            }

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
    //Time listener, every min
    public class StartTimeListener extends AsyncTask<Integer, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(Integer... integers) {
            LocalDateTime time = LocalDateTime.now().withSecond(0).plusMinutes(1);
            myTimer = new Timer();
            myTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    TimerMethod();
                }
            }, myModel.DateToStart(), 60000);
        return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                Toast.makeText(getApplicationContext(), "Background monitoring started!", Toast.LENGTH_SHORT).show();
            }
            else
                Toast.makeText(getApplicationContext(), "Couldn't create database (((", Toast.LENGTH_SHORT).show();
            super.onPostExecute(aBoolean);
        }
    }
    public class DeleteItem extends AsyncTask<Integer, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(Integer... integers) {
            try {
                toDoItemDAO.removeToDoItem(integers[0]);
                myModel.DeleteArrays();
                return true;
            }
            catch (android.database.SQLException e) {
                Log.d("AAAA", "Error removing items...");
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                try {
                    ArrayList <ToDoItem> tdArray = toDoItemDAO.getToDoItems();
                    ArrayList <Integer> itemIDs = new ArrayList();
                    if (tdArray.size() != 0){
                        for (int i = 0; i < tdArray.size(); i++){
                            myModel.updateTodoData(tdArray.get(i));
                            itemIDs.add(tdArray.get(i).getItemID());
                        }
                        int max = itemIDs.get(0);
                        for (int j = 1 ; j < itemIDs.size() ; j++ ){
                            max = (max < itemIDs.get(j)) ? itemIDs.get(j) : max;
                        }
                        myModel.numberOfItems = max;
                    }
                }
                catch (android.database.SQLException e) {
                    Log.d("AAAA", "Error getting items...");
                    e.printStackTrace();
                }
            }
            else
                Toast.makeText(getApplicationContext(), "Couldn't delete todo items (((", Toast.LENGTH_LONG).show();

            overdueAdapter.notifyDataSetChanged();
            todayAdapter.notifyDataSetChanged();
            tomorrowAdapter.notifyDataSetChanged();
            weekAdapter.notifyDataSetChanged();
            monthAdapter.notifyDataSetChanged();
            otherAdapter.notifyDataSetChanged();
            adapter.notifyDataSetChanged();
            super.onPostExecute(aBoolean);
        }
    }
    public class StartDB extends AsyncTask<Integer, Integer, Boolean>{
        @Override
        protected Boolean doInBackground(Integer... integers) {
            toDoItemDAO = new ToDoItemDAO(getApplicationContext());
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean)
            super.onPostExecute(aBoolean);
        }

    }


    public class UpdateToDoArrayList extends AsyncTask<Integer, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(Integer... integers) {
            try {
                ArrayList <ToDoItem> tdArray = toDoItemDAO.getToDoItems();
                ArrayList <Integer> itemIDs = new ArrayList();
                if (tdArray.size() != 0){
                    for (int i = 0; i < tdArray.size(); i++){
                        myModel.updateTodoData(tdArray.get(i));
                        itemIDs.add(tdArray.get(i).getItemID());
                    }
                    int max = itemIDs.get(0);
                    for (int j = 1 ; j < itemIDs.size() ; j++ ){
                        max = (max < itemIDs.get(j)) ? itemIDs.get(j) : max;
                    }
                    myModel.numberOfItems = max;
                }
                else{
                    Log.i("AAAA","Array size is "+ tdArray.size());
                }

                return true;
            }
            catch (android.database.SQLException e) {
                Log.d("AAAA", "Error getting todo items ...");
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                    overdueAdapter.notifyDataSetChanged();
                    todayAdapter.notifyDataSetChanged();
                    tomorrowAdapter.notifyDataSetChanged();
                    weekAdapter.notifyDataSetChanged();
                    monthAdapter.notifyDataSetChanged();
                    otherAdapter.notifyDataSetChanged();
                    adapter.notifyDataSetChanged();
 //               }

            }
            else
                Toast.makeText(getApplicationContext(), "Couldn't create database (((", Toast.LENGTH_LONG).show();
                super.onPostExecute(aBoolean);
        }
    }
    public class saveToDatabase extends AsyncTask<ToDoItem, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(ToDoItem... toDoItems) {
            try {
                toDoItemDAO.addToDoItem(toDoItems[0]);
                return true;
            }
            catch (android.database.SQLException e) {
                Log.d("AAAA", "Error Creating Table...");
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                myModel.DeleteArrays();
                new UpdateToDoArrayList().execute(2);
            }
            super.onPostExecute(aBoolean);
        }
    }

    public class UpdateDatabase extends AsyncTask<ToDoItem, Integer, Boolean>{

        @Override
        protected Boolean doInBackground(ToDoItem... toDoItems) {
            try {
                toDoItemDAO.updateToDoItem(toDoItems[0]);
                return true;
            }
            catch (android.database.SQLException e) {
                Log.i("AAAA", "Error Creating Table...");
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                myModel.DeleteArrays();
                new UpdateToDoArrayList().execute(1);
            }
            else
                Toast.makeText(getApplicationContext(), "Couldn't create database (((", Toast.LENGTH_LONG).show();
            super.onPostExecute(aBoolean);
        }
    }
    SectionAdapter adapter = new SectionAdapter() {
        @Override
        protected View getHeaderView(String caption, int index,
                                     View convertView, ViewGroup parent) {
            TextView result = (TextView) convertView;

            if (convertView == null) {
                result = (TextView) getLayoutInflater().inflate(
                        R.layout.section_header, null);
            }

            result.setText(caption);
            return (result);
        }
    };

    public class DeleteOverdueItems extends AsyncTask<Integer,Integer,Boolean>{
        @Override
        protected Boolean doInBackground(Integer... integers) {
            myModel.DeleteIOverdueItems(getApplicationContext());
            return true;
        }
        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean) {
                new UpdateToDoArrayList().execute(1);
            }
            else
                Toast.makeText(getApplicationContext(), "Couldn't create database (((", Toast.LENGTH_LONG).show();
            super.onPostExecute(aBoolean);
        }
    }

}
