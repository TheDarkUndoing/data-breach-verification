package net.anubis.passwordDB;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Properties;

public class ConfigFile
{
  private String path;
  private Properties config = new Properties();

  public ConfigFile(String path)
  {
    this.path = path;
  }
  public Properties load()
  {
    try (InputStream input = new FileInputStream(this.path))
    {
      //Properties config = new Properties();
      this.config.load(input);
      input.close();
    } catch (IOException ex)
    {
      ex.printStackTrace();
    }
    return this.config;
  }
  public void setPath(String newPath)
  {
    path = newPath;
  }
  public void setValue(String key, String value)
  {
    load();
    config.setProperty(key,value);
    store();
  }
  public void store()
  {
    try (OutputStream output = new FileOutputStream(path))
    {
      config.store(output, null);
      output.close();
    } catch (IOException io)
      {
      io.printStackTrace();
      }
  }
  public static void makeDBConfig(String path)
  {
    try (OutputStream output = new FileOutputStream(path))
    {
      Properties prop = new Properties();

      // set the properties value
      prop.setProperty("db.url", "jdbc:mysql://localhost/password_db");
      prop.setProperty("db.username", "passwordDB");
      prop.setProperty("db.password", "passwordDB");
      prop.setProperty("db.db", "password_db");

      // save properties to project root folder
      prop.store(output, null);
      output.close();

    } catch (IOException io)
      {
      io.printStackTrace();
      }
  }

  public String toString()
  {
    return config.toString();
  }
}
