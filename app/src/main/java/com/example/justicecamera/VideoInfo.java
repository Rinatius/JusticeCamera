package com.example.justicecamera;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessException;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;

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
    String videoUrl, photoUrls;
    String objectId = "";
    String violLat = "";
    String violLongt = "";
    String thisObjectId;
    public static String THIS_OBJECT_ID = "objectId";
    public static String VIDEO_URL = "url";
    VideoView video;
    //ImageView img, img2, img3, img4, img5;
    ArrayList<ImageView> imgList;
    ArrayList<String> listOfPhotoUrls;
    ProgressDialog pd;
    BackendlessUser user;
    GridLayout grid;
    int columnCount = 2;
    int rowCount = 2;
    int width, height;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
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
        photoUrls = thisViolation.getPhotoUrl();
        if (photoUrls.length() > 0) {

            listOfPhotoUrls = getUrlsFromString(photoUrls);
            int k = 0;
            for (int i = 0; i < grid.getRowCount(); i++){
                for (int j = 0; j < grid.getColumnCount(); j++) {
                    if (k < listOfPhotoUrls.size()) {
                        final SimpleDraweeView img = new SimpleDraweeView(this);
                        GenericDraweeHierarchyBuilder builder =
                                new GenericDraweeHierarchyBuilder(getResources());
                        GenericDraweeHierarchy hierarchy = builder
                                //.setFadeDuration(300)
                                .setPlaceholderImage(R.drawable.car)
                                .setActualImageScaleType(ScalingUtils.ScaleType.FIT_CENTER)
                                .setProgressBarImage(new ProgressBarDrawable())
                                //.setBackground(background)
                                //.setOverlays(overlaysList)
                                .build();
                        img.setHierarchy(hierarchy);

                        GridLayout.LayoutParams lpImg = new GridLayout.LayoutParams();
                        lpImg.columnSpec = GridLayout.spec(j);
                        lpImg.rowSpec = GridLayout.spec(i);
                        lpImg.width = width/columnCount - width/14;
                        lpImg.height = lpImg.width;

                        Uri uri = Uri.parse(listOfPhotoUrls.get(k));
                        img.setImageURI(uri);
                        grid.addView(img, lpImg);
                        img.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(VideoInfo.this);


                                final AlertDialog dialog = builder.create();
                                LayoutInflater inflater = getLayoutInflater();
                                View dialogLayout = inflater.inflate(R.layout.image_dialog, null);
                                dialog.setView(dialogLayout);
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                final Drawable drawable = img.getDrawable();

                                img.buildDrawingCache();
                                final Bitmap bitmap = img.getDrawingCache();

                               // final Drawable drawable = img.getHierarchy().getTopLevelDrawable();

                                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(final DialogInterface d) {
                                        ImageView image = (ImageView) dialog.findViewById(R.id.imageView2);
                                        image.setBackgroundColor(Color.WHITE);
                                        //image.setImageDrawable(drawable);
                                        image.setImageBitmap(bitmap);
                                        image.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                d.dismiss();
                                            }
                                        });
                                    }
                                });

                                dialog.show();
                            }
                        });

                        k++;

                    }

//                    SimpleDraweeView img = new SimpleDraweeView(this);
//                    GridLayout.LayoutParams lpImg = new GridLayout.LayoutParams();
//                    lpImg.columnSpec = GridLayout.spec(j);
//                    lpImg.rowSpec = GridLayout.spec(i);
//                    lpImg.width = width / columnCount;
//                    lpImg.height = lpImg.width;
//                    Uri uri = Uri.parse(listOfPhotoUrls.get(k));
//                    img.setBackgroundColor(Color.BLACK);
//                    img.setImageURI(uri);
//                    grid.addView(img, lpImg);
//                    k++;

                }
            }
          // new DownloadImgs().execute(listOfPhotoUrls.toArray(new String[listOfPhotoUrls.size()]));


        } else {
            checkUserStatus();
        }
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
        grid = (GridLayout) findViewById(R.id.grid);
        grid.setColumnCount(columnCount);
        grid.setRowCount(rowCount);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

//        for (int i = 0; i < grid.getRowCount(); i++){
//            for (int j = 0; j < grid.getColumnCount(); j++) {
//                final ImageView img = new ImageView(this);
//                GridLayout.LayoutParams lpImage = new GridLayout.LayoutParams();
//                lpImage.columnSpec = GridLayout.spec(j);
//                lpImage.rowSpec = GridLayout.spec(i);
//                lpImage.width = width/11;
//                lpImage.height = lpImage.width;
//                img.setImageResource(R.drawable.anon);
//                img.setScaleType(ImageView.ScaleType.FIT_CENTER);
//                grid.addView(img, lpImage);
//
//            }
//        }

        listViolation = new ArrayList<>();
        thisViolation = new Violation();
        video = (VideoView) findViewById(R.id.videoView);
//        img = (ImageView) findViewById(R.id.violImg);
//        img2 = (ImageView) findViewById(R.id.violImg2);
//        img3 = (ImageView) findViewById(R.id.violImg3);
//        img4 = (ImageView) findViewById(R.id.violImg4);
//        img5 = (ImageView) findViewById(R.id.violImg5);
//        imgList = new ArrayList<>();
//        imgList.add(img);
//        imgList.add(img2);
//        imgList.add(img3);
//        imgList.add(img4);
//        imgList.add(img5);
        listOfPhotoUrls = new ArrayList<>();

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

    private void drawGrid(){

    }

    private ArrayList<String> getUrlsFromString(String photoUrls){
        ArrayList<String> listOfUrls = new ArrayList<>();
        char[] chars = photoUrls.toCharArray();
        StringBuilder url = new StringBuilder();
        for (int i = 0; i < chars.length; i++){
            if (chars[i] != ' ') {
                url.append(chars[i]);
            } else {
                if (url.length() > 0){
                    listOfUrls.add(url.toString());
                    url.setLength(0);
                }
            }
        }
        return listOfUrls;
    }

    private void setImgsFromUrls(ArrayList<String> urls){


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
            loading.dismiss();
            showDetails();

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

    private class DownloadImgs extends AsyncTask<String, Void, ArrayList<Bitmap>> {

        @Override
        protected void onPreExecute() {
            loading = new ProgressDialog(VideoInfo.this);
            loading.setCancelable(false);
            loading.setTitle("Загрузка изображений");
            loading.setMessage(getString(R.string.wait));
            loading.show();
        }

        @Override
        protected ArrayList<Bitmap> doInBackground(String... urls) {
            ArrayList<Bitmap> list = new ArrayList<>();
            for (int i = 0; i < urls.length; i++) {
                String urldisplay = urls[i];
                Bitmap mIcon11 = null;
                //InputStream in;
                try {
                    InputStream in = new java.net.URL(urldisplay).openStream();
                    mIcon11 = BitmapFactory.decodeStream(in);
                    list.add(mIcon11);
                } catch (Exception e) {
                    // Toast.makeText(VideoInfo.class, "Error", Toast.LENGTH_SHORT).show();
                    // e.printStackTrace();
                }
                //return mIcon11;
                //return Helper.findUserById(strings[0]);
            }
            return list;
        }

        protected void onPostExecute(ArrayList<Bitmap> bitmaps) {

            for (int i = 0; i < bitmaps.size(); i++){
                imgList.get(i).setImageBitmap(bitmaps.get(i));
            }

            loading.dismiss();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            checkUserStatus();
        }
    }
}

