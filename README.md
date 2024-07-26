[![](https://jitpack.io/v/wix-incubator/detox-butler.svg)](https://jitpack.io/#wix-incubator/detox-butler)
[![DetoxButler is released under the MIT license](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

# detox-butler

This project is based on the [Test Butler](https://github.com/linkedin/test-butler) project. This 
project is implementation of the very basic functionality taken from the original Test Butler but
with support for the latest Android versions.
The original Test Butler project at this time is outdated and trying to make all of the functionality
work on all Android versions is a bit of a challenge.

## How to integrate the butler
1. Make sure you have jitpack repository installed. Add the following to your root build.gradle file at the end of repositories:
```groovy
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            mavenCentral()
            maven { url 'https://jitpack.io' }
        }
    }
```
2. Add the following dependency to your app/build.gradle
```groovy
    dependencies {
        androidTestImplementation 'com.github.wix-incubator:detox-butler:detoxbutler:Tag'
        androidTestUtil 'com.github.wix-incubator:detox-butler:app-aosp:Tag' 
    }
```
> [!NOTE]
> You can use the app-genymotion instead of app-aosp if you are using Genymotion emulator.


## Features
TBD

