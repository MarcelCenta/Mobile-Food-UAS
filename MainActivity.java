package com.example.speechtext;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_SPEECH_INPUT=1000;

    ImageButton microphone_button;
    EditText search_edittext;
    Button search_button;

    String url;


    ImageButton newbtn;

    TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        microphone_button = findViewById(R.id.microphone);
        search_edittext = findViewById(R.id.intext);
        search_button = findViewById(R.id.searchbutton);

        //Button btn = findViewById(R.id.btn1);

        microphone_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
    }

    public void toasting(){
        Toast.makeText(this,"button", Toast.LENGTH_SHORT).show();
    }


    public void addButton(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.linlay);
        newbtn = new ImageButton(this);
        newbtn.setBackgroundResource(R.drawable.pizza);
        invalidateOptionsMenu();

        //.with(this).load("http://res.cloudinary.com/dk4ocuiwa/image/upload/v1618257399/RecipesApi/Pizza.png").resize(480,480).into(newbtn);
        layout.addView(newbtn);
    }

    public void searchFood(View view){
        url="http://recipesapi.herokuapp.com/api/v2/recipes?q="+search_edittext.getText().toString(); //ini cara naruh url
        Ion.with(this)
                .load(url)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        processFoodData(result);
                    }
                });

    }

    private void processFoodData(String data){
        try{
            JSONObject json = new JSONObject(data);
            JSONArray uArray = json.getJSONArray("recipes");
            GridLayout gri = (GridLayout) findViewById(R.id.grid);
            gri.removeAllViews();
            for (int i = 0;i<uArray.length();i++){
                try {
                    JSONObject udet = uArray.getJSONObject(i);
                    String image = udet.getString("imageUrl");
                    String title = udet.getString("title");
                    addfood(title, image, gri);
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

        } catch (Exception e){
            Log.wtf("json",e);
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }


    public void addfood(String title, String imageUrl, GridLayout lin) {
        View lala = getLayoutInflater().inflate(R.layout.griddynamic, null);
        ImageButton img = (ImageButton) lala.findViewById(R.id.foodImagee);
        TextView tit = (TextView)lala.findViewById(R.id.foodTitlee);
        tit.setText(title);
        Picasso.with(this).load(imageUrl).resize(450,450).into(img);
        lin.addView(lala);
    }



    public void speak(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Say one word of any food...");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT:{
                if(resultCode == RESULT_OK && null != data){
                    ArrayList<String>  result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    url="http://recipesapi.herokuapp.com/api/v2/recipes?q="+result.get(0); //ini cara naruh url
                    search_edittext.setText(result.get(0));
                }
            }
        }
    }
}