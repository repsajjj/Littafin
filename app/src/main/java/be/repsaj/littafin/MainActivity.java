package be.repsaj.littafin;

import android.content.Intent;
import android.graphics.Canvas;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity{
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_categories:
                    startActivity(new Intent(getApplicationContext(),CategorieActivity.class));
                    return true;

                case R.id.nav_book_scan:
                    startActivity(new Intent(getApplicationContext(),ScanBookActivity.class));
                    return true;

                case R.id.nav_book_manual:
                    startActivity(new Intent(getApplicationContext(),AddBookActivity.class));
                    return true;
            }
            return false;
        }
    };

    private BookDataAdapter mAdapter;
    private List<Book> books;
    private int deleteUid;
    SwipeController swipeController = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        makeBookDataAdapter();
    }

    @Override
    public void onResume(){
        super.onResume();
        makeBookDataAdapter();
    }



    private void makeBookDataAdapter(){
        GetBooksTask getBooksTask = new GetBooksTask();
        getBooksTask.execute((Void) null);

    }

    public class GetBooksTask extends AsyncTask<Void, Void, List<Book>> {

        @Override
        protected List<Book> doInBackground(Void... voids) {
            List<Book> books = AppDatabase.getInstance(getApplicationContext()).bookDao()
                    .getAll();
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
