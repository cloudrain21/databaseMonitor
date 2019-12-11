package com.cloudrain21.dbmonitor;

import org.slf4j.MDC;

/*
 * logger 관련하여 실시간으로 변경할 사항들이 있을 때 사용하는 Class.
 * logger(slf4j) 관련한 대부분의 설정은 logback.xml 파일을 이용한다.
 */
public class LoggerManager {
    public static void initialize(String threadName) {
        /*
         * logger 를 통해 Thread 별로 로그파일을 다르게 하기 위함.
         * (이 프로그램에서는 Class 별로 logger 를 따로둘 필요는 없다.
         *  logger 는 DatabaseMon.class 에 대해 하나만 두고 대신 DB별(Thread별)로 파일을 나눈다.)
         * 
         * https://thinkwarelab.wordpress.com/2016/11/18/
         * https://www.mkyong.com/logging/logback-different-log-file-for-each-thread/
         */
        Thread.currentThread().setName(threadName);
        MDC.put("logFileName", Thread.currentThread().getName());
    }
    
    public static void remove() {
        MDC.remove("logFileName");
    }
}
