# SNetBus-master
Let your app observe the network state anywhere .
##### 简介
NetBus 是一个实时监听网络状态的一个框架，接入简单。只需要几步就可以在 Activity 和 Fragment 中很方便的收到网络状态变化的通知。从根本上来说这个框架是在 EventBus 基础上做的更改，使用的方式上差别不是很大。应该是细化了通知的功能，方便于 Android 的开发。
##### 愿景
- 可以在 App 任意位置监听网络的变化
- 监听者可以指定不同的网络环境
- 布局封装，简化布局状态切换
##### 使用
**添加依赖**
1. Gradle Groovy DSL ：
```
implementation 'com.github.meetsl.netbus:SNetBus:1.1'
```
2. Maven
```
<dependency>
  <groupId>com.github.meetsl.netbus</groupId>
  <artifactId>SNetBus</artifactId>
  <version>1.1</version>
  <type>aar</type>
</dependency>
```
**初始化**
在项目最开始的地方需要对 NetBus 进行初始化，例如 Application 或者 启动页中初始化。初始化代码如下：
```
//初始化 NetBus（必须）
NetBus.init(this)
//全局设置公共布局(可选)
NetBus.initNetView(loadResId = R.layout.layout_loading_view, netErrorResId = R.layout.layout_net_error_view)
```
**添加监听**
- *Activity 或者 Fragment 中添加监听*
>在 `Activity` 或者 `Fragment`中注册该接收者，在 `onCreate()` 或者 `onCreateView()` 中添加注册：
>```
>NetBus.getDefault().register(this)
>```
>然后需要在类中添加一个只有一个 boolean 类型参数的方法并添加注解：
>```
>/**
> *  接受网络状态变化通知的方法
> *
> *  @param isAvailable : true 当前 NetMode 下网络可用；false 无网络状态，与 NetMode 无关
> */
>@NetSubscribe(netMode = NetMode.WIFI, threadMode = ThreadMode.MAIN, priority = 1)
>fun onNetEvent(isAvailable: Boolean) {
>    Log.i("Callback_Network", "${this.javaClass} $name 网络变化了")
>}
>```
>OK，Activity 或者 Fragment 对网络的监听这样子就可以了。如果你的项目中 Activity 或者 Fragment 中有父类，则注册的这个操作放在父类中也是可以的。
- *在其他位置中监听网络变化*
>在代码中新建一个 `NetBusTest` 的测试类，示例代码完整的展示了如何进行监听：
>```
>public class NetBusTest {
>
>    /**
>     * 需要在适当的时机，比如类对象创建的时候就进行注册
>     */
>    public void run() {
>        NetBus.Companion.getDefault().register(this);
>    }
>
>    /**
>     * 接收网络状态变化，这里监听的是移动数据网络
>     *
>     * @param isAvailable true 当前 NetMode 下网络可用；false 无网络状态，与 NetMode 无关
>     */
>    @NetSubscribe(netMode = NetMode.CELLULAR, threadMode = ThreadMode.MAIN, priority = 1)
>    public void onEvent(boolean isAvailable) {
>        Log.i("Callback_Network", "NetBusTest ---- 网络变化了");
>    }
>
>    /**
>     * 在类对象不再被使用或者销毁的时候，进行解注册
>     */
>    public void stop() {
>        NetBus.Companion.getDefault().unregister(this);
>    }
>}
>```
>在类中添加了一个 `run()` 和一个 `stop()` 方法用于注册和解注册，`onEvent` 方法用于接收网络状态变化。与 `Activity` 或者 `Fragment` 不一样的地方就是需要我们自己手动解注册，这一点需要在使用的时候注意一下。
##### 辅助开发
在该框架中还提供了一个用于辅助开发的布局  `PageStateLayout` ,在一个正常的业务 App 中，一个 Page 中一般会有三到四种的状态布局，比如 EmptyLayout (无数据视图) 、NetErrorLayout（网络错误视图）、LoadLayout（加载视图）以及 NormalLayout（正常显示视图）。这个辅助布局有一定的限制，大家可以根据场景决定使不使用。下面说一下这个布局的具体使用：  

**1. 设置全局状态布局**
>比如我们的 App 设计的时候，网络错误的视图都是统一的，那么我们可以将该试图设置为全局视图。这样每一个 `PageStateLayout` 对象在显示网络错误视图时，显示的都是一个：(在 App 启动页或者 Application 中设置)
>```
>//全局设置公共布局
>NetBus.initNetView(netErrorResId = R.layout.layout_net_error_view)
>```
**2. 设置局部状态布局**
> 由于每一个 Page 在同一状态下布局可能不一致，所以提供了局部设置：
>```
>private lateinit var stateLayout: PageStateLayout
>
>    override fun onCreate(savedInstanceState: Bundle?) {
>        super.onCreate(savedInstanceState)
>        stateLayout = PageStateLayout(this)
>        //设置正常显示视图
>        stateLayout.setNormalView(R.layout.activity_main)
>        //设置该 Page 的 空视图
>        stateLayout.setEmptyView(R.layout.layout_empty_default)
>        //设置该 Page 的加载视图
>        stateLayout.setLoadingView(R.layout.layout_loading_view)
>        //设置该 Page 的网络错误视图
>        stateLayout.setNetErrorView(R.layout.layout_net_error_view)
>        //显示
>        setContentView(stateLayout)
>        NetBus.getDefault().register(this)
>    }
>```
>示例代码说明了如何设置局部视图，以及显示使用

**3.显示调用**
>将 `PageStateLayout` 设置给 Page 后如何控制显示布局，很简单：
>```
>@NetSubscribe(netMode = NetMode.WIFI, threadMode = ThreadMode.POSTING, priority = 1)
>    fun onEvent(isAvailable: Boolean) {
>        println("网络变化了")
>        if (!isAvailable)
>            stateLayout.showNetErrorView()
>        else
>            stateLayout.showNormalView()
>        Log.i("Callback_Network", "MainActivity ----$isAvailable 网络变化了")
>    }
>```
> `PageStateLayout` 提供了 `showXXXX` 的方法来显示布局

#####总结
到这里该框架基本就介绍完了，希望它可以在你的项目中，很方便的帮助你监听网络状态。如果在使用中有什么问题或者建议，能告诉我那就太棒了。框架会持续更新，这里放上框架的 github 地址，欢迎 star 以及 comment 。
