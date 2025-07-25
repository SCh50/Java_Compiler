import org.fife.ui.rsyntaxtextarea.RSyntaxTextArea;
import org.fife.ui.rsyntaxtextarea.SyntaxConstants;
import org.fife.ui.rtextarea.RTextScrollPane;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.filechooser.FileNameExtensionFilter;
import javax.tools.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Editor {
    final static int FONT_SIZE = 25;
    JFrame frame;
    RSyntaxTextArea codeArea;

    JMenuBar createFileMenu() {
        JMenuBar menu = new JMenuBar();
        JMenu fileMenu = new JMenu("File");

        JMenuItem newItem = new JMenuItem("New");
        newItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.setText("");
            }
        });

        JMenuItem openItem = new JMenuItem("Open");
        openItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser("./examples");
                chooser.setFileFilter(new FileNameExtensionFilter(
                        "Java source file (.java)", "java"));
                int returnVal = chooser.showOpenDialog(null);
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                codeArea.setText(readEntireFile(chooser.getSelectedFile().toPath()));
            }
        });

        JMenuItem saveAsItem = new JMenuItem("Save As");
        saveAsItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser("./examples");
                chooser.setFileFilter(new FileNameExtensionFilter(
                       "Java source file (.java)", ".java"));
                String mainClass = findMainClass(codeArea.getText());
                chooser.setSelectedFile(new File(mainClass == null? "Main.java": mainClass + ".java"));
                int returnVal = chooser.showSaveDialog(null);
                if (returnVal != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                writeEntireFile(chooser.getSelectedFile().toPath());
            }
        });

        JMenuItem quitItem = new JMenuItem("Quit");
        quitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                frame.dispose();
            }
        });

        fileMenu.add(newItem);
        fileMenu.add(openItem);
        fileMenu.add(saveAsItem);
        fileMenu.add(quitItem);
        menu.add(fileMenu);
        return menu;
    }

    private String readEntireFile(Path filepath) {
        try {
            return Files.readString(filepath);
        } catch (IOException e) {
            e.printStackTrace();
            return "Could not read selected file!";
        }
    }

    private void writeEntireFile(Path filepath) {
        try {
            Files.writeString(filepath, codeArea.getText(), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    JMenuBar createEditMenu() {
        JMenuBar menu = new JMenuBar();
        JMenu editMenu = new JMenu("Edit");
        JMenuItem cutItem = new JMenuItem("Cut");
        cutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(codeArea.getSelectedText()), null);
                codeArea.replaceSelection("");
            }
        });

        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(new StringSelection(codeArea.getSelectedText()), null);
            }
        });

        JMenuItem pasteItem = new JMenuItem("Paste");
        pasteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                try {
                    Object clipboardData = clipboard.getContents(codeArea).getTransferData(DataFlavor.stringFlavor);
                    codeArea.replaceSelection(clipboardData == null? "" : clipboardData.toString());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });


        JMenuItem deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                codeArea.replaceSelection("");
            }
        });

        JMenuItem findItem = new JMenuItem("Find");
        findItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                FindBox fb = new FindBox(codeArea);
                fb.setVisible(true);
            }
        });

        JMenuItem fontItem = new JMenuItem("Font");
        fontItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                FontBox fb = new FontBox(codeArea);
                fb.setVisible(true);
            }
        });

        editMenu.add(cutItem);
        editMenu.add(copyItem);
        editMenu.add(pasteItem);
        editMenu.add(deleteItem);
        editMenu.add(findItem);
        editMenu.add(fontItem);
        menu.add(editMenu);
        return menu;
    }

    public void createAndShowGUI(String defaultCode) throws IOException {
        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
        frame = new JFrame("JVCODE");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(dim.width,dim.height);

        // Text area for code input
        codeArea = new RSyntaxTextArea();
        codeArea.setSyntaxEditingStyle(SyntaxConstants.SYNTAX_STYLE_JAVA);
        codeArea.setText(defaultCode);
        codeArea.setFont(new Font("JetBrains Mono", Font.PLAIN, FONT_SIZE));

        RTextScrollPane codeScrollPane = new RTextScrollPane(codeArea);
        codeScrollPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, FONT_SIZE));

        // Text area for compilation output
        JTextArea outputArea = new JTextArea(10, 60);
        outputArea.setFont(new Font("JetBrains Mono", Font.PLAIN, FONT_SIZE));
        outputArea.setEditable(false);
        JScrollPane outputScrollPane = new JScrollPane(outputArea);

        // Button for compilation
        JButton compileButton = new JButton("Compile and Run");
        compileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String code = codeArea.getText();
                String mainClass = findMainClass(code);
                if (mainClass == null) {
                    outputArea.append("Error: No main class found\n");
                    return;
                }
                outputArea.setText("");
                compileAndRun(mainClass, code, outputArea);
            }
        });

        JMenuBar menubar = new JMenuBar();
        menubar.add(createFileMenu());
        menubar.add(createEditMenu());
        JPanel buttons = new JPanel(new BorderLayout());
        buttons.add(compileButton, BorderLayout.EAST);
        buttons.add(menubar, BorderLayout.WEST);

        JPanel workspace = new JPanel(new GridLayout(1, 2));
        workspace.add(codeScrollPane);
        workspace.add(outputScrollPane);

        JPanel panel = new JPanel(new BorderLayout());
        panel.add(buttons, BorderLayout.NORTH);
        panel.add(workspace, BorderLayout.CENTER);

        frame.add(panel);
        frame.setVisible(true);
        frame.setExtendedState(frame.getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    private static void runCode(String name, JTextArea outputArea) {
        try {
            Process process = new ProcessBuilder("java", name).start();
            process.waitFor();
            outputArea.append(new String(process.getInputStream().readAllBytes()));
            outputArea.append(new String(process.getErrorStream().readAllBytes()));
        } catch (Exception e) {
            outputArea.append("Execution error: " + e.getMessage());
        }
    }

    // find name of main class in file
    private static String findMainClass(String code) {
        Pattern mainClass = Pattern.compile(".*public\\s+class\\s+([_$A-Za-z][_$A-Za-z0-9]+).*");
        Matcher matcher = mainClass.matcher(code);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }


    private void compileAndRun(String name, String code, JTextArea outputArea) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        JavaFileObject file = new JavaSourceFromString(name, code);
        Iterable<JavaFileObject> compilationUnits = List.of(file);

        // Set up compilation task
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();
        JavaCompiler.CompilationTask task = compiler.getTask(
                null, null, diagnostics, null, null, compilationUnits);
        // Compile the code
        boolean success = task.call();

        if (success) {
            outputArea.append("Compilation successful.\n");
            runCode(name, outputArea);
        } else {
            for (var diagnostic : diagnostics.getDiagnostics()) {
                outputArea.append(diagnostic.toString() + "\n");
            }
        }
    }


    // Helper class to define in-memory Java file
    private static class JavaSourceFromString extends SimpleJavaFileObject {
        final String code;

        JavaSourceFromString(String name, String code) {
            super(java.net.URI.create("string:///" + name.replace('.', '/')
                    + JavaFileObject.Kind.SOURCE.extension), JavaFileObject.Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}


