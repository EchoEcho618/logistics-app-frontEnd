package cn.itcast.baoxiu;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.text.ParseException;
import java.util.Calendar;

import cn.itcast.baoxiu.entity.Repair;
import cn.itcast.baoxiu.entity.Support;
import cn.itcast.baoxiu.entity.User;
import cn.itcast.baoxiu.util.ActivityCollector;
import cn.itcast.baoxiu.util.AddControl;
import cn.itcast.baoxiu.util.DateAndTime;
import cn.itcast.baoxiu.util.MyRequest;

public class Info extends AppCompatActivity {
    //activity间传输
    private Bundle bundle;
    private String id;
    private String type;
    private String authority;
    private String state;
    //控件
    private LinearLayout linearLayout;
    private Button edit;
    private Button del;
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
                setResult(1);
            }
            if (result.getResultCode() ==2){
                setResult(1);
                finish();
            }
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info);
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
        linearLayout = this.findViewById(R.id.linearLayout);
        edit = this.findViewById(R.id.edit_info);
        del = this.findViewById(R.id.del_info);
    }

    private void initEvent() {
        bundle = getIntent().getExtras();
        id = bundle.getString("id");
        type = bundle.getString("type");
        authority = bundle.getString("authority");
        state = bundle.getString("state");
        new Thread(this::showInfo).start();
        switch (state) {
            case "未处理":
                edit.setOnClickListener(v -> {
                    Intent intent = new Intent(this, EditInfo.class);
                    intent.putExtras(bundle);
                    intentActivityResultLauncher.launch(intent);
                });
                del.setOnClickListener(v -> new Thread(this::delInfo).start());
                break;
            case "未完成":
                edit.setText("完成");
                edit.setOnClickListener(v -> new Thread(this::complete).start());
                del.setOnClickListener(v -> new Thread(() -> acceptOrCancel(null, "取消报修单成功！")).start());
                break;
            case "新报修单":
                edit.setVisibility(View.INVISIBLE);
                String sid = bundle.getString("sid");
                del.setOnClickListener(v -> new Thread(() -> acceptOrCancel(sid, "接受报修单成功！")).start()
                );
                del.setText("接受");
                break;
            case "待评价":
                edit.setVisibility(View.INVISIBLE);
                del.setOnClickListener(v -> {
                    Intent intent = new Intent(this, Evaluate.class);
                    intent.putExtras(bundle);
                    intentActivityResultLauncher.launch(intent);
                });
                del.setText("评价");
                break;
            default:
                edit.setVisibility(View.INVISIBLE);
                del.setVisibility(View.INVISIBLE);
        }
    }

    //动态绘制页面
    private void showInfo() {
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
                AddControl.addInfo("账号：" + user.getuId(), this, handler, linearLayout);
                AddControl.addInfo("姓名：" + user.getuName(), this, handler, linearLayout);
                if (authority.equals("admin"))
                    AddControl.addInfo("密码：" + user.getuPassword(), this, handler, linearLayout);
                AddControl.addInfo("性别：" + sex, this, handler, linearLayout);
                AddControl.addInfo("电话：" + user.getuTel(), this, handler, linearLayout);
                AddControl.addInfo("身份：" + tp, this, handler, linearLayout);
                break;
            }
            case "support": {
                String url = "http://" + getResources().getString(R.string.DNS) + ":8080//support/info?s_id=" + id;
                Support support = MyRequest.getSupport(url);
                String sex;
                assert support != null;
                if (support.getsSex() == 0) sex = "男";
                else sex = "女";
                AddControl.addInfo("账号：" + support.getsId(), this, handler, linearLayout);
                AddControl.addInfo("姓名：" + support.getsName(), this, handler, linearLayout);
                if (authority.equals("admin"))
                    AddControl.addInfo("密码：" + support.getsPassword(), this, handler, linearLayout);
                AddControl.addInfo("性别：" + sex, this, handler, linearLayout);
                AddControl.addInfo("电话：" + support.getsTel(), this, handler, linearLayout);
                break;
            }
            case "repair": {
                String url = "http://" + getResources().getString(R.string.DNS) + ":8080//repair/info?r_id=" + id;
                Repair repair = MyRequest.getRepair(url);
                assert repair != null;
                url = "http://" + getResources().getString(R.string.DNS) + ":8080//user/name?u_id=" + repair.getuId();
                String uName = MyRequest.getString(url);
                String sName;
                if (repair.getsId() != null) {
                    url = "http://" + getResources().getString(R.string.DNS) + ":8080//support/name?s_id=" + repair.getsId();
                    sName = MyRequest.getString(url);
                } else sName = "无";
                AddControl.addInfo("报修单号：" + repair.getrId(), this, handler, linearLayout);
                AddControl.addInfo("报修人：" + uName, this, handler, linearLayout);
                AddControl.addInfo("报修地点：" + repair.getrPlace(), this, handler, linearLayout);
                AddControl.addInfo("报修内容：" + repair.getrContent(), this, handler, linearLayout);
                AddControl.addInfo("报修时间：" + repair.getStart(), this, handler, linearLayout);
                AddControl.addInfo("维修人：" + sName, this, handler, linearLayout);
                if (repair.getEnd() != null)
                    AddControl.addInfo("完成时间：" + repair.getEnd(), this, handler, linearLayout);
                else AddControl.addInfo("完成时间：未完成", this, handler, linearLayout);
                if (repair.getStar()!=null)
                    AddControl.addRating("星级：",repair.getStar(),this,handler,linearLayout);
                else AddControl.addInfo("星级：无", this, handler, linearLayout);
                if (repair.getEvaluation()!=null)
                    AddControl.addInfo("留言："+repair.getEvaluation(),this,handler,linearLayout);
                else AddControl.addInfo("留言：无",this,handler,linearLayout);
                break;
            }
            default:
                Looper.prepare();
                Toast.makeText(this, "身份识别错误！请重试！", Toast.LENGTH_SHORT).show();
                Looper.loop();
                break;
        }
    }

    //删除
    private void delInfo() throws JSONException {
        Looper.prepare();
        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("确认删除？")
                .setNegativeButton("取消",((dialogInterface, i) -> dialogInterface.dismiss()))
                .setPositiveButton("确认", (dialogInterface, i) -> {
                    String url = "http://" + getResources().getString(R.string.DNS) + ":8080//" + type + "/delete";
                    JSONObject json = new JSONObject();
                    switch (type) {
                        case "user":
                            json.put("uid", id);
                            break;
                        case "support":
                            json.put("sid", id);
                            break;
                        case "repair":
                            json.put("rid", id);
                            break;
                        default:
                            Looper.prepare();
                            Toast.makeText(this, "身份识别错误！请重试！", Toast.LENGTH_SHORT).show();
                            Looper.loop();
                    }
                    String res = MyRequest.post(url, json);
                    Toast.makeText(Info.this, res, Toast.LENGTH_SHORT).show();
                    assert res != null;
                    if (res.matches(".*成功.*")) {
                        //返回并刷新
                        setResult(1);
                        finish();
                        ActivityCollector.removeActivity(this);
                    }
                })
                .create();
        alertDialog.show();
        Looper.loop();
    }

    //接受/取消报修单
    private void acceptOrCancel(String sid, String res) {
        JSONObject json = new JSONObject();
        json.put("rid", id);
        String url;
        if (sid == null) {
            url = "http://" + getResources().getString(R.string.DNS) + ":8080//repair/cancel";
        } else {
            json.put("sid", sid);
            url = "http://" + getResources().getString(R.string.DNS) + ":8080//repair/update";
        }
        MyRequest.post(url, json);
        Looper.prepare();
        Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
        //返回并刷新
        setResult(1);
        finish();
        ActivityCollector.removeActivity(this);
        Looper.loop();
    }

    //完成报修单
    private void complete() {
        String date = Calendar.getInstance().get(Calendar.YEAR) + "-" + Calendar.getInstance().get(Calendar.MONTH) + "-" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        try {
            date = DateAndTime.getTransDate(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String timeStr = Calendar.getInstance().get(Calendar.HOUR_OF_DAY) + ":" + Calendar.getInstance().get(Calendar.MINUTE) + ":" + Calendar.getInstance().get(Calendar.SECOND);
        try {
            timeStr = DateAndTime.getTransTime(timeStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        date = date + "T" + timeStr;
        JSONObject json = new JSONObject();
        json.put("rid", id);
        json.put("end", date);
        String url = "http://" + getResources().getString(R.string.DNS) + ":8080//repair/update";
        MyRequest.post(url, json);
        Looper.prepare();
        Toast.makeText(this, "该报修单已完成！", Toast.LENGTH_SHORT).show();
        //返回并刷新
        setResult(1);
        finish();
        ActivityCollector.removeActivity(this);
        Looper.loop();
    }
}
