package be.repsaj.littafin;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;



class CategoryDataAdapter extends ArrayAdapter<String> {

    private Context context;
    private List<String> categories;

    public CategoryDataAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);

        this.context = context;
        this.categories = objects;
    }

    //called when rendering the list
    public View getView(int position, View convertView, ViewGroup parent) {
        String category = categories.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.category_row, null);

        TextView tv_category = (TextView) view.findViewById(R.id.row_category);
        tv_category.setText(category);

        return view;
    }
}
