package fisher.helpers;

import fisher.FisherMain;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/**
 * @author Jeff Smith
 */
public class FisherGUI extends JFrame {
    private FisherMain ctx;
    public FisherGUI(FisherMain main){
        this.ctx = main;
        initComponents();
    }

    private void button1ActionPerformed(ActionEvent e) {
        ctx.setShouldStart(true);
        this.setVisible(false);
    }

    private void initComponents() {
        startButton = new JButton();
        modeComboBox = new JComboBox<>();
        modeLabel = new JLabel();

        //======== this ========
        setTitle("Simple WoodCutter GUI");
        Container contentPane = getContentPane();
        contentPane.setLayout(null);

        //---- button1 ----
        startButton.setText("Start");
        startButton.addActionListener(e -> button1ActionPerformed(e));
        contentPane.add(startButton);
        startButton.setBounds(10, 55, 275, 55);

        //---- comboBox1 ----
        modeComboBox.setModel(new DefaultComboBoxModel<>(new String[]{
                "Lumbridge Shrimp",
                "Karamja Harpoon"
        }));
        contentPane.add(modeComboBox);
        modeComboBox.setBounds(15, 10, 185, 35);

        //---- label1 ----
        modeLabel.setText("Select your mode: ");
        contentPane.add(modeLabel);
        modeLabel.setBounds(new Rectangle(new Point(205, 20), modeLabel.getPreferredSize()));

        contentPane.setPreferredSize(new Dimension(335, 120));
        pack();
        setLocationRelativeTo(getOwner());
    }

    public String getTreeType() {
        return modeComboBox.getSelectedItem().toString();
    }

    private JButton startButton;
    private JComboBox<String> modeComboBox;
    private JLabel modeLabel;
}