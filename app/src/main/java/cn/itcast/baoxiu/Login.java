package cn.itcast.baoxiu;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import cn.itcast.baoxiu.util.MyRequest;

public class Login extends AppCompatActivity {
    //控件
    private TextView et_username;
    private TextView et_password;
    private RadioGroup userType;
    private Button login;
    private Button register;
    //全局变量
    private String[] types = {"user", "u_id", "u_password"};
    private String authority = "common";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        //初始化控件
        initView();
        //初始化事件
        initEvent();
    }

    public void initView() {
        et_username = this.findViewById(R.id.et_username);
        et_password = this.findViewById(R.id.et_password);
        userType = this.findViewById(R.id.userType);
        login = this.findViewById(R.id.login);
        register = this.findViewById(R.id.register);
    }

    public void initEvent() {
        userType.setOnCheckedChangeListener((radioGroup, i) -> setType(radioGroup));
        //给登录按钮添加点击事件监听器(登录事件)
        login.setOnClickListener(v -> {
            //调用API验证用户名密码是否正确
            new Thread(this::login).start();
        });
        register.setOnClickListener(v -> {
            Intent intent = new Intent(this, Register.class);
            startActivity(intent);
        });
    }

    //区别身份和权力
    public void setType(RadioGroup radioGroup) {
        RadioButton rb = Login.this.findViewById(radioGroup.getCheckedRadioButtonId());
        if (rb.getText().equals("管理员")) {
            types = new String[]{"admin", "a_id", "a_password"};
            authority = "admin";
        } else if (rb.getText().equals("后勤人员")) {
            types = new String[]{"support", "s_id", "s_password"};
            authority = "common";
        } else if (rb.getText().equals("教师/学生")) {
            types = new String[]{"user", "u_id", "u_password"};
            authority = "common";
        } else {
            Looper.prepare();
            Toast.makeText(this, "身份识别错误！请重试！", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }

    //登录
    public void login() {
        if (et_username.getText().toString().trim().length() == 0 || et_password.getText().toString().trim().length() == 0) {
            Looper.prepare();
            Toast.makeText(Login.this, "请输入账号密码！", Toast.LENGTH_SHORT).show();
            Looper.loop();
            return;
        }
        String url = "http://" + getResources().getString(R.string.DNS) + ":8080//" + types[0] + "/login?" + types[1] + "=" + et_username.getText() + "&" + types[2] + "=" + et_password.getText();
        String res = MyRequest.getString(url);
        assert res != null;
        boolean isLogin = res.equals("true");
        //登录事件
        if (isLogin) {
            Intent intent = new Intent(this, Main.class);
            Bundle bundle = new Bundle();
            bundle.putString("id", et_username.getText().toString());
            bundle.putString("type", types[0]);
            bundle.putString("authority", authority);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            Looper.prepare();
            Toast.makeText(Login.this, "账号或密码错误！", Toast.LENGTH_SHORT).show();
            Looper.loop();
        }
    }
}