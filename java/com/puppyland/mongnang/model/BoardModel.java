package com.puppyland.mongnang.model;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.puppyland.mongnang.DTO.BoardDTO;
import com.puppyland.mongnang.DTO.CommentDTO;
import com.puppyland.mongnang.board.LIstViewAdapter3;
import com.puppyland.mongnang.contract.BoardContract;
import com.puppyland.mongnang.retrofitService.NetRetrofit;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardModel implements BoardContract.presenter {

    BoardContract.presenter boardPresenter;

    private LIstViewAdapter3 adapter;
    private List<CommentDTO> list;

    public BoardModel(BoardContract.presenter presenter){
        this.boardPresenter = presenter;
    }


    @Override
    public void DeleteBoard(String bno) {
        BoardDTO dto = new BoardDTO();
        dto.setBno(bno);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> DeleteBoard = NetRetrofit.getInstance().getService().DeleteBoard(objJson);
        DeleteBoard.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("###", "통신성공123");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    @Override
    public void UpdateBoard(String bno, String title, String content, String imgName) {
        BoardDTO dto = new BoardDTO();
        dto.setBno(bno);
        dto.setTitle(title);
        dto.setBcontent(content);
        dto.setImg(imgName);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> UpdateBoard = NetRetrofit.getInstance().getService().UpdateBoard(objJson);
        UpdateBoard.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("###", "통신성공:)");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###", t.getMessage());
            }
        });
    }

    @Override
    public void InsertContent(String bno, String id, String content , String nickname) {
        CommentDTO dto = new CommentDTO();
        dto.setBno(bno);
        dto.setId(id);
        dto.setContent(content);
        dto.setNickname(nickname);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> commentInsert = NetRetrofit.getInstance().getService().commentInsert(objJson);
        commentInsert.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(response.isSuccessful()){
                    Log.d("###", "성공");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("###", t.getMessage());
            }
        });

    }

    @Override
    public void updateComment(String bno, TextView reviews, ListView listview) {
        BoardDTO dto = new BoardDTO();
        dto.setBno(bno);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> updateComment = NetRetrofit.getInstance().getService().updateComment(objJson);
        updateComment.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    BoardDTO dto = new BoardDTO();
                    dto.setBno(bno);

                    Gson gson = new Gson();
                    String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

                    Call<BoardDTO> boarddetailInfo = NetRetrofit.getInstance().getService().boarddetailInfo(objJson);
                    boarddetailInfo.enqueue(new Callback<BoardDTO>() {
                        @Override
                        public void onResponse(Call<BoardDTO> call, Response<BoardDTO> response) {
                            BoardDTO dto2 = response.body();
                            reviews.setText("댓글" + "(" + dto2.getCnt() + ")");

                            CommentDTO dto = new CommentDTO();
                            dto.setBno(bno);

                            Gson gson = new Gson();
                            String objJson = gson.toJson(dto); // DTO 객체를 json 으로 변환

                            list = new ArrayList<CommentDTO>();
                            Call<List<CommentDTO>> getComment = NetRetrofit.getInstance().getService().getComment(objJson);
                            getComment.enqueue(new Callback<List<CommentDTO>>() {
                                @Override
                                public void onResponse(Call<List<CommentDTO>> call, Response<List<CommentDTO>> response) {
                                    list = response.body();

                                    adapter = new LIstViewAdapter3();
                                    listview.setAdapter(adapter);
                                    try {
                                        if (list != null) {
                                            for (CommentDTO commentDTO : list) { // 리스트로 담기는// 것들 하나씩 출력
                                                adapter.addItem(commentDTO.getId(), commentDTO.getContent(), commentDTO.getNickname());
                                            }
                                            setListViewHeightBasedOnItems(listview);
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Call<List<CommentDTO>> call, Throwable t) {
                                    Log.e("###listerr", t.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<BoardDTO> call, Throwable t) {
                            Log.e("###reviews", t.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("###commenterr", t.getMessage());
            }
        });
    }

    @Override
    public void updateHit(String bno) {
        BoardDTO dto = new BoardDTO();
        dto.setBno(bno);

        Gson gson = new Gson();
        String objJson = gson.toJson(dto);

        Call<ResponseBody> updateHit = NetRetrofit.getInstance().getService().updateHit(objJson);
        updateHit.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("###updateHit", t.getMessage());
            }
        });

    }

    private boolean setListViewHeightBasedOnItems(ListView listView) {

        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter != null) {

            int numberOfItems = listAdapter.getCount();

            // Get total height of all items.
            int totalItemsHeight = 0;
            for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
                View item = listAdapter.getView(itemPos, null, listView);
                float px = 500 * (listView.getResources().getDisplayMetrics().density);
                item.measure(View.MeasureSpec.makeMeasureSpec((int) px, View.MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                totalItemsHeight += item.getMeasuredHeight();
            }

            // Get total height of all item dividers.
            int totalDividersHeight = listView.getDividerHeight() *
                    (numberOfItems - 1);
            // Get padding
            int totalPadding = listView.getPaddingTop() + listView.getPaddingBottom();

            // Set list height.
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalItemsHeight + totalDividersHeight + totalPadding;
            listView.setLayoutParams(params);
            listView.requestLayout();
            //setDynamicHeight(listView);
            return true;

        } else {
            return false;
        }
    }

}