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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

/*
 * Controller class for doctor registration and profile update.
 */
@Controller
public class ControllerUsage {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/*
	 * Request form for usage search.
	 */
	@GetMapping("/usage/get")
	public String getUsage(Model model) {
		// return form to enter doctor id and name
		model.addAttribute("usage", new Usage());
		return "usage_get";
	}
	
	/*
	 * Search for doctor by id and name.
	 */
	@PostMapping("/usage/get")
//	public String getUsage(Usage usage, Model model) {
//		
//		try (Connection con = getConnection();) {
//			// for DEBUG 
//			System.out.println("start getDoctor "+doctor);
//			PreparedStatement ps = con.prepareStatement("select name, specialty, practice_since from doctor where id=? and name=?");
//			ps.setInt(1, doctor.getId());
//			ps.setString(2, doctor.getName());
//			
//			ResultSet rs = ps.executeQuery();
//			if (rs.next()) {
//				doctor.setName(rs.getString(1));
//				doctor.setPractice_since_year(rs.getString(3));
//				doctor.setSpecialty(rs.getString(2));
//				model.addAttribute("doctor", doctor);
//				// for DEBUG 
//				System.out.println("end getDoctor "+doctor);
//				return "doctor_show";
//				
//			} else {
//				model.addAttribute("message", "Doctor not found.");
//				return "doctor_get";
//			}
//						
//		} catch (SQLException e) {
//			System.out.println("SQL error in getDoctor "+e.getMessage());
//			model.addAttribute("message", "SQL Error."+e.getMessage());
//			model.addAttribute("doctor", doctor);
//			return "doctor_get";
//		}
//	}
	
	
	/*
	 * return JDBC Connection using jdbcTemplate in Spring Server
	 */

	private Connection getConnection() throws SQLException {
		Connection conn = jdbcTemplate.getDataSource().getConnection();
		return conn;
	}

}
