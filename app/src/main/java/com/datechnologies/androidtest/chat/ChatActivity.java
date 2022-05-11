package com.datechnologies.androidtest.chat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.datechnologies.androidtest.MainActivity;
import com.datechnologies.androidtest.R;
import com.datechnologies.androidtest.api.ChatLogMessageModel;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Screen that displays a list of chats from a chat log.
 */
public class ChatActivity extends AppCompatActivity {

    //==============================================================================================
    // Class Properties
    //==============================================================================================

    private RecyclerView recyclerView;
    private ChatAdapter chatAdapter;

    //==============================================================================================
    // Static Class Methods
    //==============================================================================================

    public static void start(Context context)
    {
        Intent starter = new Intent(context, ChatActivity.class);
        context.startActivity(starter);
    }

    //==============================================================================================
    // Lifecycle Methods
    //==============================================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        ActionBar actionBar = getSupportActionBar();

        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        setTitle(R.string.chat_title);
        chatAdapter = new ChatAdapter();

        recyclerView.setAdapter(chatAdapter);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(),
                LinearLayoutManager.VERTICAL,
                false));

        List<ChatLogMessageModel> tempList = new ArrayList<>();

        RequestQueue requestQueue = Volley.newRequestQueue(this);


        String url = "https://dev.rapptrlabs.com/Tests/scripts/chat_log.php";
        //displaying loading dialog
        ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();
        //making call to api to get messages
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        pDialog.cancel();
                        try {
                            Log.d("Here","Response data: " + response.getJSONArray("data"));
                            JSONArray jsonArray = response.getJSONArray("data");
                            //populating models with data
                            for(int i= 0; i < jsonArray.length(); i++){
                                ChatLogMessageModel chatLogMessageModel = new ChatLogMessageModel();
                                JSONObject obj = jsonArray.getJSONObject(i);
                                chatLogMessageModel.userId = obj.getInt("user_id");
                                chatLogMessageModel.message = obj.getString("message");
                                chatLogMessageModel.username = obj.getString("name");
                                chatLogMessageModel.avatarUrl = obj.getString("avatar_url");

                                //adding the populated model to the list
                                tempList.add(chatLogMessageModel);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        //setting adapter list
                        chatAdapter.setChatLogMessageModelList(tempList);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        pDialog.cancel();

                        Log.d("Here","Response: " + error.toString());

                    }
                });

        requestQueue.add(jsonObjectRequest);


    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
