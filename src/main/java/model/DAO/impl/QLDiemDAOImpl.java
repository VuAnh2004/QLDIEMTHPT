package model.DAO.impl;

import config.DBConnection;
import model.DAO.QLDiemDAO;
import model.bean.QLDiem;
import model.bean.QLHocKy;
import model.bean.QLHocSinh;
import model.bean.QLKhoaHoc;
import model.bean.QLLopHoc;
import model.bean.QLMonHoc;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class QLDiemDAOImpl implements QLDiemDAO {

    private QLDiem mapResultSetToQLDiem(ResultSet rs) throws SQLException {
        QLDiem diem = new QLDiem();
        diem.setGradeID(rs.getInt("GradeID"));
        diem.setStudentID(rs.getString("StudentID"));
        diem.setSubjectID(rs.getInt("SubjectID"));
        diem.setSemesterID(rs.getInt("SemesterID"));
        diem.setTeacherID(rs.getString("TeacherID"));
        diem.setOralScore1(rs.getDouble("OralScore1"));
        diem.setOralScore2(rs.getDouble("OralScore2"));
        diem.setOralScore3(rs.getDouble("OralScore3"));
        diem.setQuiz15Min1(rs.getDouble("Quiz15Min1"));
        diem.setQuiz15Min2(rs.getDouble("Quiz15Min2"));
        diem.setMidtermScore(rs.getDouble("MidtermScore"));
        diem.setFinalScore(rs.getDouble("Final_Score"));
        diem.setAverageScore(rs.getDouble("AverageScore"));
        diem.setGradeCategory(rs.getString("GradeCategory"));
        diem.setNotes(rs.getString("Notes"));
        diem.setCreateDate(rs.getTimestamp("Create_date"));
        diem.setUpdatedDate(rs.getTimestamp("UpdatedDate"));
        diem.setActive(rs.getBoolean("IsActive"));
        return diem;
    }

    @Override
    public List<QLDiem> getAll() {
        List<QLDiem> list = new ArrayList<>();
        String sql =
        	    "SELECT d.*, " +
        	    "h.FullName AS StudentName, " +
        	    "m.SubjectName, " +
        	    "hk.SemesterName, " +
        	    "hk.SemesterCode," +
        	    "l.ClassName, hl.ClassID, " +
        	    "kh.CourseID, kh.StartYear, kh.EndYear, kh.Cohort " +
        	    "FROM QLDiem d " +

        	    "LEFT JOIN QLHocSinh h ON d.StudentID = h.StudentID " +
        	    "LEFT JOIN QLMonHoc m ON d.SubjectID = m.SubjectID " +
        	    "LEFT JOIN QLHocKy hk ON d.SemesterID = hk.SemesterID " +

        	    "LEFT JOIN QLHocSinhLopHoc hl " +
        	    " ON d.StudentID = hl.StudentID AND d.SemesterID = hl.SemesterID " +

        	    "LEFT JOIN QLLopHoc l ON hl.ClassID = l.ClassID " +
        	    "LEFT JOIN QLKhoaHoc kh ON hl.CourseID = kh.CourseID " +
        	    "ORDER BY d.GradeID";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                QLDiem d = new QLDiem();
                
                // 1. Ánh xạ các thuộc tính chính của QLDiem
                d.setGradeID(rs.getInt("GradeID"));
                d.setStudentID(rs.getString("StudentID"));
                d.setSubjectID(rs.getInt("SubjectID"));
                d.setSemesterID(rs.getInt("SemesterID"));
                d.setTeacherID(rs.getString("TeacherID"));
                d.setOralScore1(rs.getDouble("OralScore1"));
                d.setOralScore2(rs.getDouble("OralScore2"));
                d.setOralScore3(rs.getDouble("OralScore3"));
                d.setQuiz15Min1(rs.getDouble("Quiz15Min1"));
                d.setQuiz15Min2(rs.getDouble("Quiz15Min2"));
                d.setMidtermScore(rs.getDouble("MidtermScore"));
                d.setFinalScore(rs.getDouble("Final_score"));
                d.setAverageScore(rs.getDouble("AverageScore"));
                d.setGradeCategory(rs.getString("GradeCategory"));
                d.setNotes(rs.getString("Notes"));
                d.setActive(rs.getBoolean("IsActive"));

                // 2. Ánh xạ đối tượng liên kết (Xử lý NULL an toàn)

                // Học sinh (QLHocSinh)
                QLHocSinh hs = new QLHocSinh();
                hs.setStudentID(rs.getString("StudentID"));
                hs.setFullName(rs.getString("StudentName")); // Lấy từ Alias
                d.setHocsinh(hs);

                // Môn học (QLMonHoc)
                QLMonHoc monHoc = new QLMonHoc();
                monHoc.setSubjectID(rs.getInt("SubjectID"));
                monHoc.setSubjectName(rs.getString("SubjectName"));
                d.setMonHoc(monHoc);

                // Học kỳ (QLHocKy)
                QLHocKy hk = new QLHocKy();
                hk.setSemesterId(rs.getInt("SemesterID"));
                hk.setSemesterName(rs.getString("SemesterName"));   // HK1, HK2
                hk.setSemesterCode(rs.getString("SemesterCode"));   // 2025-2026

                

                d.setHocKy(hk);
             // ===== KHÓA HỌC (QLKhoaHoc) =====
                Integer courseId = rs.getObject("CourseID", Integer.class);
                if (courseId != null) {
                    QLKhoaHoc khoaHoc = new QLKhoaHoc();
                    khoaHoc.setCourseID(courseId);
                    khoaHoc.setStartYear(rs.getInt("StartYear"));
                    khoaHoc.setEndYear(rs.getInt("EndYear"));
                    khoaHoc.setCohort(rs.getInt("Cohort"));
                    d.setKhoaHoc(khoaHoc);
                }


                // Lớp học (QLLopHoc) - Cần kiểm tra NULL vì dùng LEFT JOIN
                String className = rs.getString("ClassName");
                
                if (className != null) {
                    QLLopHoc lop = new QLLopHoc();
                    // Lấy ClassID từ hl.ClassID (đã thêm vào SELECT)
                    lop.setClassID(rs.getInt("ClassID")); 
                    lop.setClassName(className);
                    d.setLopHoc(lop);
                } else {
                    // Nếu không có lớp, set QLLopHoc là null (hoặc đối tượng mặc định)
                    d.setLopHoc(null); 
                }

                list.add(d);
            }

        } catch (SQLException e) {
            System.err.println("Lỗi truy vấn SQL hoặc kết nối cơ sở dữ liệu khi lấy điểm: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public QLDiem getById(int gradeID) {
        String sql = "SELECT * FROM QLDiem WHERE GradeID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gradeID);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToQLDiem(rs);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void insert(QLDiem diem) {
        String sql = "INSERT INTO QLDiem (StudentID, SubjectID, SemesterID, TeacherID, OralScore1, OralScore2, OralScore3, Quiz15Min1, Quiz15Min2, MidtermScore, Final_score, AverageScore, GradeCategory, Notes, IsActive) "
                   + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, diem.getStudentID());
            ps.setInt(2, diem.getSubjectID());
            ps.setInt(3, diem.getSemesterID());
            ps.setString(4, diem.getTeacherID());
            ps.setObject(5, diem.getOralScore1());
            ps.setObject(6, diem.getOralScore2());
            ps.setObject(7, diem.getOralScore3());
            ps.setObject(8, diem.getQuiz15Min1());
            ps.setObject(9, diem.getQuiz15Min2());
            ps.setObject(10, diem.getMidtermScore());
            ps.setObject(11, diem.getFinalScore());
            ps.setObject(12, diem.getAverageScore());
            ps.setString(13, diem.getGradeCategory());
            ps.setString(14, diem.getNotes());
            ps.setBoolean(15, diem.isActive());

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    diem.setGradeID(rs.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi thêm điểm.", e);
        }
    }

    @Override
    public void update(QLDiem diem) {
        String sql = "UPDATE QLDiem SET StudentID=?, SubjectID=?, SemesterID=?, TeacherID=?, OralScore1=?, OralScore2=?, OralScore3=?, Quiz15Min1=?, Quiz15Min2=?, MidtermScore=?, Final_score=?, AverageScore=?, GradeCategory=?, Notes=?, IsActive=?, UpdatedDate=GETDATE() WHERE GradeID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, diem.getStudentID());
            ps.setInt(2, diem.getSubjectID());
            ps.setInt(3, diem.getSemesterID());
            ps.setString(4, diem.getTeacherID());
            ps.setObject(5, diem.getOralScore1());
            ps.setObject(6, diem.getOralScore2());
            ps.setObject(7, diem.getOralScore3());
            ps.setObject(8, diem.getQuiz15Min1());
            ps.setObject(9, diem.getQuiz15Min2());
            ps.setObject(10, diem.getMidtermScore());
            ps.setObject(11, diem.getFinalScore());
            ps.setObject(12, diem.getAverageScore());
            ps.setString(13, diem.getGradeCategory());
            ps.setString(14, diem.getNotes());
            ps.setBoolean(15, diem.isActive());
            ps.setInt(16, diem.getGradeID());

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật điểm.", e);
        }
    }

    @Override
    public void delete(int gradeID) {
        String sql = "DELETE FROM QLDiem WHERE GradeID=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, gradeID);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa điểm.", e);
        }
    }
    @Override
    public List<QLDiem> getByTeacher(String teacherID) {
        List<QLDiem> list = new ArrayList<>();

        String sql =
            "SELECT d.*, h.FullName AS StudentName, m.SubjectName, hk.SemesterName, " +
            "l.ClassName, hl.ClassID, kh.CourseID, kh.StartYear, kh.EndYear, kh.Cohort " +
            "FROM QLDiem d " +
            "LEFT JOIN QLHocSinh h ON d.StudentID = h.StudentID " +
            "LEFT JOIN QLMonHoc m ON d.SubjectID = m.SubjectID " +
            "LEFT JOIN QLHocKy hk ON d.SemesterID = hk.SemesterID " +
            "LEFT JOIN QLHocSinhLopHoc hl ON d.StudentID = hl.StudentID AND d.SemesterID = hl.SemesterID " +
            "LEFT JOIN QLLopHoc l ON hl.ClassID = l.ClassID " +
            "LEFT JOIN QLKhoaHoc kh ON hl.CourseID = kh.CourseID " +
            "WHERE d.TeacherID = ? " +
            "ORDER BY d.GradeID";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, teacherID);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                QLDiem d = mapResultSetToQLDiem(rs);

                QLHocSinh hs = new QLHocSinh();
                hs.setStudentID(rs.getString("StudentID"));
                hs.setFullName(rs.getString("StudentName"));
                d.setHocsinh(hs);

                QLMonHoc mh = new QLMonHoc();
                mh.setSubjectID(rs.getInt("SubjectID"));
                mh.setSubjectName(rs.getString("SubjectName"));
                d.setMonHoc(mh);

                list.add(d);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }


}
