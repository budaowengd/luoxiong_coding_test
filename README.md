# 一 如何Build

作者使用的是win10操作系统。

- gradle 插件版本是 7.2.1 ， gradle wrapper版本是 gradle-7.3.3-bin.zip
- kotlin版本是 1.7.10
- JDK 版本是 1.8
- minSdk 21 targetSdk 32

如果编译不通过，请联系作者，wx：382060748

# 二 运行截图
![输入图片说明](https://cdn.zhjugee.com/static/3d/img/image-20221208155407838.png)

# 三 项目介绍

该项目使用Kotlin语言进行开发，[基于PT实现屏幕适配](https://blankj.com/2018/12/18/android-adapt-screen-killer/#more)，采用MVVM架构 + Databinding + Google [Jetpack](https://developer.android.google.cn/jetpack)组件搭建项目。 但是和谷歌官网的MVVM架构思想有些不同，比如官方建议使用Repository层管理数据，如果有些页面只需要网络数据，不需要本地缓存的话，就没必要加Repository层，减少模板代码。下面我对本项目中的亮点进行介绍。

## 2.1 基于泛型减少模板代码

业务层只需要声明ViewModel 和 Manager，Base层会基于反射创建具体类，业务层无需关注具体实现细节。

```kotlin
abstract class BaseVm : ViewModel()
abstract class BaseManager<VM : ViewModel>

abstract class BaseAct<B : ViewDataBinding, VM : BaseVm, M : BaseManager<VM>>(private val layoutId: Int) : AppCompatActivity() {
    protected var mBinding: B? = null
    private lateinit var mModel: VM
    private lateinit var mManager: M

    private fun createViewModel() {
        val genericSuperclass = javaClass.genericSuperclass!!
        val type = genericSuperclass as ParameterizedType
        val modelClass = type.actualTypeArguments[1] as Class<VM>
        mModel = ViewModelProvider(this).get(modelClass)
    }

    private fun createManager(argsType: Array<out Type>) {
        mManager = ... 同上
        mManager.setActivity(this).setViewModel(mModel)
    }
}


class MainAct : BaseAct<MainActBinding, MainVm, MainManager>(R.layout.main_act) {

    override fun onCreateAfter() {
        // 处理业务逻辑...

    }
}
```



## 2.2 添加Manager业务层

传统MVVM模式，业务逻辑一般放ViewModel中，但是由于ViewModel不能持有Activity或View的引用，有时候处理基于View的弹窗等逻辑不方便。放在Activity会使得View层代码过多，难以维护。所以新增了Manager层专门处理业务逻辑，其生命周期和Activity相同，并持有Activity和ViewModel的引用，处理具体逻辑会方便许多。

```kotlin
class MainManager : AbstractActManager<MainAct, MainActBinding, MainVm>() {
    override fun initActView(act: MainAct, b: MainActBinding) {
    	initTablayout()
    }

    private fun initTablayout(){

    }
 }
```

## 2.3 统一管理点击事件

页面的点击事件可以定义在xml中， 也可以view.setOnClickListener() ，也可以通过Databinding设置variable进行定义，长期以往，后面维护起来想快速找到某个页面的点击事件很难。   所以我借鉴kotlin的sealed语法特性，Activity或Fragment统一管理整个页面的点击事件，然后交给业务层处理具体点击逻辑。

 ![输入图片说明](https://cdn.zhjugee.com/static/3d/img/image-20221208170714163.png)

1. 上面PostListFrag页面总共有3个点击事件，分别是所有语言、时间排序、列表item。

2. 点击事件首先在Manager中定义，然后交给Fragment进行分发。

3. ```kotlin
   class PostListManager : AbstractFragManager<PostListFrag, MainFragPostListBinding, PostListVm>() {
       private val mAdapter by lazy(LazyThreadSafetyMode.NONE) { DefaultRvAdapter() }

       override fun initFragView(frag: PostListFrag, b: MainFragPostListBinding) {
           // 所有语言的点击事件
           b.btnLanguageSort.setOnClickListener { frag.doAction(PostListAction.LanguageSort()) }
           // 时间排序的点击事件
           b.clDateSort.setOnClickListener { frag.doAction(PostListAction.DateSort()) }
           // 列表item的点击事件
           mAdapter.setItemClickEvent(object : BaseRvFun2ItemClickEvent<AbstractPostModel, Int> {
               override fun clickRvItem(item: AbstractPostModel, flag: Int) {
                   getRealFrag().doAction(PostListAction.ListItem(item))
               }
           })
       }
   }

   class PostListFrag : BaseFrag<MainFragPostListBinding, PostListVm, PostListManager>(R.layout.main_frag_post_list) {
      fun doAction(action: PostListAction) {
           when (action) {
               // 点击时间排序
               is PostListAction.DateSort -> getM().doDateSort()
               // 点击列表item
               is PostListAction.ListItem -> getM().doListItem(action.m)
               // 点击语言排序
               is PostListAction.LanguageSort -> getM().doLanguageSort()
           }
       }
   }
   sealed class PostListAction {
       class DateSort() : PostListAction()
       class LanguageSort() : PostListAction()
       class ListItem(val m: AbstractPostModel) : PostListAction()
   }
   ```

## 2.4 统一管理网络请求

如果我想知道某个页面总共请求了哪些接口，在什么时机下请求的，是否有loading条，loading是否阻塞，接口报错了是否有错误页面，是否会弹吐司提示用户。

针对以上问题，业界也没标准流程和规章。 我的做法是基于kotlin的sealed语法特性，在Activity中定义render( ) 方法，进行统一分发。

比如：进入首页需要请求列表接口，显示非阻塞loading，接口失败了，点击重试按钮会重新请求。

```kotlin
class MainAct : BaseAct<MainActBinding, MainVm, MainManager>(R.layout.main_act) {

    override fun onCreateAfter() {
        // 请求列表接口
        render(MainActState.NewsList())
    }

    private fun render(state: MainActState) {
        when (state)
        	// 请求列表接口
            is MainActState.NewsList -> {
                getScope().launch(Main) {
                    // 显示非阻塞加载中
                    getB().stateLayout.showLoading()
                    runCatching {
                        createApi<MainApi>().queryPosts()
                    }.onSuccess {
                        // 显示内容
                        getB().stateLayout.showContent()
                        // 初始化页面
                        getM().initTabLayoutAndRefreshPage(it)
                    }.onFailure {
                        // 显示错误布局
                        getB().stateLayout.showError {
                            // 点击重试后，重新请求接口
                            render(state)
                        }
                    }
                }
            }
        }
    }
 }
sealed class MainActState {
    class NewsList : MainActState()
}
```

## 2.5 命名统一后缀

1. Activity 跳转和 Fragment之间传值，统一使用类封装，类的后缀定义为Dto，如：

```kotlin
@Parcelize
data class ToPostListFragDto(
    val categoryName: String, // 分类名
) : Parcelable

@Parcelize
data class ToPostDetailActDto(
    val h5Url: String, // h5地址
    val title: String, // 页面标题
) : Parcelable

```

2. 接收服务端返回的数据统一以Vo结尾。如： PostItemVo、PostDetailVo
3. Activity中显示一个弹窗，需要传个对象给弹窗，这个对象对应的类名后缀如：ToPostListLanguageSelectDialogBo

## 2.6 使用注解代替枚举和魔法数字

代码中不能直接使用数字定义某种含义，为了可持续维护，应该使用枚举或注解定义具体的名字。

比如页面中有时间排序，排序有上下2个箭头，我们需要定义高亮显示的箭头类型有上、下、默认。

 ![输入图片说明](https://cdn.zhjugee.com/static/3d/img/image-20221208173429473.png)

```kotlin
// 列表排序状态注解类
annotation class SortStateFlagNote {
    companion object {
        const val blank = 0 // 上下箭头都不选中
        const val top = 1 // 上的箭头选中
        const val bottom = 2 // 下的箭头选中
    }
}
```

# 四 使用的3方库

```groovy
/*三方库*/
// 工具
implementation 'com.blankj:utilcodex:1.31.0'   // https://github.com/Blankj/AndroidUtilCode
// 弹窗
implementation 'com.gitee.my_lib:xpopup:1.1.1' // https://gitee.com/my_lib/xpopup
// 动画库
implementation 'com.airbnb.android:lottie:5.2.0' // https://github.com/airbnb/lottie-android
// 网络请求
implementation 'com.squareup.okhttp3:okhttp:4.9.1'
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.google.code.gson:gson:2.10'
// 图片加载
implementation 'com.github.bumptech.glide:glide:4.14.2'
// 让OkHttp加载图片
implementation 'com.github.bumptech.glide:okhttp3-integration:4.14.2'
// Glide自动生成代码
kapt 'com.github.bumptech.glide:compiler:4.12.0'
// 胶水布局，让每个列表可以连续在一起滑动
implementation 'com.github.donkingliang:ConsecutiveScroller:4.6.3' // https://github.com/donkingliang/ConsecutiveScroller
```

# 五 可优化的地方

1. 如果页面的接口数量很多，render方法的行数会很多，可以封装下网络请求，render只负责分发，不在该方法中开协程，而交给ViewModel。

2. 适配器封装，可统一管理RecyclerView的布局管理器，适配器的创建，上拉和加载更多的逻辑处理。具体实现见：https://gitee.com/my_lib/x-rv-adapter