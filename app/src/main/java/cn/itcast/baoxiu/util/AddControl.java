package cn.itcast.baoxiu.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;

import java.util.List;

import cn.itcast.baoxiu.Info;
import cn.itcast.baoxiu.Manage;
import cn.itcast.baoxiu.R;

public class AddControl {
    //添加一行文本
    public static void addInfo(String text,Context context,Handler handler,LinearLayout linearLayout) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(24);
        textView.setPadding(10, 10, 10, 10);
        handler.post(() -> linearLayout.addView(textView));
    }

    //添加一行提示
    public static void addHint(String text,Context context,Handler handler,LinearLayout linearLayout) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(15);
        textView.setPadding(10, 10, 10, 10);
        handler.post(() -> linearLayout.addView(textView));
    }

    //添加一个输入框和对应提示文字
    public static void addInfo(int editTextId, String text, Context context, Handler handler, LinearLayout linearLayout) {
        LinearLayout linear = new LinearLayout(context);
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(24);
        EditText editText = new EditText(context);
        editText.setTextSize(24);
        editText.setId(editTextId);
        editText.setEms(20);
        linear.addView(textView);
        linear.addView(editText);
        linear.setPadding(10, 10, 10, 10);
        handler.post(() -> linearLayout.addView(linear));
    }

    //添加一个输入框和对应提示文字，并填上内容
    public static void addInfo(int editTextId, String text, String value,Context context,Handler handler,LinearLayout linearLayout) {
        LinearLayout linear = new LinearLayout(context);
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(24);
        EditText editText = new EditText(context);
        editText.setText(value);
        editText.setTextSize(24);
        editText.setId(editTextId);
        editText.setEms(20);
        linear.addView(textView);
        linear.addView(editText);
        linear.setPadding(10, 10, 10, 10);
        handler.post(() -> linearLayout.addView(linear));
    }
    //添加一个下拉选项，内容在xml中
    @SuppressLint("ResourceType")
    public static void addSpinner(int spinnerId, String text, int resource,Context context,Handler handler,LinearLayout linearLayout) {
        LinearLayout linear = new LinearLayout(context);
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(24);
        Spinner spinner = new Spinner(context);
        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, context.getResources().getStringArray(resource));
        spinner.setAdapter(arrayAdapter);
        spinner.setId(spinnerId);
        linear.addView(textView);
        linear.addView(spinner);
        linear.setPadding(10, 10, 10, 10);
        handler.post(() -> linearLayout.addView(linear));
    }

    //添加一个下拉选项，内容在xml中，并指定选项
    @SuppressLint("ResourceType")
    public static void addSpinner(int spinnerId, String text, int resource, int position,Context context,Handler handler,LinearLayout linearLayout) {
        LinearLayout linear = new LinearLayout(context);
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(24);
        Spinner spinner = new Spinner(context);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, context.getResources().getStringArray(resource));
        spinner.setAdapter(arrayAdapter);
        spinner.setId(spinnerId);
        spinner.setSelection(position);
        linear.addView(textView);
        linear.addView(spinner);
        linear.setPadding(10, 10, 10, 10);
        handler.post(() -> linearLayout.addView(linear));
    }

    //添加一个下拉选项，内容动态生成
    @SuppressLint("ResourceType")
    public static void addSpinner(int spinnerId, String text, List<CharSequence> resource,Context context,Handler handler,LinearLayout linearLayout) {
        LinearLayout linear = new LinearLayout(context);
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(24);
        Spinner spinner = new Spinner(context);
        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, resource);
        spinner.setAdapter(arrayAdapter);
        spinner.setId(spinnerId);
        linear.addView(textView);
        linear.addView(spinner);
        linear.setPadding(10, 10, 10, 10);
        handler.post(() -> linearLayout.addView(linear));
    }

    //添加一个下拉选项，内容动态生成，并指定选项
    @SuppressLint("ResourceType")
    public static void addSpinner(int spinnerId, String text, List<CharSequence> resource,int position,Context context,Handler handler,LinearLayout linearLayout) {
        LinearLayout linear = new LinearLayout(context);
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(24);
        Spinner spinner = new Spinner(context);
        ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter<>(context, R.layout.simple_spinner_item, resource);
        spinner.setAdapter(arrayAdapter);
        spinner.setId(spinnerId);
        spinner.setSelection(position);
        linear.addView(textView);
        linear.addView(spinner);
        linear.setPadding(10, 10, 10, 10);
        handler.post(() -> linearLayout.addView(linear));
    }

    //添加一个时间选项
    public static void addTimeInfo(int textViewId, String text, Activity activity,Handler handler,LinearLayout linearLayout) {
        LinearLayout linear = new LinearLayout(activity);
        TextView textView = new TextView(activity);
        textView.setText(text);
        textView.setTextSize(24);
        TextView dateAndTime = new TextView(activity);
        dateAndTime.setId(textViewId);
        dateAndTime.setTextSize(24);
        dateAndTime.setEms(20);
        dateAndTime.setClickable(true);
        dateAndTime.setOnClickListener(view -> {
            DateAndTime.showTimePickerDialog(activity, dateAndTime);
            DateAndTime.showDatePickerDialog(activity, dateAndTime);
        });
        linear.addView(textView);
        linear.addView(dateAndTime);
        linear.setPadding(10, 10, 10, 10);
        handler.post(() -> linearLayout.addView(linear));
    }

    //添加一个时间选项，并填好时间
    public static void addTimeInfo(int textViewId, String text, String time,Activity activity,Handler handler,LinearLayout linearLayout) {
        LinearLayout linear = new LinearLayout(activity);
        TextView textView = new TextView(activity);
        textView.setText(text);
        textView.setTextSize(24);
        TextView dateAndTime = new TextView(activity);
        dateAndTime.setId(textViewId);
        dateAndTime.setText(time);
        dateAndTime.setTextSize(24);
        dateAndTime.setEms(20);
        dateAndTime.setClickable(true);
        dateAndTime.setOnClickListener(view -> {
            DateAndTime.showTimePickerDialog(activity, dateAndTime);
            DateAndTime.showDatePickerDialog(activity, dateAndTime);
        });
        linear.addView(textView);
        linear.addView(dateAndTime);
        linear.setPadding(10, 10, 10, 10);
        handler.post(() -> linearLayout.addView(linear));
    }

    //动态生成一条报修单
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    public static void addRepair(String state, String content, String rid, Context context, Bundle bundle, ActivityResultLauncher<Intent> intentActivityResultLauncher,Handler handler,LinearLayout linearLayout) {
        TextView text = new TextView(context);
        text.setText(state + ":" + content);
        text.setTextSize(24);
        text.setPadding(10, 10, 10, 10);
        text.setSingleLine(true);
        text.setEllipsize(TextUtils.TruncateAt.END);
        text.setClickable(true);
        text.setOnClickListener(v -> {
            Intent intent = new Intent(context, Info.class);
            bundle.putString("id", rid);
            bundle.putString("type", "repair");
            bundle.putString("state", state);
            intent.putExtras(bundle);
            intentActivityResultLauncher.launch(intent);
        });
        handler.post(() -> linearLayout.addView(text));
    }

    //生成一条新报修单
    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    public static void addNewRepair(String id,String content, String rid,Context context,Bundle bundle,ActivityResultLauncher<Intent> intentActivityResultLauncher,Handler handler,LinearLayout linearLayout) {
        TextView text = new TextView(context);
        text.setText(content);
        text.setTextSize(24);
        text.setPadding(10, 10, 10, 10);
        text.setSingleLine(true);
        text.setEllipsize(TextUtils.TruncateAt.END);
        text.setClickable(true);
        text.setOnClickListener(v -> {
            Intent intent = new Intent(context, Info.class);
            bundle.putString("id", rid);
            bundle.putString("sid",id);
            bundle.putString("type","repair");
            bundle.putString("state","新报修单");
            intent.putExtras(bundle);
            intentActivityResultLauncher.launch(intent);
        });
        handler.post(() -> linearLayout.addView(text));
    }

    //添加一行可点击文本
    public static void addManage(String type,String text,Context context,Bundle bundle,ActivityResultLauncher<Intent> intentActivityResultLauncher,Handler handler,LinearLayout linearLayout){
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(48);
        textView.setPadding(10, 10, 10, 10);
        textView.setClickable(true);
        textView.setOnClickListener(v -> {
            Intent intent = new Intent(context, Manage.class);
            bundle.putString("type", type);
            intent.putExtras(bundle);
            intentActivityResultLauncher.launch(intent);
        });
        handler.post(()-> linearLayout.addView(textView));
    }

    public static void addRating(String text,Integer rating,Context context,Handler handler,LinearLayout linearLayout){
        LinearLayout linear = new LinearLayout(context);
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(24);
        RatingBar ratingBar = new RatingBar(context);
        ratingBar.setRating(rating);
        ratingBar.setIsIndicator(true);
        linear.addView(textView);
        linear.addView(ratingBar);
        linear.setPadding(10, 10, 10, 10);
        handler.post(() -> linearLayout.addView(linear));
    }

    public static void addRating(int ratingBarId,String text,Integer rating,Context context,Handler handler,LinearLayout linearLayout){
        LinearLayout linear = new LinearLayout(context);
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextSize(24);
        RatingBar ratingBar = new RatingBar(context);
        ratingBar.setId(ratingBarId);
        ratingBar.setStepSize(1);
        ratingBar.setRating(rating);
        ratingBar.setIsIndicator(false);
        linear.addView(textView);
        linear.addView(ratingBar);
        linear.setPadding(10, 10, 10, 10);
        handler.post(() -> linearLayout.addView(linear));
    }
}
