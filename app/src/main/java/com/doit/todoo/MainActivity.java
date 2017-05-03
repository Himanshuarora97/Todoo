package com.doit.todoo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.doit.todoo.adapter.RecyclerAdapter;
import com.doit.todoo.model.Model;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ArrayList<Model> models = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            Random random = new Random();
            int rand = random.nextInt(1);
            models.add(new Model(i, "title " + i, "Content string " + i, "#000000", rand, "password"));
        }
        View view  = findViewById(R.id.emptyView);
        view.setVisibility(View.GONE);
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        RecyclerAdapter adapter = new RecyclerAdapter(this, models);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

    }
}
