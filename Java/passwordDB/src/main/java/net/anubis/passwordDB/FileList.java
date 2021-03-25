package net.anubis.passwordDB;

import java.io.*;

public class FileList implements Serializable {
  private File[] files;
  private int count;

  public FileList(File[] files, int count)
  {
    this.files = files;
    this.count = count;
  }

  public File[] getFiles()
  {
    return this.files;
  }

  public void setFiles(File[] files)
  {
    this.files = files;
  }
  public int getCount()
  {
    return this.count;
  }
}
