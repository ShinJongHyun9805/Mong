package com.puppyland.mongnang.retrofitService;

import com.puppyland.mongnang.DTO.BoardDTO;
import com.puppyland.mongnang.DTO.ChatUserDTO;
import com.puppyland.mongnang.DTO.CommentDTO;
import com.puppyland.mongnang.DTO.DeviceIdDTO;
import com.puppyland.mongnang.DTO.DiaryDTO;
import com.puppyland.mongnang.DTO.DogDTO;
import com.puppyland.mongnang.DTO.EncryptionDTO;
import com.puppyland.mongnang.DTO.FollowDTO;
import com.puppyland.mongnang.DTO.HospitalDTO;
import com.puppyland.mongnang.DTO.HospitalReviewDTO;
import com.puppyland.mongnang.DTO.MemberDTO;
import com.puppyland.mongnang.DTO.NotiboardDTO;
import com.puppyland.mongnang.DTO.NotificationDTO;
import com.puppyland.mongnang.DTO.PickDTO;
import com.puppyland.mongnang.DTO.SearchMemberDTO;
import com.puppyland.mongnang.DTO.StoryReplyDTO;
import com.puppyland.mongnang.DTO.VideoDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RetrofitService {
    @FormUrlEncoded
    @POST("memberJoin")
    Call<ResponseBody> signUpRequest(@Field("objJson") String objJson);

    @FormUrlEncoded
    @POST("memberLogin")
    Call<ResponseBody> signInRequest(@Field("objJson2") String objJson2);

    @FormUrlEncoded
    @POST("searchMember")
    Call<ArrayList<SearchMemberDTO>> searchRequest(@Field("objJson3") String objJson3);


    @FormUrlEncoded
    @POST("insertImage")
    Call<ResponseBody> insertImage(@Field("objJson5") String objJson5);

    @FormUrlEncoded
    @POST("fileName")
    Call<MemberDTO> fileName1(@Field("objJson99") String objJson99);

    @FormUrlEncoded
    @POST("getMyInfo")
    Call<MemberDTO> getMyInfo(@Field("objJson62") String objJson62);

    @FormUrlEncoded
    @POST("setCertificate")
    Call<ResponseBody> certificateEmail(@Field("objJson7") String objJson7);

    @FormUrlEncoded
    @POST("certificateCheck")
    Call<ResponseBody> certificateEmailCheck(@Field("objJson8") String objJson8);

    //내 정보 업데이트.
    @FormUrlEncoded
    @POST("updateInfo")
    Call<ResponseBody> updateInfo(@Field("objJson9") String objJson9);

    //유저 정보 가져오기
    @FormUrlEncoded
    @POST("getuserInfo")
    Call<MemberDTO> getuserInfo(@Field("objJson13") String objJson13);

    //Dog DB userId값 입력
    @FormUrlEncoded
    @POST("insertDogID")
    Call<ResponseBody> insertDogID(@Field("objJson10") String objJson10);

    //강아지 정보 업데이트
    @FormUrlEncoded
    @POST("dogUpdate")
    Call<ResponseBody> dogUpdate(@Field("objJson11") String objJson11);

    //유저 정보 업데이트
    @FormUrlEncoded
    @POST("userInfoUpdate")
    Call<ResponseBody> userInfoUpdate(@Field("objJson12") String objJson12);

    //강아지 사진 파일이름
    @FormUrlEncoded
    @POST("dogfileName")
    Call<List<DogDTO>> dogfileName(@Field("objJson14") String objJson14);

    @FormUrlEncoded
    @POST("getClickMemberinfo")
    Call<MemberDTO> getClickMemberinfo(@Field("objJson15") String objJson15);

    @FormUrlEncoded
    @POST("getClickMemberDoginfo")
    Call<List<DogDTO>> getClickMemberDoginfo(@Field("objJson16") String objJson16);

    @FormUrlEncoded
    @POST("chatUserInsert")
    Call<ResponseBody> chatUserInsert(@Field("objJson17") String objJson17);

    @FormUrlEncoded
    @POST("chatUserCheck")
    Call<List<ChatUserDTO>> chatUserListCheck(@Field("objJson18") String objJson18);

    @FormUrlEncoded
    @POST("chatUserDelete")
    Call<ResponseBody> chatUserDelete(@Field("objJson19") String objJson19);

    //현재 위치정보 입력
    @FormUrlEncoded
    @POST("LocationAddress")
    Call<ResponseBody> LocationAddress(@Field("objJson20") String objJson20);

    //
    @FormUrlEncoded
    @POST("TimeCheckInsert")
    Call<ResponseBody> TimeCheckInsert(@Field("objJson21") String objJson21);

    @FormUrlEncoded
    @POST("getTimeCheck")
    Call<List<String>> getTimeCheck(@Field("objJson22") String objJson22);


    @FormUrlEncoded
    @POST("TimeCheckUpdate")
    Call<ResponseBody> TimeCheckUpdate(@Field("objJson23") String objJson23);


    @FormUrlEncoded
    @POST("TimeCheckDelete")
    Call<ResponseBody> TimeCheckDelete(@Field("objJson24") String objJson24);

    @FormUrlEncoded
    @POST("getOneTimeCheck")
    Call<ResponseBody> getOneTimeCheck(@Field("objJson25") String objJson25);

    @FormUrlEncoded
    @POST("chatUserListChecknoAccept")
    Call<List<ChatUserDTO>> chatUserListChecknoAccept(@Field("objJson26") String objJson26);

    @FormUrlEncoded
    @POST("chatUserListCheckAcceptUpdate")
    Call<ChatUserDTO> chatUserListCheckAcceptUpdate(@Field("objJson27") String objJson27);

    //게시판 정보 가져오기
    @FormUrlEncoded
    @POST("boardInfo")
    Call<List<BoardDTO>> boardInfo(@Field("objJson28") String objJson28);

    //게시판 상세보기 정보
    @FormUrlEncoded
    @POST("boarddetailInfo")
    Call<BoardDTO> boarddetailInfo(@Field("objJson29") String objJson29);

    //게시글 검색
    @FormUrlEncoded
    @POST("Searchboard")
    Call<List<BoardDTO>> Searchboard(@Field("objJson30") String objJson30);

    //게시글 삭제
    @FormUrlEncoded
    @POST("DeleteBoard")
    Call<ResponseBody> DeleteBoard(@Field("objJson31") String objJson31);

    //게시글 업데이트
    @FormUrlEncoded
    @POST("UpdateBoard")
    Call<ResponseBody> UpdateBoard(@Field("objJson32") String objJson32);

    //게시글 작성
    @FormUrlEncoded
    @POST("boardWrite")
    Call<ResponseBody> boardWrite(@Field("objJson33") String objJson33);



    //다이어리 정보 가져오기
    @FormUrlEncoded
    @POST("diaryInfo")
    Call<List<DiaryDTO>> diaryInfo(@Field("objJson34") String objJson34);

    //다이어리 상세보기 정보
    @FormUrlEncoded
    @POST("diarydetailInfo")
    Call<DiaryDTO> diarydetailInfo(@Field("objJson35") String objJson35);

    //다이어리 검색
    @FormUrlEncoded
    @POST("Searchdiary")
    Call<List<DiaryDTO>> Searchdiary(@Field("objJson36") String objJson36);

    //다이어리 삭제
    @FormUrlEncoded
    @POST("Deletediary")
    Call<ResponseBody> Deletediary(@Field("objJson37") String objJson37);

    //다이어리 업데이트
    @FormUrlEncoded
    @POST("Updatediary")
    Call<ResponseBody> Updatediary(@Field("objJson38") String objJson38);

    //다이어리 작성
    @FormUrlEncoded
    @POST("Writediary")
    Call<ResponseBody> Writediary(@Field("objJson39") String objJson39);

    //다이어리 리스트 가져오기
    @FormUrlEncoded
    @POST("shareSelectList")
    Call<List<DiaryDTO>> shareInsertdiary(@Field("objJson40") String objJson40);

    //다이어리 공유 변수 업데이트
    @FormUrlEncoded
    @POST("shareUpdateStory")
    Call<ResponseBody> shareUpdateStory(@Field("objJson41") String objJson41);

    //다이어리 공유 변수 업데이트 off
    @FormUrlEncoded
    @POST("shareUpdateStoryoff")
    Call<ResponseBody> shareUpdateStoryoff(@Field("objJson42") String objJson42);

    //스토리 댓글 리스트 가져오기
    @FormUrlEncoded
    @POST("getstoryReplyList")
    Call<List<StoryReplyDTO>> getstoryReplyList(@Field("objJson43") String objJson43);


    //스토리 댓글 달기
    @FormUrlEncoded
    @POST("storyReplyinsert")
    Call<ResponseBody> storyReplyinsert(@Field("objJson44") String objJson44);


    // 댓글 쓰기
    @FormUrlEncoded
    @POST("commentInsert")
    Call<ResponseBody> commentInsert(@Field("objJson45") String objJson45);

    // 댓글 리스트
    @FormUrlEncoded
    @POST("getComment")
    Call<List<CommentDTO>> getComment(@Field("objJson46") String objJson46);


    //다이어리 정보 가져오기
    @FormUrlEncoded
    @POST("selectedUserdiaryInfo")
    Call<List<DiaryDTO>> selectedUserdiaryInfo(@Field("objJson47") String objJson47);

    //디바이스 아이디 insert
    @FormUrlEncoded
    @POST("insertDeviceId")
    Call<ResponseBody> insertDeviceId(@Field("objJson48") String objJson48);

    //디바이스 아이디 insert
    @FormUrlEncoded
    @POST("getDeviceId")
    Call<DeviceIdDTO> getDeviceId(@Field("objJson49") String objJson49);

    @FormUrlEncoded
    @POST("PushAlarm/{flag}/{no}")
    Call<ResponseBody> PushAlarm(@Path("flag") int flag, @Field("objJson50") String objJson50 , @Path("no") String no);

    @FormUrlEncoded
    @POST("functionCountUpdateChangePdf")
    Call<ResponseBody> functionCount_update_changepdf(@Field("objJson51") String objJson51);

    // 댓글갯수 업데이트
    @FormUrlEncoded
    @POST("updateComment")
    Call<ResponseBody> updateComment(@Field("objJson52") String objJson52);

    //등급표 유저 입력
    @FormUrlEncoded
    @POST("functionCount_tbl_functionCount_insert_userId")
    Call<ResponseBody> functionCount_tbl_functionCount_insert_userId(@Field("objJson53") String objJson53);

    //등급표 유저 입력
    @FormUrlEncoded
    @POST("functionCount_tbl_membership_insert_userId")
    Call<ResponseBody> functionCount_tbl_membership_insert_userId(@Field("objJson54") String objJson54);


    //등급표 유저 업데이트
    @FormUrlEncoded
    @POST("functionCountUpdateRating")
    Call<ResponseBody> functionCount_update_rating(@Field("objJson55") String objJson55);


    //회원 등급 가져오기
    @FormUrlEncoded
    @POST("functionCount_select_rating")
    Call<ResponseBody> functionCount_select_rating(@Field("objJson56") String objJson56);

    //jump
    @FormUrlEncoded
    @POST("functionCount_update_jump")
    Call<ResponseBody> functionCount_update_jump(@Field("objJson57") String objJson57);

    //jump 남은 갯수
    @FormUrlEncoded
    @POST("getCountJump")
    Call<ResponseBody> getCountJump(@Field("objJson58") String objJson58);

    @FormUrlEncoded
    @POST("Checkingoverlap")
    Call<ResponseBody> Checkingoverlap(@Field("objJson59") String objJson59);

    @FormUrlEncoded
    @POST("getMynickname")
    Call<MemberDTO> getMynickname(@Field("objJson60") String objJson60);

    @FormUrlEncoded
    @POST("insertfont")
    Call<ResponseBody> insert_font(@Field("objJson61") String objJson61);

    @FormUrlEncoded
    @POST("getfontList")
    Call<List<String>> getfontList(@Field("objJson62") String objJson62);

    //비디오 정보
    @FormUrlEncoded
    @POST("InsertVideo")
    Call<ResponseBody> InsertVideo(@Field("objJson63") String objJson63);

    //비디오 랜덤 값 가져오기
    @FormUrlEncoded
    @POST("getRandVideo")
    Call<VideoDTO> getRandVideo(@Field("objJson64") String objJson64);

    //비디오 랜덤 값 가져오기
    @FormUrlEncoded
    @POST("getVideolist")
    Call<List<VideoDTO>> getVideolist(@Field("objJson65") String objJson65);

    //팔로잉 리스트
    @FormUrlEncoded
    @POST("FollowingList")
    Call<List<VideoDTO>> FollowingList(@Field("objJson66") String objJson66);

    //팔로워 리스트
    @FormUrlEncoded
    @POST("FollwerList")
    Call<List<FollowDTO>> FollwerList(@Field("objJson67") String objJson67);

    //팔로우 체크
    @FormUrlEncoded
    @POST("CheckFollow")
    Call<ResponseBody> CheckFollow(@Field("objJson68") String objJson68);

    //조아여 체크
    @FormUrlEncoded
    @POST("CheckLike")
    Call<ResponseBody> CheckLike(@Field("objJson69") String objJson69);

    //라이크 업데이트
    @FormUrlEncoded
    @POST("UpdateLike")
    Call<ResponseBody> UpdateLike(@Field("objJson70") String objJson70);

    //동영상 팔로우
    @FormUrlEncoded
    @POST("FollowVideo")
    Call<ResponseBody> FollowVideo(@Field("objJson71") String objJson71);

    //내가 누른 조아여
    @FormUrlEncoded
    @POST("InsertLike")
    Call<ResponseBody> InsertLike(@Field("objJson72") String objJson72);

    //안 조아여
    @FormUrlEncoded
    @POST("UnLike")
    Call<ResponseBody> UnLike(@Field("objJson73") String objJson73);

    //언팔로우
    @FormUrlEncoded
    @POST("UnFollow")
    Call<ResponseBody> UnFollow(@Field("objJson74") String objJson74);

    //조회수 증가
    @FormUrlEncoded
    @POST("updateHit")
    Call<ResponseBody> updateHit(@Field("objJson75") String objJson75);

    //조회수 증가
    @FormUrlEncoded
    @POST("diaryCheckLike")
    Call<ResponseBody> diaryCheckLike(@Field("objJson76") String objJson76);

    //내가 누른 조아여
    @FormUrlEncoded
    @POST("diaryInsertLike")
    Call<ResponseBody> diaryInsertLike(@Field("objJson77") String objJson77);

    //안 조아여
    @FormUrlEncoded
    @POST("diaryUnLike")
    Call<ResponseBody> diaryUnLike(@Field("objJson78") String objJson78);

    //라이크 업데이트
    @FormUrlEncoded
    @POST("diaryUpdateLike")
    Call<ResponseBody> diaryUpdateLike(@Field("objJson79") String objJson79);

    @FormUrlEncoded
    @POST("diarydesUpdateLike")
    Call<ResponseBody> diarydesUpdateLike(@Field("objJson80") String objJson80);


    //디바이스 아이디 삭제
    @FormUrlEncoded
    @POST("DeviceIdDelete")
    Call<ResponseBody> DeviceIdDelete(@Field("objJson81") String objJson81);

    //강아지 테이블 정보 삭제
    @FormUrlEncoded
    @POST("DeleteDog")
    Call<ResponseBody> DeleteDog(@Field("objJson82") String objJson82);

    //멤버쉽 정보 삭제
    @FormUrlEncoded
    @POST("DeleteMembership")
    Call<ResponseBody> DeleteMembership(@Field("objJson83") String objJson83);

    //아이템 횟수 테이블 정보 삭제
    @FormUrlEncoded
    @POST("DeleteFunctioncount")
    Call<ResponseBody> DeleteFunctioncount(@Field("objJson84") String objJson84);

    //채팅 리스트 유저 정보 삭제
    @FormUrlEncoded
    @POST("DeleteChatlist")
    Call<ResponseBody> DeleteChatlist(@Field("objJson85") String objJson85);

    //좋아요 테이블 유저 정보 삭제
    @FormUrlEncoded
    @POST("DeleteLike")
    Call<ResponseBody> DeleteLike(@Field("objJson86") String objJson86);

    //팔로우 테이블 유저 정보 삭제
    @FormUrlEncoded
    @POST("DeleteFollow")
    Call<ResponseBody> DeleteFollow(@Field("objJson87") String objJson87);

    //비디오 테이블 유저 정보 삭제
    @FormUrlEncoded
    @POST("DeleteVideo")
    Call<ResponseBody> DeleteVideo(@Field("objJson88") String objJson88);

    //게시판 테이블 유저 정보 삭제
    @FormUrlEncoded
    @POST("DeleteBoardUser")
    Call<ResponseBody> DeleteBoardUser(@Field("objJson89") String objJson89);

    //댓글 테이블 유저 정보 삭제
    @FormUrlEncoded
    @POST("DeleteComment")
    Call<ResponseBody> DeleteComment(@Field("objJson90") String objJson90);

    //스토리 테이블 유저 정보 삭제
    @FormUrlEncoded
    @POST("DeleteDiaryreview")
    Call<ResponseBody> DeleteDiaryreview(@Field("objJson91") String objJson91);

    //회원 탈퇴
    @FormUrlEncoded
    @POST("goodbye")
    Call<ResponseBody> goodbye(@Field("objJson92") String objJson92);

    //좋아요 차감
    @FormUrlEncoded
    @POST("subtractLike")
    Call<ResponseBody> subtractLike(@Field("objJson93") String objJson93);

    //다이어리 유저 글 삭제
    @FormUrlEncoded
    @POST("DeleteMyDiary")
    Call<ResponseBody> DeleteMyDiary(@Field("objJson94") String objJson94);

    //로그인할때 deviceid가 달라졌으면 업데이트
    @FormUrlEncoded
    @POST("UpdateDeviceId")
    Call<ResponseBody> UpdateDeviceId(@Field("objJson95") String objJson95);

    //로그인할때 deviceid가 달라졌으면 업데이트
    @FormUrlEncoded
    @POST("myboardlistInfo")
    Call<List<BoardDTO>> myboardlistInfo(@Field("objJson96") String objJson96);

    //게시판 조회 수 높은 순
    @FormUrlEncoded
    @POST("Highview")
    Call<List<BoardDTO>> Highview(@Field("objJson97") String objJson97);

    //포인트 증가
    @FormUrlEncoded
    @POST("functionCount_update_point/{flag}")
    Call<ResponseBody> functionCount_update_point(@Path("flag") int flag, @Field("objJson98") String objJson98);

    //현재 내 포인트 가져오는 값
    @FormUrlEncoded
    @POST("getMyPoint")
    Call<ResponseBody> getMyPoint(@Field("objJson99") String objJson99);

    //동물병원 정보
    @FormUrlEncoded
    @POST("getHospitalInfo")
    Call<List<HospitalDTO>> getHospitalInfo(@Field("objJson100") String objJson100);

    //동물병원 후기 등록
    @FormUrlEncoded
    @POST("WriteReview")
    Call<ResponseBody> WriteReview(@Field("objJson101") String objJson101);

    //동물병원 후기
    @FormUrlEncoded
    @POST("getHosReviews")
    Call<List<HospitalReviewDTO>> getHosReviews(@Field("objJson102") String objJson102);

    //동물병원 후기 갯수
    @FormUrlEncoded
    @POST("ReviewCount")
    Call<ResponseBody> ReviewCount(@Field("objJson103") String objJson103);

    //팔로워 갯수 나를 따르는 사람들
    @FormUrlEncoded
    @POST("FollwerListCount")
    Call<ResponseBody> FollwerListCount(@Field("objJson104") String objJson103);
    //팔로우 갯수 // 내가 따르는 사람들
    @FormUrlEncoded
    @POST("FollowingListCount")
    Call<ResponseBody> FollowingListCount(@Field("objJson105") String objJson103);

    //채팅요청확인
    @FormUrlEncoded
    @POST("CheckConnectChat")
    Call<ResponseBody> CheckConnectChat(@Field("objJson106") String objJson106);

    //채팅허용유저
    @FormUrlEncoded
    @POST("AcceptUser")
    Call<List<ChatUserDTO>> AcceptUser(@Field("objJson107") String objJson107);

    //알람업데이트
    @FormUrlEncoded
    @POST("alarmUpdate")
    Call<ResponseBody> alarmUpdate(@Field("objJson108") String objJson108);

    //알람 초기정보 가져오기
    @FormUrlEncoded
    @POST("getAlarmInfo")
    Call<MemberDTO> getAlarmInfo(@Field("objJson109") String objJson109);

    //신고기능
    @FormUrlEncoded
    @POST("Warning")
    Call<ResponseBody> Warning(@Field("objJson110") String objJson110);

    //신고 확인
    @FormUrlEncoded
    @POST("CheckWarning")
    Call<ResponseBody> CheckWarning(@Field("objJson111") String objJson111);

    //동영상 신고 확인
    @FormUrlEncoded
    @POST("VideoCheckWarning")
    Call<ResponseBody> VideoCheckWarning(@Field("objJson112") String objJson112);

    //점프권 사용하기 전에 같은 지역 사람인지 아닌지 체크
    @FormUrlEncoded
    @POST("CheckSamePlace")
    Call<ResponseBody> CheckSamePlace(@Field("objJson113") String objJson113);

    //스토리 신고 초기 체크
    @FormUrlEncoded
    @POST("StoryCheckWarning")
    Call<ResponseBody> StoryCheckWarning(@Field("objJson114") String objJson114);

    //PDF 변환권 구매
    @FormUrlEncoded
    @POST("functionCount_purchase_pdf/{flag}")
    Call<ResponseBody> functionCount_purchase_pdf(@Path("flag") int flag, @Field("objJson115") String objJson115);

    //jump chat 구매
    @FormUrlEncoded
    @POST("functionCount_purchase_chat/{flag}")
    Call<ResponseBody> functionCount_purchase_chat(@Path("flag") int flag, @Field("objJson116") String objJson116);

    //채팅유저 delete
    @FormUrlEncoded
    @POST("chatUserListDelete")
    Call<ResponseBody> chatUserListDelete(@Field("objJson117") String objJson117);

    //차단당하면 당한사람의 채팅 db를 지워버림 delete
    @FormUrlEncoded
    @POST("BlockDeleteChat")
    Call<ResponseBody> BlockDeleteChat(@Field("objJson118") String objJson118);

    @FormUrlEncoded
    @POST("UpdateNotification")
    Call<ResponseBody> UpdateNotification(@Field("objJson119") String objJson119);

    @FormUrlEncoded
    @POST("getListNotification")
    Call<List<NotificationDTO>> getListNotification(@Field("objJson120") String objJson120);

    //회원가입시 가입된 계정인지 체크
    @FormUrlEncoded
    @POST("CheckOverlapID")
    Call<ResponseBody> CheckOverlapID(@Field("objJson121") String objJson121);

    //회원가입시 가입된 계정인지 체크
    @FormUrlEncoded
    @POST("getnotiboardlist")
    Call<List<NotiboardDTO>> getnotiboardlist(@Field("objJson122") String objJson122);
    //구입한 폰트인지
    @FormUrlEncoded
    @POST("checkpurchasedfont")
    Call<ResponseBody> checkpurchasedfont(@Field("objJson123") String objJson123);

    @FormUrlEncoded
    @POST("UpdateAddress")
    Call<ResponseBody> UpdateAddress(@Field("objJson124") String objJson124);

    // 채팅에서 보여지는 내 사진
    @FormUrlEncoded
    @POST("ChatgetMyImg")
    Call<MemberDTO> ChatgetMyImg(@Field("objJson125") String objJson125);

    //남은 PDF권 체크
    @FormUrlEncoded
    @POST("CheckCountPDF")
    Call<ResponseBody> CheckCountPDF(@Field("objJson126") String objJson126);

    @FormUrlEncoded
    @POST("eEnecrbypnttiwqon")
    Call<EncryptionDTO> Encryption(@Field("objJson127") String objJson127);

    //댓글 갯수가져오기
    @FormUrlEncoded
    @POST("getCountComment")
    Call<BoardDTO> getCountComment(@Field("objJson128") String objJson128);

    //기부하기
    @FormUrlEncoded
    @POST("giveHeart")
    Call<ResponseBody> giveHeart(@Field("objJson129") String objJson129);

    //기부한거 갯수
    @FormUrlEncoded
    @POST("getCountGive")
    Call<ResponseBody>  getCountGive(@Field("objJson130") String objJson130);

    @FormUrlEncoded
    @POST("getPickList")
    Call<List<PickDTO>>  getPickList(@Field("objJson131") String objJson131);

    @FormUrlEncoded
    @POST("setStarPoint")
    Call<Map<String , Object>>  setStarPoint(@Field("objJson132") String objJson132);

    //이 글을 평가했는지 안했는지 체크
    @FormUrlEncoded
    @POST("checkpick")
    Call<Integer>  checkpick(@Field("objJson133") String objJson133);

    //별점 업데이트 시 디비에 등록
    @FormUrlEncoded
    @POST("insertStar")
    Call<ResponseBody>  insertStar(@Field("objJson134") String objJson134);
}
