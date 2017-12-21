package be.repsaj.littafin;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import java.util.List;

public class ViewBooksActivity extends AppCompatActivity {

    private BookDataAdapter mAdapter;
    private List<Book> books;
    private String category;
    private Integer deleteUid;
    SwipeController swipeController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_books);


    }

    @Override
    public void onResume(){
        super.onResume();
        Intent intent = getIntent();

        category = intent.getStringExtra("category");
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
                int uId=mAdapter.getUid(position);
                deleteUid= uId;
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
