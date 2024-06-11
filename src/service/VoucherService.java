package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import model.Voucher;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.NhanVien;
import repository.JdbcHelper;

/**
 *
 * @author DELL
 */
public class VoucherService extends SellingApplicationImpl<Voucher, Integer> {

    @Override
    public void insert(Voucher entity) {
        String sql_insert = """
                            INSERT INTO [dbo].[Voucher]
                                       ([Ma]
                                       ,[Ten]
                                       ,[ID_NhanVien]
                                       ,[NgayBatDau]
                                       ,[NgayHetHan]
                                       ,[KieuGiam]
                                       ,[GiaTri]
                                       )
                                 VALUES
                                       (?,?,?,?,?,?,?)
                      
                            """;
        JdbcHelper.update(sql_insert,
                entity.getMa(),
                entity.getTen(),
                entity.getId_NV(),
                entity.getNgayBatDau(),
                entity.getNgayHetHan(),
                entity.getKieuGiam(),
                entity.getGiaTri()
        );
    }

    @Override
    public void update(Voucher entity) {
        String sql_update = """
                            UPDATE [dbo].[Voucher]
                               SET 
                                  [Ten] = ?
                                  ,[NgayBatDau] = ?
                                  ,[NgayHetHan] = ?                                
                                  ,[KieuGiam] = ?
                                  ,[GiaTri] = ?
                                  ,[TrangThai] = ?
                             WHERE ID = ?
                            """;
        JdbcHelper.update(sql_update,
                entity.getTen(),
                entity.getNgayBatDau(),
                entity.getNgayHetHan(),
                entity.getKieuGiam(),
                entity.getGiaTri(),
                entity.getTrangThai(),
                entity.getId()
        );
    }

    @Override
    public void delete(Integer id) {
        String sql_delete = """
                            DELETE FROM [dbo].[Voucher]
                                  WHERE ID = ?
                            """;
        JdbcHelper.update(sql_delete, id);
    }

    @Override
    public Voucher selectById(Integer id) {
        String sql = """
                     SELECT dbo.Voucher.[ID]
                           ,dbo.Voucher.[Ma]
                           ,dbo.Voucher.[Ten]
                           ,[dbo].NhanVien.Ma AS MaNV
                           ,[dbo].NhanVien.Ten AS TenNV
                           ,dbo.Voucher.[ngayTao]
                           ,dbo.Voucher.[NgayBatDau]
                           ,dbo.Voucher.[NgayHetHan]
                           ,dbo.Voucher.[SoLuong]
                           ,dbo.Voucher.[KieuGiam]
                           ,dbo.Voucher.[GiaTri]
                           ,dbo.Voucher.[TrangThai]
                       FROM dbo.NhanVien INNER JOIN dbo.Voucher
                            ON dbo.NhanVien.ID = dbo.Voucher.ID_NhanVien 
                       WHERE dbo.Voucher.ID = ?
                     """;
        List<Voucher> list = this.selectBySql(sql, id);
        if (list == null) {
            return null;
        }

        return list.get(0);
    }

    @Override
    public List<Voucher> selectAll() {
        String sql = """
                     SELECT
                         dbo.Voucher.ID,
                         dbo.Voucher.Ma,
                         dbo.Voucher.Ten,
                         dbo.NhanVien.Ma AS MaNV,
                         dbo.NhanVien.Ten AS TenNV,
                         dbo.Voucher.NgayTao,
                         dbo.Voucher.NgayBatDau,
                         dbo.Voucher.NgayHetHan,
                         dbo.Voucher.SoLuong,
                         dbo.Voucher.KieuGiam,
                         dbo.Voucher.GiaTri,
                         dbo.Voucher.TrangThai
                     FROM
                         dbo.NhanVien
                     INNER JOIN
                         dbo.Voucher ON dbo.NhanVien.ID = dbo.Voucher.ID_NhanVien
                     """;

        return this.selectBySql(sql);
    }

    @Override
    protected List<Voucher> selectBySql(String sql, Object... args) {
        List<Voucher> list = new ArrayList<>();

        try {
            ResultSet rs = JdbcHelper.query(sql, args);
            while (rs.next()) {
                Voucher vc = new Voucher();
                vc.setId(rs.getInt("ID"));
                vc.setMa(rs.getString("Ma"));
                vc.setTen(rs.getString("Ten"));
                vc.setNgayTao(rs.getDate("NgayTao"));
                vc.setNv(new NhanVien(rs.getString("MaNV"), rs.getString("TenNV")));
                vc.setNgayBatDau(rs.getDate("NgayBatDau"));
                vc.setNgayHetHan(rs.getDate("NgayHetHan"));
                vc.setSoLuong(rs.getInt("SoLuong"));
                vc.setKieuGiam(rs.getBoolean("KieuGiam"));
                vc.setGiaTri(rs.getDouble("GiaTri"));
                vc.setTrangThai(rs.getBoolean("TrangThai"));

                list.add(vc);
            }
            rs.getStatement().getConnection().close();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    public List<Voucher> selectByKeyWord(Integer id_voucher) {
        String sql = """
                        SELECT
                            dbo.Voucher.ID,
                            dbo.Voucher.Ma,
                            dbo.Voucher.Ten,
                            dbo.NhanVien.Ma AS MaNV,
                            dbo.NhanVien.Ten AS TenNV,
                            dbo.Voucher.NgayTao,
                            dbo.Voucher.NgayBatDau,
                            dbo.Voucher.NgayHetHan,
                            dbo.Voucher.SoLuong,
                            dbo.Voucher.KieuGiam,
                            dbo.Voucher.GiaTri,
                            dbo.Voucher.TrangThai
                        FROM
                            dbo.NhanVien
                        INNER JOIN
                            dbo.Voucher ON dbo.NhanVien.ID = dbo.Voucher.ID_NhanVien
                     """;
        return this.selectBySql(sql, id_voucher);
    }
    //search
    public List<Voucher> searchKeyWord(String keyWord) {
        String sql = """
                     SELECT  
                         dbo.Voucher.ID,
                         dbo.Voucher.Ma,
                         dbo.Voucher.Ten,
                         dbo.NhanVien.Ma AS MaNV,
                         dbo.NhanVien.Ten AS TenNV,
                         dbo.Voucher.NgayTao,
                         dbo.Voucher.NgayBatDau,
                         dbo.Voucher.NgayHetHan,
                         dbo.Voucher.SoLuong,
                         dbo.Voucher.KieuGiam,
                         dbo.Voucher.GiaTri,
                         dbo.Voucher.TrangThai
                     FROM
                         dbo.NhanVien
                     JOIN
                         dbo.Voucher ON dbo.NhanVien.ID = dbo.Voucher.ID_NhanVien
                     WHERE 
                         dbo.Voucher.Ten LIKE ? or dbo.Voucher.Ma LIKE ? or dbo.NhanVien.Ma LIKE ?;
                     
                     """;
        return this.selectBySql(sql,
                "%" + keyWord + "%",
                "%" + keyWord + "%",
                "%" + keyWord + "%"
                        
                );
    }
     public void updateStatus(Voucher vc) {
        try {
            String sql = """
                         UPDATE [dbo].[Voucher]
                            SET [TrangThai] = ?
                          WHERE ID =?
                         """;
            Connection connection = JdbcHelper.getConnection();
            PreparedStatement ps = connection.prepareStatement(sql);
            ps.setBoolean(1, vc.getTrangThai());
            ps.setInt(2, vc.getId());
            ps.executeUpdate();
            ps.close();
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(Voucher.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
