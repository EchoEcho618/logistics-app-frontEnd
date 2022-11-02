package cn.itcast.baoxiu;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputFilter;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.itcast.baoxiu.entity.Support;
import cn.itcast.baoxiu.entity.User;
import cn.itcast.baoxiu.util.ActivityCollector;
import cn.itcast.baoxiu.util.AddControl;
import cn.itcast.baoxiu.util.MyRequest;

public class Add extends AppCompatActivity {
    //activity间传输
    private Bundle bundle;
    private String id;
    private String type;
    private String authority;
    //控件
    private Button save;
    private LinearLayout linearLayout;
    //线程间传输
    final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_info);
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
    @SuppressLint("NonConstantResourceId")
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
        save = this.findViewById(R.id.save_info);
        linearLayout = this.findViewById(R.id.linearLayout);
    }

    private void initEvent() {
        bundle = getIntent().getExtras();
        id = bundle.getString("id");
        type = bundle.getString("type");
        authority = bundle.getString("authority");
        new Thread(this::showInfo).start();
        save.setOnClickListener(v -> new Thread(this::saveInfo).start());
    }

    //动态绘制界面
    private void showInfo() {
        switch (type) {
            case "user": {
                AddControl.addInfo(R.id.edit_text_1, "姓名*：",this,handler,linearLayout);
                AddControl.addInfo(R.id.edit_text_4, "密码*：",this,handler,linearLayout);
                AddControl.addSpinner(R.id.spinner_1, "性别：", R.array.sex,this,handler,linearLayout);
                AddControl.addInfo(R.id.edit_text_2, "电话*：",this,handler,linearLayout);
                AddControl.addSpinner(R.id.spinner_2, "身份：", R.array.type,this,handler,linearLayout);
                break;
            }
            case "support": {
                AddControl.addInfo(R.id.edit_text_1, "姓名*：",this,handler,linearLayout);
                AddControl.addInfo(R.id.edit_text_4, "密码*：",this,handler,linearLayout);
                AddControl.addSpinner(R.id.spinner_1, "性别：", R.array.sex,this,handler,linearLayout);
                AddControl.addInfo(R.id.edit_text_2, "电话*：",this,handler,linearLayout);
                break;
            }
            case "repair": {
                List<CharSequence> supportNames = null;
                if (authority.equals("admin")) {
                    List<CharSequence> userNames = new ArrayList<>();
                    String url = "http://" + getResources().getString(R.string.DNS) + ":8080//user/selectAll";
                    List<User> users = MyRequest.getUsers(url);
                    for (int i = 0; i < Objects.requireNonNull(users).size(); i++) {
                        userNames.add(users.get(i).getuId() + "." + users.get(i).getuName());
                    }
                    supportNames = new ArrayList<>();
                    supportNames.add("无");
                    url = "http://" + getResources().getString(R.string.DNS) + ":8080//support/selectAll";
                    List<Support> supports = MyRequest.getSupports(url);
                    for (int i = 0; i < Objects.requireNonNull(supports).size(); i++) {
                        supportNames.add(supports.get(i).getsId() + "." + supports.get(i).getsName());
                    }
                    AddControl.addSpinner(R.id.spinner_1, "报修人：", userNames,this,handler,linearLayout);
                }
                AddControl.addInfo(R.id.edit_text_1, "报修地点*：",this,handler,linearLayout);
                AddControl.addInfo(R.id.edit_text_2, "报修内容*：",this,handler,linearLayout);
                if (authority.equals("admin")) {
                    AddControl.addSpinner(R.id.spinner_2, "维修人：", supportNames,this,handler,linearLayout);
                    AddControl.addTimeInfo(R.id.edit_text_4, "完成时间：",this,handler,linearLayout);
                }
                break;
            }
            default: {
                Looper.prepare();
                Toast.makeText(Add.this, "身份识别错误！请重试！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }

    //提交信息
    private void saveInfo() {
        String url = "http://" + getResources().getString(R.string.DNS) + ":8080//" + type + "/add";
        JSONObject json = new JSONObject();
        switch (type) {
            case "user": {
                EditText uname = this.findViewById(R.id.edit_text_1);
                EditText upassword = this.findViewById(R.id.edit_text_4);
                EditText utel = this.findViewById(R.id.edit_text_2);
                Spinner usexText = this.findViewById(R.id.spinner_1);
                Spinner utypeText = this.findViewById(R.id.spinner_2);
                utel.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});
                int usex = usexText.getSelectedItemPosition();
                int utype = utypeText.getSelectedItemPosition();
                try {
                    json.put("uname", uname.getText());
                    json.put("upassword", upassword.getText());
                    json.put("utel", utel.getText());
                    json.put("usex", usex);
                    json.put("utype", utype);
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                break;
            }
            case "support": {
                EditText sname = this.findViewById(R.id.edit_text_1);
                EditText spassword = this.findViewById(R.id.edit_text_4);
                EditText stel = this.findViewById(R.id.edit_text_2);
                Spinner ssexText = this.findViewById(R.id.spinner_1);
                int ssex = ssexText.getSelectedItemPosition();
                try {
                    json.put("sname", sname.getText());
                    json.put("spassword", spassword.getText());
                    json.put("stel", stel.getText());
                    json.put("ssex", ssex);
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                break;
            }
            case "repair": {
                String uid = null;
                String sid = "无";
                TextView end = null;
                if (authority.equals("admin")) {
                    Spinner uidText = this.findViewById(R.id.spinner_1);
                    String line = uidText.getSelectedItem().toString();
                    Pattern r = Pattern.compile("\\d+");
                    Matcher m = r.matcher(line);
                    if (m.find()) {
                        uid = m.group();
                        System.out.println(uid);
                    }
                }
                EditText rplace = this.findViewById(R.id.edit_text_1);
                EditText rcontent = this.findViewById(R.id.edit_text_2);
                if (authority.equals("admin")) {
                    Spinner sidText = this.findViewById(R.id.spinner_2);
                    String line = sidText.getSelectedItem().toString();
                    Pattern r = Pattern.compile("(\\d+)(\\..*)");
                    Matcher m = r.matcher(line);
                    if (m.find()) sid = m.group(1);
                    end = this.findViewById(R.id.edit_text_4);
                }
                try {
                    if (authority.equals("admin")) json.put("uid", uid);
                    else json.put("uid", id);
                    json.put("rplace", rplace.getText());
                    json.put("rcontent", rcontent.getText());
                    if (authority.equals("admin")) {
                        assert sid != null;
                        if (!sid.equals("无")) json.put("sid", sid);
                        assert end != null;
                        if (end.getText() != null) json.put("end", end.getText());
                    }
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                break;
            }
            default: {
                Looper.prepare();
                Toast.makeText(Add.this, "身份识别错误！请重试！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
        String res = MyRequest.post(url, json);
        Looper.prepare();
        Toast.makeText(Add.this, res, Toast.LENGTH_SHORT).show();
        assert res != null;
        if (res.matches(".*成功.*"))
        {
            //返回上页并刷新
            setResult(1);
            finish();
            ActivityCollector.removeActivity(this);
        }
        Looper.loop();
    }
}
