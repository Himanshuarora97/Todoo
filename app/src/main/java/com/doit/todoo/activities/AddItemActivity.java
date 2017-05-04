package com.doit.todoo.activities;

/**
 * Created by himanshu on 4/5/17.
 */

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.doit.todoo.R;
import com.doit.todoo.model.DataSource;
import com.doit.todoo.model.Model;
import com.doit.todoo.utils.PerformEdit;
import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;
import com.jrummyapps.android.colorpicker.ColorPickerDialog;
import com.jrummyapps.android.colorpicker.ColorPickerDialogListener;


public class AddItemActivity extends AppCompatActivity implements ColorPickerDialogListener, View.OnClickListener {
    private static final String TAG = AddItemActivity.class.getName();
    EditText title, content;
    Toolbar toolbar;
    CoordinatorLayout layout;
    LinearLayout ll;
    ImageButton arrowButton, label;
    FloatingActionButton editFab;
    ImageButton bold, italic, underline, checkbox, bullet, numbullet, undo, redo;
    HorizontalScrollView hScroll;
    boolean loaded = true;
    private String colorCode = "#000000"; // default colorCode set to Black
    private static final int DIALOG_ID = 0;
    private boolean hasBold;
    private boolean hasItalic;
    private boolean hasUnderline;
    private boolean showOnly;
    private int isPassword = 0;
    private String password = "";
    Model model;
    private PerformEdit mPerformEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_item_activity);
        initViews();
//        Animation anim = new AnimationUtils().loadAnimation(AddItemActivity.this, R.anim.close);
//        hScroll.startAnimation(anim);

        if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().containsKey("OBJECT")) {
            model = getIntent().getExtras().getParcelable("OBJECT");
            showOnly = true;
        } else
            showOnly = false;

        if (showOnly) {
            // non Editable;

            Log.e(TAG, "onCreate: " + "Show only");
            editFab = (FloatingActionButton) findViewById(R.id.editFab);
            editFab.setVisibility(View.VISIBLE);
            editFab.setOnClickListener(this);
            hScroll.setVisibility(View.INVISIBLE);
            arrowButton.setVisibility(View.INVISIBLE);
            title.setFocusable(false);
            content.setFocusable(false);
            title.setText(model.getTitle());
            content.setText(model.getContent());
            setUpColors(Color.parseColor(model.getColor()));
        }
        // click Listeners
        bold.setOnClickListener(this);
        undo.setOnClickListener(this);
        redo.setOnClickListener(this);
        italic.setOnClickListener(this);
        underline.setOnClickListener(this);
        label.setOnClickListener(this);
        arrowButton.setOnClickListener(this);


        title.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        content.setTypeface(Typeface.SERIF);
        setUpToolbar();
        hideKeyboard();
    }

    private void setUpToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_clear_white_24dp);
    }

    private void initViews() {
        title = (EditText) findViewById(R.id.title);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        content = (EditText) findViewById(R.id.content);
        layout = (CoordinatorLayout) findViewById(R.id.layout);
        hScroll = (HorizontalScrollView) findViewById(R.id.hScroll);
        ll = (LinearLayout) findViewById(R.id.ll);
        undo = (ImageButton) findViewById(R.id.undo);
        redo = (ImageButton) findViewById(R.id.redo);
        arrowButton = (ImageButton) findViewById(R.id.arrow);
        label = (ImageButton) findViewById(R.id.label);
        bold = (ImageButton) findViewById(R.id.bold);
        italic = (ImageButton) findViewById(R.id.italic);
        underline = (ImageButton) findViewById(R.id.underline);
        checkbox = (ImageButton) findViewById(R.id.checkbox);
        numbullet = (ImageButton) findViewById(R.id.numbullet);
        bullet = (ImageButton) findViewById(R.id.bullet);
        mPerformEdit = new PerformEdit(content) {
            @Override
            protected void onTextChanged(Editable s) {
                super.onTextChanged(s);
            }
        };
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(new View(this).getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        hideKeyboard();
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.submit:
                Submit();
                return true;
            case R.id.lock:
                showCustomDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showCustomDialog() {
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(this);

        final EditText input = new EditText(AddItemActivity.this);
        input.setHint("Enter your password here");
        input.setInputType(InputType.TYPE_CLASS_TEXT |
                InputType.TYPE_TEXT_VARIATION_PASSWORD);
        int color = ContextCompat.getColor(AddItemActivity.this, R.color.black);
        input.getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        dialogBuilder
                .withTitle("Enter Password")                                  //.withTitle(null)  no title
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#11000000")                              //def
                .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                .withDialogColor("#FF2196F0")                               //def  | withDialogColor(int resid)
                .withIcon(getResources().getDrawable(R.drawable.ic_lock_white_24dp))
                .withDuration(500)                                          //def
                .withEffect(Effectstype.Sidefill)                                         //def Effectstype.Slidetop
                .withButton1Text("OK")                                      //def gone
                .withButton2Text("Cancel")                                  //def gone
                .isCancelableOnTouchOutside(true)                           //def    | isCancelable(true)
                .setCustomView(input, AddItemActivity.this)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isPassword = 1;
                        password = input.getText().toString();
                        Toast.makeText(v.getContext(), "Password set Successfully", Toast.LENGTH_SHORT).show();
                        dialogBuilder.dismiss();
                    }
                })
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(v.getContext(), "Not Successfully", Toast.LENGTH_SHORT).show();
                        dialogBuilder.dismiss();
                    }
                })
                .show();
    }

    public void Submit() {
        String message = title.getText().toString();
        String contentMessage = content.getText().toString();
        Intent intent = new Intent(this, MainActivity.class);
        DataSource dataSource = DataSource.getInstance(this);
        if (showOnly) {
            model.setTitle(message);
            model.setContent(contentMessage);
            model.setIsPasswordProtected(isPassword);
            model.setPassword(password);
            model.setColor(colorCode);
            dataSource.updateData(model);
        } else {
            model = dataSource.createModel(message, contentMessage, colorCode, isPassword, password);
            intent.putExtra("OBJECT", model);
        }
        startActivity(intent);
        finish();
    }

    @Override
    public void onColorSelected(int dialogId, @ColorInt int color) {
        colorCode = String.format("#%06X", (0xFFFFFF & color));
        setUpColors(color);
    }

    private void setUpColors(@ColorInt int color) {

        if (color == Color.BLACK || color == Color.WHITE)
            return;
        toolbar.setBackgroundColor(color);
        title.setBackgroundColor(color);
        content.setBackgroundColor(color);
        layout.setBackgroundColor(color);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(manipulateColor(color, 0.8f));
        }
    }

    @Override
    public void onDialogDismissed(int dialogId) {

    }

    public static int manipulateColor(int color, float factor) {
        int a = Color.alpha(color);
        int r = Math.round(Color.red(color) * factor);
        int g = Math.round(Color.green(color) * factor);
        int b = Math.round(Color.blue(color) * factor);
        return Color.argb(a,
                Math.min(r, 255),
                Math.min(g, 255),
                Math.min(b, 255));
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.undo:
                mPerformEdit.undo();
                break;
            case R.id.redo:
                mPerformEdit.redo();
                break;
            case R.id.bold:
                break;
            case R.id.italic:
                break;
            case R.id.underline:
                break;
            case R.id.arrow:
                Animation anim;
                if (loaded) {
                    arrowButton.setBackgroundColor(ContextCompat.getColor(this, R.color.black));
                    arrowButton.setImageDrawable(ContextCompat.getDrawable(AddItemActivity.this, R.drawable.ic_navigate_before_black_24dp));
                    anim = AnimationUtils.loadAnimation(AddItemActivity.this, R.anim.close);
                } else {
                    arrowButton.setBackground(ContextCompat.getDrawable(AddItemActivity.this, R.drawable.border));
                    arrowButton.setImageDrawable(ContextCompat.getDrawable(AddItemActivity.this, R.drawable.ic_navigate_next_black_24dp));
                    anim = AnimationUtils.loadAnimation(AddItemActivity.this, R.anim.load);
                }
                hScroll.startAnimation(anim);
                loaded = !loaded;
                break;
            case R.id.label:
                hideKeyboard();
                ColorPickerDialog.newBuilder()
                        .setDialogType(ColorPickerDialog.TYPE_PRESETS)
                        .setDialogId(DIALOG_ID)
                        .setColor(Color.LTGRAY)
                        .setShowAlphaSlider(true)
                        .show(AddItemActivity.this);
                break;
            case R.id.editFab:
                View myView = findViewById(R.id.hScroll);
                View view = findViewById(R.id.arrow);
                int cx = myView.getWidth() / 2;
                int cy = myView.getHeight() / 2;
                float finalRadius = (float) Math.hypot(cx, cy);
                int cx1 = view.getWidth();
                int cy1 = view.getHeight() / 2;

                float finalRadius1 = (float) Math.hypot(cx1, cy1);
                Animator anim1 =
                        null;
                Animator anim2 = null;

                myView.setVisibility(View.VISIBLE);
                view.setVisibility(View.VISIBLE);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    anim1 = ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, finalRadius);
                    anim2 = ViewAnimationUtils.createCircularReveal(view, cx1, cy1, 0, finalRadius1);
                    anim2.setDuration(800);
                    anim1.setDuration(700);
                    AnimatorSet set = new AnimatorSet();
                    set.playTogether(anim1, anim2);
                    set.start();
                }
                editFab.setVisibility(View.GONE);
                title.setFocusableInTouchMode(true);
                content.setFocusableInTouchMode(true);
                break;

        }
    }
}
