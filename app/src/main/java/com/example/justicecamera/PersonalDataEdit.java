package com.example.justicecamera;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.backendless.exceptions.BackendlessFault;
import com.backendless.files.BackendlessFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PersonalDataEdit extends AppCompatActivity {
    private ImageView mImageView;
    Button buttonSavePersonalData, buttonAddUserPhoto;
    EditText editLastname, editFirstname, editCarNumber, editPassportNo, editPhoneNumber;
    RadioButton radioButtonMale, radioButtonFeMale;
    TextView textViewTester, textViewCard;
    ModeratorStatus defaultModeratorStatus;

    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;
    private Uri mImageCaptureUri;

    String pathToUserPhoto = "";
    int dayBirthday, monthBirthday, yearBirthday;
    String carNumber = "";
    String passportNo = "";
    int phoneNumber = 0;
    boolean sex = true;
    String photoUrl = "";
    String lastName = "";
    String firstName = "";
    View.OnClickListener radioListener;
    NumberPicker day, month, year;
    BackendlessUser user = Backendless.UserService.CurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data_edit);
        getSupportActionBar().setTitle(getString(R.string.dashboard));
        init();

        File file = new File(Environment.getExternalStorageDirectory() + File.separator
                + getString(R.string.app_name), "user_photo.jpg");
        if (file.exists()) {
            Uri userPhoto = Uri.fromFile(file);
            mImageView.setImageURI(userPhoto);
            pathToUserPhoto = Environment.getExternalStorageDirectory() + File.separator
                    + getString(R.string.app_name) + "/user_photo.jpg";
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

        if (!(user.getProperty("firstName") == null)) {
            editFirstname.setText(user.getProperty("firstName").toString());
            editLastname.setText(user.getProperty("lastName").toString());
            editCarNumber.setText(user.getProperty("carNumber").toString());
            day.setValue(Integer.parseInt(user.getProperty("dayBirthday").toString()));
            month.setValue(Integer.parseInt(user.getProperty("monthBirthday").toString()));
            year.setValue(Integer.parseInt(user.getProperty("yearBirhday").toString()));
            editPassportNo.setText(user.getProperty("passportNo").toString());
            editPhoneNumber.setText(user.getProperty("phoneNumber").toString());
            boolean sex = (boolean) user.getProperty("sex");
            radioButtonMale.setChecked(sex);
            radioButtonFeMale.setChecked(!sex);


        }

        buttonSavePersonalData.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final ProgressDialog updatingUser = new ProgressDialog(PersonalDataEdit.this);
                updatingUser.setTitle(getString(R.string.updating_user));
                updatingUser.setMessage(getString(R.string.wait));
                updatingUser.show();
                lastName = editLastname.getText().toString();
                firstName = editFirstname.getText().toString();
                dayBirthday = day.getValue();
                monthBirthday = month.getValue();
                yearBirthday = year.getValue();
                carNumber = editCarNumber.getText().toString();
                passportNo = editPassportNo.getText().toString();
                phoneNumber = Integer.parseInt(editPhoneNumber.getText().toString());

                user.setProperty("firstName", firstName);
                user.setProperty("lastName", lastName);
                user.setProperty("dayBirthday", dayBirthday);
                user.setProperty("monthBirthday", monthBirthday);
                user.setProperty("yearBirhday", yearBirthday);
                user.setProperty("carNumber", carNumber);
                user.setProperty("passportNo", passportNo);
                user.setProperty("phoneNumber", phoneNumber);
                user.setProperty("sex", sex);

                if (user.getProperty("moderator") == null) {
                    user.setProperty("moderator", defaultModeratorStatus);
                }

                final File fileSrc = new File(pathToUserPhoto);
                final File fileUpl = new File(Environment.getExternalStorageDirectory() + File.separator
                        + getString(R.string.app_name) + "/userPhoto_" + user.getProperty("objectId")+String.valueOf(System.currentTimeMillis())+ ".jpg");
                try {
                    copy(fileSrc, fileUpl);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                String path ="";
                String lastPart ="";

/*
                photoUrl = user.getProperty("photoUrl").toString();
                try {
                    //  URL url = new URL(photoUrl);
                    //   Toast.makeText(getApplicationContext(), url.getPath(), Toast.LENGTH_LONG ).show();
                    URI uri = new URI(photoUrl);
                    path = uri.getPath();
                    lastPart = path.substring(path.lastIndexOf('/') + 1);

                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
*/
                // URL url = Uri.parse(photoUrl).toURL();

//                Backendless.Files.remove("UsersPhoto/"+lastPart, new AsyncCallback<Void>() {
//                    @Override
//                    public void handleResponse(Void aVoid) {
                        Backendless.Files.upload(fileUpl, "/UsersPhoto", new AsyncCallback<BackendlessFile>() {
                            @Override
                            public void handleResponse(BackendlessFile uploadedPhoto) {
                                photoUrl = uploadedPhoto.getFileURL();
                                user.setProperty("photoUrl", photoUrl);

                                Backendless.UserService.update(user, new AsyncCallback<BackendlessUser>() {
                                    @Override
                                    public void handleResponse(BackendlessUser backendlessUser) {
                                        updatingUser.dismiss();
                                        textViewTester.setText(getString(R.string.updated_user));
                                        fileUpl.delete();
                                        //user has been updated
                                    }

                                    @Override
                                    public void handleFault(BackendlessFault backendlessFault) {
                                        textViewTester.setText(getString(R.string.error_occurred) + backendlessFault.getMessage() + getString(R.string.code) + backendlessFault.getCode());
                                        updatingUser.dismiss();
                                    }
                                });
                            }

                            @Override
                            public void handleFault(BackendlessFault backendlessFault) {
                                textViewTester.setText(getString(R.string.error_occurred) + backendlessFault.getMessage() + getString(R.string.code) + backendlessFault.getCode());
                                updatingUser.dismiss();
                            }
                        });

//                    }
//
//                    @Override
//                    public void handleFault(BackendlessFault backendlessFault) {
//                        updatingUser.dismiss();
//                            Toast.makeText(getApplicationContext(), "Error while file removing", Toast.LENGTH_LONG).show();
//                    }
//                });
            }
        });

        final String[] items = new String[]{"Take from camera", "Select from gallery"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, items);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Select Image");
        builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) { //pick from camera
                if (item == 0) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory() + File.separator
                            + getString(R.string.app_name), "user_photo.jpg"));

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

        final AlertDialog dialog = builder.create();

        buttonAddUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }

    private void init() {
        mImageView = (ImageView) findViewById(R.id.imageView2);
        editLastname = (EditText) findViewById(R.id.editTextVideoName);
        editFirstname = (EditText) findViewById(R.id.editTextFirstname);
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
        day.setMinValue(0);
        month.setMaxValue(12);
        month.setMinValue(0);
        year.setMaxValue(2000);
        year.setMinValue(1950);

        Backendless.Persistence.of(ModeratorStatus.class).findById(Defaults.DEFAULT_USER_STATUS_ID, new AsyncCallback<ModeratorStatus>() {
            @Override
            public void handleResponse(ModeratorStatus status) {
                defaultModeratorStatus = status;
            }

            @Override
            public void handleFault(BackendlessFault fault) {
                // an error has occurred, the error code can be retrieved with fault.getCode()
            }
        });
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

                // File f = new File(mImageCaptureUri.getPath());
                File f = new File(Environment.getExternalStorageDirectory() + File.separator
                        + getString(R.string.app_name), "user_photo.jpg");

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
                builder.setTitle("Choose Crop App");
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
    private void uploadData(){

    }

}
