package cn.huizhi.disruptor;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

public class SingleThreadDisruptor implements cn.huizhi.disruptor.Disruptor {

	final static Logger LOG = LoggerFactory.getLogger(SingleThreadDisruptor.class.getName());
	private RingBuffer<Event> buffer = null;
	private ExecutorService executor = null;
	Disruptor<Event> disruptor = null;
	
	public SingleThreadDisruptor(final String name) {
		this(name, 1 << 10);
	}
	
	public SingleThreadDisruptor(final String name, final int bufferSize) {
		executor = Executors.newSingleThreadExecutor(new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				Thread thread = new Thread(r, name);
				thread.setDaemon(true);
				return thread;
			}
		});
		
		disruptor = new Disruptor<Event>(new EventFactory(), bufferSize , executor, 
				ProducerType.SINGLE, new BlockingWaitStrategy());
		
		EventWorkHandler[] eventWorkHandlers = new EventWorkHandler[1];
		eventWorkHandlers[0] = new EventWorkHandler();
		
		disruptor.handleEventsWithWorkerPool(eventWorkHandlers);
		buffer = disruptor.getRingBuffer();
		disruptor.start();
	}
	public void publish(final Runnable event) {
		if(event !=null) {
			long next = buffer.next();
			try {
				Event tmp = buffer.get(next);
				tmp.setEvent(event);
			} finally {
				buffer.publish(next);
			}
		}
	}
	
	public void shutdown() {
		disruptor.shutdown();
		executor.shutdown();
	}
	
	public void awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		executor.awaitTermination(timeout, unit);
	}
	
	public static void main(String args[]) {
		final CountDownLatch count = new CountDownLatch(10000);
		SingleThreadDisruptor disruptor = new SingleThreadDisruptor("Disruptor-Thread");
		long start = System.currentTimeMillis();
		for(int i = 0; i < 10000; i ++) {
			final int num = i;
			disruptor.publish(new Runnable() {
				
				@Override
				public void run() {
					LOG.info("task-" + num);
					count.countDown();
				}
			});
		}
		try {
			count.await();
			LOG.info("count={},time={}", count.getCount(), (System.currentTimeMillis() - start));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
