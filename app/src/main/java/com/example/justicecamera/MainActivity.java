package com.example.justicecamera;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;
import com.backendless.persistence.BackendlessDataQuery;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    static ProgressDialog pd;
    static EditText editCarMake, editCarModel, editCarNumber, editCarColor, editViolatCarComment, editVideoName;
    static List<Category_id> listCategory;
    static String violationType, videoUrl;
    static String path = "";
    private static long back_pressed;
    String prefCarMake = "CarMake";
    String prefCarModel = "CarModel";
    String prefCarNumber = "CarNumber";
    String prefCarColor = "CarColor";
    String prefComment = "Comment";
    String prefVideoName = "VideoName";
    String prefCategory = "Category";
    String prefVideoPath = "path";
    SharedPreferences persData;
    String lat = "";
    String longt = "";
    VideoStatus defaultVideoStatus;
    Button buttonAddVideo, buttonSendViolation, buttonAddLocaton, buttonAddViolPhoto;
    CheckBox checkBoxVideo, checkBoxText, checkBoxLocation, checkBoxUser;
    List<String> listOfPhotoPath;
    private static int RESULT_LOAD_VIDEO = 1;
    private static int RESULT_LOAD_IMAGE = 7;
    private static int RESULT_ADD_LOC = 2;
    private static int RESULT_PERSDATA = 3;
    private static int RESULT_CHECKED_LIST = 4;
    private static int RESULT_MODERATOR_LIST = 5;
    private static int RESULT_MAP = 6;
    static TextView textShowError;
    Boolean isUserReady = false;
    Spinner spinner;
    BackendlessUser user;
    Violation current;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();

        if (user == null) {
            Backendless.UserService.isValidLogin(new AsyncCallback<Boolean>() {
                @Override
                public void handleResponse(Boolean isValidLogin) {
                    if (isValidLogin && Backendless.UserService.CurrentUser() == null) {
                        String currentUserId = Backendless.UserService.loggedInUser();

                        if (!currentUserId.equals("")) {
                            Backendless.UserService.findById(currentUserId, new AsyncCallback<BackendlessUser>() {
                                @Override
                                public void handleResponse(BackendlessUser currentUser) {
                                    Backendless.UserService.setCurrentUser(currentUser);
                                    isUserReady = true;
                                    setMenuItems(isUserReady);
                                    checkUser();
                                }

                                @Override
                                public void handleFault(BackendlessFault backendlessFault) {
                                    Toast.makeText(getApplicationContext(), " BackendError", Toast.LENGTH_LONG).show();
                                }
                            });
                        }
                    }
                }

                @Override
                public void handleFault(BackendlessFault backendlessFault) {

                }
            });
        } else {
            isUserReady = true;
            setMenuItems(isUserReady);
            checkUser();
        }

        buttonAddVideo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_VIDEO);
            }
        });

        buttonSendViolation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                /*
                try {
                    uploadAsync();
                } catch (Exception e) {
                    textShowError.setText(e.getLocalizedMessage());
                }
                */
                setViolationParams(current);
                new UploadViolationTask().execute(current);
            }
        });

        buttonAddLocaton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                saveCurrentInfo();
                Intent i = new Intent(MainActivity.this, AddViolationLocation.class);
                startActivityForResult(i, RESULT_ADD_LOC);
            }
        });
    }

    private void uploadAsync() throws Exception {
        pd = new ProgressDialog(MainActivity.this);
        pd.setTitle(getString(R.string.sendingVideo));
        pd.setMessage(getString(R.string.wait));
        pd.show();
        final File file = new File(path);

        // now upload the file
        Backendless.Files.upload(file, "/video", new AsyncCallback<BackendlessFile>() {
            @Override
            public void handleResponse(final BackendlessFile uploadedFile) {
                textShowError.setText(getString(R.string.uploadedVideo));

                BackendlessDataQuery dataQuery = new BackendlessDataQuery();
                Backendless.Data.of(Category_id.class).find(dataQuery, new AsyncCallback<BackendlessCollection<Category_id>>() {
                    @Override
                    public void handleResponse(BackendlessCollection<Category_id> products) {
                        listCategory = products.getData();

                        final Violation currentViolation = new Violation();

                        Category_id currentViolatCat = listCategory.get(0);
                        for (int i = 0; i < listCategory.size(); i++) {
                            if (listCategory.get(i).getType().equals(violationType)) {
                                currentViolatCat = listCategory.get(i);
                            }
                        }

                        videoUrl = uploadedFile.getFileURL();

                        currentViolation.setCarMake(editCarMake.getText().toString());
                        currentViolation.setCarModel(editCarModel.getText().toString());
                        currentViolation.setCarNumber(editCarNumber.getText().toString());
                        currentViolation.setCategory(currentViolatCat);
                        currentViolation.setColor(editCarColor.getText().toString());
                        currentViolation.setComment(editViolatCarComment.getText().toString());
                        currentViolation.setName(editVideoName.getText().toString());
                        currentViolation.setVideoUrl(videoUrl);
                        currentViolation.setLat(lat);
                        currentViolation.setLongt(longt);
                        currentViolation.setVideoStatus(defaultVideoStatus);

                        Backendless.Persistence.save(currentViolation, new AsyncCallback<Violation>() {
                            public void handleResponse(Violation response) {
                                pd.dismiss();
                                textShowError.setText(getString(R.string.uploadedViolation));
                            }

                            public void handleFault(BackendlessFault fault) {
                                textShowError.setText(getString(R.string.error_occurred) + fault.getMessage());
                                // an error has occurred, the error code can be retrieved with fault.getCode()
                            }
                        });
                    }

                    @Override
                    public void handleFault(BackendlessFault backendlessFault) {

                    }
                });
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {
                pd.dismiss();
                textShowError.setText(getString(R.string.server_error) + backendlessFault.getMessage());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_VIDEO) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                Uri uri = data.getData();
                path = getRealPathFromURI(this, uri);
                checkBoxVideo.setChecked(true);
                checkBoxVideo.setText(getString(R.string.added_video));

            } else {
                checkBoxVideo.setChecked(false);
                checkBoxVideo.setText(getString(R.string.video_notselected));
            }
        }

        if (requestCode == RESULT_ADD_LOC)

        {
            if (resultCode == RESULT_OK) {
                lat = data.getStringExtra(AddViolationLocation.LAT);
                longt = data.getStringExtra(AddViolationLocation.LONGT);
                loadCurrentInfo();
            } else {
                checkBoxLocation.setChecked(false);
                checkBoxLocation.setText(getString(R.string.add_violation_location));
            }
        }

        if (requestCode == RESULT_PERSDATA)

        {
            if (resultCode == RESULT_OK) {

            } else {
                checkUser();
            }
        }

        if (requestCode == RESULT_CHECKED_LIST)

        {
            if (resultCode == RESULT_OK) {

            } else {

            }
        }

        if (requestCode == RESULT_MODERATOR_LIST)

        {
            if (resultCode == RESULT_OK) {

            } else {

            }
        }

        if (requestCode == RESULT_MAP)

        {
            if (resultCode == RESULT_OK) {

            } else {

            }
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (back_pressed + 2000 > System.currentTimeMillis()) {
                Intent a = new Intent(Intent.ACTION_MAIN);
                a.addCategory(Intent.CATEGORY_HOME);
                a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(a);
            } else {
                Toast.makeText(getBaseContext(), getString(R.string.press_to_exit),
                        Toast.LENGTH_SHORT).show();
            }
            back_pressed = System.currentTimeMillis();
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
            startActivityForResult(i, RESULT_PERSDATA);
        } else if (id == R.id.videoList) {
            Intent i = new Intent(MainActivity.this, CheckedVideoList.class);
            startActivityForResult(i, RESULT_CHECKED_LIST);

        } else if (id == R.id.moderation) {
            BackendlessUser user = Backendless.UserService.CurrentUser();
            HashMap<String, Object> map = (HashMap) user.getProperty("moderator");
            int stat = (Integer) map.get("status");
            if (stat == 0) {
                Toast toast = Toast.makeText(getApplicationContext(),
                        getString(R.string.no_moderator_permission), Toast.LENGTH_LONG);
                toast.show();
            } else if (stat == 1) {
                startActivityForResult(new Intent(MainActivity.this, ModeratorVideoList.class), RESULT_MODERATOR_LIST);
            }
        } else if (id == R.id.mapOfViolations) {
            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            startActivityForResult(i, RESULT_MAP);
        } else if (id == R.id.nav_logout) {
            Backendless.UserService.logout(new AsyncCallback<Void>() {
                public void handleResponse(Void response) {
                    startActivity(new Intent(MainActivity.this, Login.class));
                    // user has been logged out.
                }

                public void handleFault(BackendlessFault fault) {
                    // something went wrong and logout failed, to get the error code call fault.getCode()
                }
            });
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private String getRealPathFromURI(Context context, Uri contentUri) {
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

    private void checkText() {
        if (!(editCarColor.getText().toString().equals("")) && !(editCarNumber.getText().toString().equals("")) && !(editCarModel.getText().toString().equals("")) && !(editVideoName.getText().toString().equals("")) && !(editCarMake.getText().toString().equals("")) && !(editViolatCarComment.getText().toString().equals(""))) {
            checkBoxText.setChecked(true);
            checkBoxText.setText(getString(R.string.fields_filled));
        } else {
            checkBoxText.setChecked(false);
            checkBoxText.setText(getString(R.string.fields_notfilled));
        }
    }

    private void checkBox() {
        if (checkBoxText.isChecked() && checkBoxVideo.isChecked() && checkBoxLocation.isChecked() && checkBoxUser.isChecked()) {
            buttonSendViolation.setEnabled(true);
            textShowError.setText(getString(R.string.ready_forsend));
        } else {
            buttonSendViolation.setEnabled(false);
            textShowError.setText(R.string.fill_all_data);
        }
    }

    private void init() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setSubtitle(getString(R.string.send_violation));
        File folder = new File(Environment.getExternalStorageDirectory() + File.separator
                + getString(R.string.app_name));
        if (!folder.exists()) {
             folder.mkdir();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
        setMenuItems(false);

        listCategory = new ArrayList<>();
        editCarMake = (EditText) findViewById(R.id.editTextCarMake);
        editCarModel = (EditText) findViewById(R.id.editTextCarModel);
        editCarNumber = (EditText) findViewById(R.id.editTextViolatCarNumber);
        editCarColor = (EditText) findViewById(R.id.editTextViolatCarColor);
        editViolatCarComment = (EditText) findViewById(R.id.editTextComments);
        editVideoName = (EditText) findViewById(R.id.editTextVideoName);
        buttonAddVideo = (Button) findViewById(R.id.buttonAddVideo);
        buttonSendViolation = (Button) findViewById(R.id.buttonSendViolation);
        buttonAddLocaton = (Button) findViewById(R.id.buttonAddLocation);
      //  buttonAddViolPhoto = (Button) findViewById(R.id.buttonAddViolPhoto);
        buttonSendViolation.setEnabled(false);
        textShowError = (TextView) findViewById(R.id.textShowError);
        checkBoxLocation = (CheckBox) findViewById(R.id.checkBoxLocation);
        checkBoxLocation.setEnabled(false);
        checkBoxText = (CheckBox) findViewById(R.id.checkBoxText);
        checkBoxText.setEnabled(false);
        checkBoxVideo = (CheckBox) findViewById(R.id.checkBoxVideo);
        checkBoxVideo.setEnabled(false);
        checkBoxUser = (CheckBox) findViewById(R.id.checkBoxUser);
        checkBoxUser.setEnabled(false);
        user = Backendless.UserService.CurrentUser();
        listOfPhotoPath = new ArrayList<>();
        current = new Violation();

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                checkText();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        };
        CompoundButton.OnCheckedChangeListener checkBoxListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()) {
                    checkBox();
                } else {
                    checkBox();
                }
            }
        };

        checkBoxVideo.setOnCheckedChangeListener(checkBoxListener);
        checkBoxText.setOnCheckedChangeListener(checkBoxListener);
        checkBoxLocation.setOnCheckedChangeListener(checkBoxListener);
        checkBoxUser.setOnCheckedChangeListener(checkBoxListener);
        editViolatCarComment.addTextChangedListener(textWatcher);
        editCarMake.addTextChangedListener(textWatcher);
        editVideoName.addTextChangedListener(textWatcher);
        editCarNumber.addTextChangedListener(textWatcher);
        editCarModel.addTextChangedListener(textWatcher);
        editCarColor.addTextChangedListener(textWatcher);

        String[] data = {"Проезд на красный", "Пересечение двойной сплошной"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, data);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        spinner.setPrompt(getString(R.string.violation_type));
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

        Backendless.Persistence.of(VideoStatus.class).findById(Defaults.DEFAULT_VIDEO_STATUS_ID, new AsyncCallback<VideoStatus>() {
            @Override
            public void handleResponse(VideoStatus videoStatus) {
                defaultVideoStatus = videoStatus;
            }

            @Override
            public void handleFault(BackendlessFault backendlessFault) {

            }
        });
    }

    private void setMenuItems(Boolean isUserReady) {
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu navMenu = navigationView.getMenu();
        if (isUserReady) {
            MenuItem item1 = navMenu.findItem(R.id.personalData);
            item1.setEnabled(true);
            MenuItem item2 = navMenu.findItem(R.id.moderation);
            item2.setEnabled(true);
        } else {
            MenuItem item1 = navMenu.findItem(R.id.personalData);
            item1.setEnabled(false);
            MenuItem item2 = navMenu.findItem(R.id.moderation);
            item2.setEnabled(false);
        }
    }

    private void saveCurrentInfo() {
        persData = getSharedPreferences("Data", MODE_PRIVATE);
        SharedPreferences.Editor ed = persData.edit();
        ed.putString(prefCarMake, editCarMake.getText().toString());
        ed.putString(prefCarModel, editCarModel.getText().toString());
        ed.putString(prefCarColor, editCarColor.getText().toString());
        ed.putString(prefCarNumber, editCarNumber.getText().toString());
        ed.putString(prefComment, editViolatCarComment.getText().toString());
        ed.putString(prefVideoName, editVideoName.getText().toString());
        ed.putInt(prefCategory, spinner.getSelectedItemPosition());
        ed.putString(prefVideoPath, path);
        ed.commit();
    }

    private void loadCurrentInfo() {

        persData = getSharedPreferences("Data", MODE_PRIVATE);
        editCarColor.setText(persData.getString(prefCarColor, ""));
        editCarMake.setText(persData.getString(prefCarMake, ""));
        editViolatCarComment.setText(persData.getString(prefComment, ""));
        editVideoName.setText(persData.getString(prefVideoName, ""));
        editCarModel.setText(persData.getString(prefCarModel, ""));
        editCarNumber.setText(persData.getString(prefCarNumber, ""));
        spinner.setSelection(persData.getInt(prefCategory, 0));
        setMenuItems(isUserReady);

        path = persData.getString(prefVideoPath, "");
        if (!(path.equals(""))) {
            checkBoxVideo.setChecked(true);
            checkBoxVideo.setText(getString(R.string.added_video));
        } else {
            checkBoxVideo.setChecked(false);
            checkBoxVideo.setText(R.string.video_notselected);
        }

        if (!lat.equals("")) {
            checkBoxLocation.setChecked(true);
            checkBoxLocation.setText(getString(R.string.v_coordinates_added));
        }
    }

    private void checkUser() {
        user = Backendless.UserService.CurrentUser();
        int count = 0;
        if (!(user.getProperty("firstName")==null)&& !user.getProperty("firstName").toString().equals(""))
            count++;
        if (!(user.getProperty("lastName")==null) && (!user.getProperty("lastName").toString().equals("")))
            count++;
        if (!(user.getProperty("passportNo") == null) && !user.getProperty("passportNo").toString().equals(""))
            count++;
        if (!(user.getProperty("phoneNumber") == null) && !user.getProperty("phoneNumber").toString().equals(""))
            count++;
        if (count == 4) {
            checkBoxUser.setChecked(true);
        } else {
            checkBoxUser.setChecked(false);
            Toast.makeText(getApplicationContext(), getString(R.string.fill_the_form), Toast.LENGTH_LONG).show();
        }
    }

    public void setViolationParams(Violation currentViolation){
        currentViolation.setCarMake(editCarMake.getText().toString());
        currentViolation.setCarModel(editCarModel.getText().toString());
        currentViolation.setCarNumber(editCarNumber.getText().toString());
        currentViolation.setColor(editCarColor.getText().toString());
        currentViolation.setComment(editViolatCarComment.getText().toString());
        currentViolation.setName(editVideoName.getText().toString());
        currentViolation.setLat(lat);
        currentViolation.setLongt(longt);
        currentViolation.setVideoStatus(defaultVideoStatus);
    }

    private class UploadViolationTask extends AsyncTask <Violation, Void, Void>{

        @Override
        protected void onPreExecute() {
            pd = new ProgressDialog(MainActivity.this);
            pd.setTitle(getString(R.string.sendingVideo));
            pd.setMessage(getString(R.string.wait));
            pd.show();
        }

        @Override
        protected Void doInBackground(Violation... violations) {
            final  File file = new File(path);
            try {
                Helper.uploadVideo(file);
                listCategory = Helper.getAllCategories().getData();

                Category_id currentViolatCat = listCategory.get(0);
                for (int i = 0; i < listCategory.size(); i++) {
                    if (listCategory.get(i).getType().equals(violationType)) {
                        currentViolatCat = listCategory.get(i);
                    }
                }

                violations[0].setCategory(currentViolatCat);
                violations[0].setVideoUrl("https://api.backendless.com/" + Defaults.APPLICATION_ID + "/" + Defaults.VERSION + "/files/video/"  +file.getName());
                violations[0] = Backendless.Persistence.save(violations[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {
            pd.dismiss();
        }
    }
}
