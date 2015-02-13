package cn.huizhi.network.message;

import cn.huizhi.message.PBMessagePro.PBMessage;

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
public class TransferMessage implements IMessage {

	public TransferMessage(PBMessage pbMessage) {
		
	}
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override
	public int cmd() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override
	public int pid() {
		// TODO Auto-generated method stub
		return 0;
	}

	
	@Override
	public <T> T parseBody(Class<? extends Message> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

}
