package com.michaelfotiadis.ibeaconscanner.activities;

import java.util.Locale;
import uk.co.alt236.bluetoothlelib.device.BluetoothLeDevice;
import uk.co.alt236.bluetoothlelib.device.mfdata.IBeaconManufacturerData;
import uk.co.alt236.bluetoothlelib.resolvers.CompanyIdentifierResolver;
import uk.co.alt236.bluetoothlelib.util.IBeaconUtils;
import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.commonsware.cwac.merge.MergeAdapter;
import com.michaelfotiadis.ibeaconscanner.R;
import com.michaelfotiadis.ibeaconscanner.containers.CustomConstants;
import com.michaelfotiadis.ibeaconscanner.utils.TimeFormatter;


public class DeviceActivity extends ListActivity {

	@SuppressLint("InflateParams")
	private void appendDeviceInfo(MergeAdapter adapter, BluetoothLeDevice device){
		final LinearLayout lt = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item_view_device_info, null);
		final TextView tvName = (TextView) lt.findViewById(R.id.deviceName);
		final TextView tvAddress = (TextView) lt.findViewById(R.id.deviceAddress);

		tvName.setText(device.getName());
		tvAddress.setText(device.getAddress());

		adapter.addView(lt);
	}
	
	/**
	 * Append a header to the MergeAdapter
	 * @param adapter
	 * @param title
	 */
	@SuppressLint("InflateParams")
	private void appendHeader(MergeAdapter adapter, String title){
		final LinearLayout lt = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item_view_header, null);
		final TextView tvTitle = (TextView) lt.findViewById(R.id.title);
		tvTitle.setText(title);

		adapter.addView(lt);
	}
	
	/**
	 * Append body text to the MergeAdapter
	 * @param adapter
	 * @param data
	 */
	@SuppressLint("InflateParams")
	private void appendSimpleText(MergeAdapter adapter, String data){
		final LinearLayout lt = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item_view_textview, null);
		final TextView tvData = (TextView) lt.findViewById(R.id.data);

		tvData.setText(data);

		adapter.addView(lt);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mDevice = getIntent().getParcelableExtra(CustomConstants.Payloads.PAYLOAD_1.toString());
		
		populateDetails(mDevice);
	}
	
	private BluetoothLeDevice mDevice;
	
	@SuppressLint("InflateParams")
	private void appendIBeaconInfo(MergeAdapter adapter, IBeaconManufacturerData iBeaconData){
		final LinearLayout lt = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item_view_ibeacon_details, null);
		final TextView tvCompanyId = (TextView) lt.findViewById(R.id.companyId);
		final TextView tvUUID = (TextView) lt.findViewById(R.id.uuid);
		final TextView tvMajor = (TextView) lt.findViewById(R.id.major);
		final TextView tvMinor = (TextView) lt.findViewById(R.id.minor);
		final TextView tvTxPower = (TextView) lt.findViewById(R.id.txpower);

		tvCompanyId.setText(
				CompanyIdentifierResolver.getCompanyName(iBeaconData.getCompanyIdentifier(), "Not Available")
				+ " (" + hexEncode(iBeaconData.getCompanyIdentifier()) + ")");
		tvUUID.setText(iBeaconData.getUUID().toString());
		tvMajor.setText(iBeaconData.getMajor() + " (" + hexEncode( iBeaconData.getMajor() ) + ")");
		tvMinor.setText(iBeaconData.getMinor() + " (" + hexEncode( iBeaconData.getMinor() ) + ")");
		tvTxPower.setText(iBeaconData.getCalibratedTxPower() + " (" + hexEncode( iBeaconData.getCalibratedTxPower() ) + ")");

		adapter.addView(lt);
	}
	
	@SuppressLint("InflateParams")
	private void appendRssiInfo(MergeAdapter adapter, BluetoothLeDevice device){
		final LinearLayout lt = (LinearLayout) getLayoutInflater().inflate(R.layout.list_item_view_rssi_info, null);
		final TextView tvLastTimestamp = (TextView) lt.findViewById(R.id.lastTimestamp);
		final TextView tvLastRssi = (TextView) lt.findViewById(R.id.lastRssi);

		tvLastTimestamp.setText(formatTime(device.getTimestamp()));
		tvLastRssi.setText(formatRssi(device.getRssi()));

		adapter.addView(lt);
	}

	private String formatRssi(int rssi){
		return getString(R.string.formatter_db, String.valueOf(rssi));
	}
	
	private static String formatTime(long time){
		return TimeFormatter.getIsoDateTime(time);
	}
	
	private void populateDetails(BluetoothLeDevice device) {
		final MergeAdapter adapter = new MergeAdapter();

		if(device == null){
			appendHeader(adapter,"Device Info");
			appendSimpleText(adapter, "Invalid Device");
		} else {
			appendHeader(adapter, "Device Info");
			appendDeviceInfo(adapter, device);
			
			final boolean isIBeacon = IBeaconUtils.isThisAnIBeacon(device);
			if(isIBeacon){
				final IBeaconManufacturerData iBeaconData = new IBeaconManufacturerData(device);
				appendHeader(adapter, "iBeacon Data");
				appendIBeaconInfo(adapter, iBeaconData);
			}
		
			appendHeader(adapter, "RSSI Info");
			appendRssiInfo(adapter, device);
			
		}
		getListView().setAdapter(adapter);
	}
	
	private static String hexEncode(int integer){
		return "0x" + Integer.toHexString(integer).toUpperCase(Locale.US);
	}
	
	
}
