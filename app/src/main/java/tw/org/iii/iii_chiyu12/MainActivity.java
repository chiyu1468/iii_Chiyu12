package tw.org.iii.iii_chiyu12;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;


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
                            buf, buf.length, InetAddress.getByName("10.0.3.2"), 8888);
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
                case 2:
                    iv.setImageBitmap(bmp1);
                    break;
            }
        }
    }


}
