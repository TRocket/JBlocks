package org.jblocks.byob;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.jblocks.editor.BlockFactory;

/**
 * A GUI class for the Build-Your-Own-Block Editor. <br />
 * 
 * @see InputTypeChooserListener
 * @author ZeroLuck
 */
class InputTypeChooser extends JPanel {

    public static final String TYPE_TEXT = "text";
    // <member>
    private List<InputTypeChooserListener> listeners;

    public static interface InputTypeChooserListener {

        /**
         * Called when the "cancel" button was pressed. <br />
         */
        public void cancel();

        /**
         * Called when the "OK" button was pressed. <br />
         * 
         * @see #TYPE_BOOLEAN_INPUT
         * @see #TYPE_TEXT
         * @param type - the type of the selected input.
         * @param label - the label of the block or the text.
         */
        public void finished(String type, String label);
    }

    public InputTypeChooser() {
        super(new BorderLayout());
        listeners = new ArrayList<InputTypeChooserListener>();

        final JTextField inputLabel = new JTextField(25);

        JPanel north = new JPanel(new GridLayout(0, 1));
        north.setBorder(BorderFactory.createTitledBorder("Create input name"));
        north.add(inputLabel);


        final JPanel center = new JPanel(new BorderLayout());
        JPanel centerNorth = new JPanel(new GridLayout(0, 1));

        JPanel northBottom = new JPanel(new FlowLayout());
        final ButtonGroup northGroup = new ButtonGroup();
        JRadioButton titleText = new JRadioButton("Title text");
        titleText.setActionCommand("title-text");
        JRadioButton inputName = new JRadioButton("Input name", true);
        inputName.setActionCommand("input-name");
        northGroup.add(titleText);
        northGroup.add(inputName);

        ActionListener typeChangedListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                String command = ae.getActionCommand();
                if (command.equals("title-text")) {
                    center.setVisible(false);
                }
                if (command.equals("input-name")) {
                    center.setVisible(true);
                }
            }
        };
        inputName.addActionListener(typeChangedListener);
        titleText.addActionListener(typeChangedListener);

        northBottom.add(titleText);
        northBottom.add(inputName);

        north.add(northBottom);

        
        JRadioButton textInput = new JRadioButton("Reporter / Text", true);
        textInput.setActionCommand(BlockFactory.TYPE_REPORTER_AND_TEXT_INPUT);
        JRadioButton booleanInput = new JRadioButton("Boolean");
        booleanInput.setActionCommand(BlockFactory.TYPE_BOOLEAN_INPUT);
        JRadioButton sequenceInput = new JRadioButton("C-Shape");
        sequenceInput.setActionCommand(BlockFactory.TYPE_SEQUENCE_INPUT);
        JRadioButton reporterInput = new JRadioButton("Reporter");
        reporterInput.setActionCommand(BlockFactory.TYPE_REPORTER_INPUT);
        JRadioButton variableInput = new JRadioButton("Variable");
        variableInput.setActionCommand(BlockFactory.TYPE_VARIABLE_INPUT);

        final ButtonGroup centerGroup = new ButtonGroup();
        centerGroup.add(textInput);
        centerGroup.add(booleanInput);
        centerGroup.add(sequenceInput);
        centerGroup.add(reporterInput);
        centerGroup.add(variableInput);

        centerNorth.add(textInput);
        centerNorth.add(booleanInput);
        centerNorth.add(sequenceInput);
        centerNorth.add(reporterInput);
        centerNorth.add(variableInput);


        center.setBorder(BorderFactory.createTitledBorder("Input type / Shape of slot"));
        center.add(centerNorth, BorderLayout.NORTH);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancel = new JButton("Cancel");
        JButton OK = new JButton("OK");
        south.add(cancel);
        south.add(OK);

        cancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                cancel();
            }
        });

        ActionListener OKListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent ae) {
                if (inputLabel.getText().isEmpty()) {
                    cancel();
                    return;
                }
                String nc = northGroup.getSelection().getActionCommand();
                String cc = centerGroup.getSelection().getActionCommand();

                String type = TYPE_TEXT;

                if (nc.equals("input-name")) {
                    type = cc;
                }

                String label = inputLabel.getText();

                for (InputTypeChooserListener m : listeners) {
                    m.finished(type, label);
                }
            }
        };
        OK.addActionListener(OKListener);
        inputLabel.addActionListener(OKListener);

        add(north, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(south, BorderLayout.SOUTH);
    }

    private void cancel() {
        for (InputTypeChooserListener m : listeners) {
            m.cancel();
        }
    }

    /**
     * Adds the specified InputTypeChooserListener. <br />
     * 
     * @throws IllegalArgumentException - if 'm' is null.
     * @param m - the listener
     */
    public void addInputTypeChooserListener(InputTypeChooserListener m) {
        if (m == null) {
            throw new IllegalArgumentException("'m' is null!");
        }
        listeners.add(m);
    }

    /**
     * Removes the specified InputTypeChooserListener. <br />
     * 
     * @param m - the listener
     */
    public void removeInputTypeChooserListener(InputTypeChooserListener m) {
        listeners.remove(m);
    }
}
