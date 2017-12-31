package be.repsaj.littafin;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;

import java.util.List;

import be.repsaj.littafin.barcode.BarcodeCaptureActivity;

public class ViewBooksActivity extends AppCompatActivity {
    private BookDataAdapter mAdapter;
    private List<Book> books;
    private String category;
    private Integer deleteUid;
    SwipeController swipeController = null;

    private BottomNavigationView.OnNavigationItemSelectedListener OnNavigationCategoryItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_book_scan:
                    Intent scanBook = new Intent(getApplicationContext(),BarcodeCaptureActivity.class);
                    scanBook.putExtra("bookCategory", category);
                    startActivity(scanBook);
                    return true;

                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                    return true;

                case R.id.nav_book_manual:
                    Intent manualBook = new Intent(getApplicationContext(),AddBookActivity.class);
                    manualBook.putExtra("bookCategory", category);
                    startActivity(manualBook);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_books);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.categoryNavigation);
        navigation.setOnNavigationItemSelectedListener(OnNavigationCategoryItemSelectedListener);


    }

    @Override
    public void onResume(){
        super.onResume();
        Intent intent = getIntent();

        category = intent.getStringExtra("category");
        setTitle(category);
        makeBookDataAdapter();
    }

    private void makeBookDataAdapter(){
        GetBooksByCategoryTask getBooksByCategoryTask = new GetBooksByCategoryTask();
        getBooksByCategoryTask.execute((Void) null);
    }



    public class GetBooksByCategoryTask extends AsyncTask<Void, Void, List<Book>> {

        @Override
        protected List<Book> doInBackground(Void... voids) {
            List<Book> books = AppDatabase.getInstance(getApplicationContext()).bookDao()
                    .findByCategory(category);
            return books;
        }

        @Override
        protected void onPostExecute(final List<Book> tBooks) {
            books=tBooks;
            if (books != null) {
                mAdapter = new BookDataAdapter(books);
                setupRecyclerView();
            }
        }
    }

    public class DeleteBookByIdTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            AppDatabase.getInstance(getApplicationContext()).bookDao()
                    .deleteByUid(deleteUid);
            return true;
        }
    }



    private void setupRecyclerView() {
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);

        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                deleteUid=mAdapter.getUid(position);

                mAdapter.books.remove(position);

                DeleteBookByIdTask deleteBookByIdTask = new DeleteBookByIdTask();
                deleteBookByIdTask.execute((Void) null);

                mAdapter.notifyItemRemoved(position);
                mAdapter.notifyItemRangeChanged(position, mAdapter.getItemCount());
            }

            @Override
            public void onLeftClicked(int position) {
                Book tBook=mAdapter.getBook(position);
                Intent intent = new Intent(getApplicationContext(),EditBookActivity.class);
                intent.putExtra("bookId", tBook.getUid());
                intent.putExtra("bookTitle", tBook.getTitle());
                intent.putExtra("bookAuthor", tBook.getAuthor());
                intent.putExtra("bookCategory", tBook.getCategory());
                startActivity(intent);
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }
}
