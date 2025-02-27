# This has been fixed
The [Navigation 2.8.0](https://developer.android.com/jetpack/androidx/releases/navigation#2.8.0) release seems to resolve the issue. This repository can only reproduce the described issue with versions prior to that one.

# compose-nav-deeplink-issue
A demo application that demonstrates an unexpected behavior where one deeplink destination is 
always triggered despite it not matching the current deeplink.

### Steps to reproduce
1. Install the app and open it to see the navigation structure. It consists of a `NavHost` with 
three destinations: `home`, `feature1`, and `feature2`. The `home` destination is start destination of the `NavHost`.
The `feature1` and `feature2` destinations are children. Both support deeplinking, but have completely different
uriPatterns.
2. Close the app and try to open the `feature1` destination using the following deeplink: 
`com.example.nav_dl_issue://feature1/parameter=hello`. You can do it through ADB with the following command:
`adb shell am start com.example.nav_dl_issue://feature1/parameter=hello`
3. Notice how the app opens the feature2 screen instead of feature1. This is unexpected behavior. Feature2 
should not be opened, because we have launched an Intent with the `uriPattern` of `feature1`. The Uri differs in schemas, 
host and parameters. Furthermore, the uri of `feature2` isn't public at all, thus cannot be launched with an implicit Intent.

### How to "fix" the issue
1. Change in the code the `uriPattern` of the `feature2` destination in the `MainActivity.kt` to the commented out 
value and repeat step 2 above.
2. Notice how the app opens the feature1 screen as expected.

### Some thoughts
This behavior seems illogical and can be caused by one of the following:
- A bug in the Navigation library.
- Wrong usage of the Navigation library. This could be due to misunderstanding and lack of documentation. Indeed, 
all examples of deeplinks on the website include a `{parameter}` field inside the uriPattern. However, if this is a hard
requirement, it should be explicitly documented.
