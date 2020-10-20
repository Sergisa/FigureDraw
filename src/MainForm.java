import javax.swing.*;
import java.awt.*;

public class MainForm extends JFrame {
    private JPanel rootPanel;
    private JButton button1;

    public MainForm(){


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
