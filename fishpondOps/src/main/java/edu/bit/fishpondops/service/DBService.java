package edu.bit.fishpondops.service;

import java.io.IOException;
import java.sql.*;

/**
 * 测试数据库最大连接数
 */
public class DBService {

    private static volatile int count = 0;
    private static Connection[] conn = new Connection[1000];
    private static Statement[] stmt = new Statement[1000];
    private static ResultSet[] rs = new ResultSet[1000];

    static class Connect extends Thread {
        @Override
        public void run() {
            int tempCount = count;
            if(conn[tempCount] != null) return;
            try {
                conn[tempCount] = DriverManager.getConnection("jdbc:postgresql://124.70.67.6:26000/fishpond?useAffectedRows=true", "mhn", "fpDbMhn#");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                stmt[tempCount] = conn[tempCount].createStatement();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try {
                rs[tempCount] = stmt[tempCount].executeQuery(" SELECT * FROM fishpond.user ");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            System.out.print(tempCount + " connect\n");
        }
    }

    /**
     * @param num 要并发的数据库连接数
     */
    public static void testDBConnect(int num) {
        Connect[] connects = new Connect[num];
        for (count = 0; count < num; count++) {
            connects[count] = new Connect();
        }

        try {
            Class.forName("org.postgresql.Driver").newInstance();
            for (count = 0; count < 1000; count++) {
                connects[count].run();
            }
        } catch (InstantiationException ex2) {
            System.out.println("ex:" + ex2.toString());
        } catch (ClassNotFoundException ex3) {
            System.out.println("ex:" + ex3.toString());
        } catch (IllegalAccessException ex4) {
            System.out.println("ex:" + ex4.toString());
        } finally {
            try {
                System.out.println("System has opened  " + count-- + "  openGauss connections.\n Press Enter key to close the connections ");
                System.in.read();
                System.out.println("Close the Connections: \n");
                for (; count >= 0; count--) {
                    rs[count].close();
                    stmt[count].close();
                    conn[count].close();
                    System.out.print(count + " disconnect\n");
                }
            } catch (SQLException | IOException ex) {
                System.out.println("Close connection exception: " + ex.toString());
            }
        } // end the first "try"
    }
}
