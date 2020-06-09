package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileHandler {
  private BufferedReader reader; //Flujo de lectura
  private BufferedWriter writer;

  //Constructor sin apertura de archivo.
  public FileHandler() {
    reader = null;
    writer = null;
  }

  //Abrir archivo para Lectura. Recibe el nombre del archivo e intenta abrirlo para lectura.
  //Retorna <true> si lo pudo abrir, <false> si no.
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
    else {
      try {
        writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "UTF-8"));
        return true;
      }
      catch (IOException ioe) {
        return false;
      }
    }
  }

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

  //Leer Línea. Lee una línea del archivo de texto. Retorna la cadena o null,
  //según la función readLine(). Lanza la excepción IOException en caso de error.
  public String readline() throws IOException {
    return reader.readLine();
  }

  public String read() throws IOException {
    StringBuilder s = new StringBuilder();
    String aux = reader.readLine();
    while (aux != null) {
      s.append(aux + "\n");
      aux = reader.readLine();
    }
    
    return s.toString();
  }

  //Escribir Línea. Recibe un String y lo escribe al archivo, seguido de un salto de Línea
  //(line feed). Lanza una excepción IOException en caso de error.
  public void write(String line) throws IOException {
    writer.write(line);
    writer.newLine();
    writer.flush();
  }
}
