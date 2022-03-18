package org.meteothink.weather.form;

import bibliothek.gui.dock.ScreenDockStation;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.theme.ThemeMap;
import com.formdev.flatlaf.extras.FlatSVGIcon;
import com.formdev.flatlaf.extras.FlatSVGUtils;
import org.meteoinfo.chart.ChartPanel;
import org.meteoinfo.data.meteodata.MeteoDataInfo;
import org.meteothink.weather.Options;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FrmMain extends JFrame {

    private JMenuBar jMenuBar;
    private JMenu jMenuFile;
    private JMenuItem jMenuItemFileOpen;
    private JMenuItem jMenuItemExit;
    private JMenu jMenuOptions;
    private JMenuItem jMenuItemSetting;

    private JToolBar jToolBar;
    private JButton jButtonOpenFile;

    private JPanel jPanelMain;
    private JPanel jPanelSetting;
    private JPanel jPanelView;
    private JPanel jPanelPlot;
    private ConfigDockable configDockable;
    private FigureDockable figureDockable;

    private JPanel jPanelStatus;
    private JLabel jLabelInstitute;
    private JProgressBar jProgressBarMemory;
    private JProgressBar jProgressBarRun;

    private String startupPath;
    private Options options;

    protected MeteoDataInfo meteoDataInfo;

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
        this.setTitle("灾害天气分析与可视化系统");
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
        /*if (this.options.isDockWindowDecorated()) {
            control.putProperty(ScreenDockStation.WINDOW_FACTORY, new CustomWindowFactory());
        }*/
        control.setTheme(ThemeMap.KEY_FLAT_THEME);
        control.getIcons().setIconClient("locationmanager.minimize", new FlatSVGIcon("org/meteothink/weather/icons/minimize.svg"));
        control.getIcons().setIconClient("locationmanager.maximize", new FlatSVGIcon("org/meteothink/weather/icons/maximize.svg"));
        control.getIcons().setIconClient("locationmanager.externalize", new FlatSVGIcon("org/meteothink/weather/icons/outgoing.svg"));
        control.getIcons().setIconClient("locationmanager.unexternalize", new FlatSVGIcon("org/meteothink/weather/icons/incoming.svg"));
        control.getIcons().setIconClient("locationmanager.normalize", new FlatSVGIcon("org/meteothink/weather/icons/restore.svg"));
        control.getIcons().setIconClient("locationmanager.unmaximize_externalized", new FlatSVGIcon("org/meteothink/weather/icons/restore.svg"));

        System.out.println("Editor and Console panels...");
        CGrid grid = new CGrid(control);

        configDockable = new ConfigDockable("Configure");
        figureDockable = new FigureDockable(this, this.startupPath, "Figure");
        grid.add(0, 0, 3, 10, configDockable);
        grid.add(3, 0, 7, 10, figureDockable);
        control.getContentArea().deploy(grid);

        //Location and size
        this.setLocation(this.options.getMainFormLocation());
        this.setSize(this.options.getMainFormSize());
    }

    private void initComponents() {
        //Menu
        this.jMenuBar = new JMenuBar();
        this.jMenuFile = new JMenu("文件");
        this.jMenuItemFileOpen = new JMenuItem("打开");
        jMenuItemFileOpen.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFileActionPerformed(e);
            }
        });
        this.jMenuItemExit = new JMenuItem("退出");

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

        this.jMenuOptions = new JMenu("选项");
        this.jMenuItemSetting = new JMenuItem("设置");
        jMenuItemSetting.setIcon(new FlatSVGIcon("org/meteothink/weather/icons/gear.svg"));
        jMenuItemSetting.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                jMenuItemSettingActionPerformed(e);
            }
        });
        this.jMenuOptions.add(jMenuItemSetting);

        this.jMenuBar.add(jMenuOptions);

        this.setJMenuBar(jMenuBar);

        //Toolbar
        this.jToolBar = new JToolBar();
        this.jButtonOpenFile = new JButton();
        jButtonOpenFile.setIcon(new FlatSVGIcon("org/meteothink/weather/icons/file-open.svg"));
        jButtonOpenFile.setToolTipText("打开数据文件");
        jButtonOpenFile.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openFileActionPerformed(e);
            }
        });
        this.jToolBar.add(jButtonOpenFile);

        //Main panel
        /*this.jPanelMain = new JPanel();
        this.jPanelMain.setLayout(new BorderLayout());
        this.jPanelSetting = new JPanel();
        this.jPanelMain.add(jPanelSetting, BorderLayout.WEST);
        this.jPanelView = new JPanel();
        this.jPanelMain.add(jPanelView, BorderLayout.EAST);
        this.jPanelPlot = new ChartPanel();
        this.jPanelMain.add(jPanelPlot);*/

        //Status panel
        this.jPanelStatus = new JPanel();
        this.jPanelStatus.setLayout(new BorderLayout());
        this.jLabelInstitute = new JLabel("灾害天气国家重点实验室");
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
        this.getContentPane().add(jToolBar, BorderLayout.NORTH);
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

    private void openFileActionPerformed(ActionEvent e) {
        this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

        String path = this.getStartupPath();
        File pathDir = new File(path);

        JFileChooser aDlg = new JFileChooser();
        aDlg.setMultiSelectionEnabled(true);
        aDlg.setCurrentDirectory(pathDir);
        if (JFileChooser.APPROVE_OPTION == aDlg.showOpenDialog(this)) {
            File[] files = aDlg.getSelectedFiles();
            System.setProperty("user.dir", files[0].getParent());
            this.meteoDataInfo = new MeteoDataInfo();
            meteoDataInfo.openNetCDFData(files[0].getAbsolutePath());
            this.configDockable.setTitleText(files[0].getName());
        }

        this.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
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
}
