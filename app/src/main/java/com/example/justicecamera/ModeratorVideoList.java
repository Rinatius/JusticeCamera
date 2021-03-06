package com.example.justicecamera;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.BackendlessDataQuery;

import java.util.ArrayList;
import java.util.List;

public class ModeratorVideoList extends AppCompatActivity {
    static ProgressDialog pd;
    List<Violation> listViolation;
    ListView list;
    TextView textViewTester;
    String objectId = "";
    String searchParameter ="status = 0";
    static final String OBJECTID = "checking";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_moderator_video_list);
        textViewTester = (TextView) findViewById(R.id.textViewTesterM);
        listViolation = new ArrayList<>();
        list = (ListView) findViewById(R.id.listView);
    }

    class MyAdapter extends BaseAdapter {

        private Context context;
        private List<Violation> data;

        MyAdapter(Context context, List<Violation> data) {
            this.data = data;
            this.context = context;
        }

        @Override
        public int getCount() {   // кол во элементов
            return data.size();
        }

        @Override
        public String getItem(int position) {   // возвращает на какой элемент из массива мы нажали
            return String.valueOf(data.get(position));
        }

        @Override
        public long getItemId(int position) {  // возврат индекса, идентификатора
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {   // заполняет листвью

            View con = convertView;
            if (con == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                con = inflater.inflate(R.layout.myitem, parent, false);
            }

            TextView videoName = (TextView) con.findViewById(R.id.videoName);
            TextView addInfo = (TextView) con.findViewById(R.id.addInfo);
            videoName.setText(data.get(position).getName());
            addInfo.setText(data.get(position).getCarMake());

            con.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    objectId = data.get(position).getObjectId();
                    Intent i = new Intent(ModeratorVideoList.this, VideoInfo.class);
                    i.putExtra(OBJECTID, objectId);
                    startActivity(i);
                }
            });
            return con;
        }
    }

    @Override
    protected void onStart(){
        super.onStart();
        new ListOfViolationTask().execute(searchParameter);

    }

    private class ListOfViolationTask extends AsyncTask<String, Void, BackendlessCollection<Violation>> {


        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(ModeratorVideoList.this);
            pd.setTitle(getString(R.string.videolist_downloading));
            pd.setMessage(getString(R.string.wait));
            pd.show();
        }

        @Override
        protected BackendlessCollection<Violation> doInBackground(String... strings) {
            return Helper.getAllViolations(strings[0]);
        }

        protected void onPostExecute(BackendlessCollection<Violation> result) {
            showViolationList(result);
            pd.dismiss();
        }
    }

    private void showViolationList(BackendlessCollection<Violation> listOfViolatioons){
        listViolation = listOfViolatioons.getData();
        String textToShow = getString(R.string.number_of_elements) + Integer.toString(listViolation.size());
        textViewTester.setText(textToShow);
        registerForContextMenu(list);
        MyAdapter adapter = new MyAdapter(ModeratorVideoList.this, listViolation);
        list.setAdapter(adapter);
        pd.dismiss();
    }

}
