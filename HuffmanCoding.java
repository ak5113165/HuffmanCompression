package HuffmanCompression;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

class HuffmanNode {
    char ch;
    int freq;
    HuffmanNode left, right;

    HuffmanNode(char ch, int freq) {
        this.ch = ch;
        this.freq = freq;
    }
}

class HuffmanComparator implements Comparator<HuffmanNode> {
    public int compare(HuffmanNode a, HuffmanNode b) {
        return a.freq - b.freq;
    }
}

class HuffmanCompression {
    private Map<Character, String> huffmanCodes = new HashMap<>();
    private HuffmanNode root;

    public void buildTree(String text) {
        Map<Character, Integer> freqMap = new HashMap<>();
        for (char c : text.toCharArray()) {
            freqMap.put(c, freqMap.getOrDefault(c, 0) + 1);
        }

        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>(new HuffmanComparator());
        for (Map.Entry<Character, Integer> entry : freqMap.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            HuffmanNode parent = new HuffmanNode('\0', left.freq + right.freq);
            parent.left = left;
            parent.right = right;
            pq.add(parent);
        }

        root = pq.poll();
        generateCodes(root, "");
    }

    private void generateCodes(HuffmanNode node, String code) {
        if (node == null) return;
        if (node.left == null && node.right == null) {
            huffmanCodes.put(node.ch, code);
        }
        generateCodes(node.left, code + "0");
        generateCodes(node.right, code + "1");
    }

    public String encode(String text) {
        StringBuilder encoded = new StringBuilder();
        for (char c : text.toCharArray()) {
            encoded.append(huffmanCodes.get(c));
        }
        return encoded.toString();
    }

    public String decode(String encodedText) {
        StringBuilder decoded = new StringBuilder();
        HuffmanNode current = root;

        for (char bit : encodedText.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;

            if (current.left == null && current.right == null) {
                decoded.append(current.ch);
                current = root;
            }
        }
        return decoded.toString();
    }

}

public class HuffmanCoding extends JFrame {
    private  JTextArea inputArea;
    private  JTextArea outputArea;
    private HuffmanCompression huffman;

    public HuffmanCoding() {
        setTitle("Huffman Compression Tool");
        setSize(700, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        huffman = new HuffmanCompression();

        inputArea = new JTextArea();
        outputArea = new JTextArea();
        outputArea.setEditable(false);

        JPanel panel = new JPanel(new GridLayout(1, 2));
        panel.add(new JScrollPane(inputArea));
        panel.add(new JScrollPane(outputArea));

        JPanel buttonPanel = new JPanel();

        JButton compressBtn = new JButton("Compress");
        JButton decompressBtn = new JButton("Decompress");

        buttonPanel.add(compressBtn);
        buttonPanel.add(decompressBtn);

        add(panel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        compressBtn.addActionListener(e -> compress());
        decompressBtn.addActionListener(e -> decompress());
    }

    private void compress() {
        String text = inputArea.getText();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter text to compress");
            return;
        }

        huffman.buildTree(text);
        String encoded = huffman.encode(text);
        outputArea.setText(encoded);
    }

    private void decompress() {
        String encoded = inputArea.getText();
        if (encoded.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter binary data to decompress");
            return;
        }

        String decoded = huffman.decode(encoded);
        outputArea.setText(decoded);
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HuffmanCoding ui = new HuffmanCoding();
            ui.setVisible(true);
        });
    }


}


