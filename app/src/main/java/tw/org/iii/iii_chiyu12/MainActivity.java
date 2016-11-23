package tw.org.iii.iii_chiyu12;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.util.List;


//  1. 網路要開權限
//  2. 網路要在執行緒或背景service 不然會出現 NetworkOnMainThreadException
//  3. ALIAS 10.0.3.2

public class MainActivity extends AppCompatActivity {
    public byte[] buf = "Hello, Brad".getBytes();
    private TextView tv;
    private ImageView iv;

    private UIHandler UIH;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
        iv = (ImageView) findViewById(R.id.IV);
        UIH = new UIHandler();

        // 因為要開外部存取空間 需要引用權限
        permition();
    }


    public void test1(View v){

        // 1. 創造一個執行緒
        // new Thread()

        // 2. 讓執行緒跑起來
        //new Thread().start();

        // 3. 這個執行緒 要幹嘛呢？
        //new Thread(){...}.start();

        // 4. 要修改裏面的 run方法
//        new Thread(){
//            @Override
//            public void run() {
//                super.run();
//            }
//        }.start();

        new Thread(){
            @Override
            public void run() {
                // ==================================
                try {
                    DatagramSocket socket = new DatagramSocket();
                    DatagramPacket packet = new DatagramPacket(
                            buf, buf.length, InetAddress.getByName("10.2.1.133"), 8888);
                    socket.send(packet);
                    socket.close();

                    Log.v("chiyu","Send OK");
                }catch (Exception e){
                    Log.v("chiyu",e.toString());
                }
                // ==================================
            }
        }.start();
    }

    public void test2(View v){
        new Thread(){
            @Override
            public void run() {
                try {
                    Socket socket = new Socket(InetAddress.getByName("10.0.3.2"), 9999);
                    socket.close();
                    Log.v("brad", "TCP Client OK");
                }catch (Exception e){
                    Log.v("brad", e.toString());
                }
            }
        }.start();
    }

    public void test3(View v){
        new Thread(){
            @Override
            public void run() {

                try {
                    URL url = new URL("http://www.iii.org.tw/");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    BufferedReader reader =
                            new BufferedReader(
                                    new InputStreamReader(
                                            conn.getInputStream()));

                    String line;
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null){
                        sb.append(line + "\n");
                        // Log.i("chiyu", line);
                    }
                    reader.close();

                    //
                    Bundle b1 = new Bundle();
                    b1.putCharSequence("myKey",sb);
                    //
                    Message msg = new Message();
                    msg.setData(b1);
                    msg.what = 0;
                    //
                    UIH.sendMessage(msg);

                }catch (Exception e){
                    Log.v("chiyu",e.toString());
                }

            }
        }.start();
    }

    public void test4(View v){
        new Thread(){

            @Override
            public void run() {
                try {
                    URL Purl = new URL("http://blogs-images.forbes.com/brittanyhodak/files/2016/10/snoopy_for_president_peanuts_button-11.jpg");
                    HttpURLConnection conn = (HttpURLConnection) Purl.openConnection();
                    conn.connect();

                    InputStream is = conn.getInputStream();
                    Bitmap bmp = BitmapFactory.decodeStream(is);
                    //
                    Bundle b1 = new Bundle();
                    b1.putParcelable("myPic",bmp);
                    //
                    Message msg = new Message();
                    msg.setData(b1);
                    msg.what = 1;
                    //
                    UIH.sendMessage(msg);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private Bitmap bmp1;
    public void test5(View v){
        new Thread(){

            @Override
            public void run() {
                try {
                    URL Purl = new URL("http://fc09.deviantart.net/fs15/f/2007/049/c/4/Snoopy_and_Woodstock_by_stridzio.jpg");
                    HttpURLConnection conn = (HttpURLConnection) Purl.openConnection();
                    conn.connect();

                    bmp1 = BitmapFactory.decodeStream(conn.getInputStream());
                    UIH.sendEmptyMessage(2);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private class UIHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    tv.setText(msg.getData().getCharSequence("myKey"));
                    break;
                case 1:
                    iv.setImageBitmap((Bitmap) msg.getData().getParcelable("myPic"));
                    break;
                case 3:
                    pDialog.dismiss();
                    break;
            }
        }
    }

    // ===============================================

    // php add member
    public void test6(View v){
        new Thread(){
            @Override
            public void run() {
                try{
//                    Multipartutility mu = new Multipartutility("http://10.0.3.2/_Brad/add2.php","UTF-8");
                    Multipartutility mu = new Multipartutility("http://iiihw2-picard.c9users.io/_Brad/add2.php","UTF-8");
                    mu.addFormField("account","mark");
                    mu.addFormField("password","987");
                    List<String> ret = mu.finish();
                    Log.v("chiyu",ret.get(0));
                }catch (Exception e){
                    Log.v("chiyu",e.toString());
                }
            }
        }.start();
    }

    // php login
    public void test7(View v){

    }

    // ===============================================

    // 1. 權限詢問 ＆ 獲取
    public void permition(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    0);
        }

        Init();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Init();
    }
    // 2. 初始化
    private File sdroot,approot;
    private ProgressDialog pDialog;
    public void Init(){
        sdroot = Environment.getExternalStorageDirectory();
        Log.v("chiyu",sdroot + "");
        approot = new File(sdroot, "Android/data/" + getPackageName());
        if (!approot.exists()) {approot.mkdirs();}


        pDialog = new ProgressDialog(this);
        // 進度條 （能掌握執行進度時）
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        // 轉圈圈 （不能掌握進度時）
        pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

    }

    // Download 2 SD
    public void test8(View v){
        // 開啟特效
        pDialog.setMessage("Download...");
        pDialog.show();

        new Thread(){
            @Override
            public void run() {
                try{
                    String Durl = "www.gamer.com.tw";

                    // 連接外面
                    URL url = new URL("http://pdfmyurl.com/?url=" + Durl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    InputStream in = conn.getInputStream();

                    // 建立內部
                    File download = new File(approot,"chiyu.pdf");
                    FileOutputStream fout = new FileOutputStream(download);

                    // 寫入
                    byte[] buf = new byte[4096];
                    int len;
                    while ((len = in.read(buf)) != -1){
                        fout.write(buf,0,len);
                    }
                    fout.flush();
                    fout.close();

                    //
                    Log.v("chiyu","Download OK");
                    UIH.sendEmptyMessage(3);
                }catch (Exception e){
                    Log.v("chiyu","Download Fail : " + e.toString());
                    UIH.sendEmptyMessage(3);
                }
            }
        }.start();
    }

    // Upload 2 Server
    public void test9(View v){
        // 開啟特效
        pDialog.setMessage("Upload...");
        pDialog.show();

        new Thread(){
            @Override
            public void run() {
                try{
                    Multipartutility mu = new Multipartutility("http://iiihw2-picard.c9users.io/_Brad/add2.php","UTF-8");


                    mu.addFormField("account","gogo");
                    mu.addFormField("password","666");
                    Log.v("chiyu","Debug1");
                    //
                    File download = new File(approot,"chiyu.pdf");
                    mu.addFilePart("myUpload",download);

                    //
                    List<String> ret = mu.finish();
                    //  輸出所有訊息
                    while (ret.size() > 0) {
                        Log.v("chiyu",ret.remove(ret.size()-1));
                    }
                    UIH.sendEmptyMessage(3);


                }catch (Exception e){
                    Log.v("chiyu",e.toString());
                    UIH.sendEmptyMessage(3);
                }
            }
        }.start();
    }



}
