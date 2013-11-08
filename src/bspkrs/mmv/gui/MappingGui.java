package bspkrs.mmv.gui;

/*
 * This is mainly just a mock-up of the actual mapping GUI, so it is subject to change
 */

import immibis.bon.gui.BrowseActionListener;
import immibis.bon.gui.Reference;
import immibis.bon.gui.Side;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.table.DefaultTableModel;

public class MappingGui extends JFrame
{
    private static final long     serialVersionUID           = 1L;
    private final Preferences     prefs                      = Preferences.userNodeForPackage(MappingGui.class);
    private JFrame                frmMcpMappingViewer;
    private JComboBox<String>     cmbMCPDirPath;
    private final static String   PREFS_KEY_MCPDIR           = "mcpDir";
    private final Reference<File> mcpBrowseDir               = new Reference<File>();
    private JTable                tblClasses;
    private JTable                tblMethods;
    private JTable                tblFields;
    private boolean               isCmbMCPDirPathTextChanged = false;
    
    private void savePrefs()
    {
        for (int i = 0; i < cmbMCPDirPath.getItemCount(); i++)
            prefs.put(PREFS_KEY_MCPDIR + i, cmbMCPDirPath.getItemAt(i));
    }
    
    private void loadPrefs()
    {
        for (int i = 0; i < 8; i++)
        {
            String item = prefs.get(PREFS_KEY_MCPDIR + i, "");
            if (!item.equals(""))
            {
                DefaultComboBoxModel<String> model = (DefaultComboBoxModel<String>) cmbMCPDirPath.getModel();
                
                if (model.getIndexOf(item) == -1)
                    cmbMCPDirPath.addItem(item);
            }
            
            if (i == 0)
                if (!item.equals(""))
                    mcpBrowseDir.val = new File(item);
                else
                    mcpBrowseDir.val = new File(".");
        }
    }
    
    /**
     * Launch the application.
     */
    public static void main(String[] args)
    {
        EventQueue.invokeLater(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    MappingGui window = new MappingGui();
                    window.frmMcpMappingViewer.setVisible(true);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
    
    /**
     * Create the application.
     */
    public MappingGui()
    {
        initialize();
    }
    
    /**
     * Initialize the contents of the frame.
     */
    private void initialize()
    {
        {
            String mcpDirString = prefs.get(PREFS_KEY_MCPDIR + 0, ".");
            
            if (!mcpDirString.equals(""))
                mcpBrowseDir.val = new File(mcpDirString);
            else
                mcpBrowseDir.val = new File(".");
        }
        frmMcpMappingViewer = new JFrame();
        frmMcpMappingViewer.addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent arg0)
            {
                savePrefs();
            }
        });
        frmMcpMappingViewer.setTitle("MCP Mapping Viewer");
        frmMcpMappingViewer.setBounds(100, 100, 1051, 618);
        frmMcpMappingViewer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JPanel pnlHeader = new JPanel();
        pnlHeader.setSize(new Dimension(0, 20));
        pnlHeader.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        JLabel lblSide = new JLabel("Side");
        pnlHeader.add(lblSide);
        
        JComboBox<Side> cmbSide = new JComboBox<Side>();
        cmbSide.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent arg0)
            {}
        });
        pnlHeader.add(cmbSide);
        
        JLabel lblMCPFolder = new JLabel("MCP folder");
        pnlHeader.add(lblMCPFolder);
        
        cmbMCPDirPath = new JComboBox<String>(new DefaultComboBoxModel<String>());
        cmbMCPDirPath.addInputMethodListener(new InputMethodListener()
        {
            @Override
            public void caretPositionChanged(InputMethodEvent event)
            {}
            
            @Override
            public void inputMethodTextChanged(InputMethodEvent event)
            {
                JComboBox jcb = (JComboBox) event.getSource();
            }
        });
        cmbMCPDirPath.addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {}
        });
        cmbMCPDirPath.setEditable(true);
        loadPrefs();
        cmbMCPDirPath.setSelectedItem(mcpBrowseDir.val.getAbsolutePath());
        pnlHeader.add(cmbMCPDirPath);
        
        JButton btnBrowseFile = new JButton("Browse");
        btnBrowseFile.addActionListener(new BrowseActionListener(cmbMCPDirPath, true, btnBrowseFile, true, mcpBrowseDir));
        frmMcpMappingViewer.getContentPane().setLayout(new BorderLayout(0, 0));
        
        JSplitPane splitMain = new JSplitPane();
        splitMain.setResizeWeight(0.3);
        splitMain.setContinuousLayout(true);
        splitMain.setMinimumSize(new Dimension(179, 80));
        splitMain.setPreferredSize(new Dimension(179, 80));
        splitMain.setOrientation(JSplitPane.VERTICAL_SPLIT);
        
        JScrollPane scrlpnClasses = new JScrollPane();
        scrlpnClasses.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        splitMain.setLeftComponent(scrlpnClasses);
        
        tblClasses = new JTable();
        scrlpnClasses.setViewportView(tblClasses);
        tblClasses.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblClasses.setModel(new DefaultTableModel(
                new Object[][] {
                        { null, null, null },
                },
                new String[] {
                        "MCP name", "Obf name", "Pkg name"
                }
                )
                {
                    Class[] columnTypes = new Class[] {
                                                String.class, String.class, String.class
                                        };
                    
                    @Override
                    public Class getColumnClass(int columnIndex)
                    {
                        return columnTypes[columnIndex];
                    }
                });
        tblClasses.setFillsViewportHeight(true);
        tblClasses.setCellSelectionEnabled(true);
        frmMcpMappingViewer.getContentPane().add(splitMain, BorderLayout.CENTER);
        
        JSplitPane splitMembers = new JSplitPane();
        splitMembers.setResizeWeight(0.5);
        splitMembers.setOrientation(JSplitPane.VERTICAL_SPLIT);
        splitMain.setRightComponent(splitMembers);
        
        JScrollPane scrlpnMethods = new JScrollPane();
        scrlpnMethods.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        splitMembers.setLeftComponent(scrlpnMethods);
        
        tblMethods = new JTable();
        tblMethods.setCellSelectionEnabled(true);
        tblMethods.setFillsViewportHeight(true);
        tblMethods.setModel(new DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null, null },
                },
                new String[] {
                        "MCP Name", "SRG Name", "Obf. Name", "Return Type", "Descriptor", "Comment"
                }
                )
                {
                    boolean[] columnEditables = new boolean[] {
                                                      false, false, false, false, false, false
                                              };
                    
                    @Override
                    public boolean isCellEditable(int row, int column)
                    {
                        return columnEditables[column];
                    }
                });
        scrlpnMethods.setViewportView(tblMethods);
        
        JScrollPane scrlpnFields = new JScrollPane();
        scrlpnFields.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        splitMembers.setRightComponent(scrlpnFields);
        
        tblFields = new JTable();
        tblFields.setModel(new DefaultTableModel(
                new Object[][] {
                        { null, null, null, null, null },
                },
                new String[] {
                        "MCP Name", "SRG Name", "Obf. Name", "Type", "Comment"
                }
                )
                {
                    boolean[] columnEditables = new boolean[] {
                                                      false, false, false, false, false
                                              };
                    
                    @Override
                    public boolean isCellEditable(int row, int column)
                    {
                        return columnEditables[column];
                    }
                });
        tblFields.setFillsViewportHeight(true);
        scrlpnFields.setViewportView(tblFields);
        pnlHeader.add(btnBrowseFile);
        frmMcpMappingViewer.getContentPane().add(pnlHeader, BorderLayout.NORTH);
        
        {
            String mcpDirString = prefs.get(PREFS_KEY_MCPDIR + 0, ".");
            
            if (!mcpDirString.equals(""))
                mcpBrowseDir.val = new File(mcpDirString);
            else
                mcpBrowseDir.val = new File(".");
        }
        
        addWindowListener(new WindowAdapter()
        {
            @Override
            public void windowClosing(WindowEvent e)
            {
                savePrefs();
            }
        });
    }
}
