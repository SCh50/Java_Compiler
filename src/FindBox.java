import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class FindBox extends JFrame {
    int lastPos = 0;
    JTextField searchBox;
    JTextField results;
    JButton searchButton;

    public FindBox(RSyntaxTextArea codeArea) {
        super();
        setTitle("Find");
        setBounds(100, 100, 450, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.LEFT));

        searchBox = new JTextField("", 20);
        searchButton = new JButton("Search");
        JButton resetButton = new JButton("Reset Search");
        results = new JTextField("", 20);

        searchBox.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchText(codeArea);
                }
            }
        });

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchText(codeArea);
            }
        });

        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                lastPos = 0;
            }
        });

        add(searchBox);
        add(searchButton);
        add(results);
        add(resetButton);
    }

    private void searchText(RSyntaxTextArea codeArea) {
        String text = codeArea.getText();
        int index = text.indexOf(searchBox.getText(), lastPos);
        if (index == -1) {
            results.setText("No results found");
        } else {
            results.setText("Found matches");
            searchButton.setText("Find Next");
            lastPos = index + searchBox.getText().length();
            codeArea.setCaretPosition(index);
            codeArea.moveCaretPosition(index + searchBox.getText().length());
        }
    }
}
