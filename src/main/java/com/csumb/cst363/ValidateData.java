package com.csumb.cst363;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ValidateData
{
   public boolean validateSSN(String ssn)
   {
      boolean numeric =  true;
      //Checking for ssn length
      if(ssn.length() == 9)
      {
         //Check if ssn is numeric
         try
         {
            Integer.parseInt(ssn);

         }catch(Exception e)
         {
            numeric = false;
         }

         int[] index = {0,3,4,5,6,7,8};
         for(int i = 0; i < index.length; i++)
         {
            char number = ssn.charAt(index[i]);
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

   public boolean validateName(String name)
   {
      boolean alphabetic = true;

      for(int i=0; i < name.length(); i++)
      {
         char k = name.charAt(i);
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

   public boolean validateDate(String date)
   {
      //Checking if string is numeric.
      try
      {
         Integer.parseInt(date);

      }catch(Exception e)
      {
         return false;
      }



      //Checking if string has correct length YYYYMMDD

      if(date.length() !=8 )
      {
         return false;
      }
      else
      {

         String year = String.valueOf(date.charAt(0));

         year += String.valueOf(date.charAt(1));
         year += String.valueOf(date.charAt(2));
         year += String.valueOf(date.charAt(3));

         String month = String.valueOf(date.charAt(4));

         month += String.valueOf(date.charAt(5));

         String day = String.valueOf(date.charAt(6));

         day += String.valueOf(date.charAt(7));

         if(Integer.valueOf(year) >= 1900 && Integer.valueOf(year) <= 2022)
         {
            if(Integer.valueOf(String.valueOf(date.charAt(4)))== 0)
            {
               if(Integer.valueOf(String.valueOf(date.charAt(5))) <=9)
               {
                  if(Integer.valueOf(String.valueOf(date.charAt(6))) == 0)
                  {
                     if(Integer.valueOf(String.valueOf(date.charAt(7))) <= 9)
                     {
                        return true;
                     }
                  }
                  else if(Integer.valueOf(day) >= 10 && Integer.valueOf(day) <=31)
                  {
                    return true;
                  }
               }
            }
            else if(Integer.valueOf(month) >= 10 && Integer.valueOf(month) <=12)
            {
                  if(Integer.valueOf(String.valueOf(date.charAt(6))) == 0)
                  {
                     if(Integer.valueOf(String.valueOf(date.charAt(6))) <= 9)
                     {

                        return true;
                     }
                  }
                  else if(Integer.valueOf(day) >= 10 && Integer.valueOf(day) <=31)
                  {
                     return true;
                  }
            }
         }
         return false;
      }
   }
}
