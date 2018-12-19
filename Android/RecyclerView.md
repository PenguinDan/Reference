# RecylerView

## Reference
https://developer.android.com/guide/topics/ui/layout/recyclerview#java

## Support Library
```
dependencies {
    implementation 'com.android.support:recyclerview-v7:28.0.0'
}
```
## Terms
* **Adapter**
  * A subclass of RecyclerView.Adapter which is responsible for providing views that represent items in a data set.
* **Position**
  * The position of a data item within an *Adapter*.
* **Index**
  * The index of an attached child view as used in a call to getChildAt(int).
* **Binding**
  * The process of preparing a child view to display data corresponding to a *position* within the adapter.
* **Recycle (View)**
  * A view previously used to display data for a specific adapter position may be placed in a cache for later reuse to display the same type of data again later. This can drastically improve performance by skipping initial layout inflation or construction.
* **Scrap (View)**
  * A child view that has entered into a temporarily decached state during layout. Scrap views may be reused without becoming fully detached from the parent RecyclerView, either unmodified if no rebinding is required or modified by the adapter if the view was considered *dirty*.
* **Dirty (View)**
  * A child view that must be rebounded by the adapter before being displayed.
* **LinearLayoutManager**
  * Implementation of a manager that provides similar functionality to a ListView
* **GridLayoutManager**
  * Implementation of a manager that layouts out items in a grid.
  * By default, each item occupies 1 span. These can be changed by providing a custom GridLayoutManager.SpanSizeLookup instance via **setSpanSizeLookup(SpanSizeLookup)**

## Overview
**The Container** <br>
The overall container for your user interface is a **RecyclerView** object that you add into your layout. The **RecyclerView** fills itself with views provided by a **layout manager** that *you* provide. You can use one of the standard **layout managers** such as **LinearLayoutManager** or **GridLayoutManager**, or implement you own.

**The Views** <br>
The views in the list are represented by **view holder** objects. These objects are instances of a class defined by extending **RecyclerView.ViewHolder**. Each **view holder** is in charge of displaying a single item with a view.
* The **RecyclerView** creates only as many **view holders** as are needed to display the on-screen portion of the dynamic content, plus a few extra. 
* As the user scrolls through the list, the **RecyclerView** takes the off-screen views and rebinds them to the data which is scrolling onto the screen.

**The View Manager** <br>
The **view holder** objects are managed by an adapter which you create by extending **RecyclerView.Adapter**. The adapter creates **view holders** as needed. The adapter also binds the view holders to their data. It does this by assigning the view holder to a position, and calling the adapter's **onBindViewHolder()** method.
