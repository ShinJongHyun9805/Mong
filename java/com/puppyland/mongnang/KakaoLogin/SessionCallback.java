package com.puppyland.mongnang.KakaoLogin;

import android.util.Log;

import com.kakao.auth.ISessionCallback;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Gender;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
import com.puppyland.mongnang.contract.ChatuserListContract;
import com.puppyland.mongnang.contract.EmailCertificationContract;
import com.puppyland.mongnang.contract.FunctionCountContract;
import com.puppyland.mongnang.contract.MemberContract;

public class SessionCallback implements ISessionCallback , MemberContract.View, ChatuserListContract.View, EmailCertificationContract.View, FunctionCountContract.View {

    // 로그인에 성공한 상태
    @Override
    public void onSessionOpened() {
        requestMe();
    }

    // 로그인에 실패한 상태
    @Override
    public void onSessionOpenFailed(KakaoException exception) {
        Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
    }

    // 사용자 정보 요청
    public void requestMe() {
        UserManagement.getInstance()
                .me(new MeV2ResponseCallback() {
                    @Override
                    public void onSessionClosed(ErrorResult errorResult) {
                        Log.e("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
                    }

                    @Override
                    public void onFailure(ErrorResult errorResult) {
                        Log.e("KAKAO_API", "사용자 정보 요청 실패: " + errorResult);
                    }

                    @Override
                    public void onSuccess(MeV2Response result) {
                        Log.i("KAKAO_API", "사용자 아이디: " + result.getId()); // 이건 고유 아이디인거 같음 아마 이걸 db에 저장하고 비밀번호처럼 써야할듯
                        UserAccount kakaoAccount = result.getKakaoAccount();
                        if (kakaoAccount != null) {

                            // 이메일
                            String email = kakaoAccount.getEmail(); // 무조건 동의하게 해놔서 아마 바로 가져올듯
                            //이거 db로 넣고

                            if (email != null) {
                                Log.i("KAKAO_API", "email: " + email);

                            } else if (kakaoAccount.emailNeedsAgreement() == OptionalBoolean.TRUE) {
                                // 동의 요청 후 이메일 획득 가능
                                // 단, 선택 동의로 설정되어 있다면 서비스 이용 시나리오 상에서 반드시 필요한 경우에만 요청해야 합니다.
                            } else {
                                // 이메일 획득 불가
                            }
                            // 프로필
                            Profile profile = kakaoAccount.getProfile();

                            if (profile != null) {
                                Log.d("KAKAO_API", "nickname: " + profile.getNickname()); // 닉네임도 넣고
                                Log.d("KAKAOhttps://mongnyang.shop_API", "profile image: " + profile.getProfileImageUrl());
                                Log.d("KAKAO_API", "thumbnail image: " + profile.getThumbnailImageUrl());
                                    //여기까지 정상적으로 가져옴
                            } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                                // 동의 요청 후 프로필 정보 획득 가능

                            } else {
                                // 프로필 획득 불가
                            }

                            Gender gender = kakaoAccount.getGender();
                            if (gender != null) {
                                Log.d("KAKAO_API_Gender", "성별: " + gender.getValue()); // 닉네임도 넣고
                                //여기까지 정상적으로 가져옴
                            } else if (kakaoAccount.genderNeedsAgreement() == OptionalBoolean.TRUE) { // 성별 선택동의
                                // 동의 요청 후 프로필 정보 획득 가능
                                Log.d("KAKAO_API_Gender", "성별: " + gender.getValue()); // 닉네임도 넣고
                            } else {
                                // 프로필 획득 불가
                            }
                        }

                        //여기까지하면 닉네임 , 프로필사진, 이메일은 필수로 가져오고 선택으로 성별이 들어감
                    }
                });
    }

    @Override
    public void showResult(String result) {

    }

    @Override
    public void goMainView() {

    }
}