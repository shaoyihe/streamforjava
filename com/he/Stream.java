package com.he;

import java.lang.reflect.Method;

/** stream.js for java
 * @author he
 * @version 0.1 beta
 */
public class Stream {
	
//	public static void main(String[] args) throws Exception {
//		Stream stream=Stream.make(12,13,14,15,16,18);
//		Stream stream2=Stream.make(12,13,14,11,17,18);
//		System.err.println(stream.member(19));
//	}
	
	private static class StreamMethod{
		private Method method=null;
		private Object args=null;
		private Object subject=null;
		public StreamMethod(Object subject,Method method, Object args) {
			this.method = method;
			this.args = args;
			this.subject = subject;
		}
		public StreamMethod(Method method, Object args) {
			this(null, method, args);
		}
		
		public Stream invoke() throws Exception{
			return (Stream) method.invoke(subject, args );
		}
	}
	
	private Integer head=null;
	private StreamMethod tailPromise=null;
	private static Class<Stream> STREAM_CLAZZ=Stream.class;
	
	private Stream(int head, StreamMethod tailPromise) {
		this.head = head;
		this.tailPromise = tailPromise;
	}

	private Stream() {
	}
	
	public boolean isEmpty(){
		return head==null;
	}
	
	public int head() throws Exception{
		if(isEmpty()){
			throw new Exception("没有值啦");
		}
		return getHead();
	}
	
	public int item(int n) throws Exception{
		Stream cur=this;
		
		for (--n;n >0;--n) {
			if(cur.isEmpty()){
				throw new Exception("元素不存在");
			}
			cur=cur.getTailPromise().invoke();
		}
		
		return cur.getHead();
	}
	
	public Stream drop(int n) throws Exception{
		Stream cur=this;
		
		for (;n >0;--n) {
			if(cur.isEmpty()){
				return new Stream();
			}
			cur=cur.getTailPromise().invoke();
		}
		return cur;
	}
	
	public Stream tail() throws Exception{
		if(isEmpty()){
			return new Stream();
		}
		return getTailPromise().invoke();
	}
	
	
	public void print() throws Exception{
		Stream cur=this;
		while(!cur.isEmpty()){
			System.err.println(cur.getHead());
			cur=cur.getTailPromise().invoke();
		}
	}
	
	public Stream take(int n) throws Exception{
		if(n<=0||isEmpty()){
			return new Stream();
		}
		Stream tail=this.getTailPromise().invoke();
		return new Stream(this.getHead(),new StreamMethod(tail,tail.getClass().getMethod("take",int.class),--n ));
	}
	
	public Stream append(Stream stream) throws Exception{
		if(isEmpty()){
			return stream;
		}
		Stream tail=this.getTailPromise().invoke();
		return new Stream(this.getHead(),new StreamMethod(tail,tail.getClass().getMethod("append",STREAM_CLAZZ),stream ));
	}
	
	public int length() throws Exception{
		int len=0;
		
		Stream cur=this;
		while(!cur.isEmpty()){
			++len;
			cur=cur.getTailPromise().invoke();
		}
		return len;
	}
	
	public boolean member(int m) throws Exception{
		Stream cur=this;
		while(!cur.isEmpty()){
			if(cur.getHead()==m){
				return true;
			}
			cur=cur.getTailPromise().invoke();
		}
		return false;
	}
	
	
	public static Stream make(Integer...args ) throws Exception{
		if(args.length==0){
			return new Stream();
		}
		return new Stream(args[0],new StreamMethod( STREAM_CLAZZ.getMethod("make", Integer[].class),getArrayTail(args) ));
	}
	
	private static Integer[] getArrayTail(Integer[] orgi){
		Integer [] tail=new Integer[orgi.length-1];
		for(int i=1;i<orgi.length;++i){
			tail[i-1]=orgi[i];
		}
		return tail;
	}

	private Integer getHead() {
		return head;
	}

	private StreamMethod getTailPromise() {
		return tailPromise;
	}
	
}
