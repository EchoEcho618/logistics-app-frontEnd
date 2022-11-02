package cn.itcast.baoxiu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Objects;

import cn.itcast.baoxiu.entity.Admin;
import cn.itcast.baoxiu.entity.Repair;
import cn.itcast.baoxiu.entity.Support;
import cn.itcast.baoxiu.entity.User;
import cn.itcast.baoxiu.util.ActivityCollector;
import cn.itcast.baoxiu.util.AddControl;
import cn.itcast.baoxiu.util.MyRequest;

public class Main extends AppCompatActivity {
    //activity间传输
    private Bundle bundle;
    private String id;
    private String type;
    //控件
    private TextView textView;
    private TextView textView10;
    private TextView count;
    private LinearLayout linearLayout;
    private LinearLayout state_layout;
    private LinearLayout buttons;
    private Button left;
    private Button right;
    private Button mid;
    private Spinner spinner;
    //线程间传输
    final Handler handler = new Handler();
    //回调
    private final ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == 1) {
                linearLayout.removeAllViews();
                initView();
                initEvent();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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

    //菜单栏
    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                //ActivityCollector.removeActivity(this);
                return true;
            case R.id.exit:
                ActivityCollector.finishAllActivity();
                break;
            case R.id.edit_password:
                Intent intent1 = new Intent(this, EditPWD.class);
                intent1.putExtras(bundle);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void initView() {
        textView = this.findViewById(R.id.textView);
        textView10 = this.findViewById(R.id.textView10);
        count = this.findViewById(R.id.count_repairs);
        linearLayout = this.findViewById(R.id.linearLayout);
        state_layout = this.findViewById(R.id.state_layout);
        buttons = this.findViewById(R.id.buttons);
        left = this.findViewById(R.id.left_btn);
        right = this.findViewById(R.id.right_btn);
        mid = this.findViewById(R.id.mid_btn);
        spinner = this.findViewById(R.id.state);
    }

    public void initEvent() {
        bundle = getIntent().getExtras();
        id = bundle.getString("id");
        type = bundle.getString("type");
        //authority = bundle.getString("authority");
        switch (type) {
            case "user": {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, this.getResources().getStringArray(R.array.state_u));
                spinner.setAdapter(adapter);
                mid.setOnClickListener(v -> {
                    bundle.putString("id", id);
                    bundle.putString("type", "repair");
                    Intent intent = new Intent(this, Add.class);
                    intent.putExtras(bundle);
                    intentActivityResultLauncher.launch(intent);
                });
                break;
            }
            case "support": {
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_spinner_item, this.getResources().getStringArray(R.array.state_s));
                spinner.setAdapter(adapter);
                mid.setOnClickListener(v -> {
                    Intent intent = new Intent(this, New.class);
                    bundle.putString("id", id);
                    intent.putExtras(bundle);
                    intentActivityResultLauncher.launch(intent);
                });
                break;
            }
            case "admin":
                textView10.setText("功能列表");
                state_layout.removeAllViews();
                buttons.removeView(mid);
                showRepairs();
                break;
            default:
                Looper.prepare();
                Toast.makeText(this, "身份识别错误！请重试！", Toast.LENGTH_SHORT).show();
                Looper.loop();
        }
        new Thread(this::setName).start();
        left.setOnClickListener(v -> {
            finish();
            ActivityCollector.removeActivity(this);
            Intent intent = new Intent(this, Main.class);
            bundle.putString("id", id);
            bundle.putString("type", type);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
        right.setOnClickListener(v -> {
            Intent intent = new Intent(this, Mine.class);
            bundle.putString("id", id);
            bundle.putString("type", type);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
        //监听下拉框选项
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                linearLayout.removeAllViews();
                new Thread(Main.this::showRepairs).start();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(Main.this, "请选择一项！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    //动态设置欢迎语句
    @SuppressLint("SetTextI18n")
    public void setName() {
        String name;
        String typeText;
        switch (type) {
            case "user": {
                String url = "http://" + getResources().getString(R.string.DNS) + ":8080//user/info?u_id=" + id;
                User user = MyRequest.getUser(url);
                assert user != null;
                name = user.getuName();
                Integer uType = user.getuType();
                if (uType == 0) typeText = "老师";
                else typeText = "同学";
                break;
            }
            case "support": {
                String url = "http://" + getResources().getString(R.string.DNS) + ":8080//support/info?s_id=" + id;
                Support support = MyRequest.getSupport(url);
                assert support != null;
                name = support.getsName();
                Integer sSex = support.getsSex();
                if (sSex == 0) typeText = "先生";
                else typeText = "女士";
                break;
            }
            case "admin": {
                String url = "http://" + getResources().getString(R.string.DNS) + ":8080//admin/info?a_id=" + id;
                Admin admin = MyRequest.getAdmin(url);
                assert admin != null;
                name = admin.getaName();
                typeText = "管理员";
                break;
            }
            default:
                name = "";
                typeText = "";
                Looper.prepare();
                Toast.makeText(this, "身份识别错误！请重试！", Toast.LENGTH_SHORT).show();
                Looper.loop();
        }
        handler.post(() -> textView.setText("你好，" + name + typeText + "!"));
    }

    //动态生成相应报修单
    public void showRepairs() {
        switch (type) {
            case "user": {
                /*
                 * spinner.getSelectedItemPosition()
                 * 0：全部
                 * 1：未处理
                 * 2：进行中
                 * 3：已完成
                 * */
                String url = "http://" + getResources().getString(R.string.DNS) + ":8080//repair/selectByUid?u_id=" + id;
                List<Repair> repairs = MyRequest.getRepairs(url);
                if (spinner.getSelectedItemPosition() == 0) {
                    handler.post(() -> count.setText(String.valueOf(repairs != null ? repairs.size() : 0)));
                    for (int i = Objects.requireNonNull(repairs).size() - 1; i >= 0; i--) {
                        String state;
                        if (repairs.get(i).getStar() != null)
                            state = "已完成";
                        else if (repairs.get(i).getEnd() != null)
                            state = "待评价";
                        else if (repairs.get(i).getsId() != null)
                            state = "进行中";
                        else state = "未处理";
                        String content = repairs.get(i).getrContent();
                        String rid = repairs.get(i).getrId().toString();
                        AddControl.addRepair(state, content, rid, this, bundle, intentActivityResultLauncher, handler, linearLayout);
                    }
                } else if (spinner.getSelectedItemPosition() == 1) {
                    int j = 0;
                    for (int i = 0; i < Objects.requireNonNull(repairs).size(); i++) {
                        if (repairs.get(i).getEnd() == null && repairs.get(i).getsId() == null) {
                            String content = repairs.get(i).getrContent();
                            String rid = repairs.get(i).getrId().toString();
                            AddControl.addRepair("未处理", content, rid, this, bundle, intentActivityResultLauncher, handler, linearLayout);
                            j++;
                        }
                    }
                    int finalJ = j;
                    handler.post(() -> count.setText(String.valueOf(finalJ)));
                } else if (spinner.getSelectedItemPosition() == 2) {
                    int j = 0;
                    for (int i = 0; i < Objects.requireNonNull(repairs).size(); i++) {
                        if (repairs.get(i).getEnd() == null && repairs.get(i).getsId() != null) {
                            String content = repairs.get(i).getrContent();
                            String rid = repairs.get(i).getrId().toString();
                            AddControl.addRepair("进行中", content, rid, this, bundle, intentActivityResultLauncher, handler, linearLayout);
                            j++;
                        }
                    }
                    int finalJ = j;
                    handler.post(() -> count.setText(String.valueOf(finalJ)));
                } else if (spinner.getSelectedItemPosition() == 3) {
                    int j =0;
                    for (int i = 0; i < Objects.requireNonNull(repairs).size(); i++) {
                        if (repairs.get(i).getStar() == null && repairs.get(i).getEnd() != null) {
                            String content = repairs.get(i).getrContent();
                            String rid = repairs.get(i).getrId().toString();
                            AddControl.addRepair("待评价", content, rid, this, bundle, intentActivityResultLauncher, handler, linearLayout);
                            j++;
                        }
                    }
                    int finalJ = j;
                    handler.post(() -> count.setText(String.valueOf(finalJ)));
                } else {
                    int j =0;
                    for (int i = 0; i < Objects.requireNonNull(repairs).size(); i++) {
                        if (repairs.get(i).getStar() != null) {
                            String content = repairs.get(i).getrContent();
                            String rid = repairs.get(i).getrId().toString();
                            AddControl.addRepair("已完成", content, rid, this, bundle, intentActivityResultLauncher, handler, linearLayout);
                            j++;
                        }
                    }
                    int finalJ = j;
                    handler.post(() -> count.setText(String.valueOf(finalJ)));
                }
                break;
            }
            case "support": {
                String url = "http://" + getResources().getString(R.string.DNS) + ":8080//repair/selectBySid?s_id=" + id;
                List<Repair> repairs = MyRequest.getRepairs(url);
                if (spinner.getSelectedItemPosition() == 0) {
                    handler.post(() -> count.setText(String.valueOf(repairs != null ? repairs.size() : 0)));
                    for (int i = Objects.requireNonNull(repairs).size() - 1; i >= 0; i--) {
                        String state;
                        if (repairs.get(i).getStar() != null)
                            state = "已评价";
                        else if (repairs.get(i).getEnd() != null)
                            state = "已完成";
                        else state = "未完成";
                        String content = "[" + repairs.get(i).getrPlace() + "]" + repairs.get(i).getrContent();
                        String rid = repairs.get(i).getrId().toString();
                        AddControl.addRepair(state, content, rid, this, bundle, intentActivityResultLauncher, handler, linearLayout);
                    }
                } else if (spinner.getSelectedItemPosition() == 1) {
                    int j=0;
                    for (int i = 0; i < Objects.requireNonNull(repairs).size(); i++) {
                        if (repairs.get(i).getEnd() == null) {
                            String state = "未完成";
                            String content = "[" + repairs.get(i).getrPlace() + "]" + repairs.get(i).getrContent();
                            String rid = repairs.get(i).getrId().toString();
                            AddControl.addRepair(state, content, rid, this, bundle, intentActivityResultLauncher, handler, linearLayout);
                            j++;
                        }
                    }
                    int finalJ = j;
                    handler.post(() -> count.setText(String.valueOf(finalJ)));
                } else if (spinner.getSelectedItemPosition() == 2) {
                    int j =0;
                    for (int i = 0; i < Objects.requireNonNull(repairs).size(); i++) {
                        if (repairs.get(i).getStar() == null && repairs.get(i).getEnd() != null) {
                            String state = "已完成";
                            String content = "[" + repairs.get(i).getrPlace() + "]" + repairs.get(i).getrContent();
                            String rid = repairs.get(i).getrId().toString();
                            AddControl.addRepair(state, content, rid, this, bundle, intentActivityResultLauncher, handler, linearLayout);
                            j++;
                        }
                    }
                    int finalJ = j;
                    handler.post(() -> count.setText(String.valueOf(finalJ)));
                } else {
                    int j =0;
                    for (int i = 0; i < Objects.requireNonNull(repairs).size(); i++) {
                        if (repairs.get(i).getStar() != null) {
                            String state = "已评价";
                            String content = "[" + repairs.get(i).getrPlace() + "]" + repairs.get(i).getrContent();
                            String rid = repairs.get(i).getrId().toString();
                            AddControl.addRepair(state, content, rid, this, bundle, intentActivityResultLauncher, handler, linearLayout);
                            j++;
                        }
                    }
                    int finalJ = j;
                    handler.post(() -> count.setText(String.valueOf(finalJ)));
                }
                break;
            }
            case "admin":  //管理员主页生成
                AddControl.addManage("user", "用户管理", this, bundle, intentActivityResultLauncher, handler, linearLayout);
                AddControl.addManage("support", "后勤人员管理", this, bundle, intentActivityResultLauncher, handler, linearLayout);
                AddControl.addManage("repair", "报修单管理", this, bundle, intentActivityResultLauncher, handler, linearLayout);
                break;
            default:
                Looper.prepare();
                Toast.makeText(this, "身份识别错误！请重试！", Toast.LENGTH_SHORT).show();
                Looper.loop();
        }
    }
}
