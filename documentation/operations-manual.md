# Travelcy Operations Manual

## Overview
Travelcy is a native android application developed in Kotlin with Android Studio. Its base functionality is a location aware currency converter and bill splitter (tax and tip included). The app is divided into 5 layers:

### database (com.travelcy.travelcy.database)
We created our database with room https://developer.android.com/jetpack/androidx/releases/room so it is persistent and local (data can be fetched offline).
We defined our DAO's for storing and querying data for our Convert, Split and Settings components.

### model (com.travelcy.travelcy.model)
Our models are the entities mapped to the same tables in the Travelcy Database - the tables are generated from the entities defined. So the process we followed was to create our models which then are created in the room database when the app runs.

### services (com.travelcy.travelcy.services)
Our service layer contains our repositories, webservices and livedata observables.

The repositories act as a layer for the viewmodels to retrieve and store necessary data, they use the functions implemented in the DAO's to achieve this.

The webservices (for currency and tipping), use retrofit https://square.github.io/retrofit/ to interact with the APIs and GSON to convert the json data to java objects.

### ui (com.travelcy.travelcy.ui)
Here we have created fragments and viewmodels for each screen so our UI implementation follows the MVVM pattern.
Our fragments instantiate our viewmodels and set up necessary event listeners, observables and adapters.
The viewmodels are where we fetch the liveData defined in the service layer and implement any business logic, such as currency converter calculations, etc.

### layout (app.res.layout)
These are the screens we have designed and styled according to our design, as specified before - the fragments are connected with a relevent layout.

## Coding conventions
We have been following Kotlins' standard coding convention, which can be found here https://kotlinlang.org/docs/reference/coding-conventions.html

## Testing
Tests are defined under seperate packages com.travelcy.travelcy (test) and com.travelcy.travelcy (androidTest), under which we define our tests, mocks of repositories/services etc.
Creating tests is a necessary part of our workflow, and it is taken into consideration when reviewing pull requests.

For running the tests in Android Studio - see this documentation https://developer.android.com/studio/test/#run_a_test 
For explanation on how the tests are run in our CI/CD pipeline, see the CI/CD section below.

## Setting up the development environment
1. Download and install Android Studio https://developer.android.com/studio/
2. Set up git on your machine if you haven't already and clone the repository from here https://github.com/Travelcy/Travelcy
3. You may have to download and install the most recent java sdk https://www.oracle.com/java/technologies/javase-jdk13-downloads.html

## Pull request/review policy
For every new feature or bug fix you should create a branch based on develop. When work is done you create a pull request in github and wait for it to be reviewed.

When reviewing a pull request, there are certain standards that need to be met. Are coding conventions being met, have relevant tests been created, does everything build and run accordingly, is there redundant code, are there obvious security flaws?

Pull request merging is blocked until the test run is successful and at least one reviewer has approved.

## CI/CD
Our CI/CD is configured with github actions and fastlane: https://github.com/Travelcy/Travelcy/actions
https://fastlane.tools/ 

Depending on the action - push to a branch based on develop, pull request from a branch to develop, etc.. a relevant workflow in github actions will run, currently we have 3 workflows: fastlane-production, fastlane-test and fastlane-beta. The beta and production workflows build the app and deploy it to the relevant destination, and the test workflow runs relevant tests and creates an artifact containing the result.

In more detail on how the workflows are triggered:
Creating a pull request from <branch_a> to develop or master will trigger fastlane-tests
Pushing or merging to develop will trigger fastlane-tests and fastlane-beta (runs only if fastlane-tests succeeds)
And finally pushing or merging to master from a branch triggers fastlane-tests and fastlane-production (runs only if fastlane-tests succeeds)

In more detail on what each workflow does:
fastlane-beta simply deploys the app to the play store for internal testing and bumps the version.

fastlane-production deploys the app to the play store for release and generates a user guide and operations manual, and also bumps the version.

As mentioned before both these workflows depend on fastlane-tests to be successful - this workflow generates an artifact containing the results from our tests.

If a test fails you may in some cases want to re-run it, to do so navigate to the relevant test run from github-actions and re-run the test from there. Github actions also show you logs of what failed.

[![Screenshot-2020-10-28-at-16-13-29.png](https://i.postimg.cc/sxDqYSkY/Screenshot-2020-10-28-at-16-13-29.png)](https://postimg.cc/k6zT9VXG)

[![Screenshot-2020-10-28-at-16-16-15.png](https://i.postimg.cc/HxjPjHXz/Screenshot-2020-10-28-at-16-16-15.png)](https://postimg.cc/qhVGZPm6)

Our fastlane configuration can be found in the fastlane folder under the root of the project. The fastfile contains the steps each workflow takes.


## Bug reporting
For bug reporting we use bugsnag https://www.bugsnag.com , it reports unhandled exceptions, counts  along with useful details to ease the bugfixing process.

By logging in with Travelcys' credentials, you will have an overview of everything bugsnag reports from the app.

We configured bugsnag in Travelcy by following their documentation https://docs.bugsnag.com/platforms/android/

Under manifests/AndroidManifest.xml in the project folder, we configured the API Key necessary to communicate with our BugSnag space

[![Screenshot-2020-10-28-at-16-38-26.png](https://i.postimg.cc/PrTjSvj8/Screenshot-2020-10-28-at-16-38-26.png)](https://postimg.cc/Z072RR0T)

In the MainApplication file, BugSnag is started in the overrided onCreate function

[![Screenshot-2020-10-28-at-16-41-07.png](https://i.postimg.cc/8P4V9kpX/Screenshot-2020-10-28-at-16-41-07.png)](https://postimg.cc/LnJ7ZpZj)

This gives the app all the default functionality of bugsnag for bug reports.

## Analytics
For analytics we use Firebase https://firebase.google.com/ , it gives us useful data on the demographic of our users, retention (do users only use the app for a certain period of time), etc.

We configured firebase in Travelcy by following their documentation https://firebase.google.com/docs/android/setup

[![Screenshot-2020-10-28-at-18-40-37.png](https://i.postimg.cc/43XqDPTT/Screenshot-2020-10-28-at-18-40-37.png)](https://postimg.cc/JsdpJbnT)

Here you can see an example of how we use firebase - everytime a user enters the convert screen, a firebase log is performed, this gives us useful data for what features of the app the users use the most, and more.

[![image.png](https://i.postimg.cc/6Q0qKM2V/image.png)](https://postimg.cc/bGZpHHRd)

[![image-1.png](https://i.postimg.cc/xdY8BwCT/image-1.png)](https://postimg.cc/87KT6nMq)

[![image-2.png](https://i.postimg.cc/1t3t3Dwk/image-2.png)](https://postimg.cc/4KjZ878P)


## Ads
For ads we use Google Admob https://admob.google.com/home/, ads are our source of revenue. Currently we have one configured one ad displayed in all of the screens in the least obtrusive way we could think of, see the image below.

[![Screenshot-2020-11-06-at-19-40-31.png](https://i.postimg.cc/DZvn2TgQ/Screenshot-2020-11-06-at-19-40-31.png)](https://postimg.cc/p96gZ33y)

To understand how we configured Google Admob for the project, see the images below - they show the steps we took.

[![Screenshot-2020-11-06-at-19-43-07.png](https://i.postimg.cc/dQmvhDX2/Screenshot-2020-11-06-at-19-43-07.png)](https://postimg.cc/bdJ4MYqr)

[![Screenshot-2020-11-06-at-19-47-26.png](https://i.postimg.cc/tCBjzw97/Screenshot-2020-11-06-at-19-47-26.png)](https://postimg.cc/1fV2mWnZ)

[![Screenshot-2020-11-06-at-19-51-02.png](https://i.postimg.cc/prxH0t95/Screenshot-2020-11-06-at-19-51-02.png)](https://postimg.cc/TLkFwFJT)

[![Screenshot-2020-11-06-at-19-54-32.png](https://i.postimg.cc/VvRcpHMG/Screenshot-2020-11-06-at-19-54-32.png)](https://postimg.cc/BPXy1pmH)

[![Screenshot-2020-11-06-at-19-56-02.png](https://i.postimg.cc/WbNVgTLQ/Screenshot-2020-11-06-at-19-56-02.png)](https://postimg.cc/8s0xV8CH)

And the image below shows our google admob portal

[![image-3.png](https://i.postimg.cc/Y9Q7fSSw/image-3.png)](https://postimg.cc/Q9Nvjh56)
