package cn.itcast.baoxiu;

import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import cn.itcast.baoxiu.util.ActivityCollector;
import cn.itcast.baoxiu.util.MyRequest;
import cn.itcast.baoxiu.util.Validation;

public class EditPWD extends AppCompatActivity {
    //activity间传输
    private Bundle bundle;
    private String id;
    private String type;
    //控件
    private EditText oldPwd;
    private EditText newPwd;
    private EditText rePwd;
    private Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_pwd);
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
        oldPwd = this.findViewById(R.id.old_pwd);
        newPwd = this.findViewById(R.id.new_pwd);
        rePwd = this.findViewById(R.id.re_pwd);
        save = this.findViewById(R.id.save_pwd);
    }

    private void initEvent() {
        bundle = getIntent().getExtras();
        id = bundle.getString("id");
        type = bundle.getString("type");
        save.setOnClickListener(v -> new Thread(this::savaPwd).start());
    }

    //提交信息
    private void savaPwd() {
        JSONObject json = new JSONObject();
        String url1 = null, url2 = null;
        switch (type) {
            case "user":
                url1 = "http://" + getResources().getString(R.string.DNS) + ":8080//user/login?u_id=" + id + "&u_password=" + oldPwd.getText();
                url2 = "http://" + getResources().getString(R.string.DNS) + ":8080//user/update";
                try {
                    json.put("uid", id);
                    json.put("upassword", newPwd.getText());
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                break;
            case "support":
                url1 = "http://" + getResources().getString(R.string.DNS) + ":8080//support/login?s_id=" + id + "&s_password=" + oldPwd.getText();
                url2 = "http://" + getResources().getString(R.string.DNS) + ":8080//support/update";
                try {
                    json.put("sid", id);
                    json.put("spassword", newPwd.getText());
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                break;
            case "admin":
                url1 = "http://" + getResources().getString(R.string.DNS) + ":8080//admin/login?a_id=" + id + "&a_password=" + oldPwd.getText();
                url2 = "http://" + getResources().getString(R.string.DNS) + ":8080//admin/update";
                try {
                    json.put("aid", id);
                    json.put("apassword", newPwd.getText());
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                break;
            default:
                Looper.prepare();
                Toast.makeText(EditPWD.this, "身份识别错误！请重试！", Toast.LENGTH_SHORT).show();
                Looper.loop();
        }
        String res = MyRequest.getString(url1);
        assert res != null;
        boolean isLogin = res.equals("true");
        if (!isLogin) {
            Looper.prepare();
            Toast.makeText(EditPWD.this, "原密码错误！", Toast.LENGTH_SHORT).show();
            Looper.loop();
            return;
        }
        Validation.editTextNotBlank(newPwd,this,"密码");
        Validation.reEtPwd(newPwd,rePwd,this);
        res = MyRequest.post(url2, json);
        Looper.prepare();
        assert res != null;
        if (res.matches(".*成功.*")) {
            Toast.makeText(EditPWD.this, res + "请重新登录!", Toast.LENGTH_SHORT).show();
            //结束除登录外所有activity
            ActivityCollector.finishAllActivity();
        }else
            Toast.makeText(EditPWD.this,res,Toast.LENGTH_SHORT).show();
        Looper.loop();
    }
}
