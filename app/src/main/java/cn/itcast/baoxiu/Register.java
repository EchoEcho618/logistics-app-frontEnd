package cn.itcast.baoxiu;

import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import cn.itcast.baoxiu.util.ActivityCollector;
import cn.itcast.baoxiu.util.MyRequest;
import cn.itcast.baoxiu.util.Validation;

public class Register extends AppCompatActivity {
    //控件
    private EditText et_password;
    private EditText re_password;
    private EditText et_name;
    private EditText et_tel;
    private RadioGroup userSex;
    private RadioGroup userType;
    private Button register;
    //全局变量
    private int sex = 0;//默认男
    private int type = 1;//默认学生
    private String typeText = "user";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
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

    public void initView() {
        et_password = this.findViewById(R.id.et_password);
        re_password = this.findViewById(R.id.re_password);
        et_name = this.findViewById(R.id.et_name);
        et_tel = this.findViewById(R.id.et_tel);
        userSex = this.findViewById(R.id.userSex);
        userType = this.findViewById(R.id.userType);
        register = this.findViewById(R.id.register);
    }

    public void initEvent() {
        userSex.setOnCheckedChangeListener((radioGroup, i) -> setSex(radioGroup));
        userType.setOnCheckedChangeListener((radioGroup, i) -> setType(radioGroup));
        register.setOnClickListener(v -> new Thread(this::register).start());
    }

    //性别数据转换
    public void setSex(RadioGroup radioGroup) {
        RadioButton rb = Register.this.findViewById(radioGroup.getCheckedRadioButtonId());
        if (rb.getText().equals("女")) {
            sex = 1;
        } else {
            sex = 0;
        }
    }

    //身份数据转换
    public void setType(RadioGroup radioGroup) {
        RadioButton rb = Register.this.findViewById(radioGroup.getCheckedRadioButtonId());
        if (rb.getText().equals("后勤人员")) {
            typeText = "support";
        } else {
            typeText = "user";
            if (rb.getText().equals("教师")) {
                type = 0;
            } else {
                type = 1;
            }
        }
    }

    //注册
    public void register() {
        Validation.reEtPwd(et_password, re_password, this);
        String url = "http://" + getResources().getString(R.string.DNS) + ":8080//" + typeText + "/add";
        JSONObject json = new JSONObject();
        try {
            if (typeText.equals("support")) {
                json.put("sname", et_name.getText());
                json.put("spassword", et_password.getText());
                json.put("ssex", sex);
                json.put("stel", et_tel.getText());
            } else {
                json.put("uname", et_name.getText());
                json.put("upassword", et_password.getText());
                json.put("usex", sex);
                json.put("utel", et_tel.getText());
                json.put("utype", type);
            }
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        String res = MyRequest.post(url, json);
        Looper.prepare();
        assert res != null;
        if (res.matches(".*成功.*")) {
            AlertDialog alertDialog = new AlertDialog.Builder(this)
                    .setTitle("提示")
                    .setMessage(res)
                    .setPositiveButton("前往登录", (dialogInterface, i) -> {
                        finish();
                        ActivityCollector.removeActivity(this);
                    })
                    .create();
            alertDialog.show();
        } else
            Toast.makeText(this, res, Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
