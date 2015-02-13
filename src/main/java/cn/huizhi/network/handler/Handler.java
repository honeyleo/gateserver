package cn.huizhi.network.handler;

import cn.huizhi.network.message.IMessage;

/**
 * @copyright SHENZHEN RONG WANG HUI ZHI TECHNOLOGY CORP
 * @author Lyon.liao
 * 创建时间：2015年2月13日
 * 类说明：
 * 
 * 最后修改时间：2015年2月13日
 * 修改内容： 新建此类
 */
public interface Handler {

	/**
	 * 处理方法
	 * @param message
	 * @throws Throwable
	 */
	void handle(IMessage message) throws Throwable;
}
