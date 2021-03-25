package net.anubis.passwordDB;
import java.sql.*;
import java.util.Properties;

public class DatabaseHandlerMariaDB
{
  ConfigFile config;

  String url = null;
  String password = null;
  int count = 0;
  String username = null;
  String database = null;
  Connection con = null;
  Properties prop = null;
  Statement st = null;
  ResultSet res = null;

  public void connect(String configPath)
  {

    config = new ConfigFile(configPath);
    prop = config.load();

    url = prop.getProperty("db.url");
    password = prop.getProperty("db.password");
    username = prop.getProperty("db.username");
    database = prop.getProperty("db.db");

    try
    {
      con = DriverManager.getConnection(url,username, password);
      st = con.createStatement();
    }catch (SQLException e)
    {
      e.printStackTrace();
    }

  }
  public void insert(String username)
  {

    try
    {

      res = st.executeQuery("Select username FROM "+database+".password_count_by_user WHERE username = \'"+username+"\'");
      //If username exists already update passwords
      //res.beforeFirst();

      if(res.next() == false)// add new entry
      {
        //System.out.println(res.getString("username"));
        st.executeQuery("INSERT INTO "+database+".password_count_by_user (username,count) VALUES (\'"+username+"\',\'"+1+"\')");
        System.out.println("NEW Username inserted");
      }
      else
      {
        System.out.println("Existing Username found");
        res = st.executeQuery("Select count FROM "+database+".password_count_by_user WHERE username = \'"+username+"\'");
        res.next();
        count = Integer.parseInt(res.getString("count"));
        count += 1;

        System.out.println();



        System.out.println("Updating count");
        res = st.executeQuery("UPDATE "+database+".password_count_by_user Set count = \'"+count+"\' WHERE username = \'"+username+"\'");


      }
      System.out.println("[LOG]Reached end of insert");
      } catch(SQLException e)
      {
        e.printStackTrace();
      }

      }





  }
