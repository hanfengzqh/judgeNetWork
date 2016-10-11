package com.zng.network;
import com.example.test.R;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class TestActivity extends Activity implements NetReceiver.NetEventHandle{
	
    private TextView tv;
    private Button button;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NetReceiver.ehList.add(this);

        tv=(TextView)super.findViewById(R.id.main_net_tip);
        button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				String currentNetType = getCurrentNetType(getApplicationContext());
				if(currentNetType.equals("null")){
					button.setText("没有网络连接");
				}else if(currentNetType.equals("wifi")){
					button.setText("wifi");
				}else if(currentNetType.equals("2G")){
					button.setText("2G网络");
				}else if(currentNetType.equals("3G")){
					button.setText("3G网络");
				}else if(currentNetType.equals("4G")){
					button.setText("4G网格");
				}
				
				 Toast.makeText(TestActivity.this, currentNetType, 0).show();
				 
				 
			}
		});
    }

    @Override
    protected void onPause() {
        super.onPause();
        NetReceiver.ehList.remove(this);
    }

    @Override
    public void netState(NetReceiver.NetState netCode) {
        switch (netCode){
            case NET_NO:
                tv.setText("没有网络连接");
                Toast.makeText(this, "没有网络连接", 0).show();
                break;
            case NET_2G:
                tv.setText("2g网络");
                Toast.makeText(this, "2g网络", 0).show();
                break;
            case NET_3G:
                tv.setText("3g网络");
                Toast.makeText(this, "3g网络", 0).show();
                break;
            case NET_4G:
                tv.setText("4g网络");
                Toast.makeText(this, "4g网络", 0).show();
                break;
            case NET_WIFI:
                tv.setText("WIFI网络");
                Toast.makeText(this, "WIFI网络", 0).show();
                break;
            case NET_UNKNOWN:
                tv.setText("未知网络");
                Toast.makeText(this, "未知网络", 0).show();
                break;
            default:
                tv.setText("不知道什么情况~>_<~");
                Toast.makeText(this, "不知道什么情况~>_<~", 0).show();
        }
    }
    public static String getCurrentNetType(Context context) {
		String type = "";
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cm.getActiveNetworkInfo();
		if (info == null) {
			type = "null";
		} else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
			type = "wifi";
		} else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
			int subType = info.getSubtype();
			if (subType == TelephonyManager.NETWORK_TYPE_CDMA
					|| subType == TelephonyManager.NETWORK_TYPE_GPRS
					|| subType == TelephonyManager.NETWORK_TYPE_EDGE) {
				type = "2G";
			} else if (subType == TelephonyManager.NETWORK_TYPE_UMTS
					|| subType == TelephonyManager.NETWORK_TYPE_HSDPA
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_A
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_0
					|| subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
				type = "3G";
			} else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {
				// LTE是3g到4g的过渡，是3.9G的全球标准
				type = "4G";
			}
		}
		return type;
	}
    
    
}
