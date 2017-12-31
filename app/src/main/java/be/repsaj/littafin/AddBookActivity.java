package be.repsaj.littafin;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class AddBookActivity extends AppCompatActivity {
    String category;
    TextView addCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);
        setTitle("Add book");

        Intent currentIntent = getIntent();
        category = currentIntent.getStringExtra("bookCategory");
        addCategory=(TextView)findViewById(R.id.txtCategory);
        addCategory.setText(category);
    }
    public void Add(View view) {
        String title = ((EditText) findViewById(R.id.txtTitle)).getText().toString();
        String author = ((EditText) findViewById(R.id.txtAuthor)).getText().toString();
        String category = ((EditText) findViewById(R.id.txtCategory)).getText().toString();
        String cover ="";
        if (!"".equals(title)) {
            if("".equals(category)){category="Unknown";};
            Book newBook = new Book(title, author, category, cover);
            AddBookTask addBookTask = new AddBookTask(newBook);
            addBookTask.execute((Void) null);
        }
        else {
            Toast.makeText(getApplicationContext(),":( This book have no name. \n Fill in the title please.", Toast.LENGTH_SHORT).show();
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
                finish();
            } else {
                Toast.makeText(getApplicationContext(),"Could not insert book", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
