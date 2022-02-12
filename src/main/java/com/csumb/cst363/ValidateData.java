package com.csumb.cst363;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class ValidateData
{
   /*
    * validateSSN checks for a valid SSN input from user.
    *
    */
   public boolean validateSSN(String ssn)
   {
      //Checking for SSN length.
      if(ssn.length() == 9)
      {
         //Check if SSN is numeric.
         try
         {
            Integer.parseInt(ssn);

         }catch(Exception e)
         {
            return false;
         }

         //Checking if SSN starts with a 0 or 9. If middle two digits are 00.
         //If last four digits are 0000.
         if((int)ssn.charAt(0) == 48 || (int)ssn.charAt(0) == 57)
         {
            return false;
         }
         else if((int)ssn.charAt(3) == 48 && (int)ssn.charAt(4) == 48)
         {
           return false;
         }
         else if(((int)ssn.charAt(5) == 48 && (int)ssn.charAt(6) == 48) && ((int)ssn.charAt(7) == 48 && (int)ssn.charAt(8) == 48))
         {
            return false;
         }
         else
         {
            return true;
         }
      }
      else
      {
        return false;
      }
   }

   /*
    * validateName checks if name is alphabetic.
    */
   public boolean validateName(String name)
   {
      if(name.isEmpty() || name.length() < 2)
      {
         return false;
      }
      else
      {
         for(int i=0; i < name.length(); i++)
         {
            char k = name.charAt(i);
            if( ((int)k < 65 && (int)k != 32 ) || ((int)k > 122) )
            {
               return false;
            }
            else if( ((int)k > 90 && (int)k < 97) )
            {
               return false;
            }
         }
      }

      return true;
   }

   /*
    * validateStreet checks if street does not contain special characters (e.g @ ! & $ %)
    */
   public boolean validateStreet(String street)
   {
      if(street.isEmpty())
      {
         return false;
      }
      else
      {
         for(int i=0; i < street.length(); i++)
         {
            char k = street.charAt(i);

            if( i == 0 && ((int)k == 32) )
            {
               return false;
            }
            else if( ((int)k < 48 && (int)k != 32) || (int)k > 122)
            {
               return false;
            }
            if( ((int)k > 57 && (int)k < 65) || ((int)k > 90 && (int)k < 97))
            {
               return false;
            }
         }
      }
      return true;
   }
   /*
    * validateZipcode checks if Zipcode is 5 or 9 digits long.
    */
   public boolean validateZipcode(String zipcode)
   {
      //Checking Zipcode length
      if(zipcode.length() == 9 || zipcode.length() == 5)
      {
         //Checking if zipcode is numeric
         try
         {
            Integer.parseInt(zipcode);

         }catch(Exception e)
         {
            return false;
         }
      }
      else
      {
         return false;
      }
      return true;
   }

   /*
    * getCorrectDoctor determines the correct primary doctor for patient according to their age.
    */
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

      if(patient_birthdate.get(Calendar.YEAR) >= current_time.get(Calendar.YEAR))
      {
         return "Pediatrics";
      }
      else
      {
         return "Family Medicine";
      }

   }

   /*
    * validateDate checks if doctor enters date correctly (e.g YYYMMDD)
    */
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
            if(Integer.valueOf(String.valueOf(date.charAt(4)))== 0 && Integer.valueOf(String.valueOf(date.charAt(5)))!= 0)
            {
               if(Integer.valueOf(String.valueOf(date.charAt(6))) == 0 && Integer.valueOf(String.valueOf(date.charAt(7)))!= 0)
               {
                  return true;
               }
               else if(Integer.valueOf(day) >= 10 && Integer.valueOf(day) <=31)
               {
                  return true;
               }
            }
            else if(Integer.valueOf(month) >= 10 && Integer.valueOf(month) <=12)
            {
                  if(Integer.valueOf(String.valueOf(date.charAt(6))) == 0 && Integer.valueOf(String.valueOf(date.charAt(5)))!= 0)
                  {
                     return true;
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
