package com.csumb.cst363;
import java.util.Random;
import java.util.Scanner;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.io.*;
import java.sql.*;

public class DataGenerate
{

   public static void main(String[] args) throws FileNotFoundException
   {
 
         try(Connection con = DriverManager.getConnection("jdbc:mysql://ec2-13-56-197-130.us-west-1.compute.amazonaws.com:3306/OtterZoneDB", "root", "ToInfinityAndBeyond");)
         {
            //Generating random specialty. 
            Random gen = new Random();
            String[] specialties = { "Internal Medicine", 
                  "Family Medicine", "Pediatrics", "Orthopedics",
                  "Dermatology",  "Cardiology", "Gynecology", 
                  "Gastroenterology", "Psychiatry", "Oncology" };
            String random_specialty = specialties[gen.nextInt(specialties.length)];
            System.out.println(random_specialty);

            //Generating random SSN number.
            Random ssn_generator = new Random(100000000); 
            int A = 1;
            int B = 99999999;
            int ssn =   A + ssn_generator.nextInt(B-A+1);
            System.out.println(ssn);
            
            //IO to get random names. 
            File name = new File("src/main/resources/names.txt");
            Scanner test = new Scanner(name);
            test.hasNextLine();
            String data = test.nextLine();
            System.out.println(data);
           

            //Generating random date. 
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
            Calendar c = Calendar.getInstance();
            c.set(Calendar.YEAR,  2000);
            c.set(Calendar.DAY_OF_YEAR, 1);
            c.add(Calendar.DAY_OF_YEAR, gen.nextInt(365));
            Date dt = new Date(c.getTimeInMillis());
            String random_date = simpleDateFormat.format(dt);
            System.out.println(random_date);
                     
         
            PreparedStatement ps = con.prepareStatement(
                  "insert into doctor(ssn, name, specialty, practice_since_year) values(?, ?, ?, ?)",
                  Statement.RETURN_GENERATED_KEYS);
        
         
        
         ps.setString(1, String.valueOf(ssn));
         ps.setString(2, data);
         ps.setString(3, random_specialty);
         ps.setString(4, random_date);
         
         
         ps.executeUpdate();
         ResultSet rs = ps.getGeneratedKeys();
         int doctor_id;
         if (rs.next()) doctor_id = rs.getInt(1);

         test.close();
          
         }catch (SQLException e) {
            System.out.print("Error");
         }

         
         
   }
 
}
