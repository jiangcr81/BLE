package com.vise.bledemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.vise.baseble.ViseBle;
import com.vise.baseble.callback.scan.IScanCallback;
import com.vise.baseble.callback.scan.ScanCallback;
import com.vise.baseble.callback.scan.SingleFilterScanCallback;
import com.vise.baseble.model.BluetoothLeDevice;
import com.vise.baseble.model.BluetoothLeDeviceStore;
import com.vise.bledemo.R;
import com.vise.bledemo.adapter.DeviceAdapter;

import java.util.ArrayList;

/**
 * 设备扫描展示界面
 */
public class DeviceScanActivity extends AppCompatActivity {
	private static String TAG = "DeviceScanActivity";
	public static final String EXTRAS_FILTER_TYPE = "FILTER_TYPE";
	public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
	
	private ListView deviceLv;
	private TextView scanCountTv;
	private int mFilterType;        //扫描过滤类型
	private String mDeviceName = "";
	
	//设备扫描结果展示适配器
	private DeviceAdapter adapter;
	
	private BluetoothLeDeviceStore bluetoothLeDeviceStore = new BluetoothLeDeviceStore();
	
	/**
	 * 周边所有设备扫描回调
	 */
	private ScanCallback periodScanCallback = new ScanCallback(new IScanCallback() {
		@Override
		public void onDeviceFound(final BluetoothLeDevice bluetoothLeDevice) {
			Log.i(TAG, "Founded Scan Device:" + bluetoothLeDevice);
			bluetoothLeDeviceStore.addDevice(bluetoothLeDevice);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (adapter != null && bluetoothLeDeviceStore != null) {
						adapter.setListAll(bluetoothLeDeviceStore.getDeviceList());
						updateItemCount(adapter.getCount());
					}
				}
			});
		}
		
		@Override
		public void onScanFinish(BluetoothLeDeviceStore bluetoothLeDeviceStore) {
			Log.i(TAG, "scan finish " + bluetoothLeDeviceStore);
		}
		
		@Override
		public void onScanTimeout() {
			Log.i(TAG, "scan timeout");
		}
		
	});
	
	/**
	 * 周边设备名字过滤扫描回调
	 */
	private SingleFilterScanCallback singleFilterScanCallback = new SingleFilterScanCallback(new IScanCallback() {
		@Override
		public void onDeviceFound(BluetoothLeDevice bluetoothLeDevice) {
			Log.i(TAG, "SingleFilter Scan Device:" + bluetoothLeDevice);
			bluetoothLeDeviceStore.addDevice(bluetoothLeDevice);
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					if (adapter != null && bluetoothLeDeviceStore != null) {
						adapter.setListAll(bluetoothLeDeviceStore.getDeviceList());
						updateItemCount(adapter.getCount());
					}
				}
			});
		}
		
		@Override
		public void onScanFinish(BluetoothLeDeviceStore bluetoothLeDeviceStore) {
			Log.i(TAG, "SingleFilter scan finish " + bluetoothLeDeviceStore);
		}
		
		@Override
		public void onScanTimeout() {
			Log.i(TAG, "SingleFilter scan timeout");
		}
	});
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_device_scan);
		init();
	}
	
	private void init() {
		deviceLv = (ListView) findViewById(android.R.id.list);
		scanCountTv = (TextView) findViewById(R.id.scan_device_count);
		
		adapter = new DeviceAdapter(this);
		deviceLv.setAdapter(adapter);
		
		deviceLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
				//点击某个扫描到的设备进入设备详细信息界面
				BluetoothLeDevice device = (BluetoothLeDevice) adapter.getItem(position);
				if (device == null) return;
				stopScan();
				Intent intent = new Intent(DeviceScanActivity.this, DeviceDetailActivity.class);
				intent.putExtra(DeviceDetailActivity.EXTRA_DEVICE, device);
				startActivity(intent);
			}
		});
		
		final Intent intent = getIntent();
		mFilterType = intent.getIntExtra(EXTRAS_FILTER_TYPE, 0);
		mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		startScan();
		invalidateOptionsMenu();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		stopScan();
		invalidateOptionsMenu();
		bluetoothLeDeviceStore.clear();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 * 菜单栏的显示
	 *
	 * @param menu 菜单
	 * @return 返回是否拦截操作
	 */
	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		getMenuInflater().inflate(R.menu.scan, menu);
		if (mFilterType == 0) {
			if (periodScanCallback != null && !periodScanCallback.isScanning()) {
				menu.findItem(R.id.menu_stop).setVisible(false);
				menu.findItem(R.id.menu_scan).setVisible(true);
				menu.findItem(R.id.menu_refresh).setActionView(null);
			} else {
				menu.findItem(R.id.menu_stop).setVisible(true);
				menu.findItem(R.id.menu_scan).setVisible(false);
				menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_progress_indeterminate);
			}
		} else if (mFilterType == 1) {
			if (singleFilterScanCallback != null && !singleFilterScanCallback.isScanning()) {
				menu.findItem(R.id.menu_stop).setVisible(false);
				menu.findItem(R.id.menu_scan).setVisible(true);
				menu.findItem(R.id.menu_refresh).setActionView(null);
			} else {
				menu.findItem(R.id.menu_stop).setVisible(true);
				menu.findItem(R.id.menu_scan).setVisible(false);
				menu.findItem(R.id.menu_refresh).setActionView(R.layout.actionbar_progress_indeterminate);
			}
		}
		return true;
	}
	
	/**
	 * 点击菜单栏的处理
	 *
	 * @param item
	 * @return
	 */
	@Override
	public boolean onOptionsItemSelected(final MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menu_scan://开始扫描
				startScan();
				break;
			case R.id.menu_stop://停止扫描
				stopScan();
				break;
		}
		return true;
	}
	
	/**
	 * 开始扫描
	 */
	private void startScan() {
		updateItemCount(0);
		if (adapter != null) {
			adapter.setListAll(new ArrayList<BluetoothLeDevice>());
		}
		if (mFilterType == 0) {
			ViseBle.getInstance().startScan(periodScanCallback);
		} else if (mFilterType == 1) {
			singleFilterScanCallback.setDeviceName(mDeviceName);
			ViseBle.getInstance().startScan(singleFilterScanCallback);
		} else if (mFilterType == 2) {
			singleFilterScanCallback.setDeviceNameExclude(mDeviceName);
			ViseBle.getInstance().startScan(singleFilterScanCallback);
		}
		invalidateOptionsMenu();
	}
	
	/**
	 * 停止扫描
	 */
	private void stopScan() {
		if (mFilterType == 0) {
			ViseBle.getInstance().stopScan(periodScanCallback);
		} else if (mFilterType == 1) {
			ViseBle.getInstance().stopScan(singleFilterScanCallback);
		} else if (mFilterType == 2) {
			ViseBle.getInstance().stopScan(singleFilterScanCallback);
		}
		invalidateOptionsMenu();
	}
	
	/**
	 * 更新扫描到的设备个数
	 *
	 * @param count
	 */
	private void updateItemCount(final int count) {
		scanCountTv.setText(getString(R.string.formatter_item_count, String.valueOf(count)));
	}
	
}
