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
 
         int doc_id = 0;
         
         try(Connection con = DriverManager.getConnection("jdbc:mysql://ec2-13-56-197-130.us-west-1.compute.amazonaws.com:3306/OtterZoneDB", "root", "ToInfinityAndBeyond");)
         {
            
            for(int i=0; i<10; i++)
            {
               //Generating random specialty. 
               Random gen = new Random();
               String[] specialties = { "Internal Medicine", 
                     "Family Medicine", "Pediatrics", "Orthopedics",
                     "Dermatology",  "Cardiology", "Gynecology", 
                     "Gastroenterology", "Psychiatry", "Oncology" };
               String random_specialty = specialties[gen.nextInt(specialties.length)];
               
               //Generating random SSN number for doctor.
               Random ssn_generator = new Random(100000000); 
               int A = 1;
               int B = 99999999;
               int doctor_ssn =   A + ssn_generator.nextInt(B-A+1);
      
               
               //IO to get random name for doctor. 
               File name = new File("src/main/resources/names.txt");
               Scanner test = new Scanner(name);
               test.hasNextLine();
               String doctor_name = test.nextLine();
          
               //Generating random date for doctor. 
               int[] birth_year = {1980, 1985, 1990, 1995, 2000};
               int random_birth_year = birth_year[gen.nextInt(birth_year.length)];
               
               Random number_generator = new Random(20); 
               int C = 1;
               int D = 19;
               int random_number = C + number_generator.nextInt(D-C+1);
               
               random_birth_year+=random_number;
               
               SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");
               Calendar c = Calendar.getInstance();
               c.set(Calendar.YEAR,  random_birth_year);
               c.set(Calendar.DAY_OF_YEAR, 1);
               c.add(Calendar.DAY_OF_YEAR, gen.nextInt(365));
               Date dt = new Date(c.getTimeInMillis());
               String random_date = simpleDateFormat.format(dt);
              
               PreparedStatement doctor = con.prepareStatement(
                     "insert into doctor(ssn, name, specialty, practice_since_year) values(?, ?, ?, ?)",
                     Statement.RETURN_GENERATED_KEYS);
           
            
           
               doctor.setString(1, String.valueOf(doctor_ssn));
               doctor.setString(2, doctor_name);
               doctor.setString(3, random_specialty);
               doctor.setString(4, random_date);
            
            
               doctor.executeUpdate();
            
               ResultSet rs = doctor.getGeneratedKeys();
               int doctor_id;
               if (rs.next())
               {
                  
                  doctor_id = rs.getInt(1);
                  doc_id = doctor_id;
                }
               
               
               for(int j = 0; j < 100; j++)
               {
                  //Generating random SSN number for patient.
                  int patient_ssn =   A + ssn_generator.nextInt(B-A+1);
         
                  //IO to get random name for patient. 
                  test.hasNextLine();
                  String patient_name = test.nextLine();
             
                  //Generating random date for patient. 
                  int patient_birth_year = birth_year[gen.nextInt(birth_year.length)];               
                  int patient_number = C + number_generator.nextInt(D-C+1);
                  
                  patient_birth_year+=patient_number;
               
                  Calendar patient_birthdate = Calendar.getInstance();
                  patient_birthdate.set(Calendar.YEAR,  patient_birth_year);
                  patient_birthdate.set(Calendar.DAY_OF_YEAR, 1);
                  patient_birthdate.add(Calendar.DAY_OF_YEAR, gen.nextInt(365));
                  Date patient_dt = new Date(c.getTimeInMillis());
                  String random_dt = simpleDateFormat.format(patient_dt);
                  
                  //Generating random address for patient.
                  Random street_number = new Random(1000); 
                  int E = 1;
                  int F = 999;
                  int street =   E + street_number.nextInt(F-E+1);
                  
                  //IO to get random street. 
                  File street_name = new File("src/main/resources/streets.txt");
                  Scanner st = new Scanner(street_name);
                  st.hasNextLine();
                  String st_name = st.nextLine();
                  
                  String[] zipcodes = {"94016", "94102", "94103", "94104", "94110", "94117"};
                  String random_zipcode = zipcodes[gen.nextInt(zipcodes.length)];
                  
                  PreparedStatement patient = con.prepareStatement(
                        "insert into patient(ssn, name, birthdate, street, city, state, zipcode, primaryName) values(?, ?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);
              
               
              
                  patient.setString(1, String.valueOf(patient_ssn));
                  patient.setString(2, patient_name);
                  patient.setString(3, random_dt);
                  patient.setString(4, String.valueOf(street) + " " + st_name);
                  patient.setString(5, "San Francisco");
                  patient.setString(6, "CA");
                  patient.setString(7, random_zipcode);
                  patient.setString(8, doctor_name);
               
               
                  patient.executeUpdate();
               
                  ResultSet rs_patient = patient.getGeneratedKeys();
                  int patient_id;
                  if (rs_patient.next()) patient_id = rs_patient.getInt(1);
                  st.close();
                  
                  for(int k = 0; k< 5; k++)
                  {
                     String[] drug_names = {"Cerdelga", "Tegsedi", "Adderall", "Celexa", "Femara", "Briviact", "Ritalin", 
                           "Tembexa", "Prolia" , "Otezla", "Dupixent", "Cibinqo", "Amevive", "Cleviprex" , "Juxtapid", 
                           "Cholbam", "Dificid", "Edluar", "Fetzima", "Verzenio"};
                     String random_drug = drug_names[gen.nextInt(drug_names.length)];
                     
                     Random random_quanity = new Random(100); 
                     int G = 99;
                     int quantity =   E + street_number.nextInt(G-E+1);
                     PreparedStatement prescription = con.prepareStatement(
                           "insert into Prescription(drugName, quantity, patient_ssn, patientName, doctor_ssn, doctorName, Doctor_id) values(?, ?, ?, ?, ?, ?, ?)",
                           Statement.RETURN_GENERATED_KEYS);
                     
                     prescription.setString(1, random_drug);
                     prescription.setInt(2, quantity);
                     prescription.setString(3, String.valueOf(patient_ssn));
                     prescription.setString(4, patient_name);
                     prescription.setString(5, String.valueOf(doctor_ssn));
                     prescription.setString(6, doctor_name);
                     prescription.setInt(7, doc_id);
                     
                     prescription.executeUpdate();
                     
                     ResultSet rs_prescription = prescription.getGeneratedKeys();
                     int prescription_id;
                     if (rs_prescription.next()) prescription_id = rs_prescription.getInt(1);
                     
                  }
               }
               
            }
          
         }catch (SQLException e) {
            System.out.print("Error");
         }
   }
 
}
