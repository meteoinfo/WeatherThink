/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.meteothink.weather.form;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.action.CAction;
import bibliothek.gui.dock.common.action.CButton;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import org.meteoinfo.chart.AspectType;
import org.meteoinfo.chart.GLChart;
import org.meteoinfo.chart.GLChartPanel;
import org.meteoinfo.chart.MouseMode;
import org.meteoinfo.chart.jogl.MapGLPlot;
import org.meteoinfo.chart.plot.GridLine;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author wyq
 */
public class FigureDockable extends DefaultSingleCDockable {

    private FrmMain parent;
    private GLChartPanel chartPanel;
    private boolean doubleBuffer;
    public CButton buttonFullExtent;

    public FigureDockable(final FrmMain parent, String id, String title, CAction... actions) {
        super(id, title, actions);

        this.parent = parent;
        this.doubleBuffer = true;
        this.setTitleIcon(new FlatSVGIcon("icons/figure_3d.svg"));
        chartPanel = new GLChartPanel(new GLChart());
        MapGLPlot plot3DGL = new MapGLPlot();
        plot3DGL.setPosition(0, 0, 1, 1);
        plot3DGL.setAspectType(AspectType.XY_EQUAL);
        plot3DGL.setClipPlane(false);
        plot3DGL.setOrthographic(false);
        plot3DGL.setBackground(Color.black);
        plot3DGL.setDrawBase(true);
        plot3DGL.setBoxed(true);
        plot3DGL.setDisplayXY(true);
        plot3DGL.setDisplayZ(true);
        GridLine gridLine = plot3DGL.getGridLine();
        gridLine.setDrawXLine(false);
        gridLine.setDrawYLine(false);
        gridLine.setDrawZLine(false);
        plot3DGL.setDrawBoundingBox(true);
        plot3DGL.getLighting().setEnable(true);
        plot3DGL.setAxesZoom(true);
        plot3DGL.setZScale(0.5f);
        plot3DGL.setFieldOfView(60.f);
        chartPanel.getChart().setBackground(Color.black);
        chartPanel.getChart().addPlot(plot3DGL);
        chartPanel.setMouseMode(MouseMode.ROTATE);
        //chartPanel.setZoomXY(true);
        this.getContentPane().add(chartPanel);
        //this.setCloseable(false);

        //Add actions     
        //Select action
        CButton button = new CButton();
        button.setText("Select");
        button.setIcon(new FlatSVGIcon("icons/select.svg"));
        button.setTooltip("Select");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartPanel.setMouseMode(MouseMode.SELECT);
            }
        });
        this.addAction(button);
        this.addSeparator();
        /*//Zoom in action
        button = new CButton();
        button.setText("Zoom In");
        button.setIcon(new FlatSVGIcon("icons/zoom-in.svg"));
        button.setTooltip("Zoom In");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartPanel.setMouseMode(MouseMode.ZOOM_IN);
            }
        });
        this.addAction(button);
        //Zoom out action
        button = new CButton();
        button.setText("Zoom Out");
        //button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/TSB_ZoomOut.Image.png")));
        button.setIcon(new FlatSVGIcon("icons/zoom-out.svg"));
        button.setTooltip("Zoom Out");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartPanel.setMouseMode(MouseMode.ZOOM_OUT);
            }
        });
        this.addAction(button);
        //Pan action
        button = new CButton();
        button.setText("Pan");
        //button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/TSB_Pan.Image.png")));
        button.setIcon(new FlatSVGIcon("icons/hand.svg"));
        button.setTooltip("Pan");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartPanel.setMouseMode(MouseMode.PAN);
            }
        });
        this.addAction(button);*/
        //Rotate action
        button = new CButton();
        button.setText("Rotate");
        //button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/rotate_16.png")));
        button.setIcon(new FlatSVGIcon("icons/rotate_16.svg"));
        button.setTooltip("Rotate");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartPanel.setMouseMode(MouseMode.ROTATE);
            }
        });
        this.addAction(button);
        //Full extent action
        buttonFullExtent = new CButton();
        buttonFullExtent.setText("Full Extent");
        //button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/TSB_FullExtent.Image.png")));
        buttonFullExtent.setIcon(new FlatSVGIcon("icons/full-extent.svg"));
        buttonFullExtent.setTooltip("Full Extent");
        /*buttonFullExtent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartPanel.onUndoZoomClick();
            }
        });*/
        this.addAction(buttonFullExtent);
        this.addSeparator();
        /*//Identifer action
        button = new CButton();
        button.setText("Identifer");
        //button.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/information.png")));
        button.setIcon(new FlatSVGIcon("icons/information.svg"));
        button.setTooltip("Identifer");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chartPanel.setMouseMode(MouseMode.IDENTIFIER);
            }
        });
        this.addAction(button);
        this.addSeparator();*/
    }

    /**
     * Set double buffering
     * @param doubleBuffer Double buffering or not
     */
    public void setDoubleBuffer(boolean doubleBuffer) {
        this.doubleBuffer = doubleBuffer;
        this.chartPanel.setDoubleBuffered(doubleBuffer);
    }

    /**
     * Set chart panel
     * @return Chart panel
     */
    public GLChartPanel getChartPanel() {
        return this.chartPanel;
    }

    /**
     * Get plot 3D object
     * @return Plot 3D object
     */
    public MapGLPlot getPlot() {
        return (MapGLPlot) this.chartPanel.getChart().getPlot();
    }

    /**
     * Repaint
     */
    public void rePaint() {
        this.chartPanel.repaint();
    }
}
