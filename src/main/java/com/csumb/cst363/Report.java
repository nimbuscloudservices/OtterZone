package com.csumb.cst363;

import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * An FDA government official is looking for the quantity of drugs
 * that each doctor has prescribed.
 * The report shows the doctor’s name and quantity prescribed.
 * Input is drug name (may be partial name) and a start and end date range.
 */
public class Report {

   private String doctor_name;
   private String drug_trade_name;
   private int quantity;
   @DateTimeFormat(pattern = "yyyy-MM-dd")
   private Date start_date;
   @DateTimeFormat(pattern = "yyyy-MM-dd")
   private Date end_date;

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

   public Date getStart_date() {
      return start_date;
   }

   public void setStart_date(Date start_date) {
      this.start_date = start_date;
   }

   public Date getEnd_date() {
      return end_date;
   }

   public void setEnd_date(Date end_date) {
      this.end_date = end_date;
   }


}
