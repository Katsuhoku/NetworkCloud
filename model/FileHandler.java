package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * A class for reading and writting text files. FileHandler can do both readings
 * and writtings, but only one at time. The open mode will determine wich one
 * will be possible to do. This class is designed to read and write files only
 * in text mode. It cannot manage binary files.
 */

public class FileHandler {
  /**
   * File reading text buffer.
   */
  private BufferedReader reader;

  /**
   * File writting text buffer
   */
  private BufferedWriter writer;

  /**
   * Constructs an abstract FileHandler. The file path is not determined yet.
   */
  public FileHandler() {
    reader = null;
    writer = null;
  }

  /**
   * Opens the file with the specified filename in the specified mode. Mode can be <code>r</code>
   * for reading operations, or <code>w</code> for writting. Other input will do nothing and return.
   * @param filename the filename, relative or absolute path included, of the file.
   * @param mode <code>r</code> for reading. <code>w</code> for writting.
   * @return <code>true</code> if could open the file, <code>false</code> otherwise.
   */
  public boolean open(String filename, String mode) {
    if (mode.equals("r")) {
      try {
        reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));
        return true;
      }
      catch (IOException ioe) {
        return false;
      }
    }
    else if (mode.equals("w")) {
      try {
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
        return true;
      }
      catch (IOException ioe) {
        return false;
      }
    }
    else return false;
  }

  /**
   * Closes the open file, if there was an opened file.
   * @return <code>true</code> if could close the file, <code>false</code> otherwise.
   */
  public boolean close() {
    try {
      if (reader != null) reader.close();
      else writer.close();
      return true;
    }
    catch (IOException ioe) {
      return false;
    }
  }

  /**
   * Reads the next text line in the file.
   * @return the string obtained, or <code>null</code> if reached the end of file.
   * @throws IOException - if there's a problem with the file during reading.
   */
  public String readline() throws IOException {
    return reader.readLine();
  }

  /**
   * Reads all the content in the text file.
   * @return the string generated with all the text in the file.
   * @throws IOException - if there's a problem with the file during reading.
   */
  public String read() throws IOException {
    StringBuilder s = new StringBuilder();
    String aux = reader.readLine();
    while (aux != null) {
      s.append(aux + "\n");
      aux = reader.readLine();
    }
    
    return s.toString();
  }

  /**
   * Writes the specified string. It can contain line feeds.
   * @param line the string to write to the file.
   * @throws IOException - if there's a problem with the file during writting.
   */
  public void write(String line) throws IOException {
    writer.write(line);
    writer.flush();
  }
}
