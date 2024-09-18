package AES;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Base64;

public class Ma_hoa_AES {
    private JFrame frame;

    public Ma_hoa_AES() {
        frame = new JFrame("Chương trình mã hóa và giải mã file văn bản bằng AES");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 200);
        frame.setLayout(null);

        JButton encryptButton = new JButton("Mở cửa sổ mã hóa");
        encryptButton.setBounds(50, 30, 200, 30);
        frame.add(encryptButton);

        JButton decryptButton = new JButton("Mở cửa sổ giải mã");
        decryptButton.setBounds(50, 80, 200, 30);
        frame.add(decryptButton);

        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new EncryptWindow();
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new DecryptWindow();
            }
        });

        frame.setLocationRelativeTo(null); // Đặt cửa sổ chính giữa màn hình
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        new Ma_hoa_AES();
    }
}

class EncryptWindow {
    private JFrame frame;
    private JTextField filePathField;
    private JTextField keyField;
    private JTextArea outputTextArea;

    public EncryptWindow() {
        frame = new JFrame("Mã hóa file bằng AES");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(null);

        JLabel filePathLabel = new JLabel("Đường dẫn file:");
        filePathLabel.setBounds(10, 10, 150, 25);
        frame.add(filePathLabel);

        filePathField = new JTextField();
        filePathField.setBounds(160, 10, 350, 25);
        frame.add(filePathField);

        JButton browseButton = new JButton("Duyệt");
        browseButton.setBounds(520, 10, 80, 25);
        frame.add(browseButton);

        JLabel keyLabel = new JLabel("Khóa AES (16, 24 hoặc 32 bytes):");
        keyLabel.setBounds(10, 50, 200, 25);
        frame.add(keyLabel);

        keyField = new JTextField();
        keyField.setBounds(220, 50, 290, 25);
        frame.add(keyField);

        JButton generateKeyButton = new JButton("Tạo");
        generateKeyButton.setBounds(520, 50, 80, 25);
        frame.add(generateKeyButton);

        JButton encryptButton = new JButton("Mã hóa");
        encryptButton.setBounds(10, 90, 100, 25);
        frame.add(encryptButton);

        JButton clearButton = new JButton("Xóa");
        clearButton.setBounds(120, 90, 100, 25);
        frame.add(clearButton);

        outputTextArea = new JTextArea();
        outputTextArea.setBounds(10, 130, 580, 220);
        frame.add(outputTextArea);

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    filePathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        generateKeyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    SecretKey secretKey = generateKey();
                    String keyString = Base64.getEncoder().encodeToString(secretKey.getEncoded());
                    keyField.setText(keyString);
                } catch (Exception ex) {
                    outputTextArea.setText("Lỗi tạo khóa: " + ex.getMessage());
                }
            }
        });

        encryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = filePathField.getText();
                String key = keyField.getText();
                try {
                    byte[] keyBytes = Base64.getDecoder().decode(key);
                    if (keyBytes.length == 16 || keyBytes.length == 24 || keyBytes.length == 32) {
                        encryptFile(filePath, keyBytes);
                    } else {
                        outputTextArea.setText("Khóa AES phải có độ dài 16, 24 hoặc 32 bytes.");
                    }
                } catch (Exception ex) {
                    outputTextArea.setText("Lỗi mã hóa: " + ex.getMessage());
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filePathField.setText("");
                keyField.setText("");
                outputTextArea.setText("");
            }
        });

        frame.setLocationRelativeTo(null); // Đặt cửa sổ mã hóa giữa màn hình
        frame.setVisible(true);
    }

    private void encryptFile(String filePath, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);

        FileInputStream fis = new FileInputStream(filePath);
        byte[] inputBytes = fis.readAllBytes();
        fis.close();

        byte[] outputBytes = cipher.doFinal(inputBytes);

        String encryptedFilePath = filePath + ".enc";
        FileOutputStream fos = new FileOutputStream(encryptedFilePath);
        fos.write(outputBytes);
        fos.close();

        // Hiển thị nội dung đã mã hóa trong JTextArea
        StringBuilder encryptedContent = new StringBuilder();
        for (byte b : outputBytes) {
            encryptedContent.append(String.format("%02X ", b)); // Hiển thị dưới dạng mã hex
        }
        outputTextArea.setText("Đã mã hóa file và lưu tại: " + encryptedFilePath + "\nNội dung mã hóa:\n" + encryptedContent.toString());
    }

    private SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256); // Khóa 256-bit
        return keyGenerator.generateKey();
    }
}

class DecryptWindow {
    private JFrame frame;
    private JTextField filePathField;
    private JTextField keyField;
    private JTextArea outputTextArea;

    public DecryptWindow() {
        frame = new JFrame("Giải mã file bằng AES");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(null);

        JLabel filePathLabel = new JLabel("Đường dẫn file:");
        filePathLabel.setBounds(10, 10, 150, 25);
        frame.add(filePathLabel);

        filePathField = new JTextField();
        filePathField.setBounds(160, 10, 350, 25);
        frame.add(filePathField);

        JButton browseButton = new JButton("Duyệt");
        browseButton.setBounds(520, 10, 80, 25);
        frame.add(browseButton);

        JLabel keyLabel = new JLabel("Khóa AES (16, 24 hoặc 32 bytes):");
        keyLabel.setBounds(10, 50, 200, 25);
        frame.add(keyLabel);

        keyField = new JTextField();
        keyField.setBounds(220, 50, 290, 25);
        frame.add(keyField);

        JButton decryptButton = new JButton("Giải mã");
        decryptButton.setBounds(10, 90, 100, 25);
        frame.add(decryptButton);

        JButton clearButton = new JButton("Xóa");
        clearButton.setBounds(120, 90, 100, 25);
        frame.add(clearButton);

        outputTextArea = new JTextArea();
        outputTextArea.setBounds(10, 130, 580, 220);
        frame.add(outputTextArea);

        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                int result = fileChooser.showOpenDialog(frame);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    filePathField.setText(selectedFile.getAbsolutePath());
                }
            }
        });

        decryptButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = filePathField.getText();
                String key = keyField.getText();
                try {
                    byte[] keyBytes = Base64.getDecoder().decode(key);
                    if (keyBytes.length == 16 || keyBytes.length == 24 || keyBytes.length == 32) {
                        decryptFile(filePath, keyBytes);
                    } else {
                        outputTextArea.setText("Khóa AES phải có độ dài 16, 24 hoặc 32 bytes.");
                    }
                } catch (Exception ex) {
                    outputTextArea.setText("Lỗi giải mã: " + ex.getMessage());
                }
            }
        });

        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                filePathField.setText("");
                keyField.setText("");
                outputTextArea.setText("");
            }
        });

        frame.setLocationRelativeTo(null); // Đặt cửa sổ giải mã giữa màn hình
        frame.setVisible(true);
    }

    private void decryptFile(String filePath, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);

        FileInputStream fis = new FileInputStream(filePath);
        byte[] inputBytes = fis.readAllBytes();
        fis.close();

        byte[] outputBytes = cipher.doFinal(inputBytes);

        String decryptedFilePath = filePath.replace(".enc", ".dec");
        FileOutputStream fos = new FileOutputStream(decryptedFilePath);
        fos.write(outputBytes);
        fos.close();

        // Hiển thị nội dung đã giải mã trong JTextArea
        String decryptedContent = new String(outputBytes); // Chuyển đổi byte thành chuỗi
        outputTextArea.setText("Đã giải mã file và lưu tại: " + decryptedFilePath + "\nNội dung giải mã:\n" + decryptedContent);
    }
}
