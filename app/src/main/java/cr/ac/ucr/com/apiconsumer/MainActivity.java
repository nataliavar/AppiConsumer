package cr.ac.ucr.com.apiconsumer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cr.ac.ucr.com.apiconsumer.api.RetrofitBuilder;
import cr.ac.ucr.com.apiconsumer.api.WeatherService;
import cr.ac.ucr.com.apiconsumer.models.Main;
import cr.ac.ucr.com.apiconsumer.models.Sys;
import cr.ac.ucr.com.apiconsumer.models.Weather;
import cr.ac.ucr.com.apiconsumer.models.WeatherResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private String TAG = "MainActivity";
    private ConstraintLayout clContainer;
    private TextView tvGreeting;
    private TextView tvTemperature;
    private TextView tvDescription;
    private TextView tvCity;
    private TextView tvMinMax;
    private ImageView ivImage;
    private String day;
    Location location;
    private LocationManager locationManager;
    private final int LOCATION_CODE_REQUEST = 1;
    private double lattitude;
    private double longitude;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        clContainer = findViewById(R.id.cl_container);
        tvGreeting = findViewById(R.id.tv_greetings);
        tvDescription = findViewById(R.id.tv_content);
        tvTemperature = findViewById(R.id.tv_temperature);
        tvCity = findViewById(R.id.tv_city);
        tvMinMax = findViewById(R.id.tv_min_max);
        ivImage = findViewById(R.id.iv_image);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        checkPermissions();


        lattitude = 9.99447;
        longitude = -84.66466;

        setBackgroundGreeting();
        getWeather(lattitude, longitude);
    }

    private void checkPermissions() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_DENIED) {
            }
            {
                requestPermissions(new String[]{
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION,
                        },
                        LOCATION_CODE_REQUEST
                );
                return;
            }

        }

        location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        try {
            if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                onLocationChanged(location);
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("Para una mejor funcionalidad, activa el GPS")
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                            }
                        })
                        .setNegativeButton("Cancel", null)
                        .show();

                getWeather(lattitude,longitude);

                //TODO: latlong default
            }
        } catch (Exception e) {
          //  e.printStackTrace();
            //TODO: latlong default
            getWeather(lattitude,longitude);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_CODE_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkPermissions();
                //TODO: latlong default
            } else {
                //--
                getWeather(lattitude,longitude);

            }
        }
    }


    private void setBackgroundGreeting() {
        Calendar calendar = Calendar.getInstance();
        int timeOfday = calendar.get(Calendar.HOUR_OF_DAY);

        if (timeOfday > 0 && timeOfday < 12) {
            tvGreeting.setText(R.string.day_greeting);
            clContainer.setBackgroundResource(R.drawable.background_day);
        } else if (timeOfday < 18) {
            tvGreeting.setText(R.string.afternoon_greeting);
            clContainer.setBackgroundResource(R.drawable.background_afternoon);
        } else {
            tvGreeting.setText(R.string.night_greeting);
            clContainer.setBackgroundResource(R.drawable.background_night);
        }

        day = calendar.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.getDefault());
    }

    private void getWeather(double lattitude, double longitude) {
        WeatherService service = RetrofitBuilder.createService(WeatherService.class);

        Call<WeatherResponse> response = service.getWeatherByCoordinates(0, 0);
        final AppCompatActivity activity = this;

        response.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(@NonNull Call<WeatherResponse> call, @NonNull Response<WeatherResponse> response) {
                Log.i(TAG, String.valueOf(call.request().url()));

                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();

                    Main main = weatherResponse.getMain();

                    List<Weather> weatherList = weatherResponse.getWeather();
                    Sys sys = weatherResponse.getSys();

                    String temperature = getString(R.string.temperarture, String.valueOf(Math.round(main.getTemp())));
                    tvTemperature.setText(temperature);

                    String minmax = getString(R.string.minmax, String.valueOf(Math.round(main.getTemp_min())), String.valueOf(Math.round(main.getTemp_max())));
                    tvMinMax.setText(minmax);

                    if (weatherList.size() > 0) {
                        Weather weather = weatherList.get(0);
                        tvDescription.setText(String.format("%s, %s", day, weather.getDescription()));
                        String imageURL = String.format("https://openweathermap.org/img/wn/%s@2x.png", weather.getIcon());
                        RequestOptions options = new RequestOptions()
                                .placeholder(R.mipmap.ic_launcher)
                                .error(R.mipmap.ic_launcher)
                                .centerCrop()
                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                .priority(Priority.HIGH);
                        Glide.with(activity)
                                .load(imageURL)
                                .into(ivImage);
                    }

                    tvCity.setText(String.format("$s, $s,", weatherResponse.getName(), sys.getCountry()));


                    //TODO TERMINAR DE CARGAR el weather
                } else {
                    Log.e(TAG, "OnError:" + response.errorBody().toString());
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeatherResponse> call, @NonNull Throwable t) {
                throw new RuntimeException(t);
            }
        });
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

        // Log.i(location.getLatitude() + "" + location.getLongitude());
        lattitude = location.getLatitude();
        longitude = location.getLongitude();
        getWeather(lattitude,longitude);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }

}
