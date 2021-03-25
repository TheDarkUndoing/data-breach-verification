package net.anubis.passwordDB;
import java.io.*;
import java.util.Arrays;
import java.util.List;



import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

public class Main
{
  static String INPUT_FILE_PATH;
  static String OUTPUT_FILE_PATH;
  static File inputFile;
  static DatabaseHandlerMariaDB mariadb;
  static File tarFile;
  static TarArchiveInputStream tis;
  static TarArchiveEntry[] tarEntryArray;
  static String entryName;
  static int entryStartOffset;


  public static void main(final String[] args)
  {
    // Utility.getState();
    // System.out.println("STATE GOTTEN:\n"+Arrays.toString(Utility.getState()));
    List<String> argsList = Arrays.asList(args);
    File stateFile = new File("./state.tmp");
    Boolean continueMode = stateFile.exists();
    if(argsList.contains("-h"))
    {
      System.out.println("-w to Engage web mode\n-o to specify input file");
    }
    if(argsList.contains("-w"))
    {
       mariadb = configureWebDatabase();
      System.out.println("[MODE] Web mode engaged");

    }
    else
    {
      mariadb = configureDatabase();
    }
    //Handles incoming file
    if(argsList.contains("-o"))
    {
      INPUT_FILE_PATH = args[argsList.indexOf("-o") + 1];
      OUTPUT_FILE_PATH = INPUT_FILE_PATH.substring(0,INPUT_FILE_PATH.length() - 3);
      handleFile(INPUT_FILE_PATH,OUTPUT_FILE_PATH);


    }
    //recursive directory grabbing -r DIR path
    else if(argsList.contains("-r"))
    {


        INPUT_FILE_PATH = args[argsList.indexOf("-r") + 1];

        System.out.println(INPUT_FILE_PATH);
        File folder = new File(INPUT_FILE_PATH);
        FileFilter filter = new FileFilter() {

                public boolean accept(File f)
                {
                    return f.getName().endsWith("gz");
                }
            };
        File[] files = folder.listFiles(filter);
        File[] finishedFiles = null;
        int count = 0;
        if(continueMode)
        {
          FileList fileList = loadFileList();
          finishedFiles = fileList.getFiles();
          count = fileList.getCount();
        }
        else
        {
          finishedFiles = new File[files.length];
          count = 0;
        }



        for (File file : files)
      {
          saveFileList(finishedFiles,count);
          if(count == 0)
          {
            System.out.println(file);
            String fileName = file.toString();
            System.out.println(fileName);
            OUTPUT_FILE_PATH = fileName.substring(0,fileName.length() - 3);
            File tempFile = new File(OUTPUT_FILE_PATH);
            handleFile(fileName,OUTPUT_FILE_PATH);
            if (!stateFile.delete()) {
              System.out.println("State file not deleted");
            }

            if (!tempFile.delete()) {
              System.out.println("Temp file not deleted");
            }
            //System.out.println("DELETED TEMP AND STATE FILE");
            finishedFiles[count] = file;
            saveFileList(finishedFiles,count);
            count++;
          }
          else if(count >0)
          {
            if (!foundIn(file,finishedFiles)) {
              System.out.println(file);
              String fileName = file.toString();
              System.out.println(fileName);
              OUTPUT_FILE_PATH = fileName.substring(0,fileName.length() - 3);
              File tempFile = new File(OUTPUT_FILE_PATH);
              handleFile(fileName,OUTPUT_FILE_PATH);
              if (!stateFile.delete()) {
                System.out.println("State file not deleted");
              }

              if (!tempFile.delete()) {
                System.out.println("Temp file not deleted");
              }
              //System.out.println("DELETED TEMP AND STATE FILE");
              finishedFiles[count] = file;
              count++;
              saveFileList(finishedFiles,count);

            }
          }

      }
      System.out.println("ENDED DIR RECURSIVE FUNCTION");

    }
    else
    {
      System.out.println("need switch -o for input file");
    }

System.out.println("ENDED MAIN FUNCTION");
  }

  public static Boolean foundIn(File file,File[] fileList)
  {
    Boolean doesContain = false;
    for (File tag : fileList)
    {
        if (tag != null) {
          if (file.toString() == tag.toString()) {
            return  true;

          }
        }


    }
    return doesContain;
  }
  public static void handleFile(String inputPath, String outputPath)
  {
    File tmpFile = new File("state.tmp");
    String file = "";
    String internal_file = "";
    String lineOffset = "";
    Boolean continueMode = tmpFile.exists();

    if(continueMode)
    {
      System.out.println("continueMode engaged");
      file  = Utility.getState()[0] + ".gz";
      internal_file = Utility.getState()[1];
      lineOffset = Utility.getState()[2];

    }
    int continueMarker = 0;

    OUTPUT_FILE_PATH = outputPath;
    if(continueMode)
    {
      INPUT_FILE_PATH = file;

    }
    else
    {
      INPUT_FILE_PATH = inputPath;
    }


    inputFile = new File(INPUT_FILE_PATH);

    tarFile = new File(OUTPUT_FILE_PATH);

    tarFile = GetFile.deCompressGZipFile(inputFile,tarFile);

    tis = GetFile.getTarArchiveStream(tarFile);
    tarEntryArray = GetFile.getEntries(tis);
    if(continueMode)
    {
      for (int i = 0 ; i < tarEntryArray.length; i++)
      {
        System.out.println(tarEntryArray[i].getName());
        if (tarEntryArray[i].getName() == internal_file)
        {
          continueMarker = i;
        }

      }
    }
    entryStartOffset = 0;
    if (continueMode) {

      for (int i = continueMarker; i < tarEntryArray.length; i++)
      {

        entryName = tarEntryArray[i].getName();
        System.out.println(entryName);
        entryStartOffset = Integer.parseInt(lineOffset);
        System.out.println(entryStartOffset);

        ParseFile.readTarEntry(tarFile,entryStartOffset,tarEntryArray[i],mariadb);
      }

    }
    else {
      for (int i = 0; i < tarEntryArray.length; i++)
      {
        System.out.println("GOT HERE");
        entryName = tarEntryArray[i].getName();
        System.out.println(entryName);
        System.out.println(tarFile);
        entryStartOffset = ParseFile.findTarEntry(tarFile,entryName);
        System.out.println(entryStartOffset);

        ParseFile.readTarEntry(tarFile,entryStartOffset,tarEntryArray[i],mariadb);
      }
    }
    entryStartOffset = 0;
    for (int i = 0; i < tarEntryArray.length; i++)
    {
      System.out.println("GOT HERE");
      entryName = tarEntryArray[i].getName();
      System.out.println(entryName);
      entryStartOffset = ParseFile.findTarEntry(tarFile,entryName);
      System.out.println(entryStartOffset);

      ParseFile.readTarEntry(tarFile,entryStartOffset,tarEntryArray[i],mariadb);
    }
  }
  public static void saveFileList(File[] files, int count)
  {
    String filename = "filelist.bin";
    FileList fileList = new FileList(files,count);
    FileOutputStream fos = null;
    ObjectOutputStream out = null;
        try {
            fos = new FileOutputStream(filename);
            out = new ObjectOutputStream(fos);
            out.writeObject(fileList);

            out.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

  }
  public static FileList loadFileList()
  {
    String filename = "filelist.bin";
    FileList fileList = null;
    FileInputStream fis = null;
    ObjectInputStream in = null;
    try {
        fis = new FileInputStream(filename);
        in = new ObjectInputStream(fis);
        fileList = (FileList) in.readObject();
        in.close();
    } catch (Exception ex) {ex.printStackTrace();}
    return fileList;
  }
  public static DatabaseHandlerMariaDB configureDatabase()
  {
    Utility.setupDB(0);
    mariadb = new DatabaseHandlerMariaDB();
    mariadb.connect("database.properties");
    return mariadb;
  }
  public static DatabaseHandlerMariaDB configureWebDatabase()
  {
    Utility.setupDB(1);
    mariadb = new DatabaseHandlerMariaDB();
    mariadb.connect("database_web.properties");
    return mariadb;
  }
}
