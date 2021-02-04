package com.puppyland.mongnang.presenter;

import android.util.Log;

import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.contract.ImageContract;
import com.puppyland.mongnang.model.ImageModel;

public class ImagePresenter implements ImageContract.Presenter {

    ImageContract.View view;
    ImageModel imageModel;

    public ImagePresenter(ImageContract.View view) {
        this.view = view;
        imageModel = new ImageModel(this);
    }

    @Override
    public void image(String imageName, String UID) {
        imageModel.insertImage(imageName, UID);
        Log.d("image", imageName);
    }

    //내 사진 파일이름.
    @Override
    public void fileName(String myID) {
        view.getMyImage(imageModel.fileName(myID));
    }

    @Override
    public void userInfoUpdate(MemberDTO member) {
        imageModel.userInfoUpdate(member);
    }
}
