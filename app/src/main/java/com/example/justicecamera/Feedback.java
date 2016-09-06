package com.example.justicecamera;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.backendless.exceptions.BackendlessException;

public class Feedback extends AppCompatActivity {
    ProgressDialog loading;
    ProgressDialog updating;
    EditText editTextFeedback;
    Button buttonSaveFeedback;
    String objectId;
    Violation currentViolation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        editTextFeedback = (EditText) findViewById(R.id.editTextFeedback);
        buttonSaveFeedback = (Button) findViewById(R.id.buttonSaveFeedback);
        buttonSaveFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonFeedbackClicked();
            }
        });

        Intent i = getIntent();
        objectId = i.getStringExtra(VideoInfo.THIS_OBJECT_ID);

        new FindViolationTask().execute(objectId);


    }

    private void onButtonFeedbackClicked() {
        currentViolation.setFeedback(editTextFeedback.getText().toString());
        new UpdateViolationTask().execute(currentViolation);
    }

    private class FindViolationTask extends AsyncTask<String, Void, Violation> {
        @Override
        protected void onPreExecute() {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            loading = new ProgressDialog(Feedback.this);
            loading.setTitle(getString(R.string.loading_info));
            loading.setMessage(getString(R.string.wait));
            loading.show();
        }

        @Override
        protected Violation doInBackground(String... strings) {
            return Helper.findViolationById(strings[0]);
        }

        protected void onPostExecute(Violation result) {
            currentViolation = result;
            editTextFeedback.setText(currentViolation.getFeedback());
            loading.dismiss();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
        }
    }

    private class UpdateViolationTask extends AsyncTask<Violation, Void, String> {
        @Override
        protected void onPreExecute() {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
            updating = new ProgressDialog(Feedback.this);
            updating.setTitle(getString(R.string.updating_user));
            updating.setMessage(getString(R.string.wait));
            updating.show();
        }

        @Override
        protected String doInBackground(Violation... violations) {
            try {
                Helper.updateViolation(violations[0]);
                return "updated";
            } catch (BackendlessException e){
                return "error";
            }
        }

        protected void onPostExecute(String result) {
            if (result.equals("updated")) {
                Helper.showToast("Данные успешно обновлены", Feedback.this);
            } else if (result.equals("error")){
                Helper.showToast("Error, something went wrong", Feedback.this);
            }
            updating.dismiss();
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
            finish();
        }
    }

}
