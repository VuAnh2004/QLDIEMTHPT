package model.DAO.impl;

import config.DBConnection;
import model.bean.Menu;
import model.DAO.MenuDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuDAOImpl implements MenuDAO {

    @Override
    public List<Menu> getUserMenus() {

        List<Menu> allMenus = new ArrayList<>();
        List<Menu> parentMenus = new ArrayList<>();

        String sql = """
            SELECT * FROM Menu
            WHERE IsActive = 1
            ORDER BY Levels ASC, MenuOrder ASC
        """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Menu m = new Menu();
                m.setMenuID(rs.getInt("MenuID"));
                m.setMenuName(rs.getString("MenuName"));
                m.setActive(rs.getBoolean("IsActive"));
                m.setControllerName(rs.getString("ControllerName"));
                m.setActionName(rs.getString("ActionName"));
                m.setLevels(rs.getInt("Levels"));
                m.setParentID(rs.getInt("ParentID"));
                m.setMenuOrder(rs.getInt("MenuOrder"));
                m.setPosition(rs.getInt("Position"));
                m.setIcon(rs.getString("Icon"));
                m.setIdName(rs.getString("IDName"));
                m.setItemTarget(rs.getString("ItemTarget"));
                m.setSubMenus(new ArrayList<>());

                allMenus.add(m);
            }

            // ===== GOM MENU CON VÀO MENU CHA =====
            for (Menu m : allMenus) {
                if (m.getLevels() == 1) {
                    parentMenus.add(m);
                } else {
                    for (Menu p : parentMenus) {
                        if (m.getParentID() == p.getMenuID()) {
                            p.getSubMenus().add(m);
                            break;
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return parentMenus;
    }
}
