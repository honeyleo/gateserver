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
package cn.lfyun.network.codec;

import cn.lfyun.network.message.Request;
import cn.lfyun.utilities.CheckSumStream;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @copyright SHENZHEN RONG WANG HUI ZHI TECHNOLOGY CORP
 * @author Lyon.liao
 * 创建时间：2014年10月8日
 * 类说明：消息校验解码
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
public class MessageDecoder extends LengthFieldBasedFrameDecoder {

	/**
	 * 校验包
	 */
	private final CheckSumStream checkSumStream;
	private int msgOffset;
	
	private static final int OFFSET_MAX_LIMIT_TO_MOD = 7;
	
	/**
	 * @param byteOrder
	 * @param maxFrameLength
	 * @param lengthFieldOffset
	 * @param lengthFieldLength
	 * @param lengthAdjustment
	 * @param initialBytesToStrip
	 * @param failFast
	 */
	public MessageDecoder() {
		super(4096, 0, 2, 0, 0);
		checkSumStream = new CheckSumStream();
	}

	@Override
	protected ByteBuf extractFrame(ChannelHandlerContext ctx, ByteBuf buffer,
			int index, int length) {
		
		if(buffer.readableBytes() < 2){
            System.err.println("不够读取一个长度");
            ctx.close();
            return null;
	    }
		int size = buffer.readShort();
		
		if(buffer.readableBytes() < 1){
            System.err.println("不够读取一个校验和");
            ctx.close();
            return null;
	    }
		int checkSumByte = buffer.readUnsignedByte();
		
		try {
			checkSumStream.clearSum();
			buffer.getBytes(buffer.readerIndex(), checkSumStream,
			        buffer.readableBytes());
			if (checkSumByte != checkSumStream.getCheckSum()){
	            System.err.println("校验和错误, expected: " + checkSumStream.getCheckSum() + ", actual: " + checkSumByte);
	            ctx.close();
	            return null;
	        }
		} catch (Throwable e) {
			System.err.println(e.getMessage());
		}
		
		int offset = msgOffset++ & OFFSET_MAX_LIMIT_TO_MOD;
		
		if(buffer.readableBytes() < 1){
            System.err.println("不够读出一个偏移量");
            ctx.close();
            return null;
        }
        int bigOffset = buffer.readUnsignedByte();

        if (bigOffset != calculateVerificationBytes(msgOffset)){
            System.err.println("偏移量校验错误");
            ctx.close();
            return null;
        }

        if(buffer.readableBytes() < 2){
            System.err.println("不够读出一个cmd");
            ctx.close();
            return null;
        }
        int msgId = buffer.readUnsignedShort();

        int o = msgId >>> 13;

        msgId = msgId & 0x1fff;

        if (o != offset){
            System.err.println("wrong offset, msg " + msgId);
            ctx.close();
            return null;
        }
        
        byte[] bytes = new byte[size - 4];
        buffer.readBytes(bytes);
        Request request = Request.createRequest(size, msgId, bytes);
        ctx.fireChannelRead(request);
		return buffer;
	}
	
	private static int calculateVerificationBytes(int offset){
        int v = offset;
        v ^= v >> 8;
        v ^= v >> 4;
        v &= 0xff;
        return v;
    }

}
