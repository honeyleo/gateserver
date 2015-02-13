package cn.huizhi.disruptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

public class MultiThreadDisruptor implements cn.huizhi.disruptor.Disruptor {

	final static Logger LOG = LoggerFactory.getLogger(MultiThreadDisruptor.class.getName());
	private RingBuffer<Event> buffer = null;
	private ExecutorService executor = null;
	private volatile int INDEX = 0;
	Disruptor<Event> disruptor = null;
	
	public MultiThreadDisruptor(final String name, final int thread) {
		this(name, thread, 1 << 10);
	}
	
	public MultiThreadDisruptor(final String name, final int thread, final int bufferSize) {
		this.INDEX = thread;
		executor = Executors.newFixedThreadPool(thread, new ThreadFactory() {
			
			@Override
			public Thread newThread(Runnable r) {
				String threadName = name;
				if(thread > 1) {
					threadName = threadName + INDEX --;
				}
				Thread thread = new Thread(r, threadName);
				thread.setDaemon(true);
				return thread;
			}
		});
		
		disruptor = new Disruptor<Event>(new EventFactory(), bufferSize , executor);
		
		EventWorkHandler[] dbWorkHandlers = new EventWorkHandler[thread];
		for(int i = 0; i < dbWorkHandlers.length; i ++) {
			dbWorkHandlers[i] = new EventWorkHandler();
		}
		
		disruptor.handleEventsWithWorkerPool(dbWorkHandlers);
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
		MultiThreadDisruptor disruptor = new MultiThreadDisruptor("DisruptorEvent-thread-", 2);
		for(int i = 0; i < 10000; i ++) {
			final int num = i;
			disruptor.publish(new Runnable() {
				
				@Override
				public void run() {
					LOG.info("task-" + num);
				}
			});
		}
	}
}
