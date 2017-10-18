package com.liulunsheng.bluetoothdemo;

import android.app.Activity;
import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.liulunsheng.bluetoothdemo.bean.DeviceList;
import com.liulunsheng.bluetoothdemo.bean.ScanedBikeDevice;
import com.liulunsheng.bluetoothdemo.bean.ScanedLandDevice;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Activity for scanning and displaying available Bluetooth LE devices.
 */
public class DeviceScanActivity extends ListActivity {
    private LandListAdapter mLandListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private ScanedBikeDevice[] bikeDeviceList;
    private ScanedLandDevice[] landDeviceList;
    private DeviceList mDeviceList = new DeviceList();
    private int currentCount = 0;
    private int LANDMARK_NUMBER = 20;
    private boolean mScanning;
    int mHour;
    int mMinutes;

    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setTitle(R.string.title_devices);

        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            finish();
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                //mLandListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initializes list view adapter.
        mLandListAdapter = new LandListAdapter();
        setListAdapter(mLandListAdapter);
        scanLeDevice(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        scanLeDevice(false);
//        mLandListAdapter.clear();
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            mScanning = true;
            mBluetoothAdapter.startLeScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
        invalidateOptionsMenu();
    }

    private class LandListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private ArrayList<Integer> mRSSI;
        private ArrayList<byte[]> mRecords;
        private LayoutInflater mInflator;

        public LandListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mRSSI = new ArrayList<Integer>();
            mRecords = new ArrayList<byte[]>();
            mInflator = DeviceScanActivity.this.getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
            if(!mLeDevices.contains(device)) {
                mLeDevices.add(device);
                mRSSI.add(rssi);
                mRecords.add(scanRecord);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.listitem_device, null);
                viewHolder = new ViewHolder();
                //viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.deviceBroadcastPack = (TextView) view.findViewById(R.id.device_broadcastPack);
                viewHolder.deviceRssi = (TextView) view.findViewById(R.id.device_rssi);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            int rssi = mRSSI.get(i);
            byte[] scanRecord = mRecords.get(i);

            final String deviceName = "设备名："+device.getName();
            //final String deviceAddr = "Mac地址："+device.getAddress();
            final String broadcastPack = "广播包："+byteArrayToStr(scanRecord)+ "----Name:" + byteGetName(scanRecord,2,0,6)
                    +"----Id:"+byteGetName(scanRecord,19,0,8)+"----Power:"+byteGetName(scanRecord,27,0,1)
                    +"----Status:"+byteGetName(scanRecord,28,0,2);
            final String rssiString = "RSSI："+String.valueOf(rssi);

            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else
                viewHolder.deviceName.setText(R.string.unknown_device);
            //viewHolder.deviceAddress.setText(deviceAddr);
            viewHolder.deviceBroadcastPack.setText(broadcastPack);
            viewHolder.deviceRssi.setText(rssiString);

            return view;
        }
    }

    // Adapter for holding devices found through scanning.
//    private class BikeListAdapter extends BaseAdapter {
//        private ArrayList<BluetoothDevice> mBikeDevices;
//        private ArrayList<Integer> mRSSI;
//        private ArrayList<byte[]> mRecords;
//        private LayoutInflater mInflator;
//
//        public BikeListAdapter() {
//            super();
//            mBikeDevices = new ArrayList<BluetoothDevice>();
//            mRSSI = new ArrayList<Integer>();
//            mRecords = new ArrayList<byte[]>();
//            mInflator = DeviceScanActivity.this.getLayoutInflater();
//        }
//
//        public void addDevice(BluetoothDevice device, int rssi, byte[] scanRecord) {
//            if(!mBikeDevices.contains(device)) {
//                mBikeDevices.add(device);
//                mRSSI.add(rssi);
//                mRecords.add(scanRecord);
//            }
//        }
//
//        public BluetoothDevice getDevice(int position) {
//            return mBikeDevices.get(position);
//        }
//
//        public void clear() {
//            mBikeDevices.clear();
//        }
//
//        @Override
//        public int getCount() {
//            return mBikeDevices.size();
//        }
//
//        @Override
//        public Object getItem(int i) {
//            return mBikeDevices.get(i);
//        }
//
//        @Override
//        public long getItemId(int i) {
//            return i;
//        }
//
//        @Override
//        public View getView(int i, View view, ViewGroup viewGroup) {
//            ViewHolder viewHolder;
//            // General ListView optimization code.
//            if (view == null) {
//                view = mInflator.inflate(R.layout.listitem_device, null);
//                viewHolder = new ViewHolder();
//                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
//                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
//                viewHolder.deviceBroadcastPack = (TextView) view.findViewById(R.id.device_broadcastPack);
//                viewHolder.deviceRssi = (TextView) view.findViewById(R.id.device_rssi);
//                viewHolder.deviceBikeId = (TextView) view.findViewById(R.id.bike_id);
//                viewHolder.deviceBikePower = (TextView) view.findViewById(R.id.bike_power);
//                view.setTag(viewHolder);
//            } else {
//                viewHolder = (ViewHolder) view.getTag();
//            }
//
//            BluetoothDevice device = mBikeDevices.get(i);
//            int rssi = mRSSI.get(i);
//            byte[] scanRecord = mRecords.get(i);
//            String bikeid = byteGetName(scanRecord,11,0,16);
//            String bikepower = byteGetName(scanRecord,27,0,1);
//
//            final String deviceName = "设备名："+device.getName();
//            final String deviceAddr = "Mac地址："+device.getAddress();
//            final String broadcastPack = "广播包："+byteArrayToStr(scanRecord)+ "----" + byteGetName(scanRecord,2,0,8)
//                    +"----"+bikeid+"----"+bikepower
//                    +"----"+byteGetName(scanRecord,28,0,2);
////            + "----" + byteGetName(scanRecord)
//            final String rssiString = "RSSI："+String.valueOf(rssi);
//            final String bikeId = "Bike id:"+String.valueOf(bikeid);
//            final String bikePower = "Bike power:"+String.valueOf(bikepower);
//
//            if (deviceName != null && deviceName.length() > 0)
//                viewHolder.deviceName.setText(deviceName);
//            else
//                viewHolder.deviceName.setText(R.string.unknown_device);
//            viewHolder.deviceAddress.setText(deviceAddr);
//            viewHolder.deviceBroadcastPack.setText(broadcastPack);
//            viewHolder.deviceRssi.setText(rssiString);
//            viewHolder.deviceBikeId.setText(bikeId);
//            viewHolder.deviceBikePower.setText(bikePower);
//
//            return view;
//        }
//    }

    public static String byteArrayToStr(byte[] byteArray) {
        if (byteArray == null) {
            return null;
        }
        String str = new String(byteArray);
        return str;
    }

    public static String byteGetName(byte[] byteArray,int objectStartNo, int startNo, int length){

        byte[] newByte1 = new byte[length];
        System.arraycopy(byteArray, objectStartNo, newByte1, startNo, length);
        String name = new String(newByte1);
        return name;
    }

//    static final char[] hexArray = "0123456789ABCDEF".toCharArray();
//    private static String byteToHex(byte[] bytes){
//        char[] hexChars = new char[bytes.length * 2];
//        for (int j = 0; j < bytes.length; j++){
//            int v = bytes[j] & 0xFF;
//            hexChars[j * 2] = hexArray[v >>> 4];
//            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
//        }
//        return new String(hexChars);
//    }

    private void getCurrentTime(){
        long time = System.currentTimeMillis();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        mHour = calendar.get(Calendar.HOUR);
        mMinutes = calendar.get(Calendar.MINUTE);
    }



    // Device scan callback.
    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {

        @Override
        public void onLeScan(final BluetoothDevice device, final int rssi, final byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String deviceName = byteGetName(scanRecord,2,0,6);
                    String id = byteGetName(scanRecord,19,0,8);
                    int inpark = 0;
                    getCurrentTime();
                    //if (mHour == 3 && mMinutes == 0){
                        if(!TextUtils.isEmpty(deviceName)) {
                            if (deviceName.startsWith("XKLAND") && id.startsWith("XK")) {
                                mLandListAdapter.addDevice(device, rssi, scanRecord);
                                mLandListAdapter.notifyDataSetChanged();
                                addNewLandDevice(device,deviceName,scanRecord,rssi,id);
                            }
                        }
                    //}
                    //else
                        if(!TextUtils.isEmpty(deviceName)){
                            if(deviceName.startsWith("XKBIKE") && id.startsWith("XK")){
                                mLandListAdapter.addDevice(device,rssi,scanRecord);
                                mLandListAdapter.notifyDataSetChanged();
                                addNewBikeDevice(deviceName, device, scanRecord, rssi,id,inpark);
                            }
                        }
                }
            });
        }
    };

    private void addNewLandDevice(BluetoothDevice bluetoothDevice, String deviceName, byte[] scanRecord, int RSSI, String landId){
        ScanedLandDevice scanedLandDevice = new ScanedLandDevice(bluetoothDevice,deviceName,scanRecord,RSSI,landId);

        landDeviceList = new ScanedLandDevice[20];
        for (int i = 0; i < LANDMARK_NUMBER; i++ ){
            landDeviceList[i] = scanedLandDevice;
            // TODO: 2017/10/18 上报服务器 
        }
    }

    /**
     * 方法已過期
     * @param deviceName
     * @param bluetoothDevice
     * @param scanRecord
     * @param RSSI
     * @param bikeId
     * @param inPark
     */
    @Deprecated
    private void addNewBikeDevice(String deviceName, BluetoothDevice bluetoothDevice, byte[] scanRecord, int RSSI, String bikeId, int inPark){
        ScanedBikeDevice scanedBikeDevice = new ScanedBikeDevice(deviceName, bluetoothDevice, scanRecord, RSSI, bikeId, inPark);

        String id = scanedBikeDevice.getBikeId();
        int in_Park = scanedBikeDevice.getInPark();
        if(bikeDeviceList != null){

            ScanedBikeDevice scanedBikeDevice1 = null;
            for(int i = 0; i < currentCount; i++){

                if(id.equals(bikeDeviceList[i].getBikeId())){
                    scanedBikeDevice1 = bikeDeviceList[i];
                    break;
                }else{
                    continue;
                }


//                if(!id.equals(bikeDeviceList[i].getBikeId())){
//                    //车标不存在，加入数组，且改变状态
//                    Log.d("scanedBikeDevice", "----"+ scanedBikeDevice.getBikeId());
//                    bikeDeviceList[i].setInPark(1);
//                    bikeDeviceList[bikeDeviceList.length] = scanedBikeDevice;
//                    currentCount++;
//                    // TODO: 2017/10/17 上报服务器
//                }else if (id.equals(bikeDeviceList[i].getBikeId()) && in_Park == 0){
//                    //车标存在列表中，但状态为0，上报服务器
//                    // TODO: 2017/10/17 上报服务器
//                }
            }

            if(scanedBikeDevice1!=null){

                if(scanedBikeDevice1.getInPark() != scanedBikeDevice.getInPark()){
                    scanedBikeDevice1.setInPark(1);
                    //上报服务器

                }else{
                    //如果相等则不做事情
                }
            }else{
                if(currentCount < 20){
                    bikeDeviceList[currentCount] = scanedBikeDevice;
                    currentCount++;
                }else{
//                    bikeDeviceList[currentLoc] = scanedBikeDevice;
                    currentCount = 20;
                }

            }

        }else{
            bikeDeviceList = new ScanedBikeDevice[20];
            bikeDeviceList[0] = scanedBikeDevice;
            bikeDeviceList[0].setInPark(1);
            currentCount++;
        }
    }

    /**
     * 添加掃描設備並上報服務器
     * @param deviceName
     * @param bluetoothDevice
     * @param scanRecord
     * @param RSSI
     * @param bikeId
     * @param inPark
     */
    private void add_device_to_list(String deviceName, BluetoothDevice bluetoothDevice, byte[] scanRecord, int RSSI, String bikeId, int inPark){
        ScanedBikeDevice scanedBikeDevice = new ScanedBikeDevice(deviceName, bluetoothDevice, scanRecord, RSSI, bikeId, inPark);
        int device_exist = 0;
        int device_report = 0;
//        String id = scanedBikeDevice.getBikeId();
        for(int i = 0; i < mDeviceList.MAX; i++){
            if(bikeId.equals(mDeviceList.getScanedBikeDevices()[i].getBikeId())){
                device_exist = 1;
                if(mDeviceList.getScanedBikeDevices()[i].getInPark() != inPark){
                    mDeviceList.getScanedBikeDevices()[i].setInPark(inPark);
                }else
                    device_report = 1;

                if(i == (mDeviceList.getLocation_flag()+1)%mDeviceList.MAX){
                  mDeviceList.setLocation_flag(i);
                }
                break;
            }
        }
        if(device_exist == 0){
            mDeviceList.getScanedBikeDevices()[mDeviceList.getLocation_flag()] = scanedBikeDevice;
            mDeviceList.setLocation_flag((mDeviceList.getLocation_flag()+1)%mDeviceList.MAX);
        }
        if(device_report == 0){
            if(inPark == 0){
                //在停车点内
                Toast.makeText(getApplicationContext(), "******   ******   *****   *** IN ****   ******    ******", Toast.LENGTH_SHORT).show();
            }else{
                //不在停车点内

            }
        }

    }

    static class ViewHolder {
        TextView deviceName;
        //TextView deviceAddress;
        TextView deviceBroadcastPack;//加入广播包数据
        TextView deviceRssi;//加入RSSI
    }
}