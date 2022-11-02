package cn.itcast.baoxiu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import cn.itcast.baoxiu.entity.Admin;
import cn.itcast.baoxiu.entity.Support;
import cn.itcast.baoxiu.entity.User;
import cn.itcast.baoxiu.util.ActivityCollector;
import cn.itcast.baoxiu.util.AddControl;
import cn.itcast.baoxiu.util.MyRequest;

public class Mine extends AppCompatActivity {
    //activity间传输
    private Bundle bundle;
    private String id;
    private String type;
    //控件
    private Button left;
    private Button right;
    private Button edit;
    private Button mid;
    private LinearLayout buttons;
    private LinearLayout linearLayout;
    //线程间传输
    final Handler handler = new Handler();
    //回调
    private final ActivityResultLauncher<Intent> intentActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode()==1){
                linearLayout.removeAllViews();
                initView();
                initEvent();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mine);
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
                ActivityCollector.removeActivity(this);
                return true;
            case R.id.exit:
                ActivityCollector.finishAllActivity();
                break;
            case R.id.edit_password:
                Intent intent1 = new Intent(this,EditPWD.class);
                Bundle bundle = getIntent().getExtras();
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
        left = this.findViewById(R.id.left_btn);
        right = this.findViewById(R.id.right_btn);
        edit = this.findViewById(R.id.edit_mine);
        mid = this.findViewById(R.id.mid_btn);
        linearLayout = this.findViewById(R.id.linearLayout);
        buttons = this.findViewById(R.id.buttons);
    }

    public void initEvent() {
        bundle = getIntent().getExtras();
        id = bundle.getString("id");
        type = bundle.getString("type");
        switch (type) {
            case "user":
                mid.setOnClickListener(v -> {
                    Intent intent = new Intent(this, Add.class);
                    bundle.putString("id", id);
                    bundle.putString("type", "repair");
                    intent.putExtras(bundle);
                    intentActivityResultLauncher.launch(intent);
                });
                break;
            case "support":
                mid.setOnClickListener(v -> {
                    Intent intent = new Intent(this, New.class);
                    bundle.putString("id", id);
                    intent.putExtras(bundle);
                    intentActivityResultLauncher.launch(intent);
                });
                break;
            case "admin":
                buttons.removeView(mid);
                break;
            default:
                Looper.prepare();
                Toast.makeText(this, "身份识别错误！请重试！", Toast.LENGTH_SHORT).show();
                Looper.loop();
        }
        new Thread(this::showInfo).start();
        left.setOnClickListener(v -> {
            Intent intent = new Intent(this, Main.class);
            bundle.putString("id",id);
            bundle.putString("type",type);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
        right.setOnClickListener(v -> {
            finish();
            ActivityCollector.removeActivity(this);
            Intent intent = new Intent(this, Mine.class);
            bundle.putString("id",id);
            bundle.putString("type",type);
            intent.putExtras(bundle);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });
        edit.setOnClickListener(v -> {
            Intent intent = new Intent(this, EditInfo.class);
            bundle.putString("id",id);
            bundle.putString("type",type);
            intent.putExtras(bundle);
            intentActivityResultLauncher.launch(intent);
        });
    }

    //动态生成用户信息
    public void showInfo() {
        switch (type) {
            case "user": {
                String url = "http://" + getResources().getString(R.string.DNS) + ":8080//user/info?u_id=" + id;
                User user = MyRequest.getUser(url);
                String sex;
                assert user != null;
                if (user.getuSex() == 0) sex = "男";
                else sex = "女";
                String tp;
                if (user.getuType() == 0) tp = "教师";
                else tp = "学生";
                AddControl.addInfo("账号：" + user.getuId(),this,handler,linearLayout);
                AddControl.addInfo("姓名：" + user.getuName(),this,handler,linearLayout);
                AddControl.addInfo("性别：" + sex,this,handler,linearLayout);
                AddControl.addInfo("电话：" + user.getuTel(),this,handler,linearLayout);
                AddControl.addInfo("身份：" + tp,this,handler,linearLayout);
                break;
            }
            case "support": {
                String url = "http://" + getResources().getString(R.string.DNS) + ":8080//support/info?s_id=" + id;
                Support support = MyRequest.getSupport(url);
                String sex;
                assert support != null;
                if (support.getsSex() == 0) sex = "男";
                else sex = "女";
                AddControl.addInfo("账号：" + support.getsId(),this,handler,linearLayout);
                AddControl.addInfo("姓名：" + support.getsName(),this,handler,linearLayout);
                AddControl.addInfo("性别：" + sex,this,handler,linearLayout);
                AddControl.addInfo("电话：" + support.getsTel(),this,handler,linearLayout);
                break;
            }
            case "admin": {
                String url = "http://" + getResources().getString(R.string.DNS) + ":8080//admin/info?a_id=" + id;
                Admin admin = MyRequest.getAdmin(url);
                assert admin != null;
                AddControl.addInfo("账号：" + admin.getaId(),this,handler,linearLayout);
                AddControl.addInfo("姓名：" + admin.getaName(),this,handler,linearLayout);
                break;
            }
            default:
                Looper.prepare();
                Toast.makeText(this, "身份识别错误！请重试！", Toast.LENGTH_SHORT).show();
                Looper.loop();
        }
    }
}
