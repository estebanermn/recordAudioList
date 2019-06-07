package pe.edu.cibertec.recordaudiolist;

import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import pe.edu.cibertec.recordaudiolist.adapter.RecordingAdapter;
import pe.edu.cibertec.recordaudiolist.model.Recording;

public class RecordingListActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerViewRecordings;
//    private ArrayList<Recording> recordingArraylist;
    private List<Recording> recordingArraylist = new ArrayList<>();
    public RecordingAdapter recordingAdapter;
    private TextView textViewNoRecordings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recording_list);


        initView();
        fetchRecordings();
    }

    private void initView() {
//        recordingArraylist = new ArrayList<Recording>();
        List<Recording> recordingArraylist = new ArrayList<>();
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Recording List");
        toolbar.setTitleTextColor(ContextCompat.getColor(this, android.R.color.black));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerViewRecordings = findViewById(R.id.recyclerViewRecordings);
        recyclerViewRecordings.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewRecordings.setHasFixedSize(true);

//
//        RecordingAdapter adapter = new RecordingAdapter(recordingArraylist);
//        adapter.setAdapter(recordingArraylist);

        textViewNoRecordings = (TextView) findViewById(R.id.textViewRecordingname);

    }

    private void fetchRecordings() {

        File root = android.os.Environment.getExternalStorageDirectory();
        String path = root.getAbsolutePath() + "/VoiceRecorderSimplifiedCoding/Audios";
        Log.d("Files", "Path: " + path);
        File directory = new File(path);
        File[] files = directory.listFiles();
        Log.d("Files", "Size: " + files.length);
        if (files != null) {

            for (int i = 0; i < files.length; i++) {

                Log.d("Files", "FileName:" + files[i].getName());
                String fileName = files[i].getName();
                String recordingUri = root.getAbsolutePath() + "/VoiceRecorderSimplifiedCoding/Audios/" + fileName;

                Recording recording = new Recording(recordingUri, fileName, false);
                recordingArraylist.add(recording);
            }

//            textViewNoRecordings.setVisibility(View.GONE);
            recyclerViewRecordings.setVisibility(View.VISIBLE);
            setAdaptertoRecyclerView();

        } else {
//            textViewNoRecordings.setVisibility(View.VISIBLE);
            recyclerViewRecordings.setVisibility(View.GONE);
        }

    }


    private void setAdaptertoRecyclerView() {
        recordingAdapter = new RecordingAdapter(this, (ArrayList<Recording>) recordingArraylist);
        recyclerViewRecordings.setAdapter(recordingAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case 1:
                recordingAdapter.remove(item.getGroupId());
                displayMessage("Audio Delete");
                System.out.println();
                return true;
            default:
        }
        return super.onContextItemSelected(item);
    }

    private void displayMessage(String message) {
        Snackbar.make(findViewById(R.id.recyclerViewRecordings), message, Snackbar.LENGTH_SHORT).show();

    }

}
