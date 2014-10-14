package cn.huizhi.utilities;

import java.io.IOException;
import java.io.OutputStream;

public class CheckSumStream extends OutputStream{

    private int sum;
    
    @Override
    public void write(int b) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(byte b[], int off, int len){
        for (int i = off + len; --i >= off; ){        	
            sum += b[i];
        }
    }
    
    public int getCheckSum(){
        return sum & 0xff;
    }
    
    public void clearSum(){
    	sum=0;
    }
}
