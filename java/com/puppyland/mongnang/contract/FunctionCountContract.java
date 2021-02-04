package com.puppyland.mongnang.contract;


import com.puppyland.mongnang.DTO.FunctionCountDTO;

public interface FunctionCountContract {

    interface View{

    }

    interface Presenter{
        void functionCount_insert_userId (String userId);
        void functionCount_tbl_membership_insert_userId(String userId);
        void functionCount_update_Ad(String userId,boolean flag);

        void functionCount_update_changepdf(String userId,int count);
        void functionCount_update_jump(String userId,int count);

        void functionCount_update_rating(String userId,String rating);
        void functionCount_update_rating_init(); // 아마 안씀

        void functionCount_update_point(FunctionCountDTO dto, int flag); //0이면 동영상 광고보고 얻은 포인트 ,1은 small구입,2는 middle구입 , 3은 big구입

        void functionCount_purchase_pdf(FunctionCountDTO dto, int flag);

        void functionCount_purchase_chat(FunctionCountDTO dto, int flag);

    }

}