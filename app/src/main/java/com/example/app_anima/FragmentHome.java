package com.example.app_anima;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.dinuscxj.progressbar.CircleProgressBar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class FragmentHome extends Fragment {

    private ImageButton btn_menu,circle_add,circle_minus;
    private ImageButton btn_feedinput; // 사료 입력 버튼 선언
    private TextView tv_calorie;
    private ScrollView scrollView;
    private LinearLayout appbar, vp_layout;
    private TextView tv_menu,tv_water;

    private ArrayList<Drawable> mList;
    private ViewPager viewPager;
    private ADScrollAdapter adScrollAdapter;
    private boolean load = false;
    private Timer timer;
    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 3000; // time in milliseconds between successive task executions.
    private DrawerLayout drawer;
    private static final String DEFAULT_PATTERN = "%d%%";
    private CircleProgressBar circleProgressBar;
    private int water_count, nweek;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        ViewGroup viewGroup = (ViewGroup) inflater.inflate(R.layout.fragment_home, container, false);

        btn_feedinput = (ImageButton) viewGroup.findViewById(R.id.btn_feedinput); // 사료 입력 버튼
        tv_calorie = (TextView) viewGroup.findViewById(R.id.calory);
        btn_menu = (ImageButton) viewGroup.findViewById(R.id.btn_menu);
        circle_add = (ImageButton) viewGroup.findViewById(R.id.circle_add);
        circle_minus = (ImageButton) viewGroup.findViewById(R.id.circle_minus);
        scrollView = (ScrollView) viewGroup.findViewById(R.id.sv_main);
        appbar = (LinearLayout) viewGroup.findViewById(R.id.appbar);
        tv_menu = (TextView) viewGroup.findViewById(R.id.tv_menu);
        tv_water = (TextView) viewGroup.findViewById(R.id.tv_water);
        viewPager = (ViewPager) viewGroup.findViewById(R.id.viewPager);
        vp_layout = (LinearLayout) viewGroup.findViewById(R.id.vp_layout);
        circleProgressBar = (CircleProgressBar) viewGroup.findViewById(R.id.cpb_circlebar);

        //산책
        circleProgressBar.setProgress(0);

        //drawer
        drawer = (DrawerLayout) viewGroup.findViewById(R.id.drawer) ;
        btn_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.openDrawer(Gravity.LEFT) ;
                }
                if (drawer.isDrawerOpen(Gravity.LEFT)) {
                    drawer.closeDrawer(Gravity.LEFT) ;
                }
            }
        });

        //광고창
        mList = new ArrayList<Drawable>();
        mList.add(ResourcesCompat.getDrawable(getResources(),R.drawable.img_ad1,null));
        mList.add(ResourcesCompat.getDrawable(getResources(),R.drawable.img_ad2,null));
        mList.add(ResourcesCompat.getDrawable(getResources(),R.drawable.img_ad3,null));
        mList.add(ResourcesCompat.getDrawable(getResources(),R.drawable.img_ad4,null));
        adScrollAdapter = new ADScrollAdapter(getContext(),mList);
        viewPager.setAdapter(adScrollAdapter);

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            @Override
            public void run() {
                if(load) {
                    if (viewPager.getCurrentItem() == adScrollAdapter.getCount() - 1) {
                        viewPager.setCurrentItem(0, true);
                    } else {
                        viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
                    }
                }
                else{
                    load = true;
                    viewPager.setCurrentItem(0, true);
                }
            }

        };
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(Update);
            }

        }, DELAY_MS, PERIOD_MS);


        scrollView.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                int scrollY = scrollView.getScrollY(); // For ScrollView
                if(scrollY>=vp_layout.getBottom()){
                    appbar.setBackgroundColor(Color.parseColor("#E7D0C8"));
                    tv_menu.setVisibility(View.VISIBLE);
                    btn_menu.setColorFilter(Color.parseColor("#000000"));
                }
                else {
                    appbar.setBackgroundColor(Color.parseColor("#00E7D0C8"));
                    tv_menu.setVisibility(View.INVISIBLE);
                    btn_menu.setColorFilter(Color.parseColor("#FFFFFF"));
                }
            }
        });

        //사료
        btn_feedinput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final double curCal;
                if (tv_calorie.getText().toString().equals(null)) {
                    curCal = 0;
                } else {
                    curCal = Double.parseDouble(tv_calorie.getText().toString());
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

                View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_food, null, false);
                builder.setView(view);

                final RadioGroup mRadioGroup;
                final RadioButton radioButtonFood, radioButtonSnack;
                final EditText editTextInputFeed;
                final Button btnPlus100, btnPlus10, btnPlus1, btnMinus100, btnMinus10, btnMinus1;
                final EditText editTextInputCal;
                final Button btnApply, btnCancel;
                final boolean isFood;

                mRadioGroup = (RadioGroup) view.findViewById(R.id.radiocategory);
                radioButtonFood = (RadioButton) view.findViewById(R.id.radiofood);
                radioButtonSnack = (RadioButton) view.findViewById(R.id.radiosnack);
                editTextInputFeed = (EditText) view.findViewById(R.id.input_feed);
                btnPlus100 = (Button) view.findViewById(R.id.btn_plus100);
                btnPlus10 = (Button) view.findViewById(R.id.btn_plus10);
                btnPlus1 = (Button) view.findViewById(R.id.btn_plus1);
                btnMinus100 = (Button) view.findViewById(R.id.btn_minus100);
                btnMinus10 = (Button) view.findViewById(R.id.btn_minus10);
                btnMinus1 = (Button) view.findViewById(R.id.btn_minus1);
                editTextInputCal = (EditText) view.findViewById(R.id.input_calorie);
                btnApply = (Button) view.findViewById(R.id.btn_apply);
                btnCancel = (Button) view.findViewById(R.id.btn_cancel);

                RadioGroup.OnCheckedChangeListener radioGroupButtonChangeListener = new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {

                    }
                };
                mRadioGroup.setOnCheckedChangeListener(radioGroupButtonChangeListener);
                radioButtonFood.setChecked(true);

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();

                btnPlus100.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed += 100;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });
                btnPlus10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed += 10;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });
                btnPlus1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed += 1;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });
                btnMinus100.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed -= 100;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });
                btnMinus10.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed -= 10;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });
                btnMinus1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Double curInputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        curInputFeed -= 1;
                        editTextInputFeed.setText(String.valueOf(curInputFeed));
                    }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });

                btnApply.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int checkedRadio = mRadioGroup.getCheckedRadioButtonId();
                        double inputCalorie = Double.parseDouble(editTextInputCal.getText().toString());
                        double calculatedCalorie = inputCalorie / 100;
                        double inputFeed = Double.parseDouble(editTextInputFeed.getText().toString());
                        double finalFeed = inputFeed * calculatedCalorie;
                        tv_calorie.setText(String.format("%.2f", finalFeed + curCal));
                        alertDialog.dismiss();
                    }
                });
            }
        });

        //물물
       Calendar cal = Calendar.getInstance();
        nweek = cal.get(Calendar.DAY_OF_WEEK); //요일 구하기
        if(PreferenceManager.getInt(getContext(),"nweek")!=nweek) {
            PreferenceManager.setInt(getContext(), "water_count", 0);
            PreferenceManager.setInt(getContext(),"nweek",nweek);
        }
        water_count = PreferenceManager.getInt(getContext(),"water_count");
        tv_water.setText(Integer.toString(water_count));
        circle_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                water_count+=100;
                tv_water.setText(Integer.toString(water_count));
                PreferenceManager.setInt(getContext(), "water_count", water_count);
            }
        });
        circle_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(water_count!=0) {
                    water_count-=100;
                    tv_water.setText(Integer.toString(water_count));
                    PreferenceManager.setInt(getContext(), "water_count", water_count);
                }
                else{
                    tv_water.setText("0");
                    PreferenceManager.setInt(getContext(), "water_count", 0);
                }
            }
        });

        return viewGroup;
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
    }

    public CharSequence format(int progress, int max) {
        return String.format(DEFAULT_PATTERN, (int) ((float) progress / (float) max * 100));
    }

}
