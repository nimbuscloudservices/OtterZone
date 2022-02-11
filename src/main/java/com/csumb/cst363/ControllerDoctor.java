package com.csumb.cst363;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.util.Validate;

/*
 * Controller class for doctor registration and profile update.
 */
@Controller
public class ControllerDoctor {

   @Autowired
   private JdbcTemplate jdbcTemplate;

   /*
    * Request for new doctor registration form.
    */
   @GetMapping("/doctor/register")
   public String newDoctor(Model model) {
      // return blank form for new patient registration
      model.addAttribute("doctor", new Doctor());
      return "doctor_register";
   }

   /*
    * Process doctor registration.
    */
   @PostMapping("/doctor/register")
   public String createDoctor(Doctor doctor, Model model) {

      try (Connection con = getConnection();) {
         PreparedStatement ps = con.prepareStatement("insert into doctor" +
                                                     "(name, specialty, " +
                                                     "practice_since_year,  " +
                                                     "ssn ) values(?, ?, ?, ?)",
               Statement.RETURN_GENERATED_KEYS);
         ps.setString(1, doctor.getName());
         ps.setString(2, doctor.getSpecialty());

         ps.setString(4, doctor.getSsn());

         ValidateData validate = new ValidateData();

         if(!validate.validateSSN(doctor.getSsn()))
         {
            model.addAttribute("message", "Invalid SSN");
            model.addAttribute("doctor", doctor);
            return "doctor_register";
         }
         else if(!validate.validateName(doctor.getName()))
         {
            model.addAttribute("message", "Name cannot be non-alphabetic.");
            model.addAttribute("doctor", doctor);
            return "doctor_register";
         }
         else if(!validate.validateName(doctor.getSpecialty()))
         {
            model.addAttribute("message", "Specialty cannot be non-alphabetic.");
            model.addAttribute("doctor", doctor);
            return "doctor_register";
         }
         else if(!validate.validateDate(doctor.getPractice_since_year()))
         {
            model.addAttribute("message", "First Year in Practice must be formatted as: YYYYMMDD");
            model.addAttribute("doctor", doctor);
            return "doctor_register";
         }
         else
         {
            //Formats date for DB
            String year = String.valueOf(doctor.getPractice_since_year().charAt(0));
            year += String.valueOf(doctor.getPractice_since_year().charAt(1));
            year += String.valueOf(doctor.getPractice_since_year().charAt(2));
            year += String.valueOf(doctor.getPractice_since_year().charAt(3));

            String month = String.valueOf(doctor.getPractice_since_year().charAt(4));
            month += String.valueOf(doctor.getPractice_since_year().charAt(5));

            String day = String.valueOf(doctor.getPractice_since_year().charAt(6));
            day += String.valueOf(doctor.getPractice_since_year().charAt(7));

            doctor.setPractice_since_year(year + "-" + month + "-" + day);

            ps.setString(3, doctor.getPractice_since_year());
         }

         ps.executeUpdate();
         ResultSet rs = ps.getGeneratedKeys();
         if (rs.next()) doctor.setId((int)rs.getLong(1));

         // display message and patient information
         model.addAttribute("message", "Registration successful.");
         model.addAttribute("doctor", doctor);
         return "doctor_show";

      } catch (SQLException e) {
         model.addAttribute("message", "SQL Error."+e.getMessage());
         model.addAttribute("doctor", doctor);
         return "doctor_register";
      }
   }

   /*
    * Request form for doctor search.
    */
   @GetMapping("/doctor/get")
   public String getDoctor(Model model) {
      // return form to enter doctor id and name
      model.addAttribute("doctor", new Doctor());
      return "doctor_get";
   }

   /*
    * Search for doctor by id and name.
    */
   @PostMapping("/doctor/get")
   public String getDoctor(Doctor doctor, Model model) {

      try (Connection con = getConnection();) {
         // for DEBUG
         System.out.println("start getDoctor "+doctor);

         PreparedStatement ps = con.prepareStatement("select name, specialty," +
                                                     " practice_since_year " +
                                                     "from doctor where id=? and name=?");
         ps.setInt(1, doctor.getId());
         ps.setString(2, doctor.getName());
         ResultSet rs = ps.executeQuery();


         if (rs.next()) {
            doctor.setName(rs.getString(1));
            doctor.setPractice_since_year(rs.getString(3));
            doctor.setSpecialty(rs.getString(2));
            model.addAttribute("doctor", doctor);
            // for DEBUG
            System.out.println("end getDoctor "+doctor);
            return "doctor_show";

         }
         else {
            model.addAttribute("message", "Doctor not found.");
            return "doctor_get";

         }

      }

      catch (SQLException e) {
         System.out.println("SQL error in getDoctor "+e.getMessage());
         model.addAttribute("message", "SQL Error."+e.getMessage());
         model.addAttribute("doctor", doctor);
         return "doctor_get";
      }
   }

   /*
    * search for doctor by id.
    */
   @GetMapping("/doctor/edit/{id}")
   public String getDoctor(@PathVariable int id, Model model) {
      Doctor doctor = new Doctor();
      doctor.setId(id);
      try (Connection con = getConnection();) {

         PreparedStatement ps = con.prepareStatement("select name, specialty," +
                                            " practice_since_year " +
                                          "from doctor where id=?");

         ps.setInt(1, doctor.getId());


         ResultSet rs = ps.executeQuery();
         if (rs.next()) {
            doctor.setName(rs.getString(1));
            doctor.setPractice_since_year(rs.getString(3));
            doctor.setSpecialty(rs.getString(2));
            model.addAttribute("doctor", doctor);
            return "doctor_edit";
         } else {
            model.addAttribute("message", "Doctor not found.");
            model.addAttribute("doctor", doctor);
            return "doctor_get";
         }

      } catch (SQLException e) {
         model.addAttribute("message", "SQL Error."+e.getMessage());
         model.addAttribute("doctor", doctor);
         return "doctor_get";

      }
   }

   /*
    * process profile update for doctor.  Change specialty or year of practice.
    */
   @PostMapping("/doctor/edit")
   public String updateDoctor(Doctor doctor, Model model) {
      try (Connection con = getConnection();) {

         PreparedStatement ps = con.prepareStatement("update doctor set " +
                                                     "specialty=?, " +
                                                     "practice_since_year=? " +
                                                     "where id=?");
         ps.setString(1,  doctor.getSpecialty());
         ps.setString(2, doctor.getPractice_since_year());
         ps.setInt(3,  doctor.getId());

         ValidateData validate = new ValidateData();

         if(!validate.validateName(doctor.getSpecialty()))
         {
            model.addAttribute("message", "Specialty cannot be non-alphabetic.");
            model.addAttribute("doctor", doctor);
            return "doctor_edit";
         }
         else if(!validate.validateDate(doctor.getPractice_since_year()))
         {
            model.addAttribute("message", "First Year in Practice must be formatted as: YYYYMMDD");
            model.addAttribute("doctor", doctor);
            return "doctor_edit";
         }
         else
         {
            //Formats date for DB
            String year = String.valueOf(doctor.getPractice_since_year().charAt(0));
            year += String.valueOf(doctor.getPractice_since_year().charAt(1));
            year += String.valueOf(doctor.getPractice_since_year().charAt(2));
            year += String.valueOf(doctor.getPractice_since_year().charAt(3));

            String month = String.valueOf(doctor.getPractice_since_year().charAt(4));
            month += String.valueOf(doctor.getPractice_since_year().charAt(5));

            String day = String.valueOf(doctor.getPractice_since_year().charAt(6));
            day += String.valueOf(doctor.getPractice_since_year().charAt(7));

            doctor.setPractice_since_year(year + "-" + month + "-" + day);

            ps.setString(2, doctor.getPractice_since_year());
         }

         int rc = ps.executeUpdate();
         if (rc==1) {
            model.addAttribute("message", "Update successful");
            model.addAttribute("doctor", doctor);
            return "doctor_show";

         }else {
            model.addAttribute("message", "Error. Update was not successful");
            model.addAttribute("doctor", doctor);
            return "doctor_edit";
         }

      } catch (SQLException e) {
         model.addAttribute("message", "SQL Error."+e.getMessage());
         model.addAttribute("doctor", doctor);
         return "doctor_edit";
      }
   }

   /*
    * return JDBC Connection using jdbcTemplate in Spring Server
    */

   private Connection getConnection() throws SQLException {
      Connection conn = jdbcTemplate.getDataSource().getConnection();
      return conn;
   }

}
