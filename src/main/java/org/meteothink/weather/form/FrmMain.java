package org.meteothink.weather.form;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.theme.ThemeMap;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGUtils;
import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteothink.weather.Options;
import org.meteothink.weather.data.Dataset;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrmMain extends JFrame {

    final java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("bundle/FrmMain");
    private JMenuBar jMenuBar;
    private JMenu jMenuFile;
    private JMenuItem jMenuItemFileOpen;
    private JMenuItem jMenuItemExit;
    private JMenu jMenuOptions;
    private JMenuItem jMenuItemSetting;
    private JMenu jMenuHelp;
    private JMenuItem jMenuItemAbout;

    private JPanel jPanelToolBar;
    private JToolBar jToolBar;
    private JButton jButtonOpenFile;
    private JComboBox jComboBoxTimes;

    private RenderDockable renderDockable;
    private FigureDockable figureDockable;

    private JPanel jPanelStatus;
    private JLabel jLabelInstitute;
    private JProgressBar jProgressBarMemory;
    private JProgressBar jProgressBarRun;

    private String startupPath;
    private Options options;

    private Dataset dataset;

    /**
     * Constructor
     */
    public FrmMain() {
        initComponents();

        //Window listener
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        //Set image icon
        this.setIconImages(FlatSVGUtils.createWindowIconImages("/icons/rainstorm.svg"));

        //Title
        this.setTitle(bundle.getString("FrmMain.title"));
    }

    /**
     * Constructor
     * @param startupPath Startup path
     * @param options Options
     */
    public FrmMain(String startupPath, Options options) {
        this();

        this.startupPath = startupPath;
        this.options = options;

        //Add dockable panels
        System.out.println("Add dockable panels...");
        CControl control = new CControl(this);
        this.add(control.getContentArea());
        control.putProperty(ScreenDockStation.WINDOW_FACTORY, new CustomWindowFactory());
        control.setTheme(ThemeMap.KEY_FLAT_THEME);
        control.getIcons().setIconClient("locationmanager.minimize", new FlatSVGIcon("icons/minimize.svg"));
        control.getIcons().setIconClient("locationmanager.maximize", new FlatSVGIcon("icons/maximize.svg"));
        control.getIcons().setIconClient("locationmanager.externalize", new FlatSVGIcon("icons/outgoing.svg"));
        control.getIcons().setIconClient("locationmanager.unexternalize", new FlatSVGIcon("icons/incoming.svg"));
        control.getIcons().setIconClient("locationmanager.normalize", new FlatSVGIcon("icons/restore.svg"));
        control.getIcons().setIconClient("locationmanager.unmaximize_externalized", new FlatSVGIcon("icons/restore.svg"));

        System.out.println("Editor and Console panels...");
        CGrid grid = new CGrid(control);

        figureDockable = new FigureDockable(this, this.startupPath, bundle.getString("FrmMain.figureDockable.title"));
        figureDockable.buttonFullExtent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dataset != null) {
                    figureDockable.getPlot().setDrawExtent(dataset.getExtent3D());
                    figureDockable.getPlot().setAxesExtent(dataset.getExtent3D());
                }
            }
        });
        renderDockable = new RenderDockable(this, "Configure", this.startupPath);
        grid.add(0, 0, 3, 10, renderDockable);
        grid.add(3, 0, 7, 10, figureDockable);
        control.getContentArea().deploy(grid);

        //Location and size
        this.setLocation(this.options.getMainFormLocation());
        this.setSize(this.options.getMainFormSize());
    }

    private void initComponents() {
        //Menu
        //File menu
        this.jMenuBar = new JMenuBar();
        this.jMenuFile = new JMenu(bundle.getString("FrmMain.jMenuFile.text"));
        this.jMenuItemFileOpen = new JMenuItem(bundle.getString("FrmMain.jMenuItemFileOpen.text"));
        jMenuItemFileOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFileActionPerformed(e);
            }
        });
        this.jMenuItemExit = new JMenuItem(bundle.getString("FrmMain.jMenuItemExit.text"));

        this.jMenuFile.setMnemonic(KeyEvent.VK_F);
        this.jMenuItemFileOpen.setMnemonic(KeyEvent.VK_O);
        this.jMenuFile.add(jMenuItemFileOpen);

        jMenuItemExit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItemExitActionPerformed(evt);
            }
        });
        jMenuFile.add(jMenuItemExit);

        this.jMenuBar.add(jMenuFile);

        //Options menu
        this.jMenuOptions = new JMenu(bundle.getString("FrmMain.jMenuOptions.text"));
        this.jMenuItemSetting = new JMenuItem(bundle.getString("FrmMain.jMenuItemSetting.text"));
        jMenuItemSetting.setIcon(new FlatSVGIcon("icons/gear.svg"));
        jMenuItemSetting.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItemSettingActionPerformed(e);
            }
        });
        this.jMenuOptions.add(jMenuItemSetting);

        this.jMenuBar.add(jMenuOptions);

        //Help menu
        this.jMenuHelp = new JMenu(bundle.getString("FrmMain.jMenuHelp.text"));
        this.jMenuItemAbout = new JMenuItem(bundle.getString("FrmMain.jMenuItemAbout.text"));
        jMenuItemAbout.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItemAboutActionPerformed(e);
            }
        });
        this.jMenuHelp.add(jMenuItemAbout);

        this.jMenuBar.add(jMenuHelp);

        this.setJMenuBar(jMenuBar);

        //Toolbar
        jPanelToolBar = new JPanel();
        jPanelToolBar.setLayout(new BorderLayout());
        this.jToolBar = new JToolBar();
        this.jButtonOpenFile = new JButton();
        jButtonOpenFile.setIcon(new FlatSVGIcon("icons/file-open.svg"));
        jButtonOpenFile.setToolTipText(bundle.getString("FrmMain.jButtonOpenFile.toolTipText"));
        jButtonOpenFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFileActionPerformed(e);
            }
        });
        this.jToolBar.add(jButtonOpenFile);
        jToolBar.addSeparator();
        jComboBoxTimes = new JComboBox();
        jComboBoxTimes.setPrototypeDisplayValue("yyyy-mm-dd HH:MM");
        jComboBoxTimes.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                onTimeChanged(e);
            }
        });
        jToolBar.add(jComboBoxTimes);
        jPanelToolBar.add(jToolBar, BorderLayout.LINE_START);

        //Status panel
        this.jPanelStatus = new JPanel();
        this.jPanelStatus.setLayout(new BorderLayout());
        this.jLabelInstitute = new JLabel(bundle.getString("FrmMain.jLabelInstitute.text"));
        this.jPanelStatus.add(jLabelInstitute, BorderLayout.WEST);

        this.jProgressBarRun = new JProgressBar();
        this.jProgressBarMemory = new JProgressBar();
        this.jProgressBarRun.setVisible(false);
        jPanelStatus.add(jProgressBarRun, BorderLayout.CENTER);
        //Memory progress bar
        int maxMemory = (int)(Runtime.getRuntime().maxMemory() / (1024 * 1024 * 1024));
        jProgressBarMemory.setStringPainted(true);
        jProgressBarMemory.setString(String.format("%dG", maxMemory));
        java.util.Timer timer = new java.util.Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                int ratio = (int) ((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) *
                        100. / Runtime.getRuntime().maxMemory());
                jProgressBarMemory.setValue(ratio);
                jProgressBarMemory.setString(String.format("%d%% / %dG", ratio, maxMemory));
            }
        }, 1000,1000);
        jPanelStatus.add(jProgressBarMemory, java.awt.BorderLayout.EAST);

        //Add components
        this.getContentPane().add(jPanelToolBar, BorderLayout.NORTH);
        //this.getContentPane().add(jPanelMain, BorderLayout.CENTER);
        this.getContentPane().add(jPanelStatus, BorderLayout.SOUTH);
        this.pack();
    }

    void jMenuItemExitActionPerformed(ActionEvent e) {
        this.saveConfigureFile();
        System.exit(0);
    }

    private void jMenuItemSettingActionPerformed(ActionEvent evt) {
        FrmSetting frm = new FrmSetting(this, false);
        frm.setLocationRelativeTo(this);
        frm.setVisible(true);
    }

    private void jMenuItemAboutActionPerformed(ActionEvent e) {
        FrmAbout frmAbout = new FrmAbout(this, false);
        frmAbout.setLocationRelativeTo(this);
        frmAbout.setVisible(true);
    }

    private void openFileActionPerformed(ActionEvent e) {
        String path = this.options.getCurrentPath();
        File pathDir = new File(path);
        JFileChooser aDlg = new JFileChooser();
        aDlg.setMultiSelectionEnabled(false);
        if (pathDir.isDirectory())
            aDlg.setCurrentDirectory(pathDir);
        if (JFileChooser.APPROVE_OPTION == aDlg.showOpenDialog(this)) {
            this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            File file = aDlg.getSelectedFile();
            this.options.setCurrentPath(file.getParent());
            MeteoDataInfo meteoDataInfo = new MeteoDataInfo();
            String fileName = file.getAbsolutePath();
            if (fileName.endsWith(".ctl"))
                meteoDataInfo.openGrADSData(fileName);
            else
                meteoDataInfo.openNetCDFData(fileName);
            dataset = new Dataset(meteoDataInfo);
            setDataset(dataset);
            this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    private void setDataset(Dataset dataset) {
        this.figureDockable.getPlot().setProjInfo(dataset.getDataInfo().getProjectionInfo());
        //this.figureDockable.getPlot().setExtent(dataset.getExtent3D());
        this.figureDockable.getPlot().setDrawExtent(dataset.getExtent3D());
        this.figureDockable.getPlot().setAxesExtent(dataset.getExtent3D());
        this.figureDockable.getPlot().setFixExtent(true);
        if (this.renderDockable != null)
            this.renderDockable.setDataset(dataset);

        this.jComboBoxTimes.setVisible(true);
        DateTimeFormatter sdf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        List<LocalDateTime> times = dataset.getDataInfo().getDataInfo().getTimes();
        if (times == null) {
            this.jComboBoxTimes.removeAllItems();
        } else {
            DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
            for (int i = 0; i < times.size(); i++) {
                comboBoxModel.addElement(sdf.format(times.get(i)));
            }
            this.jComboBoxTimes.setModel(comboBoxModel);
        }
    }

    private void formWindowClosing(java.awt.event.WindowEvent evt) {
        this.saveConfigureFile();
        System.exit(0);
    }

    public final void saveConfigureFile() {
        String fn = this.options.getFileName();
        try {
            this.options.setMainFormLocation(this.getLocation());
            this.options.setMainFormSize(this.getSize());
            this.options.saveConfigFile(fn);
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Get startup path
     *
     * @return Startup path
     */
    public String getStartupPath() {
        return this.startupPath;
    }

    /**
     * Get configure options
     *
     * @return Configure options
     */
    public Options getOptions() {
        return this.options;
    }

    /**
     * Get figure dockable
     * @return Figure dockable
     */
    public FigureDockable getFigureDockable() {
        return this.figureDockable;
    }

    /**
     * Get render dockable
     * @return Render dockable
     */
    public RenderDockable getRenderDockable() {
        return this.renderDockable;
    }

    private void onTimeChanged(ItemEvent e) {
        if(e.getStateChange() == ItemEvent.SELECTED) {
            this.dataset.setTimeIndex(this.jComboBoxTimes.getSelectedIndex());
        }
    }
}
