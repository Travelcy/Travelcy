# Travelcy Operations Manual

## Overview
Travelcy is a native android application developed in Kotlin with Android Studio.

## Coding conventions
We have been following Kotlins' standard coding convention, which can be found here https://kotlinlang.org/docs/reference/coding-conventions.html

## Testing
Tests are defined under seperate packages com.travelcy.travelcy (test) and com.travelcy.travelcy (androidTest), under which we define our tests, mocks of repositories/services etc.
Creating tests is a necessary part of our workflow, and it is taken into consideration when reviewing pull requests.

## Setting up the development environment
1. Download and install Android Studio https://developer.android.com/studio/
2. Set up git on your machine if you haven't already and clone the repository from here https://github.com/Travelcy/Travelcy
3. You may have to download and install the most recent java sdk https://www.oracle.com/java/technologies/javase-jdk13-downloads.html

## Pull request/review policy
For every new feature or bug fix you should create a branch based on develop. When work is done you create a pull request in github and wait for it to be reviewed.

When reviewing a pull request, there are certain standards that need to be met. Are coding conventions being met, have relevant tests been created, does everything build and run accordingly, is there redundant code?

Pull request merging is blocked until the test run is successful and at least one reviewer has approved.

## CI/CD
Our CI/CD is configured with github actions and fastlane: https://github.com/Travelcy/Travelcy/actions
https://fastlane.tools/ 

Depending on the action - push to a branch based on develop, pull request from a branch to develop etc.. a relevant workflow in github actions will run, currently we have 3 workflows: fastlane-production, fastlane-test and fastlane-beta. The beta and production workflows build the app and deploy it to the relevant destination, and the test workflow runs relevant tests.

## Bug reporting
For bug reporting we use bugsnag https://www.bugsnag.com , it reports unhandled exceptions along with useful details to ease the bugfixing process.

## Analytics
For analytics we use Firebase https://firebase.google.com/ , it gives us useful data on the demographic of our users, retention (do users only use the app for a certain period of time), etc.

## Ads
For ads we use Google Admob https://admob.google.com/home/ 
