////////////////////////////////////////////////////////////////////
//                            _ooOoo_                             //
//                           o8888888o                            //    
//                           88" . "88                            //    
//                           (| ^_^ |)                            //    
//                           O\  =  /O                            //
//                        ____/`---'\____                         //                        
//                      .'  \\|     |//  `.                       //
//                     /  \\|||  :  |||//  \                      //    
//                    /  _||||| -:- |||||-  \                     //
//                    |   | \\\  -  /// |   |                     //
//                    | \_|  ''\---/''  |   |                     //        
//                    \  .-\__  `-`  ___/-. /                     //        
//                  ___`. .'  /--.--\  `. . ___                   //    
//                ."" '<  `.___\_<|>_/___.'  >'"".                //
//              | | :  `- \`.;`\ _ /`;.`/ - ` : | |               //    
//              \  \ `-.   \_ __\ /__ _/   .-` /  /               //
//        ========`-.____`-.___\_____/___.-`____.-'========       //    
//                             `=---='                            //
//        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^      //         
//                       佛祖镇楼                  BUG辟易						  //
//          	佛曰:          									  //
//                  	写字楼里写字间，写字间里程序员；                  				  //
//                  	程序人员写程序，又拿程序换酒钱。						  //
//                  	酒醒只在网上坐，酒醉还来网下眠；						  //
//                  	酒醉酒醒日复日，网上网下年复年。                                                                            //
//                  	但愿老死电脑间，不愿鞠躬老板前；                                                                            //
//                  	奔驰宝马贵者趣，公交自行程序员。                                                                            //
//                  	别人笑我忒疯癫，我笑自己命太贱；                                                                            //
//                  	不见满街漂亮妹，哪个归得程序员？                                                                            //
//                                                                //
////////////////////////////////////////////////////////////////////
package cn.huizhi.network.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import cn.huizhi.game.Player;
import cn.huizhi.game.WorldManager;
import cn.huizhi.message.PBMessagePro.PBMessage;
import cn.huizhi.message.player.LoginByPidReqPro.LoginByPidReq;
import cn.huizhi.network.client.ServerClientMgr;
import cn.huizhi.network.message.Request;
import cn.huizhi.network.message.Response;
import cn.huizhi.utilities.ErrorCode;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * @copyright SHENZHEN RONG WANG HUI ZHI TECHNOLOGY CORP
 * @author Lyon.liao
 * 创建时间：2014年10月8日
 * 类说明：业务处理类
 * 
 * 最后修改时间：2014年10月8日
 * 修改内容： 新建此类
 *************************************************************
 *                                    .. .vr       
 *                                qBMBBBMBMY     
 *                              8BBBBBOBMBMv    
 *                            iMBMM5vOY:BMBBv        
 *            .r,             OBM;   .: rBBBBBY     
 *            vUL             7BB   .;7. LBMMBBM.   
 *           .@Wwz.           :uvir .i:.iLMOMOBM..  
 *            vv::r;             iY. ...rv,@arqiao. 
 *             Li. i:             v:.::::7vOBBMBL.. 
 *             ,i7: vSUi,         :M7.:.,:u08OP. .  
 *               .N2k5u1ju7,..     BMGiiL7   ,i,i.  
 *                :rLjFYjvjLY7r::.  ;v  vr... rE8q;.:,, 
 *               751jSLXPFu5uU@guohezou.,1vjY2E8@Yizero.    
 *               BB:FMu rkM8Eq0PFjF15FZ0Xu15F25uuLuu25Gi.   
 *             ivSvvXL    :v58ZOGZXF2UUkFSFkU1u125uUJUUZ,   
 *           :@kevensun.      ,iY20GOXSUXkSuS2F5XXkUX5SEv.  
 *       .:i0BMBMBBOOBMUi;,        ,;8PkFP5NkPXkFqPEqqkZu.  
 *     .rqMqBBMOMMBMBBBM .           @Mars.KDIDS11kFSU5q5   
 *   .7BBOi1L1MM8BBBOMBB..,          8kqS52XkkU1Uqkk1kUEJ   
 *   .;MBZ;iiMBMBMMOBBBu ,           1OkS1F1X5kPP112F51kU   
 *     .rPY  OMBMBBBMBB2 ,.          rME5SSSFk1XPqFNkSUPZ,.
 *            ;;JuBML::r:.:.,,        SZPX0SXSP5kXGNP15UBr.
 *                L,    :@huhao.      :MNZqNXqSqXk2E0PSXPE .
 *            viLBX.,,v8Bj. i:r7:,     2Zkqq0XXSNN0NOXXSXOU 
 *          :r2. rMBGBMGi .7Y, 1i::i   vO0PMNNSXXEqP@Secbone.
 *          .i1r. .jkY,    vE. iY....  20Fq0q5X5F1S2F22uuv1M;
 *
 ***************************************************************
 */
public class MessageHandler extends SimpleChannelInboundHandler<Request>{

	/**
	 * 玩家
	 */
	protected volatile Player player;
	
	/* (non-Javadoc)
	 * @see io.netty.channel.SimpleChannelInboundHandler#channelRead0(io.netty.channel.ChannelHandlerContext, java.lang.Object)
	 */
	@Override
	protected void channelRead0(ChannelHandlerContext ctx, Request msg)
			throws Exception {
		final int cmd = msg.getCmd();
		switch (cmd) {
		case 0x0113:
			processLogin(ctx, msg);
			break;

		default:
			if(player == null) {
				Response response = Response.fail(ErrorCode.NOT_LOGIN);
				ctx.channel().write(response).addListener(ChannelFutureListener.CLOSE);
			} else {
				processMessage(ctx, msg);
			}
			break;
		}
	}
	
	private void processLogin(ChannelHandlerContext ctx, Request msg) {
		try {
			LoginByPidReq loginByPidReq = LoginByPidReq.parseFrom(msg.getBytes());
			player = new Player();
			player.id = loginByPidReq.getPid();
			player.channel = ctx.channel();
			WorldManager.add(player);
		} catch (InvalidProtocolBufferException e) {
			e.printStackTrace();
		}
	}
	
	private void processMessage(ChannelHandlerContext ctx, Request msg) {
		switch(msg.getCmd()) {
		case 0x0016:
			ctx.write(Response.PONG);
			break;
		default:
			PBMessage.Builder builder = PBMessage.newBuilder();
			builder.setCmd(msg.getCmd()).setSessionId(player.id).setData(ByteString.copyFrom(msg.getBytes()));
			ServerClientMgr.getGameServerClient().send(builder.build());
			break;
		}
	}

}
