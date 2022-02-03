package com.csumb.cst363;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class FDAReport {
   public static void main(String[] args) {

      String url = "jdbc:mysql://http://ec2-13-56-197-130.us-west-1.compute.amazonaws.com:3306/OtterZone";
      String user = "root";
      String password = "ToInfinityAndBeyond";
      try (Connection con = DriverManager.getConnection(url, user, password); ) {
         con.setAutoCommit(false);
         String sql = "Select";



         con.commit();

      } catch (SQLException ex) {
         ex.printStackTrace();
      }

   }
}
