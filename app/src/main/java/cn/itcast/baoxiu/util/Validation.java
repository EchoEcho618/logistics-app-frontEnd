package cn.itcast.baoxiu.util;

import android.content.Context;
import android.os.Looper;
import android.widget.EditText;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.itcast.baoxiu.Register;

public class Validation {
    public static void editTextNotBlank(EditText editText, Context context, String str){
        if (editText.getText().toString().trim().length()==0){
            Looper.prepare();
            Toast.makeText(context,str+"不能为空！",Toast.LENGTH_SHORT).show();
            Looper.loop();
            return;
        }
    }

    public static void telPattern(EditText editText,Context context){
        String line = editText.getText().toString();
        if (line.trim().length()==0){
            Looper.prepare();
            Toast.makeText(context,"电话不能为空！",Toast.LENGTH_SHORT).show();
            Looper.loop();
            return;
        }
        Pattern r = Pattern.compile("^\\d{0,11}$");
        Matcher m = r.matcher(line);
        if (!m.find()) {
            Looper.prepare();
            Toast.makeText(context,"电话格式错误！应为11位以内纯数字！",Toast.LENGTH_SHORT).show();
            Looper.loop();
            return;
        }
    }

    public static void reEtPwd(EditText et_password,EditText re_password,Context context){
        if (!et_password.getText().toString().equals(re_password.getText().toString())) {
            Looper.prepare();
            Toast.makeText(context, "两次密码不一致！", Toast.LENGTH_SHORT).show();
            Looper.loop();
            return;
        }
    }
}
