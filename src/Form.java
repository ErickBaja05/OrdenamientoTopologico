package src;

import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class Form extends JFrame
{
    List<JTextField> textos;
    JButton boton;

    public Form(String titulo, String[] campos)
    {
        super(titulo);
        setSize(400, 400);
        setResizable(false);
        setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));

        textos = new ArrayList<>();

        for(String s : campos)
        {
            
            JPanel seccion = new JPanel();
            seccion.setLayout(new BoxLayout(seccion, BoxLayout.X_AXIS));
            seccion.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

            JLabel campo = new JLabel(s);
            campo.setVisible(true);

            JTextField texto = new JTextField();
            texto.setVisible(true);

            seccion.add(campo);
            seccion.add(texto);
            textos.add(texto);
            seccion.setVisible(true);


            add(seccion);
        }
        boton = new JButton(titulo);
        add(boton);
    }
}
