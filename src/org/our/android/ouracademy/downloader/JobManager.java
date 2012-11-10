package org.our.android.ouracademy.downloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.util.Log;

class JobManager {
	
	static final String FILE_DOWNQUEUE = "download.obj";
	
	static final int STATE_WAIT = 1;
	static final int STATE_SUSPEND = 2;
	static final int STATE_RESUME = 3;
	static final int STATE_CANCEL = 4;
	
	private Context mContext;
	private JobEntries mJobs;
	
	static class Entry implements Serializable {
		private static final long serialVersionUID = 1L;
		
		String mUrl;
		String mPath;
		boolean mVisibility;
		int mReserved;

		int mId;
		int mState;
	}
	
	private static class JobEntries implements Serializable {
		private static final long serialVersionUID = 1L;
		
		int mIdGenerator = 0;
		ArrayList<Entry> mQueue = new ArrayList<Entry>();
	}
	
	public JobManager(Context context) {
		mContext = context;
	}
	
	synchronized int add(Entry e) {
		int id = getNextId();
		e.mId = id;
		e.mState = STATE_WAIT;
		mJobs.mQueue.add(e);
		
		save(mJobs);
		return id;
	}
	
	synchronized void remove(int id) {
		Entry e = getEntry(id);
		if (null != e) {
			mJobs.mQueue.remove(e);
			save(mJobs);
		}
	}	
	
	synchronized void cancel(int id) {
		Entry e = getEntry(id);
		if (null != e) {
			e.mState = JobManager.STATE_CANCEL;	// reference from download thread
			mJobs.mQueue.remove(e);
			save(mJobs);
		}
	}
	
	synchronized Entry get(int id) {
		return getEntry(id);
	}
	
	synchronized Entry getNext() {
		Entry e = getNextState(STATE_SUSPEND);
		if (null == e) {
			e = getNextState(STATE_WAIT);
		}

		if (null != e) {
			e.mState = STATE_RESUME;
		}

		return e;
	}
	
	synchronized int size() {
		return mJobs.mQueue.size();
	}
	
	synchronized int sizeState(int state) {
		int count = 0;
		Iterator<Entry> it = mJobs.mQueue.iterator();
		Entry e = null;
		while( it.hasNext() ) {
			e = it.next();
			if (state == e.mState) {
				count++;
			}
		}
		
		return count;
	}		
	
	synchronized  void savaQueue() {
		save(mJobs);
	}
	
	synchronized void loadQueue() {
		mJobs = (JobEntries)load();
		if (null == mJobs) {
			mJobs = new JobEntries();
		} else {
			setAllResumeToSuspend();	// if previous state is RESUME, make state to SUSPEND
			removeCancelGarbage();	
		}
	}
	
	synchronized void waitForEntry(long millis) {
		try {
			this.wait(millis);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	synchronized void notifyForEntry() {
		this.notify();
	}		
	
	synchronized void notifyForAll() {
		this.notifyAll();
	}
	
	private void save(Object data) {
		BufferedOutputStream bos = null;
		ObjectOutputStream oos = null;
		try {
			bos = new BufferedOutputStream( mContext.openFileOutput(FILE_DOWNQUEUE, Context.MODE_PRIVATE) );
			oos = new ObjectOutputStream(bos);
			oos.writeObject(data);	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != oos) {
					oos.flush();
					oos.close();					
				} else if (null != bos) {
					bos.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private Object load() {
		Object object = null;
		BufferedInputStream bis = null;
		ObjectInputStream ois = null;
		try {
			bis = new BufferedInputStream( mContext.openFileInput(FILE_DOWNQUEUE) );
			ois = new ObjectInputStream(bis);
			object = ois.readObject();
		} catch (StreamCorruptedException e) {	
			e.printStackTrace();					
		} catch (OptionalDataException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();					
		} catch (IOException e) {
			e.printStackTrace();	
		} finally {
			try {
				if (null != ois) {
					ois.close();
				} else if (null != bis) {
					bis.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return object;
	}
	
	private int getNextId() {
//		int id = 0;
//		Iterator<Entry> it = mJobs.mQueue.iterator();
//		Entry e = null;
//		while( it.hasNext() ) {
//			e = it.next();
//			if (id < e.mId) {
//				id = e.mId;
//			}
//		}	
		mJobs.mIdGenerator++;
		if (0 == mJobs.mIdGenerator) {
			mJobs.mIdGenerator++;
		}
		return mJobs.mIdGenerator;
	}
	
	private Entry getEntry(int id) {
		Entry ret = null;
		Iterator<Entry> it = mJobs.mQueue.iterator();
		Entry e = null;
		while( it.hasNext() ) {
			e = it.next();
			if (id == e.mId) {
				ret = e;
				break;
			}
		}
		
		return ret;
	}

	private Entry getNextState(int state) {
		Entry ret = null;
		Iterator<Entry> it = mJobs.mQueue.iterator();
		Entry e = null;
		while( it.hasNext() ) {
			e = it.next();
			if (state == e.mState) {
				ret = e;
				break;
			}
		}

		return ret;		
	}
	
	private void setAllResumeToSuspend() {
		Iterator<Entry> it = mJobs.mQueue.iterator();
		Entry e = null;
		while( it.hasNext() ) {
			e = it.next();
			if (STATE_RESUME == e.mState) {
				e.mState = STATE_SUSPEND;
			}
		}			
	}
	
	private void removeCancelGarbage() {
		Iterator<Entry> it = mJobs.mQueue.iterator();
		Entry e = null;
		while( it.hasNext() ) {
			e = it.next();
			if (STATE_CANCEL == e.mState) {
				mJobs.mQueue.remove(e);
			}
		}			
	}	
}
