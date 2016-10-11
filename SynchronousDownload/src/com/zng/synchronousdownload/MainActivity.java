package com.zng.synchronousdownload;

import java.util.ArrayList;
import java.util.List;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.db.sqlite.Selector;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.zng.synchronousdownload.adapter.DownLoadAdapter;
import com.zng.synchronousdownload.download.DownloadInfo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {

	private ListView list_down;
	private DownLoadAdapter mDownLoadAdapter;
	private ArrayList<DownloadInfo> downloadInfoList = new ArrayList<DownloadInfo>();
	private DbUtils db;
	@ViewInject(R.id.btn_download)
	private Button btn_download;

	@Override
	protected void onRestart() {
		mDownLoadAdapter.notifyDataSetChanged();
		super.onRestart();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ViewUtils.inject(this);
		list_down = (ListView) findViewById(R.id.list_down);
		db = DbUtils.create(this);
		mDownLoadAdapter = new DownLoadAdapter(this, downloadInfoList);
		list_down.setAdapter(mDownLoadAdapter);
		initData();
		list_down.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(MainActivity.this, DownDetailsActivity.class);
				intent.putExtra("position", position);
				startActivity(intent);

			}
		});

		btn_download.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(MainActivity.this, DownloadListActivity.class);
				startActivity(intent);
			}
		});
	}

	public void initData() {

		List<DownloadInfo> loadInfoList = getData();
		for (DownloadInfo mDownFile : loadInfoList) {
			try {
				// 本地数据
				DownloadInfo mDownFileT = db.findFirst(
						Selector.from(DownloadInfo.class).where("downloadUrl", "=", mDownFile.getDownloadUrl()));
				if (mDownFileT != null) {
					mDownFile = mDownFileT;
					if (mDownFile.getProgress() == mDownFile.getFileLength() && mDownFile.getFileLength() != 0) {
						mDownFile.setState(HttpHandler.State.SUCCESS);
						downloadInfoList.add(mDownFile);
						mDownLoadAdapter.notifyDataSetChanged();

					} else { // 显示为暂停状态
						mDownFile.setState(HttpHandler.State.CANCELLED);
						downloadInfoList.add(mDownFile);
						mDownLoadAdapter.notifyDataSetChanged();

					}
				} else {
					mDownFile.setState(HttpHandler.State.WAITING);
					downloadInfoList.add(mDownFile);
					mDownLoadAdapter.notifyDataSetChanged();
				}
			} catch (DbException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public List<DownloadInfo> getData() {

		List<DownloadInfo> mDownFileList = new ArrayList<DownloadInfo>();

		DownloadInfo mDownFile1 = new DownloadInfo();
		mDownFile1.setFileName("愤怒的小鸟");
		mDownFile1.setDesc("以星球大战电影前传为背景");
		mDownFile1.setState(HttpHandler.State.WAITING);
		mDownFile1.setDownloadUrl("http://down.apk.hiapk.com/down?aid=1832508&em=13.apk");
		mDownFileList.add(mDownFile1);

		DownloadInfo mDownFile2 = new DownloadInfo();
		mDownFile2.setFileName("节奏大师");
		mDownFile2.setDesc("一款老少皆宜的绿色音乐游戏");
		mDownFile1.setState(HttpHandler.State.WAITING);
		mDownFile2.setDownloadUrl("http://down.mumayi.com/292416/mbaidu.apk");
		mDownFileList.add(mDownFile2);

		DownloadInfo mDownFile3 = new DownloadInfo();
		mDownFile3.setFileName("天天酷跑");
		mDownFile3.setDesc("腾讯移动游戏平台首批产品");
		mDownFile1.setState(HttpHandler.State.WAITING);
		mDownFile3.setDownloadUrl("http://down.mumayi.com/407098/mbaidu.apk");
		mDownFileList.add(mDownFile3);
		return mDownFileList;

	}

}
