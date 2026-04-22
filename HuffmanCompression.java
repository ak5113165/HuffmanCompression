import java.util.*;
import java.io.Serializable;

public class HuffmanCompression {

    // Nested node so this class compiles and runs on its own
    static final class HuffmanNode implements Comparable<HuffmanNode>, Serializable {
        private static final long serialVersionUID = 1L;
        char symbol;
        int frequency;
        HuffmanNode left, right;
        HuffmanNode(char s, int f) { symbol = s; frequency = f; }
        @Override
        public int compareTo(HuffmanNode o) { return Integer.compare(this.frequency, o.frequency); }
    }

    public static void main(String[] args) {
        String text = "data compression with huffman";

        // Step 1: Count character frequencies
        Map<Character, Integer> freq = new HashMap<>();
        for (char c : text.toCharArray()) {
            freq.put(c, freq.getOrDefault(c, 0) + 1);
        }

        // Step 2: Build Huffman tree
        PriorityQueue<HuffmanNode> pq = new PriorityQueue<>();
        for (var entry : freq.entrySet()) {
            pq.add(new HuffmanNode(entry.getKey(), entry.getValue()));
        }

        while (pq.size() > 1) {
            HuffmanNode left = pq.poll();
            HuffmanNode right = pq.poll();
            HuffmanNode parent = new HuffmanNode('\0', left.frequency + right.frequency);
            parent.left = left;
            parent.right = right;
            pq.add(parent);
        }

        HuffmanNode root = pq.poll();

        // Step 3: Generate codes
        Map<Character, String> codes = new HashMap<>();
        buildCodes(root, "", codes);

        // Step 4: Encode
        String encoded = encode(text, codes);

        // Step 5: Decode
        String decoded = decode(encoded, root);

        System.out.println("Original text: " + text);
        System.out.println("Encoded bits: " + encoded);
        System.out.println("Decoded text: " + decoded);
    }

    static void buildCodes(HuffmanNode node, String path, Map<Character, String> codes) {
        if (node == null) return;
        if (node.left == null && node.right == null) {
            // Handle single-symbol input so the code is at least one bit
            codes.put(node.symbol, path.isEmpty() ? "0" : path);
            return;
        }
        buildCodes(node.left, path + "0", codes);
        buildCodes(node.right, path + "1", codes);
    }

    static String encode(String text, Map<Character, String> codes) {
        StringBuilder sb = new StringBuilder();
        for (char c : text.toCharArray()) sb.append(codes.get(c));
        return sb.toString();
    }

    static String decode(String encoded, HuffmanNode root) {
        if (root == null) return "";

        // Handle single-symbol tree
        if (root.left == null && root.right == null) {
            StringBuilder only = new StringBuilder();
            for (int i = 0; i < encoded.length(); i++) only.append(root.symbol);
            return only.toString();
        }

        StringBuilder result = new StringBuilder();
        HuffmanNode current = root;
        for (char bit : encoded.toCharArray()) {
            current = (bit == '0') ? current.left : current.right;

            if (current == null) {
                throw new IllegalArgumentException("Invalid bit sequence for this Huffman tree");
            }

            if (current.left == null && current.right == null) {
                result.append(current.symbol);
                current = root;
            }
        }
        return result.toString();
    }
}