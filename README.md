# UI-Thread
## 首先，先给个结论  在子线程是不能更新UI的，确切的来说，是子线程是不能直接更新UI。
### 在子线程是可以通过执行一些代码来通知主线程，告诉主线程去更新UI，在子线程发送消息到UI线程，通知UI线程更新UI，还有 handler.post(Runnable r)、view.post(Runnable r)、activity.runOnUIThread(Runnable r)等方法
##### 至于为什么这段代码没有报错
```java
    private TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.tv);

        new Thread(new Runnable() {
            @Override
            public void run() {
                tv.setText("子线程可以更新UI吗?");
            }
        }).start();
    }
```

这是因为你的Thread执行的时候，ViewRootImpl还没有对view tree的根节点DecorView执行performTraversals，view tree里的所有View都没有被赋值mAttachInfo。在onCreate完成时，Activity并没有完成初始化view tree。view tree的初始化是从ViewRootImpl执行performTraversals开始，这个过程会对view tree进行从根节点DecorView开始的遍历，对所有视图完成初始化，初始化包括视图的大小布局，以及AttachInfo，ViewParent等域的初始化。执行ImageView.setImageResource，调用的过程是ImageView.setImageResource </br>
-> View.invalidate </br>
-> View.invalidateInternal </br>
-> ViewGroup.invalidateChild</br>
-> ViewParent.invalidateChildInParent //这里会不断Loop去取上一个结点的mParent</br>
-> ViewRootImpl.invalidateChildInParent //DecorView的mParent是ViewRootImpl</br>
-> ViewRootImpl.checkThread //在这里执行checkThread，如果非UI线程则抛出异常</br>
但是在Thread执行setImageResource时，此时Activity还在初始化，ViewRoot没有初始化整个view tree，ImageView的mAttachInfo是空的（mAttachInfo包含了Window的token等Binder）。而View.invalidateInternal调用ViewGroup.invalidateChild要判断是否存在ViewParent和AttachInfo：
```java
final AttachInfo ai = mAttachInfo;
final ViewParent p = mParent;
if (p != null && ai != null && l < r && t < b) {
    //....
    p.invalidateChild(this, damage);
}
```
也就是说，此时因为不存在ViewParent，invalidate的过程中止而没有完全执行，也即没有发生checkThread。

```java
 void checkThread() {
        if (mThread != Thread.currentThread()) {
            throw new CalledFromWrongThreadException(
                    "Only the original thread that created a view hierarchy can touch its views.");
        }
    }
```