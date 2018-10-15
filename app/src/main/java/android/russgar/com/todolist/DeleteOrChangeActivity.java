package android.russgar.com.todolist;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.speech.RecognizerIntent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;

public class DeleteOrChangeActivity extends AppCompatActivity {
    private DatePickerDialog.OnDateSetListener mDateSetListener, rDateSetListener, rdDateSetListener ;
    int finalYear,finalMonth,finalDay,finalhour,finalMin,rFinalYear, rFinalMonth, rFinalDay,rFinalHour,rFinalMin;
    private static final int RECOGNIZER_RESULT = 123;
    String everyDays;
    LinearLayout dateSelectedLayout;
    LinearLayout reminderSelectedLayout;
    LinearLayout repeatSelectedLayout;
    LinearLayout prioritySelectedLayout;
    ImageView cancelButton;
    ImageView addButton;
    ImageView deleteButton;
    TextView selectedDate;
    TextView selectedTime;
    TextView selectedReminderDate;
    TextView selectedReminderTime;
    TextView selectedRepeat;
    TextView selectedPriority;
    TextView eventName;
    TextView untilSelectedRepeat;
    int ID;
    Dialog rDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_or_change);
        dateSelectedLayout = (LinearLayout) findViewById(R.id.deleteOrChangeDateSelected);
        reminderSelectedLayout = (LinearLayout) findViewById(R.id.deleteOrChangeReminderSelected);
        repeatSelectedLayout = (LinearLayout) findViewById(R.id.deleteOrChangeRepeateSelected);
        prioritySelectedLayout = (LinearLayout) findViewById(R.id.deleteOrChangePrioritySelected);
        cancelButton =(ImageView) findViewById(R.id.deleteOrChangeCancelButton);
        addButton = (ImageView) findViewById(R.id.deleteOrChangeAddItemButton);
        deleteButton = (ImageView) findViewById(R.id.deleteButton);

        selectedDate = (TextView) findViewById(R.id.deleteOrChangeSelectedDate);
        selectedTime = (TextView) findViewById(R.id.deleteOrChangeSelectedTime);
        selectedReminderDate = (TextView) findViewById(R.id.deleteOrChangeSelectedReminderDate);
        selectedReminderTime = (TextView) findViewById(R.id.deleteOrChangeSelectedReminderTime);
        selectedRepeat = (TextView) findViewById(R.id.deleteOrChangeSelectedRepeat);
        untilSelectedRepeat = (TextView) findViewById(R.id.untilDeleteOrChangeSelectedRepeat);
        selectedPriority = (TextView) findViewById(R.id.deleteOrChangeSelectedPriority);
        eventName = (TextView) findViewById(R.id.deleteOrChangeEventName);

        Intent rIntent = getIntent();
        Bundle extras = rIntent.getExtras();
        String name = extras.getString("Name");
        //completed = extras.getBoolean("Completed");
        String priority = extras.getString("Priority");
        String date = extras.getString("Date");
        String time = extras.getString("Time");
        String rdate = extras.getString("rDate");
        String rtime = extras.getString("rTime");
        String rawrepeate = extras.getString("Repeat");
        ID = extras.getInt("ID");

        if (rawrepeate.contains(":")){
            String[] repeatArr = rawrepeate.split("\\:");
            selectedRepeat.setText(repeatArr[0]);
            untilSelectedRepeat.setText("Until: "+repeatArr[1]);
        }
        else
            selectedRepeat.setText(rawrepeate);
            
        //String repeate = rawrepeate.toString().contains(":");
        eventName.setText(name);
        selectedDate.setText(date);
        selectedTime.setText(time);
        selectedReminderDate.setText(rdate);
        selectedReminderTime.setText(rtime);
        selectedPriority.setText(priority);

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int mYear, int mMonth, int mDay) {
                mMonth += 1;
                TextView date = (TextView) findViewById(R.id.deleteOrChangeSelectedDate);
                String sMonth = (mMonth < 10)? "0"+mMonth : ""+mMonth;
                String sDay = (mDay < 10)? "0"+mDay : ""+mDay;
                date.setText(mYear+"-"+sMonth+"-"+sDay);
                dateSelectedLayout.setBackgroundColor(getResources().getColor(R.color.Selected));
                finalYear =mYear;
                finalMonth = mMonth;
                finalDay = mDay;

                Calendar mCalendar = Calendar.getInstance();
                int hour = mCalendar.get(Calendar.HOUR);
                int minute = mCalendar.get(Calendar.MINUTE);

                TimePickerDialog mdialog = new TimePickerDialog(DeleteOrChangeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int mHourOfDay, int mMin) {
                        TextView time = (TextView) findViewById(R.id.deleteOrChangeSelectedTime);
                        String smHourOfDay = (mHourOfDay < 10)? "0" + mHourOfDay : ""+mHourOfDay;
                        String smMin = (mMin < 10)? "0" + mMin : ""+mMin;
                        time.setText(smHourOfDay+":"+smMin);
                        finalhour = mHourOfDay;
                        finalMin = mMin;

                    }
                },hour,minute,true);
                mdialog.show();
            }
        };
        rDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int rYear, int rMonth, int rDay) {
                rMonth += 1;
                TextView rdate = (TextView) findViewById(R.id.deleteOrChangeSelectedReminderDate);
                String srMonth = (rMonth < 10)? "0"+rMonth : ""+ rMonth;
                String srDay = (rDay < 10)? "0"+rDay : ""+rDay;
                rdate.setText(rYear+"-"+srMonth+"-"+srDay);
                reminderSelectedLayout.setBackgroundColor(getResources().getColor(R.color.Selected));
                rFinalYear =rYear;
                rFinalMonth = rMonth;
                rFinalDay = rDay;

                Calendar cal = Calendar.getInstance();
                int hour = cal.get(Calendar.HOUR);
                int minute = cal.get(Calendar.MINUTE);

                TimePickerDialog rdialog = new TimePickerDialog(DeleteOrChangeActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int min) {
                        TextView rtime = (TextView) findViewById(R.id.deleteOrChangeSelectedReminderTime);
                        String srHourOfDay = (hourOfDay < 10)? "0" + hourOfDay : ""+hourOfDay;
                        String srMin = (min < 10)? "0" + min : ""+min;
                        rtime.setText(srHourOfDay+":"+srMin);
                        rFinalHour = hourOfDay;
                        rFinalMin = min;
                    }
                },hour,minute,true);
                rdialog.show();
            }
        };

        rdDateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int rYear, int rMonth, int rDay) {
                rMonth += 1;
                TextView rddate = (TextView) rDialog.findViewById(R.id.repeatDate);
                String srMonth = (rMonth < 10)? "0"+rMonth : ""+ rMonth;
                String srDay = (rDay < 10)? "0"+rDay : ""+rDay;
                rddate.setText(rYear+"-"+srMonth+"-"+srDay);
            }
        };

        ImageView startSpeech = (ImageView) findViewById(R.id.deleteOrChangeGetSpeechButton);
        startSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech to text");
                startActivityForResult(intent, RECOGNIZER_RESULT);
            }
        });
        final Intent intent = getIntent();
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("STATUS","DELETE");
                intent.putExtra("ID", ID);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("STATUS","CANCEL");
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!eventName.getText().toString().isEmpty()){
                    Intent intent = new Intent();
                    intent.putExtra("STATUS", "ADD");
                    intent.putExtra("NAME", eventName.getText().toString());
                    intent.putExtra("EDATE", selectedDate.getText().toString());
                    intent.putExtra("ETIME", selectedTime.getText().toString());
                    intent.putExtra("RDATE", selectedReminderDate.getText().toString());
                    intent.putExtra("RTIME", selectedReminderTime.getText().toString());
                    intent.putExtra("RPT", selectedRepeat.getText().toString());
                    intent.putExtra("PRT", selectedPriority.getText().toString());
                    intent.putExtra("ID", ID);
                    String unt = (untilSelectedRepeat.getText().toString().isEmpty())?"": untilSelectedRepeat.getText().toString().substring(7);
                    setResult(RESULT_OK, intent);
                    intent.putExtra("UNTIL",unt);
                    setResult(RESULT_OK, intent);
                    finish();
                }
                else {
                    Toast.makeText(getApplicationContext(), "Please add Event Name", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void repeadDateClicked(View view){
        Calendar dateCalendar = Calendar.getInstance();
        TextView untilText = (TextView) rDialog.findViewById(R.id.repeatDate);
        int year, month, day;
        if (!untilText.getText().toString().isEmpty()){
            String[] hm =  untilText.getText().toString().split("\\-");
            year = Integer.parseInt(hm[0]);
            month = Integer.parseInt(hm[1])-1;
            day = Integer.parseInt(hm[2]);

        }
        else {
            year = dateCalendar.get(Calendar.YEAR);
            month = dateCalendar.get(Calendar.MONTH);
            day = dateCalendar.get(Calendar.DAY_OF_MONTH);
        }
        DatePickerDialog dateDialog = new DatePickerDialog(this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                rdDateSetListener,
                year,month,day);
        dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dateDialog.show();

    }

    public void buttonClicked(View view){

        if (view.getId() == R.id.deleteOrChangeReminderButton){
            Calendar reminderCalendar = Calendar.getInstance();
            int year = reminderCalendar.get(Calendar.YEAR);
            int month = reminderCalendar.get(Calendar.MONTH);
            int day = reminderCalendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog rdialog = new DatePickerDialog(this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    rDateSetListener,
                    year,month,day);
            rdialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            rdialog.show();
        }
        else if (view.getId() == R.id.deleteOrChangeRepeateButton) {
            AlertDialog.Builder rBuilder = new AlertDialog.Builder(DeleteOrChangeActivity.this);
            View rView = getLayoutInflater().inflate(R.layout.repeat_layout, null);
            final RadioGroup rGroup = (RadioGroup) rView.findViewById(R.id.repeatRedioGroup);
            final EditText days = (EditText) rView.findViewById(R.id.days);
            final TextView selectedRepeat = (TextView) findViewById(R.id.deleteOrChangeSelectedRepeat);
            final RadioButton doNotRepeat = (RadioButton) rView.findViewById(R.id.noRepeat);
            final RadioButton onceADay = (RadioButton) rView.findViewById(R.id.dailyRepeat);
            final RadioButton onceAWeek = (RadioButton) rView.findViewById(R.id.weeklyRepeat);
            final RadioButton onceAMonth = (RadioButton) rView.findViewById(R.id.monthlyRepeat);
            final RadioButton otherRepeat = (RadioButton) rView.findViewById(R.id.otherRepeat);
            Button yesButton = (Button) rView.findViewById(R.id.addRepeat);
            Button noButton = (Button) rView.findViewById(R.id.rCancel);
            final TextView untilText = (TextView) rView.findViewById(R.id.repeatDate);
            rBuilder.setView(rView);
            rDialog = rBuilder.create();
            rDialog.show();
            if (!selectedRepeat.getText().toString().isEmpty()){
                switch (selectedRepeat.getText().toString()){
                    case "Do not repeat":
                        rGroup.check(rGroup.getChildAt(0).getId());
                        break;
                    case "Once a day":
                        rGroup.check(rGroup.getChildAt(1).getId());
                        untilText.setText(untilSelectedRepeat.getText().toString().substring(7));
                        break;
                    case "Once a week":
                        rGroup.check(rGroup.getChildAt(2).getId());
                        untilText.setText(untilSelectedRepeat.getText().toString().substring(7));
                        break;
                    case "Every weekday":
                        rGroup.check(rGroup.getChildAt(3).getId());
                        untilText.setText(untilSelectedRepeat.getText().toString().substring(7));
                        break;
                    case "Every weekend":
                        rGroup.check(rGroup.getChildAt(4).getId());
                        untilText.setText(untilSelectedRepeat.getText().toString().substring(7));
                        break;
                    case "Once a month":
                        rGroup.check(rGroup.getChildAt(5).getId());
                        untilText.setText(untilSelectedRepeat.getText().toString().substring(7));
                        break;
                    default:
                        everyDays = selectedRepeat.getText().toString().split(" ")[1];
                        rGroup.check(rGroup.getChildAt(6).getId());
                        days.setText(everyDays);
                        days.setEnabled(true);
                        untilText.setText(untilSelectedRepeat.getText().toString().substring(7));
                        break;
                }
            }
            else{
                rGroup.check(rGroup.getChildAt(0).getId());
            }

            rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                    if (checkedId == R.id.otherRepeat)
                        days.setEnabled(true);
                    else
                        days.setEnabled(false);
                    if (checkedId == R.id.noRepeat)
                    untilText.setText("");
                }
            });

            yesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RadioButton checkedRadioButton = (RadioButton)rGroup.findViewById(rGroup.getCheckedRadioButtonId());
                    if (checkedRadioButton.getText().equals("Other") && days.getText().toString().isEmpty()){
                        Toast.makeText(getApplicationContext(),"Please enter number of days!",Toast.LENGTH_LONG).show();
                    }
                    else if (!checkedRadioButton.getText().equals("Do not repeat") && untilText.getText().toString().equals("")){
                        Toast.makeText(getApplicationContext(),"Please select Until date!",Toast.LENGTH_LONG).show();
                    }
                    else if (checkedRadioButton.getText().equals("Do not repeat")){
                        selectedRepeat.setText(checkedRadioButton.getText().toString());
                        untilSelectedRepeat.setText("");
                        repeatSelectedLayout.setBackgroundColor(getResources().getColor(R.color.Selected));
                        rDialog.dismiss();
                    }
                    else{
                        if (checkedRadioButton.getText().equals("Other")){
                            selectedRepeat.setText("Every "+ days.getText().toString()+" days");
                            everyDays = days.getText().toString();
                        }
                        else{
                            selectedRepeat.setText(checkedRadioButton.getText().toString());
                        }
                        untilSelectedRepeat.setText("Until: "+untilText.getText().toString());
                        repeatSelectedLayout.setBackgroundColor(getResources().getColor(R.color.Selected));
                        rDialog.dismiss();
                    }
                }
            });
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rDialog.dismiss();
                }
            });


        }
        else if (view.getId() == R.id.deleteOrChangeDateButton) {

            Calendar dateCalendar = Calendar.getInstance();
            int year = dateCalendar.get(Calendar.YEAR);
            int month = dateCalendar.get(Calendar.MONTH);
            int day = dateCalendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dateDialog = new DatePickerDialog(this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    mDateSetListener,
                    year,month,day);
            dateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dateDialog.show();
        }
        else
        {

            AlertDialog.Builder lBuilder = new AlertDialog.Builder(DeleteOrChangeActivity.this);
            View lView = getLayoutInflater().inflate(R.layout.priority_layout, null);
            final SeekBar ePriority = (SeekBar) lView.findViewById(R.id.priorityBar);
            Button yesButton = (Button) lView.findViewById(R.id.addPriority);
            Button noButton = (Button) lView.findViewById(R.id.cancel);
            lBuilder.setView(lView);
            final AlertDialog ldialog = lBuilder.create();
            ldialog.show();
            final TextView selectedPriority = (TextView) findViewById(R.id.deleteOrChangeSelectedPriority);
            if (!selectedPriority.getText().toString().isEmpty()){
                switch (selectedPriority.getText().toString()){
                    case "Low":
                        ePriority.setProgress(0);
                        break;
                    case "Ordinary":
                        ePriority.setProgress(1);
                        break;
                    case "Hi":
                        ePriority.setProgress(2);
                        break;
                    default:
                        break;

                }
            }

            yesButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    // TextView selectedPriority = (TextView) findViewById(R.id.selectedPriority);
                    prioritySelectedLayout.setBackgroundColor(getResources().getColor(R.color.Selected));
                    switch (ePriority.getProgress()){
                        case 0:
                            selectedPriority.setText("Low");
                            break;
                        case 1:
                            selectedPriority.setText("Ordinary");
                            break;
                        case 2:
                            selectedPriority.setText("Hi");
                        default:
                            break;

                    }
                    ldialog.dismiss();
                }
            });
            noButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ldialog.dismiss();
                }
            });
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RECOGNIZER_RESULT && resultCode == RESULT_OK) {
            List<String> matches = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            TextView speechText = (TextView)findViewById(R.id.deleteOrChangeEventName);
            String string = matches.get(0).toString();
            speechText.setText(Character.toString(string.charAt(0)).toUpperCase()+ string.substring(1));
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
