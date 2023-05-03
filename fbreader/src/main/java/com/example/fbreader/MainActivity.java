package com.example.fbreader;

import android.Manifest;
import android.app.usage.StorageStatsManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.provider.Settings;
import android.util.ArraySet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbreader.Bookfileadapter.BookFile;
import com.example.fbreader.Bookfileadapter.BookFileAdapter;
import com.example.fbreader.databinding.ActivityMainBinding;

import org.fbreader.book.Book;

import java.io.File;
import java.security.Permission;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


@RequiresApi(api = Build.VERSION_CODES.M)
public class MainActivity extends AppCompatActivity {
    public static final String FILE_EXTRA = "book_file_extra";
    ActivityMainBinding binding;
    Set<String> paths = new ArraySet<>();

    private RecyclerView recyclerView;
    ArrayList<BookFile> arrayList = new ArrayList<>();
    BookFileAdapter adapter;

    TextView text_center;
    ProgressBar bar;

    boolean b = false;
    ActivityResultLauncher<Intent> r;
    ActivityResultLauncher<Intent> r1;
    boolean is_permission_window_openned = false;
    private static final int REQUEST_PERMISSION = 1;
    private final int CHOOSE_FILE_REQUESTCODE = 15;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bar = binding.bar;
        bar.setVisibility(View.VISIBLE);
        text_center = binding.textNoFiles;
        text_center.setVisibility(View.INVISIBLE);
        recyclerView = binding.rvFiles;
        getSupportActionBar().setTitle("");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.add);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = binding.rvFiles;
        adapter = new BookFileAdapter(arrayList, new BookFileAdapter.listener() {
            @Override
            public void onbookclick(int position) {
                BookFile f = arrayList.get(position);
                Intent i = new Intent(MainActivity.this,Reading.class);
                i.putExtra(FILE_EXTRA,f.getPath());
                startActivity(i);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL,false));
        bar.setVisibility(View.VISIBLE);
        r = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @RequiresApi(api = Build.VERSION_CODES.R)
            @Override
            public void onActivityResult(ActivityResult result) {
                if(Environment.isExternalStorageManager()){
                    ArrayList<String> arrayList1 = new ArrayList<>();
                    arrayList1.add(Environment.getExternalStorageDirectory().getAbsolutePath());
                    generateList(arrayList1);
                }
                else{
                    text_center.setText("Разрешение не получено");
                    text_center.setVisibility(View.VISIBLE);
                    bar.setVisibility(View.INVISIBLE);
                }
            }
        });
        r1 = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    Log.d("LOLLOL","permission granted");
                    ArrayList<String> arrayList1 = new ArrayList<>();
                    arrayList1.add("/storage/emulated/0/Android");
                    generateList(arrayList1);
                }
                else{
                    text_center.setVisibility(View.VISIBLE);
                    text_center.setText("Разрешение не получено");
                    bar.setVisibility(View.INVISIBLE);
                }
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    @Override
    protected void onStart() {
        super.onStart();
        text_center.setVisibility(View.INVISIBLE);
        bar.setVisibility(View.INVISIBLE);
        if(!is_permission_window_openned) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    ArrayList<String> arrayList1 = new ArrayList<>();
                    arrayList1.add(Environment.getExternalStorageDirectory().getAbsolutePath());
                    generateList(arrayList1);
                } else {
                    Intent i = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    i.setData(Uri.fromParts("package", getPackageName(), null));
                    r.launch(i);
                }
            } else {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                    ArrayList<String> arrayList1 = new ArrayList<>();
                    arrayList1.add("/storage/emulated/0/Android");
                    generateList(arrayList1);
                } else {
                    Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    i.setData(Uri.fromParts("package", getPackageName(), null));
                    r1.launch(i);
                }
            }
            is_permission_window_openned = true;
        }

    }

    private void generateList(List<String> paths) {
        bar.setVisibility(View.VISIBLE);
        ListFilesTask listFilesTask = new ListFilesTask(paths);
        listFilesTask.setListener(new ListFilesTask.ListFilesListener() {
            @Override
            public void onTaskCompleted(List<File> files) {
                for(File f : files){
                    arrayList.add(new BookFile(f.getName(),f.getAbsolutePath(),f));
                }
                recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.VERTICAL,false));;recyclerView.setAdapter(adapter);
                Log.d("LOLKEK","search finished");
                Log.d("LOLKEK", String.valueOf(arrayList.size() == adapter.getItemCount()));
                adapter.notifyDataSetChanged();
                if(arrayList.size() > 0){
                    text_center.setVisibility(View.INVISIBLE);
                }
                else {
                    text_center.setText("Файлы типа .epub и .fb2 отсутствуют");
                    text_center.setVisibility(View.VISIBLE);
                }
                for(File f : files){
                    Log.d("LOLKEK",f.getAbsolutePath() + " " + f.getName());
                }
                bar.setVisibility(View.INVISIBLE);

            }
        });
        listFilesTask.execute();
    }

    static class ListFilesTask extends AsyncTask<Void, Void, List<File>> {
        public interface ListFilesListener {
            void onTaskCompleted(List<File> files);
        }
        private ListFilesListener listener;
        private final List<String> startPaths;
        private List<File> files;
        private boolean completed;

        public ListFilesTask(List<String> startPaths) {
            this.startPaths = new ArrayList<>(startPaths);
            this.files = new ArrayList<>();
            this.completed = false;
        }

        public void setListener(ListFilesListener listener) {
            this.listener = listener;
            if (completed && listener != null && files != null) {
                listener.onTaskCompleted(files);
            }
        }

        @Override protected List<File> doInBackground(Void... voids) {
            List<File> fileList = new ArrayList<>();
            for (String s : startPaths) {
                searchFiles(fileList, new File(s));
            }
            return fileList;
        }

        @Override
        protected void onPostExecute(List<File> files) {
            completed = true;
            if (listener != null) {
                listener.onTaskCompleted(files);
            } else {
                this.files = new ArrayList<>(files);
            }
        }
        private void searchFiles(List<File> list, File dir) {
            String epubPattern = ".epub";
            String fb2Pattern = ".fb2";
            File[] listFiles = dir.listFiles();
            if (listFiles != null) {
                for (File listFile : listFiles) {
                    if (listFile.isDirectory()) {
                        searchFiles(list, listFile);
                    } else {
                        if (listFile.getName().endsWith(epubPattern) || listFile.getName()
                                .endsWith(fb2Pattern)) {
                            list.add(listFile);
                        }
                    }
                }
            }
        }
    }
}
