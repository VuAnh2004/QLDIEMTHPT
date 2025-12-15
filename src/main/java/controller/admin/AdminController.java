package controller.admin;

import model.DAO.AdminMenuDAO;
import model.DAO.impl.AdminMenuDAOImpl;
import model.bean.AdminMenu;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/admin")
public class AdminController extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private AdminMenuDAO menuDAO = new AdminMenuDAOImpl();

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setAttribute("pageTitle", " Quản Trị Hệ Thống");
		List<AdminMenu> flatMenus = menuDAO.getActiveMenus();
		String contextPath = request.getContextPath();

		List<AdminMenu> treeMenus = buildMenuTree(flatMenus, contextPath);

		request.setAttribute("menus", treeMenus);
		request.getRequestDispatcher("/WEB-INF/admin/layout.jsp").forward(request, response);
	}

	private List<AdminMenu> buildMenuTree(List<AdminMenu> flatMenus, String contextPath) {
	
		List<AdminMenu> allMenus = new ArrayList<>(flatMenus);
		Map<Integer, AdminMenu> menuMap = new HashMap<>();

		
		for (AdminMenu m : allMenus) {
			menuMap.put((int) m.getAdminMenuID(), m);

			// Tạo IdName nếu chưa có
			if (m.getIdName() == null || m.getIdName().isEmpty()) {
				m.setIdName("menu-" + m.getAdminMenuID());
			}

			String controller = m.getControllerName();
			String action = m.getActionName();
			
			// Tính ItemTarget: LUÔN bao gồm contextPath để khớp với request.getRequestURI() trong JSP
			if (m.getAdminMenuID() == 1 || ("Home".equalsIgnoreCase(controller) && "Index".equalsIgnoreCase(action))) {
				// Trường hợp Trang Chủ
				m.setItemTarget(contextPath + "/admin");
			} else if (controller != null && !controller.isEmpty() && action != null && !action.isEmpty()) {
				// Trường hợp Menu Con (Có Controller và Action)
				m.setItemTarget(contextPath + "/admin/" + controller + "/" + action);
			} else {
				// Trường hợp Menu Cha (Không có Controller/Action)
				m.setItemTarget("#"); 
			}
		}

		// 2. Gán con cho cha (Xây dựng cây)
		for (AdminMenu m : allMenus) {
			int parentId = m.getParentLevel();
			if (parentId != 0) {
				AdminMenu parent = menuMap.get(parentId);
				if (parent != null) {
					if (parent.getSubMenus() == null)
						parent.setSubMenus(new ArrayList<>());
					parent.getSubMenus().add(m);
				}
			}
		}

		// 3. Chỉ trả về menu root (ParentLevel = 0)
		List<AdminMenu> rootMenus = new ArrayList<>();
		for (AdminMenu m : allMenus) {
			if (m.getParentLevel() == 0) {
				rootMenus.add(m);
			}
		}

		return rootMenus;
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}