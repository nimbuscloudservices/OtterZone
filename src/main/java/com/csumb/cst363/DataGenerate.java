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

         int doctor_id;
         int patient_id;
         int prescription_id;

         String doctor_specialty = null;
         String doctor_years = null;

         int first_three_ssn, middle_two_ssn, last_four_ssn;

         File name = new File("src/main/resources/names.txt");
         Scanner test = new Scanner(name);

         Random gen = new Random();

         Random first_three_ssn_numbers = new Random(1000);
         Random middle_two_ssn_numbers = new Random(100);
         Random last_four_ssn_numbers = new Random(10000);

         Random number_generator = new Random(16);

         Random street_number = new Random(1000);

         Random random_quanity = new Random(90);

         String[] specialties = { "Internal Medicine",
               "Family Medicine", "Pediatrics", "Orthopedics",
               "Dermatology",  "Cardiology", "Gynecology",
               "Gastroenterology", "Psychiatry", "Oncology" };

         String[] zipcodes = {"94016", "94102", "94103", "94104", "94110", "94117"};

         String[] drug_names = {"Cerdelga", "Tegsedi", "Adderall", "Celexa", "Femara", "Briviact", "Ritalin",
               "Tembexa", "Prolia" , "Otezla", "Dupixent", "Cibinqo", "Amevive", "Cleviprex" , "Juxtapid",
               "Cholbam", "Dificid", "Edluar", "Fetzima", "Verzenio"};

         int[] birth_year = {1980, 1985, 1990, 1995, 2000};

         SimpleDateFormat simpleDateFormat = new SimpleDateFormat("YYYY-MM-dd");


         try(Connection con = DriverManager.getConnection("jdbc:mysql://ec2-13-56-197-130.us-west-1.compute.amazonaws.com:3306/OtterZoneDB", "root", "ToInfinityAndBeyond");)
         {

            //Generating 10 random doctors
            for(int i=0; i<10; i++)
            {
               //Generating random specialty.
               String random_specialty = specialties[i];

               //Generating random SSN number for doctor.
               first_three_ssn = first_three_ssn_numbers.nextInt(999-100+1) + 100;
               middle_two_ssn = middle_two_ssn_numbers.nextInt(99-10+1) + 10;
               last_four_ssn = last_four_ssn_numbers.nextInt(9999-1000+1) + 1000;
               int doctor_ssn = Integer.valueOf(String.valueOf(first_three_ssn) + String.valueOf(middle_two_ssn) + String.valueOf(last_four_ssn));


               //IO to get random name for doctor.
               test.hasNextLine();
               String doctor_name = test.nextLine();

               //Generating random date for doctor.
               int random_birth_year = birth_year[gen.nextInt(birth_year.length)];
               int random_number = number_generator.nextInt(15-3+1) + 3;

               random_birth_year+=random_number;

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
               if (rs.next())
               {

                  doctor_id = rs.getInt(1);

                  Doctor doc = new Doctor();

                  doc.setId(doctor_id);
                  doc.setSsn(String.valueOf(doctor_ssn));
                  doc.setName(doctor_name);
                  doc.setSpecialty(random_specialty);
                  doc.setPractice_since_year(random_date);

                }

            }

            File street_name = new File("src/main/resources/streets.txt");
            Scanner st = new Scanner(street_name);

            //Generating 1000 random patients
            for(int j = 0; j < 1000; j++)
            {
               //Generating random SSN number for patient.
               first_three_ssn = first_three_ssn_numbers.nextInt(999-100+1) + 100;
               middle_two_ssn = middle_two_ssn_numbers.nextInt(99-10+1) + 10;
               last_four_ssn = last_four_ssn_numbers.nextInt(9999-1000+1) + 1000;
               int patient_ssn = Integer.valueOf(String.valueOf(first_three_ssn) + String.valueOf(middle_two_ssn) + String.valueOf(last_four_ssn));

               //IO to get random name for patient.
               test.hasNextLine();
               String patient_name = test.nextLine();

               //Generating random date for patient.
               int patient_birth_year = birth_year[gen.nextInt(birth_year.length)];
               int patient_number = number_generator.nextInt(15-2+1) + 2;

               patient_birth_year+=patient_number;

               Calendar patient_birthdate = Calendar.getInstance();
               Calendar current_time = Calendar.getInstance();
               patient_birthdate.set(Calendar.YEAR,  patient_birth_year);
               patient_birthdate.set(Calendar.DAY_OF_YEAR, 1);
               patient_birthdate.add(Calendar.DAY_OF_YEAR, gen.nextInt(365));
               Date patient_dt = new Date(patient_birthdate.getTimeInMillis());
               Date current_dt = new Date(current_time.getTimeInMillis());
               String random_dt = simpleDateFormat.format(patient_dt);


               String doctor_ssn = null;
               String doctor = null;


               current_time.add(Calendar.YEAR, -17);

               doctor_id = 0;

               //Getting correct doctor according to date of birth
               if(patient_birthdate.get(Calendar.YEAR) >= current_time.get(Calendar.YEAR))
               {
                  PreparedStatement ps = con.prepareStatement( "select id, ssn, name, specialty, practice_since_year from doctor" +
                        " where specialty = ?");
                     ps.setString(1, "Pediatrics");

                     ResultSet rs = ps.executeQuery();
                     while (rs.next()) {
                        doctor_id = rs.getInt(1);
                        doctor_ssn = rs.getString(2);
                        doctor = rs.getString(3);
                        doctor_specialty = rs.getString(4);
                        doctor_years = rs.getString(5);
                     }
               }
               else
               {
                  PreparedStatement ps = con.prepareStatement( "select id, ssn, name, specialty, practice_since_year from doctor" +
                        " where specialty = ? or specialty = ? ORDER BY RAND() LIMIT 1" );
                     ps.setString(1, "Internal Medicine");
                     ps.setString(2, "Family Medicine");

                     ResultSet rs = ps.executeQuery();

                     while (rs.next()) {
                        doctor_id = rs.getInt(1);
                        doctor_ssn = rs.getString(2);
                        doctor = rs.getString(3);
                        doctor_specialty = rs.getString(4);
                        doctor_years = rs.getString(5);
                     }
               }
               //Generating random address for patient.
               int street = street_number.nextInt(999-10+1) + 10;

               //IO to get random street.
               String st_name;
               if(st.hasNextLine())
               {
                   st_name = st.nextLine();
               }
               else
               {
                  st.close();
                  street_name = new File("src/main/resources/streets.txt");
                  st = new Scanner(street_name);
                  st.hasNextLine();
                  st_name = st.nextLine();
               }

               //Random zipcode
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
               patient.setString(8, doctor);

               patient.executeUpdate();

               ResultSet rs_patient = patient.getGeneratedKeys();
               if (rs_patient.next())
               {
                  patient_id = rs_patient.getInt(1);

                  Patient p = new Patient();
                  p.setPatientId(patient_id);
                  p.setName(patient_name);
                  p.setBirthdate(patient_name);
                  p.setStreet(String.valueOf(street) + " " + st_name);
                  p.setCity("San Francisco");
                  p.setState("CA");
                  p.setZipcode(random_zipcode);
                  p.setPrimaryID(doctor_id);
                  p.setPrimaryName(doctor);
                  p.setSpecialty(doctor_specialty);
                  p.setYears(doctor_years);
               }
               //Generating 5000 prescription (5 prescriptions per patient)
               for(int k = 0; k < 5; k++)
               {
                  //Random drug.
                  String random_drug = drug_names[gen.nextInt(drug_names.length)];

                  //Random quantity
                  int quantity =  random_quanity.nextInt(90-10+1)+10;
                  PreparedStatement prescription = con.prepareStatement(
                        "insert into Prescription(drugName, quantity, patient_ssn, patientName, doctor_ssn, doctorName, Doctor_id) values(?, ?, ?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS);

                  prescription.setString(1, random_drug);
                  prescription.setInt(2, quantity);
                  prescription.setString(3, String.valueOf(patient_ssn));
                  prescription.setString(4, patient_name);
                  prescription.setString(5, String.valueOf(doctor_ssn));
                  prescription.setString(6, doctor);
                  prescription.setString(7, String.valueOf(doctor_id));

                  prescription.executeUpdate();

                  ResultSet rs_prescription = prescription.getGeneratedKeys();

                  if (rs_prescription.next()) prescription_id = rs_prescription.getInt(1);

               }
            }
         }catch (SQLException e) {
            System.out.print("Error " + e.getMessage());
         }
   }
}
