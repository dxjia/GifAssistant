# Deprecated
   早期代码，不再维护。
# GifAssistant
Android练手项目，可以浏览GIF，或从视频中截取一段制作成GIF图片。

#开发背景
Android没有直接用于显示gif图片的原生控件（webview除外），加之在看视频时有些很有意思的场景经常想制作成gif图片，PC上有很多影音播放软件可以轻松实现这个功能，但手机上的却很少有，所以本着一边做一边学的想法开始了开发。<br>
基本都是在业余时间制作，而且也在不断完善中，程序还没有完全OK，开始使用eclipse+adt，后来受github影响开始迁移到android studio，现在使用github托管，不断学习进步。

#程序介绍
1.第一次使用的引导界面，仿墨迹天气3.0动画<br>
2.ResideMenu效果<br>
3.显示Gif图片，GifView<br>
4.视频播放，图片截取<br>
5.移植ffmpeg到android，并使用jni进行调用，通过ffmpeg command生成gif<br>
..具体移植过程可以参考我的另外一个项目[ffmpeg for android shared library](https://github.com/dxjia/ffmpeg-for-android-shared-library)<br>
6.提供压缩率、帧率以及residemenu背景设置<br>
7.圆角菜单<br>

#程序编译
1.使用git clone下载项目到你的本地；<br>
2.使用Android Studio - Open an existing Android Studio project，打开clone下来的工程；<br>
3.build<br>

#程序截图
![image](https://github.com/dxjia/GifAssistant/blob/master/screenshot/5.png)
 ![image](https://github.com/dxjia/GifAssistant/blob/master/screenshot/6.png)
![image](https://github.com/dxjia/GifAssistant/blob/master/screenshot/7.png)
![image](https://github.com/dxjia/GifAssistant/blob/master/screenshot/8.png)
#Reference & Thanks
[ViewPager-Android](https://github.com/rharter/ViewPager-Android)<br>
[mojichina](https://github.com/xyzhang/mojichina)<br>
[AndroidResideMenu](https://github.com/SpecialCyCi/AndroidResideMenu)<br>
[SpringIndicator](https://github.com/chenupt/SpringIndicator)<br>
......
