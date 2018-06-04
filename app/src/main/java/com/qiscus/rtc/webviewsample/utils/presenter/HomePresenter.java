package com.qiscus.rtc.webviewsample.utils.presenter;

import com.qiscus.sdk.data.model.QiscusChatRoom;

import java.util.List;

import data.ChatRoomRepository;
import data.UserRepository;

/**
 * Created by fitra on 31/05/18.
 */

public class HomePresenter {
    private View view;
    private ChatRoomRepository chatRoomRepository;
    private UserRepository userRepository;

    public HomePresenter(View view, ChatRoomRepository chatRoomRepository, UserRepository userRepository) {
        this.view = view;
        this.chatRoomRepository = chatRoomRepository;
        this.userRepository = userRepository;
    }

    public void loadChatRooms() {
        chatRoomRepository.getChatRooms(chatRooms -> view.showChatRooms(chatRooms),
                throwable -> view.showErrorMessage(throwable.getMessage()));
    }

    public void createChatRoom() {
        view.showContactPage();
    }

    public void openChatRoom(QiscusChatRoom chatRoom) {
        view.showChatRoomPage(chatRoom);
    }

    public void logout() {
        userRepository.logout();
        view.showMainPage();
    }

    public interface View {
        void showChatRooms(List<QiscusChatRoom> chatRooms);
        void showChatRoomPage(QiscusChatRoom chatRoom);
        void showContactPage();
        void showErrorMessage(String errorMessage);
        void showMainPage();
    }
}
