package com.zng.synchronousdownload.adapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.DbException;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.util.LogUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.zng.synchronousdownload.R;
import com.zng.synchronousdownload.download.AdStrUtil;
import com.zng.synchronousdownload.download.BaseDownloadHolder;
import com.zng.synchronousdownload.download.DownloadInfo;
import com.zng.synchronousdownload.download.DownloadManager;
import com.zng.synchronousdownload.download.DownloadRequestCallBack;
import com.zng.synchronousdownload.download.DownloadService;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DownLoadAdapter extends BaseAdapter {

	private DownloadManager downloadManager;
	private Context mContext;
	private ArrayList<DownloadInfo> downloadInfoList;

	public DownLoadAdapter(Context context, ArrayList<DownloadInfo> downloadInfo) {
		this.mContext = context;
		this.downloadInfoList = downloadInfo;
		downloadManager = DownloadService.getDownloadManager(context);
	}

	@Override
	public int getCount() {
		return downloadInfoList.size();
	}

	@Override
	public Object getItem(int position) {
		return downloadInfoList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		DownloadInfo downloadInfo =downloadInfoList.get(position);
		holdler holder = null;
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.download_item, null);
			holder = new holdler(downloadInfo);
			ViewUtils.inject(holder, convertView);
			convertView.setTag(holder);
			holder.refresh();
		} else {
			holder = (holdler) convertView.getTag();
			holder.update(downloadInfo);
		}

		HttpHandler<File> handler = downloadInfo.getHandler();
		if (handler != null) {
			DownloadManager.ManagerCallBack requestCallBack = (DownloadManager.ManagerCallBack) handler
					.getRequestCallBack();
			if (requestCallBack.getBaseCallBack() == null) {
				requestCallBack.setBaseCallBack(new DownloadRequestCallBack());
			}
			requestCallBack.setUserTag(new WeakReference<holdler>(holder));
		}

		return convertView;
	}

	public class holdler extends BaseDownloadHolder {

		@ViewInject(R.id.itemsIcon)
		ImageView itemsIcon;
		@ViewInject(R.id.itemsTitle)
		TextView itemsTitle;
		@ViewInject(R.id.tv_category)
		TextView tv_category;
		@ViewInject(R.id.tv_size)
		TextView tv_size;
		@ViewInject(R.id.itemsDesc)
		TextView itemsDesc;

		@ViewInject(R.id.received_progressBar)
		RelativeLayout received_progressBar;

		@ViewInject(R.id.received_progress)
		ProgressBar received_progress;
		@ViewInject(R.id.received_progress_number)
		TextView received_progress_number;
		@ViewInject(R.id.received_progress_percent)
		TextView received_progress_percent;// 百分比

		@ViewInject(R.id.operateBtn)
		TextView operateBtn;

		public holdler(DownloadInfo downloadInfo) {
			super(downloadInfo);

		}

		@Override
		public void refresh() {
			itemsIcon.setImageResource(R.drawable.app_logo);
			itemsTitle.setText(downloadInfo.getFileName() + downloadInfo.getId());
			itemsDesc.setText(downloadInfo.getDesc());
			operateBtn.setText("下载");
			tv_category.setText("收银支付"+" | ");
			tv_size.setText(AdStrUtil.getSizeDesc(downloadInfo.getFileLength()));
			if (downloadInfo.getFileLength() > 0) {
				received_progress.setProgress((int) (downloadInfo.getProgress() * 100 / downloadInfo.getFileLength()));
				long c = downloadInfo.getProgress() * 100 / downloadInfo.getFileLength();
				received_progress_percent.setText(c + "%");
				received_progress_number.setText(AdStrUtil.getSizeDesc(downloadInfo.getProgress()) + "/"
						+ AdStrUtil.getSizeDesc(downloadInfo.getFileLength()));
			} else {
				received_progress.setProgress(0);
				received_progress_percent.setText(0 + "%");
				received_progress_number.setText(AdStrUtil.getSizeDesc(downloadInfo.getProgress()) + "/"
						+ AdStrUtil.getSizeDesc(downloadInfo.getFileLength()));
			}
			HttpHandler.State state = downloadInfo.getState();
			switch (state) {
			case WAITING:
				operateBtn.setText("下载");
				break;
			case STARTED:
				operateBtn.setText("暂停");
				tv_category.setVisibility(View.GONE);
				tv_size.setVisibility(View.GONE);
				received_progressBar.setVisibility(View.VISIBLE);
				break;
			case LOADING:
				operateBtn.setText("暂停");
				tv_category.setVisibility(View.GONE);
				tv_size.setVisibility(View.GONE);
				received_progressBar.setVisibility(View.VISIBLE);
				break;
			case CANCELLED:
				operateBtn.setText("继续");
				tv_category.setVisibility(View.GONE);
				tv_size.setVisibility(View.GONE);
				received_progressBar.setVisibility(View.VISIBLE);
				break;
			case SUCCESS:
				operateBtn.setText("打开");
				break;
			case FAILURE:
				operateBtn.setText("重试");
				tv_category.setVisibility(View.GONE);
				tv_size.setVisibility(View.GONE);
				received_progressBar.setVisibility(View.VISIBLE);
				break;
			default:
				break;
			}
		}

		public void setShowState() {
			tv_category.setVisibility(View.GONE);
			tv_size.setVisibility(View.GONE);
			received_progressBar.setVisibility(View.VISIBLE);
			if (downloadInfo.getFileLength() > 0) {
				received_progress.setProgress((int) (downloadInfo.getProgress() * 100 / downloadInfo.getFileLength()));
				long c = downloadInfo.getProgress() * 100 / downloadInfo.getFileLength();
				received_progress_percent.setText(c + "%");
				received_progress_number.setText(AdStrUtil.getSizeDesc(downloadInfo.getProgress()) + "/"
						+ AdStrUtil.getSizeDesc(downloadInfo.getFileLength()));
			} else {
				received_progress.setProgress(0);
				received_progress_percent.setText(0 + "%");
				received_progress_number.setText(AdStrUtil.getSizeDesc(downloadInfo.getProgress()) + "/"
						+ AdStrUtil.getSizeDesc(downloadInfo.getFileLength()));
			}
		}


		@OnClick(R.id.operateBtn)
		public void stop(View view) {
			HttpHandler.State state = downloadInfo.getState();
			switch (state) {
			case WAITING:
				String target = "/sdcard/xUtils/" + System.currentTimeMillis() + downloadInfo.getFileName()+".apk";
				try {
					downloadManager.addNewDownload(downloadInfo.getDownloadUrl(), downloadInfo.getFileName(), target, true, true, new DownloadRequestCallBack());
				} catch (DbException e) {
					e.printStackTrace();
				}
				notifyDataSetChanged();
				break;
			case STARTED:
			case LOADING:
				try {
					downloadManager.stopDownload(downloadInfo);
				} catch (DbException e) {
					LogUtils.e(e.getMessage(), e);
				}
				break;
			case CANCELLED:
			case FAILURE:
				try {
					downloadManager.resumeDownload(downloadInfo, new DownloadRequestCallBack());
				} catch (DbException e) {
					LogUtils.e(e.getMessage(), e);
				}
				notifyDataSetChanged();
				break;
			default:
				break;
			}
		}

	}

}
