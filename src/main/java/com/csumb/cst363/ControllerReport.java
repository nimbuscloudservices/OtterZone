package com.csumb.cst363;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.context.LazyContextVariable;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


@Controller
public class ControllerReport {

   @Autowired
   private JdbcTemplate jdbcTemplate;

   @GetMapping("/report/generate")
   public String newReport(Model model) {
      // return blank form for generating a new report
      model.addAttribute("Report", new Report());
      return "report_generate";
   }

   /*
    * Process report request.
    */
   @PostMapping("/report/generate")
   public String createReport(Report report, Model model) {

      try (Connection con = getConnection();) {

         String query = "SELECT doctorName, drugName, SUM(quantity) as " +
                 "Quantity from Prescription, Pharmacy_has_Prescription WHERE" +
                 " Prescription.rxid = Pharmacy_has_Prescription" +
                 ".Prescription_rxid AND drugName = ? AND " +
                 "Pharmacy_has_Prescription.fillDate between ? and ?";
         PreparedStatement ps = con.prepareStatement(query);
         ps.setString(1, "%" + report.getDrug_trade_name() + "%");
         ps.setDate(2, Date.valueOf(report.getStart_date()));
         ps.setDate(3, Date.valueOf(report.getEnd_date()));

         ResultSet rs = ps.executeQuery();

         if (!rs.next()) {
            model.addAttribute("message", "No Results Found.");
            model.addAttribute("report", report);
            return "report_generate";
         }
         ResultSetMetaData metaData = rs.getMetaData();
         int count = metaData.getColumnCount();
         List<String> tableHeader = new ArrayList<>();
         for (int i = 0; i < count; i++) {
            tableHeader.add(metaData.getColumnName(i));
         }
         model.addAttribute("tableHeader", tableHeader);


         List<List<String>> tableRow = new ArrayList<>();
         model.addAttribute("tableContent",
                 new LazyContextVariable<Iterable<List<String>>>() {
            @Override
            protected Iterable<List<String>> loadValue() {
               return () -> new Iterator<>() {
                  private boolean loadedRow;

                  {
                     try {
                        loadedRow = rs.next();
                     } catch (SQLException exception) {
                        loadedRow = false;
                        exception.printStackTrace();
                     }
                  }

                  @Override
                  public boolean hasNext() {
                     return loadedRow;
                  }

                  @Override
                  public List<String> next() {
                     if (!loadedRow) {
                        return new ArrayList<>();
                     }
                     try {
                        final List<String> list = new ArrayList<>(count);
                        for (int i = 1; i <= count; i++) {
                           list.add(rs.getString(i));
                        }
                        loadedRow = rs.next();
                        return list;
                     } catch (SQLException e) {
                        e.printStackTrace();
                        return new ArrayList<>();
                     }
                  }
               };
            }
         });

         if (!rs.next()) {
            model.addAttribute("message", "No Results Found.");
            model.addAttribute("report", report);
            return "report_generate";
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
