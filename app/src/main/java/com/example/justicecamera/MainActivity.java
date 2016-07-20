package com.example.justicecamera;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.BackendlessDataQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Intent getInt;
    static ProgressDialog pd;
    static EditText editCarMake;
    static EditText editCarModel;
    static EditText editCarNumber;
    static EditText editCarColor;
    static EditText editViolatCarComment;
    static EditText editVideoName;
    static List<Category_id> listCategory;
    static String violationType;
    static String videoUrl;
    static String path = "";
    String lat = "";
    String longt = "";
    Button buttonAddVideo;
    Button buttonSendViolation;
    Button buttonAddLocaton;
    private static int RESULT_LOAD_IMAGE = 1;
    static TextView textShowError;
    static final BackendlessUser user = Backendless.UserService.CurrentUser();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Justice Camera");
        getSupportActionBar().setSubtitle("Отправка видео нарушения");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);

        getInt = getIntent();
        listCategory = new ArrayList<>();
        editCarMake = (EditText) findViewById(R.id.editTextCarMake);
        editCarModel = (EditText) findViewById(R.id.editTextCarModel);
        editCarNumber = (EditText) findViewById(R.id.editTextViolatCarNumber);
        editCarColor = (EditText) findViewById(R.id.editTextViolatCarColor);
        editViolatCarComment = (EditText) findViewById(R.id.editTextComments);
        editVideoName = (EditText) findViewById(R.id.editTextVideoName);
        buttonAddVideo = (Button)findViewById(R.id.buttonAddVideo);
        buttonSendViolation = (Button) findViewById(R.id.buttonSendViolation);
        buttonAddLocaton = (Button) findViewById(R.id.buttonAddLocation);
       textShowError = (TextView) findViewById(R.id.textShowError);



        String[] data = {"Проезд на красный", "Пересечение двойной сплошной"};

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setPrompt("Тип нарушения");

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
               violationType = spinner.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        buttonAddVideo.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            }
        });

        buttonSendViolation.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                try {
                    uploadAsync();
                } catch (Exception e) {
                    textShowError.setText(e.getLocalizedMessage());
                }
            }
        });

        buttonAddLocaton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                Intent i = new Intent(MainActivity.this, AddViolationLocation.class);
                startActivity(i);
            }
        });

    }
    private void uploadAsync() throws Exception {
        pd = new ProgressDialog(MainActivity.this);
        pd.setTitle("Отправка видео");
        pd.setMessage("Подождите");
        pd.show();
        final File file = new File(path);

        // now upload the file
        Backendless.Files.upload(file, "/video", new AsyncCallback<BackendlessFile>() {
            @Override
            public void handleResponse(final BackendlessFile uploadedFile) {
                textShowError.setText("Видео загружено");

                BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                Backendless.Data.of(Category_id.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Category_id>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<Category_id> products) {
                        //Если загрузка массива продуктов успешна то передаем ее в наш лист productList
                        listCategory = products.getData();
                      //  String textToShow = "Успешно скачано, количество элементов " + Integer.toString(listCategory.size());

                        Category_id currentViolatCat = listCategory.get(0);
                        for (int i = 0; i< listCategory.size(); i++){
                            if (listCategory.get(i).getType().equals(violationType)){
                                currentViolatCat = listCategory.get(i);
                            }
                        }


                        videoUrl = uploadedFile.getFileURL();
                        Violation currentViolation = new Violation();
                       // Category_id currentViolationType = new Category_id();
                       // currentViolationType.setType(violationType);
                        currentViolation.setCarMake(editCarMake.getText().toString());
                        currentViolation.setCarModel(editCarModel.getText().toString());
                        currentViolation.setCarNumber(editCarNumber.getText().toString());
                        currentViolation.setCategory(currentViolatCat);
                        currentViolation.setColor(editCarColor.getText().toString());
                        currentViolation.setComment(editViolatCarComment.getText().toString());
                        currentViolation.setName(editVideoName.getText().toString());
                        currentViolation.setVideoUrl(videoUrl);
                        currentViolation.setUser_id(user);
                        lat = getInt.getStringExtra(AddViolationLocation.LAT);
                        longt = getInt.getStringExtra(AddViolationLocation.LONGT);
                        currentViolation.setLat(lat);
                        currentViolation.setLongt(longt);



                        Backendless.Persistence.save(currentViolation, new AsyncCallback<Violation>() {
                            public void handleResponse(Violation response) {
                                pd.dismiss();
                                textShowError.setText("Нарушение отправлено");
                                // new Contact instance has been saved
                            }

                            public void handleFault(BackendlessFault fault) {
                                textShowError.setText("error has occured" + fault.getMessage());
                                // an error has occurred, the error code can be retrieved with fault.getCode()
                            }
                        });
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {
                        textShowError.setText("Ошибка загрузки Categ " + backendlessFault.getMessage());

                    }
                });



            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                textShowError.setText("Server reported an error - " + backendlessFault.getMessage());
            }
        });
    }
    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            path = getRealPathFromURI(this, uri);
            textShowError.setText("Видео готово к отправке");
        } else {
           textShowError.setText("не прошел if ");
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.personalData) {
            Intent i = new Intent(MainActivity.this, PersonalDataEdit.class);
            startActivity(i);
        } else if (id == R.id.videoList) {
            Intent i = new Intent(MainActivity.this, CheckedVideoList.class);
            startActivity(i);

        } else if (id == R.id.moderation) {

        } else if (id == R.id.mapOfViolations) {
            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(i);
        } else if (id == R.id.nav_logout) {
            Backendless.UserService.logout( new AsyncCallback<Void>()
            {
                public void handleResponse( Void response )
                {
                    startActivity(new Intent(MainActivity.this, Login.class));
                    // user has been logged out.
                }

                public void handleFault( BackendlessFault fault )
                {
                    // something went wrong and logout failed, to get the error code call fault.getCode()
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
