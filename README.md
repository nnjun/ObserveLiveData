# ObserveLiveData

![](https://img.shields.io/badge/language-java-brightgreen.svg)

通过注解监听LiveData数据变化，原理类似于Butterknife编译时生成。

## 使用方式

### 准备

Step 1. Gradle文件加入
```
allprojects {
    repositories {
        ...
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```
引入依赖
```
implementation 'com.github.nnjun.ObserveLiveData:livedata_api:1.0.1'
annotationProcessor 'com.github.nnjun.ObserveLiveData:livedata_core:1.0.1'
```


### Usage

#### Step 1.注解方法

```Java
@ObserveLiveData(target = SimulationViewModel.class, field = "mConfig1")
public void onConfig(String config) {
    Log.d(TAG, "from SimulationViewModel: " + config);
}
```
- 参数1：target = LiveData所在类(如果LiveData在自己类，则可以忽略不填写)
- 参数2：field = LiveData变量名
- 具体查看demo
##### 注意事项
- 方法的修饰符为public
- SimulationViewModel中的mConfig1为public
- 具体查看demo

#### Step 2. 注册事件

```Java
ObserveManager.register(this, this, viewModel, viewMode2);
```
- 参数1：注解方法所在的实例
- 参数...：target对象的实例，此处需要监听MainActivity(this)，ViewModel1，ViewModel2中的LiveData数据
- 更多细节查看demo

---
## LiveData版本问题

编译器默认使用 **android.arch.lifecycle** 包下的类
```
android.arch.lifecycle.LifecycleOwner;
android.arch.lifecycle.Observer;
android.arch.lifecycle.MutableLiveData
```

如需更换请在Gradle中配置本项目使用了androidx下的LiveData，所以需要额外配置。
```
android {
    ...
    defaultConfig {
        ...
        // 加入此配置
        javaCompileOptions {
            annotationProcessorOptions {
                // 使用androidx.lifecycle包下的类
                arguments = ["LIFECYCLE": "androidx.lifecycle"]
            }
        }
    }
}

```

#### 如果项目帮助到你，请多多star 感谢！