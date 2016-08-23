package com.example.justicecamera;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

public class PublicOffer extends AppCompatActivity {
    TextView textViewOffer;
    ProgressDialog offerD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_offer);
        new FindOfferTask().execute();
        textViewOffer = (TextView) findViewById(R.id.textViewOffer);
    }

    private class FindOfferTask extends AsyncTask<Void, Void, Offerta> {

        @Override
        protected void onPreExecute() {
            offerD = new ProgressDialog(PublicOffer.this);
            offerD.setTitle("Загрузка...");
            offerD.setMessage(getString(R.string.wait));
            offerD.show();
        }

        @Override
        protected Offerta doInBackground(Void... voids) {
            return Helper.findLastOffer();

        }

        protected void onPostExecute(Offerta result) {
         offerD.dismiss();
            textViewOffer.setText(result.getText());
        }
    }
}
