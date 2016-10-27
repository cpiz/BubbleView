# BubbleView for Android

[![build](https://travis-ci.org/cpiz/BubbleView.svg?branch=master)](https://travis-ci.org/cpiz/BubbleView) [![build](https://jitpack.io/v/cpiz/BubbleView.svg)](https://jitpack.io/#cpiz/BubbleView)

[README in English](README.md)

BubbleView是带箭头的Android气泡控件/容器类，支持在布局中通过自定义属性或代码进行丰富的定制

* 自定义箭头朝向：上、下、左、右、无
* 箭头自动指向目标
* 箭头高度、宽度、位置
* 气泡填充颜色、填充Padding、边框、边框颜色、圆角
* BubbleTextView文字气泡/BubbleXxxLayout容器气泡

下载
--------
在项目目录的 `build.gradle` 中增加 `maven { url "https://jitpack.io" }`
```
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```

在模块目录的 `build.gradle` 中增加 `compile 'com.github.cpiz:BubbleView:{X.X.X}'`
```
dependencies {
    ...
    compile 'com.github.cpiz:BubbleView:{X.X.X}'
}
```


箭头指定特定方向
--------
指定属性app:bb_arrowDirection，可选值为Left|Up|Right|Down|None，默认箭头位置在气泡侧面居中。

![指定箭头方向](./screenshots/1.png)

```XML
<com.cpiz.android.bubbleview.BubbleTextView
    android:id="@+id/bb1"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_centerVertical="true"
    android:layout_toLeftOf="@id/iv1"
    android:padding="6dp"
    android:text="ArrowRight"
    android:textColor="@android:color/white"
    app:bb_arrowDirection="Right"
    app:bb_cornerRadius="4dp"/>
```

箭头指向特定对象
-------
指定属性app:bb_arrowTo为目标对象ViewId，将自动确定箭头方向，并将箭头位置指向目标中心。

![箭头指向特定对象](./screenshots/2.png)

```XML
<RelativeLayout
    android:id="@+id/group2"
    android:layout_width="match_parent"
    android:layout_height="180dp"
    android:layout_marginBottom="30dp"
    android:background="#bdc3c7"
    android:padding="10dp">

    <ImageView
        android:id="@+id/iv2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerHorizontal="true"
        android:layout_centerVertical="true"
        android:layout_margin="4dp"
        android:src="@android:drawable/ic_btn_speak_now"
        android:tint="#FFFFFF"/>

    <com.cpiz.android.bubbleview.BubbleTextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_alignTop="@id/iv2"
        android:layout_toLeftOf="@id/iv2"
        android:padding="6dp"
        android:text="Get your apps ready for Android 6.0 Marshmallow! "
        android:textColor="@android:color/white"
        app:bb_arrowTo="@id/iv2"/>
</RelativeLayout>
```

自定义样式
-------
![自定义样式](./screenshots/3.png)

```XML
<com.cpiz.android.bubbleview.BubbleTextView
    android:id="@+id/big4"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:layout_below="@+id/big2"
    android:layout_margin="4dp"
    android:layout_toRightOf="@id/big3"
    android:padding="30dp"
    android:text="WithBorder"
    android:textColor="@android:color/white"
    app:bb_arrowDirection="Down"
    app:bb_arrowHeight="10dp"
    app:bb_arrowOffset="30dp"
    app:bb_arrowWidth="40dp"
    app:bb_borderColor="@color/colorPrimary"
    app:bb_borderWidth="6dp"
    app:bb_cornerBottomLeftRadius="0dp"
    app:bb_cornerBottomRightRadius="10dp"
    app:bb_cornerTopLeftRadius="0dp"
    app:bb_cornerTopRightRadius="4dp"
    app:bb_fillColor="@android:color/holo_red_light"
    app:bb_fillPadding="4dp"/>
```

###作为容器
--------
除直接使用BubbleTextView显示文字外，还可以使用

* BubbleRelativeLayout
* BubbleLinearLayout
* BubbleFrameLayout

作为气泡容器，自定义包含内容

![作为容器](./screenshots/4.png)

###弹出显示
--------
还可通过BubblePopupWindow来包装，弹出显示
![弹出显示](./screenshots/5.gif)

* 支持点击气泡之外关闭
* 支持点击气泡关闭
* 支持定时关闭

```
    View rootView = LayoutInflater.from(this).inflate(R.layout.simple_text_bubble, null);
    BubbleTextView bubbleView = (BubbleTextView) rootView.findViewById(R.id.popup_bubble);
    window = new BubblePopupWindow(rootView, bubbleView);
    window.setCanceledOnTouch(true);
    window.setCanceledOnTouchOutside(true);
    window.setCanceledOnLater(3000);
    window.showArrowTo(v, BubbleStyle.ArrowDirection.Left);
```


###其它

* 如果自行指定BubbleView的setBackground/setBackgroundColor等，将导致气泡样式失效

License
-------
    Copyright 2016 Cpiz

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
