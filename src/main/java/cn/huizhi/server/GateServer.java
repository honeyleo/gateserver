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
package cn.huizhi.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.huizhi.network.codec.MessageDecoder;
import cn.huizhi.network.codec.MessageEncoder;
import cn.huizhi.network.handler.MessageHandler;
import cn.huizhi.network.handler.SimpleChannelOutboundHandler;

/**
 * @copyright SHENZHEN RONG WANG HUI ZHI TECHNOLOGY CORP
 * @author Lyon.liao
 * 创建时间：2014年10月9日
 * 类说明：
 * 	PBMessage：
	id：玩家ID
	sid：会话ID
	cmd：事件处理ID
	status：错误代码
	playerList：玩家ID列表
	data：protobuf实体字节
	
	
	客户端 ==》网关：
	1. 客户端请求：size|cmd|data
	2. 网关接到客户端请求，寻找cmd对应的handler处理
	3. 如果没有对应的handler，则封装PBMessage发送到游戏服务器
	
	网关 ==》游戏服
	1. 网关发送PBMessage数据给游戏服务器
	2. 游戏服务器接到网关服务器发过来的PBMessage，拿出cmd，寻找cmd对应的handler进行处理
	
	游戏服 ==》网关
	1. 游戏服handler处理完后，封装PBMessage返回给网关服务器
	2. 网关服务器解析出cmd，寻找是否有对应handler进行处理，没有则转换成size|cmd|errorCode|data数据写回玩家列表上玩家：List<Integer>
 * 最后修改时间：2014年10月9日
 * 修改内容： 新建此类
 */
public class GateServer {

	static ServerBootstrap serverBootstrap;

	private static Logger logger = LoggerFactory.getLogger(GateServer.class);
	
	public static void start(int port) {
		// Configure the server.
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup(2, new DefaultThreadFactory("GateServer"));
		
		try {
			serverBootstrap = new ServerBootstrap();
			
			serverBootstrap.group(bossGroup, workerGroup)
				.channel(NioServerSocketChannel.class)
				.option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.option(ChannelOption.SO_BACKLOG, 1024)
				.option(ChannelOption.TCP_NODELAY, true)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.SO_RCVBUF, 2048)
				.childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline()
							.addLast("decoder", new MessageDecoder())
							.addLast(new MessageHandler())
							.addLast("encoder", new MessageEncoder())
							.addLast(new SimpleChannelOutboundHandler())
							;
					}
				});
			serverBootstrap.bind(port).channel();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		logger.info("服务器启动成功，开始监听{} 端口...", port);
	}
	
	public static void shutdown() {
		
	}
	private static class ShutdownHook implements Runnable {

		@Override
		public void run() {
			try {
				// do shutdown procedure here.
				logger.info("正在优雅的停止服务器.....");
				GateServer.shutdown();
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				// any I/O procedure for destory?
			}
		}
	}
	
	public static void main(String[] args) {
		start(9000);
		Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownHook(),"shutdownHook"));
	}
}
