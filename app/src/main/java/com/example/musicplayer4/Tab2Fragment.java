package com.example.musicplayer4;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Tab2Fragment extends Fragment {
    private static final String TAG = "Tab2Fragment";

    private int mPosition;

    private String musicFilePath;

    DataCommunication mCallback;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (DataCommunication) context;
    }

    public void passData(String data) {
        mCallback.songPath(data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab2_fragment,container,false);

        final ListView listView = view.findViewById(R.id.listView);
        final TextAdapter textAdapter = new TextAdapter();
        musicFileList = new ArrayList<>();
        fillMusicList();
        textAdapter.setData(musicFileList);
        listView.setAdapter(textAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mPosition = position;
                musicFilePath = musicFileList.get(mPosition);
                //System.out.println(musicFilePath);
                passData(musicFilePath);
            }
        });
        return view;
    }

    private List<String> musicFileList;

    private void addMusicFilesFrom(String dirPath) {
        final File musicDir = new File(dirPath);
        if(!musicDir.exists()) {
            musicDir.mkdir();
            return;
        }
        final File[] files = musicDir.listFiles();
        for(File file : files) {
            final String path = file.getAbsolutePath();
            if(path.endsWith(".mp3")){
                musicFileList.add(path);
            }
        }
    }

    private void fillMusicList() {
        musicFileList.clear();
        addMusicFilesFrom(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC)));
        addMusicFilesFrom((String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS))));
    }

    class TextAdapter extends BaseAdapter {

        private List<String> data = new ArrayList<>();

        void setData(List<String> mData){
            data.clear();
            data.addAll(mData);
            notifyDataSetChanged();
        }

        @Override
        public int getCount(){
            return data.size();
        }

        @Override
        public String getItem(int position){
            return null;
        }

        @Override
        public long getItemId(int position){
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null){
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
                convertView.setTag(new ViewHolder((TextView) convertView.findViewById(R.id.myItem)));

            }
            ViewHolder holder = (ViewHolder) convertView.getTag();
            final String item = data.get(position);
            holder.info.setText(item.substring(item.lastIndexOf( '/') +1));
            return convertView;

        }

        class ViewHolder{
            TextView info;
            ViewHolder(TextView mInfo){
                info = mInfo;
            }
        }
    }

}
