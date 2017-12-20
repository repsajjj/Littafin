package be.repsaj.littafin;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

class BookDataAdapter extends RecyclerView.Adapter<BookDataAdapter.BookViewHolder> {
    public List<Book> books;

    public class BookViewHolder extends RecyclerView.ViewHolder {
        private TextView title, author;
        private ImageView cover;

        public BookViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            author = view.findViewById(R.id.author);
            cover = view.findViewById(R.id.image);
        }
    }

    public BookDataAdapter(List<Book> books) {
        this.books = books;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.book_row, parent, false);

        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.title.setText(book.getTitle());
        holder.author.setText(book.getAuthor());
        String url=book.getCover();
        try {
            Picasso.with(holder.itemView.getContext()).load(url).into(holder.cover);
        } catch (Exception e){
            Log.e("View","Could not load image");
        }
    }

    @Override
    public int getItemCount() {
        return books.size();
    }


    public int getUid(int position){
        Book book = books.get(position);
        return book.getUid();
    }
}