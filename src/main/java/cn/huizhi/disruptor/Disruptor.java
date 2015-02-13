package cn.huizhi.disruptor;

import java.util.concurrent.TimeUnit;

/**
 * @copyright SHENZHEN RONG WANG HUI ZHI TECHNOLOGY CORP
 * @author Lyon.liao
 * 创建时间：2015年2月13日
 * 类说明：
 * 
 * 最后修改时间：2015年2月13日
 * 修改内容： 新建此类
 */
public interface Disruptor {
	/**
	 * 发布任务
	 * @param event
	 */
	void publish(final Runnable event);
	/**
	 * 停止Disruptor
	 */
	void shutdown();
	/**
	 * 等待中断
	 * @param timeout
	 * @param unit
	 * @throws InterruptedException
	 */
	void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException;
}
