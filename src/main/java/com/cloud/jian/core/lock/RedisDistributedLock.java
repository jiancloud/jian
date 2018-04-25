package com.cloud.jian.core.lock;

import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;

public class RedisDistributedLock extends AbstractLock {

	private static final Log LOG = LogFactory.getLog(RedisDistributedLock.class);
	private String host;
	private int port;
	private Jedis jedis;
	// 锁的名字
	protected String lockKey;
	// 锁的有效时长(毫秒)
	protected long lockExpires;
	
	public RedisDistributedLock(String host, int port, String lockKey, long lockExpires) {
		super();
		this.host = host;
		this.port = port;
		this.lockKey = lockKey;
		this.lockExpires = lockExpires;
	}
	
	public RedisDistributedLock(String host, int port, long lockExpires) {
		super();
		this.host = host;
		this.port = port;
		this.lockExpires = lockExpires;
	}
	
	public RedisDistributedLock(){}
	
	public void init(){
		this.jedis = new Jedis(host, port);
	}
	
	public void dispose(){
		this.jedis = null;
	}
	
	@Override
	public void setLockIdentity(String lockId){
		this.lockKey = lockId;
	}

	protected boolean doLock(boolean useTimeout, long time, TimeUnit unit, boolean interrupt) throws InterruptedException{
		if (interrupt) {
			checkInterruption();
		}
		long start = System.currentTimeMillis();
		long timeout = unit.toMillis(time);
		Jedis jedis = getJedis();
		while (useTimeout ? isTimeout(start, timeout) : true) {
			if (interrupt) {
				checkInterruption();
			}
			// 锁超时时间
			long lockExpireTime = System.currentTimeMillis() + lockExpires + 1;
			String stringOfLockExpireTime = String.valueOf(lockExpireTime);
			// 在redis中设值成功
			// 获取到锁
			if (jedis.setnx(lockKey, stringOfLockExpireTime) == 1) {
				// 成功获取到锁, 设置相关标识
				locked = true;
				setExclusiveOwnerThread(Thread.currentThread());
				LOG.info("当前线程:" + Thread.currentThread().getName() + "成功获取到锁：" + lockKey);
				return true;
			}
			// 在redis中设值失败
			String value = jedis.get(lockKey);
			// 锁已经过期
			if (value != null && isTimeExpired(value)) {
				// 假设多个线程(多个jvm)同时走到这里
				// getSet方法是原子性的
				String oldValue = jedis.getSet(lockKey, stringOfLockExpireTime);
				// 但是走到这里时每个线程拿到的oldValue肯定不可能一样(因为getset是原子性的)
				// 假如拿到的oldValue依然是expired的，那么就此时没有其他线程获得锁，则该线程获得锁
				if (oldValue != null && isTimeExpired(oldValue)) {
					// 成功获取到锁, 设置相关标识
					locked = true;
					setExclusiveOwnerThread(Thread.currentThread());
					LOG.info("当前线程:" + Thread.currentThread().getName() + "成功获取到锁：" + lockKey);
					return true;
				}
			} 
		}
		return false;
	}

	public boolean doTryLock() {
		// 锁超时时间
		long lockExpireTime = System.currentTimeMillis() + lockExpires + 1;
		String stringOfLockExpireTime = String.valueOf(lockExpireTime);
		Jedis jedis = getJedis();
		// redis中设值成功，此时该线程获得锁
		if (jedis.setnx(lockKey, stringOfLockExpireTime) == 1) {
			// 成功获取到锁, 设置相关标识
			locked = true;
			setExclusiveOwnerThread(Thread.currentThread());
			LOG.info("当前线程:" + Thread.currentThread().getName() + "成功获取到锁：" + lockKey);
			return true;
		}
		// redis中设值失败
		String value = jedis.get(lockKey);
		// 如果锁过期了
		if (value != null && isTimeExpired(value)) {
			// 假设多个线程(多个jvm)同时走到这里
			// getSet是原子操作
			String oldValue = jedis.getSet(lockKey, stringOfLockExpireTime);
			// 但是走到这里时每个线程拿到的oldValue肯定不可能一样(因为getset是原子性的)
			// 假如拿到的oldValue依然是expired的，那么就说明此时没有其他线程获得锁，则该线程获得锁
			if (oldValue != null && isTimeExpired(oldValue)) {
				// 成功获取到锁, 设置相关标识
				locked = true;
				setExclusiveOwnerThread(Thread.currentThread());
				LOG.info("当前线程:" + Thread.currentThread().getName() + "成功获取到锁：" + lockKey);
				return true;
			}
		}
		return false;
	}


	/**
	 * 检查是否锁被占用.
	 */
	public boolean isLocked() {
		Jedis jedis = getJedis();
		if (locked) {
			return true;
		} else {
			String value = jedis.get(lockKey);
			// 这里其实是有问题的, 想:当get方法返回value后, 假设这个value已经是过期的了,
			// 而就在这瞬间, 另一个节点set了value, 这时锁是被别的线程(节点持有), 而接下来的判断
			// 是检测不出这种情况的.不过这个问题应该不会导致其它的问题出现, 因为这个方法的目的本来就
			// 不是同步控制, 它只是一种锁状态的报告.
			return !isTimeExpired(value);
		}
	}

	@Override
	protected void unlockUse() {
		Jedis jedis = getJedis();
		String value = jedis.get(lockKey);
		// 判断锁是否过期
		if (!isTimeExpired(value)) {
			doUnlock();
		}
	}

	/**
	 * 检测当前线程是否被中断
	 * @throws InterruptedException
	 */
	private void checkInterruption() throws InterruptedException {
		if(Thread.currentThread().isInterrupted()) {
			LOG.info("当前线程：" + Thread.currentThread().getName() + "被中断");
			throw new InterruptedException();
		}
	}

	/**
	 * 检测锁是否过期
	 * @param value
	 * @return
	 */
	private boolean isTimeExpired(String value) {
		return Long.parseLong(value) < System.currentTimeMillis();
	}

	/**
	 * 检测是否超时
	 * @param start
	 * @param timeout
	 * @return
	 */
	private boolean isTimeout(long start, long timeout) {
		return start + timeout > System.currentTimeMillis();
	}

	/**
	 * 释放锁
	 */
	private void doUnlock() {
		Jedis jedis = getJedis();
		jedis.del(lockKey);
	}

	private Jedis getJedis(){
		if(jedis == null){
			jedis = new Jedis(host, port);
		}
		return jedis;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setLockKey(String lockKey) {
		this.lockKey = lockKey;
	}

	public void setLockExpires(long lockExpires) {
		this.lockExpires = lockExpires;
	}

}
