package com.example.justicecamera;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessException;
import com.backendless.exceptions.BackendlessFault;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class VideoInfo extends AppCompatActivity {
    static ProgressDialog loading;
    ProgressDialog mProgressDialog;
    ProgressDialog play;
    Button buttonPlayVideo, buttonDownload, buttonApprove, buttonReject, buttonFeedback;
    TextView textViewVVideoName, textViewVCarModel, textViewVCarMake, textViewVCarColor, textViewVCarNumber, textViewVcategory, textViewVcomment, textViewVInfo, textViewVFeedback;
    Violation thisViolation;
    List<Violation> listViolation;
    String videoUrl;
    String objectId = "";
    String violLat = "";
    String violLongt = "";
    String thisObjectId;
    public static String THIS_OBJECT_ID = "objectId";
    public static String VIDEO_URL = "url";
    VideoView video;
    ProgressDialog pd;
    BackendlessUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_info);

        init();

        new FindViolationTask().execute();

        buttonPlayVideo.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                Intent i = new Intent(VideoInfo.this, VideoActivity.class);
                i.putExtra(VIDEO_URL, videoUrl);
                startActivity(i);
            }
        });

        buttonDownload.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                final DownloadTask downloadTask = new DownloadTask(VideoInfo.this);
                downloadTask.execute(videoUrl);
                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        downloadTask.cancel(true);
                    }
                });
            }
        });
    }

    private class DownloadTask extends AsyncTask<String, Integer, String> {

        private Context context;
        private PowerManager.WakeLock mWakeLock;

        public DownloadTask(Context context) {
            this.context = context;
        }

        @Override
        protected String doInBackground(String... sUrl) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(sUrl[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }
                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                String pathToDownload = Environment.getExternalStorageDirectory() + File.separator
                        + getString(R.string.app_name) + "/videoDownload_" + String.valueOf(System.currentTimeMillis()) + ".mp4";
                output = new FileOutputStream(pathToDownload);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, getString(R.string.error_download) + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, getString(R.string.file_downloaded), Toast.LENGTH_SHORT).show();
        }
    }

    public void setViolationParams(final Violation thisViolation) {

        if (thisViolation.getStatus().equals("0") && (user.getProperty("status").toString().equals("2") || user.getProperty("status").toString().equals("1"))) {
            buttonReject.setEnabled(true);
            buttonReject.setVisibility(View.VISIBLE);
            buttonApprove.setEnabled(true);
            buttonApprove.setVisibility(View.VISIBLE);
        }
        if (user.getProperty("status").toString().equals("2")) {
            buttonFeedback.setEnabled(true);
            buttonFeedback.setVisibility(View.VISIBLE);
        }

        textViewVVideoName.setText(thisViolation.getName());
        textViewVCarMake.setText(thisViolation.getCarMake());
        textViewVCarModel.setText(thisViolation.getCarModel());
        textViewVCarColor.setText(thisViolation.getColor());
        textViewVCarNumber.setText(thisViolation.getCarNumber());
        textViewVcategory.setText(thisViolation.getCategory().getType());
        textViewVcomment.setText(thisViolation.getComment());
        textViewVFeedback.setText(thisViolation.getFeedback());
        videoUrl = thisViolation.getVideoUrl();

    }

    private void init() {
        buttonPlayVideo = (Button) findViewById(R.id.buttonPlayVideo);
        buttonDownload = (Button) findViewById(R.id.buttonDownload);
        buttonApprove = (Button) findViewById(R.id.buttonApprove);
        buttonReject = (Button) findViewById(R.id.buttonReject);
        buttonFeedback = (Button) findViewById(R.id.buttonFeedback);
        textViewVVideoName = (TextView) findViewById(R.id.textViewVVideoName);
        textViewVCarModel = (TextView) findViewById(R.id.textViewVCarModel);
        textViewVCarMake = (TextView) findViewById(R.id.textViewVCarMake);
        textViewVCarColor = (TextView) findViewById(R.id.textViewVCarColor);
        textViewVCarNumber = (TextView) findViewById(R.id.textViewVCarNumber);
        textViewVcategory = (TextView) findViewById(R.id.textViewVcategory);
        textViewVcomment = (TextView) findViewById(R.id.textViewVcomment);
        textViewVFeedback = (TextView) findViewById(R.id.textViewVFeedback);
        textViewVInfo = (TextView) findViewById(R.id.textView10);
        listViolation = new ArrayList<>();
        thisViolation = new Violation();
        video = (VideoView) findViewById(R.id.videoView);

        buttonReject.setEnabled(false);
        buttonReject.setVisibility(View.INVISIBLE);
        buttonApprove.setEnabled(false);
        buttonApprove.setVisibility(View.INVISIBLE);
        buttonFeedback.setEnabled(false);
        buttonFeedback.setVisibility(View.INVISIBLE);

        user = Backendless.UserService.CurrentUser();

        buttonApprove.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                thisViolation.setStatus("1");
                new UpdateViolationTask().execute(thisViolation);
            }
        });

        buttonReject.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                try {
                    new DeleteViolation().execute(thisViolation);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        buttonFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(VideoInfo.this, Feedback.class);
                i.putExtra(THIS_OBJECT_ID, thisObjectId);
                startActivity(i);
            }
        });

        mProgressDialog = new ProgressDialog(VideoInfo.this);
        mProgressDialog.setMessage(getString(R.string.downloading));
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);

        Intent i = getIntent();
        objectId = i.getStringExtra(CheckedVideoList.OBJECTID);
        violLat = i.getStringExtra(MapsActivity.LATMAP);
        violLongt = i.getStringExtra(MapsActivity.LONGTMAP);
    }

    private void showDetails() {

        if (!(objectId == null)) {
            for (int i = 0; i < listViolation.size(); i++) {
                if (listViolation.get(i).getObjectId().equals(objectId)) {
                    thisViolation = listViolation.get(i);
                    thisObjectId = thisViolation.getObjectId();
                }
            }

            setViolationParams(thisViolation);

        } else if (!violLongt.equals("")) {

            for (int i = 0; i < listViolation.size(); i++) {
                if (listViolation.get(i).getLat().equals(violLat)) {
                    if (listViolation.get(i).getLongt().equals(violLongt)) {
                        thisViolation = listViolation.get(i);
                        thisObjectId = thisViolation.getObjectId();
                    }
                }
            }

            setViolationParams(thisViolation);

        }
    }

    private void checkUserStatus() {
        if (user.getProperty("status").toString().equals("2")) {
            buttonFeedback.setEnabled(true);
            buttonFeedback.setVisibility(View.VISIBLE);

            String videoOwnerId = thisViolation.getOwnerId();
            new FindUserByIdTask().execute(videoOwnerId);

        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }

    private class DeleteViolation extends AsyncTask<Violation, Void, String> {

        @Override
        protected void onPreExecute() {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            pd = new ProgressDialog(VideoInfo.this);
            pd.setTitle("Удаление ...");
            pd.setMessage(getString(R.string.wait));
            pd.show();
        }

        @Override
        protected String doInBackground(Violation... violations) {
            try {
                Helper.deleteViolation(violations[0]);
                return "deleted";
            } catch (Exception e) {
                return "error";
            }
        }

        protected void onPostExecute(String result) {
            if (result.equals("deleted")){
                Helper.showToast("Успешно удалено", VideoInfo.this);
            } else if (result.equals("error")){
                Helper.showToast("Error, something went wrong", VideoInfo.this);
            }

            buttonPlayVideo.setEnabled(false);
            buttonDownload.setEnabled(false);
            buttonApprove.setEnabled(false);
            buttonReject.setEnabled(false);
            buttonFeedback.setEnabled(false);
            pd.dismiss();

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }

    private class UpdateViolationTask extends AsyncTask<Violation, Void, String> {
        @Override
        protected void onPreExecute() {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
        }

        @Override
        protected String doInBackground(Violation... violations) {
            try {
                Helper.updateViolation(violations[0]);
                return "updated";
            } catch (BackendlessException e) {
                return "error";
            }
        }

        protected void onPostExecute(String result) {
            if (result.equals("updated")) {
                Helper.showToast(getString(R.string.approved), VideoInfo.this);
            } else if (result.equals("error")){
                Helper.showToast("Error, something went wrong", VideoInfo.this);
            }
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }

    private class FindViolationTask extends AsyncTask<Void, Void, BackendlessCollection<Violation>> {

        @Override
        protected void onPreExecute() {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            loading = new ProgressDialog(VideoInfo.this);
            loading.setTitle(getString(R.string.loading_info));
            loading.setMessage(getString(R.string.wait));
            loading.show();
        }

        @Override
        protected BackendlessCollection<Violation> doInBackground(Void... voids) {
            return Helper.getAllViolations();
        }

        protected void onPostExecute(BackendlessCollection<Violation> result) {

            listViolation = result.getData();
            showDetails();
            loading.dismiss();

            checkUserStatus();
        }
    }

    private class FindUserByIdTask extends AsyncTask<String, Void, BackendlessUser> {

        @Override
        protected void onPreExecute() {
            loading = new ProgressDialog(VideoInfo.this);
            loading.setTitle("Загрузка данных пользователя");
            loading.setMessage(getString(R.string.wait));
            loading.show();
        }

        @Override
        protected BackendlessUser doInBackground(String... strings) {

            return Helper.findUserById(strings[0]);
        }

        protected void onPostExecute(BackendlessUser videoOwner) {

            if (videoOwner != null) {
                textViewVcomment.append("  \n\n");
                textViewVcomment.append("Информация о пользователе: \n");
                textViewVcomment.append(videoOwner.getProperty("lastName").toString() + " " +
                        videoOwner.getProperty("firstName").toString() + " " +
                        videoOwner.getProperty("middleName").toString() + "\n");
                textViewVcomment.append(videoOwner.getEmail() + "\n");
                textViewVcomment.append(getString(R.string.passport_no) + ": " + videoOwner.getProperty("passportNo").toString() + "\n");
                textViewVcomment.append(getString(R.string.phone_number) + ": " + videoOwner.getProperty("phoneNumber").toString());
            }

            loading.dismiss();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }
}

