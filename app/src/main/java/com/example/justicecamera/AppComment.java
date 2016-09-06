package com.example.justicecamera;

import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ThemedSpinnerAdapter;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.exceptions.BackendlessException;

public class AppComment extends AppCompatActivity {
    Button buttonLeaveFeedback;
    EditText editTextLeaveFeedback;
    Report report;
    BackendlessUser user;
    ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_comment);
        init();

        buttonLeaveFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonLeaveFeedbackClicked();
            }
        });
    }

    private void init() {
        buttonLeaveFeedback = (Button) findViewById(R.id.buttonLeaveFeedback);
        editTextLeaveFeedback = (EditText) findViewById(R.id.editTextLeaveFeedback);
        report = new Report();
        user = Backendless.UserService.CurrentUser();
    }

    private void onButtonLeaveFeedbackClicked() {
        String feedbackText = editTextLeaveFeedback.getText().toString();
        report.setText(feedbackText);
        report.setEmail(user.getEmail());
        report.setFIO(user.getProperty("lastName").toString() + " " +
                user.getProperty("firstName").toString() + " " +
                user.getProperty("middleName").toString());

        new SaveReportTask().execute(report);


    }

    private class SaveReportTask extends AsyncTask<Report, Void, String> {

        protected void onPreExecute() {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            pd = new ProgressDialog(AppComment.this);
            pd.setMessage(getString(R.string.wait));
            pd.show();
        }

        @Override
        protected String doInBackground(Report... reports) {
            try {
                Helper.saveReport(reports[0]);
                return "saved";
            } catch (BackendlessException e) {
                return "error";
            }
        }

        @Override
        protected void onPostExecute(String result) {
            if (result.equals("saved")) {
                Toast.makeText(getApplicationContext(), "Отзыв успешно отправлен", Toast.LENGTH_LONG).show();
            } else if (result.equals("error")) {
                Toast.makeText(getApplicationContext(), getString(R.string.server_error), Toast.LENGTH_LONG).show();
            }

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            pd.dismiss();
            finish();
        }
    }
}
