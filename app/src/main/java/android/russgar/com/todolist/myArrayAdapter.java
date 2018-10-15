package android.russgar.com.todolist;

import android.content.Context;
import android.os.AsyncTask;
import android.russgar.com.todolist.ToDoItem;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

class myArrayAdapter extends ArrayAdapter <ToDoItem>{
    private LayoutInflater mInflater;
    private List <ToDoItem> toDoItems;
    private int mViewResourceId;
    ToDoItemDAO toDoItemDAO;
    MainActivity mainActivity = new MainActivity();

    public myArrayAdapter(@NonNull Context context, int resource, @NonNull List<ToDoItem> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        toDoItems = objects;
        mViewResourceId = resource;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;

        v= mInflater.inflate(mViewResourceId, null);
        TextView txtName = (TextView) v.findViewById(R.id.itemName);
        TextView txtDate = (TextView) v.findViewById(R.id.itemDate);
        TextView txtTime = (TextView) v.findViewById(R.id.itemTime);
        TextView txtrDate = (TextView) v.findViewById(R.id.itemrDate);
        TextView txtrTime = (TextView) v.findViewById(R.id.itemrTime);
        final CheckBox checkbox = (CheckBox) v.findViewById(R.id.doneOrNot);
        ImageView priority = (ImageView) v.findViewById(R.id.itemPriority);
        final View itemLayout = v.findViewById(R.id.rowMainLayout);

        // update the UI components
        txtName.setText(toDoItems.get(position).getName());
        txtDate.setText(toDoItems.get(position).getDate());
        txtTime.setText(toDoItems.get(position).getTime());
        txtrDate.setText(toDoItems.get(position).getrDate());
        txtrTime.setText(toDoItems.get(position).getrTime());
        checkbox.setChecked(toDoItems.get(position).getCompleted());
        float alpha = (checkbox.isChecked())? (float)0.3 : 1;
        itemLayout.setAlpha(alpha);
        switch (toDoItems.get(position).priority) {
            case "Hi":
                priority.setImageResource(R.drawable.high_priority);
                break;
            case "Ordinary":
                break;
            case "Low":
                priority.setImageResource(R.drawable.low_priority);
                break;
            default:
                break;
        }

        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final boolean isChecked = checkbox.isChecked();
                ToDoItem changedItem = toDoItems.get(position);
                changedItem.setCompleted(isChecked);
                float alpha = (isChecked)? (float)0.5 : 1;
                itemLayout.setAlpha(alpha);
                new CheckBoxUpdater().execute(changedItem);
            }
        });
        return (v);
    }
    public class CheckBoxUpdater extends AsyncTask<ToDoItem, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(ToDoItem... toDoItems) {
            toDoItemDAO = new ToDoItemDAO(getContext());
            try {
                toDoItemDAO.updateCheckbox(toDoItems[0]);
                return true;
            }
            catch (android.database.SQLException e) {
                Log.d("TAG", "Error Creating Table...");
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            if (aBoolean){
                Toast.makeText(getContext(), "Saved!", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(aBoolean);
        }
    }
}