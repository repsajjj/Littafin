package be.repsaj.littafin;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class EditBookActivity extends AppCompatActivity {
    private int bookUid;
    private TextView editTitle;
    private TextView editAuthor;
    private TextView editCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        editTitle = (TextView)findViewById(R.id.editTitle);
        editAuthor = (TextView)findViewById(R.id.editAuthor);
        editCategory = (TextView)findViewById(R.id.editCategory);

        Intent intent = getIntent();
        bookUid = intent.getIntExtra("bookId",0);
        String bookTitle = intent.getStringExtra("bookTitle");
        String bookAuthor = intent.getStringExtra("bookAuthor");
        String bookCategory = intent.getStringExtra("bookCategory");

        editTitle.setText(bookTitle);
        editAuthor.setText(bookAuthor);
        editCategory.setText(bookCategory);
    }

    public void Update(View view){
        String bookTitle= editTitle.getText().toString();
        String bookAuthor= editAuthor.getText().toString();
        String bookCategory= editCategory.getText().toString();

        UpdateBookTask updateBookTask = new UpdateBookTask(bookTitle,bookAuthor,bookCategory);
        updateBookTask.execute((Void) null);

    };

    public class UpdateBookTask extends AsyncTask<Void, Void, Boolean> {

        private String bookTitle="";
        private String bookAuthor="";
        private String bookCategory="";

        UpdateBookTask(String bookTitle, String bookAuthor,String bookCategory) {
            this.bookTitle = bookTitle;
            this.bookAuthor = bookAuthor;
            this.bookCategory = bookCategory;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            AppDatabase.getInstance(getApplicationContext()).bookDao()
                    .update(bookUid,bookTitle,bookAuthor,bookCategory);
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (success) {
                Toast.makeText(getApplicationContext(),"Book successfully updated", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(getApplicationContext(),"Could not update book", Toast.LENGTH_SHORT).show();
            }
        }
    }



}
