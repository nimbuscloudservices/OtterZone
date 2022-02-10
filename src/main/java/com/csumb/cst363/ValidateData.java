package com.csumb.cst363;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

public class ValidateData
{
   public boolean validateSSN(Patient patient)
   {
      boolean numeric =  true;
      //Checking for ssn length
      if(patient.getSsn().length() == 9)
      {
         //Check if ssn is numeric
         try
         {
            Integer.parseInt(patient.getSsn());

         }catch(Exception e)
         {
            numeric = false;
         }

         int[] index = {0,3,4,5,6,7,8};
         for(int i = 0; i < index.length; i++)
         {
            char number = patient.getSsn().charAt(index[i]);
            if((int)number == 0)
            {
               numeric = false;
            }
         }
      }
      else
      {
        numeric = false;
      }

      return numeric;
   }

   public boolean validateName(Patient patient)
   {
      boolean alphabetic = true;

      for(int i=0; i < patient.getName().length(); i++)
      {
         char k = patient.getName().charAt(i);
         if(((int)k >= 65 && (int)k <= 90) || ((int)k >= 97 && (int)k <= 122))
         {

         }
         else if((int)k == 32)
         {

         }
         else
         {
            return false;
         }
      }

      return alphabetic;
   }

   public boolean validateStreet(Patient patient)
   {
      boolean isValid = true;
      //Checking for street
      for(int i=0; i<patient.getStreet().length(); i++)
      {
         char k = patient.getStreet().charAt(i);
         if(((int)k >= 65 && (int)k <= 90) || ((int)k >= 97 && (int)k <= 122))
         {

         }
         else if((((int)k == 32) && i!=0) || ((int)k >=48 && (int)k <=57) )
         {

         }
         else
         {
            return false;
         }
      }

      return isValid;
   }

   public boolean validateCity(Patient patient)
   {
      boolean isValid = true;
      //Checking for City
      for(int i=0; i<patient.getCity().length(); i++)
      {
         char k = patient.getCity().charAt(i);
         if(((int)k >= 65 && (int)k <= 90) || ((int)k >= 97 && (int)k <= 122))
         {

         }
         else if(((int)k == 32) && i!=0)
         {

         }
         else
         {
            return false;
         }
      }
      return isValid;
   }

   public boolean validateState(Patient patient)
   {
      boolean isValid = true;
      //Checking for State
      for(int i=0; i<patient.getState().length(); i++)
      {
         char k = patient.getState().charAt(i);
         if(((int)k >= 65 && (int)k <= 90) || ((int)k >= 97 && (int)k <= 122))
         {

         }
         else if(((int)k == 32) && i!=0)
         {

         }
         else
         {
            return false;
         }
      }
      return isValid;
   }

   public boolean validateZipcode(Patient patient)
   {
      boolean isValid = true;
      //Checking zipcode
      if(patient.getZipcode().length() == 9 || patient.getZipcode().length() == 5)
      {
         //Check if ssn is numeric
         try
         {
            Integer.parseInt(patient.getZipcode());

         }catch(Exception e)
         {
            return false;
         }
      }
      else
      {
         return false;
      }
      return isValid;
   }

   public String getCorrectDoctor(Patient patient)
   {
      String year = String.valueOf(patient.getBirthdate().charAt(0));;

      year += String.valueOf(patient.getBirthdate().charAt(1));
      year += String.valueOf(patient.getBirthdate().charAt(2));
      year += String.valueOf(patient.getBirthdate().charAt(3));


      Calendar patient_birthdate = Calendar.getInstance();
      Calendar current_time = Calendar.getInstance();
      patient_birthdate.set(Calendar.YEAR,  Integer.parseInt(year));
      current_time.add(Calendar.YEAR, -17);
      System.out.println(current_time.get(Calendar.YEAR) + " " + patient_birthdate.get(Calendar.YEAR));

      if(patient_birthdate.get(Calendar.YEAR) >= current_time.get(Calendar.YEAR))
      {
         return "Pediatrics";
      }
      else
      {
         return "Family Medicine";
      }

   }
}
