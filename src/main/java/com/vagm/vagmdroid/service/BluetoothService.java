package com.vagm.vagmdroid.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.vagm.vagmdroid.activities.MainActivity;

/**
 * The Class BluetoothCommandService.
 * @author Roman_Konovalov
 */
public class BluetoothService implements Parcelable {

	// Debugging
	/**
	 * TAG constant.
	 */
	private static final String TAG = "VAGm_BluetoothCommandService";

	/**
	 * D.
	 */
	private static final boolean D = true;

	/**
	 * UUID for RFCOM.
	 */
	private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

	// Member fields
	/**
	 * mAdapter.
	 */
	private final BluetoothAdapter mAdapter = BluetoothAdapter.getDefaultAdapter();

	/**
	 * mHandler.
	 */
	private transient Handler mHandler;

	/**
	 * mConnectThread.
	 */
	private ConnectThread mConnectThread;

	/**
	 * mConnectedThread.
	 */
	private ConnectedThread mConnectedThread;

	/**
	 * mState.
	 */
	private ConnectionState mState = ConnectionState.NONE;

	/**
	 * The Class ConnectionState.
	 * @author Roman_Konovalov
	 */
	public enum ConnectionState {
		/**
		 * we're doing nothing.
		 */
		NONE,

		/**
		 * now listening for incoming connections.
		 */
		LISTEN,

		/**
		 * now initiating an outgoing connection.
		 */
		CONNECTING,

		/**
		 * now connected to a remote device.
		 */
		CONNECTED
	}

	/**
	 * The Class ServiceCommand.
	 * @author Roman_Konovalov
	 */
	public enum ServiceCommand {

		/**
		 * EXIT_CMD.
		 */
		EXIT_CMD,

		/**
		 * VOL_UP.
		 */
		VOL_UP,

		/**
		 * VOL_DOWN.
		 */
		VOL_DOWN,

		/**
		 * MOUSE_MOVE.
		 */
		MOUSE_MOVE,

		/**
		 * MESSAGE_READ.
		 */
		MESSAGE_READ,

		/**
		 * MESSAGE_STATE_CHANGE.
		 */
		MESSAGE_STATE_CHANGE,

		/**
		 * MESSAGE_WRITE.
		 */
		MESSAGE_WRITE,

		/**
		 * MESSAGE_DEVICE_NAME.
		 */
		MESSAGE_DEVICE_NAME,

		/**
		 * MESSAGE_TOAST.
		 */
		MESSAGE_TOAST,

		/**
		 * UNABLE_CONNECT.
		 */
		UNABLE_CONNECT,

		/**
		 * CONNECTION_LOST.
		 */
		CONNECTION_LOST

	}
	
	/**
	 * BLUETOOTH_SERVICE_INSTANCE.
	 */
	public static final String BLUETOOTH_SERVICE_INSTANCE = "BluetoothServiceInstance";

	/**
	 * instance.
	 */
	private static BluetoothService instance = null;

	/**
	 * Constructor.
	 */
	public BluetoothService() {
	}

	/**
	 * Gets Instance.
	 * @return BluetoothCommandService
	 */
	public static BluetoothService getInstance() {
		if (instance == null) {
			instance = new BluetoothService();
		}
		return instance;
	}

	/**
	 * Set the current state of the chat connection.
	 * @param state
	 *            ConnectionState
	 */
	private synchronized void setState(final ConnectionState state) {
		if (D) {
			Log.d(TAG, "setState() " + mState + " -> " + state);
		}
		mState = state;

		// Give the new state to the Handler so the UI Activity can update
		mHandler.obtainMessage(ServiceCommand.MESSAGE_STATE_CHANGE.ordinal(), state.ordinal(), -1).sendToTarget();
	}

	/**
	 * Return the current connection state.
	 * @return ConnectionState
	 */
	public synchronized ConnectionState getState() {
		return mState;
	}

	/**
	 * Start the chat service. Specifically start AcceptThread to begin a
	 * session in listening (server) mode. Called by the Activity onResume()
	 */
	public synchronized void start() {
		if (D) {
			Log.d(TAG, "start");
		}

		// Cancel any thread attempting to make a connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		setState(ConnectionState.LISTEN);
	}

	/**
	 * Start the ConnectThread to initiate a connection to a remote device.
	 * @param device
	 *            The BluetoothDevice to connect
	 */
	public synchronized void connect(final BluetoothDevice device) {
		if (D) {
			Log.d(TAG, "connect to: " + device);
		}

		// Cancel any thread attempting to make a connection
		if (mState == ConnectionState.CONNECTING) {
			if (mConnectThread != null) {
				mConnectThread.cancel();
				mConnectThread = null;
			}
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to connect with the given device
		mConnectThread = new ConnectThread(device);
		mConnectThread.start();
		setState(ConnectionState.CONNECTING);
	}

	/**
	 * Start the ConnectedThread to begin managing a Bluetooth connection.
	 * @param socket
	 *            The BluetoothSocket on which the connection was made
	 * @param device
	 *            The BluetoothDevice that has been connected
	 */
	public synchronized void connected(final BluetoothSocket socket, final BluetoothDevice device) {
		if (D) {
			Log.d(TAG, "connected");
		}

		// Cancel the thread that completed the connection
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}

		// Cancel any thread currently running a connection
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		// Start the thread to manage the connection and perform transmissions
		mConnectedThread = new ConnectedThread(socket);
		mConnectedThread.start();

		// Send the name of the connected device back to the UI Activity
		final Message msg = mHandler.obtainMessage(ServiceCommand.MESSAGE_DEVICE_NAME.ordinal());
		final Bundle bundle = new Bundle();
		bundle.putString(MainActivity.DEVICE_NAME, device.getName());
		msg.setData(bundle);
		mHandler.sendMessage(msg);

		// save connected device
		// mSavedDevice = device;
		// reset connection lost count
		// mConnectionLostCount = 0;

		setState(ConnectionState.CONNECTED);
	}

	/**
	 * Stop all threads.
	 */
	public synchronized void stop() {
		if (D) {
			Log.d(TAG, "stop");
		}
		if (mConnectThread != null) {
			mConnectThread.cancel();
			mConnectThread = null;
		}
		if (mConnectedThread != null) {
			mConnectedThread.cancel();
			mConnectedThread = null;
		}

		setState(ConnectionState.NONE);
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner.
	 * @param out
	 *            The bytes to write
	 * @see ConnectedThread#write(byte[])
	 */
	public void write(final byte[] out) {
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (mState != ConnectionState.CONNECTED) {
				return;
			}
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		r.write(out);
	}

	/**
	 * Write to the ConnectedThread in an unsynchronized manner.
	 * @param out
	 *            The int to write
	 * @see ConnectedThread#write(int)
	 */
	public void write(final int out) {
		// Create temporary object
		ConnectedThread r;
		// Synchronize a copy of the ConnectedThread
		synchronized (this) {
			if (mState != ConnectionState.CONNECTED) {
				return;
			}
			r = mConnectedThread;
		}
		// Perform the write unsynchronized
		r.write(out);
	}

	/**
	 * Indicate that the connection attempt failed and notify the UI Activity.
	 */
	private void connectionFailed() {
		setState(ConnectionState.LISTEN);

		// Send a failure message back to the Activity
		/*final Message msg = mHandler.obtainMessage(ServiceCommand.MESSAGE_TOAST.ordinal());
		final Bundle bundle = new Bundle();
		bundle.putString(MainActivity.TOAST, "Unable to connect device");
		msg.setData(bundle);
		mHandler.sendMessage(msg);*/
		mHandler.obtainMessage(ServiceCommand.UNABLE_CONNECT.ordinal(), -1, -1).sendToTarget();
	}

	/**
	 * Indicate that the connection was lost and notify the UI Activity.
	 */
	private void connectionLost() {
		// mConnectionLostCount++;
		// if (mConnectionLostCount < 3) {
		// // Send a reconnect message back to the Activity
		// Message msg = mHandler.obtainMessage(RemoteBluetooth.MESSAGE_TOAST);
		// Bundle bundle = new Bundle();
		// bundle.putString(RemoteBluetooth.TOAST,
		// "Device connection was lost. Reconnecting...");
		// msg.setData(bundle);
		// mHandler.sendMessage(msg);
		//
		// connect(mSavedDevice);
		// } else {
		setState(ConnectionState.LISTEN);
		// Send a failure message back to the Activity
		/*final Message msg = mHandler.obtainMessage(ServiceCommand.MESSAGE_TOAST.ordinal());
		final Bundle bundle = new Bundle();
		bundle.putString(MainActivity.TOAST, "Device connection was lost");
		msg.setData(bundle);
		mHandler.sendMessage(msg);*/
		mHandler.obtainMessage(ServiceCommand.CONNECTION_LOST.ordinal(), -1, -1).sendToTarget();
		// }
	}

	/**
	 * This thread runs while attempting to make an outgoing connection with a
	 * device. It runs straight through; the connection either succeeds or
	 * fails.
	 */
	private class ConnectThread extends Thread {

		/**
		 * mmSocket.
		 */
		private final BluetoothSocket mmSocket;

		/**
		 * mmDevice.
		 */
		private final BluetoothDevice mmDevice;

		/**
		 * Constructor.
		 * @param device
		 *            BluetoothDevice
		 */
		public ConnectThread(final BluetoothDevice device) {
			mmDevice = device;
			BluetoothSocket tmp = null;

			// Get a BluetoothSocket for a connection with the
			// given BluetoothDevice
			try {
				tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
			} catch (final IOException e) {
				Log.e(TAG, "create() failed", e);
			}
			mmSocket = tmp;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			Log.i(TAG, "BEGIN mConnectThread");
			setName("ConnectThread");

			// Always cancel discovery because it will slow down a connection
			mAdapter.cancelDiscovery();

			// Make a connection to the BluetoothSocket
			try {
				// This is a blocking call and will only return on a
				// successful connection or an exception
				mmSocket.connect();
			} catch (final IOException e) {
				Log.w(TAG, e.getMessage());
				connectionFailed();
				// Close the socket
				try {
					mmSocket.close();
				} catch (final IOException e2) {
					Log.e(TAG, "unable to close() socket during connection failure", e2);
				}
				// Start the service over to restart listening mode
				BluetoothService.this.start();
				return;
			}

			// Reset the ConnectThread because we're done
			synchronized (BluetoothService.this) {
				mConnectThread = null;
			}

			// Start the connected thread
			connected(mmSocket, mmDevice);
		}

		/**
		 * Cancel.
		 */
		public void cancel() {
			try {
				mmSocket.close();
			} catch (final IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

	/**
	 * This thread runs during a connection with a remote device. It handles all
	 * incoming and outgoing transmissions.
	 */
	private class ConnectedThread extends Thread {

		/**
		 * mmSocket.
		 */
		private final BluetoothSocket mmSocket;

		/**
		 * mmInStream.
		 */
		private final InputStream mmInStream;

		/**
		 * mmOutStream.
		 */
		private final OutputStream mmOutStream;

		/**
		 * Constructor.
		 * @param socket BluetoothSocket
		 */
		public ConnectedThread(final BluetoothSocket socket) {
			Log.d(TAG, "create ConnectedThread");
			mmSocket = socket;
			InputStream tmpIn = null;
			OutputStream tmpOut = null;

			// Get the BluetoothSocket input and output streams
			try {
				tmpIn = socket.getInputStream();
				tmpOut = socket.getOutputStream();
			} catch (final IOException e) {
				Log.e(TAG, "temp sockets not created", e);
			}

			mmInStream = tmpIn;
			mmOutStream = tmpOut;
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void run() {
			Log.i(TAG, "BEGIN mConnectedThread");
			final byte[] buffer = new byte[1024];
			byte[] message = new byte[0];

			// Keep listening to the InputStream while connected
			while (true) {
				try {
					// Read from the InputStream
					final int bytes = mmInStream.read(buffer);
					
					byte[] tempArray  = Arrays.copyOf(message, message.length + bytes);
					System.arraycopy(buffer, 0, tempArray, message.length, bytes);
					message = tempArray;
					int necessaryMessageLength = (message[0] < 0 ? message[0] + 256 : message[0]) + 1;
					if (message.length >= necessaryMessageLength) {
						final byte[] sendMessage = Arrays.copyOfRange(message, 1, necessaryMessageLength);
						mHandler.obtainMessage(ServiceCommand.MESSAGE_READ.ordinal(), sendMessage.length, -1, sendMessage).sendToTarget();
						message = Arrays.copyOfRange(message, necessaryMessageLength, message.length);
					}
					
				} catch (final IOException e) {
					Log.e(TAG, "disconnected", e);
					connectionLost();
					break;
				}
			}
		}

		/**
		 * Write to the connected OutStream.
		 * @param buffer
		 *            The bytes to write
		 */
		public void write(final byte[] buffer) {
			try {
				mmOutStream.write(buffer);

				// Share the sent message back to the UI Activity
				// mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1,
				// buffer)
				// .sendToTarget();
			} catch (final IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
		}

		/**
		 * Write to the connected OutStream.
		 * @param out
		 *            The int to write
		 */
		public void write(final int out) {
			try {
				mmOutStream.write(out);

				// Share the sent message back to the UI Activity
				// mHandler.obtainMessage(BluetoothChat.MESSAGE_WRITE, -1, -1,
				// buffer)
				// .sendToTarget();
			} catch (final IOException e) {
				Log.e(TAG, "Exception during write", e);
			}
		}

		/**
		 * Cancel.
		 */
		public void cancel() {
			try {
				// mmOutStream.write(EXIT_CMD);
				mmSocket.close();
			} catch (final IOException e) {
				Log.e(TAG, "close() of connect socket failed", e);
			}
		}
	}

	/**
	 * @param mHandler
	 *            the mHandler to set
	 */
	public void setmHandler(final Handler mHandler) {
		this.mHandler = mHandler;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(final Parcel dest, final int flags) {
		// TODO Auto-generated method stub
	}

	/**
	 * CREATOR.
	 */
	public static final Parcelable.Creator<BluetoothService> CREATOR = new Parcelable.Creator<BluetoothService>() {

		@Override
		public BluetoothService createFromParcel(final Parcel source) {
			return getInstance();
		}

		@Override
		public BluetoothService[] newArray(final int size) {
			throw new RuntimeException("Only one instance is allowed");
		}
	};

}
