# Coordinator Layout

A general purpose container that allows for coordinating interactive behaviors between its children. CoordinatorLayout manages interactions between its children, and as such **needs to contain all the Views that interact with each other.**

## 2 General Cases
* As a top-level content layout, meaning CoordinatorLayout is at the root of all views within an activity or fragment.
* As a container for a specific interaction with one or more child views

## AppBarLayout
Children are places vertically, with certain parameters the children can manage their behavior when the content is scrolled. The real power of an AppbarLayout is caused by the proper management of the different scroll flags in their views.

We can manage the behavior of direct childs in an AppbarLayout with the parameter: **layout_scrollFlags**. 

**value: scroll** : The value: **scroll** allows the children to not be static, otherwise, scrollable content will slide behind it.

**value: snap** : With this value, we avoid falling into mid-animation-states, this means that the animations will always hide or expand all the height of its view.

**value: enterAlways** : When scrolling on screen, the view will scroll on any downwards scroll event, regardless of whetherthe scrolling view is also scrolling

**value: enterAlwaysCollapsed** : An additional flag for **enterAlways** which modifies the returning view to only initially scroll back to it's collapsed height

**value: exitUntilCollapsed** : When scrollling off screen, the view will be scrolled until it is 'collapsed'

**value: none** : If **layout_scrollFlags** is not specified, it will always be visible

## Custom Behaviors
Child: The child is the view that enhances behavior <br>
Dependency: Serves as a trigger to interact with the child element. 

Example: If an ImageView is the child, and a Toolbar is the dependency, if the Toolbar moves, the ImageView will move too.

Step 1: Extend CoordinatorLayout.Behavior<T> where T is the class that belonds to the View that interests us. In this case, ImageView.

Step 2: Override the following methods <br>
**layoutDependsOn** : Called every time that something happens in the layout, what we must do to return true once we identify the dependency. In the previous example, this is fired every time the user scrolls. <br>
```
@Override
public boolean layoutDependsOn(CoordinatorLayout parent, CircleImageView child, View dependency) {
    return dependency instanceof Toolbar;
}
```
Everytime layoutDependsOn returns **true**, the second **onDependentViewChanged** will be called. Here is where we must implement our animations, transitions, or movements always related with the provided dependency.

```
@Override
public boolean onDependentViewChanged(CoordinatorLayout parent, 
    CircleImageView avatar, View dependency) {
    modifyAvatarDependingDependencyState(avatar, dependency);
}

private void modifyAvatarDependingDependencyState(
CircleImageView avatar, View dependency) {
    //  avatar.setY(dependency.getY());
    //  avatar.setBlahBlah(dependency.blah / blah);
}   
```