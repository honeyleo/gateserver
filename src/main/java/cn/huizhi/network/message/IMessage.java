package cn.huizhi.network.message;

import com.google.protobuf.Message;

/**
 * @copyright SHENZHEN RONG WANG HUI ZHI TECHNOLOGY CORP
 * @author Lyon.liao
 * 创建时间：2015年2月13日
 * 类说明：
 * 
 * 最后修改时间：2015年2月13日
 * 修改内容： 新建此类
 */
public interface IMessage {

	/**
	 * 包大小
	 * @return
	 */
	int size();
	/**
	 * 指令
	 * @return
	 */
	int cmd();
	/**
	 * 玩家ID
	 * @return
	 */
	int pid();
	/**
	 * 包体
	 * @param clazz
	 * @return
	 */
	<T> T parseBody(Class<? extends  Message> clazz);
}
