package src;

import javax.swing.*;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.Color;
import java.awt.Font;

public class Titulo extends JTextPane
{
    final Color bgDefecto = new Color(244, 244, 244);
    final Color fgDefecto = Color.BLACK;
    public Titulo(String s, int x, int y, int ancho, int alto)
    {
        super();
        configurar(s, x, y, ancho, alto, bgDefecto, fgDefecto);
    }
    public Titulo(String s, int x, int y, int ancho, int alto, Color bg)
    {
        super();
        configurar(s, x, y, ancho, alto, bg, fgDefecto);
    }

    public Titulo(int valor, int x, int y, int ancho, int alto)
    {
        super();
        configurar(String.valueOf(valor), x, y, ancho, alto, bgDefecto, fgDefecto);
    }
    public Titulo(int valor, int x, int y, int ancho, int alto, Color bg)
    {
        super();
        configurar(String.valueOf(valor), x, y, ancho, alto, bg, fgDefecto);
    }

    public Titulo(String s, int x, int y, int ancho, int alto, Color bg, Color fg)
    {
        super();
        configurar(s, x, y, ancho, alto, bg, fg);
    }

    public void configurar(String s, int x, int y, int ancho, int alto, Color bg, Color fg)
    {
        setEditable(false);
        setText(s);
        setBounds(x, y, ancho, alto);

        StyledDocument doc = getStyledDocument();
        SimpleAttributeSet center = new SimpleAttributeSet();
        StyleConstants.setAlignment(center, StyleConstants.ALIGN_CENTER);
        StyleConstants.setBold(center, true);
        doc.setParagraphAttributes(0, doc.getLength(), center, false);

        try {
            int textHeight = getFontMetrics(getFont()).getHeight() * getText().split("\n").length;
            int availableHeight = getHeight() - getInsets().top - getInsets().bottom;
            int topMargin = Math.max(0, (availableHeight - textHeight) / 2);
                    
            SimpleAttributeSet margin = new SimpleAttributeSet();
            StyleConstants.setSpaceAbove(margin, topMargin);
            doc.setParagraphAttributes(0, doc.getLength(), margin, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        setOpaque(true);
        setBackground(bg);
        setForeground(fg);
        setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
    }
}
