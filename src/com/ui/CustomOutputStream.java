package com.ui;

import java.io.IOException;
import java.io.OutputStream;
 
import javax.swing.JTextArea;
 
/**
 * This class extends from OutputStream to redirect output to a JTextArrea
 * @author www.codejava.net
 *
 */
public class CustomOutputStream extends OutputStream {
    private JTextArea jTextArea;
     
    public CustomOutputStream(JTextArea jTextArea) {
        this.jTextArea = jTextArea;
    }
     
    @Override
    public void write(int b) throws IOException {
        // redirects data to the text area
        jTextArea.append(String.valueOf((char)b));
        // scrolls the text area to the end of data
        jTextArea.setCaretPosition(jTextArea.getDocument().getLength());
    }
}
