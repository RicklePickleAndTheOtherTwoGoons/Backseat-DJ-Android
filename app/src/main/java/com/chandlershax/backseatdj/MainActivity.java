package com.chandlershax.backseatdj;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.spotify.sdk.android.authentication.AuthenticationClient;
import com.spotify.sdk.android.authentication.AuthenticationRequest;
import com.spotify.sdk.android.authentication.AuthenticationResponse;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.ConnectionStateCallback;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.PlayerEvent;
import com.spotify.sdk.android.player.Spotify;
import com.spotify.sdk.android.player.SpotifyPlayer;
import com.spotify.sdk.android.player.PlaybackState;


public class MainActivity extends Activity implements
        SpotifyPlayer.NotificationCallback, ConnectionStateCallback {

    private static final String CLIENT_ID = "7fdfe9f5f2af40efb7dd2b85b77edefd";
    // TODO: Replace with your redirect URI
    private static final String REDIRECT_URI = "backseatdj://callback";

    private Player mPlayer;
    private PlaybackState mCurrentPlaybackState;


    // Request code that will be used to verify if the result comes from correct activity
    // Can be any integer
    private static final int REQUEST_CODE = 1337;

    private final Player.OperationCallback mOperationCallback = new Player.OperationCallback() {
        @Override
        public void onSuccess() {
           System.out.println("SUCCESS!");
        }

        @Override
        public void onError(Error error) {
            System.out.println("NOT OK");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Authentication bullshit
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AuthenticationRequest.Builder builder = new AuthenticationRequest.Builder(CLIENT_ID,
                AuthenticationResponse.Type.TOKEN,
                REDIRECT_URI);

        //Change scope over here          vvvvvvvvvvvvvvvvvv
        builder.setScopes(new String[]{"user-read-private", "streaming"});
        AuthenticationRequest request = builder.build();

        AuthenticationClient.openLoginActivity(this, REQUEST_CODE, request);

        QueueController.GetNextSong();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // Check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthenticationResponse response = AuthenticationClient.getResponse(resultCode, intent);
            if (response.getType() == AuthenticationResponse.Type.TOKEN) {
                Config playerConfig = new Config(this, response.getAccessToken(), CLIENT_ID);
                Spotify.getPlayer(playerConfig, this, new SpotifyPlayer.InitializationObserver() {
                    @Override
                    public void onInitialized(SpotifyPlayer spotifyPlayer) {
                        mPlayer = spotifyPlayer;
                        mPlayer.addConnectionStateCallback(MainActivity.this);
                        mPlayer.addNotificationCallback(MainActivity.this);
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("MainActivity", "Could not initialize player: " + throwable.getMessage());
                    }
                });
            }
        }
    }

    @Override
    protected void onDestroy() {
        Spotify.destroyPlayer(this);
        super.onDestroy();
    }

    @Override
    public void onPlaybackEvent(PlayerEvent playerEvent) {
        Log.d("MainActivity", "Playback event received: " + playerEvent.name());
        mCurrentPlaybackState = mPlayer.getPlaybackState();
        }

    @Override
    public void onPlaybackError(Error error) {
        Log.d("MainActivity", "Playback error received: " + error.name());
        switch (error) {
            // Handle error type as necessary
            default:
                break;
        }
    }

    @Override
    public void onLoggedIn() {
        Log.d("MainActivity", "User logged in");
        TextView tv = findViewById(R.id.txtWorld);
        mPlayer.playUri(null, "spotify:track:20R2rF8szcx4VNA6FDRKwo", 0, 0);
        tv.setText("If you're dad is name mark for the love of god clap your hands");

    }

    @Override
    public void onLoggedOut() {
        Log.d("MainActivity", "User logged out");
    }

    @Override
    public void onLoginFailed(Error e) {
        Log.d("MainActivity", "Login failed");
    }

    @Override
    public void onTemporaryError() {
        Log.d("MainActivity", "Temporary error occurred");
    }

    @Override
    public void onConnectionMessage(String message) {
        Log.d("MainActivity", "Received connection message: " + message);
    }

    public void btnTest_OnClick(View v) {

        TextView tv = findViewById(R.id.txtWorld);
        tv.setText("Stop fucking clapping!");
        if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying) {
            mPlayer.pause(mOperationCallback);
            tv.setText("Stop fucking clapping!");
        } else {
            mPlayer.resume(mOperationCallback);
            tv.setText("CLAP MOTHERFUCKERS!");
        }

    }

    public void buttonStartRooom_onClick(View v) {

        // Send a request for a new room id to firebase.
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();
        // Wait for a response from firebase.

        // Set the roomCodeoutput textbox to the code we got back.
        EditText roomCodeOutput = findViewById(R.id.editRoomCodeOutput);
        roomCodeOutput.setText("CODE");
    }

    public void buttonSubmit_onClick(View v) {
        // Gather the URL and Room Code from the appropriate text boxes.
        EditText roomCodeInput = findViewById(R.id.editRoomCodeInput);
        Editable roomCodeEditable = roomCodeInput.getText();
        String roomCode = roomCodeEditable.toString();

        EditText urlInput = findViewById(R.id.editSpotifyLink);
        Editable urlEditable = roomCodeInput.getText();
        String urlCode = roomCodeEditable.toString();

        // Send firebase the URL and the Room Code.
        DatabaseReference db = FirebaseDatabase.getInstance().getReference();

    }
}






