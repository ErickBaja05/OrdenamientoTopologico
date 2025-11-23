import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import src.*;

public class Main
{
    public static void main(String[] args)
    {
        Pantalla p = new Pantalla();
        
        p.presentar();
        try
        {
            Scanner scanner = new Scanner(new File("csv/materias.csv"));
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] fields = line.split(",");
                p.agregarNodo(new Curso(fields[0], fields[1], Integer.valueOf(fields[2]), Integer.valueOf(fields[3])));
            }
            scanner.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        p.recargarNodos();
        try
        {
            Scanner scanner = new Scanner(new File("csv/prerequisitos.csv"));
            scanner.nextLine();
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] fields = line.split(",");
                p.agregarArista(fields[0], fields[1]);
            }
            scanner.close();
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        
        p.repaint();
    }
}
