package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.*;
import model.DAO.MenuDAO;
import model.DAO.impl.MenuDAOImpl;

import java.io.IOException;

@WebFilter("/*")
public class UserMenuFilter implements Filter {

    private MenuDAO menuDAO;

    @Override
    public void init(FilterConfig filterConfig) {
        menuDAO = new MenuDAOImpl();
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        HttpSession session = request.getSession(false);

        // Chỉ load menu khi ĐÃ ĐĂNG NHẬP
        if (session != null && session.getAttribute("userId") != null) {

            if (session.getAttribute("userMenus") == null) {
                session.setAttribute("userMenus", menuDAO.getUserMenus());
            }
        }

        chain.doFilter(request, response);
    }
}
