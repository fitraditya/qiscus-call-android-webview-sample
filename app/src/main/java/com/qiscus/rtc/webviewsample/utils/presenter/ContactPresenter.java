package com.qiscus.rtc.webviewsample.utils.presenter;

import com.qiscus.rtc.webviewsample.utils.model.User;
import com.qiscus.sdk.data.model.QiscusChatRoom;

import java.util.List;

import data.ChatRoomRepository;
import data.UserRepository;

/**
 * Created by fitra on 31/05/18.
 */

public class ContactPresenter {
    private View view;
    private UserRepository userRepository;
    private ChatRoomRepository chatRoomRepository;

    public ContactPresenter(View view, UserRepository userRepository, ChatRoomRepository chatRoomRepository) {
        this.view = view;
        this.userRepository = userRepository;
        this.chatRoomRepository = chatRoomRepository;
    }

    public void loadContacts() {
        userRepository.getUsers(users -> view.showContacts(users),
                throwable -> view.showErrorMessage(throwable.getMessage()));
    }

    public void createRoom(User contact) {
        chatRoomRepository.createChatRoom(contact,
                chatRoom -> view.showChatRoomPage(chatRoom),
                throwable -> view.showErrorMessage(throwable.getMessage()));
    }

    public interface View {
        void showContacts(List<User> contacts);
        void showChatRoomPage(QiscusChatRoom chatRoom);
        void showErrorMessage(String errorMessage);
    }
}
