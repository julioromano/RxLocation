![Build status]
(https://travis-ci.org/julioromano/RxLocation.svg?branch=master)

# RxLocation

An Android Library to get location updates with a nice, clean, user friendly rxJava API. Also takes care of runtime permissions (on Android M+).

## Installation

Add this to your gradle.conf:

```
dependencies {
    compile 'net.kjulio.RxLocation:rxlocation:0.3-alpha'
}
```

## Usage

```
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
