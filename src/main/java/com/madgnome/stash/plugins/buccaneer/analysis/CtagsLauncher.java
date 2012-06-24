package com.madgnome.stash.plugins.buccaneer.analysis;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CtagsLauncher
{
  private final static Logger logger = LoggerFactory.getLogger(CtagsLauncher.class);

  private static final String CTAGS_FILTER_TERMINATOR = "__ctags_done_with_file__";

  private Process ctags;
  private OutputStreamWriter ctagsIn;
  private BufferedReader ctagsOut;
  private ProcessBuilder processBuilder;

  public void doCtags(File directory)
  {

  }

  public void doCtags(InputStream stream) throws IOException
  {
    boolean ctagsRunning = false;
    if (ctags != null)
    {
      try
      {
        ctags.exitValue();
        ctagsRunning = false;
        // ctags is dead! we must restart!!!
      } catch (IllegalThreadStateException exp)
      {
        ctagsRunning = true;
        // ctags is still running :)
      }
    }

    if (!ctagsRunning) {
      initialize();
    }

//    Definitions ret = null;
//    if (file.length() > 0 && !"\n".equals(file)) {
//      //log.fine("doing >" + file + "<");
//      ctagsIn.write(file);
//      ctagsIn.flush();
//      ret = new Definitions();
//      readTags(ret);
//    }
//
//    return ret;
  }

//  private Process buildProcess()
//  {
//    List<String> command = new ArrayList<String>();
//    command.add("ctags.exe");
//    command.add("-R");
//    command.add("--c-kinds=+l");
//    command.add("--java-kinds=+l");
//    command.add("--sql-kinds=+l");
//    command.add("--Fortran-kinds=+L");
//    command.add("--C++-kinds=+l");
//    command.add("--fields=-anf+iKnS");
//
//    ProcessBuilder processBuilder = new ProcessBuilder(command);
//
//  }

  private void initialize() throws IOException
  {
    if (processBuilder == null) {
      List<String> command = new ArrayList<String>();
      command.add("ctags.exe");
      command.add("-R");
      command.add("--c-kinds=+l");
      command.add("--java-kinds=+l");
      command.add("--sql-kinds=+l");
      command.add("--Fortran-kinds=+L");
      command.add("--C++-kinds=+l");
//      command.add("--file-scope=yes");
//      command.add("-u");
//      command.add("--filter=yes");
//      command.add("--filter-terminator=" + CTAGS_FILTER_TERMINATOR + "\n");
      command.add("--fields=-anf+iKnS");
//      command.add("--excmd=pattern");
//      command.add("--langmap=sh:+.kshlib"); // RFE #17849
//      command.add("--regex-Asm=/^[ \\t]*(ENTRY|ENTRY2|ALTENTRY)[ \\t]*\\(([a-zA-Z0-9_]+)/\\2/f,function/");  // for assmebly definitions
      processBuilder = new ProcessBuilder(command);
    }

    ctags = processBuilder.start();
    ctagsIn = new OutputStreamWriter(ctags.getOutputStream());
    ctagsOut = new BufferedReader(new InputStreamReader(ctags.getInputStream()));

    createErrorThread();
  }

  private void createErrorThread()
  {
    final BufferedReader error = new BufferedReader(new InputStreamReader(ctags.getErrorStream()));

    Thread errThread = new Thread(new Runnable() {

      public void run() {
        StringBuilder sb = new StringBuilder();
        try
        {
          String s;
          while ((s = error.readLine()) != null)
          {
            sb.append(s);
            sb.append('\n');
          }
        }
        catch (IOException exp)
        {
          logger.warn("Got an exception reading ctags error stream: ", exp);
        }
        finally
        {
          try
          {
            error.close();
          }
          catch (IOException e)
          {
            logger.warn("Failed to close resource: ", e);
          }
        }

        if (sb.length() > 0) {
          logger.warn("Error from ctags: " + sb.toString());
        }
      }
    });

    errThread.setDaemon(true);
    errThread.start();
  }
}
