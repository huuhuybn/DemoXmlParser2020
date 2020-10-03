package vn.poly.edu.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private List<TinTuc> tinTucList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tinTucList = new ArrayList<>();
    }

    public void loadData(View view) {

        Log.e("ABC", "CHAY CHAY");
        // 1 : khoi tao thread mới
        // 2 : lẩy dữ liệu dạng InputStream
        // 3 : đưa InputStream vào XmlPullParser rồi bóc tác theo thẻ (tag)
        AsyncTask asyncTask = new AsyncTask() {
            // phương thức xử lý trong luồng
            @Override
            protected Object doInBackground(Object[] objects) {
                String link = "https://vnexpress.net/rss/tin-moi-nhat.rss";
                // String vtc = https://vtc.vn/rss/feed.rss
                // String vietnamnet = https://vietnamnet.vn/rss/thoi-su-chinh-tri.rss
                // String dantri = https://dantri.com.vn/trangchu.rss
                
                Log.e("ABC", "CHAY1 CHAY1");
                try {
                    URL url = new URL(link);
                    HttpURLConnection httpURLConnection =
                            (HttpURLConnection)
                                    url.openConnection();

                    InputStream inputStream = httpURLConnection.getInputStream();
                    Log.e("ABC", "CHAY2 CHAY2");
                    // khoi tao xmlpullParser
                    XmlPullParserFactory xmlPullParserFactory =
                            XmlPullParserFactory.newInstance();
                    xmlPullParserFactory.setNamespaceAware(false);

                    XmlPullParser xmlPullParser = xmlPullParserFactory.newPullParser();
                    xmlPullParser.setInput(inputStream, "utf-8");

                    int eventType = xmlPullParser.getEventType();

                    TinTuc tinTuc = null;
                    String text = null;

                    while (eventType != XmlPullParser.END_DOCUMENT) {

                        // lay ra ten tag
                        String tag = xmlPullParser.getName();

                        switch (eventType) {
                            case XmlPullParser.START_TAG:
                                if (tag.equalsIgnoreCase("item")) {
                                    tinTuc = new TinTuc();
                                }
                                break;
                            case XmlPullParser.TEXT:
                                text = xmlPullParser.getText();
                                break;
                            case XmlPullParser.END_TAG:
                                if (tinTuc != null) {
                                    if (tag.equalsIgnoreCase("title")) {
                                        tinTuc.title = text;
                                    }
                                    if (tag.equalsIgnoreCase("description")) {
                                        tinTuc.description = text;
                                    }
                                    if (tag.equalsIgnoreCase("link")) {
                                        tinTuc.link = text;
                                    }
                                    if (tag.equalsIgnoreCase("pubDate")) {
                                        tinTuc.pubDate = text;
                                    }
                                    if (tag.equalsIgnoreCase("item")) {
                                        tinTucList.add(tinTuc);
                                    }
                                }
                                break;

                        }
                        // sau khi doc the xong, thi doc toi the tiep theo
                        eventType = xmlPullParser.next();

                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Log.e("ABC", e.getMessage());
                } catch (IOException e) {
                    e.printStackTrace();

                    Log.e("ABC", e.getMessage());
                } catch (XmlPullParserException e) {
                    e.printStackTrace();

                    Log.e("ABC", e.getMessage());
                }

                return null;
            }
            // phương thức gọi sau khi kết thúc luồng


            @Override
            protected void onPostExecute(Object o) {
                super.onPostExecute(o);

                // Nhiệm vụ : viết giao diện hiển thị danh sách tin tức,
                // khi bấm vào tin tức thì mở ra trình duyệt

                Log.e("SIZE", "" + tinTucList.size());
            }
        };
        // thực thi
        asyncTask.execute();
    }
}