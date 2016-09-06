package com.example.justicecamera;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessException;
import com.backendless.exceptions.BackendlessFault;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class PersonalDataEdit extends AppCompatActivity {
    private ImageView mImageView;
    Button buttonSavePersonalData, buttonAddUserPhoto;
    EditText editLastname, editFirstname, editMiddlename, editCarNumber, editPassportNo, editPhoneNumber;
    RadioButton radioButtonMale, radioButtonFeMale;
    TextView textViewTester, textViewCard;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private Uri mImageCaptureUri;

    AlertDialog dialog;
    String pathToUserPhoto = "";
    int dayBirthday, monthBirthday, yearBirthday;
    String carNumber = "";
    String passportNo = "";
    String phoneNumber = "";
    boolean sex = true;
    String lastName = "";
    String firstName = "";
    String middleName;
    View.OnClickListener radioListener;
    NumberPicker day, month, year;
    BackendlessUser user = Backendless.UserService.CurrentUser();
    File fileUpl;
    ProgressDialog updatingUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data_edit);
        getSupportActionBar().setTitle(getString(R.string.dashboard));

        init();
        setUserParams();

        buttonSavePersonalData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                prepareFields();
                new UpdateUser().execute(photoFileToUpload());
            }
        });

        buttonAddUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }

    private void setUserParams() {
        if (user.getProperty("firstName") != null) {
            editFirstname.setText(user.getProperty("firstName").toString());

            if (user.getProperty("lastName")!= null){
                editLastname.setText(user.getProperty("lastName").toString());
            }
            if (user.getProperty("carNumber")!=null){
            editCarNumber.setText(user.getProperty("carNumber").toString());}
            if (user.getProperty("middleName")!=null){
            editMiddlename.setText(user.getProperty("middleName").toString());}
            if (user.getProperty("dayBirthday")!=null){
            day.setValue(Integer.parseInt(user.getProperty("dayBirthday").toString()));}
            if (user.getProperty("monthBirhday")!=null){
            month.setValue(Integer.parseInt(user.getProperty("monthBirthday").toString()));}
            if (user.getProperty("yearBirhday")!= null){
            year.setValue(Integer.parseInt(user.getProperty("yearBirhday").toString()));}
            if (user.getProperty("passportNo")!=null){
            editPassportNo.setText(user.getProperty("passportNo").toString());}
            if (user.getProperty("phoneNumber")!=null){
            editPhoneNumber.setText(user.getProperty("phoneNumber").toString());}
            if (user.getProperty("sex")!=null){
            boolean sex = (boolean) user.getProperty("sex");}
            radioButtonMale.setChecked(sex);
            radioButtonFeMale.setChecked(!sex);
        }
    }

    private void init() {
        mImageView = (ImageView) findViewById(R.id.imageView2);
        editLastname = (EditText) findViewById(R.id.editTextVideoName);
        editFirstname = (EditText) findViewById(R.id.editTextFirstname);
        editMiddlename = (EditText) findViewById(R.id.editTextMiddlename);
        editCarNumber = (EditText) findViewById(R.id.editTextCarNumber);
        editPassportNo = (EditText) findViewById(R.id.editTextPassportInfo);
        editPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        buttonSavePersonalData = (Button) findViewById(R.id.buttonSavePersonalData);
        buttonAddUserPhoto = (Button) findViewById(R.id.buttonAddPhoto);
        radioButtonMale = (RadioButton) findViewById(R.id.radioButtonMale);
        radioButtonFeMale = (RadioButton) findViewById(R.id.radioButtonFemale);
        textViewTester = (TextView) findViewById(R.id.textViewTester);
        textViewTester.setText(getString(R.string.required_fields));
        textViewCard = (TextView) findViewById(R.id.textViewCard);
        day = (NumberPicker) findViewById(R.id.numberPicker);
        month = (NumberPicker) findViewById(R.id.numberPicker2);
        year = (NumberPicker) findViewById(R.id.numberPicker3);
        day.setMaxValue(31);
        day.setMinValue(1);
        month.setMaxValue(12);
        month.setMinValue(1);
        year.setMaxValue(2000);
        year.setMinValue(1950);

        File folder = new File(Environment.getExternalStorageDirectory() + File.separator
                + getString(R.string.app_name)+File.separator+"user_"+user.getUserId());
        if (!folder.exists()) {
            folder.mkdir();
        }

        File file = new File(Environment.getExternalStorageDirectory() + File.separator
                + getString(R.string.app_name)+File.separator+"user_"+user.getUserId(), "user_photo.jpg");
        if (file.exists()) {
            Uri userPhoto = Uri.fromFile(file);
            mImageView.setImageURI(userPhoto);
            pathToUserPhoto = Environment.getExternalStorageDirectory() + File.separator
                    + getString(R.string.app_name)+File.separator+"user_"+user.getUserId() + "/user_photo.jpg";
        }

        // true = male, false = female
        radioListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                RadioButton rb = (RadioButton) v;
                switch (rb.getId()) {
                    case R.id.radioButtonMale:
                        sex = true;
                        break;
                    case R.id.radioButtonFemale:
                        sex = false;
                        break;
                    default:
                        break;
                }
            }
        };
        radioButtonFeMale.setOnClickListener(radioListener);
        radioButtonMale.setOnClickListener(radioListener);

        preparePhotoDialog();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != RESULT_OK) return;

        switch (requestCode) {
            case PICK_FROM_CAMERA:
                doCrop();

                break;

            case PICK_FROM_FILE:
                mImageCaptureUri = data.getData();

                doCrop();

                break;

            case CROP_FROM_CAMERA:
                Bundle extras = data.getExtras();

                Bitmap photo = null;
                if (extras != null) {
                    photo = extras.getParcelable("data");
                    mImageView.setImageBitmap(photo);
                }

                File f = new File(Environment.getExternalStorageDirectory() + File.separator
                        + getString(R.string.app_name)+File.separator+"user_"+user.getUserId(), "user_photo.jpg");
                pathToUserPhoto = f.getAbsolutePath();
                // Toast.makeText(getApplicationContext(), pathToUserPhoto, Toast.LENGTH_LONG).show();

                //    if (f.exists()) f.delete();

                FileOutputStream out = null;
                try {
                    out = new FileOutputStream(f);
                    photo.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                    // PNG is a lossless format, the compression factor (100) is ignored


                } catch (Exception e) {
                    e.printStackTrace();

                } finally {
                    try {
                        if (out != null) {
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                break;

        }

    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities(intent, 0);

        int size = list.size();

        if (size == 0) {
            Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();

            return;
        } else {
            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 300);
            intent.putExtra("outputY", 400);
            intent.putExtra("aspectX", 3);
            intent.putExtra("aspectY", 4);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();
                    co.title = getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Choose Crop Application");
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    private void preparePhotoDialog() {
        final String[] items = new String[]{getString(R.string.take_from_camera), getString(R.string.select_from_gallery)};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.select_image));
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) { //pick from camera
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + File.separator
                            + getString(R.string.app_name)+File.separator+"user_"+user.getUserId(), "user_photo.jpg"));

                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                    try {
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else { //pick from file
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        });
        dialog = builder.create();
    }

    private File photoFileToUpload() {
        final File fileSrc = new File(pathToUserPhoto);

        fileUpl = new File(Environment.getExternalStorageDirectory() + File.separator
                + getString(R.string.app_name) + "/userPhoto_" + user.getProperty("objectId") + ".jpg");
        try {
            copy(fileSrc, fileUpl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fileUpl;

    }

    private void prepareFields() {
        lastName = editLastname.getText().toString();
        firstName = editFirstname.getText().toString();
        middleName = editMiddlename.getText().toString();
        dayBirthday = day.getValue();
        monthBirthday = month.getValue();
        yearBirthday = year.getValue();
        carNumber = editCarNumber.getText().toString();
        passportNo = editPassportNo.getText().toString();
        phoneNumber = editPhoneNumber.getText().toString();

        user.setProperty("firstName", firstName);
        user.setProperty("lastName", lastName);
        user.setProperty("dayBirthday", dayBirthday);
        user.setProperty("monthBirthday", monthBirthday);
        user.setProperty("yearBirhday", yearBirthday);
        user.setProperty("carNumber", carNumber);
        user.setProperty("passportNo", passportNo);
        user.setProperty("phoneNumber", phoneNumber);
        user.setProperty("sex", sex);
        user.setProperty("middleName", middleName);

        if (user.getProperty("status") == null){
            user.setProperty("status", "0");
        }

    }

    private class UpdateUser extends AsyncTask<File, Void, String> {
        @Override
        protected void onPreExecute() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            updatingUser = new ProgressDialog(PersonalDataEdit.this);
            updatingUser.setTitle(getString(R.string.updating_user));
            updatingUser.setMessage(getString(R.string.wait));
            updatingUser.show();
        }

        @Override
        protected String doInBackground(File... files) {
            try {
                Helper.updateUserWithPhoto(user, files[0]);
                return "updated";
            } catch (BackendlessException exception) {
                return "error";
            } catch (Exception e) {
                return "error";
            }

        }

        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            if (result.equals("updated")) {
                Toast.makeText(getApplicationContext(), getString(R.string.updated_user), Toast.LENGTH_LONG).show();
            } else if (result.equals("error")){
                Toast.makeText(getApplicationContext(), "Error, something went wrong", Toast.LENGTH_LONG);
            }

            fileUpl.delete();
            updatingUser.dismiss();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }
}
