package com.doit.todoo;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.doit.todoo.adapter.RecyclerAdapter;
import com.doit.todoo.model.DataSource;
import com.doit.todoo.model.Model;
import com.doit.todoo.utils.CustomRecyclerScrollViewListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    Toolbar toolbar;
    private DataSource dataSource;
    View emptyView;
    private CustomRecyclerScrollViewListener customRecyclerScrollViewListener;
    RecyclerAdapter adapter;
    FloatingActionButton fab;
    List<Model> list = new ArrayList<>();
    RecyclerView recyclerView;
    View view;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataSource = DataSource.getInstance(this);
        dataSource.open();
        ArrayList<Model> models = new ArrayList<>();
        for (int i = 1; i < 10; i++) {
            Random random = new Random();
            int rand = random.nextInt(1);
            dataSource.createModel("title " + i, "Content string " + i, dataSource.getRandomMaterialColor("400"), rand, "password");
        }
        models = dataSource.getAllModels();
        View view = findViewById(R.id.emptyView);
        view.setVisibility(View.GONE);
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        RecyclerAdapter adapter = new RecyclerAdapter(this, models);
        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

    }
}
