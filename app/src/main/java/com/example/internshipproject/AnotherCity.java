package com.example.internshipproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import androidx.annotation.Nullable;

public class AnotherCity extends Activity {

   EditText editText,display;
     TextView cityName;
     TextView  CityTemp, Day, MinTemp, MaxTemp, Humidity, Visibility, Pressure, Wind;
     ImageView Image;
     Button BackToPrevious;
     String cityURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_city);
        editText = findViewById(R.id.anotherCity);
        BackToPrevious = findViewById(R.id.back);
        Image = findViewById(R.id.WeatherImage);
        CityTemp = findViewById(R.id.Temperature);
        Day = findViewById(R.id.DayType);
        MinTemp = findViewById(R.id.MinimumTemperature);
        MaxTemp = findViewById(R.id.MaximumTemperature);
        Humidity = findViewById(R.id.HumidValue);
        Visibility = findViewById(R.id.VisibilityValue);
        Pressure = findViewById(R.id.PressureValue);
        Wind = findViewById(R.id.WindValue);
        cityName = findViewById(R.id.newCityName);

    }

    public void BackToPrevious(View view)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

     public void checkWeather(View view) {
         try{
             DownloadTask task = new DownloadTask();
             String placeName = URLEncoder.encode(editText.getText().toString(),"UTF-8");
             Log.i("place",placeName);
             cityName.setText(placeName);
             cityURL = ("https://openweathermap.org/data/2.5/weather?q="+ placeName + "&appid=439d4b804bc8187953eb36d2a8c26a02");
             InputMethodManager methodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
             methodManager.hideSoftInputFromWindow(editText.getWindowToken(),0);
             task.execute(cityURL);
             Log.i("URL",cityURL);
         }
         catch (Exception e)
         {
             e.printStackTrace();
             Toast.makeText(AnotherCity.this,"No matches found",Toast.LENGTH_SHORT).show();
         }
    }

     public class DownloadTask extends AsyncTask<String,Void,String> {

         @Override
         protected String doInBackground(String... urls) {
             URL url;
             HttpURLConnection urlConnection = null;
             String result = "";
             try {
                 url = new URL(urls[0]);
                 urlConnection = (HttpURLConnection) url.openConnection();
                 InputStream in = urlConnection.getInputStream();
                 InputStreamReader reader = new InputStreamReader(in);
                 int data = reader.read();
                 while (data != -1) {
                     char current = (char) data;
                     result += current;
                     data = reader.read();
                 }
                 return result;
             } catch (Exception e) {
                 e.printStackTrace();
                 Toast.makeText(AnotherCity.this, "No matches found", Toast.LENGTH_SHORT).show();
                 return null;
             }

         }

         @Override
         protected void onPostExecute(String s) {
             super.onPostExecute(s);
             if( s != "" && s != null)
             {
                 s = s.substring(4,s.length());
                 Log.i("JSON : ",s);

                 String res = "";
                 String checkString = "";

                 double temperature, maxTemp,minTemp,humid,visible,wind, pressure;
                 String weather = "",description ="";

                 try {
                     JSONObject jsonObject = new JSONObject(s);
                     String weatherInfo = jsonObject.getString("main");

                     Log.i("Weather Content", weatherInfo);

                     JSONObject obj = new JSONObject(weatherInfo);

                     //Getting the temperature
                     Log.i("temp", obj.getString("temp"));
                     temperature = obj.getDouble("temp");
                     CityTemp.setText(Double.toString(temperature)+(char) 0x00B0);

                     //Getting the Max Temperature
                     maxTemp = obj.getDouble("temp_max");
                     MaxTemp.setText(Double.toString(maxTemp)+(char) 0x00B0);

                     //Getting the Min temperature
                     minTemp = obj.getDouble("temp_min");
                     MinTemp.setText(Double.toString(minTemp)+(char) 0x00B0);

                     //Getting and setting the pressure
                     pressure = obj.getDouble("pressure");
                     Pressure.setText(Double.toString(pressure));

                     //Getting and setting humidity value
                     humid = obj.getDouble("humidity");
                     Humidity.setText(Double.toString(humid));



                     //Getting the weather info
                     weatherInfo = jsonObject.getString("weather");
                     JSONArray array = new JSONArray(weatherInfo);

                     for( int i=0; i<array.length(); i++ )
                     {
                         JSONObject jsonPart = array.getJSONObject(i);

                         weather = jsonPart.getString("main");
                         description = jsonPart.getString("description");
                     }

                     //Setting the weather type

                     Day.setText(weather);


                     //Getting and Setting the Wind

                     weatherInfo = jsonObject.getString("wind");
                     obj = new JSONObject(weatherInfo);

                     wind = obj.getDouble("speed");
                     Wind.setText(Double.toString(wind)+" km/h");


                     //Gwtting and setting the visibility

                     weatherInfo = jsonObject.getString("visibility");
                     obj = new JSONObject(weatherInfo);

                     visible = obj.getDouble("visibility");
                     Visibility.setText(Double.toString(visible));

                     res += weather + ", "+ description + "\n" + "Temperature : " + temperature;
                     checkString = weather+description+temperature;
                     if(checkString == "")
                     {
                         Toast.makeText(getApplicationContext(),"Invalid City Name",Toast.LENGTH_SHORT).show();
                     }
                     else
                         display.setText(res);
                 }
                 catch (Exception e) {
                     e.printStackTrace();
                     Toast.makeText(getApplicationContext(),"Invalid City Name",Toast.LENGTH_SHORT).show();
                     display.setText("");
                 }
             }
             else {
                 Toast.makeText(getApplicationContext(), "Invalid City Name", Toast.LENGTH_SHORT).show();
                 display.setText("");
             }


         }
     }
     public void setImage(String weatherType){
         if(weatherType.equals("Haze"))
         {
             Image.setImageResource(R.drawable.haze);
         }
         else  if(weatherType.equals("Clouds"))
         {
             Image.setImageResource(R.drawable.cloudy);
         }
         else  if(weatherType.equals("Clear"))
         {
             Image.setImageResource(R.drawable.sunny);
         }
         else  if(weatherType.equals("Rain"))
         {
             Image.setImageResource(R.drawable.rainy);
         }
         else  if(weatherType.equals("Thunderstorm"))
         {
             Image.setImageResource(R.drawable.thunder);
         }

     }

}
