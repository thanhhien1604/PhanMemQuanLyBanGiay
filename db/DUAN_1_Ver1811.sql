CREATE DATABASE DA1
GO
USE DA1
GO

--Drop database DA1

CREATE TABLE Size(
	ID INT IDENTITY(1,1) NOT NULL,
	Ten INT NOT NULL

	CONSTRAINT [PK_KichThuoc] PRIMARY KEY (ID)
)
GO


CREATE TABLE MauSac(
	ID INT IDENTITY(1,1) NOT NULL,
	Ten NVARCHAR(50) NOT NULL

	CONSTRAINT [PK_MauSac] PRIMARY KEY (ID)
)
GO

CREATE TABLE ChatLieu(
	ID INT IDENTITY(1,1) NOT NULL,
	Ten NVARCHAR(50) NOT NULL

	CONSTRAINT [PK_ChatLieu] PRIMARY KEY (ID)
)
GO

CREATE TABLE ThuongHieu(
	ID INT IDENTITY(1,1) NOT NULL,
	Ten NVARCHAR(50) NOT NULL

	CONSTRAINT [PK_ThuongHieu] PRIMARY KEY (ID)
)
GO

CREATE TABLE DanhMuc(
	ID INT IDENTITY(1,1) NOT NULL,
	Ten NVARCHAR(50) NOT NULL

	CONSTRAINT [PK_DanhMuc] PRIMARY KEY (ID)
)
GO


CREATE TABLE KhachHang(
	ID INT IDENTITY(1,1) NOT NULL,
	Ma VARCHAR(10) UNIQUE,
	Ten NVARCHAR(30) NOT NULL,
	NgaySinh DATE  NULL,
	GioiTinh BIT  NULL,
	SDT VARCHAR(10) NOT NULL, -- review

	CONSTRAINT [PK_KhachHang] PRIMARY KEY (ID)
)
GO

CREATE TABLE NhanVien(
	ID INT IDENTITY(1,1) NOT NULL,
	Ma VARCHAR(10) UNIQUE,
	Passwords VARCHAR (15) NOT NULL,
	Ten NVARCHAR(30) NOT NULL,
	SDT VARCHAR(10) NOT NULL,
	Email VARCHAR(30) NOT NULL,
	Anh NVARCHAR(MAX) NULL,
	ChucVu BIT NOT NULL,
	TrangThai BIT NOT NULL,
	Luong MONEY NOT NULL,
	NgaySinh DATE NOT NULL,
	DiaChi NVARCHAR(max) NOT NULL
	CONSTRAINT [PK_NhanVien] PRIMARY KEY (ID)
)
GO

CREATE TABLE SanPham(
	ID INT IDENTITY(1,1) NOT NULL,
	Ma VARCHAR(10) UNIQUE,
	Ten NVARCHAR(30) NOT NULL,
	NgayThem DATETIME DEFAULT GETDATE(),
	ID_ThuongHieu INT  NULL,
	ID_DanhMuc INT  NULL,
	ID_NhanVien INT NOT NULL,


	CONSTRAINT [PK_SanPham] PRIMARY KEY (ID),
	CONSTRAINT [FK_SanPham_ThuongHieu] FOREIGN KEY(ID_ThuongHieu) REFERENCES ThuongHieu(ID),
	CONSTRAINT [FK_SanPham_DanhMuc] FOREIGN KEY(ID_DanhMuc) REFERENCES DanhMuc(ID),
	CONSTRAINT [FK_SanPham_NhanVien] FOREIGN KEY(ID_NhanVien) REFERENCES NhanVien(ID)
)
GO

CREATE TABLE SanPhamChiTiet(
	ID INT IDENTITY(1,1) NOT NULL,
	Gia FLOAT NOT NULL,
	SoLuong INT NOT NULL,
	MaSP VARCHAR(10) NOT NULL,
	TrangThai BIT NOT NULL DEFAULT 1, -- mặc định true
	ID_SP INT  NULL,
	ID_Size INT NOT NULL,
	ID_MauSac INT NOT NULL,
	ID_ChatLieu INT NOT NULL,

	CONSTRAINT [FK_CTSP_SanPham] FOREIGN KEY(ID_SP) REFERENCES SanPham (ID), 
	CONSTRAINT [FK_CTSP_Size] FOREIGN KEY(ID_Size) REFERENCES Size(ID),
	CONSTRAINT [FK_CTSP_MauSac] FOREIGN KEY(ID_MauSac) REFERENCES MauSac(ID),
	CONSTRAINT [FK_CTSP_ChatLieu] FOREIGN KEY(ID_ChatLieu) REFERENCES ChatLieu(ID),
	CONSTRAINT [PK_SanPhamChiTiet] PRIMARY KEY (ID)
)
GO
CREATE TABLE Voucher(
	ID INT IDENTITY(1,1) NOT NULL,
	Ma VARCHAR(10) UNIQUE,
	Ten NVARCHAR(30) NOT NULL,
	NgayTao DATETIME DEFAULT GETDATE(),
	ID_NhanVien INT NOT NULL,
	NgayBatDau DATE NOT NULL,
    NgayHetHan DATE NOT NULL,
    SoLuong INT NULL,
    KieuGiam Bit NOT NULL,
	GiaTri FLOAT NOT NULL,
    TrangThai BIT NOT NULL DEFAULT 1, -- Đặt mặc định TrangThai là True
	CONSTRAINT [PK_Voucher] PRIMARY KEY (ID),
	CONSTRAINT [FK_Voucher_NhanVien] FOREIGN KEY(ID_NhanVien) REFERENCES NhanVien(ID)
)

GO
CREATE TABLE HoaDon(
	ID INT IDENTITY(1,1) NOT NULL,
	Ma VARCHAR(10) UNIQUE,
	NgayTao DATETIME DEFAULT GETDATE(),
	TongTien FLOAT NULL,
	TrangThai INT NOT NULL DEFAULT 2, 
	ID_NhanVien INT NULL,
	ID_KhachHang INT  NULL,
	ID_Voucher int  null,
	CONSTRAINT [FK_CTHD_NhanVien] FOREIGN KEY(ID_NhanVien) REFERENCES NhanVien(ID),
	CONSTRAINT [FK_CTHD_KhachHang] FOREIGN KEY(ID_KhachHang) REFERENCES KhachHang(ID),
	CONSTRAINT [PK_HoaDon] PRIMARY KEY (ID),
	CONSTRAINT [FK_Voucher] FOREIGN KEY(ID_Voucher) REFERENCES Voucher(ID),
)
GO
CREATE TABLE HoaDonChiTiet(
	ID INT IDENTITY(1,1) NOT NULL,
	GiaBan FLOAT NOT NULL,
	SoLuongSP INT NOT NULL,
	TongTien FLOAT NOT NULL,
	ID_SanPhamCT INT NOT NULL,
	ID_HoaDon INT NOT NULL,
	ID_Voucher INT NULL,

	CONSTRAINT [FK_CTHD_SanPhamCT] FOREIGN KEY(ID_SanPhamCT) REFERENCES SanPhamChiTiet(ID),
	CONSTRAINT [FK_CTHD_HoaDon] FOREIGN KEY(ID_HoaDon) REFERENCES HoaDon(ID),
	CONSTRAINT [FK_CTHD_Voucher] FOREIGN KEY(ID_Voucher) REFERENCES Voucher(ID),

	CONSTRAINT [PK_HoaDonChiTiet] PRIMARY KEY (ID)
)
GO

-- Trigger để tự động sinh mã khi thêm dữ liệu mới
CREATE TRIGGER Tr_Generate_MaHD ON HoaDon
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO HoaDon (Ma, NgayTao, TongTien, ID_NhanVien, ID_KhachHang, ID_Voucher)
    SELECT 
        COALESCE(Ma, 'HD' + RIGHT('00000' + CAST((ABS(CHECKSUM(NEWID())) % 100000) AS VARCHAR(5)), 5)),
        NgayTao, TongTien, ID_NhanVien, ID_KhachHang, ID_Voucher
    FROM inserted;
END

GO
CREATE TRIGGER Tr_Generate_MaKH ON KhachHang
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO KhachHang(Ma, Ten, NgaySinh, GioiTinh, SDT)
    SELECT 
        COALESCE(Ma, 'KH' + RIGHT('00000' + CAST((ABS(CHECKSUM(NEWID())) % 100000) AS VARCHAR(5)), 5)),
        Ten, NgaySinh, GioiTinh, SDT
    FROM inserted;
END
GO


GO
CREATE TRIGGER Tr_Generate_MaVC ON Voucher
INSTEAD OF INSERT
AS
BEGIN
    SET NOCOUNT ON;

    --INSERT INTO Voucher(Ma, Ten, NgayTao, ID_NhanVien)
	 INSERT INTO Voucher([Ma], [Ten], [NgayTao], [ID_NhanVien],[NgayBatDau], [NgayHetHan], [SoLuong], [KieuGiam],[GiaTri], [TrangThai])
    SELECT 
        COALESCE(Ma, 'VC' + RIGHT('00000' + CAST((ABS(CHECKSUM(NEWID())) % 100000) AS VARCHAR(5)), 5)),
        [Ten], [NgayTao], [ID_NhanVien],[NgayBatDau], [NgayHetHan], [SoLuong], [KieuGiam],[GiaTri], [TrangThai]
    FROM inserted;
END
GO

INSERT INTO [dbo].[Size] ([Ten]) 
VALUES  (N'35'),
		(N'36'),
		(N'37'),
		(N'38'),
		(N'39'),
		(N'40'),
		(N'41'),
		(N'42'),
		(N'43');
GO
--Dữ liệu MauSac
INSERT INTO [dbo].[MauSac] ([Ten]) 
VALUES  (N'Đỏ'),
		(N'Trắng'),
		(N'Đen'),
		(N'Hồng');
GO
--Dữ liệu ChatLieu
INSERT INTO [dbo].[ChatLieu] ([Ten]) 
VALUES  (N'Da'),
		(N'Vải');
GO

--Dữ liệu ThuongHieu
INSERT INTO [dbo].[ThuongHieu] ([Ten]) 
VALUES  (N'Nike'),
		(N'Puma'),
		(N'Asics'),
		(N'Balance'),
		(N'Adidas');
GO
--Dữ liệu DanhMuc
INSERT INTO [dbo].[DanhMuc] ([Ten]) 
VALUES  (N'Giày đôi'),
		(N'Giày nam'),
		(N'Giày nữ'),
		(N'Giày thể thao'),
		(N'Giày thời trang');
GO
--Dữ liệu KhachHang
INSERT INTO [dbo].[KhachHang]([Ma], [Ten], [NgaySinh], [GioiTinh], [SDT])
VALUES		(NULL, N'Vũ Văn Nguyên', '2004-11-20', 1, '0987234141'),
			(NULL, N'Chu Thị Ngân', '2004-11-20', 0, '0987234141'),
			(NULL, N'Nguyễn Văn Tèo', '2004-11-20', 1, '0987234141'),
			(NULL, N'Nguyễn Thúy Hằng', '2004-11-20', 0, '0987234141'),
			(NULL, N'Nguyễn Anh Dũng', '2004-11-20', 1, '0987234141');
GO
use DA1
--Dữ liệu nhân viên
select * from NhanVien
INSERT INTO [dbo].[NhanVien] ([Ma], [Passwords], [Ten], [SDT], [Email], [ChucVu], [TrangThai], [Luong],[NgaySinh],[DiaChi])
VALUES		('PH12345', '123', N'Nguyễn Thanh Tùng', '0367439572', 'tung123@gmail.com', 1, 1, 200000,'2004-11-11',N'Chùa Láng'),
			('PH36590', '123', N'Phạm Thanh Hiếu', '0123456789', 'ahieu5377@gmail.com', 1, 1, 200000,'2004-11-11',N'HN'),
			('PH36591', '123', N'Nguyễn Vãn Tèo', '0932432422', 'huyldph40152@fpt.edu.vn', 1, 1, 3500000,'2004-11-11',N'Nguyên Xá');
go
--Dữ liệu SanPham
select * from NhanVien
INSERT INTO [dbo].[SanPham] ([Ma], [Ten], [NgayThem], [ID_ThuongHieu], [ID_DanhMuc], [ID_NhanVien])
VALUES			(NULL, N'Giày Nike nam', DEFAULT, 1, 4, 1),
				(NULL, N'Giày Nike nữ', DEFAULT, 1, 2, 3),
				(NULL, N'Giày đôi', DEFAULT, 2, 3, 1),
				(NULL, N'Giày Thể thao Hàn Quốc', DEFAULT, 4, 1, 1),
				(NULL, N'Giày Thời trang nam', DEFAULT, 4, 2, 2);

GO

--dữ liệu sản phẩm chi tiết
INSERT INTO [dbo].[SanPhamChiTiet] ([Gia], [SoLuong], [MaSP], [TrangThai], [ID_SP], [ID_Size], ID_MauSac, ID_ChatLieu)
VALUES			(100, 50, 'SP86615', 1, 1, 1, 1, 1),
				(200, 50, 'SP24906', 1, 2, 2, 2, 2),
				(300, 50, 'SP13164', 1, 3, 3, 3, 1),
				(400, 50, 'SP81048', 1, 4, 2, 2, 2),
				(500, 50, 'SP08464', 1, 5, 2, 4, 2);
GO

--Dữ liệu Voucher
select *from SanPhamChiTiet
select * from NhanVien
INSERT INTO [dbo].[Voucher] ([Ma], [Ten], [NgayTao], [ID_NhanVien],[NgayBatDau], [NgayHetHan], [SoLuong], [KieuGiam],[GiaTri], [TrangThai])
VALUES			(NULL, N'Giảm giá 8/4', DEFAULT, 1,'2023-10-20', '2023-10-25', 100, 1, 20, 1),
				(NULL, N'Giảm giá 20/11', DEFAULT, 3,'2023-10-20', '2023-10-25', 100, 1, 10, 1),
				(NULL, N'Giảm giá Halloween', DEFAULT, 1, '2023-10-20', '2023-10-25', 100, 0, 40,0),
				(NULL, N'Giảm giá Valentine', DEFAULT, 2, '2023-10-20', '2023-10-25', 100, 1,15, 1),
				('VCTET', N'Giảm giá Tết', DEFAULT, 2, '2023-10-20', '2023-10-25', 100, 1,10,  0);



--Dữ liệu HoaDon
select * from HoaDon
INSERT INTO [dbo].[HoaDon] ([Ma], [NgayTao], [TongTien], TrangThai, ID_NhanVien, ID_KhachHang, ID_Voucher)
VALUES			(NULL, DEFAULT, 0, 1, 1, 1,1)

--Dữ liệu HDCT
INSERT INTO [dbo].[HoaDonChiTiet] ([GiaBan], [SoLuongSP], [TongTien], [ID_SanPhamCT], [ID_HoaDon], [ID_Voucher])
VALUES			(100, 2, 200, 4, 1, 1),
				(100, 2, 200, 5, 1, 1)



GO
USE DA1

SELECT * FROM ChatLieu
SELECT * FROM Size
SELECT * FROM MauSac
SELECT * FROM DanhMuc
SELECT * FROM ThuongHieu
SELECT * FROM KhachHang
SELECT * FROM NhanVien
SELECT * FROM SanPham
SELECT * FROM SanPhamChiTiet 
SELECT * FROM Voucher
SELECT * FROM HoaDon
SELECT * FROM HoaDonChiTiet 
go

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
    dbo.Voucher.Ten LIKE '%%';






INSERT INTO [dbo].[NhanVien] ([Ma], [Passwords], [Ten], [SDT], [Email], [ChucVu], [TrangThai], [Luong],[N],[DiaChi])"
            + "VALUES	(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
       --add nv
INSERT INTO [dbo].[NhanVien]
           ([Ma]
           ,[Passwords]
           ,[Ten]
           ,[SDT]
           ,[Email]
           ,[ChucVu]
           ,[TrangThai]
           ,[Luong]
           ,[NgaySinh]
           ,[DiaChi])
     VALUES(?,?,?,?,?,?,?,?,?,?)
	 --update nv
	 UPDATE [dbo].[NhanVien]
   SET [Ma] = ?
      ,[Passwords] = ?
      ,[Ten] = ?
      ,[SDT] = ?
      ,[Email] = ?
      ,[ChucVu] = ?
      ,[TrangThai] =?
      ,[Luong] = ?
      ,[NgaySinh] = ?
      ,[DiaChi] = ?
 WHERE ID = ?

 
INSERT INTO [dbo].[NhanVien] ([Ma], [Passwords], [Ten], [SDT], [Email], [ChucVu], [TrangThai])
	VALUES		('PH36590', '123', N'Pham Thanh Hieu', '0387377953', 'hieuptph36590@fpt.edu.vn', 1, 1)

INSERT INTO [dbo].[HoaDon] ([Ma], [NgayTao], [TongTien], TrangThai, ID_NhanVien)
VALUES			(NULL, DEFAULT, 0, DEFAULT, 2)
select * from HoaDon	where TrangThai = 0


 
UPDATE [dbo].[SanPhamChiTiet]
                        SET SoLuong = 50
                      WHERE ID = 3
					  delete from HoaDonChiTiet

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
WHERE hd.TrangThai = CAST(0 AS bit)

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



	---
SELECT
        hdct.ID,
        hd.Ma AS MaHD,
        sp.Ma AS MaSP,
        sp.Ten AS TenSP,
        kh.Ma AS MaKH,
        kh.Ten AS TenKH,
        hdct.GiaBan,
        hdct.SoLuongSP,
        hdct.TongTien
    FROM
        dbo.HoaDonChiTiet hdct
    JOIN
        dbo.HoaDon hd ON hdct.ID_HoaDon = hd.ID
    JOIN
        dbo.KhachHang kh ON hdct.ID_KhachHang = kh.ID
    JOIN
        dbo.SanPhamChiTiet spct ON hdct.ID_SanPhamCT = spct.ID
    JOIN
        dbo.SanPham sp ON spct.ID_SP = sp.ID
    JOIN
        dbo.VoucherCT vc ON hdct.ID_VoucherCT = vc.ID

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
WHERE hd.ID = 1

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
    WHERE hd.TrangThai = CAST(0 AS bit)

	select * from NhanVien WHERE Ma = N'ph36590'
	select * from HoaDon
		select * from HoaDonChiTiet
delete HoaDon where id = 3
	delete HoaDonChiTiet where id = 5

	select * from NhanVien
	update	NhanVien set Ma = ?, Passwords = ?, Ten = ?, SDT = ?, Email = ?, Anh = ? , ChucVu = ?, TrangThai = ? where ID = ?
	select * from HoaDon
	UPDATE HoaDon SET NgayTao = ?, TongTien =?, TrangThai =?, ID_NhanVien =? where Ma = ?
	select * from HoaDon join HoaDonChiTiet on HoaDon.ID  = HoaDonChiTiet.ID

	SELECT HoaDon.Ma, NhanVien.Ten, NgayTao, TongTien, HOADON.TrangThai   
	FROM HoaDon 
	JOIN NhanVien ON HoaDon.ID_NhanVien = NhanVien.ID where HoaDon.TrangThai = 1
	SELECT COUNT(*) AS SOLUONG FROM HOADON
	SELECT  SUM(TongTien) AS TongTien FROM HOADON


	SELECT 
		HoaDon.Ma, NhanVien.Ten, NgayTao, TongTien, HOADON.TrangThai   
    FROM 
        HoaDon 
    JOIN 
        NhanVien ON HoaDon.ID_NhanVien = NhanVien.ID						
	ORDER BY
		HoaDon.ID OFFSET 0 ROWS FETCH NEXT 2 ROWS ONLY

		SELECT * FROM HOADON
		SELECT * FROM HOADONCHITIET

	DELETE HOADONCHITIET WHERE ID = 3
	DELETE HOADON WHERE ID = 1
	select SoLuong from SanPhamChiTiet where MaSP = ?
-- hiển thị sản phẩm trạng thái đang hoạt động 

SELECT 
        spct.ID,  
        spct.MaSP, 
        nv.Ma AS MaNV,
        nv.Ten AS TenNV,
        sp.Ten AS TenSP, 
        spct.Gia, 
        spct.SoLuong, 
        sz.Ten AS Size, 
        ms.Ten AS MauSac, 
        cl.Ten AS ChatLieu, 
        dm.Ten AS DanhMuc, 
        th.Ten AS ThuongHieu, 
        spct.TrangThai,
		count(spct.id) as sl
    FROM 
        dbo.SanPhamChiTiet spct
    JOIN 
        dbo.SanPham sp ON spct.ID_SP = sp.ID
    JOIN 
        dbo.MauSac ms ON spct.ID_MauSac = ms.ID
    JOIN 
        dbo.Size sz ON spct.ID_Size = sz.ID
    JOIN 
        dbo.ChatLieu cl ON spct.ID_ChatLieu = cl.ID
    JOIN 
        dbo.DanhMuc dm ON sp.ID_DanhMuc = dm.ID
    JOIN 
        dbo.NhanVien nv ON sp.ID_NhanVien = nv.ID
    JOIN 
        dbo.ThuongHieu th ON sp.ID_ThuongHieu = th.ID
    WHERE 
		spct.TrangThai = CAST(1 AS bit)
	ORDER BY
        spct.ID OFFSET 0 ROWS FETCH NEXT 5 ROWS ONLY
	GROUP BY spct.id;

	-----------------
	
------------------------------------------
select * from SanPhamChiTiet
update SanPhamChiTiet set TrangThai =0 where ID = 7

SELECT 
        spct.ID,  
        spct.MaSP, 
        nv.Ma AS MaNV,
        nv.Ten AS TenNV,
        sp.Ten AS TenSP, 
        spct.Gia, 
        spct.SoLuong, 
        sz.Ten AS Size, 
        ms.Ten AS MauSac, 
        cl.Ten AS ChatLieu, 
        dm.Ten AS DanhMuc, 
        th.Ten AS ThuongHieu, 
        spct.TrangThai
    FROM 
        dbo.SanPhamChiTiet spct
    JOIN 
        dbo.SanPham sp ON spct.ID_SP = sp.ID
    JOIN 
        dbo.MauSac ms ON spct.ID_MauSac = ms.ID
    JOIN 
        dbo.Size sz ON spct.ID_Size = sz.ID
    JOIN 
        dbo.ChatLieu cl ON spct.ID_ChatLieu = cl.ID
    JOIN 
        dbo.DanhMuc dm ON sp.ID_DanhMuc = dm.ID
    JOIN 
        dbo.NhanVien nv ON sp.ID_NhanVien = nv.ID
    JOIN 
        dbo.ThuongHieu th ON sp.ID_ThuongHieu = th.ID
	WHERE spct.TrangThai = 1 

UPDATE 
	SanPhamChiTiet 
SET TrangThai = 1 
WHERE ID = ?

SELECT * 
FROM 
(
    SELECT 
        spct.ID,  
        spct.MaSP, 
        nv.Ma AS MaNV,
        nv.Ten AS TenNV,
        sp.Ten AS TenSP, 
        spct.Gia, 
        spct.SoLuong, 
        sz.Ten AS Size, 
        ms.Ten AS MauSac, 
        cl.Ten AS ChatLieu, 
        dm.Ten AS DanhMuc, 
        th.Ten AS ThuongHieu, 
        spct.TrangThai
    FROM 
        dbo.SanPhamChiTiet spct
    JOIN 
        dbo.SanPham sp ON spct.ID_SP = sp.ID
    JOIN 
        dbo.MauSac ms ON spct.ID_MauSac = ms.ID
    JOIN 
        dbo.Size sz ON spct.ID_Size = sz.ID
    JOIN 
        dbo.ChatLieu cl ON spct.ID_ChatLieu = cl.ID
    JOIN 
        dbo.DanhMuc dm ON sp.ID_DanhMuc = dm.ID
    JOIN 
        dbo.NhanVien nv ON sp.ID_NhanVien = nv.ID
    JOIN 
        dbo.ThuongHieu th ON sp.ID_ThuongHieu = th.ID
    WHERE 
        (sp.Ten LIKE '%nam%' OR dm.Ten LIKE '%nam%' OR th.Ten LIKE '%nike%')
        AND spct.TrangThai = 1
) AS FilteredResults
ORDER BY ID
OFFSET 0 ROWS FETCH NEXT 5 ROWS ONLY;

select * from SanPham where Ten like '%nam%'

SELECT * 
FROM 
(
    SELECT 
        spct.ID,  
        spct.MaSP, 
        nv.Ma AS MaNV,
        nv.Ten AS TenNV,
        sp.Ten AS TenSP, 
        spct.Gia, 
        spct.SoLuong, 
        sz.Ten AS Size, 
        ms.Ten AS MauSac, 
        cl.Ten AS ChatLieu, 
        dm.Ten AS DanhMuc, 
        th.Ten AS ThuongHieu, 
        spct.TrangThai
    FROM 
        dbo.SanPhamChiTiet spct
    JOIN 
        dbo.SanPham sp ON spct.ID_SP = sp.ID
    JOIN 
        dbo.MauSac ms ON spct.ID_MauSac = ms.ID
    JOIN 
        dbo.Size sz ON spct.ID_Size = sz.ID
    JOIN 
        dbo.ChatLieu cl ON spct.ID_ChatLieu = cl.ID
    JOIN 
        dbo.DanhMuc dm ON sp.ID_DanhMuc = dm.ID
    JOIN 
        dbo.NhanVien nv ON sp.ID_NhanVien = nv.ID
    JOIN 
        dbo.ThuongHieu th ON sp.ID_ThuongHieu = th.ID
    WHERE 
        (sp.Ten LIKE ? OR dm.Ten LIKE ? OR th.Ten LIKE ?)
        AND spct.TrangThai = 1
) AS FilteredResults
ORDER BY ID
OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
-------------
--Doanh thu ngày 
go
		CREATE VIEW DoanhThuNgay AS
		SELECT SUM(TongTien * 1) AS DoanhThuNgay
		FROM HoaDon
		WHERE DAY(NgayTao) = DAY(GETDATE());
go
--Doanh thu theo tháng
		CREATE VIEW DoanhThuThang AS
		SELECT SUM(TongTien * 1) AS DoanhThuThang
		FROM HoaDon
		WHERE MONTH(NgayTao) = MONTH(GETDATE());
go
--Doanh thu theo năm
		CREATE VIEW DoanhThuNam AS
		SELECT SUM(TongTien * 1) AS DoanhThuNam
		FROM HoaDon
		WHERE YEAR(NgayTao) = YEAR(GETDATE());
go
--Doanh thu tổng đơn
		CREATE VIEW TongDonHang AS
		SELECT COUNT(ID) AS TongHoaDon FROM HoaDon
go
--Sản phẩm bán chạy
		CREATE VIEW SanPhamBanChay 
		AS
		SELECT HoaDon.ID 
			AS IDHOADON, SanPham.Ma AS MASANPHAM, HoaDon.TongTien, SanPham.Ten 
			AS TENSANPHAM, HoaDon.NgayTao, HoaDonChiTiet.SoLuongSP 
			AS SLBAN 
		FROM SanPham 
			JOIN HoaDon ON SanPham.ID = HoaDon.ID 
			JOIN HoaDonChiTiet ON HoaDon.ID = HoaDonChiTiet.ID 
		WHERE 
			YEAR(HoaDon.NgayTao) =  YEAR(GETDATE()) 
			GROUP BY
			SanPham.Ma,
			SanPham.Ten,
			HoaDon.NgayTao, 
			HoaDonChiTiet.SoLuongSP,
			HoaDon.TongTien,
			HoaDon.ID, 
			HoaDonChiTiet.ID
go
-----------------------------------------------
USE DA1
SELECT * FROM DoanhThuNgay 
SELECT * FROM DoanhThuThang
SELECT * FROM DoanhThuNam
SELECT * FROM TongDonHang
SELECT * FROM SanPhamBanChay

select * from SanPhamChiTiet
select * from SanPham

SELECT 
        spct.ID,  
        spct.MaSP, 
        nv.Ma AS MaNV,
        nv.Ten AS TenNV,
        sp.Ten AS TenSP, 
        spct.Gia, 
        spct.SoLuong, 
        sz.Ten AS Size, 
        ms.Ten AS MauSac, 
        cl.Ten AS ChatLieu, 
        dm.Ten AS DanhMuc, 
        th.Ten AS ThuongHieu, 
        spct.TrangThai
    FROM 
        dbo.SanPhamChiTiet spct
    JOIN 
        dbo.SanPham sp ON spct.ID_SP = sp.ID
    JOIN 
        dbo.MauSac ms ON spct.ID_MauSac = ms.ID
    JOIN 
        dbo.Size sz ON spct.ID_Size = sz.ID
    JOIN 
        dbo.ChatLieu cl ON spct.ID_ChatLieu = cl.ID
    JOIN 
        dbo.DanhMuc dm ON sp.ID_DanhMuc = dm.ID
    JOIN 
        dbo.NhanVien nv ON sp.ID_NhanVien = nv.ID
    JOIN 
        dbo.ThuongHieu th ON sp.ID_ThuongHieu = th.ID
    WHERE sp.Ten LIKE ? AND spct.TrangThai = CAST(1 AS bit)

SELECT 
    HoaDon.Ma, NhanVien.Ten, NgayTao, TongTien, HOADON.TrangThai   
    FROM 
        HoaDon 
    JOIN 
        NhanVien ON HoaDon.ID_NhanVien = NhanVien.ID	
	WHERE HoaDon.TrangThai = 1
ORDER BY
    HoaDon.ID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
	select* from HoaDon

SELECT 
    HoaDon.Ma, NhanVien.Ten, NgayTao, TongTien, HOADON.TrangThai   
    FROM 
        HoaDon 
    JOIN 
        NhanVien ON HoaDon.ID_NhanVien = NhanVien.ID	
WHERE ( HoaDon.TrangThai = 1) and hoadon.ngaytao between ? and ?
ORDER BY
    HoaDon.ID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
	--Tìm hoá đơn theo mã và theo tên

	SELECT 
    HoaDon.Ma, NhanVien.Ten, NgayTao, TongTien, HOADON.TrangThai   
    FROM 
        HoaDon 
    JOIN 
        NhanVien ON HoaDon.ID_NhanVien = NhanVien.ID
	WHERE HoaDon.Ma LIKE ? OR NhanVien.Ten LIKE ?

			SELECT * FROM ChatLieu
			SELECT * FROM Size
			SELECT * FROM MauSac
			SELECT * FROM DanhMuc
			SELECT * FROM ThuongHieu
			SELECT * FROM KhachHang
			SELECT * FROM NhanVien
			SELECT * FROM SanPham
			SELECT * FROM SanPhamChiTiet --
			SELECT * FROM Voucher
		
			SELECT * FROM HoaDon
			SELECT * FROM HoaDonChiTiet --

SELECT 
    hd.ID, 
    hd.Ma, 
    nv.Ma AS MaNV,
    nv.Ten AS TenNV,
    hd.NgayTao, 
    hd.TongTien, 
    hd.TrangThai,
	kh.SDT
FROM 
    dbo.HoaDon hd
JOIN 
    dbo.NhanVien nv ON hd.ID_NhanVien = nv.ID
JOIN dbo.KhachHang kh on kh.id = hd.ID_KhachHang
WHERE hd.TrangThai = 1 and  hd.ID = ?

	SELECT 
	hd.ID, 
	hd.Ma,                             
	nv.Ten,
	hd.NgayTao, 
	hd.TongTien, 
	hd.TrangThai,
	kh.SDT
	FROM 
	dbo.HoaDon hd
	JOIN 
	dbo.NhanVien nv ON hd.ID_NhanVien = nv.ID
	JOIN 
	dbo.KhachHang kh on kh.id = hd.ID_KhachHang
	WHERE 
	( hd.TrangThai = 1) and (hd.Ma LIKE ? OR nv.Ten LIKE ? OR kh.SDT LIKE ? )
	ORDER BY
	hd.ID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY

	select * from KhachHang
	SELECT * FROM HoaDon
	SELECT * FROM HoaDonChiTiet
	ALTER TABLE KhachHang
	ALTER COLUMN SDT VARCHAR(10) NULL;

	delete HoaDonChiTiet where id= 14
	delete HoaDon where ID = 9

SELECT * FROM HoaDon
SELECT * FROM HoaDonChiTiet --
SELECT * FROM SanPham
SELECT * FROM SanPhamChiTiet --

use DA1
SELECT * FROM ChatLieu
select * from KhachHang
SELECT * FROM Size
SELECT * FROM MauSac
SELECT * FROM DanhMuc
SELECT * FROM ThuongHieu
SELECT * FROM NhanVien
SELECT * FROM SanPham
SELECT * FROM SanPhamChiTiet --
SELECT * FROM Voucher
SELECT * FROM HoaDon
SELECT * FROM HoaDonChiTiet --

SELECT * FROM Voucher
SELECT 
    hd.ID, 
    hd.Ma,                             
    nv.Ten,
    hd.NgayTao, 
    hd.TongTien, 
    hd.TrangThai,
    kh.SDT,
	vc.Ten 
FROM 
    dbo.HoaDon hd
JOIN 
    dbo.NhanVien nv ON hd.ID_NhanVien = nv.ID
JOIN 
    dbo.KhachHang kh on kh.id = hd.ID_KhachHang
JOIN 
	dbo.Voucher vc on vc.ID = hd.ID_Voucher
WHERE 
    hd.TrangThai IN (1)
ORDER BY
    hd.ID OFFSET 0 ROWS FETCH NEXT 5 ROWS ONLY
select * from HoaDon
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
JOIN 
dbo.Voucher vc on vc.ID = hd.ID_Voucher
WHERE 
    hd.TrangThai IN (1)

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
    dbo.KhachHang kh ON kh.id = hd.ID_KhachHang
LEFT JOIN 
    dbo.Voucher vc ON vc.ID = hd.ID_Voucher
WHERE 
    hd.TrangThai = 1
    
	select hd.Ma, vc.Ten  from HoaDon hd  left join Voucher vc on hd.ID_Voucher = vc.ID

	use da1
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
                       WHERE dbo.Voucher.ID = 1


select * from Voucher
SELECT * 
FROM 
(
SELECT *
FROM KhachHang
WHERE Ten LIKE '%hiếu%' OR Ma LIKE '%59%' OR SDT LIKE '%321%'
) AS FilteredResults
ORDER BY ID
OFFSET 0 ROWS FETCH NEXT 9 ROWS ONLY;

	go

CREATE VIEW DonHuy AS
SELECT COUNT(ID) AS DonHuy FROM HoaDon WHERE TrangThai = 3
Select * from SanPhamChiTiet
Select * from SanPham
delete SanPham where id = 1003
select * from khachhang
delete khachhang where id = 1004
