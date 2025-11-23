package src;

public class Curso
{
    public String codigo;
    public String nombre;
    public int creditos;
    public int horas;
    public int semestre;
    
    public Curso(String cod, String nom, int cred, int sem)
    {
        codigo = cod;
        nombre = nom;
        creditos = cred;
        horas = cred * 48;
        semestre = sem;
    }
    @Override
    public String toString()
    {
        return nombre + "(" + codigo + ")" + ", creditos: " + creditos + ", horas: " + horas;
    }
}