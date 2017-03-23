package com.example.justicecamera;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.backendless.Backendless;
import com.backendless.BackendlessCollection;
import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessException;
import com.danikula.videocache.HttpProxyCacheServer;
import com.facebook.drawee.drawable.ProgressBarDrawable;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.stfalcon.frescoimageviewer.ImageViewer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import fm.jiecao.jcvideoplayer_lib.JCVideoPlayer;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;

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
    BackendlessUser videoOwner;
    public static String THIS_OBJECT_ID = "objectId";
    public static String VIDEO_URL = "url";
    VideoView video;
    ArrayList<String> listOfPhotoUrls;
    ProgressDialog pd;
    BackendlessUser user;
    GridLayout grid;
    int columnCount = 3;
    int rowCount = 2;
    int width, height;

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Fresco.initialize(this);
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
        if (thisViolation.getStatus().equals("0")
                && (user.getProperty("status").toString().equals("2")
                || user.getProperty("status").toString().equals("1"))) {
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

        if (!(videoUrl.equals(""))) {

            HttpProxyCacheServer proxy = App.getProxy(getApplicationContext());
            String proxyUrl = proxy.getProxyUrl(videoUrl);

            JCVideoPlayerStandard jcVideoPlayerStandard = new JCVideoPlayerStandard(VideoInfo.this);
            jcVideoPlayerStandard.setUp(proxyUrl
                    , JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, thisViolation.getName());

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            jcVideoPlayerStandard.setLayoutParams(params);

            linearLayout.addView(jcVideoPlayerStandard);
        }

        photoUrls = thisViolation.getPhotoUrl();
        if (photoUrls != null) {
            if (photoUrls.length() > 0) {

                listOfPhotoUrls = Helper.getUrlsFromString(photoUrls);
                int k = 0;
                for (int i = 0; i < grid.getRowCount(); i++) {
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

                            final int position = k;

                            GridLayout.LayoutParams lpImg = new GridLayout.LayoutParams();
                            lpImg.columnSpec = GridLayout.spec(j);
                            lpImg.rowSpec = GridLayout.spec(i);
                            lpImg.width = width / columnCount - width / 20;
                            lpImg.height = lpImg.width;

                            Uri uri = Uri.parse(listOfPhotoUrls.get(k));
                            img.setImageURI(uri);

                            grid.addView(img, lpImg);
                            img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    new ImageViewer.Builder(VideoInfo.this, listOfPhotoUrls)
                                            .setStartPosition(position)
                                            //.hideStatusBar(false)
//                                        .setImageMargin(this, R.dimen.image_margin)
//                                        .setImageChangeListener(getImageChangeListener())
//                                        .setOnDismissListener(getDisissListener())
//                                        .setCustomDraweeHierarchyBuilder(getHierarchyBuilder())
//                                        .setOverlayView(overlayView)
                                            .show();
                                }
                            });

                            k++;

                        }

                    }
                }
                checkUserStatus();

            } else {
                checkUserStatus();
            }
        }
    }

    private void init() {
        linearLayout = (LinearLayout) findViewById(R.id.videoLayout);
        buttonPlayVideo = (Button) findViewById(R.id.buttonPlayVideo);
        buttonPlayVideo.setVisibility(View.INVISIBLE);
        buttonDownload = (Button) findViewById(R.id.buttonDownload);
        buttonDownload.setVisibility(View.INVISIBLE);
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

        listViolation = new ArrayList<>();
        thisViolation = new Violation();
        video = (VideoView) findViewById(R.id.videoView);
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

    private ArrayList<String> getUrlsFromString(String photoUrls) {
        ArrayList<String> listOfUrls = new ArrayList<>();
        char[] chars = photoUrls.toCharArray();
        StringBuilder url = new StringBuilder();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] != ' ') {
                url.append(chars[i]);
            } else {
                if (url.length() > 0) {
                    listOfUrls.add(url.toString());
                    url.setLength(0);
                }
            }
        }
        return listOfUrls;
    }

    private class DeleteViolation extends AsyncTask<Violation, Void, String> {

        @Override
        protected void onPreExecute() {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            pd = ProgressDialog.show(VideoInfo.this, getString(R.string.deleting), getString(R.string.wait), true);
//            pd = new ProgressDialog(VideoInfo.this);
//            pd.setTitle("Удаление ...");
//            pd.setMessage(getString(R.string.wait));
//            pd.show();
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
            if (result.equals("deleted")) {
                Helper.showToast("Успешно удалено", VideoInfo.this);
            } else if (result.equals("error")) {
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
            pd = ProgressDialog.show(VideoInfo.this, "", getString(R.string.wait), true);
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
                //статус нарушения обновлен, начало поиска пароля
                //Helper.showToast(getString(R.string.approved), VideoInfo.this);
                buttonApprove.setVisibility(View.INVISIBLE);
                buttonReject.setVisibility(View.INVISIBLE);
                new GetPassword().execute();
            } else if (result.equals("error")) {
                pd.dismiss();
                Helper.showToast("Error, something went wrong", VideoInfo.this);
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            }
            //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }

    private class FindViolationTask extends AsyncTask<Void, Void, BackendlessCollection<Violation>> {

        @Override
        protected void onPreExecute() {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            loading = ProgressDialog.show(VideoInfo.this, getString(R.string.loading_info), getString(R.string.wait), true);
//            loading = new ProgressDialog(VideoInfo.this);
//            loading.setTitle(getString(R.string.loading_info));
//            loading.setMessage(getString(R.string.wait));
//            loading.show();
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
            loading = ProgressDialog.show(VideoInfo.this, "Загрузка данных пользователя", getString(R.string.wait), true);
//            loading = new ProgressDialog(VideoInfo.this);
//            loading.setTitle("Загрузка данных пользователя");
//            loading.setMessage(getString(R.string.wait));
//            loading.show();
        }

        @Override
        protected BackendlessUser doInBackground(String... strings) {

            return Helper.findUserById(strings[0]);
        }

        protected void onPostExecute(BackendlessUser owner) {

            if (owner != null) {
                videoOwner = owner;
                textViewVcomment.append("  \n\n");
                textViewVcomment.append("Информация о пользователе: \n");
                textViewVcomment.append(owner.getProperty("lastName").toString() + " " +
                        owner.getProperty("firstName").toString() + " " +
                        owner.getProperty("middleName").toString() + "\n");
                textViewVcomment.append(owner.getEmail() + "\n");
                textViewVcomment.append(getString(R.string.passport_no) + ": " + owner.getProperty("passportNo").toString() + "\n");
                textViewVcomment.append(getString(R.string.phone_number) + ": " + owner.getProperty("phoneNumber").toString());
            }

            loading.dismiss();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }

    private class GetPassword extends  AsyncTask<Void, Void, String>{

        @Override
        protected void onPreExecute(){
            //pd = ProgressDialog.show(VideoInfo.this, "", "Загрузка", true);
        }

        @Override
        protected String doInBackground(Void... params) {
            Acct passHolder = Helper.getApplicationInfo();
            String password = passHolder.getPassword();

            if ((password != null) && (!password.equals(""))) {
                return password;
            } else return "ERROR";
        }

        @Override
        protected void onPostExecute(String result){

            if (result.equals("ERROR")){
                //статус был обновлен, но не получилось скачать пароль, письмо не будет отправлено
                Toast.makeText(VideoInfo.this, getString(R.string.approved), Toast.LENGTH_SHORT).show();
                pd.dismiss();
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            } else { //пароль найден, запуск отправки письма
                //String body = "test body";
                String body = setEmailBodyText(new StringBuilder());
                new SendEmail().execute(result, body);
                //Toast.makeText(VideoInfo.this, result, Toast.LENGTH_SHORT).show();
            }

           // pd.dismiss();
        }
    }

    private class SendEmail extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            Mail m = new Mail("tester.kloop@gmail.com", params[0]);

            m.setTo(Defaults.onViolationApprovedMailRecipients);
            m.setFrom("JusticeCamera");
            m.setSubject("Заявление одобрено");
            m.setBody(params[1]);

            try {
                if (m.send()) {
                    // Toast.makeText(MailApp.this, "Email was sent successfully.", Toast.LENGTH_LONG).show();
                    return "OK";
                } else {
                    //Toast.makeText(MailApp.this, "Email was not sent.", Toast.LENGTH_LONG).show();
                    return "ERROR";
                }
            } catch (Exception e) {
                return e.toString();
            }
        }

        @Override
        protected void onPostExecute(String result){
            Toast.makeText(VideoInfo.this, getString(R.string.approved), Toast.LENGTH_SHORT).show();
            if (result.equals("OK")) {
                //Нарушение одобрено, письмо отправлено
            } else if (result.equals("ERROR")) {
                //нарушение одобрено, ошибка при отправке письма
                //Toast.makeText(VideoInfo.this, "Error sending message", Toast.LENGTH_SHORT).show();
            } else {
                //нарушение одобрено, ошибка Exception
                //можно прочитать данные об ошибке как result
                //Toast.makeText(VideoInfo.this, "Ошибка exception "+result, Toast.LENGTH_SHORT).show();
            }
            pd.dismiss();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }

    private String setEmailBodyText(StringBuilder body){
        body.append("Ф.И.О. заявителя: ").append(videoOwner.getProperty("lastName").toString() + " "
                + videoOwner.getProperty("firstName").toString() + " "
                + videoOwner.getProperty("middleName").toString() + "\n");
        body.append("email: ").append(videoOwner.getEmail()).append("\n");
        body.append("№ паспорта: ").append((videoOwner.getProperty("passportNo")).toString()).append("\n");
        body.append("тел: ").append((videoOwner.getProperty("phoneNumber")).toString()).append("\n\n");
        body.append("Данные по нарушению:").append("\n");
        body.append("Название: ").append(thisViolation.getName()).append("\n");
        body.append("Марка автомобиля: ").append(thisViolation.getCarMake()).append("\n");
        body.append("Модель автомобиля: ").append(thisViolation.getCarModel()).append("\n");
        body.append("Цвет: ").append(thisViolation.getColor()).append("\n");
        body.append("Номер: ").append(thisViolation.getCarNumber()).append("\n");
        body.append("Тип нарушения: ").append(thisViolation.getCategory().getType()).append("\n");
        body.append("Комментарии пользователя: ").append(thisViolation.getComment()).append("\n");
        if (!(thisViolation.getVideoUrl().equals(""))){
            body.append("Ссылка на видео: ").append(thisViolation.getVideoUrl()).append("\n");
        }
        if (!(thisViolation.getPhotoUrl().equals(""))){
            body.append("Ссылки на фотографии: ");
            for (String url: listOfPhotoUrls){
                body.append(url).append("\n");
            }
        }
        return body.toString();
    }

    @Override
    public void onBackPressed() {
        if (JCVideoPlayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        JCVideoPlayer.releaseAllVideos();
    }
}

