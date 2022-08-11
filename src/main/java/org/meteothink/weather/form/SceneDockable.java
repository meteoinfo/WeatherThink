package org.meteothink.weather.form;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.action.CAction;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.meteoinfo.chart.graphic.TriMeshGraphic;
import org.meteoinfo.chart.jogl.GLChartPanel;
import org.meteoinfo.chart.jogl.Plot3DGL;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ResourceBundle;

public class SceneDockable extends DefaultSingleCDockable {

    private FrmMain parent;
    private Plot3DGL plot3DGL;

    final ResourceBundle bundle = ResourceBundle.getBundle("bundle/SceneDockable");
    final ResourceBundle bundle_commons = ResourceBundle.getBundle("bundle/Commons");
    private JScrollPane jScrollPane;
    private JPanel jPanel;
    private JLabel jLabelProjections;
    private ButtonGroup buttonGroupProjections;
    private JRadioButton jRadioButtonPerspective;
    private JRadioButton jRadioButtonOrthographic;
    private JLabel jLabelZScale;
    private JSpinner jSpinnerZScale;
    private JCheckBox jCheckBoxClipPlane;
    private JLabel jLabelBackground;
    private JLabel jLabelBackgroundColor;

    /**
     * Constructor
     * @param parent Parent form
     * @param s Title
     * @param title Title
     * @param cActions Actions
     */
    public SceneDockable(final FrmMain parent, String s, String title, CAction... cActions) {
        super(s, title, cActions);

        this.parent = parent;
        this.plot3DGL = parent.getFigureDockable().getPlot();

        this.setTitleIcon(new FlatSVGIcon("icons/setting.svg"));

        initComponents();
    }

    private void initComponents() {
        jScrollPane = new JScrollPane();
        this.getContentPane().add(jScrollPane);

        jPanel = new JPanel();
        jScrollPane.setViewportView(jPanel);

        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() instanceof JRadioButton) {
                    plot3DGL.setOrthographic(jRadioButtonOrthographic.isSelected());
                    parent.getFigureDockable().rePaint();
                }
            }
        };

        jLabelProjections = new JLabel(bundle.getString("SceneDockable.jLabelProjections.text"));
        buttonGroupProjections = new ButtonGroup();
        jRadioButtonPerspective = new JRadioButton(bundle.getString("SceneDockable.jRadioButtonPerspective.text"), true);
        jRadioButtonPerspective.addActionListener(actionListener);
        jRadioButtonOrthographic = new JRadioButton(bundle.getString("SceneDockable.jRadioButtonOrthographic.text"));
        jRadioButtonOrthographic.addActionListener(actionListener);
        buttonGroupProjections.add(jRadioButtonPerspective);
        buttonGroupProjections.add(jRadioButtonOrthographic);

        jLabelZScale = new JLabel(bundle.getString("SceneDockable.jLabelZScale.text"));
        SpinnerModel spinnerModel = new SpinnerNumberModel(0.5f, 0.1f, 5.0f, 0.1f);
        jSpinnerZScale = new JSpinner(spinnerModel);
        jSpinnerZScale.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                plot3DGL.setZScale(((Number) jSpinnerZScale.getValue()).floatValue());
                plot3DGL.setDrawExtent(plot3DGL.getDrawExtent());
                parent.getFigureDockable().rePaint();
            }
        });

        jCheckBoxClipPlane = new JCheckBox(bundle.getString("SceneDockable.jCheckBoxClipPlane.text"));
        jCheckBoxClipPlane.setSelected(plot3DGL.isClipPlane());
        jCheckBoxClipPlane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                plot3DGL.setClipPlane(jCheckBoxClipPlane.isSelected());
                parent.getFigureDockable().rePaint();
            }
        });

        jLabelBackground = new JLabel(bundle.getString("SceneDockable.jLabelBackground.text"));
        jLabelBackgroundColor = new JLabel();
        jLabelBackgroundColor = new JLabel();
        jLabelBackgroundColor.setOpaque(true);
        jLabelBackgroundColor.setBackground(plot3DGL.getBackground());
        jLabelBackgroundColor.setPreferredSize(new Dimension(50, 20));
        jLabelBackgroundColor.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                onBackGroundColorClicked(e);
            }
        });

        //Layout
        GroupLayout layout = new GroupLayout(jPanel);
        jPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        layout.setHorizontalGroup(
                layout.createParallelGroup()
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelProjections)
                                .addComponent(jRadioButtonPerspective)
                                .addComponent(jRadioButtonOrthographic))
                        .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabelZScale)
                                .addComponent(jSpinnerZScale)
                                .addComponent(jCheckBoxClipPlane)
                                .addComponent(jLabelBackground)
                                .addComponent(jLabelBackgroundColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        layout.setVerticalGroup(
                layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelProjections)
                                .addComponent(jRadioButtonPerspective)
                                .addComponent(jRadioButtonOrthographic))
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabelZScale)
                                .addComponent(jSpinnerZScale)
                                .addComponent(jCheckBoxClipPlane)
                                .addComponent(jLabelBackground)
                                .addComponent(jLabelBackgroundColor, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );
    }

    private void onBackGroundColorClicked(MouseEvent e) {
        JLabel label = (JLabel) e.getSource();
        Color color = JColorChooser.showDialog(parent, bundle_commons.getString("Commons.selectColor"), label.getBackground());
        if (color != null) {
            label.setBackground(color);
            plot3DGL.setBackground(color);
            parent.getFigureDockable().rePaint();
        }
    }
}
