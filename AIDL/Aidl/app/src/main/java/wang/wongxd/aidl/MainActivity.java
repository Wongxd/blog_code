package wang.wongxd.aidl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;

import wang.wongxd.aidl.bean.Book;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "王晓东的aidl 客户端 " + this.getClass().getSimpleName() + "  ";
    //由AIDL文件生成的Java类
    private BookManager mBookManager = null;

    //标志当前与服务端连接状况的布尔值，false为未连接，true为连接中
    private boolean mBound = false;

    //包含Book对象的list
    private List<Book> mBooks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private int flag = 0;

    /**
     * 按钮的点击事件，点击之后调用服务端的addBookIn方法
     *
     * @param view
     */
    public void addBook(View view) {
        //如果与服务端的连接处于未连接状态，则尝试连接
        if (!mBound) {
            attemptToBindService();
            Toast.makeText(this, "当前与服务端处于未连接状态，正在尝试重连，请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mBookManager == null) return;

        Book book = new Book();
        book.setName("APP研发录In--" + flag);
        book.setPrice(30 + flag);
        ++flag;
        try {
            mBookManager.addBook(book);
            Log.e(TAG, book.toString() + "  " + book.getName() + "  " + book.getPrice());
            Toast.makeText(this, book.getName()+"  添加book", Toast.LENGTH_SHORT).show();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }


    public void getBooks(View v) {
        if (!mBound) {
            attemptToBindService();
            Toast.makeText(this, "当前与服务端处于未连接状态，正在尝试重连，请稍后再试", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mBookManager == null) return;

        try {
            List<Book> books = mBookManager.getBooks();
            for (Book b : books) {
                Log.e(TAG, b.getName());
            }
            Toast.makeText(this, books.toString()+"  服务器的books", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 尝试与服务端建立连接
     */
    private void attemptToBindService() {

        Intent i = new Intent();
        i.setComponent(new ComponentName("wang.wongxd.aidlservice","wang.wongxd.aidlservice.AIDLService") );
        startService(i);

//        Intent intent = new Intent();
//        intent.setAction("wang.wongxd.aidl");
//        intent.setPackage("wang.wongxd.aidlservice");
        bindService(i, mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!mBound) {
            attemptToBindService();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBound) {
            unbindService(mServiceConnection);
            mBound = false;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e(TAG, "service connected");
            Toast.makeText(MainActivity.this, "服务器连接", Toast.LENGTH_SHORT).show();
            mBookManager = BookManager.Stub.asInterface(service);
            mBound = true;

            if (mBookManager != null) {
                try {
                    mBooks = mBookManager.getBooks();
                    Log.e(TAG, mBooks.toString());
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "service disconnected");
            mBound = false;
        }
    };
}
