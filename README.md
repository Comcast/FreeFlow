FreeFlow
========

A layout engine for Android that decouples layouts from the View containers that manage scrolling and view recycling. 

FreeFlow is inspired by UI frameworks like [UICollectionViews on iOS][1] and the [Spark Architecture in Flex][2]. The decoupling of the Containers and the Layout classes means that layouts can be swapped at runtime. In the screenshot below for example, the same Container can swap between a very grid-like layout to a very custom one by tapping the "Change Layout" button on the ActionBar. 

Additionally since Layouts are only responsible or coming up with positioning rectangles, its very easy to write custom layouts in this framework.

FreeFlow also gives you full control over the animation experience as the layouts transition from one to another. 

![](examples/ArtBook/screenshots/freeflow.png)


[1]: https://developer.apple.com/library/ios/documentation/UIKit/Reference/UICollectionView_class/Reference/Reference.html

[2]: http://www.adobe.com/devnet/flex/articles/flex4_sparkintro.html
