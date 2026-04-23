import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KioskMain extends JFrame {
    private String currentStudentId = null;
    private String currentStudentName = null;

    private JPanel mainPanel;
    private CardLayout cardLayout;

    public KioskMain() {
        setTitle("ШУТИС - Хичээл Сонголт");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createLoginPanel(), "LOGIN");
        mainPanel.add(createDashboardPanel(), "DASHBOARD");

        add(mainPanel);

        // Ensure state.dat exists
        ClipsEngine.executeAction(null);
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(240, 248, 255));

        JPanel inner = new JPanel(new GridLayout(4, 1, 10, 10));
        inner.setOpaque(false);

        JLabel title = new JLabel("Хичээл сонголтын киоск", SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 28));
        inner.add(title);

        JLabel label = new JLabel("Оюутны кодоо оруулна уу (жишээ нь: B241960001):", SwingConstants.CENTER);
        label.setFont(new Font("SansSerif", Font.PLAIN, 16));
        inner.add(label);

        JTextField idField = new JTextField();
        idField.setFont(new Font("SansSerif", Font.BOLD, 20));
        idField.setHorizontalAlignment(JTextField.CENTER);
        inner.add(idField);

        JButton loginBtn = new JButton("НЭВТРЭХ");
        loginBtn.setFont(new Font("SansSerif", Font.BOLD, 18));
        loginBtn.setBackground(new Color(0, 123, 255));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.addActionListener(e -> {
            String id = idField.getText().trim();
            String name = findStudentName(id);
            if (name != null) {
                currentStudentId = id;
                currentStudentName = name;
                updateDashboard();
                cardLayout.show(mainPanel, "DASHBOARD");
                idField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Оюутан олдсонгүй! Кодоо зөв оруулна уу.", "Алдаа", JOptionPane.ERROR_MESSAGE);
            }
        });
        inner.add(loginBtn);

        panel.add(inner);
        return panel;
    }

    private JPanel dashboardContentPanel;

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Header
        JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
        header.setBackground(new Color(33, 37, 41));
        JLabel headerLabel = new JLabel("ШУТИС Хичээл Сонголт");
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        header.add(headerLabel);
        
        JButton logoutBtn = new JButton("Гарах");
        logoutBtn.addActionListener(e -> {
            currentStudentId = null;
            currentStudentName = null;
            cardLayout.show(mainPanel, "LOGIN");
        });
        header.add(Box.createHorizontalStrut(500));
        header.add(logoutBtn);
        panel.add(header, BorderLayout.NORTH);

        // Sidebar
        JPanel sidebar = new JPanel(new GridLayout(4, 1, 10, 10));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        sidebar.setPreferredSize(new Dimension(200, 0));

        JButton addBtn = new JButton("Хичээл Нэмэх");
        JButton dropBtn = new JButton("Хичээл Хасах");
        JButton reBtn = new JButton("R/E Хүсэлт");
        JButton refreshBtn = new JButton("Шинэчлэх");

        addBtn.addActionListener(e -> showAddCourseDialog());
        dropBtn.addActionListener(e -> showDropCourseDialog());
        reBtn.addActionListener(e -> showREDialog());
        refreshBtn.addActionListener(e -> updateDashboard());

        sidebar.add(addBtn);
        sidebar.add(dropBtn);
        sidebar.add(reBtn);
        sidebar.add(refreshBtn);
        panel.add(sidebar, BorderLayout.WEST);

        // Content
        dashboardContentPanel = new JPanel();
        dashboardContentPanel.setLayout(new BoxLayout(dashboardContentPanel, BoxLayout.Y_AXIS));
        dashboardContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.add(new JScrollPane(dashboardContentPanel), BorderLayout.CENTER);

        return panel;
    }

    private void updateDashboard() {
        dashboardContentPanel.removeAll();
        
        JLabel welcome = new JLabel("Тавтай морил, " + currentStudentName + " (" + currentStudentId + ")");
        welcome.setFont(new Font("SansSerif", Font.BOLD, 22));
        dashboardContentPanel.add(welcome);
        dashboardContentPanel.add(Box.createVerticalStrut(20));

        JLabel title = new JLabel("Таны хичээлийн хуваарь:");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        dashboardContentPanel.add(title);
        dashboardContentPanel.add(Box.createVerticalStrut(10));

        List<String> enrollments = ClipsEngine.parseStateDat("enrollment");
        for (String enr : enrollments) {
            if (enr.contains("(student_id \"" + currentStudentId + "\")")) {
                String cId = extractValue(enr, "course_id");
                String status = extractValue(enr, "status");
                String courseName = getCourseName(cId);

                JLabel cLabel = new JLabel("• " + cId + " - " + courseName + " [" + status + "]");
                cLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
                
                if (status.equals("R")) cLabel.setForeground(Color.RED);
                else if (status.equals("E")) cLabel.setForeground(Color.ORANGE);
                else if (status.equals("Passed")) cLabel.setForeground(new Color(0, 150, 0));
                
                dashboardContentPanel.add(cLabel);
                dashboardContentPanel.add(Box.createVerticalStrut(5));
            }
        }

        dashboardContentPanel.revalidate();
        dashboardContentPanel.repaint();
    }

    private void showAddCourseDialog() {
        List<String> courses = ClipsEngine.parseStateDat("course");
        String[] options = new String[courses.size()];
        String[] ids = new String[courses.size()];
        
        for (int i = 0; i < courses.size(); i++) {
            String c = courses.get(i);
            ids[i] = extractValue(c, "id");
            String name = extractValue(c, "name");
            options[i] = ids[i] + " - " + name;
        }

        String selection = (String) JOptionPane.showInputDialog(this, "Сонгох хичээлээ заана уу:", "Хичээл нэмэх", JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
        if (selection != null) {
            String cId = selection.split(" - ")[0];
            String action = "(Action-Enroll (student_id \"" + currentStudentId + "\") (course_id \"" + cId + "\"))";
            List<String> result = ClipsEngine.executeAction(action);
            showResultDialog(result);
            updateDashboard();
        }
    }

    private void showDropCourseDialog() {
        String cId = JOptionPane.showInputDialog(this, "Хасах хичээлийн кодоо оруулна уу (жишээ нь: F.ITM202):");
        if (cId != null && !cId.trim().isEmpty()) {
            String action = "(Action-Drop (student_id \"" + currentStudentId + "\") (course_id \"" + cId.trim() + "\"))";
            List<String> result = ClipsEngine.executeAction(action);
            showResultDialog(result);
            updateDashboard();
        }
    }

    private void showREDialog() {
        String cId = JOptionPane.showInputDialog(this, "Хүсэлт гаргах хичээлийн кодоо оруулна уу:");
        if (cId != null && !cId.trim().isEmpty()) {
            String[] options = {"R (Дахин судлах)", "E (Шалгалт дахин өгөх)"};
            int choice = JOptionPane.showOptionDialog(this, "Төлөвөө сонгоно уу:", "R/E Хүсэлт", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
            if (choice >= 0) {
                String status = choice == 0 ? "R" : "E";
                String action = "(Action-Status (student_id \"" + currentStudentId + "\") (course_id \"" + cId.trim() + "\") (new_status \"" + status + "\"))";
                List<String> result = ClipsEngine.executeAction(action);
                showResultDialog(result);
                updateDashboard();
            }
        }
    }

    private void showResultDialog(List<String> resultLines) {
        StringBuilder sb = new StringBuilder();
        for (String s : resultLines) sb.append(s).append("\n");
        JOptionPane.showMessageDialog(this, sb.toString(), "Үр дүн", JOptionPane.INFORMATION_MESSAGE);
    }

    private String findStudentName(String id) {
        List<String> students = ClipsEngine.parseStateDat("student");
        for (String s : students) {
            if (s.contains("(id \"" + id + "\")")) {
                return extractValue(s, "name");
            }
        }
        return null;
    }

    private String getCourseName(String cId) {
        List<String> courses = ClipsEngine.parseStateDat("course");
        for (String c : courses) {
            if (c.contains("(id \"" + cId + "\")")) {
                return extractValue(c, "name");
            }
        }
        return "Unknown";
    }

    private String extractValue(String fact, String slotName) {
        Pattern p = Pattern.compile("\\(" + slotName + " \"([^\"]+)\"\\)");
        Matcher m = p.matcher(fact);
        if (m.find()) {
            return m.group(1);
        }
        
        // Handle non-string values (like integers)
        p = Pattern.compile("\\(" + slotName + " (\\d+)\\)");
        m = p.matcher(fact);
        if (m.find()) return m.group(1);
        
        return "";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KioskMain().setVisible(true));
    }
}
