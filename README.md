# RxLocation

An Android Library to get location updates with a nice, clean, user friendly rxJava API. Also takes care of runtime permissions (on Android M+).

![Build status]
(https://travis-ci.org/julioromano/RxLocation.svg?branch=master)

RxLocation.initDefaultInstance(context);

RxLocation rxLocation = RxLocation.getDefaultInstance();


LocationRequest locationRequest = new LocationRequest...

rxLocation.locationObservable(locationRequest).subscribe(new Action1)...
