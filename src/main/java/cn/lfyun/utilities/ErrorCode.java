package cn.lfyun.utilities;


public interface ErrorCode {

	/**
	 * 成功回执码
	 */
	public static final ErrorCode SUCCESS = new ErrorCode() {
		
		@Override
		public String getMsg() {
			return "OK";
		}
		
		@Override
		public int getCode() {
			return 1;
		}
		@Override
		public String toString() {
			return "{code=" + getCode() + ",msg=" + getMsg() + "}";
		}
	};
	
	/**
	 * 未登陆回执码
	 */
	public static final ErrorCode NOT_LOGIN = new ErrorCode() {
		
		@Override
		public String getMsg() {
			return "NOT_LOGIN";
		}
		
		@Override
		public int getCode() {
			return 2;
		}
		@Override
		public String toString() {
			return "{code=" + getCode() + ",msg=" + getMsg() + "}";
		}
	};
	
	/**
	 * 错误代码
	 * @return
	 */
	int getCode();
	
	/**
	 * 错误信息
	 * @return
	 */
	String getMsg();
	
}
