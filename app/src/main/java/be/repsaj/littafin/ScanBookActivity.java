package be.repsaj.littafin;


import android.content.Intent;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import be.repsaj.littafin.barcode.BarcodeCaptureActivity;


public class ScanBookActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int BARCODE_READER_REQUEST_CODE = 1;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent currentIntent = getIntent();
        category = currentIntent.getStringExtra("bookCategory");

        Intent nextIntent = new Intent(getApplicationContext(),BarcodeCaptureActivity.class);
        startActivityForResult(nextIntent, BARCODE_READER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BARCODE_READER_REQUEST_CODE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeCaptureActivity.BarcodeObject);
                    Point[] p = barcode.cornerPoints;
                    String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:"+barcode.displayValue;

                    new JsonTask().execute(url);
                }
            } else Log.e(LOG_TAG, String.format(getString(R.string.barcode_error_format),
                    CommonStatusCodes.getStatusCodeString(resultCode)));
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    private class JsonTask extends AsyncTask<String, String, String []> {
        protected String[] doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                Log.e("URL", params[0]);
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                String result[] = new String[3];

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);

                }
                try {
                    JSONObject obj = new JSONObject(buffer.toString());
                    JSONArray items = obj.getJSONArray("items");
                    JSONObject volumeInfo = items.getJSONObject(0).getJSONObject("volumeInfo");
                    JSONArray authors = (volumeInfo.getJSONArray("authors"));
                    JSONArray jsonArray = new JSONArray(authors.toString(0));
                    try {
                        JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
                        result[2] =imageLinks.getString("smallThumbnail");
                    }
                    catch(Exception e){
                        Log.e("SCAN:","No image in the API");
                    }

                    result[0]=volumeInfo.getString("title");
                    result[1]= jsonArray.getString(0);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return result;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String [] result) {
            super.onPostExecute(result);
            if(category==null){category="Unknow";}
            Book newBook = new Book(result[0], result[1], category,result[2]);
            CheckIfBookExist checkIfBookExist = new CheckIfBookExist(newBook);
            checkIfBookExist.execute((Void) null);
        }

        public class CheckIfBookExist extends AsyncTask<Void, Void, Integer> {

            private final Book newBook;


            CheckIfBookExist(Book newBook) {
                this.newBook = newBook;
            }

            @Override
            protected Integer doInBackground(Void... params) {
                Integer count;
               count= AppDatabase.getInstance(getApplicationContext()).bookDao()
                       .countByTitleAuthor(newBook.getTitle(),newBook.getAuthor());
                return count;
            }

            @Override
            protected void onPostExecute(final Integer count) {
                if (count == 0) {
                    AddBookTask addBookTask = new AddBookTask(newBook);
                    addBookTask.execute((Void) null);

                } else {
                    Toast.makeText(getApplicationContext(),"Book already inserted!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        public class AddBookTask extends AsyncTask<Void, Void, Boolean> {

            private final Book newBook;

            AddBookTask(Book newBook) {
                this.newBook = newBook;
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                AppDatabase.getInstance(getApplicationContext()).bookDao()
                        .insertAll(newBook);
                return true;
            }

            @Override
            protected void onPostExecute(final Boolean success) {
                if (success) {
                    Toast.makeText(getApplicationContext(),"Book successfully inserted", Toast.LENGTH_SHORT).show();
                    Intent nextIntent = new Intent(getApplicationContext(),BarcodeCaptureActivity.class);
                    startActivityForResult(nextIntent, BARCODE_READER_REQUEST_CODE);

                } else {
                    Toast.makeText(getApplicationContext(),"Could not insert book", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}