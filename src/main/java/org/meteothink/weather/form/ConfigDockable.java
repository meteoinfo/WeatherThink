package org.meteothink.weather.form;

import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.action.CAction;
import org.meteoinfo.ui.CheckBoxListEntry;
import org.meteoinfo.ui.JCheckBoxList;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

public class ConfigDockable extends DefaultSingleCDockable {

    private JPanel jPanelLayers;
    private JScrollPane jScrollPaneLayers;
    private JCheckBoxList jCheckBoxListLayers;

    public ConfigDockable(String s, CAction... cActions) {
        super(s, cActions);

        initComponents();
    }

    private void initComponents() {
        jPanelLayers = new JPanel();
        jScrollPaneLayers = new JScrollPane();
        jCheckBoxListLayers = new JCheckBoxList();

        DefaultListModel listModel = new DefaultListModel();
        for (LayerType layerType : LayerType.values()) {
            CheckBoxListEntry checkBoxListEntry = new CheckBoxListEntry(layerType, false);
            listModel.add(0, checkBoxListEntry);
        }
        jCheckBoxListLayers.setModel(listModel);
        Border border = BorderFactory.createTitledBorder("图层");
        jScrollPaneLayers.setBorder(border);
        jScrollPaneLayers.setViewportView(jCheckBoxListLayers);
        this.getContentPane().add(jScrollPaneLayers, BorderLayout.NORTH);
    }
}
