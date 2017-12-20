package be.repsaj.littafin;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class AddBookActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
    }
    public void Add(View view) {
        String title = ((EditText) findViewById(R.id.txtTitle)).getText().toString();
        String author = ((EditText) findViewById(R.id.txtAuthor)).getText().toString();
        String category = ((EditText) findViewById(R.id.txtCategory)).getText().toString();

        Book newBook = new Book(title, author, category);
        AddBookTask addBookTask = new AddBookTask(newBook);
        addBookTask.execute((Void) null);
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
                finish();
            } else {
                Toast.makeText(getApplicationContext(),"Could not insert book", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void Reset (View view){
        ResetTask resetTask = new ResetTask();
        resetTask.execute((Void) null);
    }

    public class ResetTask extends AsyncTask<Void, Void, Boolean> {
        @Override
        protected Boolean doInBackground(Void... params) {
            AppDatabase.getInstance(getApplicationContext()).bookDao()
                    .reset();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Toast.makeText(getApplicationContext(),"Clean dB", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
