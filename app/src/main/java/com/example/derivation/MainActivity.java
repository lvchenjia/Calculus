package com.example.derivation;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.method.ScrollingMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ScrollView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    MyHandler handler;
    TextView textView;
    AutoCompleteTextView editText;
    ScrollView scrollView;


    String urlSev="http://121.199.20.144:5000/";
    String lastresult = "";
    String posturl = "";
    String postpara = "";
    String command = "";
    SpannableStringBuilder text = new SpannableStringBuilder("");
    String[]  items={"time", "date/","fact/", "cal"};



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textview);
        scrollView = findViewById(R.id.scrollView);
        //textView.setMovementMethod(ScrollingMovementMethod.getInstance());
        editText = findViewById(R.id.editAuto);
        editText.setAdapter (new ArrayAdapter<String>(this ,android.R.layout.simple_dropdown_item_1line ,items ));
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    //runcode();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mymenu,menu);
        return true;
    }

    //???OptionsMenu?????????????????????????????????????????????
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){

            case R.id.derivate:
                derivate(editText.getText().toString());
                return true;
            case R.id.integrate:
                integrate(editText.getText().toString());
                return true;
            case R.id.limit:
                limit(editText.getText().toString());
                return true;
            case R.id.expand:
                expand(editText.getText().toString());
                return true;
            case R.id.simplify:
                simplify(editText.getText().toString());
                return true;
            case R.id.evalf:
                evalf(editText.getText().toString());
                return true;
            case R.id.solve:
                solve(editText.getText().toString());
                return true;
            case R.id.kuohao:
                int index = editText.getSelectionStart();
                Editable edit = editText.getEditableText();
                if (index < 0 || index > edit.length() ){
                    edit.append(text);
                }else{
                    edit.insert(index,"()");//??????????????????????????????
                }
                editText.setSelection(index+1);
                return true;
            case R.id.recent:
                int index1 = editText.getSelectionStart();
                Editable edit1 = editText.getEditableText();
                if (index1 < 0 || index1 > edit1.length() ){
                    edit1.append(lastresult);
                }else{
                    edit1.insert(index1,lastresult);//??????????????????????????????
                }
                editText.setSelection(index1+lastresult.length());
                return true;
            case R.id.clean:
                text = SpannableStringBuilder.valueOf("");
                editText.setText("");
                textView.setText("");
                return true;
            case R.id.about:
                print_b(" > About\n");
                print("??????2021/11/14??????\n??????:Thyme\n??????????????????\n");
                scrollDown();
                return true;
            default:
                //do nothing
        }
        return super.onOptionsItemSelected(item);
    }


//?????????????????????

    //???????????????????????????
    public void showRes(String res){
        Bundle bundle=new Bundle();
        bundle.putString("res",res);//bundle??????????????????????????????????????????????????????
        Message msg=handler.obtainMessage();//?????????????????????????????????
        msg.setData(bundle);
        handler.sendMessage(msg);//???handler????????????????????????
    }


    class MyHandler extends Handler {
        @Override
        //????????????????????????????????????
        public void handleMessage(Message msg) {
            Bundle bundle=msg.getData();
            editText.setText("");
            print_b(" > "+command);
            println("");
            if(bundle.get("res")==null) {
                print_r("Error!\n");
                scrollDown();
                return ;
            }
            lastresult = bundle.get("res").toString();
            lastresult = lastresult.trim();
            print(bundle.get("res").toString());
            scrollDown();

        }
    }

    public void scrollDown(){
        Handler handler1 = new Handler();
        handler1.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }
    public void print(String string){
        text.append(string);
        textView.setText(text);
    }

    public void println(String string){
        text.append(string+"\n");
        textView.setText(text);
    }

    public void print_b(String string){
        SpannableString spanColor = new SpannableString(string);
        spanColor.setSpan(new ForegroundColorSpan(Color.parseColor("#0000ff")), 0, string.length(), 0);
        text.append(spanColor);
        textView.setText(text);
    }

    public void print_r(String string){
        SpannableString spanColor = new SpannableString(string);
        spanColor.setSpan(new ForegroundColorSpan(Color.parseColor("#ff0000")), 0, string.length(), 0);
        text.append(spanColor);
        textView.setText(text);
    }

    class GetThread extends Thread{

        private String geturl = "";
        public void setUrl(String url){
            geturl = url;
        }


        public void run(){
            String r = HttpClient.doGet(geturl);
            showRes(r);
        }
    }

    class PostThread extends Thread{

        public void run(){
            String r = HttpClient.doPost(posturl,postpara);
            showRes(r);
        }
    }

    public void calc(String cmd, String op,String exp){
        command = cmd+" "+editText.getText();
        posturl = urlSev+op;
        postpara = "{\"exp\":\""+exp+"\"}";
        handler=new MyHandler();
        new PostThread().start();
    }

    public void derivate(String exp){
        command = "d "+editText.getText();
        posturl = urlSev+"diff";
        postpara = "{\"exp\":\""+exp+"\"}";
        handler=new MyHandler();
        new PostThread().start();
    }

    public void limit(String exp){
        command = "lim x->0  "+editText.getText();
        posturl = urlSev+"limit";
        postpara = "{\"exp\":\""+exp+"\"}";
        handler=new MyHandler();
        new PostThread().start();
    }

    public void expand(String exp){
        command = "expand  "+editText.getText();
        posturl = urlSev+"expand";
        postpara = "{\"exp\":\""+exp+"\"}";
        handler=new MyHandler();
        new PostThread().start();
    }

    public void simplify(String exp){
        command = "simplify  "+editText.getText();
        posturl = urlSev+"simplify";
        postpara = "{\"exp\":\""+exp+"\"}";
        handler=new MyHandler();
        new PostThread().start();
    }

    public void integrate(String exp){
        command = "integrate  "+editText.getText();
        posturl = urlSev+"integrate";
        postpara = "{\"exp\":\""+exp+"\"}";
        handler=new MyHandler();
        new PostThread().start();
    }

    public void evalf(String exp){
        command = "evalf  "+editText.getText();
        posturl = urlSev+"evalf";
        postpara = "{\"exp\":\""+exp+"\"}";
        handler=new MyHandler();
        new PostThread().start();
    }

    public void solve(String exp){
        command = "solve  "+editText.getText();
        posturl = urlSev+"solve";
        postpara = "{\"exp\":\""+exp+"\"}";
        handler=new MyHandler();
        new PostThread().start();
    }
}