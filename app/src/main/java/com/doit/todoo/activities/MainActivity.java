package com.doit.todoo.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.doit.todoo.R;
import com.doit.todoo.adapter.RecyclerAdapter;
import com.doit.todoo.model.DataSource;
import com.doit.todoo.model.Model;
import com.doit.todoo.utils.CustomRecyclerScrollViewListener;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class MainActivity extends AppCompatActivity implements RecyclerAdapter.OnItemClickListener {

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
        list = dataSource.getAllModels();
        view = findViewById(R.id.coordinator_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recycler);
        emptyView = findViewById(R.id.emptyView);
        setUpToolbar();
        fab = (FloatingActionButton) findViewById(R.id.fabBtn);
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.colorPrimary)));

        adapter = new RecyclerAdapter(this, list, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, AddItemActivity.class));
            }
        });


        if (list.isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
        customRecyclerScrollViewListener = new CustomRecyclerScrollViewListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void show() {
                showFab();
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void hide() {
                hideFab();
            }
        };
        recyclerView.addOnScrollListener(customRecyclerScrollViewListener);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showFab() {
        int cx = fab.getWidth() / 2;
        int cy = fab.getHeight() / 2;

        float finalRadius = (float) Math.hypot(cx, cy);

        try {
            Animator anim =
                    ViewAnimationUtils.createCircularReveal(fab, cx, cy, 0, finalRadius);
            anim.start();

        } catch (NoClassDefFoundError e) {

        }

        fab.setVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void hideFab() {

        int cx = fab.getWidth() / 2;
        int cy = fab.getHeight() / 2;

        float initialRadius = (float) Math.hypot(cx, cy);

        Animator anim =
                ViewAnimationUtils.createCircularReveal(fab, cx, cy, initialRadius, 0);

        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                fab.setVisibility(View.INVISIBLE);
            }
        });

        anim.start();
    }

    private void setUpToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Todo");
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onDeleteItem(final int delPos) {
        final Model model = list.get(delPos);
        list.remove(delPos);
        showFab();
        checkConstraint();
        adapter.notifyItemRemoved(delPos);
        Snackbar.make(view, "Item deleted ", Snackbar.LENGTH_LONG).setActionTextColor(getResources().getColor(R.color.red_A700)).setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                list.add(delPos, model);
                checkConstraint();
                adapter.notifyItemInserted(delPos);
                recyclerView.scrollToPosition(delPos);
            }
        }).addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int dismissType) {
                super.onDismissed(snackbar, dismissType);

                // delete if undo button not tap
                if (dismissType == DISMISS_EVENT_TIMEOUT || dismissType == DISMISS_EVENT_SWIPE
                        || dismissType == DISMISS_EVENT_CONSECUTIVE || dismissType == DISMISS_EVENT_MANUAL)
                    dataSource.deleteComment(model);
            }
        }).show();
    }


    @Override
    public void onItemClick(Model model) {
        boolean start = true;
        if (model.getIsPasswordProtected() == 1) {
            start = false;
            showCustomDialog(model);
        }
        if (start) {
            Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
            intent.putExtra("OBJECT", model);
            startActivity(intent);
        }
    }


    @Override
    public void onLongClick(int pos) {

    }

    private void checkConstraint() {

    }

    private void showCustomDialog(final Model model) {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);

        final EditText input = new EditText(this);
        final TextView text = new TextView(this);
        final LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(text);
        layout.addView(input);
        text.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        text.setText("You have Entered wrong password");
        text.setVisibility(View.GONE);
        input.setHint("Enter your password here");
        input.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        int color = ContextCompat.getColor(this, R.color.black);
        input.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(lp);
        dialogBuilder
                .withTitle("Enter Password")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#11000000")                              //def
                .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                .withDialogColor("#FFE74C3C")                               //def  | withDialogColor(int resid)
                .withIcon(getResources().getDrawable(R.drawable.ic_lock_white_24dp))
                .withDuration(100)                                          //def
                .withEffect(Effectstype.SlideBottom)                                         //def Effectstype.Slidetop
                .withButton1Text("OK")                                      //def gone
                .withButton2Text("Cancel")                                  //def gone
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .setCustomView(layout, this)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        password = input.getText().toString();
                        if (password.equals(model.getPassword())) {
                            Intent intent = new Intent(MainActivity.this, AddItemActivity.class);
                            intent.putExtra("OBJECT", model);
                            startActivity(intent);
                            dialogBuilder.dismiss();
                        } else {
                            text.setVisibility(View.VISIBLE);
                            Toast.makeText(MainActivity.this, "Error in Password", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialogBuilder.dismiss();
                    }
                })
                .show();

    }
}
