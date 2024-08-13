package lib;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;

public class ConnectionPool {
    private String url;
    private String user;
    private String password;
    private int maxSize;
    private Queue<Connection> connectionPool;

    private static ConnectionPool instance = null;

    private ConnectionPool(String url, String user, String password,int maxSize){
        this.url = url;
        this.user = user;
        this.password = password;
        this.maxSize = maxSize;
        this.connectionPool = new LinkedList<>();
        initPool();
    }
    //싱글턴,멀티 환경인 경우 syn키워드로 한놈씩 접근하게 해야댐
    public static synchronized ConnectionPool getInstance(){
        if(instance == null){
            instance = new ConnectionPool("","","",5);
        }
        return instance;
    }
    //초기화
    private void initPool(){
        try{
            for(int i=0;i<maxSize;++i){
                connectionPool.add(createConnection());
            }
        }catch(SQLException e){
            System.err.println("Connection 생성중 오류"+e.getMessage());
        }
    }
    private Connection createConnection() throws SQLException{
        return DriverManager.getConnection(url,user,password);
    }
    //커넥션 객체 획득
    public synchronized Connection getConnection(long timeout) throws SQLException, InterruptedException {
        long startTime = System.currentTimeMillis();

        while(connectionPool.isEmpty()){//커넥션 풀이 빈경우
            long elapsedTime = System.currentTimeMillis() - startTime;//경과된 시간..
            long waitTime = timeout - elapsedTime;
            if(waitTime <= 0){
                throw new SQLException("커넥션 획득을 기다리는 시간을 초과했습니다.");
            }
            //연결이 반환 될 때까지 기다린다.
            //wait()메서드는 다른 스레드가 notify()를 호출하거나 waitTime동안 대기
            wait(waitTime);
        }

        //poll()은 비어있으면 null을 반환한다.
        return connectionPool.poll();
    }
    //커넥션 객체 반환
    public synchronized void releaseConnection(Connection connection){
        connectionPool.offer(connection);
        notify();//다른 스레드에게 연결이 반환됨을 알려줌
    }
    //커넥션 종료
    public synchronized void closePool() throws SQLException {
        for(Connection connection:connectionPool){
            connection.close();
        }
        connectionPool.clear();
    }
}

