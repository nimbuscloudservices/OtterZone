package com.csumb.cst363;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class FDAReport {
   @Autowired
   private JdbcTemplate jdbcTemplate;
   private DateTimeFormatter dateFormatter = DateTimeFormatter.BASIC_ISO_DATE;

   public static void main(String[] args) {

      Scanner in = new Scanner(System.in);
      String drug;
      String start_date = null;
      String end_date = null;
      Report FDA_report = new Report();

      try {
         //get user to input drug name
         System.out.println("Enter name of drug to create a report for: ");
         drug = in.next();
         FDA_report.setDrug_trade_name(drug);

         //user enters starting date for period
         System.out.println("Enter starting date for prescribed range" +
                 "(YYYY-MM-DD): ");
         start_date = in.next();

         //if invalid date enter loop till correct date entered
         if (!FDA_report.isValid(start_date)) {
            while (FDA_report.isValid(start_date)) {
               System.out.println("Invalid start date, enter date in " +
                       "YYYY-MM-DD Format!");
               start_date = in.next();
            }
         }
         FDA_report.setStart_date(LocalDate.parse(start_date));

         System.out.println("Enter ending date for prescribed range" +
                 "(YYYY-MM-DD): ");
         end_date = in.next();

         if (!FDA_report.isValid(end_date)) {
            while (FDA_report.isValid(end_date)) {
               System.out.println("Invalid start date, enter date in " +
                       "YYYY-MM-DD Format!");
               end_date = in.next();
            }
         }
         FDA_report.setEnd_date(LocalDate.parse(end_date));


      } catch (Exception e) {
         e.printStackTrace();
      }
      assert start_date != null;
      FDA_report.setStart_date(LocalDate.parse(start_date));
      try {
         FDA_report.queryDB();

      } catch (SQLException ex) {
         ex.printStackTrace();
      }

   }

}

