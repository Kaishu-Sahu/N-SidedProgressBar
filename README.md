# N-SidedProgressBar 

[![Platform](https://img.shields.io/badge/platform-Android-yellow.svg)](https://www.android.com)
![GitHub](https://img.shields.io/github/license/mashape/apistatus.svg)
![GitHub](https://img.shields.io/badge/status-Under%20development-yellow.svg)



Progress Bar in the shape of regular polygon.

![1](/gifs/1.gif)
![3](/gifs/3.gif)
![2](/gifs/2.gif)


## Usage
To create NSidedProgressBar in xml :- 
```xml
...
<com.iitr.kaishu.nsidedprogressbar.NSidedProgressBar
        android:id="@+id/NSidedProgressBar"
        android:layout_width="75dp"
        android:layout_height="75dp"
        android:layout_gravity="center"
        app:nsidedProg_baseSpeed="5"
        app:nsidedProg_sideCount="3"
        app:nsidedProg_clockwise="true"
        />
...
```
Don't forget to add ```xmlns:app="http://schemas.android.com/apk/res-auto"```

Or in Java
```java
....
NSidedProgressBar nSidedProgressBar  = new NSidedProgressBar(this, 3);
nSidedProgressBar.setBaseSpeed(5);
...
```
### Properties
Properties which can be set from xml:-
* ```nsidedProg_sideCount```: set the number of sides.
* ```nsidedProg_primaryColor```: set the color of unmovable part.
* ```nsidedProg_secondaryColor```: set the colot of movable part.
* ```nsidedProg_baseSpeed```: set the speed(constant) of unaccelerated end. 
* ```nsidedProg_refreshRate```: set the fps of animation.
* ```nsidedProg_primaryRimWidth```: set the width of unmovable part.
* ```nsidedProg_secondaryRimWidth```: set the width of movable part.
* ```nsidedProg_clockwise```: set the nature of rotation.
* ```nsidedProg_determinate```: set the nature of progress bar.
* ```nsidedProg_startSide```: set the starting point of determinate progress bar.

## License
N-sidedProgressBar is licensed under MIT license. View [license](LICENSE).
