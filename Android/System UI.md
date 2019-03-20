# System UI

## System UI Constants
**SYSTEM_UI_FLAG_FULLSCREEN**
* Non-critical screen decorations (such as the status bar) will be hidden while the user is in the View's window, focusing the experience on that content.
* Best used over the window flag when it is a transient state, that is, the application does this at certain points in its user interaction where it wants to allow the user to focus on content.
* This state will be removed by the system in various situations

**SYSTEM_UI_FLAG_HIDE_NAVIGATION**
* On devices that draw essential navigatin controls (Home, back, and the link) on screen, this flag will cause those to disappear. 
* The least user interaction will cause them to reappear immediately. When this happens, both this flag and SYSTEM_UI_FLAG_FULLSCREEN will be cleared automatically, so that both elements reappear at the same time.

**SYSTEM_UI_FLAG_IMMERSIVE**
* View would like to remain interactive when hiding the navigation bar with SYSTEM_UI_FLAG_HIDE_NAVIGATION. If this flag is not set, SYSTEM_UI_FLAG_HIDE_NAVIGATION will be force cleared by the system on any user interaction.
* Must be used in conjuction with SYSTEM_UI_FLAG_HIDE_NAVIGATION

**SYTEM_UI_FLAG_IMMERSIVE_STICKY**
* View would like to remain interactive when hiding the status bar with SYSTEM_UI_FLAG_FULLSCREEN and/or hiding the navigation bar with SYSTEM_UI_FLAG_HIDE_NAVIGATION.
* When system bars are hidden in immersive mode, they can be revealed temporarily with system gestures, suchas swiping from the top of the screen. These transient system bars will overlay app's content, may have some degree of transparency, and will automatically hide after a short timeout.
* Must be used in conjuction with SYSTEM_UI_FLAG_FULLSCREEN and/or SYSTEM_UI_FLAG_HIDE_NAVIGATION

**SYSTEM_UI_FLAG_LAYOUT_STABLE**
* When using other layout flags, we ould like a stable view of the content insets

**SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR**
* Requests the navigation abr to draw in a mode that is compatible with light navigation bar backgrounds
* For this to work, the window must request FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS but not FLAG_TRANSLUCENT_NAVIGATION

**SYSTEM_UI_FLAG_LIGHT_STATUS_BAR**
* Requests the status bar to draw in a mode that is compatible with light status bar backgrounds.
* For this to work, the window must request FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS but not FLAG_TRANSLUCENT_STATUS

**SYSTEM_UI_FLAG_LOW_PROFILE**
* View has requested the system UI to enter an unobtrusive "low profile" mode.
* In low profile, the status bar and/or navigation icons may dim

**SYSTEM_UI_FLAG_VISIBLE**
* View has requested the system UI (status bar) to be visible