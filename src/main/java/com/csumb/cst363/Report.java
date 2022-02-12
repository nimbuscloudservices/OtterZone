package com.csumb.cst363;

import org.springframework.format.annotation.DateTimeFormat;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;


/**
 * An FDA government official is looking for the quantity of drugs
 * that each doctor has prescribed.
 * The report shows the doctor’s name and quantity prescribed.
 * Input is drug name (may be partial name) and a start and end date range.
 */
public class Report {

   private static final DateTimeFormatter DATE_TIME_FORMATTER =
           DateTimeFormatter.BASIC_ISO_DATE;
   private String doctor_name;
   private String drug_trade_name;
   private int quantity;
   @DateTimeFormat(pattern = "yyyy-MM-dd")
   private LocalDate start_date;
   @DateTimeFormat(pattern = "yyyy-MM-dd")
   private LocalDate end_date;
   private List<String> doc_namesCol;
   private List<String> drugsCol;
   private List<String> quantityCol;
   private List<List<String>> table;

   public Report() {
      doc_namesCol = new ArrayList<>();
      drugsCol = new ArrayList<>();
      quantityCol = new ArrayList<>();
      table = new ArrayList<>();
   }

   public List<List<String>> getTable() {
      return table;
   }

   public void setTable(List<List<String>> table) {
      this.table = table;
   }

   public List<String> getDoc_namesCol() {
      return doc_namesCol;
   }

   public void setDoc_namesCol(List<String> doc_namesCol) {
      this.doc_namesCol = doc_namesCol;
   }

   public List<String> getDrugsCol() {
      return drugsCol;
   }

   public void setDrugsCol(List<String> drugsCol) {
      this.drugsCol = drugsCol;
   }

   public List<String> getQuantityCol() {
      return quantityCol;
   }

   public void setQuantityCol(List<String> quantityCol) {
      this.quantityCol = quantityCol;
   }

   public String getDoctor_name() {
      return doctor_name;
   }

   public void setDoctor_name(String doctor_name) {
      this.doctor_name = doctor_name;
   }

   public String getDrug_trade_name() {
      return drug_trade_name;
   }

   public void setDrug_trade_name(String drug_trade_name) {
      this.drug_trade_name = drug_trade_name;
   }

   public int getQuantity() {
      return quantity;
   }

   public void setQuantity(int quantity) {
      this.quantity = quantity;
   }

   public LocalDate getStart_date() {
      return start_date;
   }

   public void setStart_date(LocalDate start_date) {
      this.start_date = start_date;
   }

   public LocalDate getEnd_date() {
      return end_date;
   }

   public void setEnd_date(LocalDate end_date) {
      this.end_date = end_date;
   }

   public boolean isValid(String dateStr) {
      try {
         LocalDate.parse(dateStr, DATE_TIME_FORMATTER);
      } catch (DateTimeParseException e) {
         return false;
      }
      return true;
   }

   public void queryDB() throws SQLException {
      ResultSet queryResult;
      try (Connection con = DriverManager.getConnection("jdbc:mysql://ec2-13" +
                      "-56-197-130.us-west-1.compute.amazonaws" +
                      ".com:3306/OtterZoneDB",
              "root", "ToInfinityAndBeyond");) {
         String query = "SELECT doctorName, drugName, SUM(Prescription" +
                 ".quantity) as Quantity FROM Prescription, " +
                 "Pharmacy_has_Prescription WHERE Prescription.rxid = " +
                 "Pharmacy_has_Prescription.Prescription_rxid AND " +
                 "Prescription_rxid AND drugName = ? AND " +
                 "Pharmacy_has_Prescription.fillDate BETWEEN ? AND ? GROUP BY" +
                 " drugName, doctorName";

         PreparedStatement ps = con.prepareStatement(query,
                 Statement.RETURN_GENERATED_KEYS);
         ps.setString(1, this.getDrug_trade_name());
         ps.setDate(2, Date.valueOf(this.getStart_date()));
         ps.setDate(3, Date.valueOf(this.getEnd_date()));
         queryResult = ps.executeQuery();

            System.out.print("Doctor Name");
            System.out.print("           ");
            System.out.print("Drug Name");
            System.out.print("           ");
            System.out.print("Quantity");
            System.out.println();
            while (queryResult.next()) {
               System.out.print(queryResult.getString("doctorName"));
               System.out.print("           ");
               System.out.print(queryResult.getString("drugName"));
               System.out.print("           ");
               System.out.print(queryResult.getInt("Quantity"));
               System.out.println();
            }

      }
   }
}
