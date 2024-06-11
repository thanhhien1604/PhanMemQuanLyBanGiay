package service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.HoaDon;
import model.NhanVien;
import repository.JdbcHelper;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.KhachHang;
import model.Voucher;

public class HoaDonService extends SellingApplicationImpl<HoaDon, Integer> {

    @Override
    public void insert(HoaDon entity) {
        String sql = """
                        INSERT INTO [dbo].[HoaDon]
                                   ([NgayTao]
                                   ,[TongTien]
                                   ,[TrangThai]
                                   ,[ID_NhanVien])
                             VALUES(?, ?, ?, ?)
                     """;
        JdbcHelper.update(sql,
                entity.getNgayTao(),
                entity.getTongTien(),
                entity.getTrangThai(),
                entity.getIdNV()
        );
    }

    @Override
    public void update(HoaDon hd) {
        String sql = """
                     UPDATE [dbo].[HoaDon]
                        SET [TongTien] = ?
                           ,[TrangThai] = ?
                           ,[ID_KhachHang] = ?
                           ,[ID_Voucher] = ?
                      WHERE ID = ?
                     """;
        JdbcHelper.update(sql,
                hd.getTongTien(),
                hd.getTrangThai(),
                hd.getIdKH(),
                hd.getIdVC(),
                hd.getId());
    }

    @Override
    public void delete(Integer id) {

    }

    @Override
    public HoaDon selectById(Integer id) {
        String sql = """
                     SELECT 
                         hd.ID, 
                         hd.Ma, 
                         nv.Ma AS MaNV,
                         nv.Ten AS TenNV,
                         hd.NgayTao, 
                         hd.TongTien, 
                         hd.TrangThai
                     FROM 
                         dbo.HoaDon hd
                     JOIN 
                         dbo.NhanVien nv ON hd.ID_NhanVien = nv.ID
                     WHERE hd.ID = ?
                     """;
        List<HoaDon> list = this.selectBySql(sql, id);
        if (list == null) {
            return null;
        }
        return list.get(0);
    }

    public HoaDon selectByMa(String ma) {
        String sql = """
                     SELECT 
                         hd.ID, 
                         hd.Ma, 
                         nv.Ma AS MaNV,
                         nv.Ten AS TenNV,
                         hd.NgayTao, 
                         hd.TongTien, 
                         hd.TrangThai
                     FROM 
                         dbo.HoaDon hd
                     JOIN 
                         dbo.NhanVien nv ON hd.ID_NhanVien = nv.ID
                     WHERE hd.Ma LIKE ?
                     """;
        List<HoaDon> list = this.selectBySql(sql, "%" + ma + "%");
        if (list == null) {
            return null;
        }
        return list.get(0);
    }

    @Override
    public List<HoaDon> selectAll() {
        String sql = """
                     SELECT 
                         hd.ID, 
                         hd.Ma, 
                         nv.Ma AS MaNV,
                         nv.Ten AS TenNV,
                         hd.NgayTao, 
                         hd.TongTien, 
                         hd.TrangThai
                     FROM 
                         dbo.HoaDon hd
                     JOIN 
                         dbo.NhanVien nv ON hd.ID_NhanVien = nv.ID
                     """;
        return this.selectBySql(sql);
    }

    @Override
    protected List<HoaDon> selectBySql(String sql, Object... args) {
        List<HoaDon> list = new ArrayList<>();

        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                HoaDon hd = new HoaDon();

                hd.setId(rs.getInt("ID"));
                hd.setMa(rs.getString("Ma"));
                hd.setNgayTao(rs.getDate("NgayTao"));
                hd.setTongTien(rs.getDouble("TongTien"));
                hd.setTrangThai(rs.getInt("TrangThai"));
                hd.setNv(new NhanVien(rs.getString("MaNV"), rs.getString("TenNV")));

                list.add(hd);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    //đếm số lượng hoá đơn 
    public int count() {
        int totalCount = 0;
        Connection conn = JdbcHelper.getConnection();
        try {
            String sql = " SELECT COUNT(*) AS SOLUONG FROM HOADON";
            Statement stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            if (rs.next()) {
                totalCount = rs.getInt("SOLUONG");
            }
            conn.close();
            rs.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return totalCount;
    }

    //select phân trang
    public List<HoaDon> paging(int page, int limit) {
        List<HoaDon> list = new ArrayList<>();
        String sql = """
                         SELECT 
                             hd.ID, 
                             hd.Ma,                             
                             nv.Ten,
                             hd.NgayTao, 
                             hd.TongTien, 
                             hd.TrangThai,
                             kh.SDT,
                             vc.Ten as tenvc
                          
                         FROM 
                             dbo.HoaDon hd
                         JOIN 
                             dbo.NhanVien nv ON hd.ID_NhanVien = nv.ID
                         JOIN 
                             dbo.KhachHang kh on kh.id = hd.ID_KhachHang
                        LEFT JOIN 
                         	dbo.Voucher vc on vc.ID = hd.ID_Voucher
                         WHERE 
                             hd.TrangThai IN (1 ,3)
                         ORDER BY
                             hd.ID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                     """;
        try {
            Connection conn = JdbcHelper.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setInt(1, (page - 1) * limit);
            ps.setInt(2, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                HoaDon hd = new HoaDon();

                hd.setId(rs.getInt("ID"));
                hd.setMa(rs.getString("Ma"));
                hd.setNv(new NhanVien(rs.getString("Ma"), rs.getString("Ten")));
                hd.setNgayTao(rs.getDate("NgayTao"));
                hd.setTongTien(rs.getDouble("TongTien"));
                hd.setTrangThai(rs.getInt("TrangThai"));
                hd.setKh(new KhachHang(rs.getInt("ID"), rs.getString("SDT")));
                hd.setVc(new Voucher(rs.getInt("ID"), rs.getString("tenvc")));

                list.add(hd);
            }
            rs.getStatement().getConnection().close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    //tìm theo khoảng ngày
    public List<HoaDon> searchDate(String startDate, String endDate) {
        List<HoaDon> list = new ArrayList<>();
        try {
            String sql = """
                     SELECT 
                       hd.ID, 
                       hd.Ma,                             
                       nv.Ten,
                       hd.NgayTao, 
                       hd.TongTien, 
                       hd.TrangThai,
                       kh.SDT,
                       vc.Ten as tenvc
                   FROM 
                       dbo.HoaDon hd
                   JOIN 
                       dbo.NhanVien nv ON hd.ID_NhanVien = nv.ID
                   JOIN 
                       dbo.KhachHang kh on kh.id = hd.ID_KhachHang
                   LEFT JOIN 
                       dbo.Voucher vc on vc.ID = hd.ID_Voucher
                     WHERE ( hd.TrangThai = 1) and hd.NgayTao BETWEEN ? AND ?                                        
                                     """;
            Connection conn = JdbcHelper.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, startDate);
            ps.setString(2, endDate);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HoaDon hd = new HoaDon();
                hd.setId(rs.getInt("ID"));
                hd.setMa(rs.getString("Ma"));
                hd.setNv(new NhanVien(rs.getString("Ma"), rs.getString("Ten")));
                hd.setNgayTao(rs.getDate("NgayTao"));
                hd.setTongTien(rs.getDouble("TongTien"));
                hd.setTrangThai(rs.getInt("TrangThai"));
                hd.setKh(new KhachHang(rs.getInt("ID"), rs.getString("SDT")));
                hd.setVc(new Voucher(rs.getInt("ID"), rs.getString("tenvc")));
                list.add(hd);
            }
        } catch (SQLException ex) {
            Logger.getLogger(HoaDonService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;

    }

    public List<HoaDon> searchHD(String keyWord) {
        List<HoaDon> list = new ArrayList<>();

        try {
            String sql = """
                         SELECT 
                              hd.ID, 
                              hd.Ma,                             
                              nv.Ten,
                              hd.NgayTao, 
                              hd.TongTien, 
                              hd.TrangThai,
                              kh.SDT,
                              vc.Ten as tenvc
                          FROM 
                              dbo.HoaDon hd
                          JOIN 
                              dbo.NhanVien nv ON hd.ID_NhanVien = nv.ID
                          JOIN 
                              dbo.KhachHang kh on kh.id = hd.ID_KhachHang
                         LEFT JOIN 
                              dbo.Voucher vc on vc.ID = hd.ID_Voucher
                          WHERE 
                              ( hd.TrangThai = 1) and (hd.Ma LIKE ? OR nv.Ten LIKE ? OR kh.SDT LIKE ?)
                        
                     """;
            Connection conn = JdbcHelper.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyWord + "%");
            ps.setString(2, "%" + keyWord + "%");
            ps.setString(3, "%" + keyWord + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                HoaDon hd = new HoaDon();

                hd.setId(rs.getInt(1));
                hd.setMa(rs.getString(2));
                hd.setNv(new NhanVien(rs.getString("Ma"), rs.getString("Ten")));
                hd.setNgayTao(rs.getDate(4));
                hd.setTongTien(rs.getDouble(5));
                hd.setTrangThai(rs.getInt(6));
                hd.setKh(new KhachHang(rs.getInt("ID"), rs.getString("SDT")));
                hd.setVc(new Voucher(rs.getInt("ID"), rs.getString("tenvc")));
                list.add(hd);
            }
        } catch (SQLException ex) {
            Logger.getLogger(HoaDonService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }

    public List<HoaDon> selectByStatus() {
        String sql = """
                     SELECT 
                          hd.ID, 
                          hd.Ma, 
                          nv.Ma AS MaNV,
                          nv.Ten AS TenNV,
                          hd.NgayTao, 
                          hd.TongTien, 
                          hd.TrangThai
                      FROM 
                          dbo.HoaDon hd
                      JOIN 
                          dbo.NhanVien nv ON hd.ID_NhanVien = nv.ID
                      WHERE hd.TrangThai = 2
                     
                     """;
        return this.selectBySql(sql);
    }
}
