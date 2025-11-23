package src;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.w3c.dom.events.MouseEvent;

public class Pantalla extends JFrame
{
    final int ancho = 900, alto = 700;
    final int nodoLado = 210;
    final int separacion = 50;
    private int semestre;

    private JPanel lienzo;
    private JPanel menu;

    private Grafo malla;
    private Map<String, JPanel> nodos;
    private Map<String, Curso> codigo2cursos;

    private List<JPanel> cuadros;
    private List<Integer> nivel;
    private List<Point> inicio;
    private List<Point> destino;
    private List<Color> colores;

    public Pantalla()
    {
        super("Ordenamiento Topologico de Malla Curricular");
        setSize(ancho, alto);
        setResizable(false);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        semestre = -1;

        lienzo = new JPanel(null);
        lienzo.setBackground(Color.WHITE);

        menu = new JPanel();
        menu.setLayout(new BoxLayout(menu, BoxLayout.X_AXIS));

        malla = new Grafo();
        nodos = new HashMap<>();
        codigo2cursos = new HashMap<>();

        cuadros = new ArrayList<>();
        nivel = new ArrayList<>();
        inicio = new ArrayList<>();
        destino = new ArrayList<>();
        colores = new ArrayList<>();
    }
    public void agregarNodo(Curso c)
    {
        if(c.semestre > semestre)
        {
            semestre = c.semestre;
            nivel.add(separacion);
        }
        JPanel nodo = new JPanel(null);
        nodo.setBounds(nivel.get(semestre - 1), (nodoLado + separacion) * (c.semestre - 1) + separacion, nodoLado + 2, nodoLado + 2);

        nodo.add(new Titulo("CREDITOS", 1, 1, nodoLado / 2, nodoLado / 7));
        nodo.add(new Titulo("HORAS", nodoLado / 2 + 1, 1, nodoLado / 2, nodoLado / 7));

        nodo.add(new Titulo(c.creditos, 1, nodoLado / 7 + 1, nodoLado / 2, nodoLado / 7));
        nodo.add(new Titulo(c.horas, nodoLado / 2 + 1, nodoLado / 7 + 1, nodoLado / 2, nodoLado / 7));

        nodo.add(new Titulo(c.nombre, 1, (nodoLado / 7) * 2 + 1, nodoLado, (nodoLado / 7) * 4));

        Color bg = (c.codigo.charAt(0) == 'I')? Color.BLUE : Color.YELLOW;
        Color fg = (c.codigo.charAt(0) == 'I')? Color.WHITE : Color.BLACK;
        nodo.add(new Titulo(c.codigo, 1, (nodoLado / 7) * 6 + 1, nodoLado, nodoLado / 7, bg, fg));

        nodo.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        cuadros.add(nodo);

        nodos.put(c.codigo, nodo);
        codigo2cursos.put(c.codigo, c);
        malla.agregarNodo(c.codigo);

        nivel.set(c.semestre-1, nivel.get(semestre-1) + nodoLado + separacion);
        lienzo.setPreferredSize(new java.awt.Dimension(Collections.max(nivel), (nodoLado + separacion) * semestre + separacion));
    }
    public void recargarNodos()
    {
        semestre = -1;
        lienzo.removeAll();
        for(JPanel p : cuadros)
        {
            lienzo.add(p);
        }
    }

    public void agregarArista(String codigo_prereq, String codigo_req)
    {
        malla.agregarArista(codigo_prereq, codigo_req);
        JPanel nodo1 = nodos.get(codigo_prereq);
        Point p1 = SwingUtilities.convertPoint(
            nodo1,
            nodo1.getWidth() / 2,
            nodo1.getHeight(),
            lienzo
        );

        JPanel nodo2 = nodos.get(codigo_req);
            Point p2 = SwingUtilities.convertPoint(
            nodo2,
            nodo2.getWidth() / 2,
            0,
            lienzo
        );
        inicio.add(p1);
        destino.add(p2);
        colores.add(new Color( (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) lienzo.getGraphics().create();;

        for(int i = 0; i < inicio.size(); i++)
        {
            drawBezierArrow(g2, inicio.get(i), destino.get(i), colores.get(i));
        }

        g2.dispose();
    }

    private void drawBezierArrow(Graphics2D g2, Point p1, Point p2, Color col) {

        g2.setStroke(new BasicStroke(3f)); // <-- thick line
        g2.setColor(col);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Point pmedio = new Point((p1.x + p2.x) / 2, (p1.y + p2.y) / 2);
    
        Path2D path = new Path2D.Double();
        path.moveTo(p1.x, p1.y);

        int ctrlX = (p1.x + pmedio.x) / 2;
        int ctrlY = Math.min(p1.y, pmedio.y) + 30;
        path.quadTo(ctrlX, ctrlY, pmedio.x, pmedio.y);

        ctrlX = (pmedio.x + p2.x) / 2;
        ctrlY = Math.min(pmedio.y, p2.y) - 10;
        path.quadTo(ctrlX, ctrlY, p2.x, p2.y);

        // Draw the curved line
        g2.draw(path);

        // ---- Compute tangent angle at the end point (for arrowhead) ----
        // Derivative of quadratic Bézier at t = 1
        double dx = p2.x - ctrlX;
        double dy = p2.y - ctrlY;
        double angle = Math.atan2(dy, dx);

        // ---- Arrowhead size ----
        int arrowSize = 14;

        // Left side of arrowhead
        int xA = (int) (p2.x - arrowSize * Math.cos(angle - Math.PI / 6));
        int yA = (int) (p2.y - arrowSize * Math.sin(angle - Math.PI / 6));

        // Right side of arrowhead
        int xB = (int) (p2.x - arrowSize * Math.cos(angle + Math.PI / 6));
        int yB = (int) (p2.y - arrowSize * Math.sin(angle + Math.PI / 6));

        // Draw arrowhead
        g2.drawLine(p2.x, p2.y, xA, yA);
        g2.drawLine(p2.x, p2.y, xB, yB);
    }

    private void agregarBotones(JPanel m) {
        JButton boton1 = new JButton("Agregar Materia");
        boolean menu1 = false;
        JButton boton2 = new JButton("Definir prerequisito");
        boolean menu2 = false;
        JButton boton3 = new JButton("Obtener orden");
        boolean menu3 = false;

        boton1.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(menu1) return;
                invertir(menu1);
                String[] campos = {"Codigo: ", "Nombre de materia: ", "Creditos: ", "Semestre: "};
                Form f = new Form("NUEVA MATERIA", campos);
                f.setVisible(true);
                f.boton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        boolean completo = true;
                        for(int i = 0; i < campos.length; i++)
                        {
                            if(f.textos.get(i).getText().trim().isEmpty()) completo = false;
                        }
                        if(!completo)
                        {
                            alerta("Campos incompletos");
                            return;
                        }
                        
                        if(nodos.get(f.textos.get(0).getText()) != null)
                        {
                            alerta("Ya existe una materia con ese codigo");
                            return;
                        }
                        try
                        {
                            Integer.parseInt(f.textos.get(2).getText().trim());
                            Integer.parseInt(f.textos.get(3).getText().trim());
                        }
                        catch (NumberFormatException err) {
                            alerta("Creditos y Semestre deben ser numeros enteros");
                            return;
                        }
                        agregarNodo(new Curso(f.textos.get(0).getText(), f.textos.get(1).getText(), Integer.parseInt(f.textos.get(2).getText()), Integer.parseInt(f.textos.get(3).getText())));
                        recargarNodos();
                        confirmar("Materia agregada con exito");
                    }
                });
                f.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        invertir(menu1);
                        // Perform cleanup or confirmation logic here
                        f.dispose(); // Close the frame
                    }
                });
            }
        });
        Pantalla principal = this;
        boton2.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(menu2) return;
                invertir(menu2);
                String[] campos = {"Codigo Prerequisito: ", "Codigo Requisito: "};
                Form f = new Form("AGREGAR DEPENDENCIA", campos);
                f.setVisible(true);
                f.boton.addActionListener(new ActionListener()
                {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        boolean completo = true;
                        for(int i = 0; i < campos.length; i++)
                        {
                            if(f.textos.get(i).getText().trim().isEmpty()) completo = false;
                        }
                        if(!completo)
                        {
                            alerta("Campos incompletos");
                            return;
                        }
                        for(int i =0; i < campos.length; i++)
                        {
                            if(nodos.get(f.textos.get(i).getText()) == null)
                            {
                                alerta("No existe materia con codigo " + f.textos.get(i).getText());
                                return;
                            }
                        }
                        agregarArista(f.textos.get(0).getText(), f.textos.get(1).getText());
                        principal.repaint();
                        confirmar("Requisito agregada con exito");
                    }
                });
                f.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        invertir(menu2);
                        // Perform cleanup or confirmation logic here
                        f.dispose(); // Close the frame
                    }
                });
            }
        });

        boton3.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(menu3) return;
                malla.resetearGrados();
                List<String> orden = malla.algoritmoKahn();
                if(orden == null)
                {
                    alerta("Existen ciclos, no se puede ordenar");
                }else{
                    invertir(menu3);
                    PantallaOrdenada porden = new PantallaOrdenada(orden, codigo2cursos, malla);
                    porden.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        invertir(menu3);
                        // Perform cleanup or confirmation logic here
                        porden.dispose(); // Close the frame
                    }
                });
                    porden.presentar();
                }
            }
        });

        m.add(boton1);
        m.add(boton2);
        m.add(boton3);
    }
    private void invertir(Boolean b)
    {
        b = !b;
    }
    private void alerta(String mensaje)
    {
        JOptionPane.showMessageDialog(this,mensaje,"Warning",JOptionPane.WARNING_MESSAGE);
    }
    private void confirmar(String mensaje)
    {
        JOptionPane.showMessageDialog(this,mensaje,"OK",JOptionPane.INFORMATION_MESSAGE);
    }

    public void presentar()
    {
        JScrollPane panelScroll = new JScrollPane(lienzo);
        panelScroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        panelScroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        panelScroll.getVerticalScrollBar().setUnitIncrement(separacion);
        JScrollBar barraVert = panelScroll.getVerticalScrollBar();
        barraVert.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                repaint();
            }
        });
        JScrollBar barraHoriz = panelScroll.getHorizontalScrollBar();
        barraHoriz.getModel().addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                repaint();
            }
        });
        add(panelScroll, BorderLayout.CENTER);

        agregarBotones(menu);
        add(menu, BorderLayout.SOUTH);
        setVisible(true);
    }
}
