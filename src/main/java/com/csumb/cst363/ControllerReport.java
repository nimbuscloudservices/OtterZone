package com.csumb.cst363;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.sql.*;


@Controller public class ControllerReport {

   @Autowired
   private JdbcTemplate jdbcTemplate;

   @GetMapping("/report/generate")
   public String newReport(Model model) {
      // return blank form for generating a new report
      model.addAttribute("Report", new Report());
      return "report_generate";
   }

   /*
    * Process doctor registration.
    */
   @PostMapping("/report/generate")
   public String createReport(Report report, Model model) {

      try (Connection con = getConnection();) {

         String query = "SELECT doctorName, drugName, SUM(quantity) as " +
                 "Quantity from Prescription, Pharmacy_has_Prescription WHERE" +
                 " Prescription.rxid = Pharmacy_has_Prescription" +
                 ".Prescription_rxid AND drugName = ? AND " +
                 "Pharmacy_has_Prescription.dateFilled between ? and ?";
         PreparedStatement ps = con.prepareStatement(query);
         ps.setString(1, "%" + report.getDrug_trade_name() + "%");
         ps.setDate(2, (Date) report.getStart_date());
         ps.setDate(3, (Date) report.getEnd_date());

         ResultSet rs = ps.executeQuery();

        if(!rs.next())
        {
           model.addAttribute("message", "No Results Found.");
           model.addAttribute("report", report);
           return "report_generate";
        }
         while(rs.next())
        {

        }

         // display message and patient information
         model.addAttribute("message", "Report Generation successful.");
         model.addAttribute("Results Found!", report);
         return "report_show";

      } catch (SQLException e) {
         model.addAttribute("message", "SQL Error." + e.getMessage());
         model.addAttribute("report", report);
         return "report_generate";
      }
   }

   /*
    * return JDBC Connection using jdbcTemplate in Spring Server
    */

   private Connection getConnection() throws SQLException {
      Connection conn = jdbcTemplate.getDataSource()
              .getConnection();
      return conn;
   }

}
