package com.cloud.jian.core.lock;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractLock implements Lock{

	private static final Log LOG = LogFactory.getLog(AbstractLock.class);

	/**
	 * 这里需不需要保证可见性得讨论, 因为是分布式的锁,
	 * 1.同一个jvm的多个线程使用不同的锁对象其实也是可以的, 这种情况下不需要保证可见性
	 * 2.同一个jvm的多个线程使用同一个锁对象, 那可见性就必须要保证了.
	 */

	protected volatile boolean locked;

	/**
	 * 当前jvm内持有该锁的线程(if have one)
	 */
	private Thread exclusiveOwnerThread;

	@Override
	public void lock() {
		try {
			doLock(false, 0, null, false);
		} catch (InterruptedException e) {
			LOG.info("当前线程:" + exclusiveOwnerThread.getName() + "已经被中�?");
			return;
		}
	}

	@Override
	public void lockInterruptibly() throws InterruptedException {
		doLock(false, 0, null, true);
	}
	
	@Override
	public boolean tryLock(){
		try {
			return doTryLock();
		} catch (InterruptedException e) {
			LOG.info("当前线程:" + exclusiveOwnerThread.getName() + "已经被中�?");
		}
		return false;
	}

	@Override
	public boolean tryLock(long time, TimeUnit unit) {
		try {
			return doLock(true, time, unit, false);
		} catch (InterruptedException e) {
			LOG.info("当前线程:" + exclusiveOwnerThread.getName() + "已经被中�?");
		}
		return false;
	}

	@Override
	public boolean tryLockInterruptibly(long time, TimeUnit unit) throws InterruptedException {
		return doTryLock();
	}

	@Override
	public void unlock() {
		// �?查当前线程是否持有锁
		if (Thread.currentThread() != getExclusiveOwnerThread()) {
			throw new IllegalMonitorStateException("current thread does not hold the lock");
		}
		unlockUse();
		setExclusiveOwnerThread(null);
	}

	protected void setExclusiveOwnerThread(Thread thread) {
		exclusiveOwnerThread = thread;
	}

	protected final Thread getExclusiveOwnerThread() {
		return exclusiveOwnerThread;
	}
	
	/**
	 * 子类实现释放锁的功能
	 */
	protected abstract void unlockUse();
	
	/**
	 * 阻塞式获取锁的实现
	 * @param useTimeout 该线程自旋时是否启用超时等待
	 * @param time 时间单位数量
	 * @param unit 时间单位
	 * @param interrupt 是否响应中断
	 * @return boolean 获取锁是否成功
	 * @throws InterruptedException
	 */
	protected abstract boolean doLock(boolean useTimeout, long time, TimeUnit unit, boolean interrupt) throws InterruptedException;

	/**
	 * 非阻塞式获取锁的实现
	 * @return boolean 获取锁是否成功
	 * @throws InterruptedException
	 */
	protected abstract boolean doTryLock() throws InterruptedException;

}
