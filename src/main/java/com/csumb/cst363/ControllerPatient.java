package com.csumb.cst363;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.regex.Pattern;

import org.apache.tomcat.util.codec.binary.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


/*
 * Controller class for patient interactions.
 *   register as a new patient.
 *   update patient profile.
 */
@Controller
public class ControllerPatient {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	/*
	 * Request patient_register form.
	 */
	@GetMapping("/patient/new")
	public String newPatient(Model model) {
		// return blank form for new patient registration
		model.addAttribute("patient", new Patient());
		return "patient_register";
	}

	/*
	 * Request form to search for patient.
	 */
	@GetMapping("/patient/edit")
	public String getPatientForm(Model model) {
		// prompt for patient id and name
		return "patient_get";
	}

	/*
	 * Process a form to create new patient.
	 */
	@PostMapping("/patient/new")
	public String newPatient(Patient patient, Model model) {

		try (Connection con = getConnection();) {

			PreparedStatement ps = con.prepareStatement("insert into patient(ssn, name, birthdate, street, city, state, zipcode, primaryName ) values(?, ?, ?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, patient.getSsn());
			ps.setString(2, patient.getName());
			ps.setString(3, patient.getBirthdate());
			ps.setString(4, patient.getStreet());
			ps.setString(5, patient.getCity());
			ps.setString(6, patient.getState());
			ps.setString(7, patient.getZipcode());
			ps.setString(8, patient.getPrimaryName());

			ValidateData validate = new ValidateData();

			if(validate.validateSSN(patient) == false)
			{
			   model.addAttribute("message", "Invalid SSN");
            return "patient_register";
			}
			else if(validate.validateName(patient) == false)
			{
			   model.addAttribute("message", "Name cannot be non-alphabetic.");
            return "patient_register";
			}
			else if(validate.validateStreet(patient) == false)
			{
			   model.addAttribute("message", "Street cannot be non-alphabetic or non-numeric.");
            return "patient_register";
			}
			else if(validate.validateCity(patient) == false)
			{
			   model.addAttribute("message", "City cannot be non-alphabetic");
            return "patient_register";
			}
			else if(validate.validateState(patient) == false)
			{
			   model.addAttribute("message", "State cannot be non-alphabetic");
            return "patient_register";
			}
			else if(validate.validateZipcode(patient) == false)
			{
			   model.addAttribute("message", "Zipcode must be 5 or 9 digits long.");
            return "patient_register";
			}


			if(validate.getCorrectDoctor(patient) == "Pediatrics")
			{
			   //Checking for doctor
	         PreparedStatement cd = con.prepareStatement("select name from doctor where name =  ? AND specialty = ?");

	            cd.setString(1, patient.getPrimaryName());
	            cd.setString(2, "Pediatrics");

	            ResultSet rs = cd.executeQuery();

               if (!rs.next()) {
                  model.addAttribute("message", "Please select the correct primary doctor");
                  return "patient_register";
               }
			}
			else
			{
			   //Checking for doctor
	         PreparedStatement cd = con.prepareStatement("select name from doctor where name =  ? AND specialty IN (?, ?)");

	            cd.setString(1, patient.getPrimaryName());
	            cd.setString(2, "Family Medicine");
	            cd.setString(3, "Internal Medicine");

	            ResultSet rs = cd.executeQuery();

	            if (!rs.next()) {
	               model.addAttribute("message", "Please select the correct primary doctor");
                  return "patient_register";
	            }
			}

			ps.executeUpdate();
         ResultSet rs = ps.getGeneratedKeys();
         if (rs.next()) patient.setPatientId(rs.getString(1));

         // display message and patient information
         model.addAttribute("message", "Registration successful.");
         model.addAttribute("patient", patient);
         return "patient_show";


		} catch (SQLException e) {
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("patient", patient);
			return "patient_show";

		}

	}

	/*
	 * Search for patient by patient id and name.
	 */
	@PostMapping("/patient/show")
	public String getPatientForm(Patient patient, @RequestParam("patientId") int patientId, @RequestParam("name") String name,
			Model model) {

		try (Connection con = getConnection();) {

			System.out.println("start getDoctor " + patient);
			PreparedStatement ps = con.prepareStatement("select patientId, name, birthdate, street, city, state, zipcode, primaryName from patient where patientId=? and name=?");
			ps.setString(1, patient.getPatientId());
			ps.setString(2, patient.getName());

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				patient.setPatientId(rs.getString(1));
				patient.setName(rs.getString(2));
				patient.setBirthdate(rs.getString(3));
				patient.setStreet(rs.getString(4));
				patient.setCity(rs.getString(5));
				patient.setState(rs.getString(6));
				patient.setZipcode(rs.getString(7));
				patient.setPrimaryName(rs.getString(8));

				model.addAttribute("patient", patient);
				// for DEBUG
				System.out.println("end getDoctor " + patient);
				return "patient_show";

			} else {
				model.addAttribute("message", "Patient not found.");
				return "patient_get";
			}

		} catch (SQLException e) {
			System.out.println("SQL error in getPatient "+e.getMessage());
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("doctor", patient);
			return "patient_show";
		}
	}


	/*
	 * Search for patient by patient id.
	 */
	@GetMapping("/patient/edit/{patientId}")
	public String updatePatient(@PathVariable int patientId, Model model) {

		Patient patient = new Patient();
		patient.setPatientId(String.valueOf(patientId));
		try (Connection con = getConnection();) {

			PreparedStatement ps = con.prepareStatement("select patientId, name, birthdate, street, city, state, zipcode, primaryName from patient where patientId=?");
			ps.setInt(1,  patientId);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				patient.setPatientId(rs.getString(1));
				patient.setName(rs.getString(2));
				patient.setBirthdate(rs.getString(3));
				patient.setStreet(rs.getString(4));
				patient.setCity(rs.getString(5));
				patient.setState(rs.getString(6));
				patient.setZipcode(rs.getString(7));
				patient.setPrimaryName(rs.getString(8));

				model.addAttribute("patient", patient);
				return "patient_edit";
			} else {
				model.addAttribute("message", "Patient not found.");
				model.addAttribute("patient", patient);
				return "patient_get";
			}

		} catch (SQLException e) {
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("patient", patient);
			return "patient_get";

		}

	}


	/*
	 * Process changes to patient address and primary doctor
	 */
	@PostMapping("/patient/edit")
	public String updatePatient(Patient patient, Model model) {

		try (Connection con = getConnection();) {

			PreparedStatement ps = con.prepareStatement("update patient set street=?, city=?, state=?, zipcode=?, primaryName=? where patientId=?");
			ps.setString(1,  patient.getStreet());
			ps.setString(2,  patient.getCity());
			ps.setString(3,  patient.getState());
			ps.setString(4,  patient.getZipcode());
			ps.setString(5,  patient.getPrimaryName());
			ps.setString(6,  patient.getPatientId());

			int rc = ps.executeUpdate();
			if (rc==1) {
				model.addAttribute("message", "Update successful");
				model.addAttribute("patient", patient);
				return "patient_show";

			}else {
				model.addAttribute("message", "Error. Update was not successful");
				model.addAttribute("patient", patient);
				return "patient_edit";
			}

		} catch (SQLException e) {
			model.addAttribute("message", "SQL Error."+e.getMessage());
			model.addAttribute("patient", patient);
			return "patient_edit";
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
