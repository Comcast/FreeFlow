FreeFlow
========

A layout engine for Android that decouples layouts from the View containers that manage scrolling and view recycling. FreeFlow makes it really easy to create custom layouts and beautiful transition animations as layouts are changed.

FreeFlow is a composition based approach to Android Layouts. As opposed to default Android Layouts, FreeFlow Layouts are swappable at runtime which allows views to their new states smoothly. The fundamental difference here is that FreeFlow prefers [Composition over Inheritance](http://en.wikipedia.org/wiki/Composition_over_inheritance) which makes the system a lot more adaptable.

Freeflow may be considered in "alpha". You can help in many ways, by reviewing and making suggestions on api's to actually finding bugs and submitting patches via pull requests.

FreeFlow is inspired by UI frameworks like [UICollectionViews on iOS][1] and the [Spark Architecture in Flex][2]. 


![](https://raw.github.com/Comcast/FreeFlow/master/examples/Artbook/screenshots/freeflow.png)

# Building Blocks

At the basic level, FreeFlow consists of 3 parts:

* [FreeFlowContainer][3]: The core class that extends ViewGroup and places all Views inside it
* [FreeFlowLayout][4]: The class thats responsible for defining the [Rect](5)'s that the Container will use to position the Views.
* [FreeFlowLayoutAnimator][6]: The animator that will animate the Views when their position Rect's are changed because of a change in the Layout
* [SectionedAdapter][7]: The data adapter class that returns the View instances based on the data being rendered. Its modeled very closely to the List Adapters that are used in Android but also understand the concept of "[Section][8]" which might segment data into different parts (For example a user's contacts list may include Sections that hold names beginning with a particular character). 

Additionally there are some helper classes like the [DefaultLayoutAnimator][9] that will transition views automatically as they get added, moved or removed and is pretty configurable. FreeFlow comes with some basic Layouts like HLayout, VLayout , HGridLayout and VGridLayout but its easy enough to create custom layouts (see the [Artbook example's custom layout][10])


The Artbook example in this repository is a good example of whats possible with FreeFlow. You can download the .apk from the releases tab or see the experience on the video below: 

[![Artbook demo project](http://img.youtube.com/vi/xDd-bcGqLkw/0.jpg)][11]

# Feedback/Support/Help

Join the [Google+ community][12] for questions or keeping up with upcoming features, releases, etc


[1]: https://developer.apple.com/library/ios/documentation/UIKit/Reference/UICollectionView_class/Reference/Reference.html
[2]: http://www.adobe.com/devnet/flex/articles/flex4_sparkintro.html
[3]: FreeFlow/src/com/comcast/freeflow/core/FreeFlowContainer.java
[4]: FreeFlow/src/com/comcast/freeflow/layouts/FreeFlowLayout.java
[5]: http://developer.android.com/reference/android/graphics/Rect.html
[6]: FreeFlow/src/com/comcast/freeflow/animations/FreeFlowLayoutAnimator.java
[7]: FreeFlow/src/com/comcast/freeflow/core/SectionedAdapter.java
[8]: FreeFlow/src/com/comcast/freeflow/core/Section.java
[9]: FreeFlow/src/com/comcast/freeflow/animations/DefaultLayoutAnimator.java
[10]: examples/Artbook/src/com/comcast/freeflow/examples/artbook/layouts/ArtbookLayout.java
[11]: http://www.youtube.com/watch?v=xDd-bcGqLkw
[12]: https://plus.google.com/communities/109232474194967955567
