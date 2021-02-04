package com.puppyland.mongnang.presenter;

import com.puppyland.mongnang.DTO.NotificationDTO;
import com.puppyland.mongnang.contract.NotificationContract;
import com.puppyland.mongnang.model.NotificationModel;

public class NotificationPresenter implements NotificationContract.Presenter {

    NotificationContract.View notificationView;
    NotificationModel notificationModel;

    public NotificationPresenter(NotificationContract.View view) {
        //뷰 연결
        notificationView = view;
        //model연결
        notificationModel = new NotificationModel(this);
    }


    @Override
    public void UpdateNotification(NotificationDTO dto) {
            notificationModel.UpdateNotification(dto);
    }

    @Override
    public void selectNotification(String userid) {

    }

    @Override
    public void goNotificationItem(String userid, String item, String no) {

    }
}
