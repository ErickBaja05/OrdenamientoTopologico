package src;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.geom.Path2D;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PantallaOrdenada extends JFrame {
    final int ancho = 900, alto = 700;
    final int nodoLado = 210;
    final int separacion = 50;
    private int siguienteX;

    private JPanel lienzo;
    private Map<String, JPanel> nodos;
    private Map<String, Curso> cursos;

    private Grafo malla;

    private List<Point> inicio;
    private List<Point> destino;
    private List<Color> colores;
    private List<Integer> curvatura;

    private int alternador = 0;

    public PantallaOrdenada(List<String> orden, Map<String, Curso> cur, Grafo g)
    {
        super();
        setSize(ancho, alto);
        setResizable(false);

        lienzo = new JPanel(null);
        lienzo.setBackground(Color.WHITE);

        siguienteX = separacion;
        nodos = new HashMap<>();
        cursos = cur;
        malla = g;

        inicio = new ArrayList<>();
        destino = new ArrayList<>();
        colores = new ArrayList<>();
        curvatura = new ArrayList<>();

        for(String s : orden)
        {
            agregarNodo(cursos.get(s));
        }
        for(Grafo.Arista a : malla.obtenerAristas())
        {
            agregarArista(a.desde, a.hasta);
        }
    }

    public void agregarNodo(Curso c)
    {
        JPanel nodo = new JPanel(null);
        nodo.setBounds(siguienteX, nodoLado + separacion, nodoLado + 2, nodoLado + 2);

        nodo.add(new Titulo("CREDITOS", 1, 1, nodoLado / 2, nodoLado / 7));
        nodo.add(new Titulo("HORAS", nodoLado / 2 + 1, 1, nodoLado / 2, nodoLado / 7));

        nodo.add(new Titulo(c.creditos, 1, nodoLado / 7 + 1, nodoLado / 2, nodoLado / 7));
        nodo.add(new Titulo(c.horas, nodoLado / 2 + 1, nodoLado / 7 + 1, nodoLado / 2, nodoLado / 7));

        nodo.add(new Titulo(c.nombre, 1, (nodoLado / 7) * 2 + 1, nodoLado, (nodoLado / 7) * 4));

        Color bg = (c.codigo.charAt(0) == 'I')? Color.BLUE : Color.YELLOW;
        Color fg = (c.codigo.charAt(0) == 'I')? Color.WHITE : Color.BLACK;
        nodo.add(new Titulo(c.codigo, 1, (nodoLado / 7) * 6 + 1, nodoLado, nodoLado / 7, bg, fg));

        nodo.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));

        siguienteX += nodoLado + separacion;
        lienzo.add(nodo);
        nodos.put(c.codigo, nodo);

        lienzo.setPreferredSize(new java.awt.Dimension(siguienteX, nodoLado + separacion));
    }

    public void agregarArista(String codigo_prereq, String codigo_req)
    {
        malla.agregarArista(codigo_prereq, codigo_req);
        JPanel nodo1 = nodos.get(codigo_prereq);
        Point p1 = SwingUtilities.convertPoint(
            nodo1,
            nodo1.getWidth() / 2,
            alternador % 2 == 0 ? 0 : nodo1.getHeight(),
            lienzo
        );

        JPanel nodo2 = nodos.get(codigo_req);
            Point p2 = SwingUtilities.convertPoint(
            nodo2,
            nodo2.getWidth() / 2,
            alternador % 2 == 0 ? 0 : nodo2.getHeight(),
            lienzo
        );
        
        inicio.add(p1);
        destino.add(p2);
        colores.add(new Color( (int) (Math.random() * 255), (int) (Math.random() * 255), (int) (Math.random() * 255)));
        curvatura.add(alternador % 2 == 0 ? -60 : 60);

        alternador++;

    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        Graphics2D g2 = (Graphics2D) lienzo.getGraphics().create();;

        for(int i = 0; i < inicio.size(); i++)
        {
            drawBezierArrow(g2, inicio.get(i), destino.get(i), colores.get(i), curvatura.get(i));
        }

        g2.dispose();
    }

    private void drawBezierArrow(Graphics2D g2, Point p1, Point p2, Color col, Integer curv) {

        g2.setStroke(new BasicStroke(3f)); // <-- thick line
        g2.setColor(col);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    
        Path2D path = new Path2D.Double();
        path.moveTo(p1.x, p1.y);

        int ctrlX = (p1.x + p2.x) / 2;
        int ctrlY = Math.min(p1.y, p2.y) - 60;
        if(curv == 60)
        {
            ctrlY = Math.max(p1.y, p2.y) + 60;
        }
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
        add(panelScroll);
        SwingUtilities.invokeLater(() -> {
            panelScroll.getViewport().setViewPosition(new Point(0, 0));
        });
        setVisible(true);
    }
}
