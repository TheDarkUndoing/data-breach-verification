package net.anubis.passwordDB;

import java.sql.*;
import java.util.Properties;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.util.Scanner;
import java.io.IOException;
import java.util.Arrays;

public class Utility
{
  public static void setupDB(int mode)
  {
    ConfigFile config;
    Properties prop;
    String url = null;
    String user = null;
    String pass = null;
    String db = null;
    Statement st = null;
    Connection con = null;
    ResultSet res = null;
    String dbUser = null;
    String dbDatabase = null;
    String dbTable = null;
    String dbIndex = null;
    Boolean dbDatabaseExist = false;
    Boolean dbUserExist = false;
    Boolean dbTableExist = false;
    Boolean dbIndexExist = false;
    if (mode == 0) {
      ConfigFile.makeDBConfig("database.properties");
      url = "jdbc:mysql://localhost/";
      user = "root";
      pass = "password";


    }
    else if (mode == 1) {
      config = new ConfigFile("database_web.properties");

      prop = config.load();


      url = prop.getProperty("db.url");
      pass = prop.getProperty("db.password");
      user = prop.getProperty("db.username");
      db = prop.getProperty("db.db");
      System.out.println("WEB BUILD");
    }





    try
    {
      con = DriverManager.getConnection(url,user, pass);
      st = con.createStatement();

      if (mode == 0) {
        //Add USER
        res = st.executeQuery("select * from mysql.user");
        while(res.next())
        {
          dbUser = res.getString("User");
          if(dbUser.equals("passwordDB"))
          {
            dbUserExist = true;
            System.out.println("[SETUP-DB] user found in database");
          }

        }
        if(!dbUserExist)
        {
          System.out.println("[SETUP-DB] user not found in database");
          System.out.println("[SETUP-DB] adding passwordDB user...");
          st.executeQuery("CREATE USER 'passwordDB'@'"+user+"' IDENTIFIED BY 'passwordDB'");
          st.executeQuery("GRANT ALL ON *.* TO 'passwordDB'@'localhost' WITH GRANT OPTION");

        }
        //Add DATABASE
        res = st.executeQuery("show databases");
        while(res.next())
        {
          dbDatabase = res.getString("Database");
          if(dbDatabase.equals("password_db"))
          {
            dbDatabaseExist = true;
            System.out.println("[SETUP-DB] Database found in database");
          }

        }
        if(!dbDatabaseExist)
        {
          System.out.println("[SETUP-DB] Database not found in database");
          System.out.println("[SETUP-DB] adding password_db database...");
          st.executeQuery("CREATE DATABASE password_db");
        }
      }

      //Add TABLE
      st.executeQuery("use "+db);
      res = st.executeQuery("show tables");
      while(res.next())
      {
        dbTable = res.getString("Tables_in_"+db);
        if(dbTable.equals("password_count_by_user"))
        {
          dbTableExist = true;
          System.out.println("[SETUP-DB] Table found in database");
        }
      }
      if(!dbTableExist)
      {
        System.out.println("[SETUP-DB] Table not found in database");
        System.out.println("[SETUP-DB] adding password_count_by_user table...");
        st.executeQuery("CREATE TABLE "+db+".password_count_by_user( username varchar(255), count INTEGER );");
      }
      // Add Index
      res = st.executeQuery("SHOW INDEX FROM "+db+".password_count_by_user");
      while(res.next())
      {
        dbTable = res.getString("Table");
        dbIndex = res.getString("Key_name");
        if(dbTable.equals("password_count_by_user") && dbIndex.equals("username"))
        {
          dbIndexExist = true;
          System.out.println("[SETUP-DB] Index found in database");
        }

      }
      if(!dbIndexExist)
      {
        System.out.println("[SETUP-DB] index not found in database");
        System.out.println("[SETUP-DB] adding username index...");
        st.executeQuery("CREATE INDEX username ON password_count_by_user (username)");


      }
    }catch (SQLException e)
    {
      e.printStackTrace();
    }




  }


  public static String[] getState()
  {
    String[] state = new String[3];

    try {

      File tmpFile = new File("state.tmp");
      Scanner tmpFileReader = new Scanner(tmpFile);
      int count = 0;
      while (tmpFileReader.hasNextLine()) {

        String line = tmpFileReader.nextLine();

        state[count] = line;
        count += 1;
      }
      tmpFileReader.close();
    } catch(IOException e){}
      System.out.println(Arrays.toString(state));
      System.out.println("INSIDE getState()");

      return state;

  }
  public static void setState(String tarfile,String file,String line)
  {
    try {
      File tmpFile = new File("state.tmp");

      FileWriter tmpFileWriter = new FileWriter(tmpFile);
      if(!tmpFile.exists())
      {
        tmpFile.createNewFile();

      }else {
        tmpFileWriter.write(tarfile+"\n"+file+"\n"+line);
        tmpFileWriter.flush();
        //System.out.println(tarfile+"\n"+file+"\n"+line);
      }
      tmpFileWriter.close();
    } catch(IOException e) {}


  }
}
