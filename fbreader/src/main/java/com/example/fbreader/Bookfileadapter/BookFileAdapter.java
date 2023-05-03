package com.example.fbreader.Bookfileadapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fbreader.R;
import com.example.fbreader.databinding.BookfileElementLayoutBinding;

import org.fbreader.book.Book;

import java.util.ArrayList;

public class BookFileAdapter extends RecyclerView.Adapter<BookFileAdapter.ViewHolder> {
    ArrayList<BookFile> arrayList;
    listener l;
    public BookFileAdapter(ArrayList<BookFile>arrayList,listener l){
        this.arrayList = arrayList;
        this.l = l;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.bookfile_element_layout,null,false );
        view.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                l.onbookclick((int)v.getTag());
            }
        });
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookFile f = arrayList.get(position);
        holder.bind(f);
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        final private TextView name;
        final private TextView path;
        BookfileElementLayoutBinding binding;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = BookfileElementLayoutBinding.bind(itemView);
            name = binding.name;
            path = binding.path;
        }
        public void bind(BookFile f){
            name.setText(f.getName());
            path.setText(f.getPath());
        }
    }
    public interface listener{
        void onbookclick(int position);
    }
}
