package view;

import java.awt.HeadlessException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.Voucher;
import repository.Authu;
import repository.MsgBox;
import service.VoucherService;

public class Form_KhuyenMai extends javax.swing.JPanel {

    private VoucherService VCservice = new VoucherService();
    private int row = -1;
    private int check;

    public Form_KhuyenMai() {
        initComponents();
        fillTable();
        this.row = -1;
        this.updateStatus();
    }

    private void fillTable() {
        DefaultTableModel model = (DefaultTableModel) tblVoucherCT.getModel();
        model.setRowCount(0);
        try {
            List<Voucher> list = VCservice.selectAll();
            for (Voucher vc : list) {
                // Kiểm tra và cập nhật trạng thái nếu cần thiết
                Date currentDate = new Date();
                if (vc.getTrangThai() && vc.getNgayHetHan().before(currentDate)) {
                    vc.setTrangThai(false);
                    VCservice.update(vc);
                }

                // Thêm dữ liệu vào bảng
                model.addRow(new Object[]{
                    vc.getId(),
                    vc.getMa(),
                    vc.getNv().getMa(),
                    vc.getTen(),
                    vc.getNgayTao(),
                    vc.getNgayBatDau(),
                    vc.getNgayHetHan(),
                    vc.getGiaTri(),
                    vc.getKieuGiam() ? "%" : "VND",
                    vc.getTrangThai() ? "Đang hoạt động" : "Hết hạn"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setDataForm(Voucher vc) {

        txtMa.setText(vc.getMa());
        rdoPhanTram.setSelected(vc.getKieuGiam());
        rdoVND.setSelected(!vc.getKieuGiam());

        jdcNgayBD.setDate(vc.getNgayBatDau());
        jdcNgayHH.setDate(vc.getNgayHetHan());
        if (vc.getTrangThai() == true) {
            rdoDHD.setSelected(true);
        } else {
            rdoHH.setSelected(true);
        }
        txtGiaTri.setText(String.valueOf(vc.getGiaTri()));
        txtTen.setText(vc.getTen());
    }

    private void updateStatus() {
        Boolean edit = this.row >= 0;

        btnAdd.setEnabled(!edit);
        btnUpdate.setEnabled(edit);
        btnDelete.setEnabled(edit);
    }

    private void editForm() {
        Integer id = (Integer) tblVoucherCT.getValueAt(row, 0);
        Voucher vc = VCservice.selectById(id);

        this.setDataForm(vc);
        this.updateStatus();
    }

    private Voucher getDataForm() {
        Voucher vc = new Voucher();
        if (jdcNgayBD.getDate() == null || jdcNgayHH.getDate() == null) {
            MsgBox.alert(this, "Vui lòng chọn ngày bắt dấu và ngày kết thúc!");
            return null;
        }
        Date dateStart = jdcNgayBD.getDate();
        Date dateEnd = jdcNgayHH.getDate();

        if (dateStart.after(dateEnd)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không thể lớn hơn ngày kết thúc!");
            txtSearch.setText(" ");
            return null;
        }
        if (txtMa.getText().trim().length() > 10) {
            MsgBox.alert(this, "Mã tối đa 10 kí tự!");
            return null;
        } else {
            vc.setMa(txtMa.getText().trim());
        }
       
        vc.setNgayBatDau(new java.sql.Date(jdcNgayBD.getDate().getTime()));
        vc.setNgayHetHan(new java.sql.Date(jdcNgayHH.getDate().getTime()));
        vc.setKieuGiam(rdoPhanTram.isSelected());
        vc.setTen(txtTen.getText());
        vc.setId_NV(Authu.user.getId());
        vc.setGiaTri(Double.valueOf(txtGiaTri.getText()));
        vc.setTrangThai(rdoDHD.isSelected());
        return vc;
    }

    private void insert() {
        Voucher vc = this.getDataForm();
        if (vc == null) {
            return;
        }
        check = JOptionPane.showConfirmDialog(this, "Xác nhận thêm dữ liệu?");
        if (check != JOptionPane.YES_OPTION) {
            return;
        }

        ArrayList<Voucher> list = (ArrayList<Voucher>) VCservice.selectAll();
        for (Voucher o : list) {
            if (o.getMa().equalsIgnoreCase(vc.getMa())) {
                MsgBox.alert(this, "Mã voucher đã tồn tại!");
                txtMa.requestFocus();
                return;
            }
        }
        try {
            VCservice.insert(vc);
            this.fillTable();
            this.clear();
            JOptionPane.showMessageDialog(this, "Thêm dữ liệu thành công!");
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, "Thêm dữ liệu thất bại!");
        }
    }

    private void update() {
        check = JOptionPane.showConfirmDialog(this, "Xác nhận sửa dữ liệu?");
        if (check != JOptionPane.YES_OPTION) {
            return;
        }

        Voucher vc = this.getDataForm();
        Integer id = (Integer) tblVoucherCT.getValueAt(row, 0);
        vc.setId(id);

        try {
            VCservice.update(vc);
            this.fillTable();
            JOptionPane.showMessageDialog(this, "Sửa dữ liệu thành công!");
        } catch (HeadlessException e) {
            JOptionPane.showMessageDialog(this, "Sửa dữ liệu thất bại!");
        }
    }

    private void delete() {
        check = JOptionPane.showConfirmDialog(this, "Xác nhận xóa dữ liệu?");
        if (check != JOptionPane.YES_OPTION) {
            return;
        }

        Integer id = (Integer) tblVoucherCT.getValueAt(row, 0);
        try {
            VCservice.delete(id);
            this.fillTable();
            JOptionPane.showMessageDialog(this, "Xóa dữ liệu thành công!");
        } catch (HeadlessException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Xóa dữ liệu thất bại!");
        }

    }

    public void clear() {
        jdcNgayBD.setDate(null);
        jdcNgayHH.setDate(null);
        this.row = -1;
        this.updateStatus();
        rdoDHD.setSelected(true);
        rdoPhanTram.setSelected(true);
        txtGiaTri.setText("");
        txtTen.setText("");
        txtMa.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jLabel1 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        rdoPhanTram = new javax.swing.JRadioButton();
        rdoVND = new javax.swing.JRadioButton();
        jLabel11 = new javax.swing.JLabel();
        txtGiaTri = new javax.swing.JTextField();
        jLabel13 = new javax.swing.JLabel();
        txtTen = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        rdoDHD = new javax.swing.JRadioButton();
        rdoHH = new javax.swing.JRadioButton();
        jdcNgayBD = new com.toedter.calendar.JDateChooser();
        jdcNgayHH = new com.toedter.calendar.JDateChooser();
        jLabel12 = new javax.swing.JLabel();
        txtMa = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        btnUpdate = new javax.swing.JButton();
        btnDelete = new javax.swing.JButton();
        btnNew = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblVoucherCT = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        txtSearch = new javax.swing.JTextField();

        setPreferredSize(new java.awt.Dimension(850, 510));

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel1.setText("Quản Lý Khuyến Mãi");

        jPanel3.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel5.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel5.setText("Ngày bắt đầu");

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Kiểu giảm giá");

        jLabel7.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel7.setText("Ngày hết hạn");

        buttonGroup1.add(rdoPhanTram);
        rdoPhanTram.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        rdoPhanTram.setText("%");

        buttonGroup1.add(rdoVND);
        rdoVND.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        rdoVND.setSelected(true);
        rdoVND.setText("VND");

        jLabel11.setText("Giá trị");

        jLabel13.setText("Tên Voucher");

        jLabel9.setText("Trạng thái");

        buttonGroup2.add(rdoDHD);
        rdoDHD.setSelected(true);
        rdoDHD.setText("Đang hoạt động");

        buttonGroup2.add(rdoHH);
        rdoHH.setText("Hết hạn");

        jLabel12.setText("Mã VC");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel7)
                        .addGap(18, 18, 18)
                        .addComponent(jdcNgayHH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(18, 18, 18)
                        .addComponent(jdcNgayBD, javax.swing.GroupLayout.DEFAULT_SIZE, 271, Short.MAX_VALUE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12))
                        .addGap(13, 13, 13)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtMa)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addComponent(rdoPhanTram)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(rdoVND)))))
                .addGap(182, 182, 182)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtGiaTri, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 337, Short.MAX_VALUE)
                            .addComponent(txtTen, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(rdoDHD)
                        .addGap(18, 18, 18)
                        .addComponent(rdoHH)
                        .addGap(147, 147, 147))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtGiaTri, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11))
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtMa, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel12)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(rdoPhanTram)
                    .addComponent(rdoVND)
                    .addComponent(txtTen, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel13))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel5)
                        .addComponent(jLabel9)
                        .addComponent(rdoDHD)
                        .addComponent(rdoHH))
                    .addComponent(jdcNgayBD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, 19, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jdcNgayHH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7)))
        );

        btnAdd.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnAdd.setText("Add");
        btnAdd.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddActionPerformed(evt);
            }
        });

        btnUpdate.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnDelete.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnDelete.setText("Delete");
        btnDelete.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDeleteActionPerformed(evt);
            }
        });

        btnNew.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        btnNew.setText("New");
        btnNew.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        btnNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNewActionPerformed(evt);
            }
        });

        tblVoucherCT.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID", "Mã voucher", "Mã NV", "Tên voucher", "Ngày tạo", "Ngày bắt đầu", "Ngày hết hạn", "Giá trị", "Kiểu giảm", "Trạng thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblVoucherCT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblVoucherCTMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblVoucherCT);

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel3.setText("Form:");

        jLabel4.setText("Search");

        txtSearch.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSearchKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1))
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(10, 10, 10)
                                        .addComponent(btnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel3)))
                            .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jLabel4)
                                        .addGap(18, 18, 18)
                                        .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, 304, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 607, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(txtSearch, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44)
                .addComponent(jLabel3)
                .addGap(3, 3, 3)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelete, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNew, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 268, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void btnAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddActionPerformed
        // TODO add your handling code here:
        this.insert();
    }//GEN-LAST:event_btnAddActionPerformed

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:
        this.update();
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void btnDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDeleteActionPerformed
        // TODO add your handling code here:
        this.delete();
    }//GEN-LAST:event_btnDeleteActionPerformed

    private void btnNewActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNewActionPerformed
        // TODO add your handling code here:
        this.clear();
    }//GEN-LAST:event_btnNewActionPerformed

    private void tblVoucherCTMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblVoucherCTMouseClicked
        // TODO add your handling code here:
        this.row = tblVoucherCT.getSelectedRow();
        this.editForm();
    }//GEN-LAST:event_tblVoucherCTMouseClicked

    private void txtSearchKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSearchKeyReleased
        // TODO add your handling code here:
        this.search();
    }//GEN-LAST:event_txtSearchKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDelete;
    private javax.swing.JButton btnNew;
    private javax.swing.JButton btnUpdate;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private com.toedter.calendar.JDateChooser jdcNgayBD;
    private com.toedter.calendar.JDateChooser jdcNgayHH;
    private javax.swing.JRadioButton rdoDHD;
    private javax.swing.JRadioButton rdoHH;
    private javax.swing.JRadioButton rdoPhanTram;
    private javax.swing.JRadioButton rdoVND;
    private javax.swing.JTable tblVoucherCT;
    private javax.swing.JTextField txtGiaTri;
    private javax.swing.JTextField txtMa;
    private javax.swing.JTextField txtSearch;
    private javax.swing.JTextField txtTen;
    // End of variables declaration//GEN-END:variables

    //update trạng thái voucher
    private void fillTableSauUpdateTrangThai(List<Voucher> list) {
        DefaultTableModel model = (DefaultTableModel) tblVoucherCT.getModel();
        model.setRowCount(0);
        try {
            for (Voucher vc : list) {
                model.addRow(new Object[]{
                    vc.getId(),
                    vc.getMa(),
                    vc.getNv().getMa(),
                    vc.getTen(),
                    vc.getNgayTao(),
                    vc.getNgayBatDau(),
                    vc.getNgayHetHan(),
                    vc.getGiaTri(),
                    vc.getKieuGiam() ? "%" : "VND",
                    vc.getTrangThai() ? "Đang hoạt động" : "Hết hạn"
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateVoucherStatus() {
        Date currentDate = new Date();
        List<Voucher> list = VCservice.selectAll();
        for (Voucher voucher : list) {
            if (voucher.getTrangThai() && voucher.getNgayHetHan().before(currentDate)) {
                voucher.setTrangThai(false);
                fillTableSauUpdateTrangThai(list);
                System.out.println("Voucher đã quá hạn");
            }
        }
    }

    public void startScheduledTask() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::updateVoucherStatus, 0, 1, TimeUnit.DAYS);
    }

    private void search() {
        DefaultTableModel model = (DefaultTableModel) tblVoucherCT.getModel();
        model.setRowCount(0);
        try {
            String keyString = txtSearch.getText().trim();
            List<Voucher> list = VCservice.searchKeyWord(keyString);
            for (Voucher vc : list) {
                model.addRow(new Object[]{
                    vc.getId(),
                    vc.getMa(),
                    vc.getNv().getMa(),
                    vc.getTen(),
                    vc.getNgayTao(),
                    vc.getNgayBatDau(),
                    vc.getNgayHetHan(),
                    vc.getGiaTri(),
                    vc.getKieuGiam() ? "%" : "VND",
                    vc.getTrangThai() ? "Đang hoạt động" : "Hết hạn"
                });
            }
        } catch (Exception e) {

        }
    }
}
