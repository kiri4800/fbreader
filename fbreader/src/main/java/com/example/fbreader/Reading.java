package com.example.fbreader;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fbreader.SampleExtension.TextWidgetExt;
import com.example.fbreader.databinding.LaoyutForReaderBinding;

import org.fbreader.book.Book;
import org.fbreader.book.BookLoader;
import org.fbreader.format.BookException;

public class Reading extends AppCompatActivity {
    String book_path;
    LaoyutForReaderBinding binding;
    TextWidgetExt text;
    TextView error;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = LaoyutForReaderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        text = binding.textWidget;
        error = binding.errorMessage;
        text.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
        book_path = getIntent().getStringExtra(MainActivity.FILE_EXTRA);
        try {
            text.setBook(BookLoader.fromFile(book_path,this,1L));
            Book book = text.controller().book;

            if(book != null){
                text.invalidate();
                text.post(new Runnable() {
                    @Override
                    public void run() {
                        text.gotoPage(0);
                        setTitle(book.getTitle());
                    }
                });
            }
            else{
                error.setText("Ошибка при открытии книги");
                error.setVisibility(View.VISIBLE);
            }
        } catch (BookException e) {
            throw new RuntimeException(e);
        }
    }
}
