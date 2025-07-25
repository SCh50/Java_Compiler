import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

public class FontBox extends JFrame {
    public FontBox(RSyntaxTextArea codeArea) {
        super();
        setTitle("Set Font");
        setBounds(100, 100, 450, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.LEFT));

        JTextField selectedFont = new JTextField("Choose a font", 30);

        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        List<String> fontFamilies = Arrays.stream(ge.getAvailableFontFamilyNames()).toList();

        FilterComboBox fontChooser = new FilterComboBox(fontFamilies);
        fontChooser.setSelectedItem("JetBrains Mono");

        fontChooser.setEditable(true);
        fontChooser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                selectedFont.setText((String)fontChooser.getSelectedItem());
            }
        });

        JSpinner fontSizeChooser = new JSpinner();
        fontSizeChooser.setModel(new SpinnerNumberModel(25,1,100,1));

        JButton submit = new JButton("Submit");
        submit.addActionListener(new ActionListener() {
           @Override
           public void actionPerformed(ActionEvent actionEvent) {
                codeArea.setFont(new Font(fontChooser.getSelectedItem().toString(), Font.PLAIN, (Integer) fontSizeChooser.getValue()));
           }
        });

        add(fontChooser);
        add(selectedFont);
        add(fontSizeChooser);
        add(submit);
    }
}