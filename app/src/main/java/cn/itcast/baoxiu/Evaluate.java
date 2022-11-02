package cn.itcast.baoxiu;

import android.os.Bundle;
import android.os.Looper;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;

import cn.itcast.baoxiu.util.ActivityCollector;
import cn.itcast.baoxiu.util.MyRequest;

public class Evaluate extends AppCompatActivity {
    //activity间传输
    private Bundle bundle;
    private String id;
    //控件
    RatingBar star;
    EditText evaluation;
    Button save;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.evaluate);
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
        star = this.findViewById(R.id.star);
        evaluation = this.findViewById(R.id.evaluation);
        save = this.findViewById(R.id.save_evaluation);
    }

    private void initEvent() {
        bundle = getIntent().getExtras();
        id = bundle.getString("id");
        save.setOnClickListener(view -> new Thread(this::saveEvaluation).start());
    }

    private void saveEvaluation() {
        JSONObject json = new JSONObject();
        try {
            json.put("rid", id);
            json.put("star", star.getRating());
            json.put("evaluation", evaluation.getText());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = "http://" + getResources().getString(R.string.DNS) + ":8080//repair/update";
        String res = MyRequest.post(url, json);
        Looper.prepare();
        assert res != null;
        if (res.matches(".*成功.*")) {
            Toast.makeText(this, "评价成功！", Toast.LENGTH_SHORT).show();
            setResult(2);
            finish();
            ActivityCollector.removeActivity(this);
        }else {
            Toast.makeText(this,res,Toast.LENGTH_SHORT).show();
        }
        Looper.loop();
    }
}
