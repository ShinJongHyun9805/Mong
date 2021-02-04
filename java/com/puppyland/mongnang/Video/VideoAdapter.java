package com.puppyland.mongnang.Video;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.puppyland.mongnang.DTO.VideoDTO;
import com.puppyland.mongnang.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

public class VideoAdapter extends BaseAdapter {
    private ArrayList<VideoDTO> mList = new ArrayList<VideoDTO>();
    Context context;
    public  ImageView ime;
    ThumnailLoadTask thumnailLoadTask;
    private String MOVIE_URL = "http://192.168.219.100:8092/upload/videoupload/";
    //아이템 데이터 추가를 위한 함수.



    public void addItem(int vno , String userid, String nickname, String videoname){
        VideoDTO item = new VideoDTO();
        item.setVno(vno);
        item.setUserid(userid);
        item.setNickname(nickname);
        item.setVideoname(videoname);
        mList.add(item);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public VideoDTO getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context=parent.getContext();
        VideoDTO videoDTO = mList.get(position);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.videolist_item , parent ,false);
        }

        TextView vno = convertView.findViewById(R.id.vno_listitem);
        TextView userid = convertView.findViewById(R.id.userid_listitem);;
        TextView videoname = convertView.findViewById(R.id.videoname_listitem);;
        TextView nickname = convertView.findViewById(R.id.nickname_listitem);;
        ImageView img = convertView.findViewById(R.id.img_listitem);
       // ime = convertView.findViewById(R.id.img_listitem);
        vno.setText(String.valueOf(videoDTO.getVno()));
        userid.setText(videoDTO.getUserid());
        videoname.setText(videoDTO.getVideoname());
        nickname.setText(videoDTO.getNickname());
        String name = videoDTO.getVideoname();
        for(int i=0;i<4;i++){
            name = name.substring(0, name.length()-1); // 뒤에 .mp4 지우고
        }
        String fileName = name + ".jpg";  // 파일이름은 마음대로!
        Picasso.get().load("http://192.168.219.100:8092/upload/videoupload/" + fileName).error(R.drawable.monglogo2).fit().centerCrop().into(img);
     //   thumnailLoadTask = new ThumnailLoadTask(img);
        //thumnailLoadTask.execute(videoDTO.getVideoname());
  //      thumnailLoadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR , videoDTO.getVideoname());
        //  img.setImageBitmap(retriveVideoFrameFromVideo(MOVIE_URL+videoDTO.getNickname()));
        return convertView;
    }

    private  class  ThumnailLoadTask extends AsyncTask<String,Integer,Bitmap>{
        ImageView imageView;
        public ThumnailLoadTask (ImageView imgview){
            imageView = imgview;
        }
        @Override
        protected Bitmap doInBackground(String... string) {
            try {
                Log.v("videoPathdd", string[0]);
              Bitmap bitmap =  retriveVideoFrameFromVideo(MOVIE_URL+string[0]);
                return bitmap;
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                return  null;
            }
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            thumnailLoadTask=null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            imageView.setImageBitmap(bitmap);
        }
        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }
    public static Bitmap retriveVideoFrameFromVideo(String videoPath)throws Throwable
    {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try
        {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime(1, MediaMetadataRetriever.OPTION_CLOSEST);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)"+ e.getMessage());
        }
        finally
        {
            if (mediaMetadataRetriever != null)
            {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
}
