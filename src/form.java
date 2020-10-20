import javax.swing.*;
import java.awt.*;

public class form extends JFrame {
    private JPanel rootPanel;

    public form(){


        rootPanel = new DrawPanel();
        //rootPanel.paintComponents();
        DrawPanel drawPanel = new DrawPanel();
        add(drawPanel);
        setSize(new Dimension(500,500));
        setLocationRelativeTo(null);
        //setContentPane(rootPanel);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }
}
