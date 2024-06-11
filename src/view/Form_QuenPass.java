package view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Timer;
import model.NhanVien;
import repository.JdbcHelper;
import repository.MsgBox;
import repository.XImage;
import service.NhanVienService;
import static view.QuenPassJDialog.updatePassword;

public class Form_QuenPass extends javax.swing.JFrame {

    private int countDownSeconds = 30;
    private Timer timer;
    private boolean canSendCode = true;
    private int pin;
    private long pinTimestamp;

    public Form_QuenPass() {
        initComponents();
        setLocationRelativeTo(null);

        setIconImage(XImage.getAppIcon());
        txtCode.setEnabled(false);
    }
 private void startCountdown() {
        btnGuiCode.setEnabled(false);
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                countDownSeconds--;
                btnGuiCode.setText(countDownSeconds + " giây");

                if (countDownSeconds <= 0) {
                    timer.stop();
                    btnGuiCode.setText("Gửi Code");
                    btnGuiCode.setEnabled(true);
                    canSendCode = true;
                    countDownSeconds = 20;
                }
            }
        });
        timer.start();
    }

     private boolean isPinValid(String inputPin) {

        if (System.currentTimeMillis() - pinTimestamp > 20000) {
            return false; // Mã PIN đã hết hạn
        }

        return inputPin.equals(String.valueOf(pin));
    }
    private String generatePin() {
        int pinLength = 4; // Độ dài của mã pin
        Random random = new Random();
        StringBuilder pinBuilder = new StringBuilder();

        for (int i = 0; i < pinLength; i++) {
            pinBuilder.append(random.nextInt(10)); // Thêm mỗi chữ số vào mã pin
        }

        return pinBuilder.toString();
    }

       private void sendPin(String toEmail) {
        final String fromEmail = "adamstore112023@gmail.com";
        final String password = "rgyr twvu dagk kwuz";

        Properties properties = new Properties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "587");

        Session session = Session.getInstance(properties, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(fromEmail, password);
            }
        });

        try {

            pin = (int) (Math.random() * 9000) + 1000;
            pinTimestamp = System.currentTimeMillis();
            canSendCode = false;
            Message mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(fromEmail));
            mimeMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
            mimeMessage.setSubject("Mã Code Lấy Lại Mật Khẩu");
            mimeMessage.setText("Mã Code Là: " + pin);

            Transport.send(mimeMessage);

            System.out.println(pin);
            MsgBox.alert(this, "Đã Gửi Mã Code Thành Công\nMã Code Có Hiệu Lực Trong 20s!");
        } catch (MessagingException e) {
            e.printStackTrace();
            MsgBox.alert(this, "Error sending PIN. Please try again.");
        }
    }

    public static void updatePassword(String email, String newPassword) {
        Connection conn = JdbcHelper.getConnection();
        String sql = "UPDATE NhanVien SET Passwords = ? WHERE Email = ?";
        try {
            int kq = 0;
            PreparedStatement preSt = conn.prepareCall(sql);
            preSt.setString(1, newPassword);
            preSt.setString(2, email);
            kq = preSt.executeUpdate();
        } catch (SQLException ex) {
            Logger.getLogger(Form_QuenPass.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        btnExit = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        txtMail = new javax.swing.JTextField();
        txtCode = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        txtMatKhau = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        txtNhapLai = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        btnConfirm = new javax.swing.JButton();
        btnGuiCode = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        btnExit.setText("Thoát");
        btnExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnExitMouseClicked(evt);
            }
        });

        jLabel1.setText("Nhập Email: ");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        jLabel2.setText("Quên Mật Khẩu");

        txtMail.setBackground(new java.awt.Color(60, 63, 65));
        txtMail.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(255, 255, 255)));
        txtMail.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMailActionPerformed(evt);
            }
        });

        txtCode.setBackground(new java.awt.Color(60, 63, 65));
        txtCode.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(255, 255, 255)));

        jLabel3.setText("Nhập Code: ");

        txtMatKhau.setBackground(new java.awt.Color(60, 63, 65));
        txtMatKhau.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(255, 255, 255)));
        txtMatKhau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtMatKhauActionPerformed(evt);
            }
        });

        jLabel4.setText("Nhập Mật Khẩu Mới: ");

        txtNhapLai.setBackground(new java.awt.Color(60, 63, 65));
        txtNhapLai.setBorder(javax.swing.BorderFactory.createMatteBorder(0, 0, 1, 0, new java.awt.Color(255, 255, 255)));
        txtNhapLai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNhapLaiActionPerformed(evt);
            }
        });

        jLabel5.setText("Nhập Lại Mật Khẩu Mới: ");

        btnConfirm.setText("Xác Nhận");
        btnConfirm.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnConfirmMouseClicked(evt);
            }
        });
        btnConfirm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConfirmActionPerformed(evt);
            }
        });

        btnGuiCode.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icon/send_code.png"))); // NOI18N
        btnGuiCode.setText("send");
        btnGuiCode.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                btnGuiCodeMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtMail, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnGuiCode)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtNhapLai, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(31, Short.MAX_VALUE))))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(166, 166, 166)
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel2)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(txtMail, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(txtCode, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuiCode))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(txtNhapLai, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnConfirm, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnExit, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnExitMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnExitMouseClicked
        // TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_btnExitMouseClicked

    private void txtMailActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMailActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMailActionPerformed

    private void txtMatKhauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtMatKhauActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtMatKhauActionPerformed

    private void txtNhapLaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNhapLaiActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNhapLaiActionPerformed

    private void btnGuiCodeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnGuiCodeMouseClicked
        // TODO add your handling code here:
      try {
            NhanVienService nhanVienService = new NhanVienService();
            List<NhanVien> nhanVienList = nhanVienService.selectAll();
            List<String> emailList = new ArrayList<>();
            for (NhanVien nhanVien : nhanVienList) {
                String emaill = nhanVien.getEmail();
                emailList.add(emaill);
            }
            String email = txtMail.getText();
            if (!email.isEmpty()) {
                if (emailList.contains(email)) {
                    if (canSendCode) {
                        txtCode.setEnabled(true);
                        sendPin(email);
                        startCountdown();
                    }
                } else {
                    MsgBox.alert(this, "Email Nhập Không Có Trong Hệ Thống!>");
                }
            } else {
                MsgBox.alert(this, "Vui Lòng Nhập Email.");
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }//GEN-LAST:event_btnGuiCodeMouseClicked

    private void btnConfirmMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_btnConfirmMouseClicked
        // TODO add your handling code here:
        String maPin = txtCode.getText();
        String email = txtMail.getText();
        String mkMoi = txtMatKhau.getText();
        String mkLai = txtNhapLai.getText();
        System.out.println(pin);
        if (isPinValid(maPin)) {
            if (mkMoi.equals(mkLai)) {
                updatePassword(email, mkMoi);
                MsgBox.alert(null, "Cập nhật mật khẩu thành công!");
            } else {
                MsgBox.alert(this, "2 Mật Khẩu Không Khớp!");
            }
        } else {
            MsgBox.alert(null, "Mã pin không đúng hoặc đã hết hạn. Vui lòng thử lại.");
        }
    }//GEN-LAST:event_btnConfirmMouseClicked

    private void btnConfirmActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConfirmActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnConfirmActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Form_QuenPass.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Form_QuenPass.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Form_QuenPass.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Form_QuenPass.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Form_QuenPass().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnConfirm;
    private javax.swing.JButton btnExit;
    private javax.swing.JButton btnGuiCode;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField txtCode;
    private javax.swing.JTextField txtMail;
    private javax.swing.JTextField txtMatKhau;
    private javax.swing.JTextField txtNhapLai;
    // End of variables declaration//GEN-END:variables
}
