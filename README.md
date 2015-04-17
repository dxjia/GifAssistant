# GifAssistant
Android练手项目，可以浏览GIF，或从视频中截取一段制作为GIF图片。

#开发背景
Android没有直接用于显示gif图片的原生控件（webview除外），加之在看视频时有些很有意思的场景经常想制作成gif图片，PC上有很多影音播放软件可以轻松实现这个功能，但手机上的却很少有，所以本着一边做一边学的想法开始了开发。<br>
基本都是在业余时间制作，而且也在不断完善中，程序还没有完全OK，开始使用eclipse+adt，后来受github影响开始迁移到android studio，现在使用github托管，不断学习进步。

#程序介绍
1.第一次使用的引导界面，仿墨迹天气3.0动画<br>
2.ResideMenu效果<br>
3.显示Gif图片，GifView<br>
4.视频播放，图片截取<br>
5.将N张图片合并为一个gif图片，核心代码使用native实现<br>
6.提供压缩率、帧率已经residemenu背景设置<br>
7.圆角菜单<br>

#程序编译
1.使用git clone下载项目到你的本地；
2.使用Android Studio - Open an existing Android Studio project，打开clone下来的工程；
3.在local.properties文件中增加你的NDK路径
  ```xml
  ndk.dir=D\:\\android-ndk
  ```xml
4.build，如有error，请根据提示修改你的compileSdkVersion buildToolsVersion targetSdkVersion等，并保持app/build.gradle内的
```java
dependencies {
    compile fileTree(include: ['*.jar'], dir: 'libs')
    compile 'com.android.support:appcompat-v7:21.0.0'
    compile files('libs/nineoldandroids-library-2.4.0.jar')
}
```java
其中的 ```java compile 'com.android.support:appcompat-v7:21.0.0' ```java 与你的target sdkversion相匹配，如若你的环境是19，那么这里就是 v7:21

#程序截图
![image](https://github.com/dxjia/GifAssistant/blob/master/screenshot/1.png)
 ![image](https://github.com/dxjia/GifAssistant/blob/master/screenshot/2.png)
![image](https://github.com/dxjia/GifAssistant/blob/master/screenshot/3.png)
 ![image](https://github.com/dxjia/GifAssistant/blob/master/screenshot/4.png)
![image](https://github.com/dxjia/GifAssistant/blob/master/screenshot/5.png)
 ![image](https://github.com/dxjia/GifAssistant/blob/master/screenshot/6.png)
![image](https://github.com/dxjia/GifAssistant/blob/master/screenshot/7.png)
