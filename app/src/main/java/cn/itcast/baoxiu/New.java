package cn.itcast.baoxiu;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.List;
import java.util.Objects;

import cn.itcast.baoxiu.entity.Repair;
import cn.itcast.baoxiu.util.ActivityCollector;
import cn.itcast.baoxiu.util.AddControl;
import cn.itcast.baoxiu.util.MyRequest;

public class New extends AppCompatActivity {
    //activity间传输
    private Bundle bundle;
    private String id;
    //控件
    private LinearLayout linearLayout;
    private TextView count;
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
                setResult(1);
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.blank_layout);
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

    private void initView(){
        linearLayout = this.findViewById(R.id.linearLayout);
        count = this.findViewById(R.id.count_repairs);
    }

    private void initEvent(){
        bundle = getIntent().getExtras();
        id = bundle.getString("id");
        new Thread(this::showRepairs).start();
    }

    //动态生成报修单
    private void showRepairs() {
        String url = "http://" + getResources().getString(R.string.DNS) + ":8080//repair/selectNew";
        java.util.List<Repair> repairs = MyRequest.getRepairs(url);
        handler.post(() -> count.setText(String.valueOf(repairs != null ? repairs.size() : 0)));
        for (int i = 0; i < Objects.requireNonNull(repairs).size(); i++) {
            String content = "[" + repairs.get(i).getrPlace() + "]" + repairs.get(i).getrContent();
            String rid = repairs.get(i).getrId().toString();
            AddControl.addNewRepair(id,content, rid,this,bundle,intentActivityResultLauncher,handler,linearLayout);
        }
    }
}
