export MON_PATH=./

# 먼저 외부 lib들을 이용하여 dbmonitor의 모든 class들을 컴파일 후 bin으로
javac -d bin -classpath 'external_libs/*' src/com/cloudrain21/dbmonitor/*.java

# db monitor 실행
 java -Dfile.encoding=UTF-8 \
      -classpath  \
      $MON_PATH/bin:$MON_PATH/external_libs/mysql-connector-java-8.0.18.jar:$MON_PATH/external_libs/slf4j-api-1.7.29.jar:$MON_PATH/external_libs/logback-core-1.2.3.jar:$MON_PATH/external_libs/logback-classic-1.2.3.jar  com.cloudrain21.dbmonitor.DatabaseMon conf/dbmon.xml
