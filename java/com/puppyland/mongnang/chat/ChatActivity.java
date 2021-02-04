package com.puppyland.mongnang.chat;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.puppyland.mongnang.R;
import com.puppyland.mongnang.fragment.ChatFragment;
import com.puppyland.mongnang.fragment.UserListInRoomFragment;

public class ChatActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private ChatFragment chatFragment;
    private UserListInRoomFragment userListInRoomFragment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);



        String toUid = getIntent().getStringExtra("toUid");
        final String roomID = getIntent().getStringExtra("roomID");
        String chatuserImage = getIntent().getStringExtra("chatuserImage");
        String chatuserNickname = getIntent().getStringExtra("chatuserNickname");
     //   String roomTitle = getIntent().getStringExtra("roomTitle");

        /*
        // left drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        findViewById(R.id.rightMenuBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                    drawerLayout.closeDrawer(Gravity.RIGHT);
                } else {
                    if (userListInRoomFragment==null) {

                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.drawerFragment, UserListInRoomFragment.getInstance(roomID, chatFragment.getUserList()))
                                .commit();
                    }
                    drawerLayout.openDrawer(Gravity.RIGHT);
                }
            }
        });*/
        // chatting area
        chatFragment = ChatFragment.getInstance(toUid, roomID ,chatuserImage , chatuserNickname);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.mainFragment, chatFragment )
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        chatFragment.backPressed();
        finish();;
    }

}