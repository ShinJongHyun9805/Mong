package com.puppyland.mongnang.model;


import android.util.Log;

import com.puppyland.mongnang.DTO.AlarmDTO;
import com.puppyland.mongnang.DTO.BoardDTO;
import com.puppyland.mongnang.DTO.ChatUserDTO;
import com.puppyland.mongnang.DTO.CommentDTO;
import com.puppyland.mongnang.DTO.DeviceIdDTO;
import com.puppyland.mongnang.DTO.DiaryDTO;
import com.puppyland.mongnang.DTO.DogDTO;
import com.puppyland.mongnang.DTO.FollowDTO;
import com.puppyland.mongnang.DTO.FunctionCountDTO;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.DTO.StoryReplyDTO;
import com.puppyland.mongnang.DTO.VideoDTO;
import com.puppyland.mongnang.contract.Apicallback;
import com.puppyland.mongnang.contract.MemberContract;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.puppyland.mongnang.retrofitService.RetrofitService;
import com.google.gson.Gson;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MemberModel {

    MemberContract.Presenter presenter;

    public static Boolean check = false;

    public MemberModel(MemberContract.Presenter presenter) {
        this.presenter = presenter;
    }

    /********************************************************************************************
     * 유저 정보 등록
     * *******************************************************************************************/
    public void saveData(String id, String gender, String age, String address1, String address2, String address3, String memberimage, String certification, String nickname, String deviceId, Apicallback apicallback) {
        MemberDTO dto = new MemberDTO();
        dto.setUserId(id);
        dto.setGender(gender);
        dto.setAddress1(address1);
        dto.setAddress2(address2);
        dto.setAddress3(address3);
        dto.setMemberimage(memberimage);
        dto.setAge(age);
        dto.setCertification("1");
        dto.setNickname(nickname);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        final Call<ResponseBody> signup = NetRetrofit.getInstance().getService().signUpRequest(objJson);
        signup.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    /********************************************************************************************
                     * 강아지 정보 등록
                     * *******************************************************************************************/
                    MemberDTO dto = new MemberDTO();
                    dto.setUserId(id);

                    Gson gson = new Gson();
                    String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

                    final Call<ResponseBody> dogJoin = NetRetrofit.getInstance().getService().insertDogID(objJson);
                    dogJoin.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                /********************************************************************************************
                                 * 디바이스 아이디 입력
                                 * *******************************************************************************************/
                                DeviceIdDTO dto = new DeviceIdDTO();
                                dto.setId(id);
                                dto.setDeviceId(deviceId);

                                Gson gson = new Gson();
                                String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

                                Call<ResponseBody> insertDeviceId = NetRetrofit.getInstance().getService().insertDeviceId(objJson);
                                insertDeviceId.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {
                                            /********************************************************************************************
                                             * 상점 관련 아이템 아이디 입력
                                             * *******************************************************************************************/
                                            FunctionCountDTO dto = new FunctionCountDTO();
                                            dto.setUserid(id);

                                            Gson gson = new Gson();
                                            String objJson = gson.toJson(dto);

                                            Call<ResponseBody> functionCount_tbl_functionCount_insert_userId = NetRetrofit.getInstance().getService().functionCount_tbl_functionCount_insert_userId(objJson);
                                            functionCount_tbl_functionCount_insert_userId.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if (response.isSuccessful()) {
                                                        apicallback.onSuccess();
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Log.d("상점 관련 아이템 아이디 입력 실패", t.getMessage());
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Log.d("디바이스 아이디 입력 실패", t.getMessage());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("강아지 정보 등록 실패", t.getMessage());
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("회원가입 실패", t.getMessage());
            }
        });
    }

    //Dog Db, userID값
    public void dogJoin(String id) {
        Log.d("MM", id);
        MemberDTO dto = new MemberDTO();
        dto.setUserId(id);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        final Call<ResponseBody> dogJoin = NetRetrofit.getInstance().getService().insertDogID(objJson);
        dogJoin.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("강아지 정보 등록 실패", t.getMessage());
            }
        });
    }

    //강아지 정보 업데이트
    public void dogUpdate(String userID, String id, String kind, String age, String gender, String imageName) {
        DogDTO dto = new DogDTO();
        dto.setUserId(userID);
        dto.setDogName(id);
        dto.setDogKind(kind);
        dto.setDogAge(age);
        dto.setDogGender(gender);
        dto.setDogImage(imageName);

        Log.d("MM", dto.getUserId());
        Log.d("MM", dto.getDogName());
        Log.d("MM", dto.getDogKind());
        Log.d("MM", dto.getDogAge());
        Log.d("MM", dto.getDogGender());


        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        Call<ResponseBody> dogupdate = NetRetrofit.getInstance().getService().dogUpdate(objJson);
        dogupdate.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("res", "성공");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });


    }

    //유저의 디바이스 아이디 insert
    public void DeviceIdInsert(String id, String deviceId) {
        DeviceIdDTO dto = new DeviceIdDTO();
        dto.setId(id);
        dto.setDeviceId(deviceId);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

        Call<ResponseBody> insertDeviceId = NetRetrofit.getInstance().getService().insertDeviceId(objJson);
        insertDeviceId.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("res", "성공");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });
    }

    public void alarmUpdate(String userid, int whatAlarm, int onoff) {
        AlarmDTO alarmDTO = new AlarmDTO();
        alarmDTO.setUserid(userid);
        alarmDTO.setWhatAlarm(whatAlarm);
        alarmDTO.setOnoff(onoff);

        Gson gson = new Gson();
        String objJson = gson.toJson(alarmDTO); // DTO 객체를 json 으로 변환

        Call<ResponseBody> alarmUpdate = NetRetrofit.getInstance().getService().alarmUpdate(objJson);
        alarmUpdate.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("res", "성공");

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("fail", t.getMessage());
            }
        });


    }

    //디바이스 아이디 삭제 -> 강아지 테이블 정보 삭제 -> 멤버쉽 삭제 ->아이템 횟수 삭제-> 채팅리스트 유저 정보 삭제 -> 회원 탈퇴
    public boolean DeviceIdDelete(String userID) {
        check = false;
        DeviceIdDTO dto = new DeviceIdDTO();
        dto.setId(userID);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        /**
         * 디바이스 아이디 삭제
         * */
        Call<ResponseBody> DeviceIdDelete = NetRetrofit.getInstance().getService().DeviceIdDelete(objJson);
        DeviceIdDelete.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    /**
                     * 강아지 테이블 정보 삭제
                     * */
                    MemberDTO dto = new MemberDTO();
                    dto.setUserId(userID);
                    Gson gson = new Gson();
                    String objJson = gson.toJson(dto);

                    Call<ResponseBody> DeleteDog = NetRetrofit.getInstance().getService().DeleteDog(objJson);
                    DeleteDog.enqueue(new Callback<ResponseBody>() {
                        @Override
                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                            if (response.isSuccessful()) {
                                /**
                                 * 멤버쉽 정보 삭제
                                 * */
                                FunctionCountDTO dto = new FunctionCountDTO();
                                dto.setUserid(userID);

                                Gson gson = new Gson();
                                String objJson = gson.toJson(dto);

                                Call<ResponseBody> DeleteMembership = NetRetrofit.getInstance().getService().DeleteMembership(objJson);
                                DeleteMembership.enqueue(new Callback<ResponseBody>() {
                                    @Override
                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                        if (response.isSuccessful()) {
                                            /**
                                             * 아이템 횟수 테이블 정보 삭제
                                             * */
                                            FunctionCountDTO dto = new FunctionCountDTO();
                                            dto.setUserid(userID);

                                            Gson gson = new Gson();
                                            String objJson = gson.toJson(dto);

                                            Call<ResponseBody> DeleteFunctioncount = NetRetrofit.getInstance().getService().DeleteFunctioncount(objJson);
                                            DeleteFunctioncount.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if (response.isSuccessful()) {
                                                        /**
                                                         * 채팅 리스트 유저 정보 삭제
                                                         * */
                                                        ChatUserDTO dto = new ChatUserDTO();
                                                        dto.setUserId(userID);

                                                        Gson gson = new Gson();
                                                        String objJson = gson.toJson(dto);

                                                        Call<ResponseBody> DeleteChatlist = NetRetrofit.getInstance().getService().DeleteChatlist(objJson);
                                                        DeleteChatlist.enqueue(new Callback<ResponseBody>() {
                                                            @Override
                                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                                if (response.isSuccessful()) {
                                                                    /**
                                                                     * 좋아요 테이블 유저 정보 삭제
                                                                     * */
                                                                    VideoDTO dto = new VideoDTO();
                                                                    dto.setUserid(userID);

                                                                    Gson gson = new Gson();
                                                                    String objJson = gson.toJson(dto);

                                                                    Call<ResponseBody> DeleteLike = NetRetrofit.getInstance().getService().DeleteLike(objJson);
                                                                    DeleteLike.enqueue(new Callback<ResponseBody>() {
                                                                        @Override
                                                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                                            if (response.isSuccessful()) {
                                                                                /**
                                                                                 * 팔로우 테이블 유저 정보 삭제
                                                                                 * */
                                                                                FollowDTO dto = new FollowDTO();
                                                                                dto.setUserid(userID);

                                                                                Gson gson = new Gson();
                                                                                String objJson = gson.toJson(dto);

                                                                                Call<ResponseBody> DeleteFollow = NetRetrofit.getInstance().getService().DeleteFollow(objJson);
                                                                                DeleteFollow.enqueue(new Callback<ResponseBody>() {
                                                                                    @Override
                                                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                                                        if (response.isSuccessful()) {
                                                                                            /**
                                                                                             * 비디오 테이블 유저 정보 삭제
                                                                                             * */
                                                                                            VideoDTO dto = new VideoDTO();
                                                                                            dto.setUserid(userID);

                                                                                            Gson gson = new Gson();
                                                                                            String objJson = gson.toJson(dto);

                                                                                            Call<ResponseBody> DeleteVideo = NetRetrofit.getInstance().getService().DeleteVideo(objJson);
                                                                                            DeleteVideo.enqueue(new Callback<ResponseBody>() {
                                                                                                @Override
                                                                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                                                                    if (response.isSuccessful()) {
                                                                                                        /**
                                                                                                         * 게시판 테이블 유저 정보 삭제
                                                                                                         * */
                                                                                                        BoardDTO dto = new BoardDTO();
                                                                                                        dto.setUserid(userID);

                                                                                                        Gson gson = new Gson();
                                                                                                        String objJson = gson.toJson(dto);

                                                                                                        Call<ResponseBody> DeleteBoardUser = NetRetrofit.getInstance().getService().DeleteBoardUser(objJson);
                                                                                                        DeleteBoardUser.enqueue(new Callback<ResponseBody>() {
                                                                                                            @Override
                                                                                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                                                                                if (response.isSuccessful()) {
                                                                                                                    /**
                                                                                                                     * 댓글 테이블 유저 정보 삭제
                                                                                                                     * */
                                                                                                                    CommentDTO dto = new CommentDTO();
                                                                                                                    dto.setId(userID);

                                                                                                                    Gson gson = new Gson();
                                                                                                                    String objJson = gson.toJson(dto);

                                                                                                                    Call<ResponseBody> DeleteComment = NetRetrofit.getInstance().getService().DeleteComment(objJson);
                                                                                                                    DeleteComment.enqueue(new Callback<ResponseBody>() {
                                                                                                                        @Override
                                                                                                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                                                                                            if (response.isSuccessful()) {
                                                                                                                                /**
                                                                                                                                 * 스토리 댓글 유저 정보 삭제
                                                                                                                                 * */
                                                                                                                                StoryReplyDTO dto = new StoryReplyDTO();
                                                                                                                                dto.setUserid(userID);

                                                                                                                                Gson gson = new Gson();
                                                                                                                                String objJson = gson.toJson(dto);

                                                                                                                                Call<ResponseBody> DeleteDiaryreview = NetRetrofit.getInstance().getService().DeleteDiaryreview(objJson);
                                                                                                                                DeleteDiaryreview.enqueue(new Callback<ResponseBody>() {
                                                                                                                                    @Override
                                                                                                                                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                                                                                                        if (response.isSuccessful()) {
                                                                                                                                            /**
                                                                                                                                             * 다이어리 유저 글 삭제
                                                                                                                                             * */
                                                                                                                                            DiaryDTO dto = new DiaryDTO();
                                                                                                                                            dto.setUserid(userID);

                                                                                                                                            Gson gson = new Gson();
                                                                                                                                            String objJson = gson.toJson(dto);

                                                                                                                                            Call<ResponseBody> DeleteMyDiary = NetRetrofit.getInstance().getService().DeleteMyDiary(objJson);
                                                                                                                                            DeleteMyDiary.enqueue(new Callback<ResponseBody>() {
                                                                                                                                                @Override
                                                                                                                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                                                                                                                    /**
                                                                                                                                                     * 회원 탈퇴
                                                                                                                                                     * */
                                                                                                                                                    MemberDTO dto = new MemberDTO();
                                                                                                                                                    dto.setUserId(userID);

                                                                                                                                                    Gson gson = new Gson();
                                                                                                                                                    String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

                                                                                                                                                    Call<ResponseBody> goodbye = NetRetrofit.getInstance().getService().goodbye(objJson);
                                                                                                                                                    goodbye.enqueue(new Callback<ResponseBody>() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                                                                                                                            if (response.isSuccessful()) {
                                                                                                                                                                check = true; // 다 성공해서 젤 안으로 들어왔을때  true 로 바뀐다.

                                                                                                                                                            }
                                                                                                                                                        }

                                                                                                                                                        @Override
                                                                                                                                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                                                                                                                            Log.e("회원 탈퇴 실패", t.getMessage());
                                                                                                                                                        }
                                                                                                                                                    });
                                                                                                                                                }

                                                                                                                                                @Override
                                                                                                                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                                                                                                                    Log.e("다이어리 유저 글 삭제 실패", t.getMessage());
                                                                                                                                                }
                                                                                                                                            });

                                                                                                                                        }
                                                                                                                                    }

                                                                                                                                    @Override
                                                                                                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                                                                                                        Log.e("스토리 댓글 유저 정보 삭제", t.getMessage());
                                                                                                                                    }
                                                                                                                                });
                                                                                                                            }

                                                                                                                        }

                                                                                                                        @Override
                                                                                                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                                                                                            Log.e("댓글 테이블 유저 정보 삭제 실패", t.getMessage());
                                                                                                                        }
                                                                                                                    });
                                                                                                                }
                                                                                                            }

                                                                                                            @Override
                                                                                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                                                                                Log.e("게시판 테이블 유저 정보 삭제", t.getMessage());
                                                                                                            }
                                                                                                        });
                                                                                                    }
                                                                                                }

                                                                                                @Override
                                                                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                                                                    Log.e("비디오 테이블 유저 정보 삭제 실패", t.getMessage());
                                                                                                }
                                                                                            });

                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                                                        Log.e("팔로우 테이블 유저 정보 삭제 실패", t.getMessage());
                                                                                    }
                                                                                });
                                                                            }
                                                                        }

                                                                        @Override
                                                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                                            Log.e("좋아요 테이블 유저 정보 삭제 실패", t.getMessage());
                                                                        }
                                                                    });
                                                                }
                                                            }

                                                            @Override
                                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                                Log.e("채팅 리스트 유저 정보 삭제 실패", t.getMessage());
                                                            }
                                                        });
                                                    }
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Log.e("아이템 횟수 테이블 정보 삭제", t.getMessage());
                                                }
                                            });
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                                        Log.e("멤버쉽 정보 삭제 실패", t.getMessage());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                            Log.e("강아지테이블정보삭제실패", t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("디바이스아이디삭제실패", t.getMessage());
            }
        });
        return check; // check 를 반환함
    }
}
