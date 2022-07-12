package org.meteothink.weather;

import com.formdev.flatlaf.FlatDarculaLaf;
import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatIntelliJLaf;
import com.formdev.flatlaf.FlatLightLaf;
import org.meteoinfo.common.util.GlobalUtil;
import org.meteoinfo.ui.util.FontUtil;
import org.meteothink.weather.form.FrmMain;
import org.xml.sax.SAXException;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WeatherThink {

    public static void main(String args[]) {
        String startupPath = getStartupPath();
        Options options = loadConfigureFile(startupPath);

        /* Set look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        String laf = options.getLookFeel();
        if (laf.equals("FlatLightLaf")) {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (laf.equals("FlatDarculaLaf")) {
            try {
                UIManager.setLookAndFeel(new FlatDarculaLaf());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (laf.equals("FlatDarkLaf")) {
            try {
                UIManager.setLookAndFeel(new FlatDarkLaf());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (laf.equals("FlatIntelliJLaf")) {
            try {
                UIManager.setLookAndFeel(new FlatIntelliJLaf());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                String lafName;
                switch (laf) {
                    case "CDE/Motif":
                        lafName = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
                        break;
                    case "Metal":
                        lafName = "javax.swing.plaf.metal.MetalLookAndFeel";
                        break;
                    case "Windows":
                        lafName = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
                        break;
                    case "Windows Classic":
                        lafName = "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel";
                        break;
                    case "Nimbus":
                        lafName = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
                        break;
                    case "Mac":
                        lafName = "com.sun.java.swing.plaf.mac.MacLookAndFeel";
                        break;
                    case "GTK":
                        lafName = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
                        break;
                    default:
                        lafName = "javax.swing.plaf.nimbus.NimbusLookAndFeel";
                        break;
                }

                UIManager.setLookAndFeel(lafName);

            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
                Logger.getLogger(WeatherThink.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //</editor-fold>

        //Enable window decorations
        if (laf.startsWith("Flat")) {
            if (options.isLafDecorated()) {
                JFrame.setDefaultLookAndFeelDecorated(true);
                JDialog.setDefaultLookAndFeelDecorated(true);
            }
        }

        if (args.length >= 1) {
            if (args[0].startsWith("-locale:")) {
                String locale = args[0].substring(8);
                switch (locale.toLowerCase()) {
                    case "eng":
                    case "en":
                    case "english":
                        Locale.setDefault(Locale.ENGLISH);
                        break;
                    case "zh":
                    case "cn":
                    case "chinese":
                        Locale.setDefault(Locale.CHINESE);
                        break;
                }
            }
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {

                boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
                        getInputArguments().toString().contains("jdwp");

                StackWindow sw = null;
                if (!isDebug) {
                    sw = new StackWindow("Show Exception Stack", 600, 400);
                    Thread.UncaughtExceptionHandler handler = sw;
                    Thread.setDefaultUncaughtExceptionHandler(handler);
                    System.setOut(sw.printStream);
                    System.setErr(sw.printStream);
                }

                //registerFonts();
                FontUtil.registerWeatherFont();
                FrmMain frame = new FrmMain(startupPath, options);
                frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
                //frame.setLocationRelativeTo(null);
                frame.setVisible(true);
                if (sw != null) {
                    sw.setLocationRelativeTo(frame);
                }
            }
        });
    }

    /**
     * Get startup path.
     *
     * @return Startup path.
     */
    private static String getStartupPath() {
        boolean isDebug = java.lang.management.ManagementFactory.getRuntimeMXBean().
                getInputArguments().toString().contains("jdwp");
        String startupPath;
        if (isDebug) {
            startupPath = System.getProperty("user.dir");
        } else {
            startupPath = GlobalUtil.getAppPath(FrmMain.class);
        }
        return startupPath;
    }

    /**
     * Load configure file
     *
     * @return Configure file
     */
    private static Options loadConfigureFile(String startupPath) {
        String fn = startupPath + File.separator + "config.xml";
        Options options = new Options();
        if (new File(fn).exists()) {
            try {
                options.loadConfigFile(fn);
            } catch (SAXException | IOException | ParserConfigurationException ex) {
                Logger.getLogger(FrmMain.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return options;
    }
}
