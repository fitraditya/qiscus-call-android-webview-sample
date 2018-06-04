package data;

import com.qiscus.rtc.webviewsample.utils.Action;
import com.qiscus.rtc.webviewsample.utils.model.User;
import com.qiscus.sdk.Qiscus;
import com.qiscus.sdk.data.model.QiscusChatRoom;
import com.qiscus.sdk.data.remote.QiscusApi;

import java.util.List;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by fitra on 31/05/18.
 */

public class ChatRoomRepositoryImpl implements ChatRoomRepository {

    @Override
    public void getChatRooms(Action<List<QiscusChatRoom>> onSuccess, Action<Throwable> onError) {
        Observable.from(Qiscus.getDataStore().getChatRooms(100))
                .filter(chatRoom -> chatRoom.getLastComment() != null)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess::call, onError::call);

        QiscusApi.getInstance()
                .getChatRooms(1, 100, true)
                .flatMap(Observable::from)
                .doOnNext(qiscusChatRoom -> Qiscus.getDataStore().addOrUpdate(qiscusChatRoom))
                .filter(chatRoom -> chatRoom.getLastComment().getId() != 0)
                .toList()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess::call, onError::call);
    }

    @Override
    public void createChatRoom(User user, Action<QiscusChatRoom> onSuccess, Action<Throwable> onError) {
        QiscusChatRoom savedChatRoom = Qiscus.getDataStore().getChatRoom(user.getId());
        if (savedChatRoom != null) {
            onSuccess.call(savedChatRoom);
            return;
        }

        QiscusApi.getInstance()
                .getChatRoom(user.getId(), null, null)
                .doOnNext(chatRoom -> Qiscus.getDataStore().addOrUpdate(chatRoom))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onSuccess::call, onError::call);
    }
}

