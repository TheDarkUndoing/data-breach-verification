package net.anubis.passwordDB;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.ArrayList;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;


public class GetFile
{
  public static File deCompressGZipFile(File gZippedFile, File tarFile)
  {
    try
    {
      FileInputStream fis = new FileInputStream(gZippedFile);
      GZIPInputStream gZIPInputStream = new GZIPInputStream(fis);

      FileOutputStream fos = new FileOutputStream(tarFile);
      byte[] buffer = new byte[1024];
      int len;
      while((len = gZIPInputStream.read(buffer)) > 0){
          fos.write(buffer, 0, len);
      }

      fos.close();
      gZIPInputStream.close();

    }catch (IOException e)
    {
      e.printStackTrace();
    }
    return tarFile;
  }

  public static TarArchiveInputStream getTarArchiveStream(File tarFile)
  {
    FileInputStream fis = null;
    TarArchiveInputStream tis = null;
    try
    {
      fis = new FileInputStream(tarFile);
      tis = new TarArchiveInputStream(fis);
      return tis;

    }catch (IOException e)
    {
      e.printStackTrace();
    }
    return tis;
  }

  public static  TarArchiveEntry[] getEntries(TarArchiveInputStream tis)
  {
    ArrayList<TarArchiveEntry> tarEntryArray = new ArrayList<TarArchiveEntry>();
    TarArchiveInputStream currentStream = tis;
    TarArchiveEntry currentEntry = null;
    TarArchiveEntry[] tarEntryArray_final = null;
    try
    {

      currentStream.getNextTarEntry();
      while ((currentEntry = currentStream.getCurrentEntry()) != null)
      {
        tarEntryArray.add(currentEntry);
        currentStream.getNextTarEntry();
      }
      currentStream.close();

      tarEntryArray_final = new TarArchiveEntry[tarEntryArray.size()];
      for (int counter = 0; counter < tarEntryArray.size(); counter++)
      {
        	tarEntryArray_final[counter] = tarEntryArray.get(counter);
      }
      return (TarArchiveEntry[])tarEntryArray_final;
      }catch (IOException e)
      {
        e.printStackTrace();
      }
      return (TarArchiveEntry[])tarEntryArray_final;
    }

  }
