![Build status]
(https://travis-ci.org/julioromano/RxLocation.svg?branch=master)

# RxLocation

An Android Library to get location updates with a nice and clean [RxJava](https://github.com/ReactiveX/RxJava) API. Also takes care of runtime permissions (on Android M+).

## Features
 - Transparent management of GoogleApiClient: Connection to GoogleApiClient is automatically opened and closed when needed.
 - Location permission request (Android M+): Shall the app not have been granted location permission, the permission request dialog will pop-up automatically.
 - RxJava API: Get one time location updates using rx.Single or continuous location updates using rx.Observable.
 - Convenient error handling: In case the user doesn't grant location permission an error condition will be issued via rxJava's onError() API.

## Installation

Add this to your project's gradle.conf:
```groovy
allprojects {
    repositories {
        maven {
            url 'https://dl.bintray.com/julioromano/RxLocation'
        }
    }
}
```

Add this to your module's gradle.conf:

```groovy
dependencies {
    compile 'net.kjulio.RxLocation:rxlocation:0.3-alpha'
}
```

## Usage

```java
public class MainActivity extends AppCompatActivity {

    TextView textView;
    Button button;
    
    RxLocation rxLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        textView = (TextView) findViewById(R.id.textView);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonClick();
            }
        });

        rxLocation = RxLocation.newInstance(this);
    }

    private void onButtonClick() {
        rxLocation.locationSingle()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Location>() {
                    @Override
                    public void call(Location location) {
                        textView.setText(location.toString());
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        textView.setText(throwable.getLocalizedMessage());
                    }
                });
    }
}
```
