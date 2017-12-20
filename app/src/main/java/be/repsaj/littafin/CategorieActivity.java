package be.repsaj.littafin;

import android.database.Cursor;
import android.os.AsyncTask;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CategorieActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categorie);
    }

    @Override
    public void onResume(){
        super.onResume();
        ViewCategoriesTask viewCategoriesTask = new ViewCategoriesTask();
        viewCategoriesTask.execute((Void) null);

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
            printCategoriesList(categories);
        }
    }

    public void printCategoriesList( List<String> categories){
        ViewGroup linearLayout = findViewById(R.id.layCat);
        linearLayout.removeAllViews();
        for (String category : categories) {
            TextView catTV = new TextView(this);
            catTV.setText("Category: "+category);
            linearLayout.addView(catTV);
        }
    }


}
