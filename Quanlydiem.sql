-- ==========================================
-- TẠO DATABASE
-- ==========================================
CREATE DATABASE QuanLyDiem;
GO

USE QuanLyDiem;
GO

-- ==========================================
-- BẢNG: QLKhoaHoc - Quản lý khóa học (niên khóa)
-- ==========================================
CREATE TABLE QLKhoaHoc (
    CourseID INT IDENTITY(1,1) PRIMARY KEY,
    StartYear INT NOT NULL,
    EndYear INT NOT NULL,
    Cohort INT NULL, -- Khóa (VD: K60, K61)
    IsActive BIT NOT NULL DEFAULT 1
);
GO

-- ==========================================
-- BẢNG: QLKhoi - Quản lý khối (10, 11, 12)
-- ==========================================
CREATE TABLE QLKhoi (
    GradeLevelId INT IDENTITY(1,1) PRIMARY KEY,
    GradeName NVARCHAR(50) NOT NULL, -- VD: "Khối 10"
    Description NVARCHAR(255) NULL,
    IsActive BIT NOT NULL DEFAULT 1
);
GO

-- ==========================================
-- BẢNG: QLHocKy - Quản lý học kỳ
-- ==========================================
CREATE TABLE QLHocKy (
    SemesterID INT IDENTITY(1,1) PRIMARY KEY,
    SemesterName NVARCHAR(50) NOT NULL,  -- VD: "Học kỳ 1"
    SemesterCode NVARCHAR(50) NOT NULL,  -- VD: "2024-2025"
    IsActive BIT NOT NULL DEFAULT 1
);
GO

-- ==========================================
-- BẢNG: QLHocSinh - Quản lý thông tin học sinh
-- ==========================================
CREATE TABLE QLHocSinh (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    StudentID VARCHAR(20) NOT NULL UNIQUE,
    FullName NVARCHAR(100) NOT NULL,
    Birth DATE NULL,
    Gender NVARCHAR(10) NULL,
    Address NVARCHAR(255) NULL,
    Nation NVARCHAR(50) NULL,
    Religion NVARCHAR(50) NULL,
    StatusStudent NVARCHAR(50) NULL,  -- Trạng thái học (đang học, nghỉ, bảo lưu)
    NumberPhone VARCHAR(15) NULL,
    Images NVARCHAR(255) NULL,
    Hamlet NVARCHAR(100) NULL,
    Commune NVARCHAR(100) NULL,
    Province NVARCHAR(100) NULL,
    Nationality NVARCHAR(50) NULL,
    IsActive BIT NOT NULL DEFAULT 1
);
GO

-- ==========================================
-- BẢNG: QLGiaoVien - Quản lý giáo viên
-- ==========================================
CREATE TABLE QLGiaoVien (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    TeacherID VARCHAR(20) NOT NULL UNIQUE,
    FullName NVARCHAR(100) NOT NULL,
    Birth DATE NULL,
    Gender NVARCHAR(10) NULL,
    Address NVARCHAR(255) NULL,
    StatusTeacher NVARCHAR(50) NULL, -- Đang dạy, Nghỉ hưu, Nghỉ thai sản...
    CCCD VARCHAR(20) NULL,
    Nation NVARCHAR(50) NULL,
    Religion NVARCHAR(50) NULL,
    GroupDV NVARCHAR(50) NULL, -- Đảng viên(Vào đảng,chưa vào,..)
    NumberPhone VARCHAR(15) NULL,
    NumberBHXH VARCHAR(20) NULL,
    Images NVARCHAR(255) NULL,
    department_ID INT NULL, -- Nếu sau này bổ sung QLBoMon
    IsActive BIT NOT NULL DEFAULT 1
);
GO

-- ==========================================
-- BẢNG: QLMonHoc - Quản lý môn học
-- ==========================================
CREATE TABLE QLMonHoc (
    SubjectID INT IDENTITY(1,1) PRIMARY KEY,
    SubjectName NVARCHAR(100) NOT NULL,
    NumberOfLesson INT NOT NULL, -- Số tiết học
    Semester NVARCHAR(50) NULL,  -- HK1 / HK2
    Link NVARCHAR(255) NULL,     -- Tài liệu / học liệu
    department_ID INT NULL,
    IsActive BIT NOT NULL DEFAULT 1
);
GO

-- ==========================================
-- BẢNG: QLLopHoc - Quản lý lớp học
-- ==========================================
CREATE TABLE QLLopHoc (
    ClassID INT IDENTITY(1,1) PRIMARY KEY,
    ClassName NVARCHAR(50) NOT NULL,       -- VD: "12A1"
    GradeLevelId INT NOT NULL,
    CourseID INT NULL,
    MaxStudents INT NOT NULL DEFAULT 40,
    CurrentStudents INT NULL,
    SchoolYear NVARCHAR(50) NOT NULL,      -- "2024-2025"
    IsActive BIT NOT NULL DEFAULT 1,
    FOREIGN KEY (GradeLevelId) REFERENCES QLKhoi(GradeLevelId),
    FOREIGN KEY (CourseID) REFERENCES QLKhoaHoc(CourseID)
);
GO

-- ==========================================
-- BẢNG: QLHocSinhLopHoc - Quan hệ HS - Lớp - Học kỳ - Khóa học
-- ==========================================
CREATE TABLE QLHocSinhLopHoc (
    HocSinhLopHocID INT IDENTITY(1,1) PRIMARY KEY,
    StudentID VARCHAR(20) NOT NULL,
    ClassID INT NOT NULL,
    SemesterID INT NULL,
    CourseID INT NULL,
    IsActive BIT NOT NULL DEFAULT 1,
    FOREIGN KEY (StudentID) REFERENCES QLHocSinh(StudentID),
    FOREIGN KEY (ClassID) REFERENCES QLLopHoc(ClassID),
    FOREIGN KEY (SemesterID) REFERENCES QLHocKy(SemesterID),
    FOREIGN KEY (CourseID) REFERENCES QLKhoaHoc(CourseID)
);
GO

-- ==========================================
-- BẢNG: QLGVMonHoc - Quan hệ N-N giữa Giáo viên và Môn học
-- ==========================================
CREATE TABLE QLGVMonHoc (
    TeacherID VARCHAR(20) NOT NULL,
    SubjectID INT NOT NULL,
    PRIMARY KEY (TeacherID, SubjectID),
    FOREIGN KEY (TeacherID) REFERENCES QLGiaoVien(TeacherID),
    FOREIGN KEY (SubjectID) REFERENCES QLMonHoc(SubjectID)
);
GO

-- ==========================================
-- BẢNG: QLDiem - Quản lý điểm học sinh
-- ==========================================
CREATE TABLE QLDiem (
    GradeID INT IDENTITY(1,1) PRIMARY KEY,
    StudentID VARCHAR(20) NOT NULL,
    SubjectID INT NOT NULL,
    SemesterID INT NOT NULL,
    TeacherID VARCHAR(20) NULL,
    OralScore1 FLOAT NULL,
    OralScore2 FLOAT NULL,
    OralScore3 FLOAT NULL,
    Quiz15Min1 FLOAT NULL,
    Quiz15Min2 FLOAT NULL,
    MidtermScore FLOAT NULL,
    Final_score FLOAT NULL,
    AverageScore FLOAT NULL,
    GradeCategory NVARCHAR(50) NULL,  -- Giỏi / Khá / Trung bình...
    Notes NVARCHAR(255) NULL,
    Create_date DATETIME DEFAULT GETDATE(),
    UpdatedDate DATETIME NULL,
    IsActive BIT NOT NULL DEFAULT 1,
    FOREIGN KEY (TeacherID) REFERENCES QLGiaoVien(TeacherID),
    FOREIGN KEY (StudentID) REFERENCES QLHocSinh(StudentID),
    FOREIGN KEY (SubjectID) REFERENCES QLMonHoc(SubjectID),
    FOREIGN KEY (SemesterID) REFERENCES QLHocKy(SemesterID)
);
GO
-- ==========================================
-- BẢNG: Account - Tài khoản người dùng
-- ==========================================
CREATE TABLE Account (
    UserID INT IDENTITY(1,1) PRIMARY KEY,
    UserName NVARCHAR(100) NOT NULL UNIQUE,
    Email NVARCHAR(255) NOT NULL UNIQUE,
    Password NVARCHAR(255) NOT NULL,
    IsActive BIT NOT NULL DEFAULT 1,
    IsFirstLogin BIT NULL,
    [Date] DATETIME NULL,
    [Count] INT NULL
);
GO

-- ==========================================
-- BẢNG: tblRoles - Vai trò (quyền hạn)
-- ==========================================
CREATE TABLE tblRoles (
    RoleID INT IDENTITY(1,1) PRIMARY KEY,
    RoleName NVARCHAR(100) NOT NULL UNIQUE
);
GO

-- ==========================================
-- BẢNG: tblUsersRoles - Liên kết người dùng và vai trò (N-N)
-- ==========================================
CREATE TABLE tblUsersRoles (
    UserID INT NOT NULL,
    RoleID INT NOT NULL,
    PRIMARY KEY (UserID, RoleID),
    FOREIGN KEY (UserID) REFERENCES Account(UserID),
    FOREIGN KEY (RoleID) REFERENCES tblRoles(RoleID)
);
GO
