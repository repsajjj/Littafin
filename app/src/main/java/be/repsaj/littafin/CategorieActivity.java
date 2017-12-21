package be.repsaj.littafin;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

    public void printCategoriesList(final List<String> categories){
        ArrayAdapter<String> adapter = new CategoryDataAdapter (this, 0, categories);
        ListView listView = (ListView) findViewById(R.id.listViewCategory);
        listView.setAdapter(adapter);

        AdapterView.OnItemClickListener adapterViewListener = new AdapterView.OnItemClickListener() {

            //on click
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String category = categories.get(position);

                Log.e("Push",category);

                Intent intent = new Intent(CategorieActivity.this, ViewBooksActivity.class);
                intent.putExtra("sort", "Category");
                intent.putExtra("category", category);
                startActivity(intent);
            }
        };
        listView.setOnItemClickListener(adapterViewListener);
    }


}
