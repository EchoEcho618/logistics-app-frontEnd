package cn.itcast.baoxiu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.itcast.baoxiu.entity.Repair;
import cn.itcast.baoxiu.entity.Support;
import cn.itcast.baoxiu.entity.User;
import cn.itcast.baoxiu.util.ActivityCollector;
import cn.itcast.baoxiu.util.MyRequest;

public class Manage extends AppCompatActivity {
    //activity间传输
    private Bundle bundle;
    private String type;
    //全局变量
    private String globalUrl;
    private int num;
    //控件
    private LinearLayout searchBar;
    private LinearLayout spinnerLayout;
    private TableLayout tableLayout;
    private TextView count;
    private Button add;
    private Spinner spinner1;
    private Spinner spinner2;
    //线程间传输
    final Handler handler = new Handler();
    //下拉监听
    private final AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            Thread t1 = new Thread(() -> {
                Runnable r1 = () -> tableLayout.removeAllViews();
                handler.post(r1);
            });
            Thread t2 = new Thread(Manage.this::showAll);
            t1.start();
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            t2.start();
            try {
                t2.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            Toast.makeText(Manage.this, "请选择一项！", Toast.LENGTH_SHORT).show();
        }
    };
    //回调
    private final ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == 1) {
                spinnerLayout.removeView(findViewById(R.id.spinner_1));
                spinnerLayout.removeView(findViewById(R.id.spinner_2));
                tableLayout.removeAllViews();
                initView();
                initEvent();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage);
        ActivityCollector.addActivity(this);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        //初始化控件
        initView();
        //初始化事件
        initEvent();
    }

    //返回按钮
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            ActivityCollector.removeActivity(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        searchBar = this.findViewById(R.id.search_bar);
        spinnerLayout = this.findViewById(R.id.spinner_layout);
        tableLayout = this.findViewById(R.id.tableLayout);
        count = this.findViewById(R.id.count_repairs);
        add = this.findViewById(R.id.add_info);
    }

    private void initEvent() {
        bundle = getIntent().getExtras();
        type = bundle.getString("type");
        globalUrl = "http://" + getResources().getString(R.string.DNS) + ":8080//" + type + "/selectAll";
        add.setOnClickListener(v -> {
            Intent intent = new Intent(this, Add.class);
            intent.putExtras(bundle);
            intentActivityResultLauncher.launch(intent);
        });
        new Thread(this::showSearchBar).start();
        new Thread(this::showSpinner).start();
    }

    //生成搜索框
    private void showSearchBar() {
        EditText search = addSearch(R.id.edit_text_9);
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                new Thread(() -> {
                    Runnable r1 = () -> tableLayout.removeAllViews();
                    handler.post(r1);
                    String text = search.getText().toString();
                    if (text.equals("")) {
                        globalUrl = "http://" + getResources().getString(R.string.DNS) + ":8080//" + type + "/selectAll";
                        showAll();
                    } else {
                        switch (type) {
                            case "user":
                                globalUrl = "http://" + getResources().getString(R.string.DNS) + ":8080//user/search?text=" + text;
                                List<User> users = MyRequest.getUsers(globalUrl);
                                if (users != null) {
                                    showTitle(4, new String[]{"ID", "姓名", "性别", "身份"});
                                    showUsers(users);
                                    addBlank();
                                }
                                break;
                            case "support":
                                globalUrl = "http://" + getResources().getString(R.string.DNS) + ":8080//support/search?text=" + text;
                                List<Support> supports = MyRequest.getSupports(globalUrl);
                                if (supports != null) {
                                    showTitle(4, new String[]{"ID", "姓名", "性别", "平均星级"});
                                    showSupports(supports);
                                    addBlank();
                                }
                                break;
                            case "repair":
                                globalUrl = "http://" + getResources().getString(R.string.DNS) + ":8080//repair/search?text=" + text;
                                List<Repair> repairs = MyRequest.getRepairs(globalUrl);
                                if (repairs != null) {
                                    showTitle(3, new String[]{"ID", "报修人", "报修内容"});
                                    showRepairs(repairs);
                                    addBlank();
                                }
                                break;
                        }
                    }
                }).start();
            }
        });
    }

    //生成下拉筛选框
    private void showSpinner() {
        switch (type) {
            case "user":
                spinner1 = addSpinner(R.id.spinner_1, R.array.sex_p);
                spinner2 = addSpinner(R.id.spinner_2, R.array.type_p);
                spinner1.setOnItemSelectedListener(onItemSelectedListener);
                spinner2.setOnItemSelectedListener(onItemSelectedListener);
                break;
            case "support":
                spinner1 = addSpinner(R.id.spinner_1, R.array.sex_p);
                spinner1.setOnItemSelectedListener(onItemSelectedListener);
                break;
            case "repair":
                List<CharSequence> userNames = new ArrayList<>();
                userNames.add("报修人");
                String url = "http://" + getResources().getString(R.string.DNS) + ":8080//user/allName";
                List<User> users = MyRequest.getUsers(url);
                for (int i = 0; i < Objects.requireNonNull(users).size(); i++) {
                    userNames.add(users.get(i).getuId() + "." + users.get(i).getuName());
                }
                spinner1 = addSpinner(R.id.spinner_1, userNames);
                List<CharSequence> supportNames = new ArrayList<>();
                supportNames.add("维修人");
                url = "http://" + getResources().getString(R.string.DNS) + ":8080//support/allName";
                List<Support> supports = MyRequest.getSupports(url);
                for (int i = 0; i < Objects.requireNonNull(supports).size(); i++) {
                    supportNames.add(supports.get(i).getsId() + "." + supports.get(i).getsName());
                }
                spinner2 = addSpinner(R.id.spinner_2, supportNames);
                spinner1.setOnItemSelectedListener(onItemSelectedListener);
                spinner2.setOnItemSelectedListener(onItemSelectedListener);
                break;
        }

    }

    //生成主体界面
    private void showAll() {
        switch (type) {
            case "user":
                List<User> users = MyRequest.getUsers(globalUrl);
                showTitle(4, new String[]{"ID", "姓名", "性别", "身份"});
                showUsers(users);
                addBlank();
                break;
            case "support":
                List<Support> supports = MyRequest.getSupports(globalUrl);
                showTitle(4, new String[]{"ID", "姓名", "性别", "平均星级"});
                showSupports(supports);
                addBlank();
                break;
            case "repair":
                List<Repair> repairs = MyRequest.getRepairs(globalUrl);
                showTitle(3, new String[]{"ID", "报修人", "报修内容"});
                showRepairs(repairs);
                addBlank();
                break;
            default:
                Looper.prepare();
                Toast.makeText(this, "身份识别错误！请重试！", Toast.LENGTH_SHORT).show();
                Looper.loop();
                break;
        }
    }

    //展示列名
    @SuppressLint("UseCompatLoadingForDrawables")
    private void showTitle(int num, String[] strings) {
        TableRow row = new TableRow(this);
        row.setDividerDrawable(getDrawable(R.drawable.divider));
        row.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        for (int i = 0; i < num; i++) {
            TextView text = makeText(strings[i]);
            row.addView(text);
        }
        handler.post(() -> tableLayout.addView(row));
    }

    //生成用户表
    @SuppressLint("SetTextI18n")
    private void showUsers(List<User> users) {
        num = 0;
        for (int i = 0; i < Objects.requireNonNull(users).size(); i++) {
            String id = users.get(i).getuId().toString();
            String name = users.get(i).getuName();
            int sexCode = users.get(i).getuSex();
            String sex;
            if (sexCode == 0) sex = "男";
            else sex = "女";
            int typeCode = users.get(i).getuType();
            String tp;
            if (typeCode == 0) tp = "教师";
            else tp = "学生";
            if (spinner1.getSelectedItemPosition() == 0 || sexCode == (spinner1.getSelectedItemPosition() - 1)) {
                if (spinner2.getSelectedItemPosition() == 0 || typeCode == (spinner2.getSelectedItemPosition() - 1)) {
                    showUser(id, name, sex, tp);
                    num++;
                }
            }
        }
        handler.post(() -> count.setText(String.valueOf(num)));
    }

    //展示一个用户信息
    @SuppressLint("UseCompatLoadingForDrawables")
    private void showUser(String id, String name, String sex, String tp) {
        TextView text1 = makeText(id);
        TextView text2 = makeText(name);
        TextView text3 = makeText(sex);
        TextView text4 = makeText(tp);
        TableRow row = new TableRow(this);
        row.setDividerDrawable(getDrawable(R.drawable.divider));
        row.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        row.addView(text1);
        row.addView(text2);
        row.addView(text3);
        row.addView(text4);
        row.setClickable(true);
        row.setOnClickListener(v -> {
            Intent intent = new Intent(this, Info.class);
            bundle.putString("id", id);
            bundle.putString("type", "user");
            bundle.putString("state", "未处理");
            intent.putExtras(bundle);
            intentActivityResultLauncher.launch(intent);
        });
        handler.post(() -> tableLayout.addView(row));
    }

    //生成后勤人员表
    @SuppressLint("SetTextI18n")
    private void showSupports(List<Support> supports) {
        num = 0;
        for (int i = 0; i < Objects.requireNonNull(supports).size(); i++) {
            String id = supports.get(i).getsId().toString();
            String name = supports.get(i).getsName();
            int sexCode = supports.get(i).getsSex();
            String sex;
            if (sexCode == 0) sex = "男";
            else sex = "女";
            if (spinner1.getSelectedItemPosition() == 0 || sexCode == (spinner1.getSelectedItemPosition() - 1)) {
                showSupport(id, name, sex);
                num++;
            }
        }
        handler.post(() -> count.setText(String.valueOf(num)));
    }

    //展示一个后勤人员信息
    @SuppressLint("UseCompatLoadingForDrawables")
    private void showSupport(String id, String name, String sex) {
        String url = "http://" + getResources().getString(R.string.DNS) + ":8080//repair/avgStar?s_id=" + id;
        String res = MyRequest.getString(url);
        TextView text1 = makeText(id);
        TextView text2 = makeText(name);
        TextView text3 = makeText(sex);
        TextView text4 = makeText(res);
        TableRow row = new TableRow(this);
        row.setDividerDrawable(getDrawable(R.drawable.divider));
        row.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        row.addView(text1);
        row.addView(text2);
        row.addView(text3);
        row.addView(text4);
        row.setClickable(true);
        row.setOnClickListener(v -> {
            Intent intent = new Intent(this, Info.class);
            bundle.putString("id", id);
            bundle.putString("type", "support");
            bundle.putString("state", "未处理");
            intent.putExtras(bundle);
            intentActivityResultLauncher.launch(intent);
        });
        handler.post(() -> tableLayout.addView(row));
    }

    //生成报修单表
    @SuppressLint("SetTextI18n")
    private void showRepairs(List<Repair> repairs) {
        num = 0;
        for (int i = 0; i < Objects.requireNonNull(repairs).size(); i++) {
            String line = spinner1.getSelectedItem().toString();
            Pattern r = Pattern.compile("\\d+");
            Matcher m = r.matcher(line);
            String spinnerUid = null;
            if (m.find()) {
                spinnerUid = m.group();
            }
            line = spinner2.getSelectedItem().toString();
            m = r.matcher(line);
            String spinnerSid = null;
            if (m.find()) {
                spinnerSid = m.group();
            }
            String id = repairs.get(i).getrId().toString();
            String uid = repairs.get(i).getuId().toString();
            String sid = null;
            if (repairs.get(i).getsId() != null) {
                sid = repairs.get(i).getsId().toString();
            }
            if (spinner1.getSelectedItemPosition() == 0 || uid.equals(spinnerUid)) {
                if (spinner2.getSelectedItemPosition() == 0 || Objects.equals(spinnerSid, sid)) {
                    String url = "http://" + getResources().getString(R.string.DNS) + ":8080//user/name?u_id=" + uid;
                    String name = MyRequest.getString(url);
                    String content = repairs.get(i).getrContent();
                    showRepair(id, name, content);
                    num++;
                }
            }
        }
        handler.post(() -> count.setText(String.valueOf(num)));
    }

    //展示一个报修单的信息
    @SuppressLint("UseCompatLoadingForDrawables")
    private void showRepair(String id, String name, String content) {
        TextView text1 = makeText(id);
        TextView text2 = makeText(name);
        TextView text3 = makeText(content);
        TableRow row = new TableRow(this);
        row.setDividerDrawable(getDrawable(R.drawable.divider));
        row.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
        row.addView(text1);
        row.addView(text2);
        row.addView(text3);
        row.setClickable(true);
        row.setOnClickListener(v -> {
            Intent intent = new Intent(this, Info.class);
            bundle.putString("id", id);
            bundle.putString("type", "repair");
            bundle.putString("state", "未处理");
            intent.putExtras(bundle);
            intentActivityResultLauncher.launch(intent);
        });
        handler.post(() -> tableLayout.addView(row));
    }

    //生成一个文本
    private TextView makeText(String value) {
        TextView text = new TextView(this);
        text.setText(value);
        text.setTextSize(24);
        text.setPadding(10, 10, 10, 10);
        return text;
    }

    //底部添加空白方便操作
    private void addBlank() {
        TableRow blank = new TableRow(this);
        blank.setMinimumHeight(300);
        handler.post(() -> tableLayout.addView(blank));
    }

    //新建一个spinner，内容在xml中
    private Spinner addSpinner(int spinnerId, int resource) {
        Spinner spinner = new Spinner(this);
        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, getResources().getStringArray(resource));
        spinner.setAdapter(arrayAdapter);
        spinner.setId(spinnerId);
        spinner.setPadding(10, 0, 0, 0);
        handler.post(() -> spinnerLayout.addView(spinner));
        return spinner;
    }

    //新建一个spinner，内容动态生成
    private Spinner addSpinner(int spinnerId, List<CharSequence> resource) {
        Spinner spinner = new Spinner(this);
        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, resource);
        spinner.setAdapter(arrayAdapter);
        spinner.setId(spinnerId);
        spinner.setPadding(10, 0, 0, 0);
        handler.post(() -> spinnerLayout.addView(spinner));
        return spinner;
    }

    //新建一个搜索框
    private EditText addSearch(int editTextId) {
        LinearLayout linear = new LinearLayout(this);
        TextView textView = new TextView(this);
        textView.setText("搜索：");
        textView.setTextSize(24);
        EditText editText = new EditText(this);
        editText.setTextSize(24);
        editText.setId(editTextId);
        editText.setEms(20);
        linear.addView(textView);
        linear.addView(editText);
        linear.setPadding(10, 10, 10, 10);
        handler.post(() -> searchBar.addView(linear));
        return editText;
    }
}
