import java.awt.event.*;
import javax.swing.*;
import javax.swing.GroupLayout;
import javax.swing.LayoutStyle;
/*
 * Created by JFormDesigner on Thu Feb 28 01:06:48 PST 2013
 */



/**
 * @author Alan B
 */
public class ExperimentLog extends JPanel {
	public ExperimentLog() {
		initComponents();
		btn_terminar.setEnabled(false);
	}

	private void btn_nuevoActionPerformed(ActionEvent e) {
		System.out.println("boton nuevo");
		btn_nuevo.setEnabled(false);
		btn_terminar.setEnabled(true);
	}

	private void btn_terminarActionPerformed(ActionEvent e) {
		System.out.println("boton terminar");
		btn_terminar.setEnabled(false);
		btn_nuevo.setEnabled(true);
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - Alan B
		lbl_header = new JLabel();
		btn_nuevo = new JButton();
		btn_terminar = new JButton();
		btn_inicio = new JButton();
		btn_termino = new JButton();

		//======== this ========

		// JFormDesigner evaluation mark
		setBorder(new javax.swing.border.CompoundBorder(
			new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
				"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
				javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
				java.awt.Color.red), getBorder())); addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});


		//---- lbl_header ----
		lbl_header.setText("Bitacora de experimentos");

		//---- btn_nuevo ----
		btn_nuevo.setText("Nuevo Experimento");
		btn_nuevo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btn_nuevoActionPerformed(e);
				btn_nuevoActionPerformed(e);
			}
		});

		//---- btn_terminar ----
		btn_terminar.setText("Terminar Experimento");
		btn_terminar.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btn_terminarActionPerformed(e);
			}
		});

		//---- btn_inicio ----
		btn_inicio.setText("Hora Inicio");

		//---- btn_termino ----
		btn_termino.setText("Hora Termino");

		GroupLayout layout = new GroupLayout(this);
		setLayout(layout);
		layout.setHorizontalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addGroup(layout.createParallelGroup()
						.addGroup(layout.createSequentialGroup()
							.addGap(69, 69, 69)
							.addComponent(lbl_header, GroupLayout.PREFERRED_SIZE, 187, GroupLayout.PREFERRED_SIZE))
						.addGroup(layout.createSequentialGroup()
							.addContainerGap()
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
								.addComponent(btn_nuevo, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btn_inicio, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
							.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
							.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
								.addComponent(btn_terminar, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(btn_termino, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
					.addContainerGap(6, Short.MAX_VALUE))
		);
		layout.setVerticalGroup(
			layout.createParallelGroup()
				.addGroup(layout.createSequentialGroup()
					.addComponent(lbl_header)
					.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(btn_nuevo)
						.addComponent(btn_terminar))
					.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
					.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(btn_inicio)
						.addComponent(btn_termino))
					.addContainerGap(14, Short.MAX_VALUE))
		);
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - Alan B
	private JLabel lbl_header;
	private JButton btn_nuevo;
	private JButton btn_terminar;
	private JButton btn_inicio;
	private JButton btn_termino;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
