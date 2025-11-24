/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package lab_8;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;


/**
 *
 * @author sarahkhaled
 */
public class InsightsFrame extends javax.swing.JFrame {
    
    
    private JComboBox<String> comboCourses;

    /**
     * Creates new form InsightsFrame
     */
    public InsightsFrame() {
        initComponents();
        panelStudentPerformance.setPreferredSize(new Dimension(400, 400));
panelQuizAverages.setPreferredSize(new Dimension(400, 400));
panelCompletionPercentages.setPreferredSize(new Dimension(400, 400));
panelStudentPerformance.setLayout(new BorderLayout());
panelQuizAverages.setLayout(new BorderLayout());
panelCompletionPercentages.setLayout(new BorderLayout());

    comboCourses = new JComboBox<>();
        comboCourses.addActionListener(e -> refreshCharts());

        javax.swing.JPanel topPanel = new javax.swing.JPanel();
        topPanel.add(new javax.swing.JLabel("Select Course:"));
        topPanel.add(comboCourses);
        getContentPane().add(topPanel, BorderLayout.NORTH);

        panelStudentPerformance.setLayout(new BorderLayout());
        panelQuizAverages.setLayout(new BorderLayout());
        panelCompletionPercentages.setLayout(new BorderLayout());
        
        loadCourses();
    }
    
   private void loadCourses() {
        comboCourses.removeAllItems();
        JsonDatabaseManager.loadCourses().forEach(c -> {
            String courseId = ((org.json.JSONObject) c).getString("courseId");
            comboCourses.addItem(courseId);
        });
        if (comboCourses.getItemCount() > 0) {
            comboCourses.setSelectedIndex(0);
            refreshCharts();
        }
    }


    private void refreshCharts() {
        String selectedCourse = (String) comboCourses.getSelectedItem();
        if (selectedCourse == null) return;

        showStudentPerformanceChart(selectedCourse);
        showQuizAveragesChart(selectedCourse);
        showCompletionChart(selectedCourse);
    }


private void showStudentPerformanceChart(String courseId) {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    List<Lesson> lessons = JsonDatabaseManager.getLessons(courseId);

    for (Lesson l : lessons) {
        dataset.addValue(l.getAverageScore(), "Average Score", l.getTitle());
    }

    JFreeChart chart = ChartFactory.createLineChart(
            "Student Performance",
            "Lesson",
            "Average Score",
            dataset,
            PlotOrientation.VERTICAL,
            true,  // legend
            true,  // tooltips
            false  // URLs
    );

    // Rotate X-axis labels
    CategoryAxis domainAxis = chart.getCategoryPlot().getDomainAxis();
    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

    updatePanelWithChart(panelStudentPerformance, chart);
}

private void showQuizAveragesChart(String courseId) {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    List<Lesson> lessons = JsonDatabaseManager.getLessons(courseId);

    for (Lesson l : lessons) {
        double quizAvg = (l.getQuiz() != null) ? l.getAverageScore() : 0;
        dataset.addValue(quizAvg, "Quiz Average", l.getTitle());
    }

    JFreeChart chart = ChartFactory.createBarChart(
            "Quiz Averages",
            "Lesson",
            "Average Score",
            dataset,
            PlotOrientation.VERTICAL,
            true,   // legend
            true,   // tooltips
            false   // URLs
    );

    // Rotate X-axis labels
    CategoryAxis domainAxis = chart.getCategoryPlot().getDomainAxis();
    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

    updatePanelWithChart(panelQuizAverages, chart);
}

private void showCompletionChart(String courseId) {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    List<Lesson> lessons = JsonDatabaseManager.getLessons(courseId);

    for (Lesson l : lessons) {
        dataset.addValue(l.getCompletionPercentage(), "Completion %", l.getTitle());
    }

    JFreeChart chart = ChartFactory.createBarChart(
            "Lesson Completion %",
            "Lesson",
            "Completion %",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
    );

    // Rotate X-axis labels
    CategoryAxis domainAxis = chart.getCategoryPlot().getDomainAxis();
    domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

    updatePanelWithChart(panelCompletionPercentages, chart);
}

private void updatePanelWithChart(JPanel panel, JFreeChart chart) {
    panel.removeAll();
    ChartPanel chartPanel = new ChartPanel(chart);
    chartPanel.setPreferredSize(panel.getSize());
    panel.setLayout(new BorderLayout());
    panel.add(chartPanel, BorderLayout.CENTER);
    panel.revalidate();
    panel.repaint();
}





    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToggleButton1 = new javax.swing.JToggleButton();
        panelStudentPerformance = new javax.swing.JPanel();
        lblStudentPerformance = new javax.swing.JLabel();
        panelQuizAverages = new javax.swing.JPanel();
        lblQuizAverages = new javax.swing.JLabel();
        panelCompletionPercentages = new javax.swing.JPanel();
        lblCompletionPercentages = new javax.swing.JLabel();

        jToggleButton1.setText("jToggleButton1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        lblStudentPerformance.setText("Student Performance (Scores over time)");

        javax.swing.GroupLayout panelStudentPerformanceLayout = new javax.swing.GroupLayout(panelStudentPerformance);
        panelStudentPerformance.setLayout(panelStudentPerformanceLayout);
        panelStudentPerformanceLayout.setHorizontalGroup(
            panelStudentPerformanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStudentPerformanceLayout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(lblStudentPerformance)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panelStudentPerformanceLayout.setVerticalGroup(
            panelStudentPerformanceLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelStudentPerformanceLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblStudentPerformance)
                .addContainerGap(492, Short.MAX_VALUE))
        );

        lblQuizAverages.setText("Quiz Averages per Lesson");

        javax.swing.GroupLayout panelQuizAveragesLayout = new javax.swing.GroupLayout(panelQuizAverages);
        panelQuizAverages.setLayout(panelQuizAveragesLayout);
        panelQuizAveragesLayout.setHorizontalGroup(
            panelQuizAveragesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelQuizAveragesLayout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(lblQuizAverages)
                .addContainerGap(108, Short.MAX_VALUE))
        );
        panelQuizAveragesLayout.setVerticalGroup(
            panelQuizAveragesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelQuizAveragesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblQuizAverages)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lblCompletionPercentages.setText("Lesson Completion %");

        javax.swing.GroupLayout panelCompletionPercentagesLayout = new javax.swing.GroupLayout(panelCompletionPercentages);
        panelCompletionPercentages.setLayout(panelCompletionPercentagesLayout);
        panelCompletionPercentagesLayout.setHorizontalGroup(
            panelCompletionPercentagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCompletionPercentagesLayout.createSequentialGroup()
                .addGap(88, 88, 88)
                .addComponent(lblCompletionPercentages)
                .addContainerGap(124, Short.MAX_VALUE))
        );
        panelCompletionPercentagesLayout.setVerticalGroup(
            panelCompletionPercentagesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panelCompletionPercentagesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblCompletionPercentages)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panelStudentPerformance, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelQuizAverages, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelCompletionPercentages, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panelStudentPerformance, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelQuizAverages, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(panelCompletionPercentages, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(InsightsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(InsightsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(InsightsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(InsightsFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new InsightsFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JLabel lblCompletionPercentages;
    private javax.swing.JLabel lblQuizAverages;
    private javax.swing.JLabel lblStudentPerformance;
    private javax.swing.JPanel panelCompletionPercentages;
    private javax.swing.JPanel panelQuizAverages;
    private javax.swing.JPanel panelStudentPerformance;
    // End of variables declaration//GEN-END:variables
}
