package be.repsaj.littafin;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class EditBookActivity extends AppCompatActivity {
    private int bookUid;
    private TextView editTitle;
    private TextView editAuthor;
    private TextView editCategory;
    private Spinner sEditCategory;
    private Switch swCategory;
    private String bookCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);
        setTitle("Edit book");

        editTitle = (TextView)findViewById(R.id.editTitle);
        editAuthor = (TextView)findViewById(R.id.editAuthor);
        editCategory = (TextView)findViewById(R.id.editCategory);
        sEditCategory = (Spinner) findViewById(R.id.sEditCategory);
        swCategory = (Switch) findViewById(R.id.swCategory);

        Intent intent = getIntent();
        bookUid = intent.getIntExtra("bookId",0);
        String bookTitle = intent.getStringExtra("bookTitle");
        String bookAuthor = intent.getStringExtra("bookAuthor");
        bookCategory = intent.getStringExtra("bookCategory");

        editTitle.setText(bookTitle);
        editAuthor.setText(bookAuthor);
        editCategory.setText(bookCategory);

        editCategory.setVisibility(View.INVISIBLE);
        sEditCategory.setVisibility(View.VISIBLE);

        swCategory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    editCategory.setVisibility(View.VISIBLE);
                    sEditCategory.setVisibility(View.INVISIBLE);
                } else {
                    editCategory.setVisibility(View.INVISIBLE);
                    sEditCategory.setVisibility(View.VISIBLE);
                }
            }

        });


    ViewCategoriesTask viewCategoriesTask = new ViewCategoriesTask();
        viewCategoriesTask.execute((Void) null);


    }

    public void Update(View view){
        String bookTitle= editTitle.getText().toString();
        String bookAuthor= editAuthor.getText().toString();
        String bookCategory;

        if (swCategory.isChecked()){
            bookCategory = editCategory.getText().toString();
        } else {
            bookCategory = sEditCategory.getSelectedItem().toString();
        }


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


    public class ViewCategoriesTask extends AsyncTask<Void, Void, List<String>> {


        @Override
        protected List<String> doInBackground(Void... params) {
            Cursor cCategories;
            List<String> sCategories = new ArrayList<String>();

            cCategories = AppDatabase.getInstance(getApplicationContext()).bookDao()
                    .getAllCategories();
            while(cCategories.moveToNext()) {
                sCategories.add(cCategories.getString(0));
            }
            cCategories.close();
            return sCategories;
        }

        @Override
        protected void onPostExecute(final  List<String> categories) {
            updateSpinner(categories);
        }
    }

    public void updateSpinner(List<String> categories){
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        int spinnerPosition = dataAdapter.getPosition(bookCategory);
        sEditCategory.setAdapter(dataAdapter);
        sEditCategory.setSelection(spinnerPosition);
    }

}

