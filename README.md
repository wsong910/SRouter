# ShomoRouter 1.0

## 功能

Activity 路由。

## 集成

### 1. 添加引用

#### 1.1 apt 方式
根目录 build.gradle

项目 app/build.gradle
``` groovy

dependencies {
    implementation project(':shimorouter')
    implementation project(':annotation')
    annotationProcessor project(':compiler')
}

```
### 2. 集成

在`AndroidManifest.xml`配置

``` xml
<activity
    android:name="chuxin.shimo.shimowendang.smrouter.core.RouterActivity"
    android:theme="@android:style/Theme.Translucent.NoTitleBar">

</activity>
```
需要在`Application#onCreate`中初始化

``` java
public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Routers.init(this);
        Routers.showLog(true);//打开打印
    }
}
```

在需要配置的`Activity`上添加注解

``` java
@Router("test")
public class TestActivity extends Activity {
	...
}
```
这样就可以通过`test`或者'/test'来打开`TestActivity`了。

### 注意

也可以这样写

``` java
@Router("/test")
public class TestActivity extends Activity {
	...
}
```
这样就可以通过`test`或者'/test'来打开`TestActivity`了。

### 支持配置多个地址

``` java
@Router({"main", "root"})
```

`main`和`root`都可以访问到同一个`Activity`


### 支持获取 url 中`?`传递的参数

``` java
@Router("main")
```
上面的配置，可以通过`/main?color=0xff878798&name=you+are+best`(`shimo://main?color=0xff878798&name=you+are+best`)来传递参数，在`TestActivity#onCreate`中通过`getIntent().getStringExtra("name")`的方式来获取参数，所有的参数默认为`String`类型，但是可以通过配置指定参数类型。

### 支持在 path 中定义参数

``` java
@Router("main/:color")
```

通过`:color`的方式定义参数，参数名为`color`，访问`/main/0xff878798`(`shimo://main/0xff878798`)，可以在`TestActivity#onCreate`通过`getIntent().getStringExtra("color")`获取到 color 的值`0xff878798`

### 支持多级 path 参数

``` java
@Router("user/:userId/:topicId/:commentId")

@Router("user/:userId/topic/:topicId/comment/:commentId")
```

上面两种方式都是被支持的，分别定义了三个参数，`userId`,`topicId`,`commentId`


### 支持指定参数类型

``` java
@Router(value = "main/:color", intParams = "color")
```
这样指定了参数`color`的类型为`int`，在`TestActivity#onCreate`获取 color 可以通过`getIntent().getIntExtra("color", 0)`来获取。支持的参数类型有`int`,`long`,`short`,`byte`,`char`,`float`,`double`,`boolean`，默认不指定则为`String`类型。

### 支持优先适配

``` java
@Router("user/:userId")
public class TestActivity extends Activity {
	...
}

@Router("user/statistics")
public class Test1Activity extends Activity {
	...
}
```
假设有上面两个配置，

不支持优先适配的情况下，`/user/statistics`(`shimo://user/statistics`)可能会适配到`@Router("user/:userId")`，并且`userId=statistics`

支持优先适配，意味着，`/user/statistics`(`shimo://user/statistics`)会直接适配到`@Router("user/statistics")`，不会适配前一个`@Router("user/:userId")`

### 支持 Callback

``` java
RouterCallback
```

### 支持 Http(s) 协议

``` java
@Router({"http://shimo.im/main", "main"})
```

### 应用内调用

### 1. （同步）直接打开路由，忽略拦截器
``` java
Routers.getInstance().open(context, "/main/0xff878798")
Routers.getInstance().open(context, Uri.parse("shimo://main/0xff878798"))
Routers.getInstance().open(context, Uri.parse("shimo://main/0xff878798"),new RouterCallback())
Routers.getInstance().open(context, "shimo://main/0xff878798")
Routers.getInstance().open(context, "shimo://main/0xff878798",new RouterCallback())
Routers.getInstance().openForResult(activity, "/main/0xff878798", REQUEST_CODE);
Routers.getInstance().openForResult(activity, "/main/0xff878798", REQUEST_CODE,new RouterCallback());
Routers.getInstance().openForResult(activity, Uri.parse("/main/0xff878798"), REQUEST_CODE);
Routers.getInstance().openForResult(activity, Uri.parse("/main/0xff878798"), REQUEST_CODE,new RouterCallback());
// 如果需要添加数据，一种方式是在url后添加，另一种是在RouterCallback#RouterCallback#onFound中将bundle写入request中即可
```

### 2. 异步路由,建议使用的方式
``` java
Routers.getInstance().build("/main/0xff878798").navigation()
Routers.getInstance().build("/main/0xff878798").navigation(new RouterCallback())
Routers.getInstance().build("/main/0xff878798").navigation(context,new RouterCallback())
Routers.getInstance().build("/main/0xff878798").navigation(context,REQUEST_CODE)
Routers.getInstance().build("/main/0xff878798").navigation(context,REQUEST_CODE,new RouterCallback())
```
### 3. 同步路由
``` java
Routers.getInstance().build("/main/0xff878798").navigationSync()
Routers.getInstance().build("/main/0xff878798").navigationSync(new RouterCallback())
Routers.getInstance().build("/main/0xff878798").navigationSync(context,new RouterCallback())
Routers.getInstance().build("/main/0xff878798").navigationSync(context,REQUEST_CODE)
Routers.getInstance().build("/main/0xff878798").navigationSync(context,REQUEST_CODE,new RouterCallback())
```

### 拦截器
在需要配置的类中继承接口 chuxin.shimo.shimowendang.smrouter.interfaces.Interceptor 
``` java
public class TestInterceptor implements Interceptor {

    @Override
    public Observable<RouteResponse> intercept(@Nullable Context context,Chain chain) throws RuntimeException {
        
        return chain.proceed(chain.getRequest());
    }
}

```
并在需要拦截的Activity上添加注解
``` java
@Router(value = RouterTable.TEST1, interceptors = {TestInterceptor.class})
public class Test1Activity extends Activity {

}
```
可添加多个拦截器，拦截器的书写顺序即为 拦截顺序

``` java
@Router(value = RouterTable.TEST3, interceptors = {TestInterceptor1.class, TestInterceptor3.class})
public class Test3Activity extends Activity {

}
```

### 拦截器的处理方式

### 处理1
``` java
public class TestInterceptor3 implements Interceptor {

    @Override
    public Observable<RouteResponse> intercept(@Nullable Context context, Chain chain) throws RuntimeException {
    return new RouterResponseNext(chain) {
                @Override
                public RouteResponse onFailed(RouteResponse currentResponse) {
                    //失败回调
                    return null;
                }

                @Override
                public RouteResponse onSuccess(Intent data, Chain chain, RouteResponse currentResponse,
                    RouteRequest request) {
                    // data = null;
                    currentResponse.addData(new Bundle());
                    final RouteResponse response = chain.proceed(request);
                    response.setStatus(RouteStatus.SUCCEED);//成功并继续
                    return response;
                }
            }.cover();
}
```

### 处理2
``` java
public class TestInterceptor3 implements Interceptor {

    @Override
    public Observable<RouteResponse> intercept(@Nullable Context context, Chain chain) throws RuntimeException {
    currentResponse.addData(new Bundle());
        final RouteResponse response = chain.proceed(chain.getCurrentRequest());
        response.setStatus(RouteStatus.SUCCEED);//成功并继续
        return Observable.just(response);
}
```

### 处理3
``` java
public class TestInterceptor3 implements Interceptor {

    @Override
    public Observable<RouteResponse> intercept(@Nullable Context context, Chain chain) throws RuntimeException {
    final RouteResponse response = chain.proceed(chain.getCurrentRequest());
    response.setStatus(RouteStatus.FAILED);//失败
    return Observable.just(response);
}
```

### 处理4
``` java
public class TestInterceptor3 implements Interceptor {

    @Override
    public Observable<RouteResponse> intercept(@Nullable Context context, Chain chain) throws RuntimeException {
    return new RouterResponseNext(RouterTable.TEST1, null, chain) {
        @Override
        public RouteResponse onFailed(RouteResponse currentResponse) {
            currentResponse.setStatus(RouteStatus.FAILED);//失败
            return currentResponse;
        }

        @Override
        public RouteResponse onSuccess(@Nullable Intent data, Chain chain, RouteResponse currentResponse,
            RouteRequest request) {
            //成功
            if (data != null) {
                Bundle bundle = new Bundle();
                bundle.putString("test",data.getStringExtra("test"));
                currentResponse.addData(bundle);
            }
            return chain.proceed(request);
        }
    }.cover();
}
```

### 支持重定向
``` java
public class TestInterceptor3 implements Interceptor {

    @Override
    public Observable<RouteResponse> intercept(@Nullable Context context, Chain chain) throws RuntimeException {
            // 重定向 优先级大于 成功或者失败,不会继续走之前的 拦截器
            final RouteResponse currentResponse = chain.getCurrentResponse();
            currentResponse.openRedirect(RouterTable.TEST1);
            currentResponse.addData(new Bundle());
            return Observable.just(currentResponse);
}
```


### 支持获取原始 url 信息

``` java
getIntent().getStringExtra(Routers.KEY_RAW_URL);
```

### 支持多模块

* 每个包含 activity 的 module 都要添加 apt 依赖
* 每个 module(包含主项目) 都要添加一个 @Module(name) 的注解在任意类上面，name 是项目的名称
* 主项目要添加一个 @Modules({name0, name1, name2}) 的注解，指定所有的 module 名称集合

## 混淆配置

``` groovy
-keep class chuxin.shimo.shimowendang.smrouter.** { *; }
```
