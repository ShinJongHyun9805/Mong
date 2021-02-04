package com.puppyland.mongnang.model;

import com.puppyland.mongnang.DTO.MemberDTO;

import java.util.List;

public interface ApiCallback {

    void onSuccess(List<MemberDTO> result);
}
