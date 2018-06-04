package com.qiscus.rtc.webviewsample;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.qiscus.rtc.webviewsample.basic.CallActivity;
import com.qiscus.rtc.webviewsample.utils.AsyncHttpUrlConnection;
import com.qiscus.sdk.Qiscus;
import com.qiscus.sdk.data.model.QiscusChatRoom;
import com.qiscus.sdk.data.model.QiscusRoomMember;
import com.qiscus.sdk.ui.QiscusBaseChatActivity;
import com.qiscus.sdk.ui.fragment.QiscusBaseChatFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class CustomChatActivity extends QiscusBaseChatActivity {
    private static final String TAG = CustomChatActivity.class.getSimpleName();

    private AsyncHttpUrlConnection httpConnection;
    private TextView title;
    private ImageView back;
    private ImageView voiceCall;
    private ImageView videoCall;

    public static Intent generateIntent(Context context, QiscusChatRoom qiscusChatRoom) {
        Intent intent = new Intent(context, CustomChatActivity.class);
        intent.putExtra(CHAT_ROOM_DATA, qiscusChatRoom);
        return intent;
    }

    @Override
    protected int getResourceLayout() {
        return R.layout.activity_custom_chat;
    }

    @Override
    protected void onLoadView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        title = (TextView) findViewById(R.id.tv_title);
        back = (ImageView) findViewById(R.id.back);
        videoCall = (ImageView) findViewById(R.id.video_call);
    }

    @Override
    protected QiscusBaseChatFragment onCreateChatFragment() {
        return CustomChatFragment.newInstance(qiscusChatRoom);
    }

    @Override
    protected void onViewReady(Bundle savedInstanceState) {
        super.onViewReady(savedInstanceState);
        title.setText(qiscusChatRoom.getName());
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        videoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (QiscusRoomMember member : qiscusChatRoom.getMember()) {
                    if (!member.getEmail().equalsIgnoreCase(Qiscus.getQiscusAccount().getEmail())) {
                        startVideoCall(member);
                    }
                }
            }
        });
    }

    @Override
    public void onUserStatusChanged(String user, boolean online, Date lastActive) {
        //
    }

    @Override
    public void onUserTyping(String user, boolean typing) {
        //
    }

    private void startVideoCall(final QiscusRoomMember target) {
        JSONObject request = new JSONObject();
        JSONObject payload = new JSONObject();
        JSONObject caller = new JSONObject();
        JSONObject callee = new JSONObject();
        final String roomId = Config.CHAT_APP_ID + "_" + String.valueOf(System.currentTimeMillis());

        try {
            request.put("system_event_type", "custom");
            request.put("room_id",String.valueOf(qiscusChatRoom.getId()));
            request.put("subject_email", target.getEmail());
            request.put("message", Qiscus.getQiscusAccount().getUsername() + " call " + target.getUsername());
            payload.put("type", "webview_call");
            payload.put("call_event", "incoming");
            payload.put("call_room_id", roomId);
            caller.put("username", Qiscus.getQiscusAccount().getEmail());
            caller.put("name", Qiscus.getQiscusAccount().getUsername());
            caller.put("avatar", Qiscus.getQiscusAccount().getAvatar());
            callee.put("username", target.getEmail());
            callee.put("name", target.getUsername());
            callee.put("avatar", target.getAvatar());
            payload.put("call_caller", caller);
            payload.put("call_callee", callee);
            request.put("payload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        httpConnection = new AsyncHttpUrlConnection("POST", "/api/v2/rest/post_system_event_message", request.toString(), new AsyncHttpUrlConnection.AsyncHttpEvents() {
            @Override
            public void onHttpError(String errorMessage) {
                Log.e(TAG, "API connection error: " + errorMessage);
            }

            @Override
            public void onHttpComplete(String response) {
                Log.d(TAG, "API connection success: " + response);
                try {
                    JSONObject objStream = new JSONObject(response);
                    if (objStream.getInt("status") == 200) {
                        Intent intent = new Intent(CustomChatActivity.this, CallActivity.class);
                        intent.putExtra("call_room_id", roomId);
                        intent.putExtra("callee_name", target.getUsername());
                        intent.putExtra("callee_email", target.getEmail());
                        intent.putExtra("callee_avatar", target.getAvatar());
                        startActivity(intent);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }
        });
        httpConnection.setContentType("application/json");
        httpConnection.send();
    }
}
