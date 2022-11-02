package cn.itcast.baoxiu;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
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

import cn.itcast.baoxiu.entity.Admin;
import cn.itcast.baoxiu.entity.Repair;
import cn.itcast.baoxiu.entity.Support;
import cn.itcast.baoxiu.entity.User;
import cn.itcast.baoxiu.util.ActivityCollector;
import cn.itcast.baoxiu.util.AddControl;
import cn.itcast.baoxiu.util.MyRequest;
import cn.itcast.baoxiu.util.Validation;

public class EditInfo extends AppCompatActivity {
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
        save.setOnClickListener(v -> new Thread(this::savaInfo).start());
    }

    //动态绘制界面
    private void showInfo() {
        switch (type) {
            case "user": {
                String url = "http://" + getResources().getString(R.string.DNS) + ":8080//user/info?u_id=" + id;
                User user = MyRequest.getUser(url);
                assert user != null;
                AddControl.addInfo(R.id.edit_text_1, "姓名：", user.getuName(),this,handler,linearLayout);
                if (authority.equals("admin"))
                    AddControl.addInfo(R.id.edit_text_3, "密码：", user.getuPassword(),this,handler,linearLayout);
                AddControl.addSpinner(R.id.spinner_1, "性别：", R.array.sex, user.getuSex(),this,handler,linearLayout);
                AddControl.addInfo(R.id.edit_text_2, "电话：", user.getuTel(),this,handler,linearLayout);
                AddControl.addSpinner(R.id.spinner_2, "身份：", R.array.type, user.getuType(),this,handler,linearLayout);
                break;
            }
            case "support": {
                String url = "http://" + getResources().getString(R.string.DNS) + ":8080//support/info?s_id=" + id;
                Support support = MyRequest.getSupport(url);
                assert support != null;
                AddControl.addInfo(R.id.edit_text_1, "姓名：", support.getsName(),this,handler,linearLayout);
                if (authority.equals("admin"))
                    AddControl.addInfo(R.id.edit_text_3, "密码：", support.getsPassword(),this,handler,linearLayout);
                AddControl.addSpinner(R.id.spinner_1, "性别：", R.array.sex, support.getsSex(),this,handler,linearLayout);
                AddControl.addInfo(R.id.edit_text_2, "电话：", support.getsTel(),this,handler,linearLayout);
                break;
            }
            case "repair": {
                String url = "http://" + getResources().getString(R.string.DNS) + ":8080//repair/info?r_id=" + id;
                Repair repair = MyRequest.getRepair(url);
                int userCode = 0;
                int supportCode = 0;
                List<CharSequence> supportNames = null;
                if (authority.equals("admin")) {
                    List<CharSequence> userNames = new ArrayList<>();
                    url = "http://" + getResources().getString(R.string.DNS) + ":8080//user/selectAll";
                    List<User> users = MyRequest.getUsers(url);
                    for (int i = 0; i < Objects.requireNonNull(users).size(); i++) {
                        userNames.add(users.get(i).getuId() + "." + users.get(i).getuName());
                        assert repair != null;
                        if (users.get(i).getuId().equals(repair.getuId())) {
                            userCode = i;
                        }
                    }
                    supportNames = new ArrayList<>();
                    supportNames.add("无");
                    url = "http://" + getResources().getString(R.string.DNS) + ":8080//support/selectAll";
                    List<Support> supports = MyRequest.getSupports(url);
                    for (int i = 0; i < Objects.requireNonNull(supports).size(); i++) {
                        supportNames.add(supports.get(i).getsId() + "." + supports.get(i).getsName());
                        assert repair != null;
                        if (supports.get(i).getsId().equals(repair.getsId())) {
                            supportCode = i + 1;
                        }
                    }
                    AddControl.addSpinner(R.id.spinner_1, "报修人：", userNames, userCode,this,handler,linearLayout);
                }
                assert repair != null;
                AddControl.addInfo(R.id.edit_text_1, "报修地点：", repair.getrPlace(),this,handler,linearLayout);
                AddControl.addInfo(R.id.edit_text_2, "报修内容：", repair.getrContent(),this,handler,linearLayout);
                if (authority.equals("admin")) {
                    AddControl.addTimeInfo(R.id.edit_text_3, "开始时间：", repair.getStart().toString(),this,handler,linearLayout);
                    AddControl.addSpinner(R.id.spinner_2, "维修人：", supportNames, supportCode,this,handler,linearLayout);
                    if (repair.getEnd() == null) AddControl.addTimeInfo(R.id.edit_text_4, "完成时间：", "",this,handler,linearLayout);
                    else AddControl.addTimeInfo(R.id.edit_text_4, "完成时间：", repair.getEnd().toString(),this,handler,linearLayout);
                    int rating;
                    if (repair.getStar() == null) rating = 0;
                    else rating = repair.getStar();
                    AddControl.addRating(R.id.rating_bar_1,"星级：",rating,this,handler,linearLayout);
                    String evaluation;
                    if (repair.getEvaluation() == null) evaluation = "";
                    else evaluation = repair.getEvaluation();
                    AddControl.addInfo(R.id.edit_text_5,"留言：",evaluation,this,handler,linearLayout);
                    AddControl.addHint("提示：只有完成时间不为空时星级和留言才能成功保存",this,handler,linearLayout);
                }
                break;
            }
            case "admin": {
                String url = "http://" + getResources().getString(R.string.DNS) + ":8080//admin/info?a_id=" + id;
                Admin admin = MyRequest.getAdmin(url);
                assert admin != null;
                AddControl.addInfo(R.id.edit_text_1, "姓名：", admin.getaName(),this,handler,linearLayout);
                break;
            }
            default: {
                Looper.prepare();
                Toast.makeText(EditInfo.this, "身份识别错误！请重试！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
    }

    //提交信息
    private void savaInfo() {
        String url = "http://" + getResources().getString(R.string.DNS) + ":8080//" + type + "/update";
        JSONObject json = new JSONObject();
        switch (type) {
            case "user": {
                EditText uname = this.findViewById(R.id.edit_text_1);
                Validation.editTextNotBlank(uname,this,"姓名");
                EditText upassword = null;
                if (authority.equals("admin")) {
                    upassword = this.findViewById(R.id.edit_text_3);
                    Validation.editTextNotBlank(upassword,this,"密码");
                }
                EditText utel = this.findViewById(R.id.edit_text_2);
                Validation.telPattern(utel,this);
                Spinner usexText = this.findViewById(R.id.spinner_1);
                Spinner utypeText = this.findViewById(R.id.spinner_2);
                int usex = usexText.getSelectedItemPosition();
                int utype = utypeText.getSelectedItemPosition();
                try {
                    json.put("uid", id);
                    json.put("uname", uname.getText());
                    if (authority.equals("admin")) {
                        assert upassword != null;
                        json.put("upassword", upassword.getText());
                    }
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
                Validation.editTextNotBlank(sname,this,"姓名");
                EditText spassword = null;
                if (authority.equals("admin")) {
                    spassword = this.findViewById(R.id.edit_text_3);
                    Validation.editTextNotBlank(spassword,this,"密码");
                }
                EditText stel = this.findViewById(R.id.edit_text_2);
                Validation.telPattern(stel,this);
                Spinner ssexText = this.findViewById(R.id.spinner_1);
                int ssex = ssexText.getSelectedItemPosition();
                try {
                    json.put("sid", id);
                    json.put("sname", sname.getText());
                    if (authority.equals("admin")) {
                        assert spassword != null;
                        json.put("spassword", spassword.getText());
                    }
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
                TextView start = null;
                TextView end = null;
                RatingBar star = null;
                EditText evaluation = null;
                if (authority.equals("admin")) {
                    Spinner uidText = this.findViewById(R.id.spinner_1);
                    String line = uidText.getSelectedItem().toString();
                    Pattern r = Pattern.compile("^\\d+");
                    Matcher m = r.matcher(line);
                    if (m.find()) {
                        uid = m.group();
                    }
                }
                EditText rplace = this.findViewById(R.id.edit_text_1);
                Validation.editTextNotBlank(rplace,this,"报修地点");
                EditText rcontent = this.findViewById(R.id.edit_text_2);
                Validation.editTextNotBlank(rcontent,this,"报修内容");
                if (authority.equals("admin")) {
                    start = this.findViewById(R.id.edit_text_3);
                    Spinner sidText = this.findViewById(R.id.spinner_2);
                    String line = sidText.getSelectedItem().toString();
                    Pattern r = Pattern.compile("(\\d+)(\\..*)");
                    Matcher m = r.matcher(line);
                    if (m.find()) sid = m.group(1);
                    end = this.findViewById(R.id.edit_text_4);
                    star = this.findViewById(R.id.rating_bar_1);
                    evaluation = this.findViewById(R.id.edit_text_5);
                }
                try {
                    json.put("rid", id);
                    if (authority.equals("admin")) json.put("uid", uid);
                    json.put("rplace", rplace.getText());
                    json.put("rcontent", rcontent.getText());
                    if (authority.equals("admin")) {
                        if (start != null) json.put("start", start.getText());
                        assert sid != null;
                        if (!sid.equals("无")) json.put("sid", sid);
                        assert end != null;
                        if (end.getText().toString().trim().length()!=0) {
                            json.put("end", end.getText());
                            if (star.getRating()!=0) json.put("star",star.getRating());
                            if (evaluation.getText().toString().trim().length()!=0) json.put("evaluation",evaluation.getText());
                        }
                    }
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                break;
            }
            case "admin": {
                EditText aname = this.findViewById(R.id.edit_text_1);
                Validation.editTextNotBlank(aname,this,"姓名");
                try {
                    json.put("aid", id);
                    json.put("aname", aname.getText());
                } catch (JSONException jsonException) {
                    jsonException.printStackTrace();
                }
                break;
            }
            default: {
                Looper.prepare();
                Toast.makeText(EditInfo.this, "身份识别错误！请重试！", Toast.LENGTH_SHORT).show();
                Looper.loop();
            }
        }
        String res = MyRequest.post(url, json);
        Looper.prepare();
        Toast.makeText(EditInfo.this, res, Toast.LENGTH_SHORT).show();
        assert res != null;
        if (res.matches(".*成功.*"))
        {
            //返回并刷新
            setResult(1);
            finish();
            ActivityCollector.removeActivity(this);
        }
        Looper.loop();
    }
}
