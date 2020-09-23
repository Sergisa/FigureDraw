import javax.swing.*;
import java.awt.*;

public class form extends JFrame {
    private JPanel rootPanel;
    private Canvas canvas;
    public form(){
        canvas = new Canvas();
        canvas.setSize(new Dimension(500, 500));
        add(canvas);
        pack();
        //setSize(new Dimension(500,500));
        //setContentPane(rootPanel);
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }
}
