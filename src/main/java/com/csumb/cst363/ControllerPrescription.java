package com.csumb.cst363;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;


@Controller public class ControllerPrescription
{

   @Autowired private JdbcTemplate jdbcTemplate;

   /*
    * Doctor requests form to create new prescription.
    */
   @GetMapping("/prescription/new") public String newPrescripton(Model model)
   {
      model.addAttribute("prescription", new Prescription());
      return "prescription_create";
   }

   /*
    * Patient requests form to search for prescription.
    */
   @GetMapping("/prescription/fill") public String getfillForm(Model model)
   {
      model.addAttribute("prescription", new Prescription());
      return "prescription_fill";
   }

   /*
    * Process the new prescription form.
    * 1.  Validate that Doctor SSN exists and matches Doctor Name.
    * 2.  Validate that Patient SSN exists and matches Patient Name.
    * 3.  Validate that Drug name exists.
    * 4.  Insert new prescription.
    * 5.  If error, return error message and the prescription form
    * 6.  Otherwise, return the prescription with the rxid number that was
    * generated by the database.
    */
   @PostMapping("/prescription") public String newPrescription(Prescription p,
         Model model)
   {

      // TODO
      try (Connection con = getConnection();)
      {
         if(!isValidDoctor( p.getDoctor_ssn(), p.getDoctorName()))
         {
            model.addAttribute("message", "Error. Invalid Doctor Information.");
            model.addAttribute("prescription", p);
            return "prescription_create";
         }
         if(!isValidPatient( p.getPatient_ssn(), p.getPatientName()))
         {
            model.addAttribute("message", "Error. Invalid Patient Information.");
            model.addAttribute("prescription", p);
            return "prescription_create";
         }

         if(!isValidDrug(p.getDrugName()))
         {
            model.addAttribute("message", "Error. Drug not found.");
            model.addAttribute("prescription", p);
            return "prescription_create";
         }


         PreparedStatement ps = con.prepareStatement(
               "insert into Prescription(doctor_ssn, doctorName, patient_ssn," +
               " patientName, drugName, quantity) " + "values (?,?,?,?,?,?)",
               Statement.RETURN_GENERATED_KEYS);

         ps.setString(1, p.getDoctor_ssn());
         ps.setString(2, p.getDoctorName());
         ps.setString(3, p.getPatient_ssn());
         ps.setString(4, p.getPatientName());
         ps.setString(5, p.getDrugName());
         ps.setString(6, String.valueOf(p.getQuantity()));
         int rc = ps.executeUpdate();
         ResultSet pres = ps.getGeneratedKeys();
         if (pres.next())
            p.setRxid(String.valueOf(pres.getInt(1)));
         if (rc == 1)
         {
            p.setRxid(String.valueOf(pres.getInt(1)));
            model.addAttribute("message", "Update successful");
            model.addAttribute("prescription", p);
            return "prescription_show";
         }
         else
         {
            model.addAttribute("message", "Error. Update was not successful");
            model.addAttribute("prescription", p);
            return "prescription_create";
         }

      } catch (SQLException e)
      {
         model.addAttribute("message", "SQL Error: " + e.getMessage());
         model.addAttribute("prescription", p);
         return "prescription_show";
      }
   }

   /*
    * Process the prescription fill request from a patient.
    * 1.  Validate that Prescription p contains rxid, pharmacy name and
    * pharmacy address
    *     and uniquely identify a prescription and a pharmacy.
    * 2.  update prescription with pharmacyid, name and address.
    * 3.  update prescription with today's date.
    * 4.  Display updated prescription
    * 5.  or if there is an error show the form with an error message.
    */
   @PostMapping("/prescription/fill")
   public String processFillForm(Prescription p, Model model)
   {

      try (Connection con = getConnection();)
      {
         // checks that all fields are filled
         if (p.getRxid()
                   .equals("") && p.getPharmacyName()
                   .equals("") && p.getPharmacyAddress()
                   .equals(""))
         {
            model.addAttribute("message", "Please fill all fields");
            model.addAttribute("prescription", p);
            return "prescription_fill";
         }

         //Checks to validate prescription rxid and patient
         String query = "SELECT rxid, patientName " +
                        "from Prescription where rxid = ? AND " +
                        "patientName = ?";
         PreparedStatement ps = con.prepareStatement(query);
         ps.setString(1, p.getRxid());
         ps.setString(2, p.getPatientName());
         ResultSet rs = ps.executeQuery();
         if (!rs.next())
         {
            model.addAttribute("message", "Prescription Not Found");
            model.addAttribute("prescription", p);
            return "prescription_fill";
         }
         //retrieve pharmacy id based on name and address
         PreparedStatement pharmID = con.prepareStatement("SELECT pharmacyID," +
                                                          " name " +
                                                          "from Pharmacy, " +
                                                          "Pharmacy_has_Prescription" +
                                                          " " +
                                                          "where " +
                                                          "Pharmacy_has_Prescription.Pharmacy_pharmacyID = Pharmacy.pharmacyID AND" +
                                                          " name = ? AND" +
                                                          "  pharmacyAddress " +
                                                          "LIKE ?");
         pharmID.setString(1, p.getPharmacyName());
         pharmID.setString(2, "%" + p.getPharmacyAddress() + "%");

         //Update prescription
         PreparedStatement fill = con.prepareStatement("INSERT INTO " +
                                                       "Pharmacy_has_Prescription(Prescription_rxid, pharmacyAddress, pharmacyPhone, pharmacyName, dateFilled, cost) VALUES (?, ?, ?, ?, ?, ?)");

      } catch (SQLException e)
      {
         e.printStackTrace();
      }
      return "prescription_show";
   }

   /*
    * return JDBC Connection using jdbcTemplate in Spring Server
    */

   private Connection getConnection() throws SQLException
   {
      Connection conn = jdbcTemplate.getDataSource()
            .getConnection();
      return conn;
   }

   /**
    * Helper method that validates doctor information
    * @param doctor_ssn doctor's ssn
    * @param name doctors name
    * @return true if doctor information matches DB, false if not
    */
   private boolean isValidDoctor(String doctor_ssn, String name)
   {
      try(Connection con = getConnection();)
      {
         PreparedStatement ps = con.prepareStatement("SELECT name, ssn from doctor where name = ? and ssn = ?");
         ps.setString(1, name);
         ps.setInt(2, Integer.parseInt(doctor_ssn));
         ResultSet rs = ps.executeQuery();

         return rs.next();
      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }

   }
   /**
    * Helper method that validates Patient information
    * @param patient_ssn patient's ssn
    * @param name patient name
    * @return true if patient information matches DB, false if not
    */
   private boolean isValidPatient(String patient_ssn, String name)
   {
      try(Connection con = getConnection();)
      {
         PreparedStatement ps = con.prepareStatement("SELECT name, ssn from patient where name = ? and ssn = ?");
         ps.setString(1, name);
         ps.setInt(2, Integer.parseInt(patient_ssn));
         ResultSet rs = ps.executeQuery();

         return rs.next();
      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }

   }

   /**
    * validates drug
    * @param drug being prescribed
    * @return true if drug exists, false if not
    */
   private boolean isValidDrug(String drug)
   {
      try(Connection con = getConnection();)
      {
         PreparedStatement ps = con.prepareStatement("SELECT trade_name from drug where trade_name LIKE ?");
         ps.setString(1, "%" + drug + "%");
         ResultSet rs = ps.executeQuery();
         return rs.next();
      } catch (SQLException e) {
         e.printStackTrace();
         return false;
      }
   }


}
