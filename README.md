# Android Image Searcher

## Overview

The goal of this project is to display defaults photos and search photos to display from [Pixabay image API][pixabay].  Additionally this project supports saving photos to database as well marking photos as favorite.

Image searcher application has 3 tabs setup as mentioned below :

- "All" tab is the default selected tab when user launches the application which then starts to download images from Pixabay Image API and loads them as Thumbnail in 2 column Gridlayout. User can click on any thumbnail to view a slide show of all downloaded images starting from the photo user clicked and then swipte left or right to view other photos.
  During slideshow mode user can save photo or add to favorite which in turn saves photo using the menu available at the right top corner showing 3 dots.
  
- "Saved" tab just loads the saved images if any in same format as All tabs. If not saved images, found then an empty with default message and a button to save photos. By clicking on photo in Saved tab turns on slide show mode only for the saved photo.
  Photo can be unsaved by going following the same process of saving photo by going to menu on top right corner and clicking on unsave.
  
- "Favorite" is very similar to Saved tab with only one distinction is if you mark a photo as favorite which isn't saved, then automatically photo will be saved too but not vice versa. If a photo is unsave then automatically it's removed from favorites as well but removing photo from Favorites doesn't unsave photo.

## Installation

- Ensure you have the latest version of Android Studio and the v23 Android Build Tools.

- Fork and clone the repo.

- Open up the project with Android Studio. You can build the project using [Gradle][gradle].

## API Reference

https://pixabay.com/api/docs/

## Required Libraries

- Android appcompat v7
- Android design
- Android support v4
- Android cardview v7
- Squareup Picasso
- Squareup Retrofit
- Greenrobot Eventbus

## Tests

This project doesn't include any unit tests.

## Known Issues

- This application crashes when screen is rotated during slide show mode. 

## Contributors

[Altspace](https://github.com/AltspaceVR)

[Chandra Gopalaiah](https://github.com/gcp1304)

